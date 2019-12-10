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

import org.opencv.android.Utils;
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
    private static ArrayList<Mat> screens;
    private ImageView imageView;

    private Mat tempImage;

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



        screens = new ArrayList<Mat>();
        final WebView myWebView = findViewById(R.id.webClient);
        WebViewClient MyWebViewClient = new WebViewClient();
        myWebView.setWebViewClient(MyWebViewClient);

        myWebView.loadUrl("https://cs125.cs.illinois.edu/learn/");
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.getSettings().setUserAgentString(USER_AGENT);

        Button screenshot = findViewById(R.id.screenshotButton);
        imageView = (ImageView) findViewById(R.id.imageView);
        screenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getScreens(myWebView);


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

        tempImage = frame;
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

    private void getScreens(WebView view) {
        int i = 0;
        do {
            Bitmap b = Screenshot.takeScreenshot(view);
            //imageView.setImageBitmap(b);
            Mat mat = new Mat();
            Utils.bitmapToMat(b.copy(Bitmap.Config.ARGB_8888, true), mat);
            screens.add(mat);
            view.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT));
            i++;

        } while(i <= 4);
        System.out.println(screens.size());


    }

    private boolean compare(Mat a, Mat b) {
        Mat mat = new Mat();
        Core.compare(a, b, mat, Core.CMP_EQ);
        if (Core.countNonZero(mat) < 1) {
            return true;
        }
        return false;

    }

    private int topMatch(Mat currentImage) {
        int max = 0;
        int maxIndex = -1;

        for (int i = 0; i < screens.size(); i++) {
            int currentMatch = matches(screens.get(i), currentImage);
            if (currentMatch > max) {
                max = currentMatch;
                maxIndex = i;
            }
        }

        return maxIndex;
    }

    //WORK IN PROGRESS
    private int topMatchOptimized(Mat currentImage) {
        int max = 0;
        int maxIndex = -1;

        return 0;
    }



    public int matches(Mat img1, Mat img2) {

//        img1 = Imgcodecs.imread(filename1, Imgcodecs.IMREAD_GRAYSCALE);
//        img2 = Imgcodecs.imread(filename2, Imgcodecs.IMREAD_GRAYSCALE);
//        if (img1.empty() || img2.empty()) {
//            System.err.println("Cannot read images!");
//            System.exit(0);
//        }
        //-- Step 1: Detect the keypoints using SURF Detector, compute the descriptors


        double hessianThreshold = 400;
        int nOctaves = 4, nOctaveLayers = 3;
        boolean extended = false, upright = false;
        SURF detector = SURF.create(hessianThreshold, nOctaves, nOctaveLayers, extended, upright);
        MatOfKeyPoint keypoints1 = new MatOfKeyPoint(), keypoints2 = new MatOfKeyPoint();
        Mat descriptors1 = new Mat(), descriptors2 = new Mat();
        detector.detectAndCompute(img1, new Mat(), keypoints1, descriptors1);
        detector.detectAndCompute(img2, new Mat(), keypoints2, descriptors2);
        //-- Step 2: Matching descriptor vectors with a FLANN based matcher
        // Since SURF is a floating-point descriptor NORM_L2 is used
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
        List<MatOfDMatch> knnMatches = new ArrayList<>();
        matcher.knnMatch(descriptors1, descriptors2, knnMatches, 2);
        //-- Filter matches using the Lowe's ratio test
        float ratioThresh = 0.7f;
        List<DMatch> listOfGoodMatches = new ArrayList<>();
        for (int i = 0; i < knnMatches.size(); i++) {
            if (knnMatches.get(i).rows() > 1) {
                DMatch[] matches = knnMatches.get(i).toArray();
                if (matches[0].distance < ratioThresh * matches[1].distance) {
                    listOfGoodMatches.add(matches[0]);
                }
            }
        }
        MatOfDMatch goodMatches = new MatOfDMatch();
        goodMatches.fromList(listOfGoodMatches);


        return listOfGoodMatches.size();

    }

}
