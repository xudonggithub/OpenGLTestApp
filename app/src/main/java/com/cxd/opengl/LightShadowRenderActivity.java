package com.cxd.opengl;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.cxd.render.CubeRender;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class LightShadowRenderActivity extends BaseRenderActivity {
    final float[] lightPos = {1.8f,1.8f,0.0f,1.0f};
    final float[] eyePos = {-3.0f, 0.0f, 3.0f, 1.0f};
    final float[] position = {
            //front red
            -0.5f,0.5f,0.5f,    0.8f,0.0f,0.0f,1.0f,  0.0f,0.0f,1.0f,
            0.5f,0.5f,0.5f,     0.8f,0.0f,0.0f,1.0f,  0.0f,0.0f,1.0f,
            -0.5f, -0.5f,0.5f,  0.8f,0.0f,0.0f,1.0f,  0.0f,0.0f,1.0f,
            0.5f, -0.5f, 0.5f,  0.8f,0.0f,0.0f,1.0f,  0.0f,0.0f,1.0f,

            //back green
            -0.5f,0.7f,-0.5f,    0.0f,0.8f,0.0f,1.0f, 0.0f,0.0f,-1.0f,
            0.5f,0.7f,-0.5f,     0.0f,0.8f,0.0f,1.0f, 0.0f,0.0f,-1.0f,
            -0.5f, -0.3f,-0.5f,  0.0f,0.8f,0.0f,1.0f, 0.0f,0.0f,-1.0f,
            0.5f, -0.3f, -0.5f,  0.0f,0.8f,0.0f,1.0f, 0.0f,0.0f,-1.0f,

            //right blue
            0.5f,-0.5f, 0.5f,   0.0f,0.0f,0.8f,1.0f, 1.0f,0.0f,0.0f,
            0.5f,-0.5f, -0.5f,  0.0f,0.0f,0.8f,1.0f, 1.0f,0.0f,0.0f,
            0.5f, 0.5f, 0.5f,   0.0f,0.0f,0.8f,1.0f, 1.0f,0.0f,0.0f,
            0.5f, 0.5f, -0.5f,  0.0f,0.0f,0.8f,1.0f, 1.0f,0.0f,0.0f,

            //floor
            -10.0f, -0.5f, -10.0f, 1.0f,1.0f,1.0f,1.0f, 0.0f,0.0f,1.0f,
             10.0f, -0.5f, -10.0f, 1.0f,1.0f,1.0f,1.0f, 0.0f,0.0f,1.0f,
            -10.0f, -0.5f,  10.0f, 1.0f,1.0f,1.0f,1.0f, 0.0f,0.0f,1.0f,
             10.0f, -0.5f,  10.0f, 1.0f,1.0f,1.0f,1.0f, 0.0f,0.0f,1.0f,

            //light
            lightPos[0],lightPos[1],lightPos[2], 1.0f,1.0f,1.0f,1.0f, 0.0f,0.0f,1.0f,
    };
    private  final float[] texture = {//个数与上面的vertex一致，不然后面的会黑
            0.0f,0.0f,
            1.0f, 0.0f,
            0.0f,1.0f,
            1.0f, 1.0f,
            //back
            0.0f,1.0f,
            0.0f,0.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            //right
            0.0f,0.0f,
            1.0f, 0.0f,
            0.0f,1.0f,
            1.0f, 1.0f,
    };

    private final FloatBuffer posFloatBuf = getGLBuffer(position);//java和opengl有大小头区别，记得native order
    private final FloatBuffer textFloatBuf = getGLBuffer(texture);
    private int depthVertexShaderID = 0, depthFragmentShaderID = 0, depthProgram = 0, depthTextureID = 0;
    private float[] depthPojectMatrix = new float[16];
    private float[] lightViewMatrix = new float[16];
    private boolean bRotating = false;
    private float rotateDegree = 50f;
    private CubeRender cuberRender = new CubeRender();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bRotating = !bRotating;
                ((Button)v).setText(bRotating?"pause":"rotate");
                if(bRotating)
                    startTime = System.currentTimeMillis();
            }
        });
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        setShaderRawID(R.raw.light_shadow_vertex_shader, R.raw.light_shadow_fragment_shader, 0);// R.raw.light_shadow_geometry_shader);

        super.onSurfaceCreated(gl10, eglConfig);
        depthVertexShaderID = createShader(GLES30.GL_VERTEX_SHADER, R.raw.depth_texture_vertex_shader);
        depthFragmentShaderID = createShader(GLES30.GL_FRAGMENT_SHADER, R.raw.depth_texture_fragment_shader);
        depthProgram = createProgram(depthVertexShaderID, depthFragmentShaderID, 0);
        depthTextureID = createImageTexture(null, 1024, 1204);

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix,0, 30,1,0,0);
        Matrix.rotateM(modelMatrix,0, 30,0,1,0);

        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.setLookAtM(viewMatrix,0, eyePos[0], eyePos[1], eyePos[2], 0, 0, 0, 0, 1.0f, 0);

        Matrix.setIdentityM(lightViewMatrix, 0);
        Matrix.setLookAtM(lightViewMatrix,0, lightPos[0], lightPos[1], lightPos[2], 0, 0, 0, 0, 1.0f, 0);

        Matrix.setIdentityM(projectMatrix, 0);
        Matrix.perspectiveM(projectMatrix, 0, 40.0f, 1.0f,  1f, 1000f);

        Matrix.setIdentityM(depthPojectMatrix, 0);
        Matrix.perspectiveM(depthPojectMatrix, 0, 60.0f, 1.0f,  1f, 1000f);
        Matrix.multiplyMM(depthPojectMatrix, 0, fbMatrix,0,projectMatrix,0);

        Matrix.setIdentityM(lightModelMatrix, 0);

        Matrix.translateM(lightModelMatrix,0, lightPos[0],lightPos[1],lightPos[2]);
        Matrix.scaleM(lightModelMatrix, 0, 0.5f, 0.5f,0.5f);

        cuberRender.onCreated(this);
    }

    @Override
    public void onPause() {
        mGLSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                if(depthVertexShaderID > 0) {
                    GLES30.glDeleteShader(depthVertexShaderID);
                    depthVertexShaderID = 0;
                }
                if(depthFragmentShaderID > 0) {
                    GLES30.glDeleteShader(depthFragmentShaderID);
                    depthFragmentShaderID = 0;
                }
                if(depthProgram > 0) {
                    GLES30.glDeleteProgram(depthProgram);
                    depthProgram = 0;
                }
                if(depthTextureID > 0){
                    GLES30.glDeleteTextures(1, new int[]{depthTextureID}, 0);
                    depthTextureID = 0;
                }
                cuberRender.onDestroy();
            }
        });
        super.onPause();
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT|GLES30.GL_DEPTH_BUFFER_BIT);
        GLES30.glClearColor(0.4f,0.4f,0.4f,1.0f);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glViewport(0,0,1024,1024);

        //cal depth texture
        int[] fBuffer = new int[1];
        GLES30.glGenFramebuffers(1,fBuffer,0);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fBuffer[0]);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, depthTextureID);//注意，这句不能落
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, depthTextureID, 0);
        if(GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER) != GLES30.GL_FRAMEBUFFER_COMPLETE){
            Log.e(TAG, "Create frame buffer failed ,satus:"+GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER) );
        }
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT|GLES30.GL_DEPTH_BUFFER_BIT);
        GLES30.glViewport(0,0,1024,1024);
        GLES30.glUseProgram(depthProgram);

        posFloatBuf.position(0);
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 4*10, posFloatBuf);

        GLES30.glUniform4f(GLES30.glGetUniformLocation(depthProgram, "lightPos"), lightPos[0],lightPos[1],lightPos[2],lightPos[3]);

        Matrix.setIdentityM(modelMatrix, 0);
//        Matrix.rotateM(modelMatrix,0, 30,1,0,0);
        if(bRotating)
            rotateDegree =(rotateDegree + 1)%360;

        Matrix.rotateM(modelMatrix, 0, rotateDegree , 0, 1, 0);
        GLES30.glUniformMatrix4fv(GLES30.glGetUniformLocation(depthProgram, "modelMatrix"), 1, false, modelMatrix, 0);
        GLES30.glUniformMatrix4fv(GLES30.glGetUniformLocation(depthProgram, "projectMatrix"), 1, false, depthPojectMatrix,0);
        GLES30.glUniformMatrix4fv(GLES30.glGetUniformLocation(depthProgram, "viewMatrix"), 1, false, lightViewMatrix,0);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP,0,4);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP,4,4);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP,8,4);


        GLES30.glDisableVertexAttribArray(0);
        GLES30.glUseProgram(0);
        GLES30.glDeleteFramebuffers(1, fBuffer, 0);

//
//        //test depth texture
//        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT|GLES30.GL_DEPTH_BUFFER_BIT);
//        GLES30.glViewport(0,0,1024,1024);
//        GLES30.glUseProgram(program);
//        GLES30.glEnableVertexAttribArray(0);
//        posFloatBuf.position(0);//容易踩的坑，小心
//        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 4*7, posFloatBuf);
//
//        GLES30.glEnableVertexAttribArray(1);
//        textFloatBuf.position(0);
//        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 0, textFloatBuf);
//
//        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
//        GLES30.glUniform1i(GLES30.glGetUniformLocation(program, "uTextue"), 0);
//        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, depthTextureID);
//
//        Matrix.setIdentityM(modelMatrix, 0);
//        GLES30.glUniformMatrix4fv(GLES30.glGetUniformLocation(program, "modelMatrix"), 1, false, modelMatrix, 0);
//        GLES30.glUniformMatrix4fv(GLES30.glGetUniformLocation(program, "projectMatrix"), 1, false, projectMatrix,0);
//        GLES30.glUniformMatrix4fv(GLES30.glGetUniformLocation(program, "viewMatrix"), 1, false, viewMatrix,0);
//
//        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP,0,4);
//
//        GLES30.glDisableVertexAttribArray(0);
//        GLES30.glDisableVertexAttribArray(1);
//        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
//        GLES30.glUseProgram(0);


        GLES30.glUseProgram(program);

        GLES30.glEnableVertexAttribArray(0);
        posFloatBuf.position(0);//容易踩的坑，小心
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 4*10, posFloatBuf);

        //color
        GLES30.glEnableVertexAttribArray(2);
        posFloatBuf.position(3);
        GLES30.glVertexAttribPointer(2, 4, GLES30.GL_FLOAT, false, 4*10, posFloatBuf);
        //normal
        posFloatBuf.position(7);
        GLES30.glEnableVertexAttribArray(3);
        GLES30.glVertexAttribPointer(3, 3, GLES30.GL_FLOAT, false, 4*10, posFloatBuf);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glUniform1i(GLES30.glGetUniformLocation(program, "uDepTextue"), 0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, depthTextureID);

        GLES30.glUniform4f(GLES30.glGetUniformLocation(program, "uLightPos"), lightPos[0],lightPos[1],lightPos[2],lightPos[3]);
        GLES30.glUniformMatrix4fv(GLES30.glGetUniformLocation(program, "uLightViewMatrix"), 1, false, lightViewMatrix, 0);
        GLES30.glUniform2f(GLES30.glGetUniformLocation(program, "uDepTextueSize"), 1024,1024);
        GLES30.glUniform4f(GLES30.glGetUniformLocation(program, "uEyePos"), eyePos[0], eyePos[1], eyePos[2], eyePos[3]);

        GLES30.glUniformMatrix4fv(GLES30.glGetUniformLocation(program, "modelMatrix"), 1, false, modelMatrix, 0);
        GLES30.glUniformMatrix4fv(GLES30.glGetUniformLocation(program, "projectMatrix"), 1, false, projectMatrix,0);
        GLES30.glUniformMatrix4fv(GLES30.glGetUniformLocation(program, "viewMatrix"), 1, false, viewMatrix,0);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP,0,4);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP,4,4);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP,8,4);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP,12,4);

        GLES30.glDrawArrays(GLES30.GL_POINTS, 16, 1);

        GLES30.glDisableVertexAttribArray(0);
        GLES30.glDisableVertexAttribArray(2);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        GLES30.glUseProgram(0);

        //画光源

        cuberRender.setMatrixs(lightModelMatrix, viewMatrix, projectMatrix);
        cuberRender.onDraw(false);

    }

    private float[] lightModelMatrix = new float[16];
}
