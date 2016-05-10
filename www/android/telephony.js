module.exports = {
  hasReadPermission: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'Telephony', 'hasReadPermission', []);
  },
  requestReadPermission: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'Telephony', 'requestReadPermission', []);
  }
};
