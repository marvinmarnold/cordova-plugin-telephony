package org.stingraymappingproject.cordova;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.stingraymappingproject.cordova.listeners.TelephonyStateListener;
import org.stingraymappingproject.cordova.utils.CellInfoUtil;
import org.stingraymappingproject.cordova.utils.CellLocationUtil;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import java.util.List;

import android.content.Context;
import android.util.Log;
/*
// Much credit to AIMSICD
// https://github.com/CellularPrivacy/Android-IMSI-Catcher-Detector/blob/development/AIMSICD/src/main/java/com/secupwn/aimsicd/utils/Device.java
//
// AND
//
// SnoopSnitch
// https://opensource.srlabs.de/git/snoopsnitch.git
//
// AND
//
// cordova-plugin-sim
// https://github.com/pbakondy/cordova-plugin-sim/blob/master/src/android/com/pbakondy/Sim.java
*/

public class Telephony extends CordovaPlugin {
    private static final String TAG = "TELEPHONY";

    // API required for advanced readings
    public final static int ADVANCED_VERSION_CODE = Build.VERSION_CODES.JELLY_BEAN_MR2;

    // Fields returned
    public static final String MCC = "mcc";
    public static final String MNC = "mnc";
    public static final String CID = "cid";
    public static final String LAC = "lac";
    public static final String NETWORK_TYPE = "networkType";
    public static final String PSC = "psc";
    public static final String SIGNAL_STRENGTH = "signalStrength";
    public static final String PHONE_TYPE = "phoneType";

    public static final String PHONE_TYPE_ANDROID_V1_SIM = "android-v1-sim";
    public static final String PHONE_TYPE_ANDROID_V1_GSM = "android-v1-gsm";
    public static final String PHONE_TYPE_ANDROID_V1_NEIGHBOR = "android-v1-neighbor";
    public static final String PHONE_TYPE_ANDROID_V17_GSM = "android-v17-gsm";
    public static final String PHONE_TYPE_ANDROID_V17_CDMA = "android-v17-cdma";
    public static final String PHONE_TYPE_ANDROID_V17_LTE = "android-v17-lte";
    public static final String PHONE_TYPE_ANDROID_V17_WCDMA = "android-v17-wcdma";

    // Actions
    private static final String ACTION_GET_TELEPHONY_INFO = "getTelephonyInfo";
    private static final String LISTEN_TELEPHONY_INFO = "listenTelephonyInfo";

    // Permissions
    private static final String HAS_READ_PERMISSION = "hasReadPermission";
    private static final String REQUEST_READ_PERMISSION = "requestReadPermission";

    private static final String[] REQUIRED_PERMISSIONS = {
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.CHANGE_NETWORK_STATE,
//        Manifest.permission.WAKE_LOCK,
//        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//        Manifest.permission.RECEIVE_BOOT_COMPLETED
    };

    private TelephonyStateListener telephonyStateListener;

    @Override
    public boolean execute(String action, JSONArray data, final CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, "execute " + action);

        if (action.equals(ACTION_GET_TELEPHONY_INFO)) {
            Context context = this.cordova.getActivity().getApplicationContext();
            final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            if (Build.VERSION.SDK_INT >= ADVANCED_VERSION_CODE) {
                Log.d(TAG, "Newer than advanced");
                cordova.getThreadPool().execute(new Runnable() {
                    public void run() {
                        List<JSONObject> readings = CellInfoUtil.getAllCellInfo(telephonyManager);

                        // Unable to get all cell info
                        if(readings == null) {
                            Log.d(TAG, "Fallback to older API");
                            JSONObject reading = CellLocationUtil.getReading(telephonyManager);

                            // Unable to get any cell info
                            if(reading == null) {
                                Log.d(TAG, "Reading null");
                                callbackContext.error("Could not get any readings");
                            } else {
                                Log.d(TAG, "Reading not null");
                                callbackContext.success(reading);
                            }

                        } else {
                            Log.d(TAG, "Got some readings " + readings.size());
                            splitAndSendReadings(callbackContext, readings);
                        }
                    }
                });
            } else {
                cordova.getThreadPool().execute(new Runnable() {
                    public void run() {
                Log.d(TAG, "Older than advanced");
                        JSONObject reading = CellLocationUtil.getReading(telephonyManager);
                        callbackContext.success(reading);
                    }
                });

            }
            return true;

        } else if (action.equals(LISTEN_TELEPHONY_INFO)) {
            Log.d(TAG, "LISTEN TELEPHONY INFO");
            Context context = this.cordova.getActivity().getApplicationContext();
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            if(telephonyStateListener == null) {
                telephonyStateListener = new TelephonyStateListener(telephonyManager, callbackContext);
                if (Build.VERSION.SDK_INT >= ADVANCED_VERSION_CODE) {
                    telephonyManager.listen(telephonyStateListener,
                            PhoneStateListener.LISTEN_CELL_INFO |
                            PhoneStateListener.LISTEN_CELL_LOCATION |
                            PhoneStateListener.LISTEN_DATA_CONNECTION_STATE |
                            PhoneStateListener.LISTEN_SERVICE_STATE |
                            PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                    );
                } else {
                    telephonyManager.listen(telephonyStateListener,
                            PhoneStateListener.LISTEN_CELL_LOCATION |
                            PhoneStateListener.LISTEN_DATA_CONNECTION_STATE |
                            PhoneStateListener.LISTEN_SERVICE_STATE |
                            PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                    );
                }
            }
            return true;
        } else if (action.equals(HAS_READ_PERMISSION)) {
            Log.d(TAG, "HAS READ PERMISSION");
            hasReadPermission(callbackContext);
            return true;
        } else if (action.equals(REQUEST_READ_PERMISSION)) {
            Log.d(TAG, "REQUEST READ POSITION");
            requestReadPermission(callbackContext);
            return true;
        }
        else {
            return false;
        }
    }

    public static void splitAndSendReadings(CallbackContext callbackContext, List<JSONObject> readings) {
        if(readings == null)
            return;

        for(JSONObject reading : readings) {
            callbackContext.success(reading);
        }
    }

    private void hasReadPermission(CallbackContext callbackContext) {

        boolean hasPermission = true;

        for(String requiredPermission : REQUIRED_PERMISSIONS) {
            hasPermission = hasPermission && hasPermission(requiredPermission);
        }

        PluginResult result = new PluginResult(PluginResult.Status.OK, hasPermission);

        callbackContext.sendPluginResult(result);
    }

    private boolean hasPermission(String permissionType) {
        // In older APIs, permissions granted at install time
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }

        // Must check permission at run time for new API
        return ContextCompat.checkSelfPermission(this.cordova.getActivity(), permissionType) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void requestReadPermission(CallbackContext callbackContext) {
        String requestType = null;
        for(String requiredPermission : REQUIRED_PERMISSIONS) {
            if(!hasPermission(requiredPermission))
                requestType = requiredPermission;
        }

        if(requestType != null)
            requestPermission(requestType);

        callbackContext.success();
    }

    private void requestPermission(String type) {
        if (!hasPermission(type)) {
            ActivityCompat.requestPermissions(this.cordova.getActivity(), new String[]{type}, 18643);
        }
    }
}