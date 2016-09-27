package com.example.huangqihua.myphotoviewapplication;

import android.app.Application;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by huangqihua on 16/9/26.
 */
public class CEApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
    }
}
