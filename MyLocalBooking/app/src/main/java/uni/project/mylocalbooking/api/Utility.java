package uni.project.mylocalbooking.api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;

import uni.project.mylocalbooking.models.Establishment;

class Utility {

    public static String generateEncryptedPassword(String password){
        try {
            password = AESCrypt.encrypt(password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return password;
    }

    public static <T> void callAPI(String jwt, String requestBody, String url, String method, RunOnResponse<T> runOnResponse){
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

    public static Collection<Establishment> getOwnedEstablishmentData(JSONArray response) {
        return null;
    }
}
