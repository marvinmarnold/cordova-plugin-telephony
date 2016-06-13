package org.stingraymappingproject.cordova.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.stingraymappingproject.cordova.Telephony;
import org.stingraymappingproject.cordova.parsers.CellInfoCdmaParser;
import org.stingraymappingproject.cordova.parsers.CellInfoGsmParser;
import org.stingraymappingproject.cordova.parsers.CellInfoLteParser;
import org.stingraymappingproject.cordova.parsers.CellInfoWcdmaParser;

import java.util.ArrayList;
import java.util.List;

public class CellInfoUtil {
    private final static String TAG = "CellInfoUtil";

    /**
     *
     * @return JSONArray of JSONObjects of CellInfo or null if older Android
     */
    @TargetApi(Telephony.ADVANCED_VERSION_CODE)
    public static List<JSONObject> getAllCellInfo(TelephonyManager telephonyManager) {
        if (Build.VERSION.SDK_INT >= Telephony.ADVANCED_VERSION_CODE) {
            List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();

            return parseCellInfoList(cellInfoList);
        }

        return null;
    }

    @TargetApi(Telephony.ADVANCED_VERSION_CODE)
    public static List<JSONObject> parseCellInfoList(List<CellInfo> cellInfoList) {
        List<JSONObject> cellInfoArray = new ArrayList<JSONObject>();

        if(cellInfoList == null)
            return null;

        for (final CellInfo cellInfo : cellInfoList) {
            JSONObject infoObj;

            if (cellInfo instanceof CellInfoGsm) {
                infoObj = CellInfoGsmParser.parse((CellInfoGsm) cellInfo);
            } else if (cellInfo instanceof CellInfoCdma) {
                infoObj = CellInfoCdmaParser.parse((CellInfoCdma) cellInfo);
            } else if (cellInfo instanceof CellInfoLte) {
                infoObj = CellInfoLteParser.parse((CellInfoLte) cellInfo);
            } else if (cellInfo instanceof CellInfoWcdma) {
                infoObj = CellInfoWcdmaParser.parse((CellInfoWcdma) cellInfo);
            } else {
                infoObj = new JSONObject();
            }

            infoObj = sanitizeInfoObj(infoObj);
            cellInfoArray.add(infoObj);
        }

        return cellInfoArray;
    }

    private static boolean isFieldValid(int field) {
        return (field != -1) && (field != Integer.MAX_VALUE);
    }

    private static JSONObject sanitizeInfoObj(JSONObject infoObj) {
        try {
            if(!isFieldValid(infoObj.getInt(Telephony.SIGNAL_STRENGTH))) {
                infoObj.remove(Telephony.SIGNAL_STRENGTH);
                Log.d(TAG, "Removed invalid signal strength");
            }

            if(!isFieldValid(infoObj.getInt(Telephony.MCC))) {
                infoObj.remove(Telephony.MCC);
                Log.d(TAG, "Removed invalid MCC");
            }

            if(!isFieldValid(infoObj.getInt(Telephony.MNC))) {
                infoObj.remove(Telephony.MNC);
                Log.d(TAG, "Removed invalid MNC");
            }

            if(!isFieldValid(infoObj.getInt(Telephony.CID))) {
                infoObj.remove(Telephony.CID);
                Log.d(TAG, "Removed invalid CID");
            }

            if(!isFieldValid(infoObj.getInt(Telephony.LAC))) {
                infoObj.remove(Telephony.LAC);
                Log.d(TAG, "Removed invalid LAC");
            }

            if(!isFieldValid(infoObj.getInt(Telephony.PSC))) {
                infoObj.remove(Telephony.PSC);
                Log.d(TAG, "Removed invalid PSC");
            }

        } catch (JSONException e) {
            throw new Error("Error sanitizing values");
        }

        return infoObj;
    }

    public static JSONObject getNeighbors(JSONObject result, TelephonyManager telephonyManager) throws Error {
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
