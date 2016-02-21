/*global cordova*/
module.exports = {

    refresh: function (success, failure) {
      cordova.exec(success, failure, "Telephony", "refresh", []);
    },

}
