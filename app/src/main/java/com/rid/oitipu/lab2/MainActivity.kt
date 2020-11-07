package com.rid.oitipu.lab2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.rid.oitipu.lab2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.drawingView.apply {
            initializePen()
            initializeEraser()
            setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite))
            penSize = 10f
            setPenColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        }

        binding.ivSettings.setOnClickListener {
//            binding.drawingView.changeBackground()
            val dialog = SettingsBottomSheetDialogFragment.newInstance(
                binding.drawingView.penSize.toInt(),
                binding.drawingView.eraserSize.toInt(),
                binding.drawingView.getPenColor()
            )

            dialog.apply {
                show(supportFragmentManager, "SettingsDialog")

                eraserSeekBarCallback = {
                    binding.drawingView.eraserSize = it.toFloat()
                }

                penSeekBarCallback = {
                    binding.drawingView.penSize = it.toFloat()
                }

                penColorCallback = {
                    binding.drawingView.setPenColor(it)
                }

                lineCallback = {
                    binding.drawingView.isRectangleMode = false
                    binding.drawingView.isRoundMode = false
                    dialog.dismiss()
                }

                rectangleCallback = {
                    binding.drawingView.isRectangleMode = true
                    binding.drawingView.isRoundMode = false
                    dialog.dismiss()
                }

                circleCallback = {
                    binding.drawingView.isRectangleMode = false
                    binding.drawingView.isRoundMode = true
                    dialog.dismiss()
                }

            }

        }


    }
}