package com.example.bharku.mhandroidapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;

public class DatabaseActivity extends AppCompatActivity {

    String JWTToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        this.JWTToken = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_JWT);
        Log.e("test","DatabaseActivity:OnCreate JWTToken: "+this.JWTToken);

        // Capture the layout's TextView and set the string as its text
        //TextView textView = (TextView) findViewById(R.id.textView);
        //textView.setText(message);

    }

    public void insertIntoDB1(View view) {
        Log.e("test","insertIntoDB1 JWTToken: "+this.JWTToken);
        DynamoDBOperation dbOperation = DynamoDBOperation.getInstance(view.getContext(),this.JWTToken);
        dbOperation.insertIntoAndroidSDKtable("987654321");
    }
}
