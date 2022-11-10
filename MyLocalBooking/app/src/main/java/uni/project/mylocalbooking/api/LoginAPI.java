package uni.project.mylocalbooking.api;

import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginAPI {
    private static final String username = "admin";
    private static final String password = "$2a$12$3K1dWlkO3ZcKZfjUgvPbGeG83i6KxcITC7ap.D3/wq5/GHOhMuRZe";
    private static final String url = "https://mylocalbooking-api-o1he.onrender.com/api/auth";
    private static String requestBody = "{ \"auth\": { \"username\": \"" + LoginAPI.username + "\", \"password\": \"" + LoginAPI.password + "\" } }";
    private static String jwt = null;

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
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.POST,
                LoginAPI.url,
                jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            LoginAPI.jwt = response.getString("jwt");
                            Log.i("auth request", LoginAPI.jwt);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("auth request", error.toString());
                    }
                }
        );
        return jsonRequest;
    }
}
