package com.zhangy_by.opencv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private ImageView iv;
    private Button btn;

    private String TAG = "xxxxxxxxx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = findViewById(R.id.imageView);
        btn = findViewById(R.id.button);

        if (OpenCVLoader.initDebug()) {
            Log.e(TAG, "onCreate: success");
        } else {
            Log.e(TAG, "onCreate: fail");
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cat, options);
                Mat src = new Mat();
                Mat dst = new Mat();
                Utils.bitmapToMat(bitmap, src);
                Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGR2GRAY);
                Utils.matToBitmap(dst, bitmap);
                iv.setImageBitmap(bitmap);
            }
        });
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
