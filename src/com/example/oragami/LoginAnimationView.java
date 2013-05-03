package com.example.oragami;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import java.util.List;

import static android.opengl.GLES20.*;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-5-3
 * Time: 上午10:09
 * To change this template use File | Settings | File Templates.
 */
public class LoginAnimationView extends GLSurfaceView implements GLSurfaceView.Renderer {

    private static final int EMPTY_VALUE = -100;

    protected int width, height;

    protected float ratio, factor, distance = 5;

    private float[] projectionMatrix = new float[16];

    private long duration = 400;

    private TextureMesh[] textureMeshs;

    private ShadowMesh shadowMesh;

    private long fadeDelay = 50;

    private List<LoginViewController.ViewPair> viewPairList;

    private View contentView;

    private AnimatorListenerAdapter animatorListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            contentView.setVisibility(View.VISIBLE);

            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            for (TextureMesh mesh : textureMeshs) {
                                mesh.clear();
                            }
                            requestRender();
                        }
                    });
                }
            }, fadeDelay);

        }

        @Override
        public void onAnimationStart(Animator animation) {
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    contentView.setVisibility(View.INVISIBLE);
                }
            }, fadeDelay);
        }
    };

    public LoginAnimationView(Context context, View contentView, List<LoginViewController.ViewPair> viewPairList) {
        super(context);

        this.contentView = contentView;
        this.viewPairList = viewPairList;
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

        textureMeshs = new TextureMesh[viewPairList.size() + 2];
        for (int i = 0; i < textureMeshs.length; i++) {
            textureMeshs[i] = new TextureMesh(getContext());
        }

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
        glClear(GL_COLOR_BUFFER_BIT);

        for (TextureMesh mesh : textureMeshs) {
            mesh.draw(projectionMatrix);
        }

        shadowMesh.draw(projectionMatrix);
    }

    public void startAnimation(final LoginViewController.ViewPair choosePair,
                               Animator.AnimatorListener animatorListener) {
        final LoginViewController.ViewPair currentPair = getCurrentViewPair();
        int[] ofFloat = null;
        if (currentPair == null) {
            ofFloat = new int[]{0, 1};
        } else if (currentPair == choosePair) {
            ofFloat = new int[]{1, 0};
        } else {
            ofFloat = new int[]{-1, 1};
        }
        ValueAnimator animator = ValueAnimator.ofFloat(ofFloat[0], ofFloat[1]);
        animator.setDuration(duration);
        animator.setInterpolator(new LinearInterpolator());

        animator.addListener(animatorListener);
        animator.addListener(this.animatorListener);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                calculateVertexesAndRender((Float) valueAnimator.getAnimatedValue(), choosePair, currentPair);
            }
        });

        lastFactor = EMPTY_VALUE;

        animator.start();
    }

    private float lastFactor;

    private void calculateVertexesAndRender(float factor, LoginViewController.ViewPair choosePair,
                                            LoginViewController.ViewPair currentPair) {
        if (lastFactor == factor) {
            return;
        }

        LoginViewController.ViewPair viewPair = factor >= 0 ? choosePair : currentPair;

        //取bitmap
        if (lastFactor == EMPTY_VALUE) {
            if (choosePair != null && choosePair.content.getVisibility() == VISIBLE) {
                choosePair.snapBitmap();
            }
            if (currentPair != null && currentPair.content.getVisibility() == VISIBLE) {
                currentPair.snapBitmap();
            }
        }

        //设置纹理
        if (lastFactor == EMPTY_VALUE || (lastFactor < 0 && factor >= 0)) {
            //清空之前的纹理数据
            for (TextureMesh mesh : textureMeshs) {
                mesh.clear();
            }
            for (int i = 0, j = 0; i < viewPairList.size(); i++, j++) {
                LoginViewController.ViewPair p = viewPairList.get(i);
                textureMeshs[j].setTexture(Bitmap.createBitmap(p.titleBitmap));
                if (p == viewPair) {
                    j++;
                    textureMeshs[j].setTexture(Bitmap.createBitmap(p.contentBitmaps[0]));
                    j++;
                    textureMeshs[j].setTexture(Bitmap.createBitmap(p.contentBitmaps[1]));
                }
            }
        }

        _calculateVertexesAndRender(Math.abs(factor), viewPair);
        lastFactor = factor;
    }

    private void _calculateVertexesAndRender(final float factor, LoginViewController.ViewPair pair) {
        final Vertex[][] vertexes = new Vertex[textureMeshs.length][];
        float bottom = -1;
        Vertex[] shadowVertexes = null;
        for (int i = viewPairList.size() - 1, j = textureMeshs.length - 1; i >= 0; i--, j--) {
            LoginViewController.ViewPair p = viewPairList.get(i);
            if (p == pair) {
                float angle = 90f * factor;
                RectF rect = new RectF(0, (p.content.getHeight() / 2f) / (height / 2f), ratio * 2, 0);
                //下半部分
                vertexes[j] = getRotatedVertexes(rect, angle, true, bottom);
                bottom += getHeight(vertexes[j]);
                shadowVertexes = vertexes[j];
                j--;

                //上半部分
                vertexes[j] = getRotatedVertexes(rect, angle, false, bottom);
                bottom += getHeight(vertexes[j]);
                j--;
            }
            vertexes[j] = getRectVertexes(p.title, bottom);
            bottom += getHeight(vertexes[j]);
        }

        final Vertex[] _shadowVertexes = shadowVertexes;

        queueEvent(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < vertexes.length; i++) {
                    textureMeshs[i].setVertexes(vertexes[i]);
                }
                shadowMesh.setVertexArray(_shadowVertexes);
                shadowMesh.setFactor(factor);
                requestRender();
            }
        });
    }

    private Vertex[] getRectVertexes(View view, float currentBottom) {
        float h, w;

        w = 2 * ratio;
        h = view.getHeight() * 1f / (height / 2f);

        Vertex[] vertexes = new Vertex[]{
                new Vertex(0, h, 0),
                new Vertex(0, 0, 0),
                new Vertex(w, h, 0),
                new Vertex(w, 0, 0)
        };

        for (Vertex v : vertexes) {
            v.translate(-ratio, currentBottom);
        }

        return vertexes;
    }

    private Vertex[] getRotatedVertexes(RectF rect, float angle, boolean isBottom, float currentBottom) {
        float x, y, nx, ny, nz, radian, dx, dy;
        radian = (float) Math.PI / 180 * angle;
        x = Math.abs(rect.width());
        y = Math.abs(rect.height());
        ny = (float) Math.sin(radian) * y;
        nz = (float) Math.cos(radian) * y;
        nx = nz / (distance - nz) * 2 / x;

        //获取变化的x，y长度
        dx = Math.abs(nx);
        dy = Math.abs(ny);

        Vertex[] vertexes = new Vertex[]{
                new Vertex(0, dy, 0),
                new Vertex(0, 0, 0),
                new Vertex(ratio * 2, dy, 0),
                new Vertex(ratio * 2, 0, 0)
        };

        //调整顶点坐标的x部分
        if (isBottom) {
            vertexes[0].translate(dx, 0);
            vertexes[2].translate(-dx, 0);
        } else {
            vertexes[1].translate(dx, 0);
            vertexes[3].translate(-dx, 0);
        }

        //整体调整坐标
        for (Vertex v : vertexes) {
            v.translate(-ratio, currentBottom);
        }

        return vertexes;
    }


    private float getHeight(Vertex[] vertexes) {
        return Math.abs(vertexes[0].positionY - vertexes[1].positionY);
    }

    private LoginViewController.ViewPair getCurrentViewPair() {
        for (LoginViewController.ViewPair viewPair : viewPairList) {
            if (viewPair.content.getVisibility() == View.VISIBLE) {
                return viewPair;
            }
        }
        return null;
    }
}
