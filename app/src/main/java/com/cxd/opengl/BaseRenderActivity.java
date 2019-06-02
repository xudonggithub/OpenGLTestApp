package com.cxd.opengl;

import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class BaseRenderActivity extends AppCompatActivity implements GLSurfaceView.Renderer{
    GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_render);
        mGLSurfaceView = findViewById(R.id.surface_view);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 24, 0);
        mGLSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
        mGLSurfaceView.setRenderer(this);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        mGLSurfaceView.getHolder().setFormat(android.graphics.PixelFormat.TRANSLUCENT);

    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {

    }

    @Override
    public void onDrawFrame(GL10 gl10) {

    }

    @Override
    protected void onResume() {
        mGLSurfaceView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {

        mGLSurfaceView.setPreserveEGLContextOnPause(true);
        mGLSurfaceView.onPause();
        super.onPause();
    }
}
