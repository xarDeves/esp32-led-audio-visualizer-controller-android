package com.example.androidespcolorpicker.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidespcolorpicker.R
import com.example.androidespcolorpicker.views.ColorPalleteFragment
import com.example.androidespcolorpicker.views.ColorPickerFragment


class PalleteRecyclerAdapter internal constructor(
    private val context: Context,
    private val parentView: ColorPalleteFragment,
    private val data: MutableSet<Int>,
    private val inflater: LayoutInflater = LayoutInflater.from(context)
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var inflatedButtons: ArrayList<ColorButtonViewHolder> = arrayListOf()
    var toggled = false

    inner class ColorButtonViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val colorButton: Button = view.findViewById(R.id.colorButton)
        private var thisToggled = true
        val toggleImageButton: ImageView = view.findViewById(R.id.toggleButton)
        var rawColor: Int? = null

        fun setColor(color: Int) {
            this.colorButton.setBackgroundColor(color)
            this.rawColor = color
        }

        init {
            colorButton.setOnClickListener {
                //FIXME change to callback, deprecate companion object
                if (!toggled) ColorPickerFragment.Companion.update(rawColor!!)
                toggleImageButton.isActivated = thisToggled
                thisToggled = thisToggled != true
            }

            colorButton.setOnLongClickListener {
                changeAllButtonsState()
                return@setOnLongClickListener true
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = inflater.inflate(R.layout.pallete_color_view, parent, false)
        return ColorButtonViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ColorButtonViewHolder).setColor(data.elementAt(position))
        inflatedButtons.add(holder)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun getDataSet(): MutableSet<Int> {
        return data
    }

    fun deleteToggledButtons() {
        val iterator = inflatedButtons.iterator()
        var index = 0

        while (iterator.hasNext()) {
            val view = iterator.next()

            if (view.toggleImageButton.isActivated) {
                //these need to get called even though "changeButtonState" gets called afterwards
                //since RecyclerView might use the same view after it gets deleted from inflatedButtons
                //(we are iterating inflatedButtons after x buttons are deleted)
                view.toggleImageButton.visibility = View.INVISIBLE
                view.toggleImageButton.isActivated = false

                data.remove(view.rawColor)
                iterator.remove()
                index--
            }
            index++
        }

        changeAllButtonsState()
    }

    fun changeAllButtonsState() {
        toggled = toggled != true

        if (toggled) {
            inflatedButtons.forEach {
                it.toggleImageButton.visibility = View.VISIBLE
            }
            parentView.deleteBtn.isEnabled = true
            return
        }

        inflatedButtons.forEach {
            it.toggleImageButton.visibility = View.INVISIBLE
            it.toggleImageButton.isActivated = false
        }
        parentView.deleteBtn.isEnabled = false
    }

}
