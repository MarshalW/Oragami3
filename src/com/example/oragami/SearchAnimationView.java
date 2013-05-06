package com.example.oragami;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.View;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-5-2
 * Time: 下午1:44
 * To change this template use File | Settings | File Templates.
 */
public class SearchAnimationView extends GLSurfaceView implements GLSurfaceView.Renderer {
    protected int width, height;

    protected float ratio, factor, distance = 5;

    private float[] projectionMatrix = new float[16];

    private long duration = 300;

    protected View titleView, contentView;

    protected TextureMesh[] textureMeshs;

    protected ShadowMesh shadowMesh;

    private int animateBackgroundColor = -1;

    public void setAnimateBackgroundColor(int animateBackgroundColor) {
        this.animateBackgroundColor = animateBackgroundColor;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    private AnimatorListenerAdapter animatorListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            for (TextureMesh mesh : textureMeshs) {
                                mesh.clear();
                            }
                            shadowMesh.clear();
                            requestRender();
                        }
                    });
                }
            }, 50);


        }
    };

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public SearchAnimationView(Context context, View titleView, View contentView) {
        super(context);
        this.titleView = titleView;
        this.contentView = contentView;
        this.init();
    }

    private void init() {
        this.setEGLContextClientVersion(2);

        this.setEGLConfigChooser(8, 8, 8, 8, 0, 0);
        this.setZOrderOnTop(true);
        this.getHolder().setFormat(PixelFormat.TRANSPARENT);

        this.setRenderer(this);
        this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(0, 0, 0, 0);

        textureMeshs = new TextureMesh[]{
                new TextureMesh(getContext()),
                new TextureMesh(getContext()),
                new TextureMesh(getContext())
        };

        shadowMesh = new ShadowMesh(getContext());
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        this.width = width;
        this.height = height;

        glViewport(0, 0, width, height);

        ratio = width / (float) height;
        Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, -10f, 10f);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        if (textureMeshs != null
                && !textureMeshs[0].isClear()
                && animateBackgroundColor != -1) {
            glClearColor(
                    Color.red(animateBackgroundColor),
                    Color.green(animateBackgroundColor),
                    Color.blue(animateBackgroundColor),
                    Color.alpha(animateBackgroundColor)
            );
        } else {
            glClearColor(0, 0, 0, 0);
        }


        glClear(GL_COLOR_BUFFER_BIT);

        for (int i = 0; i < textureMeshs.length; i++) {
            textureMeshs[i].draw(projectionMatrix);
        }

        shadowMesh.draw(projectionMatrix);
    }

    public void startAnimation(boolean openIt, Animator.AnimatorListener animatorListener) {
        setTitleBitmaps();
        setContentBitmap();

        ValueAnimator animator = ValueAnimator.ofFloat(openIt ? 0 : 1, openIt ? 1 : 0);
        animator.setDuration(duration);
        animator.addListener(animatorListener);
        animator.addListener(this.animatorListener);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                calculateVertexesAndRender((Float) valueAnimator.getAnimatedValue());
            }
        });

        animator.start();
    }

    protected void calculateVertexesAndRender(final float factor) {
        float h = titleView.getHeight() / 2;
        float _h = 2 * h / height;

        float angle = 90f * factor;
        //上半部分
        float left, top, right, bottom;
        left = -ratio;
        top = 1;
        right = ratio;
        bottom = 1 - _h;
        RectF rect = new RectF(left, top, right, bottom);
        final Vertex[] topVertexArray = rotateAngle(rect, angle, true);

        //下半部分
        float dy = topVertexArray[0].positionY - topVertexArray[1].positionY;
        rect = new RectF(left, top, right, bottom);

        final Vertex[] bottomVertexArray = rotateAngle(rect, angle, false);

        //所有下半部分顶点，下移dy
        for (Vertex v : bottomVertexArray) {
            v.positionY -= dy;
        }

        //如果有下面的图，生成下面的部分
        Vertex[] contentArray = null;

        if (contentView != null) {
            _h = 2f * contentView.getHeight() / height;

            contentArray = new Vertex[]{
                    new Vertex(left, 1, 0),
                    new Vertex(left, 1 - _h, 0),
                    new Vertex(right, 1, 0),
                    new Vertex(right, 1 - _h, 0)
            };

            for (Vertex v : contentArray) {
                v.positionY -= dy * 2;
            }
        }

        final Vertex[] _contentArray = contentArray;

        queueEvent(new Runnable() {
            @Override
            public void run() {
                textureMeshs[0].setVertexes(topVertexArray);
                textureMeshs[1].setVertexes(bottomVertexArray);
                textureMeshs[2].setVertexes(_contentArray);

                shadowMesh.setFactor(factor);
                shadowMesh.setVertexArray(bottomVertexArray);

                requestRender();
            }
        });
    }

    protected Vertex[] rotateAngle(RectF rect, float angle, boolean isTop) {
        float x, y, nx, ny, nz, radian, dx, dy, topLeft, topRight, top, bottom;
//        distance = 5;
        radian = (float) Math.PI / 180 * angle;
        x = Math.abs(rect.width());
        y = Math.abs(rect.height());
        ny = (float) Math.sin(radian) * y;//ny即delta y
        nz = (float) Math.cos(radian) * y;
        nx = nz / (distance - nz) * 2 / x;

        dx = Math.abs(nx);
        dy = Math.abs(y - ny);

        top = 1;
        bottom = 1 - ny;
        topLeft = rect.left + dx;
        topRight = rect.right - dx;

        if (isTop) {
            return new Vertex[]{
                    new Vertex(rect.left, top, 0),
                    new Vertex(topLeft, bottom, 0),
                    new Vertex(rect.right, top, 0),
                    new Vertex(topRight, bottom, 0)
            };
        } else {
            return new Vertex[]{
                    new Vertex(topLeft, top, 0),
                    new Vertex(rect.left, bottom, 0),
                    new Vertex(topRight, top, 0),
                    new Vertex(rect.right, bottom, 0)
            };
        }
    }

    protected void setTitleBitmaps() {
        Bitmap[] titleBitmaps = new Bitmap[2];

        titleView.setDrawingCacheEnabled(true);
        Bitmap bitmap = titleView.getDrawingCache();

        titleBitmaps[0] = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight() / 2);
        titleBitmaps[1] = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight() / 2,
                bitmap.getWidth(), bitmap.getHeight() / 2);

        textureMeshs[0].setTexture(titleBitmaps[0]);
        textureMeshs[1].setTexture(titleBitmaps[1]);

        titleView.setDrawingCacheEnabled(false);
    }

    private void setContentBitmap() {
        if (contentView != null) {
            contentView.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(contentView.getDrawingCache());
            contentView.setDrawingCacheEnabled(false);

            textureMeshs[2].setTexture(bitmap);
        }
    }
}