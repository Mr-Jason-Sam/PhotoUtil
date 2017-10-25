package smart.photoutil;

import android.provider.Telephony;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jasonsam on 2017/10/9.
 */

public class CmdRun {
    public final static String TAG = "PhotoUtil";
    public final static String ROOTPATH = "/sdcard/App/";
    public final static String MOIVE = "test.mp4";

    public enum  Transition { Wipe, Dim, Fade, Blur, Puzzle, GridToFlip;}

    public void processFFmpegCmd(final String[] command) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Log.i(TAG,"Starting thread");
                    ProcessBuilder builder = new ProcessBuilder();
                    builder.command(command);
                    builder.redirectErrorStream(true);

                    Process p;
                    p = builder.start();
                    BufferedReader buf = null;
                    String line = null;
                    buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    Log.i(TAG,"Starting readLine");
                    while ((line = buf.readLine()) != null) {
                        Log.i(TAG, "processFFmpegCmd, line:" + line);
//                textview.setText(line);
                    }
                    p.waitFor();//wait for command completed.
                    Log.i(TAG, "processFFmpegCmd, process exit.");

                } catch (Exception e) {
                    Log.i(TAG,"Exception");
                    e.printStackTrace();
                }

            }
        });

    }

    public void TranAction(float location , float duration, Transition transition){
        File file = new File(ROOTPATH+"out");
        PhotoCutUtil util = new PhotoCutUtil();

        if (!file.exists()){
            try {
                file.createNewFile();
            }catch (Exception e){
                Log.i(TAG,"file Exception");
                e.printStackTrace();
            }
        }
        materialFrame(location, duration);

//        for ()

        switch (transition){
            case Wipe:

//                util.cutImage("","");
                break;
            case Dim:
                break;
            case Fade:
                break;
            case Blur:
                break;
            case Puzzle:
                break;
            case GridToFlip:
                break;
            default:
        }
    }

    public void everyPhotoCut(String srcPath, String desPath, Transition transition){
        File root = new File(srcPath);
        File[] files = root.listFiles();

        for(File file:files) {
            System.out.println(srcPath + file.getName());
            String fileName[] = file.getName().split("\\.");
            int count = Integer.parseInt(fileName[0]);
            System.out.println(count);

            PhotoCutUtil util = new PhotoCutUtil();

        }
    }

    //素材每帧
    public void materialFrame(float location, float duration){
        File file = new File(ROOTPATH + "material");
        List<String> cmd =  new ArrayList<String>();
        String[] command;

        if (!file.exists()){
            try {
                file.createNewFile();
            }catch (Exception e){
                Log.i(TAG,"file Exception");
                e.printStackTrace();
            }
        }


        if (duration >0)
            cmd.add("ffmpeg -i " + MOIVE + " -ss " + location + " -t "+ duration + " " + ROOTPATH + "material"+"/%d.png");
        else if(duration <0)
            cmd.add("ffmpeg -i " + MOIVE + " -ss " + (location-duration) + " -t "+ (-duration) + " " + ROOTPATH + "material"+"/%d.png");

        processFFmpegCmd(cmd.get(0).split(" "));
    }




}
