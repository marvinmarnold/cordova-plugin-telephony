package com.megster.cordova;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
//import android.telephony.TelephonyManager;
import java.util.Set;

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

        LOG.d(TAG, "action = " + action);

        if (telephonyManager == null) {
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        }

        if (action.equals(GET_CID)) {
            return telephonyManager.getDeviceId();
        }
    }
}
