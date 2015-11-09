package cn.xin.pulltorefreshlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorUpdateListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;


public class PulltoRefreshLayout extends FrameLayout {

    public static final String TAG = "PulltoRefreshLayout";
    public static final Integer MODE_RESET = 0;
    public static final Integer MODE_UP = 1;
    public static final Integer MODE_DOWN = 2;

    private PulltoRefreshHeadView materialHeadView;

    private PulltoRefreshFootView pulltoRefreshFootView;


    private int waveType;

    private int DEFAULT_WAVE_HEIGHT = 140;

    private int HIGHER_WAVE_HEIGHT = 180;

    private int DEFAULT_HEAD_HEIGHT = 70;

    private int hIGHER_HEAD_HEIGHT = 100;

    private int DEFAULT_PROGRESS_SIZE = 50;

    private int BIG_PROGRESS_SIZE = 60;

    private int PROGRESS_STOKE_WIDTH = 3;

    private int waveColor;

    protected float mWaveHeight;

    protected float mHeadHeight;

    private View mChildView;

    protected FrameLayout mHeadLayout;
    protected FrameLayout mFootLayout;

    protected boolean isRefreshing;

    private float mTouchY;

    private float mCurrentY;

    private DecelerateInterpolator decelerateInterpolator;

    private float headHeight;

    private float waveHeight;

    private int[] colorSchemeColors;

    private int colorsId;

    private int progressTextColor;

    private int progressValue,progressMax;

    private boolean showArrow;

    private int textType;

    private PulltorefreshRefreshListener refreshListener;

    private boolean showProgressBg;

    private int progressBg;

    private boolean isShowWave;

    private int progressSizeType;

    private int progressSize = 0;

//    private boolean isLoadMoreing;

    private boolean isLoadMore;
    private int currentMode = MODE_RESET;


    public PulltoRefreshLayout(Context context) {
        this(context, null, 0);
    }

    public PulltoRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PulltoRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defstyleAttr) {
        if (isInEditMode()) {
            return;
        }

        if (getChildCount() > 1) {
            throw new RuntimeException("can only have one child widget");
        }

        decelerateInterpolator = new DecelerateInterpolator(10);


        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.PulltoRefreshLayout, defstyleAttr, 0);
        /**attrs for materialWaveView*/
        waveType = t.getInt(R.styleable.PulltoRefreshLayout_wave_height_type, 0);
        if (waveType == 0) {
            headHeight = DEFAULT_HEAD_HEIGHT;
            waveHeight = DEFAULT_WAVE_HEIGHT;
            MaterialWaveView.DefaulHeadHeight = DEFAULT_HEAD_HEIGHT;
            MaterialWaveView.DefaulWaveHeight = DEFAULT_WAVE_HEIGHT;
//            MaterialFootWaveView.DefaulHeadHeight = DEFAULT_HEAD_HEIGHT;
//            MaterialFootWaveView.DefaulWaveHeight = DEFAULT_WAVE_HEIGHT;
        } else {
            headHeight = hIGHER_HEAD_HEIGHT;
            waveHeight = HIGHER_WAVE_HEIGHT;
            MaterialWaveView.DefaulHeadHeight = hIGHER_HEAD_HEIGHT;
            MaterialWaveView.DefaulWaveHeight = HIGHER_WAVE_HEIGHT;
//            MaterialFootWaveView.DefaulHeadHeight = hIGHER_HEAD_HEIGHT;
//            MaterialFootWaveView.DefaulWaveHeight = HIGHER_WAVE_HEIGHT;
        }
        waveColor = t.getColor(R.styleable.PulltoRefreshLayout_wave_color, Color.WHITE);
        isShowWave = t.getBoolean(R.styleable.PulltoRefreshLayout_wave_show, true);

        /**attrs for circleprogressbar*/
        colorsId = t.getResourceId(R.styleable.PulltoRefreshLayout_progress_colors, R.array.material_colors);
        colorSchemeColors = context.getResources().getIntArray(colorsId);
        showArrow = t.getBoolean(R.styleable.PulltoRefreshLayout_progress_show_arrow, true);
        textType = t.getInt(R.styleable.PulltoRefreshLayout_progress_text_visibility, 1);
        progressTextColor = t.getColor(R.styleable.PulltoRefreshLayout_progress_text_color, Color.BLACK);
        progressValue = t.getInteger(R.styleable.PulltoRefreshLayout_progress_value, 0);
        progressMax = t.getInteger(R.styleable.PulltoRefreshLayout_progress_max_value, 100);
        showProgressBg = t.getBoolean(R.styleable.PulltoRefreshLayout_progress_show_circle_backgroud, true);
        progressBg = t.getColor(R.styleable.PulltoRefreshLayout_progress_backgroud_color, CircleProgressBar.DEFAULT_CIRCLE_BG_LIGHT);
        progressSizeType = t.getInt(R.styleable.PulltoRefreshLayout_progress_size_type,0);
        if(progressSizeType == 0)
        {
            progressSize = DEFAULT_PROGRESS_SIZE;
        }else {
            progressSize = BIG_PROGRESS_SIZE;
        }
        isLoadMore = t.getBoolean(R.styleable.PulltoRefreshLayout_isLoadMore,false);
        t.recycle();
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        Context context = getContext();

        FrameLayout headViewLayout = new FrameLayout(context);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        layoutParams.gravity = Gravity.TOP;
        headViewLayout.setLayoutParams(layoutParams);

        mHeadLayout = headViewLayout;
        this.addView(mHeadLayout);

        FrameLayout footViewLayout = new FrameLayout(context);
        LayoutParams layoutParams1 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        layoutParams1.gravity = Gravity.BOTTOM;
        footViewLayout.setLayoutParams(layoutParams1);
        mFootLayout = footViewLayout;
//        mFootLayout.setForeground(getResources().getDrawable(R.color.material_red));

        this.addView(mFootLayout);

        mChildView = getChildAt(0);
        if (mChildView == null) {
            return;
        }

        setWaveHeight(Utils.dip2px(context, waveHeight));
        setHeaderHeight(Utils.dip2px(context, headHeight));

        materialHeadView = new PulltoRefreshHeadView(context);
        materialHeadView.setWaveColor(isShowWave ? waveColor : Color.WHITE);
        materialHeadView.showProgressArrow(showArrow);
        materialHeadView.setProgressSize(progressSize);
        materialHeadView.setProgressColors(colorSchemeColors);
        materialHeadView.setProgressStokeWidth(PROGRESS_STOKE_WIDTH);
        materialHeadView.setTextType(textType);
        materialHeadView.setProgressTextColor(progressTextColor);
        materialHeadView.setProgressValue(progressValue);
        materialHeadView.setProgressValueMax(progressMax);
        materialHeadView.setIsProgressBg(showProgressBg);
        materialHeadView.setProgressBg(progressBg);
        setHeaderView(materialHeadView);

        pulltoRefreshFootView = new PulltoRefreshFootView(context);
//        pulltoRefreshFootView.setWaveColor(Color.RED);
        pulltoRefreshFootView.showProgressArrow(showArrow);
        pulltoRefreshFootView.setProgressSize(progressSize);
        pulltoRefreshFootView.setProgressColors(colorSchemeColors);
        pulltoRefreshFootView.setProgressStokeWidth(PROGRESS_STOKE_WIDTH);
        pulltoRefreshFootView.setTextType(textType);
        pulltoRefreshFootView.setProgressTextColor(progressTextColor);
        pulltoRefreshFootView.setProgressValue(progressValue);
        pulltoRefreshFootView.setProgressValueMax(progressMax);
        pulltoRefreshFootView.setIsProgressBg(showProgressBg);
        pulltoRefreshFootView.setProgressBg(progressBg);
        setFooderView(pulltoRefreshFootView);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isRefreshing) return true;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchY = ev.getY();
                mCurrentY = mTouchY;
                break;
            case MotionEvent.ACTION_MOVE:
                float currentY = ev.getY();
                float dy = currentY - mTouchY;
                Log.i(TAG,"onInterceptTouchEvent ----"+",currentY = "+currentY+",dy = "+dy);
                if (dy >= 1f && !canChildScrollUp()) {
                    if (materialHeadView != null) {
                        materialHeadView.onBegin(this);
                        currentMode = MODE_DOWN;
                    }
                    return true;
                }
                else if(dy<= -1f && !canChildScrollDown()&&isLoadMore)
                {
                    if(pulltoRefreshFootView != null)
                    {
//                        soveLoadMoreLogic();
//                        isLoadMoreing = true;
                        pulltoRefreshFootView.onBegin(this);
                        currentMode = MODE_UP;
                    }
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private void soveLoadMoreLogic() {
//        isLoadMoreing = true;
//        materialFoodView.setVisibility(View.VISIBLE);
        pulltoRefreshFootView.onBegin(this);
        pulltoRefreshFootView.onRefreshing(this);
        if(refreshListener != null)
        {
            refreshListener.onRefreshLoadMore(PulltoRefreshLayout.this);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (isRefreshing) {
            return super.onTouchEvent(e);
        }

        switch (e.getAction()) {

            case MotionEvent.ACTION_MOVE:
                mCurrentY = e.getY();
                float dy = mCurrentY - mTouchY;
                if (mChildView != null) {
                    if (currentMode == MODE_DOWN){
                        dy = Math.min(mWaveHeight * 2, dy);
                        dy = Math.max(0, dy);
                        float offsetY = decelerateInterpolator.getInterpolation(dy / mWaveHeight / 2) * dy / 2;
                        float fraction = offsetY / mHeadHeight;
                        mHeadLayout.getLayoutParams().height = (int) offsetY;
                        mHeadLayout.requestLayout();
                        Log.i(TAG, "ACTION_MOVE currentMode =" + currentMode + "offsetY = " + offsetY + "mHeadHeight = " + mHeadHeight +
                                " fraction =" + fraction);

                        if (materialHeadView != null) {
                            materialHeadView.onPull(this, fraction);

                        }
                        ViewCompat.setTranslationY(mChildView, offsetY);
                    }else if (currentMode == MODE_UP){
                        if (dy < -1f){

                            dy = Math.abs(dy);
                            dy = Math.min(mWaveHeight * 2, dy);
                            dy = Math.max(0, dy);
                            float offsetY = decelerateInterpolator.getInterpolation(dy / mWaveHeight / 2) * dy / 2;
                            float fraction = Math.abs(offsetY / mHeadHeight);

                            mFootLayout.getLayoutParams().height = (int) offsetY;
                            mFootLayout.requestLayout();
                            Log.i(TAG, "ACTION_MOVE currentMode =" + currentMode + "offsetY = " + offsetY + "mHeadHeight = " + mHeadHeight + " fraction =" + fraction);
                            if (pulltoRefreshFootView != null) {
                                pulltoRefreshFootView.onPull(this, fraction);
                            }

                            ViewCompat.setTranslationY(mChildView, -offsetY);
                        }
                    }

                }
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mChildView != null) {
                    if (currentMode == MODE_DOWN) {
                        if (ViewCompat.getTranslationY(mChildView) > 1f) {

                            if (ViewCompat.getTranslationY(mChildView) >= mHeadHeight) {
                                createAnimatorTranslationY(mChildView, mHeadHeight, mHeadLayout);
                                updateListener();
                            } else {
                                createAnimatorTranslationY(mChildView, 0, mHeadLayout);
                                resetPulltoRefresh();
                            }
                        } else {
                            resetPulltoRefresh();
                        }
                    } else if (currentMode == MODE_UP) {
                        if (ViewCompat.getTranslationY(mChildView) < -1f) {
                            if (Math.abs(ViewCompat.getTranslationY(mChildView)) >= mHeadHeight) {
                                createAnimatorTranslationY(mChildView, -mHeadHeight, mFootLayout);
                                updateLoadMoreListener();
                                materialHeadView.onComlete(PulltoRefreshLayout.this);
                            } else {
                                createAnimatorTranslationY(mChildView, 0, mFootLayout);
                                resetPulltoRefresh();
                            }
                        } else {
                            resetPulltoRefresh();
                        }
                    }
                    Log.i(TAG, "ACTION_UP currentMode = " + currentMode + ",ViewCompat.getTranslationY(mChildView) = " + ViewCompat.getTranslationY(mChildView) +
                            ",mHeadHeight = " + mHeadHeight);

                    currentMode = MODE_RESET;
                }
                return true;
        }
        return super.onTouchEvent(e);
    }

    private void resetPulltoRefresh() {
        materialHeadView.onComlete(PulltoRefreshLayout.this);
        pulltoRefreshFootView.onComlete(PulltoRefreshLayout.this);
    }


    public void autoRefreshLoadMore()
    {
        if(isLoadMore)
        {
            soveLoadMoreLogic();
        }else
        {
            throw new RuntimeException("you must  setLoadMore ture");
        }
    }

    public void updateListener()
    {
        isRefreshing = true;

        if (materialHeadView != null) {
            materialHeadView.onRefreshing(PulltoRefreshLayout.this);
        }

        if (refreshListener != null) {
            refreshListener.onRefresh(PulltoRefreshLayout.this);
        }

    }

    public void updateLoadMoreListener()
    {
        isRefreshing = true;

        if (pulltoRefreshFootView != null) {
            pulltoRefreshFootView.onRefreshing(PulltoRefreshLayout.this);
        }

        if (refreshListener != null) {
            refreshListener.onRefreshLoadMore(PulltoRefreshLayout.this);
        }

    }

    public void setLoadMore(boolean isLoadMore)
    {
        this.isLoadMore = isLoadMore;
    }

    public void setProgressColors(int[] colors)
    {
        this.colorSchemeColors = colors;
    }

    public void setShowArrow(boolean showArrow)
    {
        this.showArrow = showArrow;
    }

    public void setShowProgressBg(boolean showProgressBg)
    {
        this.showProgressBg = showProgressBg;
    }

    public void setWaveColor(int waveColor)
    {
        this.waveColor = waveColor;
    }

    public void setWaveShow(boolean isShowWave){
        this.isShowWave = isShowWave;
    }

    public void setProgressValue(int progressValue)
    {
        this.progressValue = progressValue;
        materialHeadView.setProgressValue(progressValue);
    }

    public void createAnimatorTranslationY(final View v, final float h, final FrameLayout fl) {
        ViewPropertyAnimatorCompat viewPropertyAnimatorCompat = ViewCompat.animate(v);
        viewPropertyAnimatorCompat.setDuration(200);
        viewPropertyAnimatorCompat.setInterpolator(new DecelerateInterpolator());
        viewPropertyAnimatorCompat.translationY(h);
        viewPropertyAnimatorCompat.start();
        viewPropertyAnimatorCompat.setUpdateListener(new ViewPropertyAnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(View view) {
                float height = Math.abs(ViewCompat.getTranslationY(v));
                fl.getLayoutParams().height = (int) height;
                fl.requestLayout();
            }
        });
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll up. Override this if the child view is a custom view.
     */
    public boolean canChildScrollUp() {
        if (mChildView == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 14) {
            if (mChildView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mChildView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(mChildView, -1) || mChildView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mChildView, -1);
        }
    }

    public boolean canChildScrollDown() {
        if (mChildView == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 14) {
            if (mChildView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mChildView;
                if (absListView.getChildCount()>0)
                {
                    int lastChildBottom = absListView.getChildAt(absListView.getChildCount() - 1).getBottom();
                    return absListView.getLastVisiblePosition() == absListView.getAdapter().getCount() - 1 && lastChildBottom <= absListView.getMeasuredHeight();
                }else
                {
                    return false;
                }

            } else {
                return ViewCompat.canScrollVertically(mChildView, 1) || mChildView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mChildView, 1);
        }
    }

    public void setWaveHigher() {
        headHeight = hIGHER_HEAD_HEIGHT;
        waveHeight = HIGHER_WAVE_HEIGHT;
        MaterialWaveView.DefaulHeadHeight = hIGHER_HEAD_HEIGHT;
        MaterialWaveView.DefaulWaveHeight = HIGHER_WAVE_HEIGHT;
    }

    public void finishRefreshing() {
        if (mChildView != null) {
            ViewPropertyAnimatorCompat viewPropertyAnimatorCompat = ViewCompat.animate(mChildView);
            viewPropertyAnimatorCompat.setDuration(200);
            viewPropertyAnimatorCompat.y(ViewCompat.getTranslationY(mChildView));
            viewPropertyAnimatorCompat.translationY(0);
            viewPropertyAnimatorCompat.setInterpolator(new DecelerateInterpolator());
            viewPropertyAnimatorCompat.start();

            if (materialHeadView != null) {
                materialHeadView.onComlete(PulltoRefreshLayout.this);
            }

            if (refreshListener != null) {
                refreshListener.onfinish();
            }
        }
        isRefreshing = false;
        progressValue = 0;
        setProgressValue(0);
    }

//    public void finishRefresh()
//    {
//        this.post(new Runnable() {
//            @Override
//            public void run() {
//                finishRefreshing();
//            }
//        });
//    }

    public void finishRefreshLoadMore()
    {
        this.post(new Runnable() {
            @Override
            public void run() {
                if (pulltoRefreshFootView != null) {
//                    isLoadMoreing = false;
//                    materialFoodView.onComlete(MaterialRefreshLayout.this);
                    if (mChildView != null) {
                        ViewPropertyAnimatorCompat viewPropertyAnimatorCompat = ViewCompat.animate(mChildView);
                        viewPropertyAnimatorCompat.setDuration(200);
                        viewPropertyAnimatorCompat.y(ViewCompat.getTranslationY(mChildView));
                        viewPropertyAnimatorCompat.translationY(0);
                        viewPropertyAnimatorCompat.setInterpolator(new DecelerateInterpolator());
                        viewPropertyAnimatorCompat.start();

                        if (pulltoRefreshFootView != null) {
                            pulltoRefreshFootView.onComlete(PulltoRefreshLayout.this);
                        }

                        if (refreshListener != null) {
                            refreshListener.onfinish();
                        }
                    }
                    isRefreshing = false;
                    progressValue = 0;
                    setProgressValue(0);
                }
            }
        });

    }



    public void setHeaderView(final View headerView) {
        post(new Runnable() {
            @Override
            public void run() {
                mHeadLayout.addView(headerView);
            }
        });
    }

    public void setFooderView(final View fooderView) {
        post(new Runnable() {
            @Override
            public void run() {
                mFootLayout.addView(fooderView);
            }
        });
    }


    public void setWaveHeight(float waveHeight) {
        this.mWaveHeight = waveHeight;
    }

    public void setHeaderHeight(float headHeight) {
        this.mHeadHeight = headHeight;
    }

    public void setPulltoRefreshListener(PulltorefreshRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

}
