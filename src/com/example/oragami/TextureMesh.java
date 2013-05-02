package com.example.oragami;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-5-2
 * Time: 上午11:05
 * To change this template use File | Settings | File Templates.
 */
public class TextureMesh {

    private int[] textureId;

    private Bitmap texture;

    private Vertex[] vertexes;

    private Shader shader;

    private FloatBuffer vertexBuffer, textureCoordBuffer;

    public TextureMesh(Context context) {
        shader = new Shader();
        shader.setProgram(context, R.raw.demo_vertex_shader, R.raw.demo_fragment_shader);

        //一个四边形所需顶点的空间: 4个点（x,y,z），float是4字节
        vertexBuffer = ByteBuffer.allocateDirect(3 * 4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        //纹理坐标，针对四边形，4个点（x,y），float是4字节
        textureCoordBuffer = ByteBuffer.allocateDirect(2 * 4 * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureCoordBuffer.put(new float[]{
                0, 0,
                0, 1,
                1, 0,
                1, 1
        });
    }

    public void draw(float[] projectionMatrix) {
        if (vertexes == null) {
            return;
        }

        initBuffer();

        this.shader.useProgram();

        if (textureId == null) {
            textureId = new int[1];

            glGenTextures(1, textureId, 0);

            glBindTexture(GL_TEXTURE_2D, textureId[0]);

            glTexParameterf(GL_TEXTURE_2D,
                    GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameterf(GL_TEXTURE_2D,
                    GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameterf(GL_TEXTURE_2D,
                    GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameterf(GL_TEXTURE_2D,
                    GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

            glEnable(GL_TEXTURE_2D);
            GLUtils.texImage2D(GL_TEXTURE_2D, 0, texture, 0);
            glDisable(GL_TEXTURE_2D);

            this.texture.recycle();
            this.texture = null;
        } else {
            glBindTexture(GL_TEXTURE_2D, textureId[0]);
        }

        int aTextureCoord = this.shader.getHandle("aTextureCoord");
        glVertexAttribPointer(aTextureCoord, 2, GL_FLOAT, false,
                0, textureCoordBuffer);
        glEnableVertexAttribArray(aTextureCoord);

        glUniformMatrix4fv(shader.getHandle("uProjectionM"), 1, false, projectionMatrix, 0);

        int aPosition = this.shader.getHandle("aPosition");
        glVertexAttribPointer(aPosition, 3, GL_FLOAT, false,
                3 * 4, vertexBuffer);
        glEnableVertexAttribArray(aPosition);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    }

    public void setTexture(Bitmap bitmap) {
        this.texture = bitmap;
    }

    public void setVertexes(Vertex[] vertexes) {
        this.vertexes = vertexes;
    }

    public void clear() {
        if(textureId!=null){
            glDeleteTextures(1, textureId, 0);
        }
        textureId = null;
        vertexes = null;
    }

    private void initBuffer() {
        this.vertexBuffer.clear();

        for (Vertex v : vertexes) {
            this.vertexBuffer.put(v.getPosition());
        }

        this.vertexBuffer.position(0);

        textureCoordBuffer.position(0);
    }
}
