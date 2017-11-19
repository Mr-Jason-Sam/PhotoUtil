package smart.photoutil.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.constraint.solver.widgets.Rectangle;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import static java.lang.Math.abs;
import static java.lang.Math.cos;

/**
 * Created by jasonsam on 2017/10/26.
 */

public class AreaMove {
    private static final String TAG = "AreaMove";
    private Rectangle mRectBegin, mRectEnd, newRect;
    private int mBeginTop, mBeginLeft;
    private int mEndTop, mEndLeft;
    private int newTop, newLeft, newW, newH;
    private float changeTop, changeLeft, changeW, changeH;
    private float originChangeTop, originChangeLeft, originChangeW, originChangeH;
    private int mFrames;
    private int[][] framesGroup = new int[10][];

    //S = vt + 0.5a*t*t
    //v = v + at

    private double[] v;
    private double xA;
    private double yA;
    private double wA;
    private double hA;

    private int oldTop;
    private int oldLeft;
    private int oldW;
    private int oldH;



    public AreaMove(Rectangle rectBegin, Rectangle rectEnd, int beginTop ,int beginLeft ,int endTop, int endLeft, int frames){
        mRectBegin    = rectBegin;
        mRectEnd      = rectEnd;
        mBeginTop = beginTop;
        mBeginLeft = beginLeft;
        mEndTop   = endTop;
        mEndLeft   = endLeft;
        initChangeData(frames);
    }

    public void initChangeData(int frames){
        mFrames = frames;

//        originChangeTop = (mEndTop - mBeginTop)/mFrames;
//        originChangeLeft = (mEndLeft - mBeginLeft)/mFrames;
//        originChangeW = (mRectEnd.width - mRectBegin.width)/mFrames;
//        originChangeH = (mRectEnd.height - mRectBegin.height)/mFrames;


        //s = 1/2*a(frames/4)*(frames/4) + (frames/4)*a*(frames/2) + (frames/4)*a*(frames/4) - 1/2*a(frames/4)*(frames/4);
        xA = (double)((mEndTop - mBeginTop)*16)/(3.0*mFrames*mFrames);
        yA = (double)((mEndLeft - mBeginLeft)*16)/(3.0*mFrames*mFrames);
        wA = (double)((mRectEnd.width - mRectBegin.width)*16)/(3.0*mFrames*mFrames);
        hA = (double)((mRectEnd.height - mRectBegin.height)*16)/(3.0*mFrames*mFrames);

        Log.i(TAG,"speed: " + xA + ":" + yA + ":" + wA + ":" + hA );
//        setFramesGroup();

    }

    private void changeSpeed(int frame){

        int lastQuarter = mFrames*3/4, beforeQuarter = mFrames*1/4;
        int midFrame = frame - beforeQuarter, lastFrame = frame - lastQuarter;

        if (frame <= beforeQuarter) {
            newTop = mBeginTop + (int)(0.5 * xA * frame *frame);
            newLeft = mBeginLeft + (int)(0.5 * yA * frame *frame);
            newW = mRectBegin.width + (int)(0.5 * wA * frame *frame);
            newH = mRectBegin.height + (int)(0.5 * hA * frame *frame);
        }else if(frame > lastQuarter){
            newTop = oldTop  + (int) (beforeQuarter * xA * lastFrame) - (int)(0.5 * xA * lastFrame *lastFrame);
            newLeft = oldLeft + (int) (beforeQuarter * yA * lastFrame) - (int)(0.5 * yA * lastFrame *lastFrame);
            newW = oldW + (int) (beforeQuarter * wA * lastFrame) - (int)(0.5 * wA * lastFrame *lastFrame);
            newH = oldH + (int) (beforeQuarter * hA * lastFrame) - (int)(0.5 * hA * lastFrame *lastFrame);
        }else {
            newTop = oldTop + (int) (beforeQuarter * xA * midFrame);
            newLeft = oldLeft + (int) (beforeQuarter * yA * midFrame);
            newW = oldW + (int) (beforeQuarter * wA * midFrame);
            newH = oldH + (int) (beforeQuarter * hA * midFrame);
        }

        if (frame == beforeQuarter || frame == lastQuarter) {
            oldTop  = newTop;
            oldLeft = newLeft;
            oldW    = newW;
            oldH    = newH;
        }

        Log.i(TAG,"newRect: " + newTop + ":" + newLeft + ":" + newW + ":" + newH);

    }

    public void imageCut(String srcPath, String desPath, int frame){
        Bitmap bm = BitmapFactory.decodeFile(srcPath);
        Bitmap image = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
        int imgH = image.getHeight(), imgW = image.getWidth();
        Matrix matrix = new Matrix();

        Canvas cas = new Canvas(image);

        changeSpeed(frame);

        matrix.postScale((float) imgW/newW,(float)imgH/newH);

        if (newLeft + newW > 1920)
            newW = 1920 - newLeft;
        if (newTop + newH > 1080)
            newH = 1080 - newTop;
        if (newLeft <= 0 || newW <= 0 || newTop <= 0 || newH <= 0)
            return;

        Bitmap newBmp = Bitmap.createBitmap(bm, newLeft, newTop, newW, newH, matrix, true);
        cas.drawBitmap(newBmp,0, 0, null);
        cas.save();
        cas.restore();

        CreateBmp bmpFile = new CreateBmp();
        bmpFile.saveBitmapToBmp(image, desPath);
    }

    private boolean produceImage(Bitmap bm, String desPath) {
        File file = new File(desPath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, fos);

//            fos.write(bm.getRowBytes());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        try{
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
