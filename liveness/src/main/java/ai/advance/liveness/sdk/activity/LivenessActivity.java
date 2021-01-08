package ai.advance.liveness.sdk.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import ai.advance.common.utils.LogUtil;
import ai.advance.common.utils.ScreenUtil;
import ai.advance.liveness.lib.GuardianLivenessDetectionSDK;
import ai.advance.liveness.lib.LivenessResult;
import ai.advance.liveness.sdk.R;
import ai.advance.liveness.sdk.fragment.LivenessFragment;
//import ai.advance.liveness.sdk.fragment.LivenessTestFragment;

public class LivenessActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CODE = 1001;
    private LivenessFragment mLivenessFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.liveness_title);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
        }
//        getSupportActionBar();
//        ViewCompat.setElevation(getSupportActionBar(), 0);

//        actionBar.setTitle("Liveness Detection");
//        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_chevron_left_24);
//        actionBar.setBackgroundDrawable(
//                new ColorDrawable(getResources().getColor(R.color.liveness_camera_bg_light))
//        );
        setContentView(R.layout.activity_liveness);
        ScreenUtil.init(this);
        changeAppBrightness(255);
        if (GuardianLivenessDetectionSDK.isSDKHandleCameraPermission() && !allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, getRequiredPermissions(), PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * set current activity brightness to max
     * 将当前页面的亮度调节至最大
     */
    public void changeAppBrightness(int brightness) {
        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (brightness == -1) {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        } else {
            lp.screenBrightness = (brightness <= 0 ? 1 : brightness) / 255f;
        }
        window.setAttributes(lp);
    }

    @Override
    protected void onResume() {
        attachFragment();
        super.onResume();
    }

    private AlertDialog mErrorDialog;

    private void attachFragment() {
        if (allPermissionsGranted()) {
            if (GuardianLivenessDetectionSDK.isDeviceSupportLiveness()) {
                mLivenessFragment = LivenessFragment.newInstance();
                if (!mLivenessFragment.isAdded()) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, mLivenessFragment).commitAllowingStateLoss();
                }
            } else {
                final String errorMsg = getString(R.string.liveness_device_not_support);
                mErrorDialog = new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage(errorMsg)
                        .setPositiveButton(R.string.liveness_perform, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LivenessResult.setErrorMsg(errorMsg);
                                setResult(RESULT_OK);
                                finish();
                            }
                        }).create();
                mErrorDialog.show();
            }
        }
    }

    @Override
    protected void onPause() {
        if (mLivenessFragment != null && mLivenessFragment.isAdded()) {
            mLivenessFragment.release();
            getSupportFragmentManager().beginTransaction().remove(mLivenessFragment).commitAllowingStateLoss();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mErrorDialog != null && mErrorDialog.isShowing()) {
            mErrorDialog.dismiss();
        }
        super.onDestroy();
    }

    //----------request permission codes--------------
    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public String[] getRequiredPermissions() {
        return new String[]{Manifest.permission.CAMERA};
    }

    private boolean allGranted(int[] grantResults) {
        boolean hasPermission = true;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                hasPermission = false;
            }
        }
        return hasPermission;
    }

    /**
     * Denied camera permissions
     */
    public void onPermissionRefused() {
        new AlertDialog.Builder(this).setMessage(getString(R.string.liveness_no_camera_permission)).setPositiveButton(getString(R.string.liveness_perform), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).create().show();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            //已授权
            if (!allGranted(grantResults)) {
                onPermissionRefused();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void exitLivenessDetection(View view) {
        onBackPressed();
    }
}
