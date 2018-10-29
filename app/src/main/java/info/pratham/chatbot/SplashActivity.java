package info.pratham.chatbot;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.pratham.chatbot.menuDisplay.ContentDisplay;
import info.pratham.chatbot.permissions.ActivityManagePermission;
import info.pratham.chatbot.permissions.PermissionResult;
import info.pratham.chatbot.permissions.PermissionUtils;

public class SplashActivity extends ActivityManagePermission implements PermissionResult {

   /* @BindView(R.id.btn_start)
    Button btn_start;*/

    String[] permissionArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_start)
    public void startUp() {
        permissionArray = new String[]{PermissionUtils.Manifest_READ_EXTERNAL_STORAGE,
                PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE,
                PermissionUtils.Manifest_RECORD_AUDIO};
        Log.d("TEST", "startUpProcess: Version " + Build.VERSION.SDK_INT);
        Log.d("TEST", "startUpProcess: Version " + Build.VERSION_CODES.M);
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && !isPermissionsGranted(SplashActivity.this, permissionArray)) {
            askCompactPermissions(permissionArray, this);
        } else {
            gotoNext();
        }

    }

    public void gotoNext() {
        startActivity(new Intent(SplashActivity.this, ContentDisplay.class));

    }

    @Override
    public void permissionGranted() {
        gotoNext();
    }

    @Override
    public void permissionDenied() {

    }

    @Override
    public void permissionForeverDenied() {

    }
}
