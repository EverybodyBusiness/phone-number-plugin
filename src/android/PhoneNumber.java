package org.apache.cordova.phonenumber;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.content.pm.PackageManager;

import android.util.Log;

public class PhoneNumber extends CordovaPlugin {
  public static final int PHONE_NUMBER_REQ_CODE = 0;
  public static final int PHONE_STATE_REQ_CODE = 1;
  public static final int PERMISSION_DENIED_ERROR = 20;
  public static final String PHONE_NUMBER = Manifest.permission.READ_PHONE_NUMBERS;
  public static final String PHONE_STATE = Manifest.permission.READ_PHONE_STATE;

  private JSONArray executeArgs;

  private SubscriptionManager mSubscriptionManager;

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
  }

  protected void getCallPermission(int requestCode) {
    if(requestCode == PHONE_NUMBER_REQ_CODE){
      cordova.requestPermission(this, requestCode, PHONE_NUMBER);
    } else if(requestCode == PHONE_STATE_REQ_CODE){
      cordova.requestPermission(this, requestCode, PHONE_STATE);
    }
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    this.executeArgs = args;

    if (cordova.hasPermission(PHONE_NUMBER) && cordova.hasPermission(PHONE_STATE)) {
      getPhoneNumber(executeArgs, callbackContext);
    } else {
      if(!cordova.hasPermission(PHONE_NUMBER)){
        getCallPermission(PHONE_NUMBER_REQ_CODE);
      }

      if(!cordova.hasPermission(PHONE_STATE)){
        getCallPermission(PHONE_STATE_REQ_CODE);
      }
    }


    return true;
  }

  private void getPhoneNumber(JSONArray args, CallbackContext callbackContext) throws JSONException {
    try {
      JSONObject r = new JSONObject();
      JSONArray phonenumbers = new JSONArray();

      if (android.os.Build.VERSION.SDK_INT >= 33) {
        Context context = cordova.getActivity().getApplicationContext();
        mSubscriptionManager = (SubscriptionManager) cordova.getActivity().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        List<SubscriptionInfo> subInfoList = mSubscriptionManager.getActiveSubscriptionInfoList();

        for (SubscriptionInfo subscriptionInfo : subInfoList) {
          phonenumbers.put(mSubscriptionManager.getPhoneNumber(subscriptionInfo.getSubscriptionId()));
        }
      }
      else{
        TelephonyManager tm = (TelephonyManager) cordova.getActivity().getSystemService(Context.TELEPHONY_SERVICE);

        phonenumbers.put(tm.getLine1Number());
      }

      r.put("phoneNumbers", phonenumbers);
      r.put("test", "testMessage");

      callbackContext.success(r);
    } catch (Exception e) {
      callbackContext.error(e.getMessage());
    }
  }

  private boolean isTelephonyEnabled() {
    TelephonyManager tm = (TelephonyManager) cordova.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
    return tm != null && tm.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
  }
}
