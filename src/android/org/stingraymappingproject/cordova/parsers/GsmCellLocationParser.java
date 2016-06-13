package org.stingraymappingproject.cordova.parsers;

import android.telephony.CellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class GsmCellLocationParser {
    private final static String TAG = "GsmCellLocationParser";

    public static JSONObject parse(JSONObject result, GsmCellLocation gsmCellLocation) {
        Log.d(TAG, "parse");
        if(gsmCellLocation == null) {
            Log.d(TAG, "gsmCellLocation null");
            return result;
        }

        int cid = gsmCellLocation.getCid();
        int lac = gsmCellLocation.getLac();
        int psc = gsmCellLocation.getPsc();

        try {
            if(cid != Integer.MAX_VALUE && cid != -1)
                result.put("cid", cid);

            if(lac != Integer.MAX_VALUE && lac != -1)
                result.put("lac", lac);

            // PSC can be -1 or MAX_VALUE
            result.put("psc", psc);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }
}
