
package org.stingraymappingproject.cordova;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

public class Telephony extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

        if (action.equals("getCid")) {

            String message = "452";
            callbackContext.success(message);

            return true;

        } else {

            return false;

        }
    }
}
