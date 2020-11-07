package com.rid.oitipu.lab2

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var penSize = 10f
        set(value) {
            field = value
            initializePen()
        }

    var eraserSize = 10f
        set(value) {
            field = value
            initializeEraser()
        }

    private var localX = 0f
    private var localY = 0f

    private var startRect = PointF(0f, 0f)
    private var endRect = PointF(0f, 0f)

    private var isDrawMode = true
    var isRectangleMode = false
    var isRoundMode = false

    private val path = Path()
    private val bitmapPaint = Paint(Paint.DITHER_FLAG)

    private var bitmap: Bitmap? = null
    private var canvas: Canvas? = null

    private val paint = Paint().apply {
        isAntiAlias = true
        isDither = true
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = penSize
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SCREEN)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        }

        bitmap?.let {
            canvas = Canvas(it)
        }

        canvas?.drawColor(Color.TRANSPARENT)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        bitmap?.let {
            canvas?.drawBitmap(it, 0f, 0f, bitmapPaint)
        }
        canvas?.drawPath(path, paint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event?.x
        val y = event?.y

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                startRect.x = event.x
                startRect.y = event.y
                endRect.x = event.x
                endRect.y = event.y
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {

                if (!isDrawMode) {
                    path.lineTo(this.localX, this.localY)
                    path.reset()
                    path.moveTo(x ?: 0f, y ?: 0f)
                } else {
                    endRect.x = event.x
                    endRect.y = event.y

                    if (!(isRectangleMode || isRoundMode)) {
                        canvas?.drawLine(startRect.x, startRect.y, endRect.x, endRect.y, paint)
                        startRect.x = endRect.x
                        startRect.y = endRect.y
                    }
                }

                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                when {
                    isRectangleMode -> canvas?.drawRect(
                        startRect.x,
                        startRect.y,
                        endRect.x,
                        endRect.y,
                        paint
                    )
                    isRoundMode -> canvas?.drawOval(
                        startRect.x,
                        startRect.y,
                        endRect.x,
                        endRect.y,
                        paint
                    )
                }

                invalidate()
            }
        }

        return true
    }

    fun initializePen() {
        isDrawMode = true
        paint.apply {
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = penSize
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SCREEN)
        }
    }

    fun initializeEraser() {
        isDrawMode = false
        paint.apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = eraserSize
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
    }

    fun clear() {
        canvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
    }

    fun changeBackground(color: Int = Color.WHITE) {
        canvas?.drawColor(color)
    }

    fun setPenColor(@ColorInt color: Int) {
        paint.color = color
    }

    fun getPenColor() = paint.color

    fun loadImage(bitmap: Bitmap) {
        this.bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        canvas?.setBitmap(this.bitmap)
        bitmap.recycle()
        invalidate()
    }

    fun saveImage(
        filePath: String?,
        filename: String,
        format: CompressFormat?,
        quality: Int
    ): Boolean {
        if (quality > 100) {
            Log.d("saveImage", "quality cannot be greater that 100")
            return false
        }

        val file: File
        var out: FileOutputStream? = null
        try {
            return when (format) {
                CompressFormat.PNG -> {
                    file = File(filePath, "$filename.png")
                    out = FileOutputStream(file)
                    bitmap?.compress(CompressFormat.PNG, quality, out) ?: false
                }
                CompressFormat.JPEG -> {
                    file = File(filePath, "$filename.jpg")
                    out = FileOutputStream(file)
                    bitmap?.compress(CompressFormat.JPEG, quality, out) ?: false
                }
                else -> {
                    file = File(filePath, "$filename.png")
                    out = FileOutputStream(file)
                    bitmap?.compress(CompressFormat.PNG, quality, out) ?: false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                out?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return false
    }


}