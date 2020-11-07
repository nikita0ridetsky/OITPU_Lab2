package com.rid.oitipu.lab2

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.os.bundleOf
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_setting_bottom_sheet.*

class SettingsBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(brushSize: Int, eraserSize: Int, penColor: Int) =
            SettingsBottomSheetDialogFragment().apply {
                arguments = bundleOf(
                    Pair("brushSize", brushSize),
                    Pair("eraserSize", eraserSize),
                    Pair("penColor", penColor)
                )
            }
    }

    var penSeekBarCallback: ((Int) -> Unit)? = null
    var eraserSeekBarCallback: ((Int) -> Unit)? = null
    var penColorCallback: ((Int) -> Unit)? = null

    var lineCallback: (() -> Unit)? = null
    var rectangleCallback: (() -> Unit)? = null
    var circleCallback: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_setting_bottom_sheet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sb_eraser.apply {
            progress = arguments?.getInt("eraserSize") ?: 0
            max = 100
        }

        sb_pen.apply {
            progress = arguments?.getInt("brushSize") ?: 0
            max = 100
        }

        btn_change_color.backgroundTintList =
            ColorStateList.valueOf(requireArguments().getInt("penColor"))

        sb_eraser.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                eraserSeekBarCallback?.invoke(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        sb_pen.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                penSeekBarCallback?.invoke(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        btn_change_color.setOnClickListener {
            ColorPickerDialogBuilder.with(btn_change_color.context)
                .setTitle("Choose color")
                .initialColor(R.color.colorPrimaryDark)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener {
                    penColorCallback?.invoke(it)
                    btn_change_color.backgroundTintList = ColorStateList.valueOf(it)
                }.build()
                .show()
        }

        btn_circle.setOnClickListener {
            circleCallback?.invoke()
        }

        btn_line.setOnClickListener {
            lineCallback?.invoke()
        }

        btn_rectangle.setOnClickListener {
            rectangleCallback?.invoke()
        }

    }


}