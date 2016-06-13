package org.stingraymappingproject.cordova.parsers;

import android.annotation.TargetApi;
import android.telephony.CellIdentityGsm;
import android.telephony.CellInfoGsm;
import android.telephony.CellSignalStrengthGsm;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.stingraymappingproject.cordova.Telephony;

public class CellInfoGsmParser {
    private final static String TAG = "CellInfoGsmParser";

    @TargetApi(Telephony.ADVANCED_VERSION_CODE)
    public static JSONObject parse(CellInfoGsm cellInfoGsm) {
        Log.d(TAG, "parse");
        JSONObject result = new JSONObject();
        final CellSignalStrengthGsm signalStrength = cellInfoGsm.getCellSignalStrength();
        final CellIdentityGsm identityGsm = cellInfoGsm.getCellIdentity();

        try {
            result.put(Telephony.PHONE_TYPE, Telephony.PHONE_TYPE_ANDROID_V17_GSM);
            result.put(Telephony.SIGNAL_STRENGTH, signalStrength.getDbm());
            result.put(Telephony.MNC, identityGsm.getMnc());
            result.put(Telephony.MCC, identityGsm.getMcc());
            result.put(Telephony.CID, identityGsm.getCid());
            result.put(Telephony.LAC, identityGsm.getLac());
            result.put(Telephony.PSC, identityGsm.getPsc());
        } catch (JSONException e) {
            throw new Error("Error parsing GSM info");
        }

        return null;
    }
}
