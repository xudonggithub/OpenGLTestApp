package com.cxd.opengl;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    static {
        System.loadLibrary("native-lib");
    }

    private static final  int INIT_PERMISSION=1000;
    private String[] permssions={
            Manifest.permission.CAMERA,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private Handler  handler = new Handler();

    private void initPermission(){
        ArrayList<String> currentPermission=new ArrayList<String>();
        for(int i=0;i<permssions.length;i++){
            if(ContextCompat.checkSelfPermission(this, permssions[i])!= PackageManager.PERMISSION_GRANTED){
                currentPermission.add(permssions[i]);
            }
        }

        String[] usedPermissions=new String[currentPermission.size()];
        currentPermission.toArray(usedPermissions);
        if(usedPermissions.length>0) {
            ActivityCompat.requestPermissions(this, usedPermissions, INIT_PERMISSION);
        } else{
            startActivity();
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case INIT_PERMISSION: {
                for(int i=0;i<grantResults.length;i++){
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "必要权限未开启", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                startActivity();
                return;
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    initPermission();
                }
            }
        },300);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.base_texture:
                startActivity();
                break;
        }
    }

    private void startActivity(){
        Intent intent=new Intent(this, CubeRenderActivity.class);
        startActivity(intent);
        finish();
    }
}
