# Usage

telephony.refresh(function(result) {
  result.mnc; // int
  result.mcc; // int
  result.signalStrengthDBM; // float
  result.lac; // int
  result.psc; // int
  result.cid; // int
  result.phoneType; // 'gsm' (only, for now)
})

# Example
https://github.com/marvinmarnold/stingwatch/blob/master/cordova/gsm.js
