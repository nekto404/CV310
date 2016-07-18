package com.example.nekto404.cv310;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import android.content.DialogInterface;
import android.os.Build;
/*
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;*/
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;
/*
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;*/
/*
@RuntimePermissions*/
public class MainActivity extends Activity implements CvCameraViewListener2 {

    private CameraBridgeViewBase mOpenCvCameraView;

    private int                    mViewMode;

    private Mat                    mRgba;
    private Mat                    mIntermediateMat;
    private Mat                    mGray;

    private MenuItem mItemPreviewRGBA;
    private MenuItem mItemPreviewGray;
    private MenuItem mItemPreviewCanny;
    private MenuItem mItemPreviewFeatures;

    TextView tv;
    /*
    @NeedsPermission(Manifest.permission.CAMERA)
    void showCamera(){
        Toast.makeText(this,
                "Ok", Toast.LENGTH_LONG).show();
    }

    @OnShowRationale(Manifest.permission.CAMERA)
    public void showRationaleForCamera(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.detail_activity_permission_camera_title)
                .setMessage(R.string.detail_activity_permission_camera_message)

                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setCancelable(false)
                .show();
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    public void cameraNeverAskAgain() {
        Toast.makeText(this,
                R.string.detail_activity_permission_camera_denied, Toast.LENGTH_LONG).show();
        openApplicationSettings();
    }

*/
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    // Load native library after(!) OpenCV initialization
//                    System.loadLibrary("mixed_sample");

                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mItemPreviewRGBA = menu.add("RGBA");
        mItemPreviewGray = menu.add("GRAY");
        mItemPreviewCanny = menu.add("Canny");
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item == mItemPreviewRGBA) {
            mViewMode = 2;
        } else if (item == mItemPreviewGray) {
            mViewMode = 1;
        } else if (item == mItemPreviewCanny) {
            mViewMode = 3;
        }

        return true;
    }/*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //MainActivityPermissionsDispatcher.showCameraWithCheck(this);
        }
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        tv =(TextView) findViewById(R.id.text1);
        tv.setText("test");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }
    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
    }

    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
        mIntermediateMat.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        final int viewMode = mViewMode;
        switch (viewMode) {
            case 1:
                // input frame has gray scale format
                Imgproc.cvtColor(inputFrame.gray(), mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
                break;
            case 2:
                // input frame has RBGA format
                mRgba = inputFrame.rgba();
                break;
            case 3:
                // input frame has gray scale format
                mRgba = inputFrame.rgba();
                Imgproc.Canny(inputFrame.gray(), mIntermediateMat, 40, 80);
                Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
                break;
        }

        return mRgba;
    }

    private void openApplicationSettings() {    }

    public native String  stringFromJNI();

    static {
        System.loadLibrary("hello-jni");
    }
}