package com.appxtank.eatin.ui.activity


import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appxtank.eatin.MyApplication
import com.appxtank.eatin.R
import com.appxtank.eatin.data.remote.response.ExcludeList
import com.appxtank.eatin.data.remote.response.MenuResponse
import com.appxtank.eatin.data.remote.response.Variation
import com.appxtank.eatin.di.ActivityModule
import com.appxtank.eatin.di.component.DaggerActivityComponent
import com.appxtank.eatin.ui.adapter.MenuItemAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    private val TAG: String = MainActivity::class.java.simpleName
    private lateinit var vegSwitch: Switch
    private lateinit var menuItemTitle: TextView
    private lateinit var menuItemChangeButton: Button
    private lateinit var menuItemProceedButton: Button
    private lateinit var menuItemsRecyclerView: RecyclerView
    private lateinit var menuItemAdapter: MenuItemAdapter

    @Inject
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        getDependencies()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
        setUpRecyclerView(vegSwitch.isChecked)
        observeData()
        fetchData()
        switchClick()
        proceedClick()
    }

    private fun init() {
        vegSwitch = findViewById(R.id.switch_veg)
        menuItemTitle = findViewById(R.id.tv_menu_item_header)
        menuItemsRecyclerView = findViewById(R.id.rv_menu_item)
        menuItemProceedButton = findViewById(R.id.btn_proceed)
        menuItemChangeButton = findViewById(R.id.btn_change)
    }

    private fun setUpRecyclerView(isOnlyVeg: Boolean) {
        menuItemAdapter = MenuItemAdapter(this, isOnlyVeg)
        menuItemsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = menuItemAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeData() {
        viewModel.menuResponse.observe(this, Observer { menuResponse ->
            setAdapterData(menuResponse)
        })
    }

    private fun fetchData() {
        viewModel.getMenu()
    }

    private fun switchClick() {
        vegSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            setUpRecyclerView(isOnlyVeg = isChecked)
            menuItemAdapter.setMenuItems(viewModel.menuResponse.value?.variants?.variant_groups!![viewModel.selectedVariants].variations)
            menuItemAdapter.notifyDataSetChanged()
        }
    }

    private fun changeClick() {
        if (viewModel.selectedVariants > 0) {
            menuItemChangeButton.visibility = View.VISIBLE
            menuItemChangeButton.setOnClickListener { v ->
                viewModel.selectedVariants = 0
                viewModel.selectedVariation = null
                fetchData()
                setUpRecyclerView(vegSwitch.isChecked)
                //setAdapterData(null)
                changeClick()
            }
        } else {
            menuItemChangeButton.visibility = View.GONE
        }
    }

    private fun proceedClick() {
        menuItemProceedButton.setOnClickListener { v ->
            if (menuItemAdapter.getSelectedVariation() != null) {
                viewModel.selectedVariants++
                viewModel.selectedVariation = menuItemAdapter.getSelectedVariation()
                changeClick()
                setUpRecyclerView(vegSwitch.isChecked)
                setAdapterData(null)
            } else {
                Toast.makeText(this, "Select any item.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setAdapterData(mR: MenuResponse?) {
        val menuResponse: MenuResponse = mR ?: viewModel.menuResponse.value!!
        menuResponse.let {
            if(viewModel.selectedVariants < menuResponse.variants.variant_groups.size) {
                menuItemTitle.text =
                    menuResponse.variants.variant_groups[viewModel.selectedVariants].name
                menuItemAdapter.setMenuItems(checkForExcludedItem(menuResponse.variants.variant_groups[viewModel.selectedVariants].variations))
            }else{
                btn_change.visibility = View.GONE
                switch_veg.visibility = View.GONE
                tv_menu_item_header.text = "Order Placed"
                btn_proceed.visibility = View.GONE
            }
        }
    }

    private fun checkForExcludedItem(variationList: List<Variation>): List<Variation> {
        if (viewModel.selectedVariation == null) {
            return variationList
        }


        val menuResponse: MenuResponse = viewModel.menuResponse.value!!
        val listType = object : TypeToken<ArrayList<ExcludeList>>() {}.type
        var excludeList: ArrayList<ArrayList<ExcludeList>>? = ArrayList()

        for (elementList in menuResponse.variants.exclude_list)
            excludeList?.add(
                Gson().fromJson<ArrayList<ExcludeList>>(
                    elementList.toString(),
                    listType
                )
            )

        if (excludeList != null && excludeList.size > 0) {
            for (excludeItemList in excludeList) {
                for (i in 0 until excludeItemList.size) {
                    if (excludeItemList[i].group_id.toInt() == viewModel.selectedVariants
                        && excludeItemList[i].variation_id.toInt() == viewModel.selectedVariation!!.id.toInt()) {
                        if(i != 1) {
                            for (variationItem in variationList) {
                                if (excludeItemList[i + 1].variation_id.toInt() == variationItem.id.toInt())
                                    variationItem.isExcluded = true
                            }
                        }else{
                            continue
                        }
                    }
                }
            }
        }


        return variationList
    }

    private fun getDependencies() {
        DaggerActivityComponent
            .builder()
            .applicationComponent((application as MyApplication).applicationComponent)
            .activityModule(ActivityModule(this))
            .build()
            .inject(this)
    }


}
