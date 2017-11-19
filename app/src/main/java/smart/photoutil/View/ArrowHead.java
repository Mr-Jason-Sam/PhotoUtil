package smart.photoutil.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.View;

import smart.photoutil.CallBack;

/**
 * Created by  on 2017/4/18.
 */

public class ArrowHead extends View implements
        CallBack {
    // SurfaceHolder
    private SurfaceHolder mSurfaceHolder;

    // 画笔
    private Paint mpaint = new Paint();

    private Canvas canvas;

    int mStartX, mStartY, mStopX, mStopY;


    /**
     * 0矩形
     * 1圆
     * 2箭头
     * 3铅笔
     * 4文字
     * 5撤回
     */
    private static int state = 0;


    public ArrowHead(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public ArrowHead(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(); // 初始化

    }

    private void initView() {
    CropImageView.setCallBack(this);
    }




    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }



    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        this.canvas = canvas;
        mpaint.setStyle(Paint.Style.STROKE);
        mpaint.setAntiAlias(true);
        mpaint.setColor(Color.YELLOW);

        jiantou(mStartX, mStartY, mStopX, mStopY);
    }

    public void initData(int startX, int startY, int stopX, int stopY) {
        mStartX = startX;
        mStartY = startY;
        mStopX  = stopX;
        mStopY  = stopY;
        invalidate();
    }


    int x3 = 0;
    int y3 = 0;
    int x4 = 0;
    int y4 = 0;

    int x23 = 0;
    int y23 = 0;
    int x24 = 0;
    int y24 = 0;

    public void jiantou(int startX, int startY, int stopX, int stopY) {
        mpaint.setStyle(Paint.Style.FILL);
        mpaint.setStrokeWidth(1);
        double d = Math.abs(stopX - startX) * Math.abs(stopX - startX) + Math.abs(stopY - startY) * Math.abs(stopY - startY);
        int r = (int) Math.sqrt(d);//两点之间的距离
        double H = r / 6; // 箭头的高度
        double L = r / 30; // 底边的一半

        x3 = 0;
        y3 = 0;
        x4 = 0;
        y4 = 0;

        x23 = 0;
        y23 = 0;
        x24 = 0;
        y24 = 0;

        double awrad = Math.atan(L / H); // 箭头角度
        double arraow_len = Math.sqrt(L * L + H * H); // 箭头的长度
        double[] arrXY_1 = rotateVec(stopX - startX, stopY - startY, awrad, true, arraow_len);
        double[] arrXY_2 = rotateVec(stopX - startX, stopY - startY, -awrad, true, arraow_len);
        //add
        double H2 = r / 6;
        double L2 = r / 45;
        double awrad2 = Math.atan(L2 / H2); // 箭头角度
        double arraow_len2 = Math.sqrt(L2 * L2 + H2 * H2); // 箭头的长度
        double[] arrXY2_1 = rotateVec(stopX - startX, stopY - startY, awrad2, true, arraow_len2);
        double[] arrXY2_2 = rotateVec(stopX - startX, stopY - startY, -awrad2, true, arraow_len2);


        double x_3 = stopX - arrXY_1[0]; // (x3,y3)第一个端点
        double y_3 = stopY - arrXY_1[1];
        double x_4 = stopX - arrXY_2[0]; // (x4,y4)第二个端点
        double y_4 = stopY - arrXY_2[1];

        double x2_3 = stopX - arrXY2_1[0]; // (x3,y3)箭头尾巴的第一个端点
        double y2_3 = stopY - arrXY2_1[1];
        double x2_4 = stopX - arrXY2_2[0]; // (x4,y4)箭头尾巴的第二个端点
        double y2_4 = stopY - arrXY2_2[1];


        Double X3 = new Double(x_3);
        x3 = X3.intValue();
        Double Y3 = new Double(y_3);
        y3 = Y3.intValue();
        Double X4 = new Double(x_4);
        x4 = X4.intValue();
        Double Y4 = new Double(y_4);
        y4 = Y4.intValue();

        //add
        Double X23 = new Double(x2_3);
        x23 = X23.intValue();
        Double Y23 = new Double(y2_3);
        y23 = Y23.intValue();
        Double X24 = new Double(x2_4);
        x24 = X24.intValue();
        Double Y24 = new Double(y2_4);
        y24 = Y24.intValue();


        Path triangle = new Path();
        triangle.moveTo(stopX, stopY);
        triangle.lineTo(x3, y3);
        triangle.lineTo(x4, y4);
        triangle.close();
        canvas.drawPath(triangle, mpaint);


        Path triangle2 = new Path();
        triangle2.moveTo(startX, startY);
        triangle2.lineTo(x23, y23);
        triangle2.lineTo(x24, y24);
        triangle2.close();
        canvas.drawPath(triangle2, mpaint);

    }


    /**
     * 计算三角形的其他两个点
     *
     * @param px
     * @param py
     * @param ang
     * @param isChLen
     * @param newLen
     * @return
     */
    public double[] rotateVec(int px, int py, double ang, boolean isChLen, double newLen) {
        double mathstr[] = new double[2];
        //矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度、新长度
        double vx = px * Math.cos(ang) - py * Math.sin(ang);
        double vy = px * Math.sin(ang) + py * Math.cos(ang);
        if (isChLen) {
            double d = Math.sqrt(vx * vx + vy * vy);
            vx = vx / d * newLen;
            vy = vy / d * newLen;
            mathstr[0] = vx;
            mathstr[1] = vy;
        }
        return mathstr;
    }


}
