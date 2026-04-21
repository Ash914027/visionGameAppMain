package com.example.visiongameapp.results

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

data class DataPoint(val timestamp: Long, val value: Float, val label: String)

class CustomChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#34B1E9")
        strokeWidth = 6f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#34B1E9")
        alpha = 40
        style = Paint.Style.FILL
    }

    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#34B1E9")
        style = Paint.Style.FILL
    }

    private val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.DKGRAY
        textSize = 30f
    }

    private var dataPoints = listOf<DataPoint>()
    private val padding = 100f

    fun setData(points: List<DataPoint>) {
        this.dataPoints = points.sortedBy { it.timestamp }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (dataPoints.size < 2) {
            canvas.drawText("Not enough data to visualize", width / 2f - 150f, height / 2f, textPaint)
            return
        }

        val chartWidth = width - padding * 2
        val chartHeight = height - padding * 2

        val maxVal = 100f // Assuming percentage
        val minVal = 0f

        val xStep = chartWidth / (dataPoints.size - 1)

        val path = Path()
        val fillPath = Path()

        dataPoints.forEachIndexed { index, point ->
            val x = padding + (index * xStep)
            val normalizedY = (point.value - minVal) / (maxVal - minVal)
            val y = padding + chartHeight - (normalizedY * chartHeight)

            if (index == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, padding + chartHeight)
                fillPath.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                fillPath.lineTo(x, y)
            }

            if (index == dataPoints.size - 1) {
                fillPath.lineTo(x, padding + chartHeight)
                fillPath.close()
            }
        }

        // Draw fill
        canvas.drawPath(fillPath, fillPaint)
        
        // Draw line
        canvas.drawPath(path, linePaint)

        // Draw points and labels
        dataPoints.forEachIndexed { index, point ->
            val x = padding + (index * xStep)
            val normalizedY = (point.value - minVal) / (maxVal - minVal)
            val y = padding + chartHeight - (normalizedY * chartHeight)

            canvas.drawCircle(x, y, 10f, pointPaint)
            
            // Draw value text
            val valText = "${point.value.toInt()}%"
            canvas.drawText(valText, x - 30f, y - 20f, textPaint)
        }

        // Draw axes
        canvas.drawLine(padding, padding, padding, padding + chartHeight, axisPaint) // Y
        canvas.drawLine(padding, padding + chartHeight, padding + chartWidth, padding + chartHeight, axisPaint) // X
    }
}
