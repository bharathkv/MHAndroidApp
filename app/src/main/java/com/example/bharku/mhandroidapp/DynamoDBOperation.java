package com.example.bharku.mhandroidapp;

import android.content.Context;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;
import com.example.bharku.mhandroidapp.Datamodel.AndroidSDKTable;

/**
 * Created by bharku on 6/13/17.
 */

public class DynamoDBOperation {

    private static volatile DynamoDBOperation instance;
    private static DynamoDBMapper mapper;

    private String poolId = "us-east-1_Tgwt5Sisz";
    private String clientId = "3l6jon02dq2ktq6u8j3gf0oqn1";
    private String clientSecret = "1q58rkfafv77hcv4p4psrm2k4k0h4cdielcsve68ql1t96ecbl1r";

    private DynamoDBOperation(Context context,String jwttoken){
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(MainActivity.getUserPoolsCredentialsProvider());
        mapper = new DynamoDBMapper(ddbClient);
    }

    public static synchronized DynamoDBOperation getInstance(Context context, String JWTToken){
        if(instance==null){
            instance = new DynamoDBOperation(context,JWTToken);
        }

        return instance;
    }

    public void insertIntoAndroidSDKtable(final String userId){
        final DynamoDBOperation db123 = this;

        Thread thread1 = new Thread(new Runnable(){
            @Override
            public void run() {
                AndroidSDKTable androidSDKTable = new AndroidSDKTable(userId);
                db123.mapper.save(androidSDKTable);
            }
        });

        thread1.start();

    }


}
