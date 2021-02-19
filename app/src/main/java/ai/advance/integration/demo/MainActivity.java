package ai.advance.integration.demo;

import android.content.Intent;
import android.graphics.Bitmap;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import ai.advance.liveness.lib.LivenessResult;
import ai.advance.liveness.sdk.activity.LivenessActivity;
import ai.advance.liveness.sdk.activity.ResultActivity;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_CODE_LIVENESS = 1000;
    static final int REQUEST_CODE_RESULT_PAGE = 1001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.liveness_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LivenessActivity.class);
                startActivityForResult(intent, REQUEST_CODE_LIVENESS);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LIVENESS) {
            if (LivenessResult.isSuccess()) {
                Bitmap livenessBitmap = LivenessResult.getLivenessBitmap();
                // TODO send bitmap to your server to request anti-spoofing
            } else {
                String errorCode = LivenessResult.getErrorCode();
                String errorMsg = LivenessResult.getErrorMsg();
                Log.e("liveness", "errorCode:" + errorCode + ",errorMsg:" + errorMsg);
            }
            startActivityForResult(new Intent(this, ResultActivity.class), REQUEST_CODE_RESULT_PAGE);
        }
    }

}
