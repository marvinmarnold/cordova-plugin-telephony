
package org.stingraymappingproject.cordova;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;

import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

public class Telephony extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

        if (action.equals("getCid")) {
            Context context = this.cordova.getActivity().getApplicationContext();
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            int phoneType = tm.getPhoneType();
            JSONObject result = new JSONObject();
            String phoneTypeName = "unknown";

            switch(phoneType) {
                case TelephonyManager.PHONE_TYPE_NONE:
                case TelephonyManager.PHONE_TYPE_SIP:
                case TelephonyManager.PHONE_TYPE_GSM:
                    result = buildGSMResult(tm);
                    phoneTypeName = "gsm";
                    break;
                case TelephonyManager.PHONE_TYPE_CDMA:
                    phoneTypeName = "cdma";
                    break;
            }
            result.put("phoneType", phoneTypeName);
            callbackContext.success(result);

            return true;

        } else {

            return false;

        }
    }

    private JSONObject buildGSMResult(TelephonyManager tm) {
        JSONObject result = new JSONObject();
        String mncMCC = tm.getNetworkOperator();

        if (mncMCC != null && mncMCC.length() >= 3 ) {
            try {
//                GsmCellLocation gsmCellLocation = (GsmCellLocation) tm.getCellLocation();
                result.put("mnc", Integer.parseInt(mncMCC.substring(3)));
                result.put("mcc", Integer.parseInt(mncMCC.substring(0, 3)));

                GsmCellLocation gsmCellLocation = (GsmCellLocation) tm.getCellLocation();
                if (gsmCellLocation != null) {
                    result.put("cid", gsmCellLocation.getCid());
                    result.put("lac", gsmCellLocation.getLac());
                    result.put("psc", gsmCellLocation.getPsc());
                }
            }catch (Exception e) {
            }

        }


        return result;
    }


}
