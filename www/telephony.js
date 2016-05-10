module.exports = {
  getTelephonyInfo: function (successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "Telephony", "getTelephonyInfo", []);
  }
};
