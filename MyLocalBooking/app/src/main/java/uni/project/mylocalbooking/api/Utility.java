package uni.project.mylocalbooking.api;

import org.json.JSONObject;

class Utility {

    public static String generateEncryptedPassword(String password){
        try {
            password = AESCrypt.encrypt(password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return password;
    }

    public static void callAPI(String jwt, String requestBody, String url, String method, RunOnResponse<JSONObject> runOnResponse){
        if(jwt != null){
            APICall call;
            if(runOnResponse != null)
                call = new APICall(jwt, method, requestBody, url, runOnResponse);
            else
                call = new APICall(jwt, method, requestBody, url);
            RequestQueueSingleton.getInstance().add(call.getRequest());
        }else{
            LoginAPI.addWaitingRequest(requestBody, url, method, runOnResponse);
        }
    }
 }
