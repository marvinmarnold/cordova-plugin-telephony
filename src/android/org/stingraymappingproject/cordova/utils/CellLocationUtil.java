package org.stingraymappingproject.cordova.utils;

import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.stingraymappingproject.cordova.Telephony;
import org.stingraymappingproject.cordova.parsers.GsmCellLocationParser;

public class CellLocationUtil {
    private static final String TAG = "CellLocationUtil";

    public static JSONObject getReading(TelephonyManager telephonyManager) {
        int phoneType = telephonyManager.getPhoneType();
        Log.d(TAG, "phoneType " + phoneType);

        switch (phoneType) {
            case TelephonyManager.PHONE_TYPE_NONE:
                Log.d(TAG, "PHONE_TYPE_NONE");
            case TelephonyManager.PHONE_TYPE_SIP:
                Log.d(TAG, "PHONE_TYPE_SIP");
                break;
            case TelephonyManager.PHONE_TYPE_GSM:
                Log.d(TAG, "PHONE_TYPE_GSM");
                GsmCellLocation gsmCellLocation = (GsmCellLocation) telephonyManager.getCellLocation();

                if(gsmCellLocation != null) {
                    Log.d(TAG, "gsmCellLocation not null");
                    return buildFromGsmCellLocation(gsmCellLocation, telephonyManager);
                }
                break;

            case TelephonyManager.PHONE_TYPE_CDMA:
                Log.d(TAG, "PHONE_TYPE_CDMA");
                // TODO
                break;
        }

        return null;
    }

    public static JSONObject buildFromGsmCellLocation(GsmCellLocation gsmCellLocation, TelephonyManager telephonyManager) {
        Log.d(TAG, "buildFromGsmCellLocation");
        JSONObject result = getNetworkOperator(telephonyManager);
        result = GsmCellLocationParser.parse(result, gsmCellLocation);
        return CellInfoUtil.getNeighbors(result, telephonyManager);
    }

    private static JSONObject getNetworkOperator(TelephonyManager telephonyManager) throws Error {
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
}
