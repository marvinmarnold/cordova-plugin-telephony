package org.stingraymappingproject.cordova.listeners;

import android.annotation.TargetApi;
import android.os.Build;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.stingraymappingproject.cordova.Telephony;
import org.stingraymappingproject.cordova.parsers.CellInfoCdmaParser;
import org.stingraymappingproject.cordova.parsers.CellInfoGsmParser;
import org.stingraymappingproject.cordova.parsers.CellInfoLteParser;
import org.stingraymappingproject.cordova.parsers.CellInfoWcdmaParser;
import org.stingraymappingproject.cordova.parsers.GsmCellLocationParser;

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
            JSONObject result = getNetworkOperator();
            result = GsmCellLocationParser.parse(result, location);
            result = getNeighbors(result);

            // Send results to Cordova
            callbackContext.success(result);

            // Some phones may not send onCellInfoChanged(). So let's record
            // the neighboring cells as well if the current serving cell
            // changes.
            getAllCellInfo();
        } else {
            Log.d(TAG, "onCellLocationChanged() called with invalid location class: " + location.getClass());
        }
    }

    @Override
    public void onCellInfoChanged(List<CellInfo> cellInfoList) {
        if(cellInfoList == null || cellInfoList.size() == 0)
            return;

        processCellInfoList(cellInfoList);
    }

    @TargetApi(Telephony.ADVANCED_VERSION_CODE)
    private void getAllCellInfo() {
        if (Build.VERSION.SDK_INT >= Telephony.ADVANCED_VERSION_CODE) {
            List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();

            processCellInfoList(cellInfoList);
        }
    }

    @TargetApi(Telephony.ADVANCED_VERSION_CODE)
    private void processCellInfoList(List<CellInfo> cellInfoList) {
        JSONObject infoObj;

        for (final CellInfo cellInfo : cellInfoList) {
            if (cellInfo instanceof CellInfoGsm) {
                infoObj = CellInfoGsmParser.parse((CellInfoGsm) cellInfo);
            } else if (cellInfo instanceof CellInfoCdma) {
                infoObj = CellInfoCdmaParser.parse((CellInfoCdma) cellInfo);
            } else if (cellInfo instanceof CellInfoLte) {
                infoObj = CellInfoLteParser.parse((CellInfoLte) cellInfo);
            } else if (cellInfo instanceof CellInfoWcdma) {
                infoObj = CellInfoWcdmaParser.parse((CellInfoWcdma) cellInfo);
            }
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

    }



    private JSONObject getNetworkOperator() throws Error {
        JSONObject result = new JSONObject();
        String networkOperator = telephonyManager.getNetworkOperator();

        if(networkOperator.length() < 5) {
            Log.d(TAG, "Invalid network operator: " + networkOperator);
            throw new Error("Invalid network operator");
        }

        try {
            result.put(Telephony.MCC, networkOperator.substring(0,3));
            result.put(Telephony.MNC, networkOperator.substring(3));
        } catch (JSONException e) {
            throw new Error("Error getting network operator information");
        }

        return result;
    }

    private JSONObject getNeighbors(JSONObject result) throws Error {
        JSONArray ncNeighbors = new JSONArray();
        List<NeighboringCellInfo> ncList = telephonyManager.getNeighboringCellInfo();

        try {
            for(NeighboringCellInfo ncInfo : ncList) {
                JSONObject ncObj = new JSONObject();

                ncObj.put(Telephony.CID, ncInfo.getCid());
                ncObj.put(Telephony.LAC, ncInfo.getLac());
                ncObj.put(Telephony.NETWORK_TYPE, ncInfo.getNetworkType());
                ncObj.put(Telephony.PSC, ncInfo.getPsc());
                ncObj.put(Telephony.SIGNAL_STRENGTH, ncInfo.getRssi());

                ncNeighbors.put(ncObj);
            }
            result.put("neighbors", ncNeighbors);
        } catch (JSONException e) {
            throw new Error("Error getting neighbors");
        }

        return result;
    }
}
