package com.example.androidespcolorpicker.views

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TableLayout
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.androidespcolorpicker.R
import com.example.androidespcolorpicker.helpers.NetworkManager
import com.example.androidespcolorpicker.viewmodels.MainActivityViewModel
import top.defaults.colorpicker.ColorPickerView

//TODO retain last used color in sharedPrefs, instantiate ColorPicker with it
class ColorPickerFragment : Fragment() {

    private lateinit var viewModel: MainActivityViewModel

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var networkManager: NetworkManager

    private lateinit var colorG: String
    private lateinit var colorR: String
    private lateinit var colorB: String

    private lateinit var rText: EditText
    private lateinit var gText: EditText
    private lateinit var bText: EditText

    private lateinit var fftBtn: Button
    private lateinit var saveBtn: Button
    private lateinit var ipTextView: EditText
    private lateinit var colorTableLayout: TableLayout
    private lateinit var colorPickerView: ColorPickerView

    private var orientation: Int? = null

    private fun setColor(color: Int) {
        updateColors(color)
        fftBtn.setBackgroundColor(color)
        saveBtn.setBackgroundColor(color)
        //FIXME this breaks everything on horizontal orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT)
            setTextFieldValues()
        networkManager.sendColorPost(colorR, colorG, colorB)
    }

    private fun updatePicker() {
        colorPickerView.setInitialColor(thisFragment.getColorFromRawRGB())
        colorPickerView.reset()
    }

    private fun attachTextColorListeners() {
        rText.setOnFocusChangeListener { view, _ ->
            if (view.hasFocus()) rText.setText("")
            return@setOnFocusChangeListener
        }
        gText.setOnFocusChangeListener { view, _ ->
            if (view.hasFocus()) gText.setText("")
            return@setOnFocusChangeListener
        }
        bText.setOnFocusChangeListener { view, _ ->
            if (view.hasFocus()) bText.setText("")
            return@setOnFocusChangeListener
        }

        rText.doAfterTextChanged {
            if (colorTableLayout.hasFocus()) {

                val rTextVal = rText.text.toString()
                colorR = if (rTextVal == "") "0"
                else rTextVal

                if (colorR.length == 3) gText.requestFocus()
            }
        }
        gText.doAfterTextChanged {
            if (colorTableLayout.hasFocus()) {

                val gTextVal = gText.text.toString()
                colorG = if (gTextVal == "") "0"
                else gTextVal

                if (colorG.length == 3) bText.requestFocus()
            }
        }
        bText.doAfterTextChanged {
            if (colorTableLayout.hasFocus()) {

                val bTextVal = bText.text.toString()
                colorB = if (bTextVal == "") "0"
                else bTextVal

                if (colorB.length == 3) {

                    colorTableLayout.clearFocus()
                    updatePicker()
                }
            }
        }

        ipTextView.doAfterTextChanged {
            networkManager.ip = ipTextView.text.toString().trim()
            saveIpToPrefs()
        }
    }

    private fun fetchXmlElements(view: View) {
        fftBtn = view.findViewById(R.id.FFTButton)
        saveBtn = view.findViewById(R.id.saveButton)
        ipTextView = view.findViewById(R.id.ipTextView)
        colorTableLayout = view.findViewById(R.id.colorValuesTable)
        colorPickerView = view.findViewById(R.id.colorPicker)

        rText = view.findViewById(R.id.rText)
        gText = view.findViewById(R.id.gText)
        bText = view.findViewById(R.id.bText)
    }

    private fun updateColors(color: Int) {
        colorR = Color.red(color).toString()
        colorG = Color.green(color).toString()
        colorB = Color.blue(color).toString()
    }

    private fun setTextFieldValues() {
        rText.setText(colorR)
        gText.setText(colorG)
        bText.setText(colorB)
    }

    private fun saveIpToPrefs() {
        with(sharedPrefs.edit()) {
            putString(R.string.IP.toString(), networkManager.ip)
            apply()
        }
    }

    private fun getColorFromRawRGB(): Int {
        return Color.rgb(
            colorR.toInt(),
            colorG.toInt(),
            colorB.toInt()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Companion.setContext(this)
        networkManager = NetworkManager(requireActivity())
        orientation = requireActivity().resources.configuration.orientation
        return inflater.inflate(R.layout.fragment_color_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchXmlElements(view)

        sharedPrefs = requireContext().getSharedPreferences("ipPref", Context.MODE_PRIVATE)
        val ipFromSharedPrefs = sharedPrefs.getString(R.string.IP.toString(), "")

        ipTextView.setText(ipFromSharedPrefs)
        networkManager.ip = ipFromSharedPrefs!!

        colorPickerView.setInitialColor(Color.MAGENTA)

        attachTextColorListeners()

        //send post to esp for FFT
        fftBtn.setOnClickListener {
            networkManager.sendFFTPost()
        }

        saveBtn.setOnClickListener {
            viewModel.data.value!!.add(getColorFromRawRGB())
            viewModel.data.postValue(viewModel.data.value)

            Toast.makeText(requireContext(), "Color Saved Successfully", Toast.LENGTH_SHORT).show()
        }

        //send post to esp with the color data when color changes
        colorPickerView.subscribe { color: Int, fromUser: Boolean, shouldPropagate: Boolean ->
            setColor(color)
        }

    }

    companion object {

        private lateinit var thisFragment: ColorPickerFragment

        fun setContext(fragment: ColorPickerFragment) {
            thisFragment = fragment
        }

        fun update(color: Int) {
            thisFragment.setColor(color)
            thisFragment.updatePicker()
        }
    }

}