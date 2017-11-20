package com.imagefilter.image.fb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import static com.imagefilter.image.fb.R.id.info;


public class MainActivity extends AppCompatActivity {
    String   profileImage,user_id;
    LoginButton loginButton;


    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        loginButton = (LoginButton) findViewById(R.id.login_button);

        loginButton.setReadPermissions("email", "public_profile");

        callbackManager = CallbackManager.Factory.create();

   


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

             user_id = loginResult.getAccessToken().getUserId();

                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {


                        try {
                           profileImage =  response.getJSONObject().getJSONObject("data").getString("url");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        desplayUserinfo(object);

                    }
                });

                Bundle parameters=new Bundle();

                parameters.putString("fields","picture.type(large),first_name,last_name,email,id");

                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();



            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }



        });


    }

    public void desplayUserinfo(JSONObject object)
    {
String first_name="",last_name="",email_id="",id="",picture="";



        try {
            first_name=object.getString("first_name");
            last_name=object.getString("last_name");
            email_id=object.getString("email");
           id=object.getString("id");

            picture=object.getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        final TextView tv1,tv2,tv3,tv4;

        final ImageView im;

        im=(ImageView)findViewById(R.id.imageView);


        tv1=(TextView)findViewById(R.id.textView);
        tv2=(TextView)findViewById(R.id.textView2);
        tv3=(TextView)findViewById(R.id.textView3);
        tv4=(TextView)findViewById(R.id.textView4);

        tv1.setText(first_name);

        tv2.setText(last_name);

        tv3.setText(email_id);

        tv4.setText(id);

        Glide.with(this)
                .load("https://graph.facebook.com/" + user_id+ "/picture?type=large")
                .into(im);


        AccessTokenTracker accessTokenTracker= new AccessTokenTracker()
        {


            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                if (newToken == null){
                    tv1.setText(" ");
                    tv2.setText(" ");
                    tv3.setText(" ");
                    tv4.setText(" ");
                    user_id="";

                    Glide.with(MainActivity.this)
                            .load("https://graph.facebook.com/" + user_id+ "/picture?type=large")
                            .into(im);



                }
            }
        };


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (AccessToken.getCurrentAccessToken() != null) ;



}}
