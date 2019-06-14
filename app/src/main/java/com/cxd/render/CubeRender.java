package com.cxd.render;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.Matrix;

import com.cxd.opengl.R;

import java.nio.FloatBuffer;

public class CubeRender extends BaseRender {
    float position[] = {
            -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
            0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
            0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
            0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
            -0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
            -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,

            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
            0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
            -0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,

            -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f,  0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f, -0.5f,  0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,

            0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,
            0.5f,  0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
            0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
            0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
            0.5f, -0.5f,  0.5f,  1.0f,  0.0f,  0.0f,
            0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,

            -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
            0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
            0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
            0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
            -0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,

            -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,
            0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
            -0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
            -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f
    };
    private final FloatBuffer posFloatBuf = getGLBuffer(position);

    @Override
    public void onCreated(Context context) {
        vertexShaderID = createShader(context, GLES30.GL_VERTEX_SHADER, R.raw.cube_vertex_shader);
        fragmentShaderID = createShader(context, GLES30.GL_FRAGMENT_SHADER, R.raw.cube_fragment_shader);
        program = createProgram(vertexShaderID, fragmentShaderID);

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.scaleM(modelMatrix, 0, 0.5f, 0.5f,0.5f);
        Matrix.translateM(modelMatrix,0, 2.8f,5.0f,0.0f);

        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.setLookAtM(viewMatrix,0, -1.7f, -1.7f, 3, 0, 0, 0, 0, 1.0f, 0);

        Matrix.setIdentityM(projectMatrix, 0);
        Matrix.perspectiveM(projectMatrix, 0, 40f, 1,  1f, 1000f);
    }
    public void onDraw(boolean doCleaR)
    {
        if(doCleaR){
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT|GLES30.GL_DEPTH_BUFFER_BIT);
            GLES30.glClearColor(0.4f,0.4f,0.4f,1.0f);
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            GLES30.glViewport(0,0,1080,1080);
        }



        GLES30.glUseProgram(program);
        //position
        GLES30.glEnableVertexAttribArray(0);
        posFloatBuf.position(0);
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 4*6, posFloatBuf);
        //normal
        GLES30.glEnableVertexAttribArray(1);
        posFloatBuf.position(3);//容易踩的坑，小心
        GLES30.glVertexAttribPointer(1, 3, GLES30.GL_FLOAT, false, 4*6, posFloatBuf);


//        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
//        GLES30.glUniform1i(GLES30.glGetUniformLocation(program, "uTextue"), 0);
//        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureID);

        GLES30.glUniformMatrix4fv(GLES30.glGetUniformLocation(program, "modelMatrix"), 1, false, modelMatrix, 0);
        GLES30.glUniformMatrix4fv(GLES30.glGetUniformLocation(program, "projectMatrix"), 1, false, projectMatrix,0);
        GLES30.glUniformMatrix4fv(GLES30.glGetUniformLocation(program, "viewMatrix"), 1, false, viewMatrix,0);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP,0,36);

        GLES30.glDisableVertexAttribArray(0);
        GLES30.glDisableVertexAttribArray(1);
        GLES30.glUseProgram(0);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
