package com.example.huangqihua.myphotoviewapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.id.list;

/**
 * Created by huangqihua on 16/9/26.
 */
public class MainActivity extends AppCompatActivity {

    private Button mPutUrl;
    private ImageView mShowImage;

    private String href = "http://img1.3lian.com/2015/w7/68/d/85.jpg";
    private List<String> mUrls;

    private int currentPosition;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUrls = new ArrayList<>();
        mUrls = getArrayUrl();

        mPutUrl = (Button) findViewById(R.id.put_url_button);

        mShowImage = (ImageView) findViewById(R.id.put_only_one_url);

        ImageLoader.getInstance().displayImage(href,mShowImage);


        mPutUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mUrls.size(); i++) {
                    currentPosition = i;
                }
                Intent intent = new Intent(MainActivity.this,CustomPhotoActivity.class);
                intent.putExtra("urls", (Serializable) mUrls);
                intent.putExtra("position",currentPosition);
                startActivity(intent);
            }
        });

        mShowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,CustomPhotoActivity.class);
                List<String> list = new ArrayList<String>();
                list.add(href);
                intent.putExtra("urls", (Serializable) list);
                startActivity(intent);
            }
        });
    }

    private List<String> getArrayUrl() {

        Map<String,List> map = new HashMap<>();

        mUrls = new ArrayList();
        mUrls.add("http://img4.imgtn.bdimg.com/it/u=1419557827,2529008583&fm=21&gp=0.jpg");
        mUrls.add("http://img5q.duitang.com/uploads/item/201503/07/20150307203721_nnS2E.png");
        mUrls.add("http://img15.3lian.com/2015/a1/16/d/202.jpg");
        mUrls.add("http://img15.3lian.com/2015/a1/16/d/202.jpg");
        map.put("list",mUrls);

        return mUrls;
    }
}
