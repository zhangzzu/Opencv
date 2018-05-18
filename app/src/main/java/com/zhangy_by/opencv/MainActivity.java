package com.zhangy_by.opencv;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private ImageView iv;

    private String TAG = "xxxxxxxxx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = findViewById(R.id.imageView);

        if (OpenCVLoader.initDebug()) {
            Log.e(TAG, "onCreate: success");
        } else {
            Log.e(TAG, "onCreate: fail");
        }

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cat);
                //创建两个矩阵（容器）对象，原图像（src），输出图像（dst）
                Mat src = new Mat();
                Mat dst = new Mat();
                //将一张图片变成矩阵对象
                Utils.bitmapToMat(bitmap, src);
                //进行图像彩色空间转换
                Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGR2GRAY);
                //将矩阵对象变成图片
                Utils.matToBitmap(dst, bitmap);
                iv.setImageBitmap(bitmap);
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cat);
                Mat src = new Mat();
                Mat dst = new Mat();
                Utils.bitmapToMat(bitmap, src);
                Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGR2GRAY);
                Mat blur1 = new Mat();
                Mat blur2 = new Mat();
                Imgproc.GaussianBlur(dst, blur1, new Size(5, 5), 5);
                Imgproc.GaussianBlur(dst, blur2, new Size(7, 7), 5);
                Mat diff = new Mat();
                Core.absdiff(blur1, blur2, diff);
                Core.multiply(diff, new Scalar(100), diff);
                Imgproc.threshold(diff, diff, 50, 255, Imgproc.THRESH_BINARY_INV);

                //Imgproc.Laplacian(src, dst, -1, 3, 1, 0, Core.BORDER_DEFAULT);
                Utils.matToBitmap(diff, bitmap);
                iv.setImageBitmap(bitmap);
            }
        });

        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cat);
                Mat src = new Mat();
                Mat dst = new Mat();
                Utils.bitmapToMat(bitmap, src);
                Imgproc.blur(src, dst, new Size(100, 100));
                Utils.matToBitmap(dst, bitmap);
                iv.setImageBitmap(bitmap);
            }
        });

        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.girl);
                Observable.just(bitmap).map(new Function<Bitmap, Bitmap>() {
                    @Override
                    public Bitmap apply(Bitmap bitmap) throws Exception {
                        Mat src = new Mat();
                        Mat dst = new Mat();
                        Utils.bitmapToMat(bitmap, src);
                        Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGR2GRAY);
                        Imgproc.Canny(src, dst, 10, 100);

                        Mat h = new Mat();
                        List<MatOfPoint> list = new ArrayList<>();
                        Imgproc.findContours(dst, list, h, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

                        Mat m = new Mat();
                        m.create(dst.rows(), dst.cols(), CvType.CV_8UC3);
                        Random random = new Random();
                        for (int i = 0; i < list.size(); i++) {
                            Imgproc.drawContours(m, list, i, new Scalar(random.nextInt(255), random.nextInt(255), random.nextInt(255)), -1);
                        }
                        Utils.matToBitmap(m, bitmap);
                        return bitmap;
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Bitmap>() {
                            @Override
                            public void accept(Bitmap bitmap) throws Exception {
                                iv.setImageBitmap(bitmap);
                            }
                        });
            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CameraActivity.class));
            }
        });
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
