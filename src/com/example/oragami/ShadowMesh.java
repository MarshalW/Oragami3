package com.example.oragami;

import android.content.Context;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-5-2
 * Time: 下午6:24
 * To change this template use File | Settings | File Templates.
 */
public class ShadowMesh {

    private Shader shader;

    private FloatBuffer vertexBuffer, shadowColorBuffer;

    private float factor;

    private Vertex[] vertexArray;

    public ShadowMesh(Context context) {
        shader = new Shader();
        shader.setProgram(context, R.raw.shadow_vertex_shader, R.raw.shadow_fragment_shader);

        //一个四边形所需顶点的空间: 4个点（x,y,z），float是4字节
        vertexBuffer = ByteBuffer.allocateDirect(3 * 4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        //顶点颜色，每个顶点4个颜色值，4个顶点，float是4字节
        shadowColorBuffer = ByteBuffer.allocateDirect(4 * 4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }

    public void setVertexArray(Vertex[] vertexArray) {
        this.vertexArray = vertexArray;
    }

    public void draw(float[] projectionMatrix) {
        if (vertexArray == null) {
            return;
        }

        this.setVertexBuffer(vertexArray);
        this.setShadowColor(factor);

        this.shader.useProgram();

        glUniformMatrix4fv(shader.getHandle("uProjectionM"), 1, false, projectionMatrix, 0);

        int aPosition = this.shader.getHandle("aPosition");
        glVertexAttribPointer(aPosition, 3, GL_FLOAT, false,
                3 * 4, vertexBuffer);
        glEnableVertexAttribArray(aPosition);

        //设置顶点颜色
        glVertexAttribPointer(shader.getHandle("aColor"), 4, GL_FLOAT, false, 0,
                this.shadowColorBuffer);
        glEnableVertexAttribArray(shader.getHandle("aColor"));

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    }

    private void setVertexBuffer(Vertex[] vertexArray) {
        this.vertexBuffer.clear();

        for (Vertex v : vertexArray) {
            this.vertexBuffer.put(v.getPosition());
        }

        this.vertexBuffer.position(0);
    }

    private void setShadowColor(float factor) {
        shadowColorBuffer.clear();

        shadowColorBuffer.put(new float[]{
                0, 0, 0, 1 - factor,
                0, 0, 0, 0,
                0, 0, 0, 1 - factor,
                0, 0, 0, 0
        });

        shadowColorBuffer.position(0);
    }

    public void clear(){
        vertexArray=null;
    }
}
