package com.hrskrs.instadotlib;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.view.ViewPager.OnPageChangeListener;


/**
 * Created by hrskrs on 10/16/17.
 */

public class InstaDotView extends View {

    private static final int MIN_VISIBLE_DOT_COUNT = 6;
    private static final int DEFAULT_VISIBLE_DOTS_COUNT = MIN_VISIBLE_DOT_COUNT;

    private int activeDotSize;
    private int inactiveDotSize;
    private int mediumDotSize;
    private int smallDotSize;
    private int dotMargin;

    private Paint activePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint inactivePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int startPosX;
    private int posY = 0;
    private int previousPage = 0;
    private int currentPage = 0;

    private ValueAnimator translationAnim;

    private List<Dot> dotsList = new ArrayList<>();

    private int noOfPages = 0;
    private int visibleDotCounts = DEFAULT_VISIBLE_DOTS_COUNT;

    private ViewPager viewPager;

    public InstaDotView(Context context) {
        super(context);
        setup(context, null);
    }

    public InstaDotView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs);
    }

    public InstaDotView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context, attrs);
    }

    public InstaDotView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup(context, attrs);
    }

    private void setup(Context context, AttributeSet attributeSet) {
        Resources resources = getResources();

        if (attributeSet != null) {
            TypedArray ta = context.obtainStyledAttributes(attributeSet, R.styleable.InstaDotView);
            activePaint.setStyle(Paint.Style.FILL);
            activePaint.setColor(ta.getColor(R.styleable.InstaDotView_dot_activeColor, resources.getColor(R.color.active)));
            inactivePaint.setStyle(Paint.Style.FILL);
            inactivePaint.setColor(ta.getColor(R.styleable.InstaDotView_dot_inactiveColor, resources.getColor(R.color.inactive)));
            activeDotSize = ta.getDimensionPixelSize(R.styleable.InstaDotView_dot_activeSize, resources.getDimensionPixelSize(R.dimen.dot_active_size));
            inactiveDotSize = ta.getDimensionPixelSize(R.styleable.InstaDotView_dot_inactiveSize, resources.getDimensionPixelSize(R.dimen.dot_inactive_size));
            mediumDotSize = ta.getDimensionPixelSize(R.styleable.InstaDotView_dot_mediumSize, resources.getDimensionPixelSize(R.dimen.dot_medium_size));
            smallDotSize = ta.getDimensionPixelSize(R.styleable.InstaDotView_dot_smallSize, resources.getDimensionPixelSize(R.dimen.dot_small_size));
            dotMargin = ta.getDimensionPixelSize(R.styleable.InstaDotView_dot_margin, resources.getDimensionPixelSize(R.dimen.dot_margin));
            setVisibleDotCounts(ta.getInteger(R.styleable.InstaDotView_dots_visible, DEFAULT_VISIBLE_DOTS_COUNT));

            ta.recycle();
        }

        posY = activeDotSize / 2;

        initCircles();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = (activeDotSize + dotMargin) * (dotsList.size() + 1);
        int desiredHeight = activeDotSize;

        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == View.MeasureSpec.EXACTLY) width = widthSize;
        else if (widthMode == View.MeasureSpec.AT_MOST) width = Math.min(desiredWidth, widthSize);
        else width = desiredWidth;

        if (heightMode == View.MeasureSpec.EXACTLY) height = heightSize;
        else if (heightMode == View.MeasureSpec.AT_MOST)
            height = Math.min(desiredHeight, heightSize);
        else height = desiredHeight;

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawCircles(canvas);
    }

    private void initCircles() {
        int viewCount = Math.min(getNoOfPages(), getVisibleDotCounts());
        if (viewCount < 1) return;

        setStartPosX(getSmallDotStartX());

        dotsList = new ArrayList<>(viewCount);
        for (int i = 0; i < viewCount; i++) {
            Dot dot = new Dot();
            Dot.State state;

            if (noOfPages > visibleDotCounts) {
                if (i == getVisibleDotCounts() - 1) state = Dot.State.SMALL;
                else if (i == getVisibleDotCounts() - 2) state = Dot.State.MEDIUM;
                else state = i == 0 ? Dot.State.ACTIVE : Dot.State.INACTIVE;
            } else {
                state = i == 0 ? Dot.State.ACTIVE : Dot.State.INACTIVE;
            }

            dot.setState(state);
            dotsList.add(dot);
        }

        invalidate();
    }

    private void drawCircles(Canvas canvas) {
        int posX = getStartPosX();

        for (int i = 0; i < dotsList.size(); i++) {

            Dot d = dotsList.get(i);
            Paint paint = inactivePaint;
            int radius;

            switch (d.getState()) {
                case ACTIVE:
                    paint = activePaint;
                    radius = getActiveDotRadius();
                    posX += getActiveDotStartX();
                    break;
                case INACTIVE:
                    radius = getInactiveDotRadius();
                    posX += getInactiveDotStartX();
                    break;
                case MEDIUM:
                    radius = getMediumDotRadius();
                    posX += getMediumDotStartX();
                    break;
                case SMALL:
                    radius = getSmallDotRadius();
                    posX += getSmallDotStartX();
                    break;
                default:
                    radius = 0;
                    posX = 0;
                    break;
            }


            canvas.drawCircle(posX, posY, radius, paint);
        }
    }


    private ValueAnimator getTranslationAnimation(int from, int to, final AnimationListener listener) {
        if (translationAnim != null) translationAnim.cancel();
        translationAnim = ValueAnimator.ofInt(from, to);
        translationAnim.setDuration(120);
        translationAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        translationAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                if (getStartPosX() != val) {
                    setStartPosX(val);
                    invalidate();
                }
            }
        });
        translationAnim.addListener(new AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                if (listener != null) listener.onAnimationEnd();
            }
        });
        return translationAnim;
    }

    public void setNoOfPages(int noOfPages) {
        //Hide if noOfPages is 0 or 1
        setVisibility(noOfPages <= 1 ? GONE : VISIBLE);
        this.noOfPages = noOfPages;
        recreate();
    }

    public int getNoOfPages() {
        return noOfPages;
    }

    public void setVisibleDotCounts(int visibleDotCounts) {
        if (visibleDotCounts < MIN_VISIBLE_DOT_COUNT)
            throw new RuntimeException("Visible Dot count cannot be smaller than " + MIN_VISIBLE_DOT_COUNT);
        this.visibleDotCounts = visibleDotCounts;
        recreate();
    }

    private void recreate() {
        initCircles();
        requestLayout();
        invalidate();
    }

    public int getVisibleDotCounts() {
        return visibleDotCounts;
    }

    public void setStartPosX(int startPosX) {
        this.startPosX = startPosX;
    }

    public int getStartPosX() {
        return startPosX;
    }

    public int getActiveDotStartX() {
        return activeDotSize + dotMargin;
    }

    private int getInactiveDotStartX() {
        return inactiveDotSize + dotMargin;
    }

    private int getMediumDotStartX() {
        return mediumDotSize + dotMargin;
    }

    private int getSmallDotStartX() {
        return smallDotSize + dotMargin;
    }

    private int getActiveDotRadius() {
        return activeDotSize / 2;
    }

    private int getInactiveDotRadius() {
        return inactiveDotSize / 2;
    }

    private int getMediumDotRadius() {
        return mediumDotSize / 2;
    }

    private int getSmallDotRadius() {
        return smallDotSize / 2;
    }

    public void onPageChange(int page) {
        this.currentPage = page;
        if (page != previousPage && page >= 0 && page <= getNoOfPages() - 1) {
            updateDots();
            previousPage = currentPage;
        }
    }


    public void setupViewPager(ViewPager viewPager) {
        if (viewPager != null && viewPager.getAdapter() != null) {
            this.viewPager = viewPager;
            setNoOfPages(viewPager.getAdapter().getCount());
            viewPager.removeOnPageChangeListener(mInternalPageChangeListener);
            viewPager.addOnPageChangeListener(mInternalPageChangeListener);
            mInternalPageChangeListener.onPageSelected(viewPager.getCurrentItem());
        }
    }

    public void addOnDotPageListener(OnPageChangeListener onPageChangeListener) {
        if (viewPager != null && viewPager.getAdapter() != null) {
            if (onPageChangeListener !=null)
                mInternalPageChangeListener = onPageChangeListener;
                viewPager.addOnPageChangeListener(mInternalPageChangeListener);
        }
    }

    public OnPageChangeListener getDotPageLister(){
        return mInternalPageChangeListener;
    }

    private OnPageChangeListener mInternalPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (viewPager !=null && viewPager.getAdapter() !=null)
                onPageChange(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void updateDots() {

        //If pages does not exceed DOT COUNT limit
        if (noOfPages <= visibleDotCounts) {
            setupNormalDots();
            return;
        }

        //If page exceed DOT COUNT limit - 2 last dots
        for (int i = 0; i < dotsList.size(); i++) {
            Dot currentDot = dotsList.get(i);
            //Active dot
            if (currentDot.getState().equals(Dot.State.ACTIVE)) {
                //Set current active dot to passive
                currentDot.setState(Dot.State.INACTIVE);
                //Left to right
                if (currentPage > previousPage) {
                    setupFlexibleCirclesRight(i);
                } else {
                    //Right to left
                    setupFlexibleCirclesLeft(i);
                }
                return;
            }
        }

    }

    private void setupNormalDots() {
        dotsList.get(currentPage).setState(Dot.State.ACTIVE);
        dotsList.get(previousPage).setState(Dot.State.INACTIVE);

        invalidate();
    }

    private void setupFlexibleCirclesRight(final int position) {
        //If position exceed last two dots
        if (position >= getVisibleDotCounts() - 3) {
            if (currentPage == getNoOfPages() - 1) {
                //Last item from right
                dotsList.get(dotsList.size() - 1).setState(Dot.State.ACTIVE);
                invalidate();
            } else if (currentPage == getNoOfPages() - 2) {
                //Second item from right
                dotsList.get(dotsList.size() - 1).setState(Dot.State.MEDIUM);
                dotsList.get(dotsList.size() - 2).setState(Dot.State.ACTIVE);
                invalidate();
            } else {
                removeAddRight(position);
            }
        } else {
            dotsList.get(position + 1).setState(Dot.State.ACTIVE);
            invalidate();
        }
    }

    private void removeAddRight(final int position) {
        dotsList.remove(0);
        setStartPosX(getStartPosX() + getSmallDotStartX());

        getTranslationAnimation(getStartPosX(), getSmallDotStartX(), new AnimationListener() {
            @Override
            public void onAnimationEnd() {
                dotsList.get(0).setState(Dot.State.SMALL);
                dotsList.get(1).setState(Dot.State.MEDIUM);

                Dot newDot = new Dot();
                newDot.setState(Dot.State.ACTIVE);
                dotsList.add(position, newDot);
                invalidate();
            }
        }).start();
    }

    private void setupFlexibleCirclesLeft(final int position) {
        //If position exceed first two dots
        if (position <= 2) {
            if (currentPage == 0) {
                //First item from left
                dotsList.get(0).setState(Dot.State.ACTIVE);
                invalidate();
            } else if (currentPage == 1) {
                //Second item from left
                dotsList.get(0).setState(Dot.State.MEDIUM);
                dotsList.get(1).setState(Dot.State.ACTIVE);
                invalidate();
            } else {
                removeAddLeft(position);
            }
        } else {
            dotsList.get(position - 1).setState(Dot.State.ACTIVE);
            invalidate();
        }
    }

    private void removeAddLeft(final int position) {
        dotsList.remove(dotsList.size() - 1);
        setStartPosX(0);

        getTranslationAnimation(getStartPosX(), getSmallDotStartX(), new AnimationListener() {
            @Override
            public void onAnimationEnd() {
                dotsList.get(dotsList.size() - 1).setState(Dot.State.SMALL);
                dotsList.get(dotsList.size() - 2).setState(Dot.State.MEDIUM);

                Dot newDot = new Dot();
                newDot.setState(Dot.State.ACTIVE);
                dotsList.add(position, newDot);
                invalidate();
            }
        }).start();
    }

}
