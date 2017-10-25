package smart.photoutil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by jasonsam on 2017/10/21.
 */

public class GridFilpAction {
    public static final int STARTLOCATION          = 0;
    public static final int GRIDROW                = 4;
    public static final int GRIDCOL                = 6;
    public static final int FRAMES                 = 25;
    public static final int GRIDNUM                = GRIDCOL*GRIDROW;
    public static final int GRIDFRAMES             = 5;
    public static final int FILPTIMES              = FRAMES-GRIDFRAMES*2;
    public static final int TURNFRAMES             = (FILPTIMES+1)/2;

    public static List<int[]> list = new LinkedList<>();

    /*
    srcPath  抽帧原始图片
    gridPath 网格处理图片
    desPath  图片生成地址
     */

    public GridFilpAction(){
        ProduceRandom();
    }

    public void gridFilpImage(String srcPath, String gridPath, String desPath, int frame){
        gridImage(srcPath, gridPath, 5, frame);
        filpAction(gridPath, desPath, frame);
    }

    /*gridShadeFrames 网格渐变帧*/
    public void gridImage(String srcPath, String desPath, int gridShadeFrames, int frame){
        Bitmap bm = BitmapFactory.decodeFile(srcPath);
        Bitmap image = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
        int imgH = image.getHeight(), imgW = image.getWidth();

        Canvas cas = new Canvas(image);

        Paint vPaint = new Paint();

        vPaint.setAntiAlias(true);
        cas.drawBitmap(bm,0, 0, vPaint);

        vPaint.setStrokeWidth(3.5f);

        if (frame <= gridShadeFrames)
            vPaint.setAlpha(255/gridShadeFrames*frame);
        else if (frame > FRAMES - gridShadeFrames)
            vPaint.setAlpha(255/gridShadeFrames*(FRAMES - frame));

        for (int i = 1; i < GRIDCOL; i++) {
            cas.drawLine(imgW/GRIDCOL * i, 0, imgW/GRIDCOL * i, imgH, vPaint);
        }
        for (int i = 1; i < GRIDROW; i++) {
            cas.drawLine(0, imgH/GRIDROW * i, imgW, imgH/GRIDROW  * i, vPaint);
        }

        cas.save();
        cas.restore();
        produceImage(image,desPath);
    }

    /*distance 开始旋转帧与当前操作帧的距离，目的判断旋转的幅度*/
    public Bitmap flipOneImage(Bitmap source, int x, int y, int width, int height, int distance){
        Bitmap bm = Bitmap.createBitmap(source, x, y, width, height);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int imgH = image.getHeight(), imgW = image.getWidth();

        Canvas cas = new Canvas(image);
        Camera camera = new Camera();
        Matrix m = new Matrix();

        camera.save();
        camera.rotateX(0);

        switch (distance){
            case 0:
                camera.rotateY(5);
                break;
            case 1:
                camera.rotateY(40);
                break;
            case 2:
                camera.rotateY(80);
                break;
            case 3:
                camera.rotateY(65);
                break;
            case 4:
                camera.rotateY(40);
                break;
            case 5:
                camera.rotateY(20);
                break;
            case 6:
                camera.rotateY(10);
                break;
            case 7:
                camera.rotateY(5);
            default:
                camera.rotateY(0);
        }

        camera.rotateZ(0);
        camera.getMatrix(m);
        camera.restore();

        m.preTranslate(-imgW/2,-imgH/2);
        m.postTranslate(imgW/2,imgH/2);

        cas.drawBitmap(bm,m,null);

        cas.save();
        cas.restore();

        return image;
    }

    public void filpAction(String srcPath, String desPath, int frame){

        Bitmap bm = BitmapFactory.decodeFile(srcPath);
        int imgH = bm.getHeight(), imgW = bm.getWidth();
        int distnace = frame - GRIDFRAMES;
        int begin, end;

        Bitmap image = Bitmap.createBitmap(imgW, imgH, Bitmap.Config.ARGB_8888);
        Canvas cas = new Canvas(image);

        cas.drawBitmap(bm, 0, 0, null);

        Paint vPaint = new Paint();
        vPaint.setColor(Color.BLACK);

        Log.i("TAG",list.get(2)[2]+"Test");

        if (frame > GRIDFRAMES && frame <= FRAMES-GRIDFRAMES){
            if (distnace <= TURNFRAMES) {
                begin = 0;
                end   = distnace;
            }else{
                begin = distnace-TURNFRAMES-1;
                end   = TURNFRAMES;
            }
            for (int i = begin; i < end; i++){
                int [] group = list.get(i);
                for (int j = 0; j < group.length ; j++){
                    int blockSn = group[j];

                    int Sx , Sy ;
                    Sx = (blockSn-1)%GRIDCOL;
                    Sy = (blockSn-1)/GRIDCOL;
                    cas.drawRect(Sx * imgW / GRIDCOL, Sy * imgH / GRIDROW, (Sx + 1) * imgW / GRIDCOL, (Sy + 1) * imgH / GRIDROW, vPaint);
                    cas.drawBitmap(flipOneImage(bm, Sx * imgW / GRIDCOL, Sy * imgH / GRIDROW, imgW / GRIDCOL, imgH / GRIDROW, distnace-i-1),
                            Sx * imgW / GRIDCOL,
                            Sy * imgH / GRIDROW,
                            vPaint);

                }
            }
        }

        cas.save();
        cas.restore();
        produceImage(image,desPath);

    }

    public static int[] GetRandomSn() {
        int originId[] = new int[GRIDNUM];
        int targetId[] = new int[GRIDNUM];
        for (int i = 0; i < GRIDNUM; i++) {
            originId[i] = i + 1;
        }
        int last = GRIDNUM-1;
        Random r = new Random();
        int temp;
        for (int i = 0; i < GRIDNUM-1; i++) {
            temp = Math.abs(r.nextInt() % last);
            targetId[i] = originId[temp];
            originId[temp] = originId[last];
            originId[last] = targetId[i];
            last--;
        }
        targetId[GRIDNUM-1] = originId[0];
        return  targetId;
    }

    private static int[] produceSequence(){
        int []sequence = {2,2,3,5,5,3,2,2};
        return sequence;
    }

    public static void ProduceRandom(){
        int [] originBlock = GetRandomSn();

        int sum = 0;
        for (int i = 0; i < TURNFRAMES; i++){
            int []sequence = produceSequence();
            int y = sequence[i];
            int [] targetBlock = new int[y];
            for (int j = sum; j < sum+y; j++)
                targetBlock[j-sum] = originBlock[j];

            sum += y;
            list.add(targetBlock);
        }
    }

    private boolean produceImage(Bitmap bm,String desPath) {
        File file = new File(desPath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 50, fos);

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
