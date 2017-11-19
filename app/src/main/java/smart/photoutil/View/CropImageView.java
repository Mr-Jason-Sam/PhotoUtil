package smart.photoutil.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.telecom.Call;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import smart.photoutil.CallBack;
import smart.photoutil.R;
import smart.photoutil.util.FloatDrawable;

public class CropImageView extends View {
    private String TAG = "CropImageView";
    // 在touch重要用到的点，
    private float mX_1 = 0;
    private float mY_1 = 0;
    // 默认裁剪的宽高
    private int cropWidth;
    private int cropHeight;
    // 浮层Drawable的四个点
    private final int EDGE_LT = 1;
    private final int EDGE_RT = 2;
    private final int EDGE_LB = 3;
    private final int EDGE_RB = 4;
    private final int EDGE_MOVE_IN = 5;
    private final int EDGE_MOVE_OUT = 6;
    private final int EDGE_NONE = 7;
    private final int MIXH = 90;
    private final int MIXW = 160;
    private final int TOP = 0;
    private final int BOTTOM = 648;
    private final int RIGHT = 1152;
    private final int LEFT = 0;

    private float SCALE = (float)9/16;

    public int currentEdge = EDGE_NONE;

    protected float oriRationWH = 0;

    protected Drawable mDrawable;
    protected FloatDrawable mFloatDrawable;

    protected Rect mOriginDrawable = new Rect(TOP, LEFT, RIGHT, BOTTOM);
    protected Rect mDrawableDst = new Rect();// 图片Rect
    protected Rect mDrawableFloat = new Rect();// 浮层的Rect
    protected boolean isFirst = true;
    private boolean isTouchInSquare = true;

    protected Context mContext;

    private static CallBack mCallBack;
    private String mDescribe;
    private Path dashPath;
    private boolean mSelect = true;

    public CropImageView(Context context) {
        super(context);
        init(context);
    }

    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CropImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @SuppressLint("NewApi")
    private void init(Context context) {
        this.mContext = context;
        try {
            if (android.os.Build.VERSION.SDK_INT >= 11) {
                this.setLayerType(LAYER_TYPE_SOFTWARE, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mFloatDrawable = new FloatDrawable(context);
    }

    public void setDrawable(Bitmap bitmap, int cropWidth, int cropHeight) {
        this.mDrawable = new BitmapDrawable(bitmap);
        this.cropWidth = cropWidth;
        this.cropHeight = cropHeight;
        this.isFirst = true;
        invalidate();
    }

    public void setSelect(boolean select){
        mSelect = select;
    }

    public void setDescribe(String describe){
        mDescribe = describe;
    }

    public static void setCallBack(CallBack callBack){
        mCallBack = callBack;
    }

    public static void doCallBackMethod(int startX, int startY, int stopX, int stopY){
        mCallBack.initData(startX, startY, stopX, stopY);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mX_1 = event.getX();
                mY_1 = event.getY();
                currentEdge = getTouch((int) mX_1, (int) mY_1);

                break;

            case MotionEvent.ACTION_MOVE:
                int dx = (int) (event.getX() - mX_1);
                int dy = (int) (event.getY() - mY_1);

                mX_1 = event.getX();
                mY_1 = event.getY();

                if (getFloatRight() - getFloatLeft() < MIXW || getFloatBottom() - getFloatTop() < MIXH) {
                    mDrawableFloat.set(mDrawableFloat.left,
                            mDrawableFloat.top,
                            mDrawableFloat.left + MIXW,
                            mDrawableFloat.top + MIXH);
                    mDrawableFloat.sort();
                    invalidate();
                    break;
                }

                // 根據得到的那一个角，并且变换Rect
                if (!(dx == 0 && dy == 0)) {
                    switch (currentEdge) {
                        case EDGE_LT:
                            actionLT(dx);
                            if (getFloatTop() <= TOP)
                                actionLB(dx);
                            if (getFloatLeft() <= LEFT)
                               actionRT(dx);
                            break;
                        case EDGE_RT:
                            actionRT(dx);
                            if (getFloatRight() >= RIGHT)
                                actionLT(dx);
                            if (getFloatTop() <= TOP)
                                actionRB(dx);
                            break;

                        case EDGE_LB:
                            actionLB(dx);
                            if (getFloatBottom() >= BOTTOM)
                                actionLT(dx);
                            if (getFloatLeft() <= LEFT)
                                actionRB(dx);
                            break;

                        case EDGE_RB:
                            actionRB(dx);
                            if (getFloatRight() >= RIGHT)
                                actionLB(dx);
                            if (getFloatBottom() >= BOTTOM)
                                actionRT(dx);
                            break;

                        case EDGE_MOVE_IN:
                            // 因为手指一直在移动，应该实时判断是否超出Layout区域
                            isTouchInSquare = mOriginDrawable.contains((int) event.getX(),
                                    (int) event.getY());
                            Log.i(TAG,"event.getX(): " + event.getX() + " event.getY(): " + event.getY());
                            Log.i(TAG,"mX_1: " + mX_1 + " mY_1: " + mY_1);
                            Log.i(TAG,"dx: " + dx + " dy: " + dy);

                            if (isTouchInSquare) {
                                Log.i(TAG,"----isTouchInSquare");
                                mDrawableFloat.offset(dx, dy);
                            }else{
                                if ((mDrawableFloat.top <= TOP && mDrawableFloat.left <= LEFT) ||
                                        (mDrawableFloat.top <= TOP && mDrawableFloat.right >= RIGHT) ||
                                        (mDrawableFloat.bottom >= BOTTOM && mDrawableFloat.left <= LEFT) ||
                                        (mDrawableFloat.bottom >= BOTTOM && mDrawableFloat.right >= RIGHT)) {
                                    mDrawableFloat.offset(0, 0);

                                }
                                else if (mDrawableFloat.top <= TOP || mDrawableFloat.bottom >= BOTTOM) {
                                    mDrawableFloat.offset(dx, 0);

                                }
                                else if (mDrawableFloat.left <= LEFT || mDrawableFloat.right >= RIGHT)
                                    mDrawableFloat.offset(0, dy);
                            }



                            break;

                        case EDGE_MOVE_OUT:

                            break;
                    }
                    mDrawableFloat.sort();
                    invalidate();
                }
//                    head.initData();
                break;
        }
        return true;
    }

    public void actionRB(int dx){
        mDrawableFloat.set(mDrawableFloat.left,
                mDrawableFloat.top,
                mDrawableFloat.right + dx,
                (int)(mDrawableFloat.top + SCALE*(mDrawableFloat.right - mDrawableFloat.left)));
    }

    public void actionLB(int dx){
        mDrawableFloat.set(mDrawableFloat.left + dx,
                mDrawableFloat.top,
                mDrawableFloat.right,
                (int)(mDrawableFloat.top + SCALE*(mDrawableFloat.right - mDrawableFloat.left)));
    }

    public void actionRT(int dx){
        mDrawableFloat.set(mDrawableFloat.left,
                (int)(mDrawableFloat.bottom - SCALE*(mDrawableFloat.right - mDrawableFloat.left)),
                mDrawableFloat.right + dx,
                mDrawableFloat.bottom);
    }

    public void actionLT(int dx){
        mDrawableFloat.set(mDrawableFloat.left + dx,
                (int)(mDrawableFloat.bottom - SCALE*(mDrawableFloat.right - mDrawableFloat.left)),
                mDrawableFloat.right,
                mDrawableFloat.bottom);
    }

    // 根据初触摸点判断是触摸的Rect哪一个角
    public int getTouch(int eventX, int eventY) {
        Rect mFloatDrawableRect = mDrawableFloat;
        int mFloatDrawableWidth = 10;
        int mFloatDrawableHeight = 10;
        if ((mFloatDrawableRect.left - mFloatDrawableWidth) <= eventX
                && eventX < (mFloatDrawableRect.left + mFloatDrawableWidth)
                && (mFloatDrawableRect.top - mFloatDrawableHeight) <= eventY
                && eventY < (mFloatDrawableRect.top + mFloatDrawableHeight)) {
            return EDGE_LT;
        } else if ((mFloatDrawableRect.right - mFloatDrawableWidth) <= eventX
                && eventX < (mFloatDrawableRect.right + mFloatDrawableWidth)
                && (mFloatDrawableRect.top - mFloatDrawableHeight) <= eventY
                && eventY < (mFloatDrawableRect.top + mFloatDrawableHeight)) {
            return EDGE_RT;
        } else if ((mFloatDrawableRect.left - mFloatDrawableWidth) <= eventX
                && eventX < (mFloatDrawableRect.left + mFloatDrawableWidth)
                && (mFloatDrawableRect.bottom - mFloatDrawableHeight) <= eventY
                && eventY < (mFloatDrawableRect.bottom + mFloatDrawableHeight)) {
            return EDGE_LB;
        } else if ((mFloatDrawableRect.right - mFloatDrawableWidth) <= eventX
                && eventX < (mFloatDrawableRect.right + mFloatDrawableWidth)
                && (mFloatDrawableRect.bottom - mFloatDrawableHeight) <= eventY
                && eventY < (mFloatDrawableRect.bottom + mFloatDrawableHeight)) {
            return EDGE_RB;
        } else if (mFloatDrawableRect.contains(eventX, eventY)) {
            return EDGE_MOVE_IN;
        }
        return EDGE_MOVE_OUT;
    }

    public int getFloatLeft(){ return mDrawableFloat.left; }

    public int getFloatTop(){
        return mDrawableFloat.top;
    }

    public int getFloatRight(){
        return mDrawableFloat.right;
    }

    public int getFloatBottom(){
        return mDrawableFloat.bottom;
    }

    public int getStatus(){
        return this.currentEdge;
    }

    public void setStatus(int status){
        this.currentEdge = status;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDrawable == null) {
            return;
        }

        if (mDrawable.getIntrinsicWidth() == 0 || mDrawable.getIntrinsicHeight() == 0) {
            return;
        }

        Paint paint = new Paint();
        paint.setStrokeWidth(2);
        paint.setColor(Color.WHITE);

        configureBounds();

        if (mSelect == true) {
            //在画布上画浮层FloatDrawable,Region.Op.DIFFERENCE是表示Rect交集的补集
            canvas.clipRect(mDrawableFloat, Region.Op.DIFFERENCE);
            canvas.drawColor(Color.parseColor("#a0000000"));
            canvas.restore();
//         画浮层
            mFloatDrawable.draw(canvas);
        }else {

            //绘制虚线
            dashPath = new Path();
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStrokeWidth(3);
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setPathEffect(new DashPathEffect(new float[]{15, 5}, 0));
            dashPath.reset();
            dashPath.moveTo(getFloatLeft(), getFloatTop());
            dashPath.lineTo(getFloatRight(), getFloatTop());
            dashPath.lineTo(getFloatRight(), getFloatBottom());
            dashPath.lineTo(getFloatLeft(), getFloatBottom());
            dashPath.lineTo(getFloatLeft(), getFloatTop());
            canvas.drawPath(dashPath, paint);
        }

        //靶心
        canvas.drawLine (getFloatLeft() + (getFloatRight() - getFloatLeft())/2 - 15,
                getFloatTop() + (getFloatBottom() - getFloatTop())/2,
                getFloatLeft() + (getFloatRight() - getFloatLeft())/2 + 15,
                getFloatTop() + (getFloatBottom() - getFloatTop())/2,
                paint);
        canvas.drawLine (getFloatLeft() + (getFloatRight() - getFloatLeft())/2,
                getFloatTop() + (getFloatBottom() - getFloatTop())/2 + 15,
                getFloatLeft() + (getFloatRight() - getFloatLeft())/2,
                getFloatTop() + (getFloatBottom() - getFloatTop())/2 - 15,
                paint);

        //绘制文字
        paint.setTextSize(25);
        Typeface font = Typeface.create(Typeface.SERIF, Typeface.NORMAL);
        paint.setTypeface(font);
        canvas.drawText(mDescribe, (float) getFloatLeft() + 10, (float)getFloatBottom() - 10, paint);




//        canvas.restore();
    }

    protected void configureBounds() {
        // configureBounds在onDraw方法中调用
        // isFirst的目的是下面对mDrawableSrc和mDrawableFloat只初始化一次，
        // 之后的变化是根据touch事件来变化的，而不是每次执行重新对mDrawableSrc和mDrawableFloat进行设置
        if (isFirst) {
            oriRationWH = ((float) mDrawable.getIntrinsicWidth())
                    / ((float) mDrawable.getIntrinsicHeight());

            final float scale = mContext.getResources().getDisplayMetrics().density;
            int mDrawableW = (int) (mDrawable.getIntrinsicWidth() * scale + 0.5f);
            if ((mDrawable.getIntrinsicHeight() * scale + 0.5f) > getHeight()) {
                mDrawableW = (int) ((mDrawable.getIntrinsicWidth() * scale + 0.5f)
                        * (getHeight() / (mDrawable.getIntrinsicHeight() * scale + 0.5f)));
            }
            int w = Math.min(getWidth(), mDrawableW);
            int h = (int) (w / oriRationWH);

            int left = (getWidth() - w) / 2;
            int top = (getHeight() - h) / 2;
            int right = left + w;
            int bottom = top + h;


            int floatWidth = dipToPx(mContext, cropWidth);
            int floatHeight = dipToPx(mContext, cropHeight);

            if (floatWidth > getWidth()) {
                floatWidth = getWidth();
                floatHeight = cropHeight * floatWidth / cropWidth;
            }

            if (floatHeight > getHeight()) {
                floatHeight = getHeight();
                floatWidth = cropWidth * floatHeight / cropHeight;
            }

            int floatLeft = (getWidth() - floatWidth) / 2;
            int floatTop = (getHeight() - floatHeight) / 2;
            mDrawableFloat.set(floatLeft, floatTop, floatLeft + floatWidth, floatTop + floatHeight);

            isFirst = false;
        } else if (getTouch((int) mX_1, (int) mY_1) == EDGE_MOVE_IN) {
            if (mDrawableFloat.left < 0) {
                mDrawableFloat.right = mDrawableFloat.width();
                mDrawableFloat.left = 0;
            }
            if (mDrawableFloat.top < 0) {
                mDrawableFloat.bottom = mDrawableFloat.height();
                mDrawableFloat.top = 0;
            }
            if (mDrawableFloat.right > getWidth()) {
                mDrawableFloat.left = getWidth() - mDrawableFloat.width();
                mDrawableFloat.right = getWidth();
            }
            if (mDrawableFloat.bottom > getHeight()) {
                mDrawableFloat.top = getHeight() - mDrawableFloat.height();
                mDrawableFloat.bottom = getHeight();
            }
            mDrawableFloat.set(mDrawableFloat.left, mDrawableFloat.top, mDrawableFloat.right,
                    mDrawableFloat.bottom);
        } else {
            if (mDrawableFloat.left < 0) {
                mDrawableFloat.left = 0;
            }
            if (mDrawableFloat.top < 0) {
                mDrawableFloat.top = 0;
            }
            if (mDrawableFloat.right > getWidth()) {
                mDrawableFloat.right = getWidth();
                mDrawableFloat.left = getWidth() - mDrawableFloat.width();
            }
            if (mDrawableFloat.bottom > getHeight()) {
                mDrawableFloat.bottom = getHeight();
                mDrawableFloat.top = getHeight() - mDrawableFloat.height();
            }
            mDrawableFloat.set(mDrawableFloat.left, mDrawableFloat.top, mDrawableFloat.right,
                    mDrawableFloat.bottom);
        }

        mDrawable.setBounds(mDrawableDst);
        mFloatDrawable.setBounds(mDrawableFloat);
    }


    public int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
