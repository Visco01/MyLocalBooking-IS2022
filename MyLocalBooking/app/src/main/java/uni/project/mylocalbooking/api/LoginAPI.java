package uni.project.mylocalbooking.api;

import android.util.Log;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;

class LoginAPI {
    private static final String username = "admin";
    private static final String password = "$2a$12$3K1dWlkO3ZcKZfjUgvPbGeG83i6KxcITC7ap.D3/wq5/GHOhMuRZe";
    private static final String url = "https://mylocalbooking-api-o1he.onrender.com/api/auth";
    private static final String requestBody = "{ \"auth\": { \"username\": \"" + LoginAPI.username + "\", \"password\": \"" + LoginAPI.password + "\" } }";
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
        for(WaitingRequest elem : WaitingRequestsSingleton.getInstance()){
            APICall call = new APICall(jwt, elem.getMethod(), elem.getRequestBody(), elem.getUrl(), elem.getRunOnResponse());
            RequestQueueSingleton.getInstance().add(call.getRequest());
        }
    }
    
    public static <T> void addWaitingRequest(String requestBody, String url, String method, RunOnResponse<T> runOnResponse){
        WaitingRequestsSingleton.getInstance().add(new WaitingRequest<T>(url, requestBody, method, runOnResponse));
    }
}
