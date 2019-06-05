package com.cxd.opengl;

import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.cxd.utils.BitmapUtil;
import com.cxd.utils.TextResourceReader;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class BaseRenderActivity extends AppCompatActivity implements GLSurfaceView.Renderer{
    protected String TAG = "CXDRender";
    protected GLSurfaceView mGLSurfaceView;
    protected int vertexShaderID = 0, fragmentShaderID = 0, program = 0, textureID = 0;

    final float[] position = {
            //front
            -0.5f,0.5f,0.5f,
            0.5f,0.5f,0.5f,
            -0.5f, -0.5f,0.5f,
            0.5f, -0.5f, 0.5f,

            //back
            -0.5f,0.5f,-0.5f,
            0.5f,0.5f,-0.5f,
            -0.5f, -0.5f,-0.5f,
            0.5f, -0.5f, -0.5f,

            //right
            0.5f,-0.5f, 0.5f,
            0.5f,-0.5f, -0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, -0.5f,
    };

    final float[] texture = {//个数与上面的vertex一致，不然后面的会黑
            0.0f,0.0f,
            1.0f, 0.0f,
            0.0f,1.0f,
            1.0f, 1.0f,

            0.0f,0.0f,
            1.0f, 0.0f,
            0.0f,1.0f,
            1.0f, 1.0f,

            0.0f,0.0f,
            1.0f, 0.0f,
            0.0f,1.0f,
            1.0f, 1.0f,
             };

    final FloatBuffer posFloatBuf = getGLBuffer(position);//java和opengl有大小头区别，记得native order
    final FloatBuffer textFloatBuf = getGLBuffer(texture);
    private float[] modelMatrix = new float[16];
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

        Matrix.setIdentityM(modelMatrix, 0);

        Matrix.rotateM(modelMatrix,0, -45,0,1,0);//顺序
        Matrix.rotateM(modelMatrix,0, 30,1,0,0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

        vertexShaderID = GLES30.glCreateShader(GLES30.GL_VERTEX_SHADER);
        String vertexShader = TextResourceReader.readTextFileFromResource(this, R.raw.base_vertex_shader);
        Log.d(TAG, vertexShader);
        GLES30.glShaderSource(vertexShaderID, vertexShader);
        GLES30.glCompileShader(vertexShaderID);
        fragmentShaderID = GLES30.glCreateShader(GLES30.GL_FRAGMENT_SHADER);

        String fragmentShader = TextResourceReader.readTextFileFromResource(this, R.raw.base_fragment_shader);
        Log.d(TAG, fragmentShader);
        GLES30.glShaderSource(fragmentShaderID, fragmentShader);
        GLES30.glCompileShader(fragmentShaderID);
        program = GLES30.glCreateProgram();
        GLES30.glAttachShader(program, vertexShaderID);
        GLES30.glAttachShader(program, fragmentShaderID);
        GLES30.glLinkProgram(program);
        int[] linkStatus = new int[1];
        GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            Log.e(TAG, "Could not link program: ");
            Log.e(TAG, GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteProgram(program);
            program = 0;
        }

        Bitmap bitmap = BitmapUtil.decodeResource(this, R.raw.tex_0);
        ByteBuffer byteBuffer = BitmapUtil.getByteBuffer(bitmap);
        byteBuffer.position(0);
        textureID = createImageTexture(byteBuffer, bitmap.getWidth(), bitmap.getHeight());
        bitmap.recycle();

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {

    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT|GLES30.GL_DEPTH_BUFFER_BIT);
        GLES30.glClearColor(0.4f,0.4f,0.4f,1.0f);
        GLES30.glViewport(0,0,1024,1024);

        GLES30.glUseProgram(program);

        GLES30.glEnableVertexAttribArray(0);
        posFloatBuf.position(0);//容易踩的坑，小心
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, posFloatBuf);

        GLES30.glEnableVertexAttribArray(1);
        textFloatBuf.position(0);
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 0, textFloatBuf);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glUniform1i(GLES30.glGetUniformLocation(program, "uTextue"), 0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureID);

        GLES30.glUniformMatrix4fv(GLES30.glGetUniformLocation(program, "modelMatrix"), 1, false, modelMatrix, 0);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP,0,4);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP,4,4);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP,8,4);

        GLES30.glDisableVertexAttribArray(0);
        GLES30.glDisableVertexAttribArray(1);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        GLES30.glUseProgram(0);
    }

private void checkError(String msg){
    Log.d("cxd", msg+":"+GLES30.glGetError());
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

    private static  FloatBuffer getGLBuffer(float[] srcByte){
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4*srcByte.length);
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.put(srcByte);
        byteBuffer.position(0);
        return floatBuffer;
    }
}
