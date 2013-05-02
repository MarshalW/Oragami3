package com.example.oragami;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-5-2
 * Time: 下午8:53
 * To change this template use File | Settings | File Templates.
 */
public class HorizonalSearchAnimationView extends SearchAnimationView {
    public HorizonalSearchAnimationView(Context context, View titleView, View contentView) {
        super(context, titleView, contentView);
    }

    protected void calculateVertexesAndRender(final float factor) {
        float h = 2f * titleView.getHeight() / height;
        float w = 2f * ratio * (titleView.getWidth()/2) / width;

        float angle = 90f * factor;
        //左部分
        float left, top, right, bottom;
        left = -ratio;
        top = 1;
        right = -ratio + w;
        bottom = 1 - h;
        RectF rect = new RectF(left, top, right, bottom);
        final Vertex[] topVertexArray = rotateAngle(rect, angle, true);

        //右半部分
        float dx = topVertexArray[0].positionX - topVertexArray[2].positionX;
        rect = new RectF(left, top, right, bottom);

        final Vertex[] bottomVertexArray = rotateAngle(rect, angle, false);

        //所有下半部分顶点，右移dx
        for (Vertex v : bottomVertexArray) {
            v.positionX -= dx;
        }

        //如果有下面的图，生成下面内容的部分

        h = 2f * contentView.getHeight() / height;
        w = 2f * ratio * contentView.getWidth() / width;

        final Vertex[] contentArray = new Vertex[]{
                new Vertex(left, 1, 0),
                new Vertex(left, 1 - h, 0),
                new Vertex(left + w, 1, 0),
                new Vertex(left + w, 1 - h, 0)
        };

        for (Vertex v : contentArray) {
            v.positionX += Math.abs(dx) * 2f;
        }

        queueEvent(new Runnable() {
            @Override
            public void run() {
                textureMeshs[0].setVertexes(topVertexArray);
                textureMeshs[1].setVertexes(bottomVertexArray);
                textureMeshs[2].setVertexes(contentArray);

                shadowMesh.setFactor(factor);
                shadowMesh.setVertexArray(bottomVertexArray);

                requestRender();
            }
        });
    }

    protected Vertex[] rotateAngle(RectF rect, float angle, boolean isLeft) {
        float x, y, nx, ny, nz, radian, dx, dy;
        radian = (float) Math.PI / 180 * angle;
        x = Math.abs(rect.width());
        y = Math.abs(rect.height());
        nx = (float) Math.sin(radian) * x;
        nz = (float) Math.cos(radian) * x;
        ny = nz / (distance - nz) * 2 / y;

        dx = Math.abs(nx);
        dy = Math.abs(ny);

        if (isLeft) {
            return new Vertex[]{
                    new Vertex(rect.left, rect.top, 0),
                    new Vertex(rect.left, rect.bottom, 0),
                    new Vertex(rect.left + dx, rect.top - dy, 0),
                    new Vertex(rect.left + dx, rect.bottom + dy, 0)
            };
        } else {
            return new Vertex[]{
                    new Vertex(rect.left, rect.top - dy, 0),
                    new Vertex(rect.left, rect.bottom + dy, 0),
                    new Vertex(rect.left + dx, rect.top, 0),
                    new Vertex(rect.left + dx, rect.bottom, 0)
            };
        }
    }

    protected void setTitleBitmaps() {
        Bitmap[] titleBitmaps = new Bitmap[2];

        titleView.setDrawingCacheEnabled(true);
        Bitmap bitmap = titleView.getDrawingCache();

        titleBitmaps[0] = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth() / 2,
                bitmap.getHeight());
        titleBitmaps[1] = Bitmap.createBitmap(bitmap, bitmap.getWidth() / 2, 0,
                bitmap.getWidth() / 2, bitmap.getHeight());

        textureMeshs[0].setTexture(titleBitmaps[0]);
        textureMeshs[1].setTexture(titleBitmaps[1]);

        titleView.setDrawingCacheEnabled(false);
    }
}
