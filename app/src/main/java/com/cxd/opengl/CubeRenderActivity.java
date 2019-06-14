package com.cxd.opengl;

import android.opengl.GLES30;
import android.opengl.Matrix;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CubeRenderActivity extends BaseRenderActivity {

    final float[] position = {

            //front red
            -0.5f,0.5f,0.5f,    1.0f,0.0f,0.0f,1.0f,
            0.5f,0.5f,0.5f,     1.0f,0.0f,0.0f,1.0f,
            -0.5f, -0.5f,0.5f,  0.3f,0.0f,0.0f,1.0f,
            0.5f, -0.5f, 0.5f,  0.3f,0.0f,0.0f,1.0f,

            //back green
            -0.5f,0.7f,-0.5f,    0.0f,1.0f,0.0f,1.0f,
            0.5f,0.7f,-0.5f,     0.0f,1.0f,0.0f,1.0f,
            -0.5f, -0.3f,-0.5f,  0.0f,0.3f,0.0f,1.0f,
            0.5f, -0.3f, -0.5f,  0.0f,0.3f,0.0f,1.0f,

            //right blue
            0.5f,-0.5f, 0.5f,   0.0f,0.0f,0.3f,1.0f,
            0.5f,-0.5f, -0.5f,  0.0f,0.0f,0.3f,1.0f,
            0.5f, 0.5f, 0.5f,   0.0f,0.0f,1.0f,1.0f,
            0.5f, 0.5f, -0.5f,  0.0f,0.0f,1.0f,1.0f,
    };


    final float[] texture = {//个数与上面的vertex一致，不然后面的会黑
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

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        super.onSurfaceCreated(gl10, eglConfig);
        setShaderRawID(R.raw.cube_vertex_shader, R.raw.cube_fragment_shader);
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT|GLES30.GL_DEPTH_BUFFER_BIT);
        GLES30.glClearColor(0.4f,0.4f,0.4f,1.0f);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glViewport(0,0,1080,1080);

        Matrix.setIdentityM(modelMatrix, 0);
//        Matrix.scaleM(modelMatrix, 0, 0.5f, 0.5f,0.5f);
//        Matrix.translateM(modelMatrix,0,0,0,0.5f);
        Matrix.rotateM(modelMatrix,0, 30,1,0,0);
        //旋转方向：正轴指向自己时逆时针
        Matrix.rotateM(modelMatrix,0, (System.currentTimeMillis()-startTime)/50%360,0,0,1);//顺序:先乘后做--VerTr = M * Mr45 * Mr30 * Vert

        GLES30.glUseProgram(program);

        GLES30.glEnableVertexAttribArray(0);
        posFloatBuf.position(0);//容易踩的坑，小心
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 4*7, posFloatBuf);

        GLES30.glEnableVertexAttribArray(2);
        posFloatBuf.position(3);//容易踩的坑，小心
        GLES30.glVertexAttribPointer(2, 4, GLES30.GL_FLOAT, false, 4*7, posFloatBuf);

        GLES30.glEnableVertexAttribArray(1);
        textFloatBuf.position(0);
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 0, textFloatBuf);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glUniform1i(GLES30.glGetUniformLocation(program, "uTextue"), 0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureID);

        GLES30.glUniformMatrix4fv(GLES30.glGetUniformLocation(program, "modelMatrix"), 1, false, modelMatrix, 0);
        GLES30.glUniformMatrix4fv(GLES30.glGetUniformLocation(program, "projectMatrix"), 1, false, projectMatrix,0);
        GLES30.glUniformMatrix4fv(GLES30.glGetUniformLocation(program, "viewMatrix"), 1, false, viewMatrix,0);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP,0,4);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP,4,4);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP,8,4);

        GLES30.glDisableVertexAttribArray(0);
        GLES30.glDisableVertexAttribArray(1);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        GLES30.glUseProgram(0);
    }
}
