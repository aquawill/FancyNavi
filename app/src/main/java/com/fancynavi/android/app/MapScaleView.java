package com.fancynavi.android.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapState;

import java.text.DecimalFormat;

import static java.lang.Math.floor;

class MapScaleView extends View implements Map.OnTransformListener {
    private static final float VIEW_WIDTH_INCH = 1.f,
            VIEW_HEIGHT_INCH = .2f;

    private static final int RULER_STROKE_WIDTH_PX = 4;
    private static final int RULER_HEIGHT_PX = 20;
    private final Paint painter = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Rect[] rulerRects = {new Rect(), new Rect(), new Rect()};
    private String m_text = "";
    private Map m_map;
    private int color = Color.BLACK;

    public MapScaleView(Context context) {
        super(context);
    }

    public MapScaleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MapScaleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setMap(Map map) {
        if (m_map != null) {
            m_map.removeTransformListener(this);
        }
        m_map = map;
        if (m_map != null) {
            m_map.addTransformListener(this);
            updateScale();
        }
    }

    void updateScale() {
        DecimalFormat dfForMeters = new DecimalFormat("##");
        DecimalFormat dfForKilometers = new DecimalFormat("##.00");
        double scale = m_map.getScaleFromZoomLevel(m_map.getZoomLevel());
        if ((int) floor(scale / 100) < 1000) {
            m_text = Double.parseDouble(dfForMeters.format(scale / 100)) + "m";
        } else {
            m_text = Double.parseDouble(dfForKilometers.format(scale / 100 / 1000)) + "km";
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int desiredWidth = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_IN, VIEW_WIDTH_INCH, getResources().getDisplayMetrics());
        int desiredHeight = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_IN, VIEW_HEIGHT_INCH, getResources().getDisplayMetrics());

        painter.setTextSize(desiredHeight / 3);
        painter.setColor(color);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        rulerRects[0].set(0, height - RULER_STROKE_WIDTH_PX, width, height);
        rulerRects[1].set(0, height - RULER_HEIGHT_PX, RULER_STROKE_WIDTH_PX, height);
        rulerRects[2].set(width - RULER_STROKE_WIDTH_PX, height - RULER_HEIGHT_PX, width, height);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        painter.setColor(color);
        for (Rect r : rulerRects) {
            canvas.drawRect(r, painter);
        }
        float width = painter.measureText(m_text);
        canvas.drawText(m_text, canvas.getWidth() / 2 - width / 2,
                canvas.getHeight() - painter.getTextSize() / 2, painter);
    }

    @Override
    public void onMapTransformStart() {

    }

    @Override
    public void onMapTransformEnd(MapState mapState) {
        updateScale();
    }
}