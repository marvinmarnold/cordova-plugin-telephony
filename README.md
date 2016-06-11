# Usage

````
telephony.refresh(result => {
  result.mnc; // int
  result.mcc; // int
  result.signalStrengthDBM; // float
  result.lac; // int
  result.psc; // int
  result.cid; // int
  result.phoneType; // 'gsm' (only, for now)
})
````

# Example
https://github.com/marvinmarnold/stingwatch/blob/ac2d1c837b5c7ca235c4cb8d0d938ad860a3f164/imports/startup/cordova/telephony.js
