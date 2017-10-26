package com.laserscorpion.impromptu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class FacebookSignupActivity extends AppCompatActivity {
    private static final String TAG = "FacebookSignupActivity";
    FacebookSignupActivity activity = this;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "logged in");
                Intent intent = new Intent(activity, BasicProfileCompletionActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "cancelled");
                // do nothing
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, "error");
                ErrorDialog dialog = ErrorDialog.newInstance(exception.getMessage());
                dialog.show(getFragmentManager(), "FacebookError");
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "got activity result");
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
