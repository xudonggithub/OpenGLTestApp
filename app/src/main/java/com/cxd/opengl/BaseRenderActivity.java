package com.cxd.opengl;

import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cxd.utils.TextResourceReader;

import com.arcsoft.utils.BitmapUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class BaseRenderActivity extends AppCompatActivity implements GLSurfaceView.Renderer{
    protected GLSurfaceView mGLSurfaceView;
    protected int vertexShaderID = 0, fragmentShaderID = 0, program = 0, textureID = 0;
    final float[] position = {
            -1.0f,1.0f,0.0f,
            1.0f,1.0f,0.0f,
            -1.0f, -1.0f,0.0f,
            1.0f, -1.0f,0.0f  };

    final float[] texture = {
           0.0f,0.0f,
            0.0f,1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f  };

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

        vertexShaderID = GLES30.glCreateShader(GLES30.GL_VERTEX_SHADER);
        GLES30.glShaderSource(vertexShaderID, TextResourceReader.readTextFileFromResource(this, R.raw.base_vertex_shader));
        GLES30.glCompileShader(vertexShaderID);

        fragmentShaderID = GLES30.glCreateShader(GLES30.GL_FRAGMENT_SHADER);
        GLES30.glShaderSource(fragmentShaderID, TextResourceReader.readTextFileFromResource(this, R.raw.base_fragment_shader));
        GLES30.glCompileShader(fragmentShaderID);

        program = GLES30.glCreateProgram();
        GLES30.glAttachShader(program, vertexShaderID);
        GLES30.glAttachShader(program, fragmentShaderID);
        GLES30.glLinkProgram(program);

        Bitmap bitmap = BitmapUtil.decodeResource(this, R.raw.tex_0);
        textureID = createImageTexture(ByteBuffer.wrap(BitmapUtil.bitmapToBytes(bitmap)), bitmap.getWidth(), bitmap.getHeight());
        bitmap.recycle();

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {

    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES30.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);

        GLES30.glUseProgram(program);

        GLES30.glEnableVertexAttribArray(0);
        GLES30.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false,  0, FloatBuffer.wrap(position));

        GLES30.glEnableVertexAttribArray(1);
        GLES30.glVertexAttribPointer(0, 2, GLES20.GL_FLOAT, false,  0, FloatBuffer.wrap(texture));

        GLES30.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES30.glUniform1i(GLES30.glGetUniformLocation(program, "uTextue"), 0);
        GLES30.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);

        GLES30.glDisableVertexAttribArray(0);
        GLES30.glDisableVertexAttribArray(1);
        GLES30.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES30.glUseProgram(0);
    }

    @Override
    protected void onResume() {
        mGLSurfaceView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {

        mGLSurfaceView.setPreserveEGLContextOnPause(true);
        mGLSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                if(vertexShaderID > 0) {
                    GLES30.glDeleteShader(vertexShaderID);
                    vertexShaderID = 0;
                }
                if(fragmentShaderID > 0) {
                    GLES30.glDeleteShader(fragmentShaderID);
                    fragmentShaderID = 0;
                }
                if(program > 0) {
                    GLES30.glDeleteProgram(program);
                    program = 0;
                }
                if(textureID > 0){
                    GLES30.glDeleteTextures(1, new int[]{textureID}, 0);
                    textureID = 0;
                }
            }
        });
        mGLSurfaceView.onPause();
        super.onPause();
    }

    public static int createImageTexture(ByteBuffer data, int width, int height) {
        int[] temp = new int[1];
        int textureID;

        GLES30.glGenTextures(1, temp, 0);
        textureID = temp[0];

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,  GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,  GLES20.GL_LINEAR);

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, /*level*/ 0, GLES20.GL_RGBA, width, height, /*border*/ 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, data);

        return textureID;
    }

}
