package me.sheepyang.loadingview.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by SheepYang on 2016/9/6.
 */
public class LoadingView extends View {
    private Paint mPaint;
    private int[] mColors = new int[]{0xB0FAED57, 0xB0ED2F12, 0xB0E438B0, 0xB03FB3FA};
    private int mWidth;
    private int mHeight;
    private int mStep;
    //静止状态
    private final int STATUS_STILL = 0;
    //加载状态
    private final int STATUS_LOADING = 1;
    //所有动画
    private List<Animator> mAnimList = new ArrayList<>();
    //最大间隔时长
    private final int MAX_DURATION = 3000;
    //最小间隔时长
    private final int MIN_DURATION = 500;
    //动画间隔时长
    private int mDuration = MAX_DURATION;
    //动画当前状态
    private int mStatus = STATUS_STILL;
    //线条最大长度
    private final int MAX_LINE_LENGTH = dp2px(getContext(), 120);
    //线条最短长度
    private final int MIN_LINE_LENGTH = dp2px(getContext(), 40);
    //线条总长度
    private int mEntireLineLength = MIN_LINE_LENGTH;
    //线条长度
    private float mLineLength;
    //线条长度
    private float mLineLength2;
    //Canvas旋转角度
    private int mCanvasAngle;
    //圆半径
    private int mCircleRadius;
    //Canvas起始旋转角度
    private final int CANVAS_ROTATE_ANGLE = 60;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColors[0]);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        initData();
    }

    private void initData() {
        mCanvasAngle = CANVAS_ROTATE_ANGLE;
        mLineLength = mEntireLineLength;
        mLineLength2 = mLineLength;
        mCircleRadius = mEntireLineLength / 5;
        mPaint.setStrokeWidth(mCircleRadius * 2);
        mStep = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mStep % 4) {
            case 0:
                for (int i = 0; i < mColors.length; i++) {
                    mPaint.setColor(mColors[i]);
                    drawCRLC1(canvas, mWidth / 2, mHeight / 2, mWidth / 2 - mLineLength / 2.2f, mHeight / 2 + mLineLength, mPaint, mCanvasAngle + i * 90);
                    drawCRLC2(canvas, mWidth / 2, mHeight / 2, mWidth / 2 - mLineLength2 / 2.2f, mHeight / 2 + mLineLength2, mPaint, mCanvasAngle + i * 90);
                }
                break;
            default:
                break;
        }
    }

    private void drawCRLC1(Canvas canvas, float startX, float startY, float stopX, float stopY, @NonNull Paint paint, int rotate) {
        canvas.rotate(rotate, mWidth / 2, mHeight / 2);// 绕中心点旋转
        canvas.drawLine(startX, startY, stopX, stopY, paint);
        canvas.rotate(-rotate, mWidth / 2, mHeight / 2);
    }

    private void drawCRLC2(Canvas canvas, float startX, float startY, float stopX, float stopY, @NonNull Paint paint, int rotate) {
        canvas.rotate(rotate, mWidth / 2, mHeight / 2);// 绕中心点旋转
        canvas.rotate(90, mWidth / 2 - mEntireLineLength, mHeight / 2 + mEntireLineLength);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
        canvas.rotate(-90, mWidth / 2 - mEntireLineLength, mHeight / 2 + mEntireLineLength);
        canvas.rotate(-rotate, mWidth / 2, mHeight / 2);
    }

    private void drawCR(Canvas canvas, float x, float y, @NonNull Paint paint, int rotate) {
        canvas.rotate(rotate, mWidth / 2, mHeight / 2);
        canvas.drawCircle(x, y, mCircleRadius, paint);
        canvas.rotate(-rotate, mWidth / 2, mHeight / 2);
    }

    /**
     * Animation1
     * 动画1
     * Canvas Rotate Line Change
     * 画布旋转及线条变化动画
     */
    private void startCRLCAnim2() {

        Collection<Animator> animList = new ArrayList<>();

        ValueAnimator canvasRotateAnim = ValueAnimator.ofInt(CANVAS_ROTATE_ANGLE + 0, CANVAS_ROTATE_ANGLE + 360);
        canvasRotateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCanvasAngle = (int) animation.getAnimatedValue();
            }
        });

//        animList.add(canvasRotateAnim);

        ValueAnimator lineWidthAnim = ValueAnimator.ofFloat(mEntireLineLength, 0);
        lineWidthAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLineLength = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        animList.add(lineWidthAnim);

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setDuration(mDuration);
        animationSet.playTogether(animList);
        animationSet.setInterpolator(new LinearInterpolator());
        animationSet.addListener(new AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d("@=>", "动画1结束");
                if (mStatus == STATUS_LOADING) {
//                    mStep++;
                    startCRLCAnim1();
                }
            }
        });
        animationSet.start();

        mAnimList.add(animationSet);
    }

    /**
     * Animation1
     * 动画1
     * Canvas Rotate Line Change
     * 画布旋转及线条变化动画
     */
    private void startCRLCAnim1() {

        Collection<Animator> animList = new ArrayList<>();

        ValueAnimator canvasRotateAnim = ValueAnimator.ofInt(CANVAS_ROTATE_ANGLE + 0, CANVAS_ROTATE_ANGLE + 360);
        canvasRotateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCanvasAngle = (int) animation.getAnimatedValue();
            }
        });

//        animList.add(canvasRotateAnim);

        ValueAnimator lineWidthAnim = ValueAnimator.ofFloat(mEntireLineLength, 0);
        lineWidthAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLineLength2 = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        animList.add(lineWidthAnim);

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setDuration(mDuration);
        animationSet.playTogether(animList);
        animationSet.setInterpolator(new LinearInterpolator());
        animationSet.addListener(new AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d("@=>", "动画1结束");
                if (mStatus == STATUS_LOADING) {
                    mStep++;
                    startCRLCAnim2();
                }
            }
        });
        animationSet.start();

        mAnimList.add(animationSet);
    }

    /**
     * Animation2
     * 动画2
     * Canvas Rotate
     * 画布旋转动画
     */
    private void startCRAnim() {
        ValueAnimator canvasRotateAnim = ValueAnimator.ofInt(mCanvasAngle, mCanvasAngle + 180);
        canvasRotateAnim.setDuration(mDuration / 2);
        canvasRotateAnim.setInterpolator(new LinearInterpolator());
        canvasRotateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCanvasAngle = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        canvasRotateAnim.addListener(new AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d("@=>", "动画2结束");
                if (mStatus == STATUS_LOADING) {
                    mStep++;
//                    startCRCCAnim();
                }
            }
        });
        canvasRotateAnim.start();

        mAnimList.add(canvasRotateAnim);
    }

    public void start() {
        if (mStatus == STATUS_STILL) {
            mAnimList.clear();
            mStatus = STATUS_LOADING;
            startCRLCAnim1();
        }
    }

    public void reset() {
        if (mStatus == STATUS_LOADING) {
            mStatus = STATUS_STILL;
            for (Animator anim : mAnimList) {
                anim.cancel();
            }
        }
        initData();
        invalidate();
    }

    private int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
