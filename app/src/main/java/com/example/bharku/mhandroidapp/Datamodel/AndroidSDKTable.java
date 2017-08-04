package com.example.bharku.mhandroidapp.Datamodel;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

@DynamoDBTable(tableName = "androidsdktable")
public class AndroidSDKTable{
    private String userId;

    @DynamoDBHashKey(attributeName = "userid")
    public String getUserId(){
        return this.userId;
    }

    public AndroidSDKTable(String userId){
        this.setUserId(userId);
    }

    private void setUserId(String userId) {
        this.userId = userId;
    }

}