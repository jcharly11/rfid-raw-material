package com.checkpoint.rfid_raw_material.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.PaintDrawable
import android.util.AttributeSet
import android.view.View
import com.checkpoint.rfid_raw_material.R


class CustomBattery @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {
    private var radius: Float = 0f
    private var topPaint = PaintDrawable(Color.RED)
    private var topRect = Rect()
    private var topPaintWidthPercent = 50
    private var topPaintHeightPercent = 13
    private var borderRect = RectF()
    private var borderStrokeWidthPercent = 3
    private var borderStroke: Float = 10f
    private var percentPaint = Paint()
    private var percentRect = RectF()
    private var percentRectTopMin = 0f

    private var borderPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
    }
    private var percent: Int = 0

    private var canvasBattery: Canvas?=null

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.BatteryView)
        try {
            percent = ta.getInt(R.styleable.BatteryView_bv_percent, 0)
        } finally {
            ta.recycle()
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measureWidth = View.getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val measureHeight = (measureWidth * 1.7f).toInt()
        setMeasuredDimension(measureWidth, measureHeight)

        radius = borderStroke / 2
        borderStroke = (borderStrokeWidthPercent * measureWidth).toFloat() / 100


        val topLeft = measureWidth * ((100 - topPaintWidthPercent) / 2) / 100
        val topRight = measureWidth - topLeft
        val topBottom = topPaintHeightPercent * measureHeight / 100
        topRect = Rect(topLeft, 0, topRight, topBottom)

        val borderLeft = borderStroke / 2
        val borderTop = topBottom.toFloat() + borderStroke / 2
        val borderRight = measureWidth - borderStroke / 2
        val borderBottom = measureHeight - borderStroke / 2
        borderRect = RectF(borderLeft, borderTop, borderRight, borderBottom)

        val progressLeft = borderStroke
        percentRectTopMin = topBottom + borderStroke
        val progressRight = measureWidth - borderStroke
        val progressBottom = measureHeight - borderStroke
        percentRect = RectF(progressLeft, percentRectTopMin, progressRight, progressBottom)

        val chargingLeft = borderStroke
        var chargingTop = topBottom + borderStroke
        val chargingRight = measureWidth - borderStroke
        var chargingBottom = measureHeight - borderStroke
        val diff = ((chargingBottom - chargingTop) - (chargingRight - chargingLeft))
        chargingTop += diff / 2
        chargingBottom -= diff / 2
    }

    override fun onDraw(canvas: Canvas) {
        canvasBattery= canvas
        drawTop(canvas)
        drawBody(canvas)
        drawProgress(canvas, percent)
    }

    private fun drawTop(canvas: Canvas) {
        topPaint.bounds = topRect
        topPaint.setCornerRadii(floatArrayOf(radius, radius, radius, radius, 0f, 0f, 0f, 0f))
        topPaint.draw(canvas)
    }

    private fun drawBody(canvas: Canvas) {
        borderPaint.strokeWidth = borderStroke
        canvas.drawRoundRect(borderRect, radius, radius, borderPaint)
    }

    private fun drawProgress(canvas: Canvas, percent: Int) {
        percentPaint.color = getPercentColor(percent)
        percentRect.top = percentRectTopMin + (percentRect.bottom - percentRectTopMin) * (100 - percent) / 100
        canvas.drawRect(percentRect, percentPaint)
    }

    private fun getPercentColor(percent: Int): Int {
        var batteryColor= Color.CYAN

        if(percent<=10) {
            batteryColor = context.getColor(R.color.red_battery)
        }
        if(percent in 11..30) {
            batteryColor = context.getColor(R.color.orange_battery)
        }
        if (percent in 31..50) {
            batteryColor = context.getColor(R.color.yellow_battery)
        }
        if (percent > 50) {
            batteryColor = context.getColor(R.color.green_battery)
        }

        topPaint = PaintDrawable(batteryColor)
        borderPaint.color=batteryColor

        drawBody(canvasBattery!!)
        drawTop(canvasBattery!!)
        return batteryColor
    }

    private fun changeBorder(colorBatt: Int){
        borderPaint = Paint().apply {
            color = colorBatt
            style = Paint.Style.STROKE
        }
    }

    fun charge() {
        invalidate()
    }

    fun unCharge() {
        setPercent(0)
        invalidate()
    }

    fun setPercent(percent: Int) {
        if (percent > 100 || percent < 0) {
            return
        }
        this.percent = percent
        invalidate()
    }

    fun getPercent(): Int {
        return percent
    }
}