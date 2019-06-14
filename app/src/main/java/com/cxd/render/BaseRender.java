package com.cxd.render;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import com.cxd.utils.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


public abstract class BaseRender {
    protected int vertexShaderID = 0, fragmentShaderID = 0, program = 0, textureID = 0;
    protected float[] modelMatrix = new float[16];
    protected float[] projectMatrix = new float[16];
    protected float[] viewMatrix = new float[16];
    protected String TAG = "CXDRender";

    public static  FloatBuffer getGLBuffer(float[] srcByte){
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4*srcByte.length);
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.put(srcByte);
        byteBuffer.position(0);
        return floatBuffer;
    }

    public int createShader(Context context, int shaderType, int shaderRawID)
    {
        int shaderID = GLES30.glCreateShader(shaderType);
        String vertexShader = TextResourceReader.readTextFileFromResource(context, shaderRawID);
        Log.d(TAG, vertexShader);
        GLES30.glShaderSource(shaderID, vertexShader);
        GLES30.glCompileShader(shaderID);
        return shaderID;
    }

    public int createProgram(int vertexShaderID, int fragmentShaderID){
        int program = GLES30.glCreateProgram();
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
        return program;
    }

    public abstract void onCreated(Context context);
    public abstract void onDraw(boolean doCleaR);

    public  void onDestroy(){
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
    }
}
