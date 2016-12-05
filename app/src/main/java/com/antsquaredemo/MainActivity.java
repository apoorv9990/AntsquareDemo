package com.antsquaredemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.antsquaredemo.adapters.FacebookPostAdapter;
import com.antsquaredemo.adapters.ImageGridRecyclerViewAdapter;
import com.antsquaredemo.models.FacebookPost;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView likesTextView;
    private TextView followersTextView;
    private TextView usernameTextView;

    private ImageView heartIconImageView;
    private ImageView yelpIconImageView;
    private ImageView facebookIconImageView;
    private ImageView twitterIconImageView;

    private ProgressDialog mProgressDialog;

    private JSONObject profileJsonObject;

    private ArrayList<String> imageURLArrayList = new ArrayList<>();
    private ArrayList<FacebookPost> posts = new ArrayList<>();

    private GridLayoutManager mGridLayoutManager;

    private RecyclerView gridImagesRecyclerView;
    private RecyclerView facebookPostsRecyclerView;

    private ImageGridRecyclerViewAdapter gridRecyclerViewAdapter;
    private FacebookPostAdapter facebookPostAdapter;

    private LinearLayout tweetsLinearLayout;
    private ScrollView tweetsParentLinearLayout;

    private WebView yelpWebView;

    private CallbackManager callbackManager;

    private TwitterAuthClient mTwitterAuthClient;

    private RequestQueue queue;

    private String yelpURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameTextView = (TextView) findViewById(R.id.profile_name_textview);
        likesTextView = (TextView) findViewById(R.id.profile_likes_textview);
        followersTextView = (TextView) findViewById(R.id.profile_followers_textview);
        usernameTextView = (TextView) findViewById(R.id.profile_username_textview);

        heartIconImageView = (ImageView) findViewById(R.id.heart_icon_imageview);
        yelpIconImageView = (ImageView) findViewById(R.id.yelp_icon_imageview);
        facebookIconImageView = (ImageView) findViewById(R.id.facebook_icon_imageview);
        twitterIconImageView = (ImageView) findViewById(R.id.twitter_icon_imageview);

        heartIconImageView.setOnClickListener(onClickListener);
        yelpIconImageView.setOnClickListener(onClickListener);
        facebookIconImageView.setOnClickListener(onClickListener);
        twitterIconImageView.setOnClickListener(onClickListener);

        mGridLayoutManager = new GridLayoutManager(MainActivity.this, 3);

        gridImagesRecyclerView = (RecyclerView)findViewById(R.id.grid_recyclerview);
        facebookPostsRecyclerView = (RecyclerView) findViewById(R.id.posts_recyclerview);

        tweetsLinearLayout = (LinearLayout) findViewById(R.id.tweets_layout);
        tweetsParentLinearLayout = (ScrollView) findViewById(R.id.tweets_parent_layout);

        yelpWebView = (WebView) findViewById(R.id.yelp_webview);

        gridImagesRecyclerView.setHasFixedSize(true);
        gridImagesRecyclerView.setLayoutManager(mGridLayoutManager);

        facebookPostsRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.show();

        mTwitterAuthClient= new TwitterAuthClient();

        initializeFacebook();

        loadInitialData();

        yelpWebView.getSettings().setJavaScriptEnabled(true);
        yelpWebView.getSettings().setLoadWithOverviewMode(true);
        yelpWebView.getSettings().setUseWideViewPort(true);
        yelpWebView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mProgressDialog.hide();
                view.loadUrl(url);

                return true;
            }
            @Override
            public void onPageFinished(WebView view, final String url) {
                mProgressDialog.hide();
            }
        });

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.antsquaredemo",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                System.err.println("KeyHash:"+ Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }
    }

    private void initializeFacebook()
    {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(
                callbackManager,
                new FacebookCallback < LoginResult > () {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // Handle success
                        Toast.makeText(MainActivity.this, "Get posts", Toast.LENGTH_SHORT).show();
                        getPosts();
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException exception) {
                    }
                }
        );
    }

    private void getPosts()
    {
        facebookPostsRecyclerView.setVisibility(View.VISIBLE);
        tweetsParentLinearLayout.setVisibility(View.GONE);
        gridImagesRecyclerView.setVisibility(View.GONE);
        yelpWebView.setVisibility(View.GONE);

        if(posts.size() > 0) {
            mProgressDialog.hide();
            return;
        }

        //        to get photos in the posts too
        //        1061238805/feed?fields=attachments,child_attachments,message,story,created_time
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/feed?fields=attachments,child_attachments,message,story,created_time,permalink_url",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                                        /* handle the result */
                        try{

                            JSONArray postsArray = response.getJSONObject().getJSONArray("data");

                            posts = new ArrayList<>();

                            for(int index = 0; index < postsArray.length(); index++)
                            {
                                FacebookPost post = new FacebookPost();
                                JSONObject postJson = postsArray.getJSONObject(index);

                                if(postJson.has("story"))
                                    post.setStory(postJson.getString("story"));

                                if(postJson.has("message"))
                                    post.setMessage(postJson.getString("message"));

                                if(postJson.has("created_time"))
                                    post.setCreated_time(postJson.getString("created_time"));

                                if(postJson.has("id"))
                                    post.setId(postJson.getString("id"));

                                if(postJson.has("attachments")){

                                    JSONObject attachments = postJson.getJSONObject("attachments");

                                    if(attachments.has("data")) {

                                        JSONArray attachmentsArray = attachments.getJSONArray("data");

                                        if(attachmentsArray.length() > 0) {

                                            if(((JSONObject) attachmentsArray.get(0)).has("media")) {

                                                JSONObject media = ((JSONObject) attachmentsArray.get(0)).getJSONObject("media");

                                                post.setImageURL(media.getJSONObject("image").getString("src"));
                                            }
                                        }
                                    }
                                }

                                posts.add(post);
                            }

                            facebookPostAdapter = new FacebookPostAdapter(MainActivity.this, posts);
                            facebookPostsRecyclerView.setAdapter(facebookPostAdapter);
                            mProgressDialog.hide();

                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                }
        ).executeAsync();
    }

    private void getTweets()
    {
        tweetsParentLinearLayout.setVisibility(View.VISIBLE);
        gridImagesRecyclerView.setVisibility(View.GONE);
        facebookPostsRecyclerView.setVisibility(View.GONE);
        yelpWebView.setVisibility(View.GONE);

        if(tweetsLinearLayout.getChildCount() > 0) {
            mProgressDialog.hide();
            return;
        }

        TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();

        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(twitterSession);
        twitterApiClient.getStatusesService().userTimeline(twitterSession.getUserId(), twitterSession.getUserName(), 200, null, null, false, false, false, false).enqueue(new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> result) {
                ArrayList<Long> tweetIds = new ArrayList<>();

                for (Tweet tweet : result.data) {
                    // here you will get list
                    tweetIds.add(tweet.getId());
                }

                TweetUtils.loadTweets(tweetIds, new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> result) {
                        for (Tweet tweet : result.data) {
                            tweetsLinearLayout.addView(new TweetView(MainActivity.this, tweet));
                        }

                        mProgressDialog.hide();
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        mProgressDialog.hide();
                        Toast.makeText(MainActivity.this, "Failed to load tweets", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void failure(TwitterException exception) {
                mProgressDialog.hide();
                Toast.makeText(MainActivity.this, "Failed to get tweets", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadInitialData()
    {
        queue = Volley.newRequestQueue(this);
        String url ="https://core1.antsquare.com/v5/users/179/mpview";
        String imageurl ="https://core1.antsquare.com/v5/users/179/resources";

        // Request a string response from the provided URL.
        StringRequest stringRequest = getProfileData(url);

        // Request a string response from the provided URL.
        StringRequest imageRequest = getImages(imageurl);

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        queue.add(imageRequest);
    }

    private StringRequest getProfileData(String url)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            profileJsonObject = new JSONObject(response);

                            yelpURL = profileJsonObject.getString("external_url");

                            nameTextView.setText(profileJsonObject.getString("name"));
                            usernameTextView.setText(profileJsonObject.getString("username"));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                likesTextView.setText(Html.fromHtml("<html><body><b>"+profileJsonObject.getString("total_moment_likes")+"</b> likes</body></html>", Html.FROM_HTML_MODE_COMPACT));
                            }
                            else
                            {
                                likesTextView.setText(Html.fromHtml("<html><body><b>"+profileJsonObject.getString("total_moment_likes")+"</b> likes</body></html>"));
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                followersTextView.setText(Html.fromHtml("<html><body><b>" + profileJsonObject.getString("total_followers") + "</b> likes</body></html>", Html.FROM_HTML_MODE_COMPACT));
                            }
                            else
                            {
                                followersTextView.setText(Html.fromHtml("<html><body><b>" + profileJsonObject.getString("total_followers") + "</b> likes</body></html>"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.hide();
                Toast.makeText(MainActivity.this, "Oops something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        return stringRequest;
    }

    private StringRequest getImages(String imageurl)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, imageurl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject responseJSON = new JSONObject(response);
                            JSONArray resourcesArray = responseJSON.getJSONArray("resources");

                            for (int index = 0; index < resourcesArray.length(); index++)
                            {
                                JSONArray imagesArray = ((JSONObject)resourcesArray.get(index)).getJSONArray("images");
                                for (int imageIndex = 0; imageIndex < imagesArray.length(); imageIndex++)
                                    imageURLArrayList.add(imagesArray.getString(imageIndex));
                            }

                            gridRecyclerViewAdapter = new ImageGridRecyclerViewAdapter(MainActivity.this, imageURLArrayList);
                            gridImagesRecyclerView.setAdapter(gridRecyclerViewAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mProgressDialog.hide();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.hide();
                Toast.makeText(MainActivity.this, "Oops something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        return stringRequest;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
        }
        else if(FacebookSdk.isFacebookRequestCode(requestCode)) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(v.getId() == R.id.heart_icon_imageview)
            {
                facebookPostsRecyclerView.setVisibility(View.GONE);
                tweetsParentLinearLayout.setVisibility(View.GONE);
                gridImagesRecyclerView.setVisibility(View.VISIBLE);
                yelpWebView.setVisibility(View.GONE);
            }
            else if(v.getId() == R.id.yelp_icon_imageview)
            {
                mProgressDialog.show();
                facebookPostsRecyclerView.setVisibility(View.GONE);
                tweetsParentLinearLayout.setVisibility(View.GONE);
                gridImagesRecyclerView.setVisibility(View.GONE);
                yelpWebView.setVisibility(View.VISIBLE);

                yelpWebView.loadUrl(yelpURL);
            }
            else if(v.getId() == R.id.facebook_icon_imageview)
            {
                boolean loggedIn = AccessToken.getCurrentAccessToken() != null;
                mProgressDialog.show();

                if(loggedIn)
                {
                    getPosts();
                }
                else {
                    LoginManager.getInstance().logInWithReadPermissions(
                            MainActivity.this,
                            Arrays.asList("user_photos", "email", "user_birthday", "public_profile",
                                    "user_friends", "user_posts", "user_about_me")
                    );
                }
            }
            else if( v.getId() == R.id.twitter_icon_imageview)
            {
                TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();

                mProgressDialog.setMessage("Loading Tweets...");
                mProgressDialog.show();

                if(twitterSession != null) {
                    getTweets();
                }
                else {
                    mTwitterAuthClient.authorize(MainActivity.this, new com.twitter.sdk.android.core.Callback<TwitterSession>() {

                        @Override
                        public void success(Result<TwitterSession> twitterSessionResult) {
                            // Success

                            getTweets();
                        }

                        @Override
                        public void failure(TwitterException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }
    };
}
