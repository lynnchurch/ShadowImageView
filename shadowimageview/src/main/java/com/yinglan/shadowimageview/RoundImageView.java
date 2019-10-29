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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * ================================================
 * 作    者：sufly0001@gmail.com
 * 版    本：1.1
 * 创建日期 ：2017/04/01
 * 描    述：
 * 修订历史： 修改显示
 * ================================================
 */
public class RoundImageView extends AppCompatImageView {
    private Paint paint;
    private int mLeftTopRound = 0;
    private int mLeftBottomRound = 0;
    private int mRightTopRound = 0;
    private int mRightBottomRound = 0;
    private Paint paint2;
    private Path path = new Path();

    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        paint2 = new Paint();
        paint2.setAntiAlias(true);
        paint2.setXfermode(null);
    }

    public void setRound(int round) {
        mLeftTopRound = round;
        mLeftBottomRound = round;
        mRightTopRound = round;
        mRightBottomRound = round;
    }

    public void setLeftTopRound(int leftTopRound) {
        mLeftTopRound = leftTopRound;
    }

    public void setLeftBottomRound(int leftBottomRound) {
        mLeftBottomRound = leftBottomRound;
    }

    public void setRightTopRound(int rightTopRound) {
        mRightTopRound = rightTopRound;
    }

    public void setRightBottomRound(int rightBottomRound) {
        mRightBottomRound = rightBottomRound;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(bitmap);
        super.onDraw(canvas2);
        path.reset();
        drawLeftTop(canvas2);
        drawLeftBottom(canvas2);
        drawRightTop(canvas2);
        drawRightBottom(canvas2);
        path.close();
        canvas.drawBitmap(bitmap, 0, 0, paint2);
    }

    private void drawLeftTop(Canvas canvas) {
        path.moveTo(0, mLeftTopRound);
        path.lineTo(0, 0);
        path.lineTo(mLeftTopRound, 0);
        path.arcTo(new RectF(0, 0, mLeftTopRound * 2, mLeftTopRound * 2), -90, -90);
        canvas.drawPath(path, paint);
    }

    private void drawLeftBottom(Canvas canvas) {
        path.moveTo(0, getHeight() - mLeftBottomRound);
        path.lineTo(0, getHeight());
        path.lineTo(mLeftBottomRound, getHeight());
        path.arcTo(new RectF(0, getHeight() - mLeftBottomRound * 2, mLeftBottomRound * 2, getHeight()), 90, 90);
        canvas.drawPath(path, paint);
    }

    private void drawRightTop(Canvas canvas) {
        path.moveTo(getWidth(), mRightTopRound);
        path.lineTo(getWidth(), 0);
        path.lineTo(getWidth() - mRightTopRound, 0);
        path.arcTo(new RectF(getWidth() - mRightTopRound * 2, 0, getWidth(), 0 + mRightTopRound * 2), -90, 90);
        canvas.drawPath(path, paint);
    }

    private void drawRightBottom(Canvas canvas) {
        path.moveTo(getWidth() - mRightBottomRound, getHeight());
        path.lineTo(getWidth(), getHeight());
        path.lineTo(getWidth(), getHeight() - mRightBottomRound);
        path.arcTo(new RectF(getWidth() - mRightBottomRound * 2, getHeight() - mRightBottomRound * 2, getWidth(), getHeight()), -0, 90);
        canvas.drawPath(path, paint);
    }
}
