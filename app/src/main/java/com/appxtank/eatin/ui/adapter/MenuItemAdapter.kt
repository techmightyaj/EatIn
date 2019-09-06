package com.appxtank.eatin.ui.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.appxtank.eatin.R
import com.appxtank.eatin.data.remote.response.Variation


class MenuItemAdapter(private val mContext: Context, private val isOnlyVeg: Boolean) :
    RecyclerView.Adapter<MenuItemAdapter.ViewHolder>() {

    private val TAG = MenuItemAdapter::class.java.simpleName
    private var mData = ArrayList<Variation>()
    var mSelectedItem = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(mContext), parent)
    }


    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (isOnlyVeg && (mData[position].isVeg == 1) && !mData[position].isExcluded) {
            holder.mRadioMenuItem.isChecked = (position == mSelectedItem)
            holder.mRadioMenuItem.text = mData[position].name
            if (mData[position].inStock == 0) {
                holder.mRadioMenuItem.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                holder.mRadioMenuItem.isEnabled = false
            }
            var clickListener = View.OnClickListener {
                mSelectedItem = position
                notifyDataSetChanged()
            }
            holder.mRadioMenuItem.setOnClickListener(clickListener)

        } else if (!isOnlyVeg && !mData[position].isExcluded) {
            holder.mRadioMenuItem.isChecked = (position == mSelectedItem)
            holder.mRadioMenuItem.text = mData[position].name
            if (mData[position].inStock == 0) {
                holder.mRadioMenuItem.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                holder.mRadioMenuItem.isEnabled = false
            }
            var clickListener = View.OnClickListener {
                mSelectedItem = position
                notifyDataSetChanged()
            }
            holder.mRadioMenuItem.setOnClickListener(clickListener)
        } else {
            holder.mRadioMenuItem.visibility = View.GONE
        }
    }

    class ViewHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(
        inflater.inflate(
            R.layout.menu_item_adapter, parent, false
        )
    ) {
        var mRadioMenuItem: RadioButton = itemView.findViewById(R.id.rb_menu_item)
    }

    fun setMenuItems(variation: List<Variation>) {
        this.mData.addAll(variation)
        notifyDataSetChanged()
    }

    fun getSelectedVariation(): Variation? {
        if (mSelectedItem > -1) {
            return mData[mSelectedItem]
        }
        return null
    }

}