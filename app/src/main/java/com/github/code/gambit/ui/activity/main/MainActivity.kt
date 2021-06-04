package com.github.code.gambit.ui.activity.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.amplifyframework.auth.AuthChannelEventName
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.InitializationStatus
import com.amplifyframework.hub.HubChannel
import com.github.code.gambit.R
import com.github.code.gambit.data.model.FileUploadStatus
import com.github.code.gambit.databinding.ActivityMainBinding
import com.github.code.gambit.helper.file.FileUploadState
import com.github.code.gambit.ui.OnItemClickListener
import com.github.code.gambit.ui.fragment.BottomNavController
import com.github.code.gambit.ui.fragment.home.main.HomeFragment
import com.github.code.gambit.utility.SystemManager
import com.github.code.gambit.utility.extention.bottomNavHide
import com.github.code.gambit.utility.extention.bottomNavShow
import com.github.code.gambit.utility.extention.hide
import com.github.code.gambit.utility.extention.show
import com.github.code.gambit.utility.extention.showDefaultMaterialAlert
import com.github.code.gambit.utility.extention.snackbar
import com.github.code.gambit.utility.extention.toggleVisibility
import com.github.code.gambit.utility.sharedpreference.UserManager
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), BottomNavController {

    private lateinit var _binding: ActivityMainBinding
    private val binding get() = _binding

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var userManager: UserManager

    @Inject
    lateinit var systemManager: SystemManager

    @Inject
    lateinit var adapter: FileMetaDataAdapter

    private lateinit var navController: NavController

    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerAmplifyCallback()
        registerForActivityResult()
        val hostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)
        if (hostFragment is NavHostFragment)
            navController = hostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment -> {
                    binding.root.getTransition(R.id.start_to_end).setEnable(false)
                    hideBottomNav()
                }
                R.id.onBoardingFragment -> {
                    hideBottomNav()
                }
                R.id.homeFragment -> {
                    // info bottomNav is hiddent until data is loading in homeFragment
                    // binding.bottomNavContainer.bottomNavShow()
                    binding.root.getTransition(R.id.start_to_end).setEnable(true)
                    binding.dragIcon.show()
                }
                R.id.authFragment -> {
                    hideBottomNav()
                }
            }
        }

        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

        setupClickListeners()

        viewModel.fileUploadState.observe(this) {
            when (it) {
                is FileUploadState.UploadStarted -> {
                    binding.root.snackbar("Uploading File ${it.fileName}")
                }
                is FileUploadState.UploadSuccess -> {
                    Toast.makeText(this, it.file.toString(), Toast.LENGTH_LONG).show()
                    binding.root.snackbar("File successfully uploaded")
                }
                is FileUploadState.NewFileUpload -> {
                    adapter.add(it.fileUploadStatus)
                }
                is FileUploadState.UpdateFileState -> {
                    adapter.updateStatus(it.uuid, it.newState)
                }
            }
        }
        setUpRecyclerView()
        viewModel.setEvent(MainEvent.ObserveFileStatus)
    }

    private fun setUpRecyclerView() {
        adapter.listener = object : OnItemClickListener<FileUploadStatus> {
            override fun onItemClick(item: FileUploadStatus) {
                showDefaultMaterialAlert(
                    "Confirmation", "Are you sure you want to cancel file upload?"
                ) { adapter.remove(item) }
            }

            override fun onItemLongClick(item: FileUploadStatus) {}
        }
        val layoutManager = LinearLayoutManager(this)
        binding.secondaryContainer.layoutManager = layoutManager
        binding.secondaryContainer.setHasFixedSize(false)
        binding.secondaryContainer.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.addFileButton.setOnClickListener { binding.fileUploadContainer.toggleVisibility() }

        binding.galleryButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            systemManager.launchActivity(launcher, intent)
        }
        binding.cameraButton.setOnClickListener {
            ImagePicker.with(this)
                .cameraOnly()
                .start { _, data ->
                    uploadFile(data!!.data)
                }
        }
        binding.documentButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "*/*"

            systemManager.launchActivity(launcher, intent)
        }
    }

    private fun registerForActivityResult() {
        launcher = systemManager.requestFile(this) { fileUri -> uploadFile(fileUri) }
    }

    private fun uploadFile(fileUri: Uri?) {
        viewModel.setEvent(MainEvent.UploadFileEvent(fileUri!!))
    }

    private fun registerAmplifyCallback() {
        Amplify.Hub.subscribe(HubChannel.AUTH) { event ->
            when (event.name) {
                InitializationStatus.SUCCEEDED.toString() ->
                    Timber.tag("AuthQuickstart").i("Auth successfully initialized")
                InitializationStatus.FAILED.toString() ->
                    Timber.tag("AuthQuickstart").i("Auth failed to succeed")
                else -> when (AuthChannelEventName.valueOf(event.name)) {
                    AuthChannelEventName.SIGNED_IN ->
                        Timber.tag("AuthQuickstart").i("Auth just became signed in")
                    AuthChannelEventName.SIGNED_OUT -> {
                        userManager.revokeAuthentication()
                        Timber.tag("AuthQuickstart").i("Auth just became signed out")
                    }
                    AuthChannelEventName.SESSION_EXPIRED -> {
                        userManager.revokeAuthentication()
                        Timber.tag("AuthQuickstart").i("Auth session just expired")
                    }
                    else ->
                        Timber.tag("AuthQuickstart").i("Unhandled Auth Event: ${event.name}")
                }
            }
        }
    }

    override fun onBackPressed() {
        val hostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)
        if (hostFragment is NavHostFragment) {
            when (val fragment = hostFragment.childFragmentManager.fragments.first()) {
                is HomeFragment -> {
                    when {
                        fragment.isFilterEnable() -> {
                            fragment.closeFilter()
                        }
                        fragment.isSearchEnable() -> {
                            fragment.closeSearch()
                        }
                        else -> {
                            finish()
                        }
                    }
                }
                else -> super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    fun getAddFab(): View {
        return binding.addFileButton
    }

    override fun animateBottomNav(offset: Float) {
        if (this::_binding.isInitialized && offset.isFinite()) {
            if (offset == 0f) {
                binding.bottomNavContainer.hide()
            } else {
                binding.bottomNavContainer.show()
            }
            binding.bottomNavContainer.animate().alpha(offset).scaleX(offset).scaleY(offset)
                .setDuration(0).start()
        }
    }

    override fun showBottomNav() = binding.bottomNavContainer.bottomNavShow()

    override fun hideBottomNav() = binding.bottomNavContainer.bottomNavHide()

    fun showSnackBar(message: String) {
        binding.root.snackbar(message, binding.bottomNavContainer)
    }
}
