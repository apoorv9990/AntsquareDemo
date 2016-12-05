package com.antsquaredemo;

import android.app.Application;
import android.graphics.Bitmap;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

/**
 * Created by patel on 12/3/2016.

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));*/

public class AntsquareDemoApplication extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "qrg6LePeMGqljhqvTExGUsxJQ";
    private static final String TWITTER_SECRET = "t9Rd9EHlFBmIp3cheOzbUflOk0YtiK5SiZWrBSw4qOFLnUv7yr";

    @Override
    public void onCreate() {
        super.onCreate();

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        DisplayImageOptions.Builder displayBuilder = new DisplayImageOptions.Builder();
        displayBuilder.cacheInMemory(true);
        displayBuilder.cacheOnDisk(true);
        displayBuilder.bitmapConfig(Bitmap.Config.RGB_565);
        displayBuilder.imageScaleType(ImageScaleType.EXACTLY);
        DisplayImageOptions defaultOptions = displayBuilder.build();

        ImageLoaderConfiguration.Builder imageBuilder = new ImageLoaderConfiguration.Builder(this);
        imageBuilder.defaultDisplayImageOptions(defaultOptions);
        imageBuilder.threadPoolSize(5);
        imageBuilder.diskCacheExtraOptions(480, 320, null);
        ImageLoaderConfiguration imageLoaderConfiguration = imageBuilder.build();

        ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance*
        imageLoader.init(imageLoaderConfiguration);
    }
}

