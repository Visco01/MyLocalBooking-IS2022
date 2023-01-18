package uni.project.mylocalbooking.api;

import android.util.Log;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginAPI {
    private static final String url = "https://mylocalbooking-api-o1he.onrender.com/api/auth";
    private static String requestBody;
    private static final String jwt = null;

    public static JsonObjectRequest getLoginRequest(){
        return makeLoginRequest();
    }

    private static JSONObject getJsonBody(){
        JSONObject jsonBody = null;
        try {
            jsonBody = new JSONObject(LoginAPI.requestBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonBody;
    }

    private static JsonObjectRequest makeLoginRequest() {
        JSONObject jsonBody = getJsonBody();
        return new JsonObjectRequest(
                Request.Method.POST,
                LoginAPI.url,
                jsonBody,
                response -> {
                    try {
                        MyLocalBookingAPI.setJWT(response.getString("jwt"));
                        freeRequests(response.getString("jwt"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.i("auth request", error.toString())
        );
    }

    private static void freeRequests(String jwt){
        for(IAPICall call : WaitingRequestsSingleton.getInstance()){
            call.setJwt(jwt);
            RequestQueueSingleton.getInstance().add(call.getRequest());
        }
    }
    
    public static <T> void addWaitingRequest(IAPICall<T> call){
        WaitingRequestsSingleton.getInstance().add(call);
    }

    public static void setCredentials(String username, String password){
        requestBody = "{ \"auth\": { \"username\": \"" + username + "\", \"password\": \"" + password + "\" } }";
    }
}
