package uni.project.mylocalbooking.api;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

class APICall {
    private String jwt = null;
    private JsonObjectRequest request = null;
    private String type = null;
    private String requestBody = null;
    private String url = null;

    public APICall(String jwt, String type, String requestBody, String url){
        this.jwt = jwt;
        this.type = type;
        this.requestBody = requestBody;
        this.url = url;

        switch (this.type){
            case "GET":
                //TODO
                break;
            case "POST":
                post(this.requestBody, this.url);
                break;
            case "PATCH":
                patch(this.requestBody, this.url);
                break;
            case "DELETE":
                //TODO
                break;
            default:
                Log.e("APICall constructor error", "Missing type");
        }
    }

    private static JSONObject getJsonBody(String body){
        JSONObject jsonBody = null;
        try {
            jsonBody = new JSONObject(body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonBody;
    }

    private void post(String requestBody, String url){
        call(requestBody, url, Request.Method.POST);
    }

    private void patch(String requestBody, String url){
        call(requestBody, url, Request.Method.PATCH);
    }

    private void call(String body, String url, int requestMethod){
        JSONObject jsonBody = getJsonBody(body);
        Log.i("post method", body);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                requestMethod,
                url,
                jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("test post", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("post error", error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + jwt);
                headers.put("Content-Type", "application/json");
                headers.put("User-Agent", "Mozilla/5.0");
                headers.put("Accept", "*/*");
                headers.put("Accept-Encoding", "gzip, deflate, br");
                headers.put("Connection", "keep-alive");
                return headers;
            }
        };

        this.request = jsonRequest;
    }

    public JsonObjectRequest getRequest(){
        return this.request;
    }

    public String getType(){
        return this.type;
    }
}
