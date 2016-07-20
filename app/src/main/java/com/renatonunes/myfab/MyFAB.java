package com.renatonunes.myfab;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AbsListView;

import java.util.Random;

/**
 * Created by Renato on 11/07/2016.
 */
public class MyFAB extends FloatingActionButton
{
    //private static final int TRANSLATE_DURATION_MILLIS = 200;
    private static final int TRANSLATE_DURATION_MILLIS = 5000;
    //private final Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
    private final Interpolator mInterpolator = new OvershootInterpolator();
    private boolean mVisible;
    private Context mContext;

    public MyFAB(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context;
        Log.i("Abscroll", "mVisible" + mVisible);
    }

    public void show() {
        show(true);
    }

    public void hide() {
        hide(true);
    }

    public void show(boolean animate) {
        toggle(true, animate, false);
    }

    public void hide(boolean animate) {
        toggle(false, animate, false);
    }

    public void hideScaled() {

        this.animate().setInterpolator(mInterpolator)
                .setDuration(TRANSLATE_DURATION_MILLIS)
                .scaleX(0)
                .scaleY(0);
    }

    public void showScaled() {
        Animation scalein = AnimationUtils.loadAnimation(mContext, R.anim.scale_in);
        this.startAnimation(scalein);
    }

    public void showFaded(){
        Animation fadein = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
        this.startAnimation(fadein);
    }

    public void showBeating(){
        Animation scaleout = AnimationUtils.loadAnimation(mContext, R.anim.scale_out);
        scaleout.setRepeatCount(Animation.INFINITE);
        scaleout.setRepeatMode(Animation.REVERSE);
        this.startAnimation(scaleout);
    }

    public void showFromOutside(){
        Animation movein = AnimationUtils.loadAnimation(mContext, R.anim.move_in);
        this.startAnimation(movein);
    }

    public void showRandom(){
        Random gerador = new Random();

        switch (gerador.nextInt(4)){
            case 0:{
                this.showBeating();
                break;
            }
            case 1:{ //aparece do fundo
                this.showScaled();
                break;
            }
            case 2:{ //aparece do invisivel
                this.showFaded();
                break;
            }
            default:{//vem da borda de baixo
                this.showFromOutside();
                break;
            }
        }
    }

    private void toggle(final boolean visible, final boolean animate, boolean force) {
        if (mVisible != visible || force) {
            mVisible = visible;
            int height = getHeight();
            if (height == 0 && !force) {
                ViewTreeObserver vto = getViewTreeObserver();
                if (vto.isAlive()) {
                    vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            ViewTreeObserver currentVto = getViewTreeObserver();
                            if (currentVto.isAlive()) {
                                currentVto.removeOnPreDrawListener(this);
                            }
                            toggle(visible, animate, true);
                            return true;
                        }
                    });
                    return;
                }
            }
            int translationY = visible ? 0 : height + getMarginBottom();
            Log.i("Abscroll", "transY" + translationY);
            if (animate) {
                this.animate().setInterpolator(mInterpolator)
                        .setDuration(TRANSLATE_DURATION_MILLIS)
                        .translationY(translationY);
            } else {
                setTranslationY(translationY);
            }
        }
    }

    private int getMarginBottom() {
        int marginBottom = 0;
        final ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            marginBottom = ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin;
        }
        return marginBottom;
    }

    public void attachToListView(@NonNull AbsListView listView)
    {
        listView.setOnScrollListener(new AbsListViewScrollDetector() {
            @Override
            void onScrollUp() {
                hide();
            }

            @Override
            void onScrollDown() {
                show();
            }

            @Override
            void setScrollThreshold() {
                setScrollThreshold(getResources().getDimensionPixelOffset(R.dimen.fab_scroll_threshold));
            }
        });
    }

    public void attachToRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerViewScrollDetector() {
            @Override
            void onScrollUp() {
                hide();
            }

            @Override
            void onScrollDown() {
                show();
            }

            @Override
            void setScrollThreshold() {
                setScrollThreshold(getResources().getDimensionPixelOffset(R.dimen.fab_scroll_threshold));
            }
        });
    }
}