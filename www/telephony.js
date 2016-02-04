/*global cordova*/
module.exports = {

    getCid: function (success, failure) {
      cordova.exec(success, failure, "Telephony", "getCid";
    },

}
