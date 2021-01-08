package ai.advance.integration.demo;

import android.app.Application;

import ai.advance.liveness.lib.GuardianLivenessDetectionSDK;
import ai.advance.liveness.lib.Market;

public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        GuardianLivenessDetectionSDK.initOffLine(this);
        GuardianLivenessDetectionSDK.letSDKHandleCameraPermission();

    }
}
