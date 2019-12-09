package com.example.autoslide;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.android.JavaCameraView;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.List;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.xfeatures2d.SURF;


import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity implements  CameraBridgeViewBase.CvCameraViewListener2 {

    CameraBridgeViewBase cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;
    public static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19";
    private static ArrayList<Bitmap> screens;
    private View main;
    private ImageView imageView;



    private static String Tag = "MainActicity";
    static {
        if (OpenCVLoader.initDebug()) {
            Log.d(Tag, "sucess");
        } else {
            Log.d(Tag, "fail");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraBridgeViewBase = findViewById(R.id.javaCamera2View);
        cameraBridgeViewBase.setVisibility(View.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);



        screens = new ArrayList<Bitmap>();
        final WebView myWebView = findViewById(R.id.webClient);
        WebViewClient MyWebViewClient = new WebViewClient();
        myWebView.setWebViewClient(MyWebViewClient);

        myWebView.loadUrl("https://cs125.cs.illinois.edu/learn/");
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.getSettings().setUserAgentString(USER_AGENT);

        Button screenshot = findViewById(R.id.screenshotButton);
        main = findViewById(R.id.webClient);
        imageView = (ImageView) findViewById(R.id.imageView);
        screenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap b = Screenshot.takeScreenshot(myWebView);
                imageView.setImageBitmap(b);



            }
        });

        Button forward = findViewById(R.id.rightArrow);
        forward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                myWebView.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT));
            }
        });

        Button back = findViewById(R.id.leftArrow);
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                myWebView.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT));
            }
        });

        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                super.onManagerConnected(status);

                switch(status){

                    case BaseLoaderCallback.SUCCESS:
                        cameraBridgeViewBase.enableView();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }


            }

        };


    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        Mat frame = inputFrame.rgba();


//        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BayerBG2BGR);
        return frame;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!OpenCVLoader.initDebug()){
            Toast.makeText(getApplicationContext(),"There's a problem, yo!", Toast.LENGTH_SHORT).show();
        }

        else
        {
            baseLoaderCallback.onManagerConnected(baseLoaderCallback.SUCCESS);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cameraBridgeViewBase!=null){

            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }
    }


    /**
     * Take a screen shot of the screen.
     * @param id the id of the picture to be taken
     * @return the URL of the photo
     */
    private String takeScreenShot(int id) {

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + id + ".jpg";
            //System.out.println(mPath);


            // create bitmap screen capture
            View v1 = findViewById(R.id.webClient);
//            For fragment view activate below line
//            View v1 = getActivity().getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);
            //Toast.makeText(MainActivity.this, mPath, Toast.LENGTH_SHORT).show();
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
//            Test
            openScreenshot(imageFile);
            return mPath;
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Opening a screen capture
     * @param imageFile
     */
    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    /**
     * Another capture screen method for testing
     * @param view root view to capture
     * @return the screenshot
     */
    public Bitmap screenShot(WebView view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Picture picture = view.capturePicture();
        Canvas canvas = new Canvas(bitmap);
//        view.draw(canvas);
        canvas.drawPicture(picture);
        FileOutputStream fos = null;
        return bitmap;
    }

}
