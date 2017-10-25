package smart.photoutil;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;


public class MainActivity extends AppCompatActivity {
    public static final String rootResource = "/sdcard/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        File root = new File(rootResource+"origin/");
        File[] files = root.listFiles();
        PhotoCutUtil op = new PhotoCutUtil();

        for(File file:files) {
//            Log.i("TAG",rootResource + "origin/" + file.getName());
            String fileName[] = file.getName().split("\\.");
            int count = Integer.parseInt(fileName[0]);
            Log.i("TAG",count+"");

//            op.blurImage(rootResource+"origin/"+file.getName(),rootResource+"test/blur/"+file.getName(),count);
//            op.dimImage(rootResource+"origin/"+file.getName(),rootResource+"test/dim/"+file.getName(),count);
//            op.wipeImage(rootResource+"origin/"+file.getName(),rootResource+"test/wipe/"+file.getName(),count);
//            op.puzzleImage(rootResource+"origin/"+file.getName(),rootResource+"test/puzzle/"+file.getName(),count);
            op.fadeImage(rootResource+"test/in/"+file.getName(), rootResource+"test/out/"+file.getName(),rootResource+"test/fade/"+file.getName(),count);
//            op.fadeTestImage(rootResource+"origin/"+file.getName(),rootResource+"test/fadeTest/"+file.getName(),count);
//            op.fadeInOutImage(rootResource+"origin/"+file.getName(),)
//            op.gridFilpImage(rootResource + "origin/" + file.getName(), rootResource + "test/grid/" + file.getName(), rootResource + "test/gridFilp/" + file.getName(), count);
        }

    }

}
