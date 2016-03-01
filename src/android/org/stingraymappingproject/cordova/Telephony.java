
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
import android.telephony.NeighboringCellInfo;

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
            JSONObject result = new JSONObject();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
              result = refreshV18(tm, result);
            } else {
              result = refreshV1(tm, result);
            }

            callbackContext.success(result);

            return true;

        } else {

            return false;

        }
    }

    private JSONObject buildGSMResult(TelephonyManager tm, JSONObject result) {
        String mncMCC = tm.getNetworkOperator();

        if (mncMCC != null && mncMCC.length() >= 3 ) {
            try {
                result.put("phoneType", "android-v1-gsm");
                result.put("mnc", Integer.parseInt(mncMCC.substring(3)));
                result.put("mcc", Integer.parseInt(mncMCC.substring(0, 3)));

                GsmCellLocation gsmCellLocation = (GsmCellLocation) tm.getCellLocation();
                if (gsmCellLocation != null) {
                    result.put("cid", gsmCellLocation.getCid());
                    result.put("lac", gsmCellLocation.getLac());
                    result.put("psc", gsmCellLocation.getPsc());
                }

                JSONArray ncNeighbors = new JSONArray();
                List<NeighboringCellInfo> ncList = tm.getNeighboringCellInfo();

                for(NeighboringCellInfo ncInfo : ncList) {
                    JSONObject ncObj = new JSONObject();

                    ncObj.put("cid", ncInfo.getCid());
                    ncObj.put("lac", ncInfo.getLac());
                    ncObj.put("networkType", ncInfo.getNetworkType());
                    ncObj.put("psc", ncInfo.getPsc());
                    ncObj.put("signalStrength", ncInfo.getRssi());

                    ncNeighbors.put(ncObj);
                }
                result.put("neighbors", ncNeighbors);
            } catch (Exception e) {
            }

        }


        return result;
    }

    private JSONObject refreshV1(TelephonyManager tm, JSONObject result) {
//        int phoneType = tm.getPhoneType();

//        switch(phoneType) {
//            case TelephonyManager.PHONE_TYPE_NONE:
//            case TelephonyManager.PHONE_TYPE_SIP:
//            case TelephonyManager.PHONE_TYPE_GSM:
                result = buildGSMResult(tm, result);
//                break;
//        }


        return result;
    }

    private JSONObject refreshV18(TelephonyManager tm, JSONObject result) {
        try {
            List<CellInfo> cellInfoList = tm.getAllCellInfo();
            if (cellInfoList != null) {
                for (final CellInfo info : cellInfoList) {
                    int tSignalStrength = -1;
                    int tMnc = -1;
                    int tMcc = -1;
                    int tCid = -1;
                    int tLac = -1;
                    int tPsc = -1;
                    String tPhoneType = null;

                    if (info instanceof CellInfoGsm) {
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
                        result.put("phoneType", tPhoneType);
                    }
                    if(tSignalStrength != -1) {
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


                // Fallback to lower API if values appear incorrect
                if(result.getInt("mnc") == Integer.MAX_VALUE) {
                    result = refreshV1(tm, result);
                }
            }
        }catch (Exception e) {
        }

        return result;
    }

}
