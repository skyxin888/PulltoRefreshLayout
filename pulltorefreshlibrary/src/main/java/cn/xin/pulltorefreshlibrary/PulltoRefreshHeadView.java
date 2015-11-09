package cn.xin.pulltorefreshlibrary;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;


public class PulltoRefreshHeadView extends FrameLayout implements PulltoRefreshHeaderListener {
    private MaterialWaveView materialWaveView;
    private CircleProgressBar circleProgressBar;
    private int waveColor;
    private int progressTextColor;
    private int[] progress_colors;
    private int progressStokeWidth;
    private boolean isShowArrow,isShowProgressBg;
    private int progressValue,progressValueMax;
    private int textType;
    private int progressBg;
    private int progressSize;
    private PulltoRefreshHeaderListener listener;


    public PulltoRefreshHeadView(Context context) {
        this(context, null);
    }

    public PulltoRefreshHeadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PulltoRefreshHeadView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }


    protected void init(AttributeSet attrs, int defStyle) {
        if (isInEditMode()) return;
        setClipToPadding(false);
        setWillNotDraw(false);
    }

    public int getWaveColor() {
        return waveColor;
    }

    public void setWaveColor(int waveColor) {
        this.waveColor = waveColor;
        if(null!= materialWaveView)
        {
            materialWaveView.setColor( this.waveColor );
        }
    }

    public void setProgressSize(int progressSize)
    {
        this.progressSize = progressSize;
    }

    public void setProgressBg(int progressBg)
    {
        this.progressBg = progressBg;
    }

    public void setIsProgressBg(boolean isShowProgressBg)
    {
        this.isShowProgressBg = isShowProgressBg;
    }

    public void setProgressTextColor(int textColor)
    {
        this.progressTextColor = textColor;
    }

    public void setProgressColors(int[] colors)
    {
        this.progress_colors = colors;
    }

    public void setTextType(int textType)
    {
        this.textType = textType;
    }

    public void setProgressValue(int value)
    {
        this.progressValue = value;
        this.post(new Runnable() {
            @Override
            public void run() {
                circleProgressBar.setProgress(progressValue);
            }
        });

    }

    public void setProgressValueMax(int value)
    {
        this.progressValueMax = value;
    }

    public void setProgressStokeWidth(int w)
    {
        this.progressStokeWidth = w;
    }

    public void showProgressArrow(boolean isShowArrow)
    {
        this.isShowArrow = isShowArrow;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        materialWaveView = new MaterialWaveView(getContext());
        materialWaveView.setColor(waveColor);
        addView(materialWaveView);

        circleProgressBar = new CircleProgressBar(getContext());
        LayoutParams layoutParams = new LayoutParams(Utils.dip2px(getContext(), progressSize),Utils.dip2px(getContext(),progressSize));
        layoutParams.gravity = Gravity.CENTER;
        circleProgressBar.setLayoutParams(layoutParams);
        circleProgressBar.setColorSchemeColors(progress_colors);
        circleProgressBar.setProgressStokeWidth(progressStokeWidth);
        circleProgressBar.setShowArrow(isShowArrow);
        circleProgressBar.setShowProgressText(textType == 0);
        circleProgressBar.setTextColor(progressTextColor);
        circleProgressBar.setProgress(progressValue);
        circleProgressBar.setMax(progressValueMax);
        circleProgressBar.setCircleBackgroundEnabled(isShowProgressBg);
        circleProgressBar.setProgressBackGroundColor(progressBg);
        addView(circleProgressBar);
    }

    @Override
    public void onComlete(PulltoRefreshLayout pulltoRefreshLayout) {
        if(materialWaveView != null)
        {
            materialWaveView.onComlete(pulltoRefreshLayout);
        }
        if(circleProgressBar != null)
        {
            circleProgressBar.onComlete(pulltoRefreshLayout);
            ViewCompat.setTranslationY(circleProgressBar, 0);
            ViewCompat.setScaleX(circleProgressBar, 0);
            ViewCompat.setScaleY(circleProgressBar, 0);
        }

    }

    @Override
    public void onBegin(PulltoRefreshLayout pulltoRefreshLayout) {
        if(materialWaveView != null)
        {
            materialWaveView.onBegin(pulltoRefreshLayout);
        }
        if(circleProgressBar != null)
        {
            circleProgressBar.onBegin(pulltoRefreshLayout);
        }
    }

    @Override
    public void onPull(PulltoRefreshLayout pulltoRefreshLayout, float fraction) {
        if(materialWaveView != null)
        {
            materialWaveView.onPull(pulltoRefreshLayout, fraction);
        }
        if(circleProgressBar != null)
        {
            circleProgressBar.onPull(pulltoRefreshLayout, fraction);
            float a = Utils.limitValue(1,fraction);
            ViewCompat.setScaleX(circleProgressBar, 1);
            ViewCompat.setScaleY(circleProgressBar, 1);
            ViewCompat.setAlpha(circleProgressBar, a);
        }
    }

    @Override
    public void onRelease(PulltoRefreshLayout pulltoRefreshLayout, float fraction) {

    }

    @Override
    public void onRefreshing(PulltoRefreshLayout pulltoRefreshLayout) {
        if(materialWaveView != null)
        {
            materialWaveView.onRefreshing(pulltoRefreshLayout);
        }
        if(circleProgressBar != null)
        {
            circleProgressBar.onRefreshing(pulltoRefreshLayout);
        }
    }


}
