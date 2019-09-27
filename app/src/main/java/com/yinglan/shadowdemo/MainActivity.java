package com.yinglan.shadowdemo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yinglan.shadowimageview.ShadowImageView;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private ShadowImageView shadow;
    private AppCompatSeekBar sbLeftTopRound;
    private AppCompatSeekBar sbRightTopRound;
    private AppCompatSeekBar sbRightBottomRound;
    private AppCompatSeekBar sbLeftBottomRound;
    private AppCompatSeekBar sbHShadow;
    private AppCompatSeekBar sbVShadow;
    private AppCompatSeekBar sbBlur;
    private int resId = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shadow = (ShadowImageView) findViewById(R.id.shadow);
        sbLeftTopRound = (AppCompatSeekBar) findViewById(R.id.sbLeftTopRound);
        sbRightTopRound = (AppCompatSeekBar) findViewById(R.id.sbRightTopRound);
        sbRightBottomRound = (AppCompatSeekBar) findViewById(R.id.sbRightBottomRound);
        sbLeftBottomRound = (AppCompatSeekBar) findViewById(R.id.sbLeftBottomRound);
        sbHShadow = (AppCompatSeekBar) findViewById(R.id.sbHShadow);
        sbVShadow = (AppCompatSeekBar) findViewById(R.id.sbVShadow);
        sbBlur = (AppCompatSeekBar) findViewById(R.id.sbBlur);

        shadow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int res = R.mipmap.lotus;
                switch (resId) {
                    case 1:
                        res = R.mipmap.mountain;
                        resId = 2;
                        break;
                    case 2:
                        res = R.mipmap.sunset;
                        resId = 3;
                        break;
                    case 3:
                        res = R.mipmap.red;
                        resId = 4;
                        break;
                    case 4:
                        res = R.mipmap.lotus;
                        resId = 1;
                        break;
                }
                if (resId == 1 || resId == 3)
                    shadow.setImageResource(res);
                else
                    shadow.setImageDrawable(getResources().getDrawable(res));
            }
        });

        sbLeftTopRound.setOnSeekBarChangeListener(this);
        sbRightTopRound.setOnSeekBarChangeListener(this);
        sbRightBottomRound.setOnSeekBarChangeListener(this);
        sbLeftBottomRound.setOnSeekBarChangeListener(this);
        sbHShadow.setOnSeekBarChangeListener(this);
        sbVShadow.setOnSeekBarChangeListener(this);
        sbBlur.setOnSeekBarChangeListener(this);

        loadNetImage();
    }

    private void loadNetImage() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCacheSize(2 * 1024 * 1024)
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs() // Remove for release app
                .build();

        ImageLoader.getInstance().init(config);

        //此处加载的是本地的图片，网络图片用法一至
        ImageLoader.getInstance().displayImage("drawable://" + R.mipmap.lotus, new ImageView(this), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                ((ShadowImageView) findViewById(R.id.shadow)).setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sbLeftTopRound:
                shadow.setLeftTopRound(progress).refresh();
                break;
            case R.id.sbRightTopRound:
                shadow.setRightTopRound(progress).refresh();
                break;
            case R.id.sbRightBottomRound:
                shadow.setRightBottomRound(progress).refresh();
                break;
            case R.id.sbLeftBottomRound:
                shadow.setLeftBottomRound(progress).refresh();
                break;
            case R.id.sbHShadow:
                shadow.setHShadow(progress - 50).refresh();
                break;
            case R.id.sbVShadow:
                shadow.setVShadow(progress - 50).refresh();
                break;
            case R.id.sbBlur:
                shadow.setBlur(progress).refresh();
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
