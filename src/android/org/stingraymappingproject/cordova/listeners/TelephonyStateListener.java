package org.stingraymappingproject.cordova.listeners;

import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.json.JSONObject;
import org.stingraymappingproject.cordova.Telephony;
import org.stingraymappingproject.cordova.utils.CellInfoUtil;
import org.stingraymappingproject.cordova.utils.CellLocationUtil;

import java.util.List;

/**
 * Modified from SnoopSnitch MsdService.java
 * https://opensource.srlabs.de/git/snoopsnitch.git
 */
public class TelephonyStateListener extends PhoneStateListener {
    private final static String TAG = "TelephonyStateListener";

    private final TelephonyManager telephonyManager;
    private final CallbackContext callbackContext;

    public TelephonyStateListener(TelephonyManager telephonyManager, CallbackContext callbackContext) {
        this.telephonyManager = telephonyManager;
        this.callbackContext = callbackContext;
    }

    @Override
    public void onCellLocationChanged(CellLocation location) {
        if (location instanceof GsmCellLocation) {
            JSONObject reading = CellLocationUtil.buildFromGsmCellLocation((GsmCellLocation) location, telephonyManager);

            // Send results to Cordova
            callbackContext.success(reading);

            // Some phones may not send onCellInfoChanged(). So let's record
            // the neighboring cells as well if the current serving cell
            // changes.
            List<JSONObject> allCellInfo = CellInfoUtil.getAllCellInfo(telephonyManager);
            Telephony.splitAndSendReadings(callbackContext, allCellInfo);
        } else {
            Log.d(TAG, "onCellLocationChanged() called with invalid location class: " + location.getClass());
        }
    }

    @Override
    public void onCellInfoChanged(List<CellInfo> cellInfoList) {
        if(cellInfoList == null || cellInfoList.size() == 0)
            return;

        List<JSONObject> changedCellInfo = CellInfoUtil.parseCellInfoList(cellInfoList);
        Telephony.splitAndSendReadings(callbackContext, changedCellInfo);
    }

}
