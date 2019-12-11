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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.JavaCamera2View;
import org.opencv.android.Utils;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.android.JavaCameraView;
import org.opencv.core.Size;
import org.opencv.features2d.BFMatcher;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.ORB;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.xfeatures2d.SURF;


import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    CameraBridgeViewBase cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;
    public static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19";
    private static ArrayList<Mat> screens;
    private ImageView imageView;

    private Mat tempImage;
    private Mat tempScreenshot;

    private boolean takeShot = false;

    private int[] matchNums = new int[9];
    private int currentSlide = 0;

    private int matches = 0;

    private int counter = 0;

    private static String Tag = "MainActicity";

    private WebView myWebView;

    private TextView matchesDisplay;

    private int max = 0;
    private int maxIndex = -1;

    FeatureDetector detector;
    DescriptorExtractor descriptor;
    DescriptorMatcher matcher;
    Mat descriptors2, descriptors1;
    Mat img1;
    MatOfKeyPoint keypoints1, keypoints2;

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

        matchesDisplay = findViewById(R.id.matchesDisplay);
        matchesDisplay.setText("Number of Matches: " + matches);

        screens = new ArrayList<Mat>();

        myWebView = findViewById(R.id.webClient);


        WebViewClient MyWebViewClient = new WebViewClient();
        myWebView.setWebViewClient(MyWebViewClient);

        myWebView.loadUrl("https://cs125.cs.illinois.edu/learn/");
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.getSettings().setUserAgentString(USER_AGENT);


        imageView = (ImageView) findViewById(R.id.imageView);
        Button startAnalysis = findViewById(R.id.startAnalysis);
        startAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (takeShot) {
                    takeShot = false;
                } else {
                    takeShot = true;
                }
//                LinearLayout cameraLayout = (LinearLayout) findViewById(R.id.cameraNest);
//
//                View cameraNest = cameraLayout;
//
//                JavaCamera2View cameraDisplay = findViewById(R.id.javaCamera2View);
//
//
//                //Bitmap cameraScreenshot = Bitmap.createBitmap(cameraDisplay);
//
//                //View v1 = getWindow().getDecorView();
//
//                //Bitmap a = Screenshot.takeScreenshot(cameraNest);
//
//
//                Bitmap a = loadBitmapFromView(cameraLayout);
//                imageView.setImageBitmap(a);
//                //cameraLayout.setVisibility(View.GONE);
//
//                Bitmap amp32 = a.copy(Bitmap.Config.ARGB_8888, true);
//                Bitmap bmp32 = b.copy(Bitmap.Config.ARGB_8888, true);
//
//                Mat mat = new Mat();
//                Mat cameraMat = new Mat();
//
//                Utils.bitmapToMat(bmp32, mat);
//                Utils.bitmapToMat(amp32, cameraMat);
//
//                screens.add(mat);
//
//                if (mat == null || mat.empty()) {
//                    Toast.makeText(getApplicationContext(), "Screenshot is null", Toast.LENGTH_SHORT).show();
//                } else if (cameraMat == null || cameraMat.empty()) {
//
//                    Toast.makeText(getApplicationContext(), "camera is null", Toast.LENGTH_SHORT).show();
//
//                } else {
//
//
//                    Mat hsvMat = new Mat();
//                    Imgproc.cvtColor(mat, hsvMat, Imgproc.COLOR_RGB2HSV);
//
//                    Mat hsvTempImage = new Mat();
//                    Imgproc.cvtColor(cameraMat, hsvTempImage, Imgproc.COLOR_RGB2HSV);
//
//
//                    int matchNum = matches(hsvMat, hsvTempImage);
//
//                    Toast.makeText(getApplicationContext(), "Number of Matches: " + matchNum, Toast.LENGTH_SHORT).show();
//                }
            }
        });


        Button screenshot = findViewById(R.id.screenshotButton);
        screenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println();
                //imageView.setImageBitmap(getScreens(myWebView));
            }
        });

        Button forward = findViewById(R.id.rightArrow);
        forward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                myWebView.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT));
                counter++;
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

                switch (status) {

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


    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        Mat frame = inputFrame.rgba();

        if (takeShot) {
            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGB2BGR);
            Mat smallerFrame = new Mat();
            try {

                Imgproc.resize(frame, smallerFrame, new Size(900, 675));
            } catch (Exception e) {

            }

            getMatchNums(myWebView, smallerFrame);



//            int top = topMatch();
            System.out.println("top: " + maxIndex);
            goToSlide(myWebView, maxIndex);
            takeShot = false;
//            goToSlide(myWebView,0);
            max = 0;
            maxIndex = -1;




        }


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

        if (!OpenCVLoader.initDebug()) {
            Toast.makeText(getApplicationContext(), "There's a problem, yo!", Toast.LENGTH_SHORT).show();
        } else {
            baseLoaderCallback.onManagerConnected(baseLoaderCallback.SUCCESS);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraBridgeViewBase != null) {

            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.disableView();
        }
    }

    private void getMatchNums(WebView view, Mat frame) {
        //int cmn = 0;


        /* TODO Change the for loop condition to a while loop that compares two Bitmaps/Mats using match so
            it knows when to stop getting slides. We can just compare the match values stored in the array
            to see if it is within a certain threshold */

        for (int i = currentSlide; i < matchNums.length; i++) {
            System.out.println("currentSlide " + currentSlide);
            matches(frame);
            //int[] toBeAveraged = new int[5];
//            int count = 0;
//            long start = java.lang.System.currentTimeMillis();

//            while (count != 5) {
//
//                if ((java.lang.System.currentTimeMillis() - start) % 500 == 0) {//need something here to know when to add number in array
//                    toBeAveraged[count] = matches;
//                    count++;
//                }
//            }

//            int average = 0;
//            for (int num : toBeAveraged) {
//                average += num;
//            }
//            average /= 5;

            matchNums[currentSlide] = matches;
            System.out.println("Current Slide: "+ currentSlide + " Number of matches: " + matches);

            if(matches > max) {
                max = matches;
                maxIndex = currentSlide;
            }

            view.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT));
            //for(int j = 0; j<400; j++){}
            try {
                wait(100l);
            } catch (Exception e) {}


            matches = 0;
            currentSlide++;
        }
        for (int num : matchNums) {
            System.out.println(num);
        }





    }


//    private Bitmap getScreens(WebView view) {
//        int i = 0;
//        Bitmap previous = null;
//        Bitmap b = null;
//        int matchNum = 0;
//        //boolean same;
//        do {
//            b = Screenshot.takeScreenshot(view);
//            System.out.println(previous);
//            System.out.println(b);
//            if (b.sameAs(previous)) {
//                System.out.print("same ");
//                System.out.println(i);
//                break;
//            }
//            //imageView.setImageBitmap(b);
//            Mat mat = new Mat();
//            Utils.bitmapToMat(b.copy(Bitmap.Config.ARGB_8888, true), mat);
//            screens.add(mat);
//            view.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT));
//            i++;
//            previous = Bitmap.createBitmap(b);
//            System.out.println(screens.size());
//        } while (true);
//
//        return b;
//
//    }


    private static boolean compare(Bitmap b1, Bitmap b2) {
        if (b1.getWidth() == b2.getWidth() && b1.getHeight() == b2.getHeight()) {
            int[] pixels1 = new int[b1.getWidth() * b1.getHeight()];
            int[] pixels2 = new int[b2.getWidth() * b2.getHeight()];
            b1.getPixels(pixels1, 0, b1.getWidth(), 0, 0, b1.getWidth(), b1.getHeight());
            b2.getPixels(pixels2, 0, b2.getWidth(), 0, 0, b2.getWidth(), b2.getHeight());
            if (Arrays.equals(pixels1, pixels2)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private int topMatch() {
        int max = 0;
        int maxIndex = -1;

        for (int i = 0; i < matchNums.length; i++) {
            if (matchNums[i] > 0) {
                max = matchNums[i];
                maxIndex = i;


            }
        }
        return maxIndex;
    }

    private void goToSlide(View v, int to) {
//        if (to > currentSlide) {
//            int moves = to - currentSlide;
//            for (int i = 0; i < moves; i++) {
//                v.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT));
//            }
//        }
//
//        int moves = currentSlide - to;
//        for (int i = 0; i < moves; i++) {
//            v.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT));
//        }

        for (int i = 0; i < 30; i++) {
            v.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT));
        }

        for (int i = 0; i < to; i++) {
            v.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT));
        }

    }

//    private int topMatch(Mat currentImage) {
//        int max = 0;
//        int maxIndex = -1;
//
//        for (int i = 0; i < screens.size(); i++) {
//            int currentMatch = matches(screens.get(i), currentImage);
//            if (currentMatch > max) {
//                max = currentMatch;
//                maxIndex = -1;
//            }
//        }
//        return maxIndex;
//    }


    public void matches(Mat img1) {

        System.out.println("algoritm started");

        Bitmap b = Screenshot.takeScreenshot(myWebView);
        Bitmap bmp32 = b.copy(Bitmap.Config.ARGB_8888, true);

        Mat imgTwo = new Mat();
        Utils.bitmapToMat(bmp32, imgTwo);

        Mat img2 = new Mat();
        Imgproc.resize(imgTwo, img2, new Size(900, 675));


        System.out.println("algoritm rarted");
//        Mat hsvMat = new Mat();
//        Imgproc.cvtColor(img1, hsvMat, Imgproc.COLOR_RGB2HSV);
//
//        Mat hsvTempImage = new Mat();
//        Imgproc.cvtColor(img2, hsvTempImage, Imgproc.COLOR_RGB2HSV);
//
//        img1 = hsvMat;
//        img2 = hsvTempImage;

        double hessianThreshold = 400;
        int nOctaves = 4, nOctaveLayers = 3;
        boolean extended = false, upright = false;
        //System.out.println("1");
        SURF detector = SURF.create(hessianThreshold, nOctaves, nOctaveLayers, extended, upright);
        MatOfKeyPoint keypoints1 = new MatOfKeyPoint(), keypoints2 = new MatOfKeyPoint();
        Mat descriptors1 = new Mat(), descriptors2 = new Mat();

        //System.out.println("2");
        detector.detectAndCompute(img1, new Mat(), keypoints1, descriptors1);
        detector.detectAndCompute(img2, new Mat(), keypoints2, descriptors2);
        //System.out.println("3");
        //-- Step 2: Matching descriptor vectors with a FLANN based matcher
        // Since SURF is a floating-point descriptor NORM_L2 is used
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
       // System.out.println("4");

        List<MatOfDMatch> knnMatches = new ArrayList<>();
        matcher.knnMatch(descriptors1, descriptors2, knnMatches, 2);
        //-- Filter matches using the Lowe's ratio test
        float ratioThresh = 0.7f;
        int goodMatch = 0;
        //List<DMatch> listOfGoodMatches = new ArrayList<>();
        for (int i = 0; i < knnMatches.size(); i++) {
            if (knnMatches.get(i).rows() > 1) {
                DMatch[] matches = knnMatches.get(i).toArray();
                if (matches[0].distance < ratioThresh * matches[1].distance) {
                    //listOfGoodMatches.add(matches[0]);
                    goodMatch++;
                }
            }
        }
        //System.out.println("5");
        //MatOfDMatch goodMatches = new MatOfDMatch();
        //goodMatches.fromList(listOfGoodMatches);


        matches = goodMatch;
        System.out.println("number of mat: " + goodMatch);
//            Toast.makeText(getApplicationContext(), "camera is null", Toast.LENGTH_SHORT).show();


    }


    /**
     * ORB orb = ORB.create();
     * <p>
     * Mat descriptors1 = new Mat();
     * MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
     * Mat mask1 = new Mat();
     * orb.detectAndCompute(img1, mask1, keypoints1, descriptors1);
     * <p>
     * Mat descriptors2 = new Mat();
     * MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
     * Mat mask2 = new Mat();
     * orb.detectAndCompute(img2, mask2, keypoints2, descriptors2);
     * <p>
     * BFMatcher bf = BFMatcher.create();
     * MatOfDMatch potMatch = new MatOfDMatch();
     * List matches = new ArrayList();
     * bf.match(descriptors1, descriptors2, potMatch);
     * <p>
     * matches = potMatch.toList();
     * <p>
     * <p>
     * return matches.size();
     */


    static MatOfDMatch filterMatchesByDistance(MatOfDMatch matches) {
        List<DMatch> matches_original = matches.toList();
        List<DMatch> matches_filtered = new ArrayList<DMatch>();

        int DIST_LIMIT = 30;
        // Check all the matches distance and if it passes add to list of filtered matches
        Log.d("DISTFILTER", "ORG SIZE:" + matches_original.size() + "");
        for (int i = 0; i < matches_original.size(); i++) {
            DMatch d = matches_original.get(i);
            if (Math.abs(d.distance) <= DIST_LIMIT) {
                matches_filtered.add(d);
            }
        }
        Log.d("DISTFILTER", "FIL SIZE:" + matches_filtered.size() + "");

        MatOfDMatch mat = new MatOfDMatch();
        mat.fromList(matches_filtered);
        return mat;
    }

}
