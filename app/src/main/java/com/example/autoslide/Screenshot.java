package com.example.autoslide;

import android.graphics.Bitmap;
import android.view.View;

public class Screenshot {
    public static Bitmap takeScreenshot(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);

        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static Bitmap takeScreenshotRoot(View view) {
        return takeScreenshot(view.getRootView());
    }

}
