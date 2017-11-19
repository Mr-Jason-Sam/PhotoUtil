package smart.photoutil;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.solver.widgets.Rectangle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import smart.photoutil.View.CropLayout;
import smart.photoutil.media.CmdRun;
import smart.photoutil.media.FFmpegCommand;
import smart.photoutil.media.VideoProduceConfig;
import smart.photoutil.util.AreaMove;
import smart.photoutil.View.ArrowHead;
import smart.photoutil.View.CropImageView;


/**
 * Created by jasonsam on 2017/10/30.
 */


public class TestActivity extends AppCompatActivity{

    private static final String FILE_ROOT = "/data/local/dev/";
    private static final int REC_REQUESTCODE = 0;
    public static final int VIDEO_DURATION = 60;
    private static final int VIDEO_SNAP_START = 0;
    private static final double VIDEO_SNAP_INTERVAL = 1;//0.5
    private static final int FPS = 25;

    public static final float TIME_FOR_TRANS = 1.0f;
    public static final double TIME_FOR_SEMI_TRANS = 0.5;
    public static final double TIME_FOR_MIX_SHOT = 1.5;
    public static final double KEY_FRAME_INTERVAL = 1.0;
    public static final long DELAY_FOR_MIX_BGM = 5000;//for wait connect video write complete, 3s in not enough

    private static final String TRANS_TYPE_HEAD = "head";
    private static final String TRANS_TYPE_TRAILER = "trailer";
    private static final String TRANS_TYPE_BODY = "body";
    private static final String TAG = "Smart";

    private ImageView preview;
    private ImageView snapshot1;
    private ImageView snapshot2;
    private ImageView snapshot3;
    private ImageView snapshot4;
//    private RelativeLayout rl;
    private List<ImageView> snapViewList = new ArrayList<ImageView>();
    private Map<ImageView, Bitmap> bmSnapList = new HashMap<ImageView, Bitmap>();
    private List<Bitmap> bitmapList = new ArrayList<Bitmap>();
    final List<File> snapList = new ArrayList<File>();
    private CmdRun cmd = new CmdRun();
    private VideoProduceConfig curVpc = new VideoProduceConfig();
    private Button btnLoad;
    private Button btnUndo;
    private Button btnSave;
    private Button btnReview;
    private Button btnConfirm;
    private Button btnReviewBat;
    private Button btnShotMove;
    private ImageButton btnCut;
    private VideoView vPreview;
    private ProgressDialog waitingDialog;
    public CropImageView beginCropImageView;
    public CropImageView endCropImageView;
//    private CropLayout cropLayout;
    private ArrowHead aHead;
    private int selectView = 1;

    private float touchX = 0;
    private float touchY = 0;

    private final int EDGE_MOVE_IN = 5;
    private final int EDGE_MOVE_OUT = 6;
    private final int EDGE_NONE = 7;

    public int beginCurrentEdge = EDGE_NONE;
    public int endCurrentEdge = EDGE_NONE;

    private float sectionStart;

//    View.OnClickListener onCropIvListener = new View.OnClickListener(){
//        public void onClick(View v) {


//    View.OnClickListener onCropIvListener = new View.OnClickListener(){
//        public void onClick(View v) {
//            Log.i("TAG","x: "+touchX+"y: "+touchY);
//            int[] location = new int[2];
//            v.getLocationOnScreen(location);
//            touchX = location[0];
//            touchY = location[1];
//            Log.i("TAG","x: "+touchX+"y: "+touchY);
//            beginCurrentEdge = beginCropImageView.getTouch((int) touchX, (int) touchY);
//                    Log.i("TAG",beginCurrentEdge+", beginCurrentEdge!!!!!!");
//            endCurrentEdge = endCropImageView.getTouch((int) touchX, (int) touchY);
//                    Log.i("TAG",endCurrentEdge+", endCropImageView:!!!!!!");
//
//
//            if (beginCurrentEdge == 5) {
//                v.post(new Runnable() {
//                    public void run() {
//                        endCropImageView.requestFocus();
//                        beginCropImageView.setStatus(7);
//                    }
//                });
//                if (endCurrentEdge == 5) {
////                            selectView = 2;
//                }else{
//                    v.post(new Runnable() {
//                        public void run() {
//
//                            beginCropImageView.requestFocus();
//                            endCropImageView.setStatus(7);
//                        }
//                    });
//                }
//                Log.i(TAG, "begin： "+beginCurrentEdge + "TestBegin5");
//                Log.i(TAG, "end： "+endCurrentEdge + "TestBegin5");
//            }
//            if (endCurrentEdge == 5) {
//                v.post(new Runnable() {
//                    public void run() {
//                        beginCropImageView.requestFocus();
//                        endCropImageView.setStatus(7);
//                    }
//                });
//                if (beginCurrentEdge == 5){
////                            selectView = 1;
//                }else{
//                    v.post(new Runnable() {
//                        public void run() {
//                            endCropImageView.requestFocus();
//                            beginCropImageView.setStatus(7);
//                        }
//                    });
//                }
//                Log.i(TAG, "begin： "+beginCurrentEdge + "TestEnd5");
//                Log.i(TAG, "end： "+endCurrentEdge + "TestEnd5");
//            }
//        }
//
//    };

    View.OnTouchListener onBeginCropListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    touchX = event.getX();
                    touchY = event.getY();
                    beginCurrentEdge = beginCropImageView.getTouch((int) touchX, (int) touchY);
                    Log.i("TAG",beginCurrentEdge+", beginCurrentEdge!!!!!!");
                    endCurrentEdge = endCropImageView.getTouch((int) touchX, (int) touchY);
                    Log.i("TAG",endCurrentEdge+", endCropImageView:!!!!!!");


                    if (beginCurrentEdge == EDGE_MOVE_IN || beginCurrentEdge == EDGE_MOVE_OUT ) {
                        v.post(new Runnable() {
                            public void run() {
                                endCropImageView.bringToFront();
                                beginCropImageView.setStatus(EDGE_NONE);
                            }
                        });
                        if (endCurrentEdge == EDGE_MOVE_IN) {
                        }else{
                            v.post(new Runnable() {
                                public void run() {
                                    beginCropImageView.bringToFront();
                                    endCropImageView.setStatus(EDGE_NONE);
                                }
                            });
                        }
                        Log.i(TAG, "begin： "+beginCurrentEdge + "TestBegin5");
                        Log.i(TAG, "end： "+endCurrentEdge + "TestBegin5");
                    }

                    Log.i(TAG, "Left： " + beginCropImageView.getFloatLeft() +
                                    " Top: " + beginCropImageView.getFloatTop() +
                                    " Width: " + beginCropImageView.getFloatRight() +
                                    " Height: " + beginCropImageView.getFloatBottom() +
                                    " Begin");
                    break;


                }
            }
            return false;
        }
    };

    View.OnTouchListener onEndCropListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    touchX = event.getX();
                    touchY = event.getY();
                    beginCurrentEdge = beginCropImageView.getTouch((int) touchX, (int) touchY);
                    Log.i("TAG",beginCurrentEdge+", beginCurrentEdge!!!!!!");
                    endCurrentEdge = endCropImageView.getTouch((int) touchX, (int) touchY);
                    Log.i("TAG",endCurrentEdge+", endCropImageView:!!!!!!");



                    if (endCurrentEdge == EDGE_MOVE_IN || endCurrentEdge == EDGE_MOVE_OUT) {
                        v.post(new Runnable() {
                            public void run() {
                                beginCropImageView.bringToFront();
                                endCropImageView.setStatus(EDGE_NONE);

                            }
                        });
                        if (beginCurrentEdge == EDGE_MOVE_IN){
//                            selectView = 1;
                        }else{
                            v.post(new Runnable() {
                                public void run() {
                                    endCropImageView.bringToFront();
                                    beginCropImageView.setStatus(EDGE_NONE);
                                }
                            });
                        }
                        beginCropImageView.callOnClick();
                        Log.i(TAG, "begin： "+beginCurrentEdge + "TestEnd5");
                        Log.i(TAG, "end： "+endCurrentEdge + "TestEnd5");
                    }

                    Log.i(TAG, "Left： " + endCropImageView.getFloatLeft() +
                                    " Top: " + endCropImageView.getFloatTop() +
                                    " Width: " + endCropImageView.getFloatRight() +
                                    " Height: " + endCropImageView.getFloatBottom() +
                                    " End");
                    break;


                }

            }
            return false;
        }
    };


    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_BUTTON_PRESS: {
                    int x = (int) event.getX();
                    int X = x + (int) v.getX();
                    float time = getIndex(X, VIDEO_DURATION);
                    if (sectionStart == 0) {
                        drawCutLine((ImageView) v, x, true);
                        sectionStart = time;
                    } else if (time - sectionStart > (TIME_FOR_TRANS * 2 + TIME_FOR_MIX_SHOT)) {
                        drawCutLine((ImageView)v, x, false);
                        Pair<Float, Float> section = new Pair<Float, Float>(sectionStart, time);
                        curVpc.getPartList().add(section);
                        sectionStart = 0;
                    } else {
                        Log.e(TAG, "onTouch, down, invalid second time.");
                    }
                }
                break;
                case MotionEvent.ACTION_MOVE: {
                    int x = (int) event.getX();
                    x += v.getX();
                    Log.i(TAG, "onTouch, move, x:" + x);
                    break;
                }

            }
            return true;
        }
    };

    View.OnHoverListener onHoverListener = new View.OnHoverListener() {
        @Override
        public boolean onHover(View v, MotionEvent event) {
            int what = event.getAction();
            switch (what) {
                case MotionEvent.ACTION_HOVER_ENTER: //鼠标进入view
                    Log.i(TAG, "onHover ACTION_HOVER_ENTER");
                    break;
                case MotionEvent.ACTION_HOVER_MOVE: //鼠标在view上
                    int x = (int) event.getX();
                    x += v.getX();
                    int index = (int) getIndex(x, snapList.size());//(int)(VIDEO_DURATION/KEY_FRAME_INTERVAL)
                    Log.i(TAG, "onHover, move, x:" + x + ", index:" + index);
                    if (index < snapList.size())
                        preview.setImageURI(Uri.fromFile(snapList.get(index)));
                    break;
                case MotionEvent.ACTION_HOVER_EXIT: //鼠标离开view
                    Log.i(TAG, "onHover ACTION_HOVER_EXIT");
                    break;
            }
            return false;
        }
    };

    private float getIndex(int x, int size) {
        //Log.i(TAG, "getIndex, x:"+x+", dur:"+duration);
        float xFirst = snapViewList.get(0).getX();
        float xLast = snapViewList.get(snapViewList.size() - 1).getX();
        float left = xFirst;
        float right = xLast + snapViewList.get(snapViewList.size() - 1).getMeasuredWidth();
        float scale = (x - left) / (right - left);
        if (scale < 0)
            scale = 0;
        if (scale > 1)
            scale = 1;
        float time = scale * size;
        Log.i(TAG, "getIndex, xFirst:" + xFirst + ", xLast:" + xLast + ", left:" + left + ", right:" + right
                + ", scale:" + scale + ", time:" + time);
        return Math.round(time);
    }

    private void drawCutLine(final ImageView iv, final int x, final boolean color) {
        final Bitmap bitmap = bmSnapList.get(iv);
        if (bitmap == null) {
            Log.i(TAG, "drawCutLine, bitmap is null.");
            return;
        }
        runOnUiThread(new Runnable() {
            public void run() {
                // 绘原图
                final Bitmap newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                Canvas canvas = new Canvas(newBitmap);
                Paint paint = new Paint();
                if (color)
                    paint.setColor(Color.BLUE);
                else
                    paint.setColor(Color.RED);
                paint.setStrokeWidth((float) 4.0);              //线宽
                Log.i(TAG, "drawCutLine, x:" + x + ", top:" + iv.getTop() + ", bottom:" + iv.getBottom());
                Log.i(TAG, "drawCutLine, x:" + x + ", top:" + iv.getHeight());
                canvas.drawLine(x, 0, x, iv.getHeight(), paint);
                canvas.save(Canvas.ALL_SAVE_FLAG);
                canvas.restore();
                iv.post(new Runnable() {
                    public void run() {
                        Log.i(TAG, "drawCutLine, newBitmap:" + newBitmap);
                        bmSnapList.put(iv, newBitmap);
                        iv.setImageBitmap(newBitmap);
                    }
                });
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video_produce);
        initView();
        initData();
    }

    private void showWaitingDialog(String message) {
        waitingDialog.setTitle("等待完成");
        waitingDialog.setMessage(message);
        waitingDialog.setIndeterminate(true);
        waitingDialog.setCancelable(false);
        waitingDialog.show();
    }

    private void initView() {
        preview = (ImageView) this.findViewById(R.id.framePreview);
//        rl  = (RelativeLayout) findViewById(R.id.corpArea);
        snapshot1 = (ImageView) findViewById(R.id.snapshot1);
        snapshot2 = (ImageView) findViewById(R.id.snapshot2);
        snapshot3 = (ImageView) findViewById(R.id.snapshot3);
        snapshot4 = (ImageView) findViewById(R.id.snapshot4);
        snapViewList.add(snapshot1);
        snapViewList.add(snapshot2);
        snapViewList.add(snapshot3);
        snapViewList.add(snapshot4);
        btnCut = (ImageButton) findViewById(R.id.btnCut);
        btnLoad = (Button) findViewById(R.id.load);
        btnUndo = (Button) findViewById(R.id.undo);
        btnSave = (Button) findViewById(R.id.save);
        btnReview = (Button) findViewById(R.id.review);
        btnConfirm = (Button) findViewById(R.id.confirm);
        btnReviewBat = (Button) findViewById(R.id.review_bat);
        btnShotMove = (Button) findViewById(R.id.shot_move);
        vPreview = (VideoView) findViewById(R.id.sectionReview);
        beginCropImageView = (CropImageView)findViewById(R.id.beginCropimage);
        endCropImageView = (CropImageView)findViewById(R.id.endCropimage);
        aHead = (ArrowHead)findViewById(R.id.ArrowHeadSurface);

//        cropLayout = (CropLayout)findViewById(R.id.cropArea);

//        Log.i(TAG, "crop: " + cropLayout + " this: " + this);
//        rl.removeAllViews();
//        rl.addView(beginCropImageView);
//        rl.addView(endCropImageView);

//        rl.setOnTouchListener(onTouchListener);


        for (View iv : snapViewList) {
            iv.setOnTouchListener(onTouchListener);
            iv.setOnHoverListener(onHoverListener);
        }

//        beginCropImageView.setOnTouchListener(onBeginCropListener);
//        endCropImageView.setOnTouchListener(onEndCropListener);

//        setContentView(aHead);

//        beginCropImageView.setOnClickListener(onCropIvListener);
//        endCropImageView.setOnClickListener(onCropIvListener);
    }


    private void initData() {

        btnCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

            }
        });

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                FFmpegCommand fFmpegCmd = new FFmpegCommand();
                File photo = new File(FILE_ROOT+"test");

                if (!photo.exists()) {
                    photo.mkdirs();
                    List<String> command = fFmpegCmd.getKeyFrameInfoCmd(FILE_ROOT+"test.mp4", FILE_ROOT+"test/", "%d", ".jpeg");
//                    String[] command = burst.split(" ");
                    for (String list : command)
                        Log.i("TAG", list);
                    cmd.processFFmpegCmd(command);
                }
                File lastPhoto = new File(FILE_ROOT+"test/61.jpeg");
                while (!lastPhoto.exists()) {

                }

                snapList.clear();
                File[] files = photo.listFiles();
                for (File file : files) {
                    String fileName[] = file.getName().split("\\.");
                    int count = Integer.parseInt(fileName[0]);
                    if (count % 15 == 0 || count == 1) {
                        Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
                        bitmapList.add(bm);
                    }
                    snapList.add(file);
                }

                runOnUiThread(new Runnable() {
                    public void run() {
                        int i = 0;
                        for (ImageView iv : snapViewList) {
                            File image = snapList.get(i);
                            Uri uri = Uri.fromFile(image);
                            iv.setImageURI(uri);
                            bmSnapList.put(iv, loadBitmapFromViewBySystem(iv));
                            i++;
                        }
                    }
                });


                snapshot1.setImageBitmap(bitmapList.get(0));
                snapshot2.setImageBitmap(bitmapList.get(1));
                snapshot3.setImageBitmap(bitmapList.get(2));
                snapshot4.setImageBitmap(bitmapList.get(3));
                preview.setImageBitmap(bitmapList.get(0));

            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vPreview.setVisibility(View.VISIBLE);
                preview.setVisibility(View.INVISIBLE);
                vPreview.setVideoURI((Uri.parse(FILE_ROOT + "video/" + "0.mp4")));
                vPreview.requestFocus();
                vPreview.start();
            }

        });

        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FFmpegCommand fFmpegCmd = new FFmpegCommand();
                int i = 0;
                File video = new File(FILE_ROOT + "video");
                if (!video.exists())
                    video.mkdirs();
                for (Pair<Float, Float> sectionTime:curVpc.getPartList()) {
                    List<String> command = FFmpegCommand.getCutCmd(FILE_ROOT+"test.mp4", curVpc.getPartList().get(i), FILE_ROOT+"video/", i+".mp4");
                    cmd.processFFmpegCmd(command);
                    i++;
                }
                try {
                    Thread.sleep(1000);
                }catch (Exception x){

                }
                vPreview.setVisibility(View.VISIBLE);
                preview.setVisibility(View.INVISIBLE);
                vPreview.setVideoURI((Uri.parse(FILE_ROOT+"video/"+"0.mp4")));
                vPreview.requestFocus();
                vPreview.start();
            }
        });

        btnCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preview.setVisibility(View.VISIBLE);

                beginCropImageView.setDrawable(null, 1152, 648);
                endCropImageView.setDrawable(null, 576, 324);

                beginCropImageView.setVisibility(View.VISIBLE);
                endCropImageView.setVisibility(View.VISIBLE);

                beginCropImageView.setDescribe("开始");
                endCropImageView.setDescribe("结束");

//                drawArrowHead();
//                selectCropImage(v);
            }
        });

        btnShotMove.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int beginTop, beginLeft, beginRight, beginBottom;
                int endTop, endLeft, endRight, endBottom;
                float scale = (float) 5/3;

                beginTop = (int)(beginCropImageView.getFloatTop() * scale);
                beginLeft = (int)(beginCropImageView.getFloatLeft() * scale);
                beginRight = (int)(beginCropImageView.getFloatRight() * scale);
                beginBottom = (int)(beginCropImageView.getFloatBottom() * scale);

                endTop = (int)(endCropImageView.getFloatTop() * scale);
                endLeft = (int)(endCropImageView.getFloatLeft() * scale);
                endRight = (int)(endCropImageView.getFloatRight() * scale);
                endBottom = (int)(endCropImageView.getFloatBottom() * scale);

                int beginW = beginRight - beginLeft;
                int beginH = beginBottom - beginTop;
                int endW   = endRight - endLeft;
                int endH   = endBottom - endTop;

                Rectangle begin,end;
                begin = new Rectangle();
                end   = new Rectangle();
                if (beginW > 1920)
                    beginW = 1920;
                if (endW > 1920)
                    endW = 1920;
                if (beginH > 1080)
                    beginH = 1080;
                if (endH > 1080)
                    endH = 1080;

                begin.setBounds(beginW - beginRight, beginH - beginBottom, beginW, beginH);
                end.setBounds(endW - endRight, endH - endBottom, endW, endH);

                Log.i(TAG, beginTop+":"+beginLeft+":"+beginRight+":"+beginBottom);
                Log.i(TAG, endTop+":"+endLeft+":"+endRight+":"+endBottom);

                File root = new File(FILE_ROOT+"12-22");
                File[] files = root.listFiles();
                int frames = files.length;
                AreaMove op = new AreaMove(begin, end, beginTop, beginLeft, endTop, endLeft, frames);

                for(File file:files) {
//            Log.i("TAG",rootResource + "origin/" + file.getName());
                    String fileName[] = file.getName().split("\\.");
                    int count = Integer.parseInt(fileName[0]);
                    Log.i("TAG", count + "");
                    op.imageCut(FILE_ROOT + "12-22/" + file.getName(), FILE_ROOT + "out12-22/" + file.getName(), count);
                }

            }
        });

    }

//    private boolean isTouchPointInView(View view, int x, int y) {
//        if (view == null) {
//            return false;
//        }
//        int[] location = new int[2];
//        view.getLocationOnScreen(location);
//        int left = location[0];
//        int top = location[1];
//        int right = left + view.getMeasuredWidth();
//        int bottom = top + view.getMeasuredHeight();
//        Log.i(TAG, "left: " + left + " top: " + top + " right: " + right + " bottom: " + bottom);
//        //view.isClickable() &&
////        if (y >= top && y <= bottom && x >= left
////                && x <= right) {
////            return true;
////        }
//        if (x >= left && y <= top && x <= right && y >= bottom){
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        int x = (int) ev.getRawX();
//        int y = (int) ev.getRawY();
//        if (isTouchPointInView(beginCropImageView, x, y) && isTouchPointInView(endCropImageView, x, y) ) {
//            return beginCropImageView.dispatchTouchEvent(ev);
//        }else if (isTouchPointInView(endCropImageView, x, y))
//            return endCropImageView.dispatchTouchEvent(ev);
//
//        //do something
//        return false;
//    }
//
//    private View getTouchTarget(View view, int x, int y) {
//        View targetView = null;
//        // 判断view是否可以聚焦
//        ArrayList<View> TouchableViews = view.getTouchables();
//        for (View child : TouchableViews) {
//            if (isTouchPointInView(child, x, y)) {
//                targetView = child;
//                break;
//            }
//        }
//        return targetView;
//    }



    public void draws(float x, float y){

    }

    public void selectCropImage(final View v){
        runOnUiThread(new Runnable() {
            public void run() {
//                Log.i("TAG", "x: " + touchX + "y: " + touchY);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                touchX = location[0];
                touchY = location[1];
                Log.i("TAG", "x: " + touchX + "y: " + touchY);
                beginCurrentEdge = beginCropImageView.getTouch((int) touchX, (int) touchY);
                Log.i("TAG", beginCurrentEdge + ", beginCurrentEdge!!!!!!");
                endCurrentEdge = endCropImageView.getTouch((int) touchX, (int) touchY);
                Log.i("TAG", endCurrentEdge + ", endCropImageView:!!!!!!");


                if (beginCurrentEdge == 5) {
                    v.post(new Runnable() {
                        public void run() {
                            endCropImageView.bringToFront();
                            beginCropImageView.setStatus(7);
                        }
                    });
                    if (endCurrentEdge == 5) {
//                            selectView = 2;
                    } else {
                        v.post(new Runnable() {
                            public void run() {
                                beginCropImageView.bringToFront();
                                endCropImageView.setStatus(7);
                            }
                        });
                    }
                    Log.i(TAG, "begin： " + beginCurrentEdge + "TestBegin5");
                    Log.i(TAG, "end： " + endCurrentEdge + "TestBegin5");
                }
                if (endCurrentEdge == 5) {
                    v.post(new Runnable() {
                        public void run() {
                            beginCropImageView.bringToFront();
                            endCropImageView.setStatus(7);
                        }
                    });
                    if (beginCurrentEdge == 5) {
//                            selectView = 1;
                    } else {
                        v.post(new Runnable() {
                            public void run() {
                                endCropImageView.bringToFront();
                                beginCropImageView.setStatus(7);
                            }
                        });
                    }
                    Log.i(TAG, "begin： " + beginCurrentEdge + "TestEnd5");
                    Log.i(TAG, "end： " + endCurrentEdge + "TestEnd5");
                }
            }
        });
    }
//

    private static Bitmap loadBitmapFromViewBySystem(View v) {
        if (v == null) {
            return null;
        }
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        Bitmap bitmap = v.getDrawingCache();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });
        return bitmap;
    }

}
