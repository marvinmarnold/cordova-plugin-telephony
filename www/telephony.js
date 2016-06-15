module.exports = {
  getTelephonyInfo: function (successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "Telephony", "getTelephonyInfo", []);
  },
  listenTelephonyInfo: function (successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "Telephony", "listenTelephonyInfo", []);
  }
};
