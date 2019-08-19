package com.appxtank.eatin.ui.activity


import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.graphics.Typeface
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.appxtank.eatin.MyApplication
import com.appxtank.eatin.R
import com.appxtank.eatin.data.remote.response.ExcludeList
import com.appxtank.eatin.data.remote.response.Variation
import com.appxtank.eatin.di.ActivityModule
import com.appxtank.eatin.di.component.DaggerActivityComponent
import com.appxtank.eatin.utils.AppConstants
import com.google.common.collect.HashMultimap
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    private val TAG: String = MainActivity::class.java.simpleName
    private lateinit var linearLayout: LinearLayout
    private lateinit var vegSwitch: Switch
    private var excludedList: HashMultimap<Int, Int> = HashMultimap.create()

    @Inject
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        getDependencies()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        linearLayout = findViewById(R.id.linear_layout)
        vegSwitch = findViewById(R.id.switch1)


        observeData()
        viewModel.getMenu()

        vegSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.getMenu()
            linearLayout.removeAllViews()
        }
    }

    private fun observeData() {
        viewModel.menuResponse.observe(this, Observer { menuResponse ->
            val numberOfGroups = menuResponse.variants.variant_groups.size
            createExcludeList(menuResponse.variants.exclude_list)
            for (i in 0 until numberOfGroups) {
                val tv = TextView(this)
                tv.text = menuResponse.variants.variant_groups[i].name
                tv.textSize = resources.getDimension(R.dimen.group_title_text_size)
                tv.typeface = Typeface.DEFAULT_BOLD
                if (AppConstants.radio_group_id.contains(menuResponse.variants.variant_groups[i].group_id.toInt())) {
                    createRadioGroup(
                        menuResponse.variants.variant_groups[i].variations,
                        tv,
                        excludedList.get(menuResponse.variants.variant_groups[i].group_id.toInt())
                    )
                } else {
                    createCheckBox(
                        menuResponse.variants.variant_groups[i].variations,
                        tv,
                        excludedList.get(menuResponse.variants.variant_groups[i].group_id.toInt())
                    )
                }
            }
        })
    }

    private fun getDependencies() {
        DaggerActivityComponent
            .builder()
            .applicationComponent((application as MyApplication).applicationComponent)
            .activityModule(ActivityModule(this))
            .build()
            .inject(this)
    }

    private fun createExcludeList(jsonArray: List<List<ExcludeList>>) {
        val listType = object : TypeToken<ArrayList<ExcludeList>>() {}.type

        for (i in 0 until jsonArray.size) {
            val excludeList = Gson().fromJson<ArrayList<ExcludeList>>(jsonArray[i].toString(), listType)
            for (j in 0 until excludeList.size)
                excludedList.put(excludeList[j].group_id.toInt(), excludeList[j].variation_id.toInt())
        }
    }

    private fun createRadioGroup(
        variation: List<Variation>,
        tv: TextView,
        excluded: MutableSet<Int>
    ) {
        val rb = arrayOfNulls<RadioButton>(variation.size)
        val rg = RadioGroup(this) //create the RadioGroup
        rg.orientation = RadioGroup.VERTICAL//or RadioGroup.VERTICAL
        for (i in 0 until variation.size) {
            rb[i] = RadioButton(this)
            rb[i]!!.text = variation[i].name + " $" + variation[i].price
            rb[i]!!.id = (i + 100)
            if (excluded.contains(variation[i].id.toInt()))
                rb[i]!!.paintFlags = STRIKE_THRU_TEXT_FLAG
            if (vegSwitch.isChecked) {
                if (variation[i].isVeg == 1) {
                    rg.addView(rb[i])
                }
            } else {
                rg.addView(rb[i])
            }
        }
        linearLayout.addView(tv)
        linearLayout.addView(rg)
    }

    private fun createCheckBox(
        variation: List<Variation>,
        tv: TextView,
        excluded: MutableSet<Int>
    ) {
        linearLayout.addView(tv)
        for (i in 0 until variation.size) {
            val cb = CheckBox(this)
            cb.text = variation[i].name + " $" + variation[i].price
            if (excluded.contains(variation[i].id.toInt()))
                cb.paintFlags = STRIKE_THRU_TEXT_FLAG
            if (vegSwitch.isChecked) {
                if (variation[i].isVeg == 1) {
                    linearLayout.addView(cb)
                }
            } else {
                linearLayout.addView(cb)
            }
        }
    }
}
