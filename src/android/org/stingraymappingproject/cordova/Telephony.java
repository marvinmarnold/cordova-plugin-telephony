package org.stingraymappingproject.cordova;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.telephony.NeighboringCellInfo;

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
    private static final String TAG = "cordova-telephony";

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
//        Manifest.permission.ACCESS_NETWORK_STATE,
//        Manifest.permission.CHANGE_NETWORK_STATE,
//        Manifest.permission.WAKE_LOCK,
//        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//        Manifest.permission.RECEIVE_BOOT_COMPLETED
    };

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, "execute " + action);

        if (action.equals(ACTION_GET_TELEPHONY_INFO)) {
            Context context = this.cordova.getActivity().getApplicationContext();
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            // Initialize empty JSONObject for response
            JSONObject result = new JSONObject();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {

            } else {

            }

            callbackContext.success(result);

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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private JSONObject refreshV18(TelephonyManager tm, JSONObject result) {
        int tSignalStrength = -1;
        int tMnc = -1;
        int tMcc = -1;
        int tCid = -1;
        int tLac = -1;
        int tPsc = -1;

        try {
            Log.d(TAG, "try get all cell info");

            List<CellInfo> cellInfoList = tm.getAllCellInfo();
            Log.d(TAG, "got cell info");

            if (cellInfoList != null) {
                Log.d(TAG, "cell info not null");

                for (final CellInfo info : cellInfoList) {
                    Log.d(TAG, "another cell info");

                    String tPhoneType = null;

                    if (info instanceof CellInfoGsm) {
                        Log.d(TAG, "its CellInfoGsm");
                        final CellSignalStrengthGsm signalStrength = ((CellInfoGsm) info).getCellSignalStrength();
                        final CellIdentityGsm identityGsm = ((CellInfoGsm) info).getCellIdentity();

                        result.put("debug", "gsm18");
                        tPhoneType = "android-v17-gsm";
                        tSignalStrength = signalStrength.getDbm();
                        tMnc = identityGsm.getMnc();
                        tMcc = identityGsm.getMcc();
                        tCid = identityGsm.getCid();
                        tLac = identityGsm.getLac();
                        tPsc = identityGsm.getPsc();
                    } else if (info instanceof CellInfoCdma) {
                        Log.d(TAG, "its CellInfoCdma");
                        final CellSignalStrengthCdma signalStrength = ((CellInfoCdma) info).getCellSignalStrength();
                        final CellIdentityCdma identityCdma = ((CellInfoCdma) info).getCellIdentity();

                        result.put("debug", "cdma");
                        tPhoneType = "android-v17-cdma";
                        tSignalStrength = signalStrength.getDbm();
                        tMnc = identityCdma.getSystemId();
//                        tMcc = identityCdma.getMcc();
                        tCid = identityCdma.getBasestationId();
                        tLac = identityCdma.getNetworkId();
//                        tPsc = identityCdma.getPsc();
                    } else if (info instanceof CellInfoLte) {
                        Log.d(TAG, "its CellInfoLte");
                        final CellSignalStrengthLte signalStrength = ((CellInfoLte) info).getCellSignalStrength();
                        final CellIdentityLte identityLte = ((CellInfoLte) info).getCellIdentity();

                        result.put("debug", "lte");
                        tPhoneType = "android-v17-lte";
                        tSignalStrength = signalStrength.getDbm();
                        tMnc = identityLte.getMnc();
                        tMcc = identityLte.getMcc();
                        tCid = identityLte.getCi();
//                        tLac = identityGsm.getLac();
//                        tPsc = identityGsm.getPsc();
//                        pDevice.mCell.setTimingAdvance(lte.getTimingAdvance());
                    } else if (info instanceof CellInfoWcdma) {
                        Log.d(TAG, "its CellInfoWcdmaParser");
                        final CellSignalStrengthWcdma signalStrength = ((CellInfoWcdma) info).getCellSignalStrength();
                        final CellIdentityWcdma identityWcdma = ((CellInfoWcdma) info).getCellIdentity();

                        result.put("debug", "wcdma");
                        tPhoneType = "android-v17-gsm";
                        tSignalStrength = signalStrength.getDbm();
                        tMnc = identityWcdma.getMnc();
                        tMcc = identityWcdma.getMcc();
                        tCid = identityWcdma.getCid();
                        tLac = identityWcdma.getLac();
                        tPsc = identityWcdma.getPsc();
                    }

                    if(tPhoneType != null) {
                        Log.d(TAG, "its phone type");
                        result.put("phoneType", tPhoneType);
                    }
                    if(tSignalStrength != -1) {
                        Log.d(TAG, "its signal strength");
                        result.put("signalStrength", tSignalStrength);
                    }

                    if(tMcc != -1) {
                        result.put("mcc", tMcc);
                    }
                    if(tMnc != -1) {
                        result.put("mnc", tMnc);
                    }
                    if(tCid != -1) {
                        result.put("cid", tCid);
                    }
                    if(tLac != -1) {
                        result.put("lac", tLac);
                    }
                    if(tPsc != -1) {
                        result.put("psc", tPsc);
                    }

                    if(result.get("debug") != null) {
                       result.put("debug", result.getInt("mnc"));
                    }
                }
            }
        }catch (Exception e) {
        }

        // Fallback to lower API if values appear incorrect
        if((tMnc == Integer.MAX_VALUE) || (tMnc == -1)) {
            Log.d(TAG, "its max value MNC");
            result = refreshV1(tm, result);
        }

        Log.d(TAG, "GOT TO THE END");
        return result;
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
        for(String requiredPermission : REQUIRED_PERMISSIONS) {
            requestPermission(requiredPermission);
        }

        callbackContext.success();
    }

    private void requestPermission(String type) {
        if (!hasPermission(type)) {
            ActivityCompat.requestPermissions(this.cordova.getActivity(), new String[]{type}, 18643);
        }
    }
}