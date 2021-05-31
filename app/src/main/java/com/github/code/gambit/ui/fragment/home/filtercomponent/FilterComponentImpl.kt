package com.github.code.gambit.ui.fragment.home.filtercomponent

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import com.github.code.gambit.R
import com.github.code.gambit.databinding.FilterLayoutBinding
import com.github.code.gambit.utility.extention.hide
import com.github.code.gambit.utility.extention.show
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Initializes all the components required for setting up the filter options, and related events.
 * Use [FilterComponent.bind] method for getting the instance of this class
 * @author Danish Jamal [https://github.com/danishjamal104]
 * @param context context where this is injected/initialised
 * @param binding instance of the [FilterLayoutBinding]
 * @property fragmentManager [FragmentManager] to be used for displaying date picker
 * @constructor creates a default filter layout with all the required dynamic dates
 */
class FilterComponentImpl(val binding: FilterLayoutBinding, val context: Context) :
    FilterComponent {

    private val filterRequest = MutableLiveData<Filter>()
    private val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetContainer)
    private var currentFilterType: FilterType = FilterType.NULL
    private var currentCheckView: View? = null
    private var today: Filter = getToday()
    private var yesterday: Filter = getYesterday()
    private var lastWeek: Filter = getLastWeek()
    private var lastMonth: Filter = getLastMonth()
    private var customRange: Filter = getDefaultCustomRange()
    private val defaultDateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

    var fragmentManager: FragmentManager? = null

    init {
        setUpFilterUI()
        registerBottomSheet()
        binding.today.root.setOnClickListener { check(FilterType.TODAY) }
        binding.yesterday.root.setOnClickListener { check(FilterType.YESTERDAY) }
        binding.lastWeek.root.setOnClickListener { check(FilterType.LAST_WEEK) }
        binding.lastMonth.root.setOnClickListener { check(FilterType.LAST_MONTH) }
        binding.customRange.root.setOnClickListener { check(FilterType.CUSTOM) }
    }

    /**
     * Selects the filter and apply necessary changes based on [FilterType]
     * @param filterType type of filter which is to be selected
     */
    private fun check(filterType: FilterType) {
        currentCheckView.hide()
        when (filterType) {
            FilterType.NULL -> {
            }
            FilterType.TODAY -> {
                currentFilterType = FilterType.TODAY
                currentCheckView = binding.today.check
                filterRequest.value = today
            }
            FilterType.YESTERDAY -> {
                currentFilterType = FilterType.YESTERDAY
                currentCheckView = binding.yesterday.check
                filterRequest.value = yesterday
            }
            FilterType.LAST_WEEK -> {
                currentFilterType = FilterType.LAST_WEEK
                currentCheckView = binding.lastWeek.check
                filterRequest.value = lastWeek
            }
            FilterType.LAST_MONTH -> {
                currentFilterType = FilterType.LAST_MONTH
                currentCheckView = binding.lastMonth.check
                filterRequest.value = lastMonth
            }
            FilterType.CUSTOM -> {
                launchDatePicker { start, end ->
                    customRange.start = Date(start)
                    customRange.end = Date(end)
                    setDates()
                    currentFilterType = FilterType.CUSTOM
                    currentCheckView = binding.customRange.check
                    filterRequest.value = customRange
                }
            }
        }
    }

    /**
     * use to set the [BottomSheetBehavior] properties.
     */
    private fun registerBottomSheet() {
        bottomSheetBehavior.peekHeight = 0
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    /**
     * use to bind the UI with data
     * @see setDates
     */
    private fun setUpFilterUI() {
        binding.today.helper.text = getString(R.string.today)
        binding.today.illustration.setImageDrawable(getDrawable(R.drawable.ic_calendar_today))
        binding.yesterday.helper.text = getString(R.string.yesterday)
        binding.yesterday.illustration.setImageDrawable(getDrawable(R.drawable.ic_calendar_yesterday))
        binding.lastWeek.helper.text = getString(R.string.last_week)
        binding.lastWeek.illustration.setImageDrawable(getDrawable(R.drawable.ic_calendar_last_week))
        binding.lastMonth.helper.text = getString(R.string.last_month)
        binding.lastMonth.illustration.setImageDrawable(getDrawable(R.drawable.ic_calendar_last_month))
        binding.customRange.helper.text = getString(R.string.custom_range)
        binding.customRange.illustration.setImageDrawable(getDrawable(R.drawable.ic_calendar_custom))
        setDates()
    }

    /**
     * use to set/update the dynamic dates in UI
     * @see today
     * @see yesterday
     * @see lastWeek
     * @see lastMonth
     * @see customRange
     */
    @SuppressLint("SetTextI18n")
    private fun setDates() {
        binding.today.date.text = defaultDateFormat.format(today.start)
        binding.yesterday.date.text = defaultDateFormat.format(yesterday.start)
        binding.lastWeek.date.text =
            "${defaultDateFormat.format(lastWeek.start)} - ${defaultDateFormat.format(lastWeek.end)}"
        binding.lastMonth.date.text =
            "${defaultDateFormat.format(lastMonth.start)} - ${defaultDateFormat.format(lastMonth.end)}"
        binding.customRange.date.text =
            "${defaultDateFormat.format(customRange.start)} - ${defaultDateFormat.format(customRange.end)}"
    }

    /**
     * launches the date picker for selecting custom date range
     * @param onSuccess lambda called when start and end date is chosen correctly
     * @property fragmentManager used for displaying the date picker dialog
     * @see R.style.ThemeOverlay_App_DatePicker
     */
    private fun launchDatePicker(onSuccess: (start: Long, end: Long) -> Unit) {
        val defaultDate: Pair<Long, Long> = Pair(customRange.start.time, customRange.end.time)
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker().setSelection(defaultDate)
            .setTitleText(getString(R.string.select_date_range))
            .setTheme(R.style.ThemeOverlay_App_DatePicker).build()
        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            selection?.let { pair ->
                pair.first?.let { start ->
                    pair.second?.let { end ->
                        onSuccess(start, end)
                    }
                }
            }
        }
        dateRangePicker.addOnNegativeButtonClickListener { currentCheckView.show() }
        dateRangePicker.addOnCancelListener { currentCheckView.show() }
        fragmentManager?.let { dateRangePicker.show(it, "picker") }
    }

    /**
     * @return the [Filter] instance holding today's date
     * @see today
     */
    private fun getToday(): Filter {
        return Calendar.getInstance().let {
            Filter(FilterType.TODAY, it.timeToStartOfDay(), it.timeToEndOfDay())
        }
    }

    /**
     * @return the [Filter] instance holding yesterday's date
     * @see yesterday
     */
    private fun getYesterday(): Filter {
        return Calendar.getInstance().let {
            it.add(Calendar.DAY_OF_MONTH, -1)
            val start = it.timeToStartOfDay()
            val end = it.timeToEndOfDay()
            Filter(FilterType.YESTERDAY, start, end)
        }
    }

    /**
     * @return the [Filter] instance holding last week's date
     * @see lastWeek
     */
    private fun getLastWeek(): Filter {
        return Calendar.getInstance().let {
            it.add(Calendar.WEEK_OF_MONTH, -1)
            it.set(Calendar.DAY_OF_WEEK, it.getActualMinimum(Calendar.DAY_OF_WEEK))
            val start = it.timeToStartOfDay()
            val end = it.timeToEndOfDay()
            Filter(FilterType.LAST_WEEK, start, end)
        }
    }

    /**
     * @return the [Filter] instance holding last month's date
     * @see lastMonth
     */
    private fun getLastMonth(): Filter {
        return Calendar.getInstance().let {
            it.add(Calendar.MONTH, -1)
            it.set(Calendar.DAY_OF_MONTH, it.getActualMinimum(Calendar.DAY_OF_MONTH))
            val start = it.timeToStartOfDay()
            it.add(Calendar.DATE, 30)
            Filter(FilterType.LAST_MONTH, start, it.timeToEndOfDay())
        }
    }

    /**
     * @return the [Filter] instance holding 10 days range before today's date
     * @see customRange
     */
    private fun getDefaultCustomRange(): Filter {
        return Calendar.getInstance().let {
            val end = today.start
            it.time = end
            it.add(Calendar.DAY_OF_YEAR, -10)
            Filter(FilterType.CUSTOM, it.timeToStartOfDay(), today.end)
        }
    }

    /**
     * sets the time to the start of day and returns the [Date] instance
     * @receiver [Calendar]
     * @return the [Date] instance after changing the required fields
     */
    private fun Calendar.timeToStartOfDay(): Date {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        return time
    }

    /**
     * sets the time to the end of day and returns the [Date] instance
     * @receiver [Calendar]
     * @return the [Date] instance after changing the required fields
     */
    private fun Calendar.timeToEndOfDay(): Date {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
        return time
    }

    /**
     * use to set the state of bottom sheet
     * @param show `true` for displaying and `false` for hiding
     */
    private fun setState(show: Boolean) {
        val state = if (show) {
            BottomSheetBehavior.STATE_EXPANDED
        } else {
            BottomSheetBehavior.STATE_COLLAPSED
        }
        bottomSheetBehavior.state = state
    }

    /**
     * @param resId string resource if
     * @return the string resource at [resId]
     */
    private fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }

    /**
     * @param resId drawable resource id
     * @return the drawable resource at [resId]
     */
    private fun getDrawable(@DrawableRes resId: Int) = ContextCompat.getDrawable(context, resId)

    /**
     * @return [filterRequest]
     */
    override fun getFilterRequest(): MutableLiveData<Filter> {
        return filterRequest
    }

    /**
     * adds the bottom sheet callback to [bottomSheetBehavior]
     * @param callback the instance of [BottomSheetBehavior.BottomSheetCallback]
     */
    override fun addBottomSheetCallback(callback: BottomSheetBehavior.BottomSheetCallback) {
        bottomSheetBehavior.addBottomSheetCallback(callback)
    }

    /**
     * clears the current selected filter
     */
    override fun clearFilter() {
        currentFilterType = FilterType.NULL
        currentCheckView = null
    }

    /**
     * shows the filter layout bottom sheet
     */
    override fun show() {
        binding.root.show()
        setState(true)
        currentCheckView.show()
    }

    /**
     * shows the filter layout bottom sheet
     */
    override fun hide() {
        setState(false)
        binding.root.hide()
    }

    /**
     * @return [Boolean] if filter is enable
     */
    override fun isFilterEnabled() = bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED
}

enum class FilterType { NULL, TODAY, YESTERDAY, LAST_WEEK, LAST_MONTH, CUSTOM }
data class Filter(val type: FilterType, var start: Date, var end: Date)
