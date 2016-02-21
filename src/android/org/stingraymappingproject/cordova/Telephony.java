
package org.stingraymappingproject.cordova;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

import java.util.List;
import android.app.Activity;
import android.content.Context;

// Much credit to AIMSICD DeviceAPI.java
public class Telephony extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

        if (action.equals("refresh")) {
            Context context = this.cordova.getActivity().getApplicationContext();
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            JSONObject result;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
              result = refreshV18(tm);
            } else {
              result = refreshV1(tm);
            }

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
                result.put("phoneType", "gsm");
                result.put("mnc", Integer.parseInt(mncMCC.substring(3)));
                result.put("mcc", Integer.parseInt(mncMCC.substring(0, 3)));

                GsmCellLocation gsmCellLocation = (GsmCellLocation) tm.getCellLocation();
                if (gsmCellLocation != null) {
                    result.put("cid", gsmCellLocation.getCid());
                    result.put("lac", gsmCellLocation.getLac());
                    result.put("psc", gsmCellLocation.getPsc());
                }
            } catch (Exception e) {
            }

        }


        return result;
    }

    private JSONObject refreshV1(TelephonyManager tm) {
        int phoneType = tm.getPhoneType();
        JSONObject result = new JSONObject();

        switch(phoneType) {
            case TelephonyManager.PHONE_TYPE_NONE:
            case TelephonyManager.PHONE_TYPE_SIP:
            case TelephonyManager.PHONE_TYPE_GSM:
                result = buildGSMResult(tm);
                break;
        }


        return result;
    }

    private JSONObject refreshV18(TelephonyManager tm) {
        JSONObject result = new JSONObject();

        try {
            result.put("phoneType", "gsm");
            List<CellInfo> cellInfoList = tm.getAllCellInfo();
            if (cellInfoList != null) {
                for (final CellInfo info : cellInfoList) {
                    if (info instanceof CellInfoGsm) {
                        final CellSignalStrengthGsm signalStrength = ((CellInfoGsm) info).getCellSignalStrength();
                        final CellIdentityGsm identityGsm = ((CellInfoGsm) info).getCellIdentity();

                        result.put("debug", "gsm");
                        result.put("signalStrengthDBM", signalStrength.getDbm());
                        result.put("mnc", identityGsm.getMnc());
                        result.put("mcc", identityGsm.getMcc());
                        result.put("cid", identityGsm.getCid());
                        result.put("lac", identityGsm.getLac());
                        result.put("psc", identityGsm.getPsc());
                    } else if (info instanceof CellInfoCdma) {
                        final CellSignalStrengthCdma signalStrength = ((CellInfoCdma) info).getCellSignalStrength();
                        final CellIdentityCdma identityCdma = ((CellInfoCdma) info).getCellIdentity();

                        result.put("debug", "cdma");
                        result.put("signalStrengthDBM", signalStrength.getDbm());
//                        pDevice.mCell.setSID(identityCdma.getSystemId());
                        result.put("mnc", identityCdma.getSystemId());
//                        result.put("mcc", identityGsm.getMcc());
                        result.put("cid", identityCdma.getBasestationId());
                        result.put("lac", identityCdma.getNetworkId());
//                        result.put("psc", identityGsm.getPsc());
                    } else if (info instanceof CellInfoLte) {
                        final CellSignalStrengthLte signalStrength = ((CellInfoLte) info).getCellSignalStrength();
                        final CellIdentityLte identityLte = ((CellInfoLte) info).getCellIdentity();

                        result.put("debug", "lte");
                        result.put("signalStrengthDBM", signalStrength.getDbm());
                        result.put("mnc", identityLte.getMnc());
                        result.put("mcc", identityLte.getMcc());
                        result.put("cid", identityLte.getCi());
//                        result.put("lac", identityGsm.getLac());
//                        result.put("psc", identityGsm.getPsc());
//                        pDevice.mCell.setTimingAdvance(lte.getTimingAdvance());
                    } else if (info instanceof CellInfoWcdma) {
                        final CellSignalStrengthWcdma signalStrength = ((CellInfoWcdma) info).getCellSignalStrength();
                        final CellIdentityWcdma identityWcdma = ((CellInfoWcdma) info).getCellIdentity();

                        result.put("debug", "wcdma");
                        result.put("signalStrengthDBM", signalStrength.getDbm());
                        result.put("mnc", identityWcdma.getMnc());
                        result.put("mcc", identityWcdma.getMcc());
                        result.put("cid", identityWcdma.getCid());
                        result.put("lac", identityWcdma.getLac());
                        result.put("psc", identityWcdma.getPsc());
                    }
                }
            }
        }catch (Exception e) {
        }

        return result;
    }

}
