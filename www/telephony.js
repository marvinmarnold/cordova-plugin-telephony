/*global cordova*/
var telephony = {

    refresh: function (success, failure) {
      cordova.exec(success, failure, "Telephony", "refresh", []);
    },

}


cordova.addConstructor(function() {
  if (!window.plugins) {
    window.plugins = {};
  }

  window.plugins.telephony = telephony;
  return window.plugins.telephony;
});
