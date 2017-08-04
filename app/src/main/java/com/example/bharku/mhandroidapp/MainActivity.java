package com.example.bharku.mhandroidapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentityprovider.model.StringAttributeConstraintsType;
import com.amazonaws.services.dynamodbv2.*;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE_JWT = "com.amazon.mhandroidapp.jwt";
    public static CognitoCachingCredentialsProvider userpoolscredentialsProvider;
    String JWTToken;

    private String poolId = "us-east-1_Tgwt5Sisz";
    private String clientId = "3l6jon02dq2ktq6u8j3gf0oqn1";
    private String clientSecret = "1q58rkfafv77hcv4p4psrm2k4k0h4cdielcsve68ql1t96ecbl1r";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void callCognitoUserPool(View view){
        ClientConfiguration clientConfiguration = new ClientConfiguration();

        // Create a CognitoUserPool object to refer to your user pool
        CognitoUserPool userPool = new CognitoUserPool(this.getApplicationContext(), poolId, clientId, clientSecret, clientConfiguration);

        // Create a CognitoUserAttributes object and add user attributes
        CognitoUserAttributes userAttributes = new CognitoUserAttributes();

        // Add the user attributes. Attributes are added as key-value pairs
        // Adding user's given name.
        // Note that the key is "given_name" which is the OIDC claim for given name
        userAttributes.addAttribute("given_name", "bharath");

        // Adding user's phone number
        userAttributes.addAttribute("phone_number", "123456789");

        // Adding user's email address
        userAttributes.addAttribute("email", "bharathkv@gmail.com");

        SignUpHandler signupCallback = new SignUpHandler() {

            @Override
            public void onSuccess(CognitoUser cognitoUser, boolean userConfirmed, CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
                // Sign-up was successful

                // Check if this user (cognitoUser) needs to be confirmed
                if(!userConfirmed) {
                    // This user must be confirmed and a confirmation code was sent to the user
                    // cognitoUserCodeDeliveryDetails will indicate where the confirmation code was sent
                    // Get the confirmation code from user
                    Log.e("test","Cognito usernotconfirmed: "+cognitoUser.getUserId());
                }
                else {
                    // The user has already been confirmed
                    Log.e("test","Cognito userconfirmed: "+cognitoUser.getUserId());
                }
            }

            @Override
            public void onFailure(Exception exception) {
                // Sign-up failed, check exception for the cause
                Log.e("test","Failed to signin");
            }
        };

        TextView usernameText = (TextView) findViewById(R.id.editText3);
        TextView passwordText = (TextView) findViewById(R.id.editText4);

        userPool.signUpInBackground(usernameText.getText().toString(), passwordText.getText().toString(), userAttributes, null, signupCallback);

    }

    public void loginUser(View view){
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        final CognitoUserPool userPool = new CognitoUserPool(this.getApplicationContext(), poolId, clientId, clientSecret, clientConfiguration);

        final Context xyz = this.getApplicationContext();
        // Callback handler for the sign-in process
        AuthenticationHandler authenticationHandler = new AuthenticationHandler() {

            @Override
            public void onSuccess(CognitoUserSession cognitoUserSession) {
                // Sign-in was successful, cognitoUserSession will contain tokens for the user
                Log.e("test","CognitoUserSession accesstoken: "+cognitoUserSession.getIdToken().getJWTToken());

                JWTToken = cognitoUserSession.getIdToken().getJWTToken(); //cognitoUserSession.getAccessToken().getJWTToken();

                userpoolscredentialsProvider = new CognitoCachingCredentialsProvider(xyz, "us-east-1:b4affe2a-bd39-481e-8ae2-de7895ce0a47", Regions.US_EAST_1);

                Map<String, String> logins = new HashMap<String, String>();
                logins.put("cognito-idp.us-east-1.amazonaws.com/us-east-1_Tgwt5Sisz", JWTToken);
                userpoolscredentialsProvider.setLogins(logins);

            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
                // The API needs user sign-in credentials to continue
                TextView usernameText = (TextView) findViewById(R.id.editText3);
                TextView passwordText = (TextView) findViewById(R.id.editText4);
                AuthenticationDetails authenticationDetails = new AuthenticationDetails(usernameText.getText().toString(), passwordText.getText().toString(), null);

                // Pass the user sign-in credentials to the continuation
                authenticationContinuation.setAuthenticationDetails(authenticationDetails);

                // Allow the sign-in to continue
                authenticationContinuation.continueTask();

                Log.e("test","getAuthenticationDetails: "+userId);
            }

            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
                // Multi-factor authentication is required; get the verification code from user
                //TextView mfaVerificationCode = (TextView) findViewById(R.id.mfaVerificationCode);
                //multiFactorAuthenticationContinuation.setMfaCode(mfaVerificationCode.getText().toString());
                // Allow the sign-in process to continue
                multiFactorAuthenticationContinuation.continueTask();
                Log.e("test","getMFACode");
            }

            @Override
            public void onFailure(Exception exception) {
                // Sign-in failed, check exception for the cause
                Log.e("test","onFailure:"+exception.getMessage());
            }
        };

        // Sign in the user
        userPool.getCurrentUser().getSessionInBackground(authenticationHandler);
    }


    public static CognitoCachingCredentialsProvider getUserPoolsCredentialsProvider(){
        return MainActivity.userpoolscredentialsProvider;
    }

    public void showDatabaseView(View view) {
        Intent intent = new Intent(this, DatabaseActivity.class);
        intent.putExtra(EXTRA_MESSAGE_JWT,this.JWTToken);
        startActivity(intent);

    }

}
