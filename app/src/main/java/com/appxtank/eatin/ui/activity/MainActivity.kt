package com.appxtank.eatin.ui.activity


import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.appxtank.eatin.MyApplication
import com.appxtank.eatin.R
import com.appxtank.eatin.data.remote.response.MenuResponse
import com.appxtank.eatin.di.ActivityModule
import com.appxtank.eatin.di.component.DaggerActivityComponent
import com.appxtank.eatin.ui.adapter.MenuItemAdapter
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    private val TAG: String = MainActivity::class.java.simpleName
    private lateinit var menuItemAdapter: MenuItemAdapter

    @Inject
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        getDependencies()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpRecyclerView(switch_veg.isChecked)
        observeData()
        fetchData()
        switchClick()
        proceedClick()
    }

    private fun setUpRecyclerView(isOnlyVeg: Boolean) {
        menuItemAdapter = MenuItemAdapter(this, isOnlyVeg)
        rv_menu_item.apply {
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
        switch_veg.setOnCheckedChangeListener { buttonView, isChecked ->
            setUpRecyclerView(isOnlyVeg = isChecked)
            menuItemAdapter.setMenuItems(viewModel.menuResponse.value?.variants?.variant_groups!![viewModel.selectedVariants].variations)
            menuItemAdapter.notifyDataSetChanged()
        }
    }

    private fun changeClick() {
        if (viewModel.selectedVariants > 0) {
            btn_change.visibility = View.VISIBLE
            btn_change.setOnClickListener { v ->
                viewModel.selectedVariants = 0
                viewModel.selectedVariation = null
                fetchData()
                setUpRecyclerView(switch_veg.isChecked)
                //setAdapterData(null)
                changeClick()
            }
        } else {
            btn_change.visibility = View.GONE
        }
    }

    private fun proceedClick() {
        btn_proceed.setOnClickListener { v ->
            if (menuItemAdapter.getSelectedVariation() != null) {
                viewModel.selectedVariants++
                viewModel.selectedVariation = menuItemAdapter.getSelectedVariation()
                changeClick()
                setUpRecyclerView(switch_veg.isChecked)
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
                tv_menu_item_header.text =
                    menuResponse.variants.variant_groups[viewModel.selectedVariants].name
                menuItemAdapter.setMenuItems(viewModel.checkForExcludedItem(menuResponse.variants.variant_groups[viewModel.selectedVariants].variations))
            }else{
                btn_change.visibility = View.GONE
                switch_veg.visibility = View.GONE
                tv_menu_item_header.text = "Order Placed"
                btn_proceed.visibility = View.GONE
            }
        }
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
