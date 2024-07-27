package com.example.myapplication.testcolormaker

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.SeekBar
import android.widget.Switch
import android.widget.Toast
import kotlin.math.roundToInt
import android.content.SharedPreferences
import android.content.Context
class MainActivity : AppCompatActivity() {
    private val colorValue = IntArray(3)
    private lateinit var tvColor: TextView
    private var textval = false
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("SwitchState", Context.MODE_PRIVATE)

        tvColor = findViewById(R.id.tv_ColorBox)

        val switches = listOf(
            findViewById<Switch>(R.id.switch_1),
            findViewById<Switch>(R.id.switch_2),
            findViewById<Switch>(R.id.switch_3)
        )

        val textViews = arrayOf(
            findViewById<EditText>(R.id.tv_0),
            findViewById<EditText>(R.id.tv_1),
            findViewById<EditText>(R.id.tv_2)
        )

        val resetButton = findViewById<Button>(R.id.button)
        resetButton.setOnClickListener {
            switches.forEach { it.isChecked = false }
            colorValue.fill(0)
            updateColor()
            textViews.forEachIndexed { index, textView ->
                textView.setText("0")
                val seekBar = findViewById<SeekBar>(resources.getIdentifier("sb_$index", "id", packageName))
                seekBar.progress = 0
                seekBar.isEnabled = false
            }
            sharedPreferences.edit().clear().apply()
        }

        switches.forEachIndexed { index, switch ->
            val seekBar = findViewById<SeekBar>(resources.getIdentifier("sb_$index", "id", packageName))
            val editText = textViews[index]
            switch.isChecked = sharedPreferences.getBoolean("switch_$index", false)
            colorValue[index] = sharedPreferences.getInt("color_$index", 0)
            editText.isEnabled = switch.isChecked
            editText.isFocusable = switch.isChecked
            editText.isFocusableInTouchMode = switch.isChecked

            if (switch.isChecked) {
                updateColor()
            } else {
                val seekBar =
                    findViewById<SeekBar>(resources.getIdentifier("sb_$index", "id", packageName))
                seekBar.progress = 0
                seekBar.isEnabled = false
                colorValue[index] = 0
                textViews[index].setText("0")
                updateColor()
            }
            switch.setOnCheckedChangeListener { _, isChecked ->
                sharedPreferences.edit().putBoolean("switch_$index", isChecked).apply()

                val seekBar =
                    findViewById<SeekBar>(resources.getIdentifier("sb_$index", "id", packageName))
                val editText = textViews[index]
                if (isChecked) {
                    // switch is turned on
                    seekBar.isEnabled = true
                    editText.isFocusable = true
                    editText.isEnabled = true
                    editText.isFocusableInTouchMode = true
                    colorValue[index] = sharedPreferences.getInt("color_$index", 0)
                    seekBar.progress = colorValue[index]
                    editText.setText((colorValue[index].toFloat() / 255.toFloat()).toString())
                    updateColor()
                } else {
                    // switch is turned off
                    seekBar.progress = 0
                    seekBar.isEnabled = false
                    editText.isFocusable = false
                    editText.isEnabled = false
                    editText.isFocusableInTouchMode = false
                    sharedPreferences.edit().putInt("color_$index", colorValue[index]).apply()
                    colorValue[index] = 0
                    editText.setText("0")
                    updateColor()
                }
            }

        }

        textViews.forEachIndexed { index, textView ->
            textView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    try {
                        val v = s.toString()
                        colorValue[index] = if (v.isEmpty()) 0 else (v.toFloat() * 255).roundToInt()
                        updateColor()
                        val seekBar = findViewById<SeekBar>(resources.getIdentifier("sb_$index", "id", packageName))
                        seekBar.progress = colorValue[index] // Set the SeekBar progress when EditText value is changed
                    } catch (e: Exception) {
                        // do nothing
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            val seekBar = findViewById<SeekBar>(resources.getIdentifier("sb_$index", "id", packageName))
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        colorValue[index] = progress
                        updateColor()
                        textViews[index].setText((progress.toFloat() / 255.toFloat()).toString())
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            colorValue[index] = sharedPreferences.getInt("color_$index", 0)
            seekBar.progress = colorValue[index]
            if (switches[index].isChecked) {
                updateColor()
            }
        }

    }

    private fun updateColor() {
        tvColor.setBackgroundColor(Color.argb(255, colorValue[0], colorValue[1], colorValue[2]))
        textval = true
    }
}