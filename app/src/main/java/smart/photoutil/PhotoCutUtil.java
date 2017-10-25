package smart.photoutil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

/**
 * Created by jasonsam on 2017/10/9.
 */

public class PhotoCutUtil {
    public static final int STARTLOCATION          = 0;
    public static final int GRIDROW                = 4;
    public static final int GRIDCOL                = 6;
    public static final int FRAMES                 = 25;
    public static final int GRIDNUM                = GRIDCOL*GRIDROW;
    public static final int GRIDFRAMES             = 5;
    public static final int FILPTIMES              = FRAMES-GRIDFRAMES*2;
    public static final int TURNFRAMES             = (FILPTIMES+1)/2;


    public enum  Transition { Wipe, Dim, Fade, Blur, Puzzle, Grid, Flip, GridFilp;}

    public List<int[]> list = new LinkedList<>();

    public void cutImage(String srcPath, String desPath, int num,Transition transition){

        switch (transition){
            case Wipe:
                wipeImage(srcPath, desPath, num);
                break;
            case Dim:
                dimImage(srcPath, desPath, num);
                break;
            case Fade:
                fadeImage(srcPath, "", desPath, num);
                break;
            case Blur:
                blurImage(srcPath, desPath, num);
                break;
            case Puzzle:
                puzzleImage(srcPath, desPath, num);
                break;
            case Flip:
//                flipImage(srcPath, desPath, num);
                break;
            case Grid:
//                gridImage(srcPath, desPath, num);
                break;
            case GridFilp:
//                gridFilpImage(srcPath, desPath, num);
            default:
        }
    }

    public void wipeImage(String srcPath, String desPath,int num){
        Bitmap bm = BitmapFactory.decodeFile(srcPath);
        Bitmap image = Bitmap.createBitmap(bm.getWidth(),bm.getHeight(), Bitmap.Config.ARGB_8888);
//        image.
        Canvas cas = new Canvas(image);

        cas.drawBitmap(bm,0,0,null);
//        Bitmap
        try {
            int imgH = image.getHeight(),imgW = image.getWidth();

            switch(num/9){
                case 0:
                    for(int y = STARTLOCATION; y  < imgH; y++)
                        for(int x = STARTLOCATION; x < imgW; x++)
                            if(x>(imgW/3/9)*(num%9))
                                image.setPixel(x, y, Color.BLACK);
                case 1:
                    for(int y = STARTLOCATION; y  < imgH; y++)
                        for(int x = imgW/3; x < imgW; x++)
                            if(y<imgH-imgH/9*(num%9)||x>imgW/3*2)
                                image.setPixel(x, y, Color.BLACK);
                case 2:
                    for(int y = STARTLOCATION; y  < imgH; y++)
                        for(int x = imgW; x > imgW/3*2; x--)
                            if(x<imgW-(imgW/3/9)*(num%9))
                                image.setPixel(x, y, Color.BLACK);
            }

            cas.save();
            cas.restore();
            produceImage(image,desPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void puzzleImage(String srcPath, String desPath,int num){
//        Bitmap bm = BitmapFactory.decodeFile(srcPath);
        Bitmap image = BitmapFactory.decodeFile(srcPath);
//        Canvas cas = new Canvas(image);
        try {
            int imgH = image.getHeight(), imgW = image.getWidth();
            Bitmap imageNew = Bitmap.createBitmap(imgW,imgH, Bitmap.Config.ARGB_8888);

            int[] RGB;
            int[] RGB1 = getPhotoRGB(cutImage(image,0,0,imgW/25*8,imgH));
            int[] RGB2 = getPhotoRGB(cutImage(image,imgW/25*8,0,imgW/25*9,imgH));
            switch(num/9){
                case 0:
                    for(int y = STARTLOCATION; y  < imgH; y++)
                        for(int x = STARTLOCATION; x < imgW; x++) {
                            if(x>(imgW/25)*(num)){
                                imageNew.setPixel(x, y, Color.BLACK);
                            }
                        }

                    RGB = getPhotoRGB(cutImage(image,imgW/3-(imgW/25)*num,0,(imgW/25)*num,imgH));

                    imageNew.setPixels(RGB, 0, (imgW / 25) * (num), 0, 0, (imgW / 25) * (num), imgH);

                    break;
                case 1:
                    for(int y = STARTLOCATION; y  < imgH; y++)
                        for(int x = imgW/3; x < imgW; x++)
                            if(y<imgH-imgH/9*(num%9)||x>imgW/3*2)
                                imageNew.setPixel(x, y, Color.BLACK);

                    imageNew.setPixels(RGB1, 0, imgW/25*8, 0, 0,  imgW/25*8, imgH);
                    if(num != 17){
                        RGB = getPhotoRGB(cutImage(image,imgW/25*8,0,imgW/25*9,imgH/9*((num+1)%9)));
                        imageNew.setPixels(RGB, 0, imgW/25*9,imgW/25*8, imgH-imgH/9*((num+1)%9),imgW/25*9, imgH/9*((num+1)%9));
                    }else{
                        imageNew.setPixels(RGB2, 0, imgW/25*9, imgW/25*8, 0, imgW/25*9, imgH);
                    }

                    break;
                case 2:
                    for(int y = STARTLOCATION; y  < imgH; y++)
                        for(int x = imgW; x > imgW/3*2; x--)
                            if(x<imgW-(imgW/25)*(num%9))
                                imageNew.setPixel(x, y, Color.BLACK);


                    if(num != 25){
                        imageNew.setPixels(RGB1, 0, imgW/25*8, 0, 0, imgW/25*8, imgH);
                        imageNew.setPixels(RGB2, 0, imgW/25*9, imgW/25*8, 0, imgW/25*9, imgH);
                        RGB = getPhotoRGB(cutImage(image,imgW/25*17,0,(imgW/25)*((num+1)%9),imgH));
                        imageNew.setPixels(RGB, 0, (imgW/25)*((num+1)%9),imgW-(imgW/25)*((num+1)%9), 0, (imgW/25)*((num+1)%9), imgH);
                    }else{
                        imageNew = image;
                    }
//

                    break;
            }
            produceImage(imageNew,desPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dimImage(String srcPath, String desPath,int num){
        Bitmap bm = BitmapFactory.decodeFile(srcPath);
        bm = scaleBitmap(bm);
        Bitmap image = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas cas = new Canvas(image);
        try {
            int imgH = image.getHeight(), imgW = image.getWidth();

            for(int y = STARTLOCATION; y  < imgH; y++)
                for(int x = STARTLOCATION; x < imgW; x++){
                    image.setPixel(x, y, Color.BLACK);
                }

            float lum = 1.0f;
            ColorMatrix lumMatrix = new ColorMatrix();
            lumMatrix.setScale(lum/25*(25-num), lum/25*(25-num), lum/25*(25-num), 1.0f);
            ColorMatrix ImageMatrix = new ColorMatrix();
            ImageMatrix.postConcat(lumMatrix);

            Paint vPaint = new Paint();
            vPaint.setColorFilter((new ColorMatrixColorFilter(ImageMatrix)));

            cas.drawBitmap(bm,0, 0, vPaint);
            cas.save();
            cas.restore();

            produceImage(image, desPath);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void blurImage(String srcPath, String desPath,int num){
        GaussianBlurUtil blurUtil = new GaussianBlurUtil();
        Bitmap bm = BitmapFactory.decodeFile(srcPath);
//        bm = compressImage(bm);
        bm = scaleBitmap(bm);
        Bitmap image = Bitmap.createBitmap(bm.getWidth(),bm.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas cas = new Canvas(image);

        cas.drawBitmap(bm,0,0,null);

        try{
            image = blurUtil.blur(image,54-2*num);
            cas.save();
            cas.restore();
            produceImage(image,desPath);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void fadeImage(String inFadePath, String outFadePath, String desPath,int num){
        Bitmap bitmapIn = fadeInOutImage(inFadePath,num,"in");
        Bitmap bitmapOut = fadeInOutImage(outFadePath,num,"out");
        Bitmap mergeBitmap = bitmapOut.copy(Bitmap.Config.ARGB_8888, true);

        Canvas cas = new Canvas(mergeBitmap);


        Rect outRect  = new Rect(0, 0, bitmapOut.getWidth(), bitmapOut.getHeight());
        Rect inRect = new Rect(0, 0, bitmapIn.getWidth(), bitmapIn.getHeight());
        cas.drawBitmap(bitmapIn,inRect,outRect,null);
        produceImage(mergeBitmap, desPath);
    }

    public Bitmap fadeInOutImage(String srcPath,int num,String type){
        Bitmap bm = BitmapFactory.decodeFile(srcPath);
//        bm = scaleBitmap(bm);
        Bitmap image = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas cas = new Canvas(image);
        try {
            int imgH = image.getHeight(), imgW = image.getWidth();

            for(int y = STARTLOCATION; y  < imgH; y++)
                for(int x = STARTLOCATION; x < imgW; x++)
                    image.setPixel(x, y, Color.BLACK);


            float lum = 1.0f;
            ColorMatrix lumMatrix = new ColorMatrix();

            if (type == "in")
                lumMatrix.setScale(lum, lum, lum, 2.0f/25*num);
            else if (type == "out")
                lumMatrix.setScale(lum,lum, lum, 2.0f/25*(25-num));
            ColorMatrix ImageMatrix = new ColorMatrix();
            ImageMatrix.postConcat(lumMatrix);

            Paint vPaint = new Paint();
            vPaint.setColorFilter((new ColorMatrixColorFilter(ImageMatrix)));

            cas.drawBitmap(bm,0, 0, vPaint);
            cas.save();
            cas.restore();



//            produceImage(image, desPath);
        } catch (Exception e){
            e.printStackTrace();
        }
        return image;
    }



    /*
    srcPath  抽帧原始图片
    gridPath 网格处理图片
    desPath  图片生成地址
     */

    public void gridFilpImage(String srcPath, String gridPath, String desPath, int frame){
//        gridImage(srcPath, gridPath, 5, num);
//        filpAction(gridPath, desPath, num);
        GridFilpAction action = new GridFilpAction();
        action.gridFilpImage(srcPath, gridPath, desPath, frame);
    }


    private HashMap<Bitmap, Integer> initImage(){
        HashMap<Bitmap , Integer> frames = new HashMap<>();
//        ArrayList<Bitmap> frames = new ArrayList<Bitmap>();
        File root = new File("/sdcard/origin/");
        File[] files = root.listFiles();

        for(File file:files) {
            Bitmap source = BitmapFactory.decodeFile("/sdcard/origin/"+file.getName());
            String fileName[] = file.getName().split("\\.");
            int count = Integer.parseInt(fileName[0]);
            source = scaleBitmap(source);
            frames.put(source,count);
        }
        return frames;

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

    private static Bitmap cutImage(Bitmap bufferedImage,int x,int y,int width,int height){
        return bufferedImage.createBitmap(bufferedImage,x,y,width,height);
    }

    private static int[] getPhotoRGB(Bitmap bufferedImage){
        int[] imageArray = new int[bufferedImage.getHeight()*bufferedImage.getWidth()];
        bufferedImage.getPixels(imageArray,0,bufferedImage.getWidth(),0,0,bufferedImage.getWidth(), bufferedImage.getHeight());
        return imageArray;
    }

    private static Bitmap scaleBitmap(Bitmap bit){
        Matrix matrix = new Matrix();
        matrix.postScale(0.3f, 0.3f);
        Bitmap bmp = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(),bit.getHeight(), matrix, true);
        return bmp;
    }

    private static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 30, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 50;

        while (baos.toByteArray().length / 1024 > 500) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }


}
