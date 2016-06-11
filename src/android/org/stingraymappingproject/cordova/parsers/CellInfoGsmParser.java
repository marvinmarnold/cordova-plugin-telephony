package org.stingraymappingproject.cordova.parsers;

import android.annotation.TargetApi;
import android.telephony.CellIdentityGsm;
import android.telephony.CellInfoGsm;
import android.telephony.CellSignalStrengthGsm;
import android.util.Log;

import org.json.JSONObject;
import org.stingraymappingproject.cordova.Telephony;

public class CellInfoGsmParser {
    private final static String TAG = "CellInfoGsmParser";

    @TargetApi(Telephony.ADVANCED_VERSION_CODE)
    public static JSONObject parse(CellInfoGsm cellInfoGsm) {
        Log.d(TAG, "parse");
        final CellSignalStrengthGsm signalStrength = cellInfoGsm.getCellSignalStrength();
        final CellIdentityGsm identityGsm = cellInfoGsm.getCellIdentity();

//        result.put("debug", "gsm18");
//        tPhoneType = "android-v17-gsm";
//        tSignalStrength = signalStrength.getDbm();
//        tMnc = identityGsm.getMnc();
//        tMcc = identityGsm.getMcc();
//        tCid = identityGsm.getCid();
//        tLac = identityGsm.getLac();
//        tPsc = identityGsm.getPsc();
        return null;
    }
}
