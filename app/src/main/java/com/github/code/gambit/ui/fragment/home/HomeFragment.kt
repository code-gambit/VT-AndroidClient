package com.github.code.gambit.ui.fragment.home

import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.code.gambit.R
import com.github.code.gambit.data.model.File
import com.github.code.gambit.data.model.Url
import com.github.code.gambit.databinding.FileUrlLayoutBinding
import com.github.code.gambit.databinding.FilterLayoutBinding
import com.github.code.gambit.databinding.FragmentHomeBinding
import com.github.code.gambit.databinding.SearchLayoutBinding
import com.github.code.gambit.repositories.HomeRepository
import com.github.code.gambit.ui.activity.main.MainActivity
import com.github.code.gambit.ui.fragment.BottomNavController
import com.github.code.gambit.utility.extention.copyToClipboard
import com.github.code.gambit.utility.extention.exitFullscreen
import com.github.code.gambit.utility.extention.hide
import com.github.code.gambit.utility.extention.hideKeyboard
import com.github.code.gambit.utility.extention.longToast
import com.github.code.gambit.utility.extention.shortToast
import com.github.code.gambit.utility.extention.show
import com.github.code.gambit.utility.extention.snackbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.takusemba.spotlight.OnSpotlightListener
import com.takusemba.spotlight.OnTargetListener
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.Target
import com.takusemba.spotlight.shape.Circle
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), FileUrlClickCallback, BottomNavController {

    private lateinit var _binding: FragmentHomeBinding
    private val binding get() = _binding

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var _filterBinding: FilterLayoutBinding
    private val filterBinding get() = _filterBinding

    private lateinit var _searchBinding: SearchLayoutBinding
    private val searchBinding get() = _searchBinding

    private lateinit var _urlBinding: FileUrlLayoutBinding
    private val urlBinding get() = _urlBinding

    @Inject
    lateinit var homeRepository: HomeRepository

    @Inject
    lateinit var adapter: FileListAdapter

    lateinit var fileSearchComponent: FileSearchComponent

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        _filterBinding = binding.filterLayout
        _searchBinding = binding.searchLayout
        _urlBinding = binding.fileUrlLayout
        activity?.window?.exitFullscreen()
        registerFilterComponents()
        registerUrlComponent()

        binding.filterButton.setOnClickListener {
            showFilter()
        }

        binding.searchButton.setOnClickListener {
            showSearch()
        }

        binding.overlay.setOnClickListener {
            closeFilter()
            closeFileUrl()
        }

        searchBinding.homeButton.setOnClickListener {
            requireMainActivity().onBackPressed()
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.setEvent(HomeEvent.GetFiles)
        }

        binding.swipeRefresh.setOnDragListener { _, dragEvent ->
            Timber.tag("home").i("drag: ${dragEvent.y}")
            true
        }

        setUpFileRecyclerView()
        registerEventCallbacks()
        viewModel.setEvent(HomeEvent.GetFiles)
        // Handler().postDelayed({ spotlight() }, 6000)
    }

    private fun registerEventCallbacks() {
        viewModel.homeState.observe(viewLifecycleOwner) {
            when (it) {
                is HomeState.Error -> longToast(it.message)
                is HomeState.FilesLoaded -> {
                    if (!it.isSearchResult) {
                        stopShimmer()
                        adapter.addAll(it.files, true)
                        binding.noFileIllustrationContainer.hide()
                        binding.swipeRefresh.isRefreshing = false
                        return@observe
                    }
                    fileSearchComponent.setFileLoaded(it.files)
                }
                is HomeState.Loading -> {
                    if (!it.isSearchResultLoading) {
                        showShimmer()
                        return@observe
                    }
                    fileSearchComponent.setRefreshing()
                }
                HomeState.LoadingUrl -> shortToast("Loading Url")
                is HomeState.UrlGenerated -> {
                    stopShimmer()
                    Timber.tag("home").i("${it.url.id}, ${it.url.fileId}")
                    adapter.addUrl(it.url)
                }
                is HomeState.UrlsLoaded -> {
                    if (it.urls.isEmpty()) {
                        shortToast("No Urls")
                        return@observe
                    }
                    adapter.addUrls(it.urls)
                }
            }
        }
    }

    private fun setUpFileRecyclerView() {
        binding.fileList.layoutManager = LinearLayoutManager(requireContext())
        binding.fileList.setHasFixedSize(false)
        binding.fileList.adapter = adapter
        adapter.listener = this
    }

    private fun registerUrlComponent() {
        val bottomSheetBehavior = BottomSheetBehavior.from(urlBinding.bottomSheetContainer)
        bottomSheetBehavior.peekHeight = 0
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
                    binding.overlay.hide()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.animate().alpha(slideOffset).setDuration(0).start()
                animateBottomNav(1 - slideOffset)
            }
        })
    }

    private fun registerFilterComponents() {
        val bottomSheetBehavior = BottomSheetBehavior.from(filterBinding.bottomSheetContainer)
        bottomSheetBehavior.peekHeight = 0
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
                    binding.overlay.hide()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.animate().alpha(slideOffset).setDuration(0).start()
                animateBottomNav(1 - slideOffset)
            }
        })
    }

    private fun registerSearchComponents() {
        adapter.backup()
        if (this::fileSearchComponent.isInitialized) {
            fileSearchComponent.show()
            return
        }
        fileSearchComponent = FileSearchComponentImpl.bind(binding.searchLayout, adapter, requireContext()) { closeSearch() }
        fileSearchComponent.getRequests().observe(
            viewLifecycleOwner
        ) {
            if (it != null) {
                if (it == "") {
                    binding.root.snackbar("Feature not implemented")
                    return@observe
                }
                viewModel.setEvent(HomeEvent.SearchFile(it))
            }
        }
    }

    private fun stopShimmer() {
        showBottomNav()
        binding.shimmerLayout.stopShimmer()
        binding.shimmerLayout.hide()
        binding.topContainer.show()
        binding.swipeRefresh.show()
        binding.fileList.show()
    }

    private fun showShimmer() {
        binding.shimmerLayout.startShimmer()
        binding.shimmerLayout.show()
        binding.fileList.hide()
        binding.topContainer.visibility = View.INVISIBLE
    }

    private fun spotlight() {
        val targets = ArrayList<Target>()

        val firstRoot = FrameLayout(requireContext())
        val first = layoutInflater.inflate(R.layout.home_first_spot, firstRoot)
        val firstTarget = Target.Builder()
            .setAnchor(requireMainActivity().getAddFab())
            .setShape(Circle(120f))
            .setOverlay(first)
            .setOnTargetListener(object : OnTargetListener { override fun onEnded() {} override fun onStarted() {} })
            .build()

        val secondRoot = FrameLayout(requireContext())
        val second = layoutInflater.inflate(R.layout.home_second_spot, secondRoot)
        val secondTarget = Target.Builder()
            .setAnchor(binding.searchButton)
            .setShape(Circle(50f))
            .setOverlay(second)
            .setOnTargetListener(object : OnTargetListener { override fun onEnded() {} override fun onStarted() {} }).build()

        val thirdTarget = Target.Builder()
            .setAnchor(binding.filterButton)
            .setShape(Circle(50f))
            .setOverlay(second)
            .setOnTargetListener(object : OnTargetListener { override fun onEnded() {} override fun onStarted() {} }).build()

        targets.add(firstTarget)
        targets.add(secondTarget)
        targets.add(thirdTarget)

        val spotlight = Spotlight.Builder(requireActivity())
            .setTargets(targets)
            .setBackgroundColorRes(R.color.spotlightBackground)
            .setDuration(1000L)
            .setAnimation(DecelerateInterpolator(2f))
            .setOnSpotlightListener(object : OnSpotlightListener { override fun onStarted() {} override fun onEnded() {} }).build()

        spotlight.start()

        val nextTarget = View.OnClickListener { spotlight.next() }

        // val closeSpotlight = View.OnClickListener { spotlight.finish() }

        first.findViewById<View>(R.id.next_button).setOnClickListener(nextTarget)
        second.findViewById<View>(R.id.next_button).setOnClickListener {
            spotlight.next()
            second.findViewById<TextView>(R.id.title_text).text = getString(R.string.use_filters)
            second.findViewById<TextView>(R.id.info_text).text = getString(R.string.or_you_can_filter_files_based_on_dates_they_were_uploaded)
            second.findViewById<View>(R.id.arrow).animate().rotation(105f).setDuration(500).start()
        }
    }

    private fun showFilter() {
        animateBottomNav(0f)
        binding.overlay.show()
        filterBinding.root.show()
        setState(true, filterBinding.bottomSheetContainer)
    }

    fun closeFilter() {
        if (isFilterEnable()) {
            setState(false, filterBinding.bottomSheetContainer)
            animateBottomNav(1f)
            binding.overlay.hide()
            filterBinding.root.hide()
        }
    }

    private fun showSearch() {
        registerSearchComponents()
        hideBottomNav()
        searchBinding.root.show()
    }

    fun closeSearch() {
        hideKeyboard()
        adapter.restore()
        searchBinding.root.hide()
        showBottomNav()
    }

    fun closeFileUrl() {
        if (isFileUrlEnable()) {
            setState(false, urlBinding.bottomSheetContainer)
            animateBottomNav(1f)
            binding.overlay.hide()
            urlBinding.root.hide()
        }
    }

    private fun setState(show: Boolean, bottomSheetContainer: View) {
        val state = if (show) {
            BottomSheetBehavior.STATE_EXPANDED
        } else {
            BottomSheetBehavior.STATE_COLLAPSED
        }
        BottomSheetBehavior.from(bottomSheetContainer).state = state
    }

    private fun requireMainActivity() = (activity as MainActivity)

    fun isFilterEnable() =
        BottomSheetBehavior.from(filterBinding.bottomSheetContainer).state == BottomSheetBehavior.STATE_EXPANDED

    fun isFileUrlEnable() =
        BottomSheetBehavior.from(urlBinding.bottomSheetContainer).state == BottomSheetBehavior.STATE_EXPANDED

    fun isSearchEnable() = searchBinding.root.isVisible

//    override fun onItemClick(item: File) {
//        //showFileUrl(item)
//        adapter.addUrl(item, Url("sdfb", item.id, "24-02-12", true, 50))
//    }

    override fun hideBottomNav() = requireMainActivity().hideBottomNav()

    override fun showBottomNav() = requireMainActivity().showBottomNav()

    override fun animateBottomNav(offset: Float) = requireMainActivity().animateBottomNav(offset)

    override fun onFileLongClick(file: File) {
        Timber.tag("home").i("LongClickFile: ${file.name}")
    }

    override fun onCreateNewUrl(file: File) {
        viewModel.setEvent(HomeEvent.GenerateUrl(file))
        Timber.tag("home").i("CreateNewUrl: ${file.name}, ${file.id}")
    }

    override fun onLoadMoreUrl(file: File) {
        Timber.tag("home").i("LoadMoreUrl: ${file.name}")
    }

    override fun onUrlLongClick(url: Url, file: File) {
        copyToClipboard(url.id)
    }

    override fun onUrlClick(url: Url, file: File) {
        Timber.tag("home").i("UrlClick: ${file.name}, ${url.id}")
    }

    override fun onItemClick(item: File) {
        viewModel.setEvent(HomeEvent.GetUrls(item))
        Timber.tag("home").i("FileClick: ${item.name}")
    }

    override fun onItemLongClick(item: File) {
        Timber.tag("home").i("LongClickFile: ${item.name}")
    }
}
