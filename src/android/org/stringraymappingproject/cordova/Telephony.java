package org.stingraymappingproject.cordova;

import android.content.Context;
import android.telephony.TelephonyManager;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CordovaArgs;
import org.json.JSONException;
import org.json.JSONObject;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.Manifest;


/**
 * PhoneGap Plugin for accessing Telephony information
 */
public class Telephony extends CordovaPlugin {

    // actions
    private static final String GET_CID = "getCid";

    private TelephonyManager telephonyManager;

    // Debugging
    private static final String TAG = "Telephony";

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
      Context context = this.cordova.getActivity().getApplicationContext();

//        LOG.d(TAG, "action = " + action);

      if (telephonyManager == null) {
          telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
      }

      if (action.equals(GET_CID)) {
          JSONObject result = new JSONObject();
          result.put("sting", 923);

          callbackContext.success(result);
      }

      return true;
    }
}
