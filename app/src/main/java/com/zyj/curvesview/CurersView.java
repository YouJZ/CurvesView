package com.zyj.curvesview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * dec:自定义折线图
 * createBy yjzhao
 * createTime 16/5/14 11:08
 */
public class CurersView extends View {
    public static final String TAG = "CurersView";
    //线条画笔
    private Paint mLinePaint;
    //圆点画笔
    private Paint mCirclePaint;
    //文字画笔
    private Paint mTextPaint;
    //异常显示颜色
    private String unusualColor = "#f52e24";
    //正常显示颜色
    private String normalColor = "#07D6ED";
    //横线显示颜色
    private String LineColor = "#66000000";
    //文字大小
    private int textSize = 20;
    //圆点半径
    private int r = 5;
    //图的说明
    private String normalText = "正常";
    private String unusualText = "异常";
    //控件宽
    int width;
    //控件高
    int height;
    //y轴最大值
    private int y_max = 200;
    //y轴最小值
    private int y_min = 0;
    //y轴间距
    private int y_Space = 20;
    //y轴刻度
    private List<String> mList;
    //七天的日期
    private List<String> mDateList;
    //数据
    private List<DataEntity> mEntityList;
    //坐标点
    private List<PointEntity> mPointentities;
    //风险值
    private Float[] mRisk = {50f, 150f};


    public CurersView(Context context) {
        super(context, null);
    }

    public CurersView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CurersView);
        String lineColor = typedArray.getString(R.styleable.CurersView_LineColor);
        if (!TextUtils.isEmpty(lineColor)) {
            this.LineColor=lineColor;
        }

        init();

    }

    public CurersView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 初始化
     */
    private void init() {
        mLinePaint = new Paint();
        mLinePaint.setColor(Color.parseColor(normalColor));
        mLinePaint.setAntiAlias(true);

        mCirclePaint = new Paint();
        mCirclePaint.setColor(Color.parseColor(normalColor));
        mCirclePaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.parseColor(LineColor));
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(textSize);

        mList = new ArrayList<>();
        mDateList = new ArrayList<>();
        DateTime dateTime = new DateTime(new Date());
        for (int i = 6; i >= 0; i--) {
            DateTime time = dateTime.minusDays(i);
            String day = time.toString("yyyy-MM-dd HH:mm:ss");
            mDateList.add(day);
        }
    }


    //测量View的宽高
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        //如果宽高都是warp_content时，设置控件的宽高的大小
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(400, 600);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(400, heightSpecSize);

        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, 600);

        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    //绘制方法
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawXY(canvas);
        drawData(canvas);

    }

    /**
     * 数据展示
     *
     * @param canvas
     */
    private void drawData(Canvas canvas) {
        //将数据转换成坐标点
        try {
            if (mDateList != null && mEntityList != null && mEntityList.size() != 0) {
                long endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(mDateList.get(6).split(" ")[0] + " 23:59:59").getTime();
                long startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(mDateList.get(0).split(" ")[0] + " 00:00:00").getTime();
                float l = (float) ((width - (float) getTextHeight(mList.get(0), mTextPaint) * 2)) / (endTime - startTime);
                // Log.e(TAG, "drawData: w1="+ (width - (float)getTextHeight(mList.get(0), mTextPaint)* 2)/7);

                float h = (float) ((height - getTextHeight(normalText, mTextPaint) - getTextHeight(mList.get(0), mTextPaint) * 2.0) / mList.size());
                float v = h * (mList.size() - 1);
                float h1 = v / (y_max - y_min);
                // Log.e(TAG, "drawData: h="+v+"  h1="+h1);
                mPointentities = new ArrayList<>();
                for (int i = 0; i < mEntityList.size(); i++) {
                    DataEntity dataEntity = mEntityList.get(i);
                    float x = ((dataEntity.getTime() - startTime) * l + getPaddingLeft() + getTextWidth(mList.get(0), mTextPaint) * 2);
                    float y = getTextHeight(normalText, mTextPaint) * 2 + getPaddingTop() + v - (float) getTextHeight(mList.get(0), mTextPaint) / 4 - h1 * dataEntity.getFloat();
                    PointEntity pointentity = new PointEntity();
                    pointentity.setX(x);
                    pointentity.setY(y);
                    mPointentities.add(pointentity);
                }
                float minRisk = getTextHeight(normalText, mTextPaint) * 2 + getPaddingTop() + v - (float) getTextHeight(mList.get(0), mTextPaint) / 4 - h1 * mRisk[1];
                float maxRisk = getTextHeight(normalText, mTextPaint) * 2 + getPaddingTop() + v - (float) getTextHeight(mList.get(0), mTextPaint) / 4 - h1 * mRisk[0];

                for (int i = 0; i < mPointentities.size() - 1; i++) {
                    PointEntity pointEntity = mPointentities.get(i);
                    PointEntity pointEntityNext = mPointentities.get(i + 1);
                    canvas.drawLine(pointEntity.getX(), pointEntity.getY(), pointEntityNext.getX(), pointEntityNext.getY(), mLinePaint);
                }
                for (int i = 0; i < mPointentities.size(); i++) {
                    PointEntity pointEntity = mPointentities.get(i);
                    Log.e(TAG, "drawData: x="+pointEntity.getX()+"  y="+pointEntity.getY() +"maxRisk="+maxRisk+" minRisk="+minRisk);
                    if (pointEntity.getY() <=maxRisk &&pointEntity.getY() >=minRisk)
                        mCirclePaint.setColor(Color.parseColor(normalColor));
                    else
                        mCirclePaint.setColor(Color.parseColor(unusualColor));
                    canvas.drawCircle(pointEntity.getX(), pointEntity.getY(), r, mCirclePaint);
                }

            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    /**
     * 初始化坐标轴
     *
     * @param canvas
     */
    private void drawXY(Canvas canvas) {
        width = getWidth() - getPaddingLeft() - getPaddingRight();
        height = getHeight() - getPaddingTop() - getPaddingBottom();
        //计算出y轴方向上的线条数
        int count = (getY_max() - getY_min()) / y_Space;
        mList.clear();
        for (int i = 0; i <= count; i++) {
            mList.add(getY_max() - i * y_Space + "");
        }
        drawDes(canvas);
        drawAxis(canvas);


    }

    /**
     * 绘制坐标轴
     * @param canvas
     */
    private void drawAxis(Canvas canvas) {
        //计算出y轴刻度的间距
        float h = (float) ((height - getTextHeight(normalText, mTextPaint) - getTextHeight(mList.get(0), mTextPaint) * 2.0) / mList.size());
        for (int i = 0; i < mList.size(); i++) {
            canvas.drawText(mList.get(i), (float) getPaddingLeft() + getTextWidth(mList.get(0), mTextPaint) / 2, getTextHeight(normalText, mTextPaint) * 2 + getPaddingTop() + h * i, mTextPaint);
            canvas.drawLine(getPaddingLeft() + getTextWidth(mList.get(0), mTextPaint) * 2,
                    getTextHeight(normalText, mTextPaint) * 2 + getPaddingTop() + h * i - (float) getTextHeight(mList.get(0), mTextPaint) / 4,
                    getWidth() - getPaddingRight(),
                    getTextHeight(normalText, mTextPaint) * 2 + getPaddingTop() + h * i - (float) getTextHeight(mList.get(0), mTextPaint) / 4,
                    mTextPaint);
            //Log.e(TAG, "drawXY: xi="+(getPaddingLeft()+getTextWidth(mList.get(0), mTextPaint) * 2));
            // Log.e(TAG, "drawXY: yj="+(getTextHeight(normalText, mTextPaint)*2 + getPaddingTop() + h * i - (float) getTextHeight(mList.get(0), mTextPaint) / 4));
            if (i == mList.size() - 1) {
                float v = (width - (float) getTextHeight(mList.get(0), mTextPaint) * 2) / 7;
                // Log.e(TAG, "drawData: w2="+v);

                for (int j = 0; j < mDateList.size(); j++) {
                    canvas.drawLine(getPaddingLeft() + getTextWidth(mList.get(0), mTextPaint) * 2 + v * j,
                            getTextHeight(normalText, mTextPaint) * 2 + getPaddingTop() + h * i - (float) getTextHeight(mList.get(0), mTextPaint) / 4,
                            getPaddingLeft() + getTextWidth(mList.get(0), mTextPaint) * 2 + v * j,
                            getTextHeight(normalText, mTextPaint) * 2 + getPaddingTop() + h * i - (float) getTextHeight(mList.get(0), mTextPaint) / 4 - 5,
                            mTextPaint);
                    canvas.drawText(mDateList.get(j).substring(8, 10), getPaddingLeft() + getTextWidth(mList.get(0), mTextPaint) * 2 + v * j + v / 2 - (float) getTextWidth(mDateList.get(0).substring(8, 10), mTextPaint) / 2, (float) getTextHeight(mDateList.get(0), mTextPaint) * 2 + getTextHeight(normalText, mTextPaint) * 2 + getPaddingTop() + h * i, mTextPaint);
                }
            }
        }
    }

    /**
     * 绘制图的说明
     * @param canvas
     */
    private void drawDes(Canvas canvas) {
        canvas.drawText(normalText, getPaddingLeft() + getTextWidth(mList.get(0), mTextPaint) * 2, (float) getTextHeight(normalText, mTextPaint) + getPaddingTop(), mTextPaint);
        mCirclePaint.setColor(Color.parseColor(normalColor));
        canvas.drawCircle(getPaddingLeft() + getTextWidth(mList.get(0), mTextPaint) * 3 + (float) getTextWidth(normalText, mTextPaint) / 2, (float) getTextHeight(normalText, mTextPaint) - r + getPaddingTop(), r, mCirclePaint);
        canvas.drawText(unusualText, getPaddingLeft() + getTextWidth(mList.get(0), mTextPaint) * 3 + getTextWidth(normalText, mTextPaint), (float) getTextHeight(normalText, mTextPaint) + getPaddingTop(), mTextPaint);
        mCirclePaint.setColor(Color.parseColor(unusualColor));
        canvas.drawCircle(getPaddingLeft() + getTextWidth(mList.get(0), mTextPaint) * 3 + (float) getTextWidth(normalText, mTextPaint) * 5 / 2, (float) getTextHeight(normalText, mTextPaint) - r + getPaddingTop(), r, mCirclePaint);

    }

    public void setEntityList(List<DataEntity> entityList) {
        mEntityList = entityList;
        Collections.sort(mEntityList, new Comparator<DataEntity>() {
            @Override
            public int compare(DataEntity lhs, DataEntity rhs) {
                if (lhs.getTime() > rhs.getTime())
                    return -1;
                if (lhs.getTime() < rhs.getTime())
                    return 1;
                return 0;
            }
        });
        invalidate();
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void setRisk(Float[] risk) {
        mRisk = risk;
    }

    public void setR(int r) {
        this.r = r;
    }

    public String getUnusualColor() {
        return unusualColor;
    }

    public void setUnusualColor(String unusualColor) {
        this.unusualColor = unusualColor;
    }

    public String getNormalColor() {
        return normalColor;
    }

    public void setNormalColor(String normalColor) {
        this.normalColor = normalColor;
    }

    public int getY_max() {
        return y_max;
    }

    public void setY_max(int y_max) {
        this.y_max = y_max;
    }

    public int getY_min() {
        return y_min;
    }

    public void setY_min(int y_min) {
        this.y_min = y_min;
    }

    public int getY_Space() {
        return y_Space;
    }

    public void setY_Space(int y_Space) {
        this.y_Space = y_Space;
    }

    /**
     * @param text  绘制的文字
     * @param paint 画笔
     * @return 文字的宽度
     */
    public int getTextWidth(String text, Paint paint) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int width = bounds.left + bounds.width();
        return width;
    }

    /**
     * @param text  绘制的文字
     * @param paint 画笔
     * @return 文字的高度
     */
    public int getTextHeight(String text, Paint paint) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int height = bounds.bottom + bounds.height();
        return height;
    }

    static class PointEntity {
        private Float x;
        private Float y;

        public Float getX() {
            return x;
        }

        public void setX(Float x) {
            this.x = x;
        }

        public Float getY() {
            return y;
        }

        public void setY(Float y) {
            this.y = y;
        }
    }


}
