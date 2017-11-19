package smart.photoutil.View;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import smart.photoutil.R;

/**
 * Created by jasonsam on 2017/11/8.
 */

public class CropLayout extends RelativeLayout {
    private String TAG = "CropLayout";
    private CropImageView beginCropView;
    private CropImageView endCropView;
    private ArrowHead head;
    private Context mContext;
    private static boolean flagMove;

//    private Button btCut;

    private final int SINGLE_IN = 1;
    private final int TOGETHER_IN = 2;
    private final int EDGE_NONE = 3;
    private final int ANGLE = 4;

    private final int EDGE_LT = 1;
    private final int EDGE_RT = 2;
    private final int EDGE_LB = 3;
    private final int EDGE_RB = 4;
    private final int EDGE_IN = 5;

    public int selectEdge;


    public CropLayout(Context context) {
        this(context,null);
    }

    public CropLayout(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public CropLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.crop_layout,this);
        beginCropView =  findViewById (R.id.beginCropimage);
        endCropView = findViewById(R.id.endCropimage);
        head = findViewById(R.id.ArrowHeadSurface);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }

    @Override
    public boolean dispatchTouchEvent (MotionEvent event){
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event){
        //画箭头
        drawArroeHead();

        float x = event.getX();
        float y = event.getY();
        int beginCurrentEdge = beginCropView.getTouch((int) x, (int) y);
        int endCurrentEdge = endCropView.getTouch((int) x, (int) y);

        setSelectEdge(beginCurrentEdge, endCurrentEdge);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                flagMove = false;
                switch (selectEdge){
                    case ANGLE:
                        return super.onInterceptTouchEvent(event);
                    case SINGLE_IN:
                        if (beginCurrentEdge == EDGE_IN) {
                            actionInBegin();
                        }else {
                            actionInEnd();
                        }
                        break;
                }


                break;
            case MotionEvent.ACTION_UP:
                switch (selectEdge) {
                    case ANGLE:
                        return super.onInterceptTouchEvent(event);
                    case TOGETHER_IN:
                        if (!flagMove){
                            if(beginCropView.isEnabled()){
                                actionInEnd();
                            }else if(endCropView.isEnabled()){
                                actionInBegin();
                            }
                        }

                        return true;
                    }
                    break;
            case MotionEvent.ACTION_MOVE:
                flagMove = true;
                break;

        }
        return super.onInterceptTouchEvent(event);
    }

    private void actionInBegin(){
        beginCropView.bringToFront();
        head.bringToFront();
        endCropView.setEnabled(false);
        endCropView.setSelect(false);
        beginCropView.setSelect(true);
        beginCropView.setEnabled(true);
        beginCropView.requestFocus();
    }

    private void actionInEnd() {
        endCropView.bringToFront();
        head.bringToFront();
        beginCropView.setEnabled(false);
        beginCropView.setSelect(false);
        endCropView.setSelect(true);
        endCropView.setEnabled(true);
        endCropView.requestFocus();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){return super.onTouchEvent(event);}

    public void drawArroeHead(){
        int beginTop = beginCropView.getFloatTop();
        int beginLeft = beginCropView.getFloatLeft();
        int beginRight = beginCropView.getFloatRight();
        int beginBottom = beginCropView.getFloatBottom();
        int endTop = endCropView.getFloatTop();
        int endLeft = endCropView.getFloatLeft();
        int endRight = endCropView.getFloatRight();
        int endBottom = endCropView.getFloatBottom();
        int beginCentreX = beginLeft + (beginRight - beginLeft)/2;
        int beginCentreY = beginTop + (beginBottom - beginTop)/2;
        int endCentreX = endLeft + (endRight - endLeft)/2;
        int endCentreY = endTop + (endBottom - endTop)/2;
        head.initData(beginCentreX, beginCentreY, endCentreX, endCentreY);
        head.setVisibility(View.VISIBLE);
    }

    private void setSelectEdge(int beginEdge, int encEdge){
        if (beginEdge == EDGE_IN && encEdge == EDGE_IN)
            selectEdge = TOGETHER_IN;
        else if (beginEdge == EDGE_LT || beginEdge == EDGE_RT ||
                beginEdge == EDGE_LB || beginEdge == EDGE_RB ||
                encEdge == EDGE_LT || encEdge == EDGE_RT ||
                encEdge ==EDGE_LB || encEdge == EDGE_RB)
            selectEdge = ANGLE;
        else if (beginEdge == EDGE_IN || encEdge == EDGE_IN)
            selectEdge = SINGLE_IN;
        else
            selectEdge = EDGE_NONE;
    }

}
