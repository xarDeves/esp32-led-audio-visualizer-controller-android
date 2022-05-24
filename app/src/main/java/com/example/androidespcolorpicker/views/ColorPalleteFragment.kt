package com.example.androidespcolorpicker.views

import android.graphics.Insets
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidespcolorpicker.R
import com.example.androidespcolorpicker.adapters.PalleteRecyclerAdapter
import com.example.androidespcolorpicker.viewmodels.MainActivityViewModel
import kotlin.math.roundToInt


class ColorPalleteFragment : Fragment() {

    lateinit var deleteBtn: Button
    private lateinit var palleteAdapter: PalleteRecyclerAdapter
    private lateinit var viewModel: MainActivityViewModel

    private fun getScreenWidth(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = requireActivity().windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }

    private fun getGridColumnCount(divider: Int): Int {
        val density = resources.displayMetrics.density
        val dpWidth = getScreenWidth() / density
        return (dpWidth / divider).roundToInt()
    }

    fun changeAdapterButtonState() {
        if (this::palleteAdapter.isInitialized)
            if (palleteAdapter.toggled) palleteAdapter.changeAllButtonsState()
    }

    fun colorChangedFromRecycler(color : Int){
        viewModel.colorChangedFromPallete(color)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_color_pallete, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        deleteBtn = view.findViewById(R.id.deleteButton)

        val recyclerView: RecyclerView = view.findViewById(R.id.colorsRecycler)
        palleteAdapter = PalleteRecyclerAdapter(requireActivity(), this, viewModel.data.value!!)
        //divider = inflated view's overall width
        val gridLayoutManager = GridLayoutManager(requireActivity(), getGridColumnCount(95))
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = palleteAdapter

        viewModel.data.observe(viewLifecycleOwner) {
            viewModel.write(it)
            palleteAdapter.notifyDataSetChanged()
        }

        deleteBtn.setOnClickListener {
            palleteAdapter.deleteToggledButtons()
            viewModel.data.value = palleteAdapter.getDataSet()
        }

    }

}