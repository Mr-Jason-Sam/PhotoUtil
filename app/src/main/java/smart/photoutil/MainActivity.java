package smart.photoutil;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;

import smart.photoutil.View.ArrowHead;
import smart.photoutil.util.PhotoCutUtil;


public class MainActivity extends AppCompatActivity {
    public static final String rootResource = "/data/local/testFFmpeg/";
    private ArrowHead aHead;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        aHead = (ArrowHead)findViewById(R.id.ArrowHeadSurface);
//
//        aHead.jiantou(0,0,200,300);

        File root = new File(rootResource+"start/");
        File[] files = root.listFiles();
        PhotoCutUtil op = new PhotoCutUtil();

        for(File file:files) {
            Log.i("TAG",rootResource + "start/" + file.getName());
            String fileName[] = file.getName().split("\\.");
            int count = Integer.parseInt(fileName[0]);
            Log.i("TAG",count+"");
//
////            op.blurImage(rootResource+"origin/"+file.getName(),rootResource+"test/blur/"+file.getName(),count);
////            op.dimImage(rootResource+"origin/"+file.getName(),rootResource+"test/dim/"+file.getName(),count);
////            op.wipeImage(rootResource+"origin/"+file.getName(),rootResource+"test/wipe/"+file.getName(),count);
////            op.puzzleImage(rootResource+"origin/"+file.getName(),rootResource+"test/puzzle/"+file.getName(),count);
            op.fadeImage(rootResource+"start/"+file.getName(), rootResource+"end/"+file.getName(),rootResource+"out/"+count+".png",count);
////            op.fadeTestImage(rootResource+"origin/"+file.getName(),rootResource+"test/fadeTest/"+file.getName(),count);
////            op.fadeInOutImage(rootResource+"origin/"+file.getName(),)
////            op.gridFilpImage(rootResource + "origin/" + file.getName(), rootResource + "test/grid/" + file.getName(), rootResource + "test/gridFilp/" + file.getName(), count);
        }

//        File root = new File(rootResource+"test/move/");
//        File[] files = root.listFiles();
//
//        Rectangle begin,end;
//        begin = new Rectangle();
//        end   = new Rectangle();
//        begin.setBounds(0, 0, 1920, 1080);
//        end.setBounds(0, 540, 960, 540);
//
////        begin.setBounds(0, 0, 960, 540);
////        end.setBounds(540, 0, 960, 540);
//
//        AreaMove op = new AreaMove(begin, end, 0, 0, 0, 540);
//
//        for(File file:files) {
////            Log.i("TAG",rootResource + "origin/" + file.getName());
//            String fileName[] = file.getName().split("\\.");
//            int count = Integer.parseInt(fileName[0]);
//            Log.i("TAG",count+"");
//            op.imageCut(rootResource+"test/move/"+file.getName(), rootResource+"test/out/"+file.getName(), count);
//        }

    }

}
