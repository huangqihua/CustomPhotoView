package com.example.huangqihua.myphotoviewapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.huangqihua.myphotoviewapplication.view.HackyViewPager;
import com.example.huangqihua.myphotoviewapplication.view.photo.PhotoView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CustomPhotoActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private HackyViewPager mViewPager;
    private RelativeLayout mSaveImageLayout;
    private TextView mTotalImageNum; //圖片的總數
    private TextView mCurrentImageNum; //顯示當前查看的張數

    private ArrayList<String> urls; //传递的url集合
    private int mCurrentPosition; //传递的当前图片的位置

    private List<PhotoView> mPagerViews;
    private PhotoView mPhotoView;

    private ViewPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_photo);

        initData();
        initView();

    }

    private void initData() {
        Intent intent = getIntent();
        urls = intent.getStringArrayListExtra("urls");
        mCurrentPosition = intent.getIntExtra("position", 0);

        mPagerViews = new ArrayList<>();
    }

    private void initView() {
        mSaveImageLayout = (RelativeLayout) findViewById(R.id.photo_view_save_rl);

        mTotalImageNum = (TextView) findViewById(R.id.photo_view_tv_total);

        mCurrentImageNum = (TextView) findViewById(R.id.photo_view_tv_current);

        assert mSaveImageLayout != null;
        mSaveImageLayout.setOnClickListener(this);


        if (urls != null && urls.size() > 0) {
            for (int i = 0; i < urls.size(); i++) {
                mPhotoView = new PhotoView(this);
                mPagerViews.add(mPhotoView);
            }
            mViewPager = (HackyViewPager) findViewById(R.id.photo_view_pager);
            mAdapter = new ViewPagerAdapter(mPagerViews);
            mViewPager.setAdapter(mAdapter);
            mViewPager.setCurrentItem(mCurrentPosition);

            showImageNum();

            mViewPager.setOnPageChangeListener(this);
        }

    }

    private void showImageNum() {
        mTotalImageNum.setText(urls.size() + "");
        mCurrentImageNum.setText((mCurrentPosition + 1) + "");
    }


    public class ViewPagerAdapter extends PagerAdapter {

        private List<PhotoView> mViews;

        public ViewPagerAdapter(List<PhotoView> pagerViews) {
            this.mViews = pagerViews;
        }

        @Override
        public int getCount() {
            return mViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            PhotoView photoView = new PhotoView(container.getContext());
            photoView.setSingleClickLister(new PhotoView.OnSingleClick() {
                @Override
                public void singleClick() {
                    finish();
                }
            });

            mPagerViews.remove(position);
            mPagerViews.add(position, photoView);
            photoView.setBackgroundColor(Color.BLACK);

            final LinearLayout progressBarLayout = (LinearLayout) LayoutInflater.from(CustomPhotoActivity.this).inflate(R.layout.view_photo_layout, null);
            progressBarLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            final ProgressBar mBar = (ProgressBar) progressBarLayout.findViewById(R.id.photo_view_progress_bar);
            photoView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            RelativeLayout relativeLayout = new RelativeLayout(CustomPhotoActivity.this);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            relativeLayout.setLayoutParams(layoutParams);
            relativeLayout.addView(photoView);
            relativeLayout.addView(progressBarLayout);

            container.addView(relativeLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            String href = urls.get(position);
            if (!TextUtils.isEmpty(href) && !href.startsWith("http")) {
                href = "file://" + href;
            }

            DisplayImageOptions options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.loading_image_bg)
                    .showImageOnFail(R.drawable.loading_image_bg)
                    .showImageOnLoading(R.drawable.loading_image_bg)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .considerExifParams(true)
                    .cacheInMemory(true)
                    .cacheOnDisk(true).build();

            ImageLoader.getInstance().displayImage(href, photoView, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                    progressBarLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    mBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    progressBarLayout.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
            return relativeLayout;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.photo_view_save_rl) {
            saveCurrentImage();
        }
    }

    /**
     * 保存圖片到本地
     */
    private void saveCurrentImage() {
        String href = urls.get(mViewPager.getCurrentItem());
        if (!TextUtils.isEmpty(href) && !href.startsWith("http://")) {
            href = "file://" + href;
        }

        ImageLoader.getInstance().loadImage(href, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                Toast.makeText(CustomPhotoActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                saveImageToGallery(loadedImage);
                Toast.makeText(CustomPhotoActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void saveImageToGallery(Bitmap bitmap) {

        //先把图片保存到本地
        File appDir = new File(Environment.getExternalStorageDirectory(), "xxx");
        if (!appDir.exists()) {
            appDir.mkdir();
        }

        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //再把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //最后通知图库更新
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCurrentImageNum.setText((position + 1) + "");
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


}
