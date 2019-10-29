/*
 * Copyright 2016 yinglan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yinglan.shadowimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v7.graphics.Palette;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * ================================================
 * 作    者：sufly0001@gmail.com
 * 版    本：1.1
 * 创建日期 ：2017/04/01
 * 描    述：
 * 修订历史： 修改显示
 * ================================================
 */
public class ShadowImageView extends RelativeLayout {
    private RoundImageView roundImageView;
    private int mLeftTopRound = 0;
    private int mLeftBottomRound = 0;
    private int mRightTopRound = 0;
    private int mRightBottomRound = 0;
    private int mHShadow; // 水平阴影的位置
    private int mVShadow; // 垂直阴影的位置
    private int mBorderWidth; // 描边大小
    private int mBorderColor; // 描边颜色
    private int mBlur; // 模糊的距离
    private int mShadowColor = -147483648; // 阴影颜色
    private int mSrcWidth;
    private int mSrcHeight;
    private Paint shadowPaint;
    private Paint backgroundPaint;
    private Paint strokePaint;

    public ShadowImageView(Context context) {
        this(context, null);
    }

    public ShadowImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShadowImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        setGravity(Gravity.CENTER);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        setClipChildren(false);
        setClipToPadding(false);
        int imageresource = -1;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShadowImageView);
            if (a.hasValue(R.styleable.ShadowImageView_src)) {
                imageresource = a.getResourceId(R.styleable.ShadowImageView_src, -1);
            }
            mSrcWidth = a.getLayoutDimension(R.styleable.ShadowImageView_srcWidth, 0);
            mSrcHeight = a.getLayoutDimension(R.styleable.ShadowImageView_srcHeight, 0);
            int round = a.getDimensionPixelSize(R.styleable.ShadowImageView_round, 0);
            mLeftTopRound = round;
            mRightTopRound = round;
            mRightBottomRound = round;
            mLeftBottomRound = round;
            mLeftTopRound = a.getDimensionPixelSize(R.styleable.ShadowImageView_leftTopRound, mLeftTopRound);
            mLeftBottomRound = a.getDimensionPixelSize(R.styleable.ShadowImageView_leftBottomRound, mLeftBottomRound);
            mRightTopRound = a.getDimensionPixelSize(R.styleable.ShadowImageView_rightTopRound, mRightTopRound);
            mRightBottomRound = a.getDimensionPixelSize(R.styleable.ShadowImageView_rightBottomRound, mRightBottomRound);
            mHShadow = a.getDimensionPixelSize(R.styleable.ShadowImageView_hShadow, mHShadow);
            mVShadow = a.getDimensionPixelSize(R.styleable.ShadowImageView_vShadow, mVShadow);
            mBorderWidth = a.getDimensionPixelSize(R.styleable.ShadowImageView_borderWidth, mBorderWidth);
            mBorderColor = a.getColor(R.styleable.ShadowImageView_borderColor, Color.BLACK);
            mBlur = a.getDimensionPixelSize(R.styleable.ShadowImageView_blur, mBlur);
            if (a.hasValue(R.styleable.ShadowImageView_shadowColor)) {
                mShadowColor = a.getColor(R.styleable.ShadowImageView_shadowColor, Color.parseColor("#8D8D8D"));
            }
        } else {
            imageresource = -1;
        }

        initPaints();

        roundImageView = (RoundImageView) LayoutInflater.from(context).inflate(R.layout.round_image_view, this, false);
        roundImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        roundImageView.setLeftTopRound(mLeftTopRound);
        roundImageView.setLeftBottomRound(mLeftBottomRound);
        roundImageView.setRightTopRound(mRightTopRound);
        roundImageView.setRightBottomRound(mRightBottomRound);
        if (imageresource == -1) {
            roundImageView.setImageResource(android.R.color.transparent);
        } else {
            roundImageView.setImageResource(imageresource);
        }

        if (this.mShadowColor == Color.parseColor("#8D8D8D")) {
            this.mShadowColor = -147483648;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mSrcWidth, mSrcHeight);
        addView(roundImageView, params);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                refreshRoundImageView();
            }
        });
    }

    private void initPaints(){
        shadowPaint = new Paint();
        shadowPaint.setStyle(Paint.Style.FILL);
        shadowPaint.setAntiAlias(true);
        backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setColor(Color.WHITE);
        backgroundPaint.setStyle(Paint.Style.FILL);
        strokePaint = new Paint();
        strokePaint.setAntiAlias(true);
        strokePaint.setColor(mShadowColor);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(1);
    }

    private void refreshRoundImageView() {
        roundImageView.setLeftTopRound(mLeftTopRound);
        roundImageView.setLeftBottomRound(mLeftBottomRound);
        roundImageView.setRightTopRound(mRightTopRound);
        roundImageView.setRightBottomRound(mRightBottomRound);
        roundImageView.invalidate();
    }

    public ShadowImageView setImageResource(int resId) {
        roundImageView.setImageResource(resId);
        return this;
    }

    public ShadowImageView setImageDrawable(Drawable drawable) {
        roundImageView.setImageDrawable(drawable);
        return this;
    }

    public ShadowImageView setImageBitmap(Bitmap bitmap) {
        roundImageView.setImageBitmap(bitmap);
        return this;
    }

    public ShadowImageView setImageShadowColor(@ColorInt int color) {
        mShadowColor = color;
        return this;
    }

    public ShadowImageView setRound(int round) {
        round = filterRound(round);
        mLeftTopRound = round;
        mRightTopRound = round;
        mRightBottomRound = round;
        mLeftBottomRound = round;
        roundImageView.setRound(round);
        roundImageView.invalidate();
        return this;
    }

    public ShadowImageView setLeftTopRound(int leftTopRound) {
        leftTopRound = filterRound(leftTopRound);
        mLeftTopRound = leftTopRound;
        roundImageView.setLeftTopRound(leftTopRound);
        roundImageView.invalidate();
        return this;
    }

    public ShadowImageView setRightTopRound(int rightTopRound) {
        rightTopRound = filterRound(rightTopRound);
        mRightTopRound = rightTopRound;
        roundImageView.setRightTopRound(rightTopRound);
        roundImageView.invalidate();
        return this;
    }

    public ShadowImageView setRightBottomRound(int rightBottomRound) {
        rightBottomRound = filterRound(rightBottomRound);
        mRightBottomRound = rightBottomRound;
        roundImageView.setRightBottomRound(rightBottomRound);
        roundImageView.invalidate();
        return this;
    }

    public ShadowImageView setLeftBottomRound(int leftBottomRound) {
        leftBottomRound = filterRound(leftBottomRound);
        mLeftBottomRound = leftBottomRound;
        roundImageView.setLeftBottomRound(leftBottomRound);
        roundImageView.invalidate();
        return this;
    }

    private int filterRound(int round) {
        if (round > roundImageView.getWidth() / 2 || round > roundImageView.getHeight() / 2) {
            if (roundImageView.getWidth() > roundImageView.getHeight()) {
                round = roundImageView.getHeight() / 2;
            } else {
                round = roundImageView.getWidth() / 2;
            }
        }
        return round;
    }

    public ShadowImageView setHShadow(int hShadow) {
        mHShadow = hShadow;
        return this;
    }

    public ShadowImageView setVShadow(int vShadow) {
        mVShadow = vShadow;
        return this;
    }

    public ShadowImageView setBorderWidth(int borderWidth) {
        mBorderWidth = borderWidth;
        return this;
    }

    public ShadowImageView setBlur(int blur) {
        mBlur = blur;
        return this;
    }

    public void refresh() {
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (0 == mBorderWidth) {
            mBorderColor = mShadowColor;
        }
        shadowPaint.setColor(mBorderColor);
        Bitmap bitmap;
        int rgb;

        if (roundImageView.getDrawable() instanceof ColorDrawable) {
            rgb = ((ColorDrawable) roundImageView.getDrawable()).getColor();
            shadowPaint.setShadowLayer(mBlur, mHShadow, mVShadow, getDarkerColor(rgb));
        } else if (roundImageView.getDrawable() instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) roundImageView.getDrawable()).getBitmap();
            Palette.Swatch mSwatch = Palette.from(bitmap).generate().getDominantSwatch();

            if (null != mSwatch) {
                rgb = mSwatch.getRgb();
            } else {
                rgb = Color.parseColor("#8D8D8D");
            }

            shadowPaint.setShadowLayer(mBlur, mHShadow, mVShadow, getDarkerColor(rgb));
            Bitmap bitmapT = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight());

            if (null != Palette.from(bitmapT).generate().getDominantSwatch()) {
                rgb = Palette.from(bitmapT).generate().getDominantSwatch().getRgb();
                shadowPaint.setShadowLayer(mBlur, mHShadow, mVShadow, rgb);
            }
        } else {
            rgb = Color.parseColor("#8D8D8D");
            shadowPaint.setShadowLayer(mBlur, mHShadow, mVShadow, getDarkerColor(rgb));
        }

        if (this.mShadowColor != -147483648) {
            shadowPaint.setShadowLayer(mBlur, mHShadow, mVShadow, this.mShadowColor);
        }
        if (mSrcWidth == mSrcHeight && mLeftTopRound == mRightTopRound && mRightTopRound == mRightBottomRound && mRightBottomRound == mLeftBottomRound && mLeftBottomRound == mLeftTopRound && mSrcWidth / 2 == mLeftTopRound) {
            drawCircleShadow(canvas);
        } else {
            drawRectShadow(canvas);
        }
        super.dispatchDraw(canvas);
    }

    private void drawRectShadow(Canvas canvas) {
        int offset = 1;
        Path path = new Path();
        float startX = roundImageView.getX() - mBorderWidth + offset;
        float startY = roundImageView.getY() - mBorderWidth + offset;
        int width = roundImageView.getWidth();
        int height = roundImageView.getHeight();
        float endX = startX + width + 2 * mBorderWidth - 2 * offset;
        float endY = startY + height + 2 * mBorderWidth - 2 * offset;
        path.moveTo(startX, startY + mLeftTopRound);
        path.arcTo(new RectF(startX, startY, startX + 2 * mLeftTopRound, startY + 2 * mLeftTopRound), 180, 90);
        path.lineTo(endX - mRightTopRound, startY);
        path.arcTo(new RectF(endX - 2 * mRightTopRound, startY, endX, startY + 2 * mRightTopRound), -90, 90);
        path.lineTo(endX, endY - mRightBottomRound);
        path.arcTo(new RectF(endX - 2 * mRightBottomRound, endY - 2 * mRightBottomRound, endX, endY), 0, 90);
        path.lineTo(startX + mLeftBottomRound, endY);
        path.arcTo(new RectF(startX, endY - 2 * mLeftBottomRound, startX + 2 * mLeftBottomRound, endY), 90, 90);
        path.lineTo(startX, startY + mLeftTopRound);
        path.close();
        canvas.drawPath(path, shadowPaint);
        canvas.drawPath(path, backgroundPaint);
        canvas.drawPath(path, strokePaint);
    }

    private void drawCircleShadow(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        float startX = roundImageView.getX() - mBorderWidth;
        float startY = roundImageView.getY() - mBorderWidth;
        int width = roundImageView.getWidth();
        int height = roundImageView.getHeight();
        float endX = startX + width + 2 * mBorderWidth;
        float endY = startY + height + 2 * mBorderWidth;
        canvas.drawCircle(startX + (endX - startX) / 2, startY + (endY - startY) / 2, (endX - startX) / 2, shadowPaint);
        canvas.drawCircle(startX + (endX - startX) / 2, startY + (endY - startY) / 2, (endX - startX) / 2, backgroundPaint);
        canvas.drawCircle(startX + (endX - startX) / 2, startY + (endY - startY) / 2, (endX - startX) / 2, strokePaint);
    }

    public int getDarkerColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[1] = hsv[1] + 0.1f;
        hsv[2] = hsv[2] - 0.1f;
        int darkerColor = Color.HSVToColor(hsv);
        return darkerColor;
    }

    public RoundImageView getRoundImageView() {
        return roundImageView;
    }
}
