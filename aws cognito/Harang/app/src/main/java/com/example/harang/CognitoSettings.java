package com.example.harang;

import android.content.Context;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.regions.Regions;

public class CognitoSettings {

    private String userPoolId = "us-east-1_wmoc2HLpY";
    private String clientId = "3mi3rng12gj8rrkiavuj0bqcvk";
    private String clientSecret = "1rrp5dsi7s7jo2vjsn7sbeveolqbafj97nrbhtsmib4lsob30rvp";
    private Regions cognitoRegion = Regions.US_EAST_1;

    private Context context;

    public CognitoSettings(Context context){
        this.context = context;
    }

    public String getUserPoolId(){
        return userPoolId;
    }

    public String getClientId(){
        return clientId;
    }

    public String getClientSecret(){
        return clientSecret;
    }

    public Regions getCognitoRegion(){
        return cognitoRegion;
    }

    public CognitoUserPool getUserPool(){
        return new CognitoUserPool(context, userPoolId, clientId, clientSecret,cognitoRegion);
    }
}
