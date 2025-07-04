package ru.netology.nmedia.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import ru.netology.nmedia.R
import ru.netology.nmedia.util.AndroidUtils
import kotlin.random.Random

class StatsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private var lineWidth = AndroidUtils.dp(context, 5f).toFloat()
    private var fontSize  = AndroidUtils.dp(context, 40f).toFloat()
    private var colors: List<Int> = emptyList()

    init {
        context.withStyledAttributes(attrs, R.styleable.StatsView) {
            lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth)
            fontSize  = getDimension(R.styleable.StatsView_fontSize,  fontSize)
            val arrayId = getResourceId(R.styleable.StatsView_colors, 0)
            if (arrayId != 0) {
                colors = resources.getIntArray(arrayId).toList()
            }
        }
    }

    // Paint для рисования дуг
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style       = Paint.Style.STROKE
        strokeWidth = lineWidth
        strokeCap   = Paint.Cap.BUTT
    }
    // Paint для текста в центре
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style     = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize  = fontSize
    }

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            normalizeData()
            invalidate()
        }

    private var normalized = emptyList<Float>()

    private var radius = 0f
    private lateinit var center: PointF
    private lateinit var oval: RectF

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = minOf(w, h) / 2f - lineWidth / 2f
        center = PointF(w / 2f, h / 2f)
        oval = RectF(
            center.x - radius, center.y - radius,
            center.x + radius, center.y + radius
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (normalized.isEmpty()) return

        var startAngle = -90f
        normalized.forEachIndexed { idx, share ->
            paint.color = colors.getOrNull(idx) ?: randomColor()
            val sweep = share * 360f
            canvas.drawArc(oval, startAngle, sweep, false, paint)
            startAngle += sweep
        }

        // Надпись с суммарным процентом
        val percent = "%.2f%%".format(normalized.sum() * 100)
        canvas.drawText(
            percent,
            center.x,
            center.y + textPaint.textSize / 4f,
            textPaint
        )
    }

    private fun normalizeData() {
        val total = data.sum().takeIf { it > 0f } ?: 1f
        normalized = data.map { it / total }
    }

    private fun randomColor(): Int =
        Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())
}
