package com.github.code.gambit.ui.fragment.home.main

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.code.gambit.R
import com.github.code.gambit.data.model.File
import com.github.code.gambit.data.model.Url
import com.github.code.gambit.databinding.FilterLayoutBinding
import com.github.code.gambit.databinding.FragmentHomeBinding
import com.github.code.gambit.databinding.SearchLayoutBinding
import com.github.code.gambit.repositories.home.HomeRepository
import com.github.code.gambit.ui.activity.main.MainActivity
import com.github.code.gambit.ui.fragment.BottomNavController
import com.github.code.gambit.ui.fragment.home.FileListAdapter
import com.github.code.gambit.ui.fragment.home.FileUrlClickCallback
import com.github.code.gambit.ui.fragment.home.filtercomponent.Filter
import com.github.code.gambit.ui.fragment.home.filtercomponent.FilterComponent
import com.github.code.gambit.ui.fragment.home.filtercomponent.FilterType
import com.github.code.gambit.ui.fragment.home.searchcomponent.FileSearchComponent
import com.github.code.gambit.ui.fragment.home.urlcomponent.UrlComponent
import com.github.code.gambit.utility.extention.copyToClipboard
import com.github.code.gambit.utility.extention.exitFullscreen
import com.github.code.gambit.utility.extention.hide
import com.github.code.gambit.utility.extention.hideKeyboard
import com.github.code.gambit.utility.extention.longToast
import com.github.code.gambit.utility.extention.shortToast
import com.github.code.gambit.utility.extention.show
import com.github.code.gambit.utility.extention.showDefaultMaterialAlert
import com.github.code.gambit.utility.extention.snackbar
import com.github.code.gambit.utility.sharedpreference.LastEvaluatedKeyManager
import com.github.code.gambit.utility.sharedpreference.UserManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.Target
import com.takusemba.spotlight.shape.Circle
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), FileUrlClickCallback, BottomNavController {

    private lateinit var _binding: FragmentHomeBinding
    private val binding get() = _binding

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var _filterBinding: FilterLayoutBinding
    private val filterBinding get() = _filterBinding

    private lateinit var _searchBinding: SearchLayoutBinding
    private val searchBinding get() = _searchBinding

    private var isFirstLoading = true
    private var isLoading = false

    @Inject
    lateinit var homeRepository: HomeRepository

    @Inject
    lateinit var adapter: FileListAdapter

    @Inject
    lateinit var lekManager: LastEvaluatedKeyManager

    @Inject
    lateinit var userManager: UserManager

    private lateinit var fileSearchComponent: FileSearchComponent
    private lateinit var filterComponent: FilterComponent
    private lateinit var urlComponent: UrlComponent

    private val customDateFormat = SimpleDateFormat("dd MMM YY", Locale.getDefault())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        _filterBinding = binding.filterLayout
        _searchBinding = binding.searchLayout
        activity?.window?.exitFullscreen()
        lekManager.flush()
        registerFilterComponents()
        registerUrlComponent()
        binding.linearProgress.hide()
        binding.filterButton.setOnClickListener {
            showFilter()
        }

        binding.searchButton.setOnClickListener {
            showSearch()
        }

        binding.overlay.setOnClickListener {
            closeFilter()
        }

        searchBinding.homeButton.setOnClickListener {
            requireMainActivity().onBackPressed()
        }

        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            setHomeFileEvent(true)
        }

        binding.clearFilter.setOnClickListener {
            filterComponent.clearFilter()
            adapter.restore()
            it.hide()
            binding.headerTitle.text = getString(R.string.recently)
            binding.headerSubtitle.text = getString(R.string.added)
        }

        setUpFileRecyclerView()
        registerEventCallbacks()
        setHomeFileEvent()
    }

    private fun registerUrlComponent() {
        urlComponent = UrlComponent.bind(
            requireContext(), {},
            { url ->
                viewModel.setEvent(HomeEvent.UpdateUrl(url))
            }
        ) { url ->
            viewModel.setEvent(HomeEvent.UpdateUrl(url, true))
        }
    }

    private fun registerEventCallbacks() {
        viewModel.homeState.observe(viewLifecycleOwner) {
            when (it) {
                is HomeState.Error -> {
                    binding.linearProgress.hide()
                    urlComponent.hide()
                    if (this::fileSearchComponent.isInitialized) {
                        fileSearchComponent.setRefreshing(hide = true)
                    }
                    longToast(it.message)
                }
                is HomeState.FilesLoaded -> {
                    if (!it.isSearchResult) {
                        if (isFirstLoading) {
                            stopShimmer()
                        } else {
                            binding.linearProgress.hide()
                        }
                        adapter.addAll(it.files, true)
                        binding.swipeRefresh.isRefreshing = false
                        return@observe
                    }
                    fileSearchComponent.setFileLoaded(it.files)
                }
                is HomeState.Loading -> {
                    if (!it.isSearchResultLoading) {
                        if (isFirstLoading) {
                            showShimmer()
                        } else {
                            binding.linearProgress.show()
                        }
                        return@observe
                    }
                    fileSearchComponent.setRefreshing()
                }
                HomeState.LoadingUrl -> shortToast("Loading Url")
                is HomeState.UrlGenerated -> {
                    binding.linearProgress.hide()
                    copyToClipboard(it.url.id)
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
                is HomeState.FilterResult -> {
                    adapter.backup()
                    binding.clearFilter.show()
                    binding.linearProgress.hide()
                    binding.headerTitle.text = getString(R.string.uploaded)
                    binding.headerSubtitle.text = when (it.filter.type) {
                        FilterType.NULL -> getString(R.string.recently)
                        FilterType.TODAY -> getString(R.string.today)
                        FilterType.YESTERDAY -> getString(R.string.yesterday)
                        FilterType.LAST_WEEK -> getString(R.string.last_week)
                        FilterType.LAST_MONTH -> getString(R.string.last_month)
                        FilterType.CUSTOM -> {
                            val header =
                                getString(R.string.uploaded) + getString(R.string.space) + getString(
                                    R.string.between
                                )
                            binding.headerTitle.text = header
                            val start = customDateFormat.format(it.filter.start)
                            val end = customDateFormat.format(it.filter.end)
                            "$start - $end"
                        }
                    }
                    adapter.addAll(it.files, true)
                }
                is HomeState.FileDeleted -> {
                    requireMainActivity().showSnackBar("${it.file.name} deleted successfully")
                    adapter.remove(it.file)
                    binding.linearProgress.hide()
                }
                is HomeState.UrlUpdateResult -> {
                    if (it.delete) {
                        if (it.success && it.updatedUrl != null) {
                            it.updatedUrl.clicksLeft = 0
                            adapter.deleteUrl(it.updatedUrl)
                            longToast("Url deleted")
                        } else {
                            longToast("Delete of url failed")
                        }
                    } else {
                        if (it.success && it.updatedUrl != null) {
                            adapter.updateUrl(it.updatedUrl)
                        } else {
                            longToast("Update of url failed")
                        }
                    }
                    urlComponent.hide()
                }
            }
        }
    }

    private fun setUpFileRecyclerView() {
        adapter.bindEmptyView(binding.noFileIllustrationContainer)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.fileList.layoutManager = layoutManager
        binding.fileList.setHasFixedSize(false)
        binding.fileList.adapter = adapter
        adapter.listener = this
        binding.fileList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val pastVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (!recyclerView.canScrollVertically(1) &&
                    newState == RecyclerView.SCROLL_STATE_IDLE &&
                    pastVisibleItem + visibleItemCount >= totalItemCount &&
                    !isLoading
                ) {
                    setHomeFileEvent()
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (searchBinding.root.isVisible) {
                    super.onScrolled(recyclerView, dx, dy)
                    return
                }
                if (dy > 0) {
                    hideBottomNav()
                } else {
                    showBottomNav()
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    private fun registerFilterComponents() {
        filterComponent = FilterComponent.bind(
            filterBinding,
            requireContext(),
            requireMainActivity().supportFragmentManager
        )
        filterComponent.addBottomSheetCallback(object : BottomSheetCallback() {
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

        filterComponent.getFilterRequest().observe(viewLifecycleOwner) {
            Timber.tag("filter").i(it.type.toString())
            closeFilter()
            applyFilter(it)
        }
    }

    private fun registerSearchComponents() {
        adapter.backup()
        if (this::fileSearchComponent.isInitialized) {
            fileSearchComponent.show()
            return
        }
        fileSearchComponent = FileSearchComponent.bind(
            binding.searchLayout,
            adapter,
            requireContext()
        ) { closeSearch() }
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

    private fun setHomeFileEvent(cacheClear: Boolean = false) {
        if (!isLoading) {
            isLoading = true
            Handler().postDelayed({ isLoading = false }, 1000)
            viewModel.setEvent(HomeEvent.GetFiles(cacheClear))
        }
    }

    private fun applyFilter(filter: Filter) {
        viewModel.setEvent(HomeEvent.FilterFiles(filter))
    }

    private fun stopShimmer() {
        isFirstLoading = false
        showBottomNav()
        binding.shimmerLayout.stopShimmer()
        binding.shimmerLayout.hide()
        binding.topContainer.show()
        binding.swipeRefresh.show()
        binding.fileList.show()
        spotlight()
    }

    private fun showShimmer() {
        binding.shimmerLayout.startShimmer()
        binding.shimmerLayout.show()
        binding.fileList.hide()
        binding.topContainer.visibility = View.INVISIBLE
    }

    private fun spotlight() {
        if (userManager.isHomeOnBoarded()) {
            return
        }
        val targets = ArrayList<Target>()

        val firstRoot = FrameLayout(requireContext())
        val first = layoutInflater.inflate(R.layout.home_add_file_spot, firstRoot)
        val firstTarget = getTarget(requireMainActivity().getAddFab(), first, 250)

        val secondRoot = FrameLayout(requireContext())
        val second = layoutInflater.inflate(R.layout.home_drag_spot, secondRoot)
        val secondTarget = getTarget(requireMainActivity().getDragView(), second, 100)

        val thirdRoot = FrameLayout(requireContext())
        val third = layoutInflater.inflate(R.layout.home_search_filter_spot, thirdRoot)
        val thirdTarget = getTarget(binding.searchButton, third)

        val fourthTarget = getTarget(binding.filterButton, third)

        targets.add(firstTarget)
        targets.add(secondTarget)
        targets.add(thirdTarget)
        targets.add(fourthTarget)

        val spotlight = Spotlight.Builder(requireActivity())
            .setTargets(targets)
            .setBackgroundColorRes(R.color.spotlightBackground)
            .setDuration(1000L)
            .setAnimation(DecelerateInterpolator(2f))
            .build()

        spotlight.start()

        val nextTarget = View.OnClickListener { spotlight.next() }

        val closeSpotlight = View.OnClickListener {
            userManager.updateHomeOnBoardingState()
            spotlight.finish()
        }

        first.findViewById<View>(R.id.next_button).setOnClickListener(nextTarget)
        second.findViewById<View>(R.id.next_button).setOnClickListener(nextTarget)
        third.findViewById<View>(R.id.next_button).setOnClickListener {
            spotlight.next()
            third.findViewById<TextView>(R.id.title_text).text = getString(R.string.use_filters)
            third.findViewById<TextView>(R.id.info_text).text =
                getString(R.string.or_you_can_filter_files_based_on_dates_they_were_uploaded)
            third.findViewById<View>(R.id.arrow).animate().rotation(105f).setDuration(500).start()
            third.findViewById<View>(R.id.next_button).setOnClickListener(closeSpotlight)
        }
    }

    private fun getTarget(anchorView: View, overlay: View, radius: Int = 50): Target {
        return Target.Builder().setAnchor(anchorView).setShape(Circle(radius.toFloat()))
            .setOverlay(overlay)
            .build()
    }

    private fun showFilter() {
        animateBottomNav(0f)
        binding.overlay.show()
        filterComponent.show()
    }

    fun closeFilter() {
        if (filterComponent.isFilterEnabled()) {
            filterComponent.hide()
            animateBottomNav(1f)
            binding.overlay.hide()
        }
    }

    private fun showSearch() {
        registerSearchComponents()
        hideBottomNav()
        searchBinding.root.show()
    }

    fun closeSearch() {
        hideKeyboard()
        adapter.bindEmptyView(binding.noFileIllustrationContainer)
        adapter.restore()
        searchBinding.root.hide()
        showBottomNav()
    }

    private fun requireMainActivity() = (activity as MainActivity)

    fun isFilterEnable() = filterComponent.isFilterEnabled()

    fun isSearchEnable() = searchBinding.root.isVisible

    override fun hideBottomNav() = requireMainActivity().hideBottomNav()

    override fun showBottomNav() = requireMainActivity().showBottomNav()

    override fun animateBottomNav(offset: Float) = requireMainActivity().animateBottomNav(offset)

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
        urlComponent.show(url, file)
    }

    override fun onItemClick(item: File) {
        viewModel.setEvent(HomeEvent.GetUrls(item))
        Timber.tag("home").i("FileClick: ${item.name}")
    }

    override fun onItemLongClick(item: File) {
        Timber.tag("home").i("FileLongClick: ${item.name}")
        val title = "Confirmation"
        val message = "Are you sure you want to permanently delete ${item.name}?"
        activity?.showDefaultMaterialAlert(
            title,
            message
        ) { viewModel.setEvent(HomeEvent.DeleteFile(item)) }
    }
}
