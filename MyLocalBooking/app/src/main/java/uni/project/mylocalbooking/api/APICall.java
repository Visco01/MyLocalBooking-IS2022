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
    private String method = null;
    private String requestBody = null;
    private String url = null;
    private RunOnResponse<JSONObject> runOnResponse = null;

    public APICall(String jwt, String method, String requestBody, String url, RunOnResponse<JSONObject> runOnResponse){
        this.jwt = jwt;
        this.method = method;
        this.requestBody = requestBody;
        this.url = url;
        this.runOnResponse = runOnResponse;
        init();
    }

    public APICall(String jwt, String method, String requestBody, String url){
        this.jwt = jwt;
        this.method = method;
        this.requestBody = requestBody;
        this.url = url;
        init();
    }

    private void init(){
        switch (this.method){
            case "GET":
                //TODO
                break;
            case "POST":
                post();
                break;
            case "PATCH":
                patch();
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

    private void post(){
        call(Request.Method.POST);
    }

    private void patch(){
        call(Request.Method.PATCH);
    }

    private void call(int requestMethod){
        JSONObject jsonBody = getJsonBody(this.requestBody);
        Log.i("post method", this.requestBody);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                requestMethod,
                this.url,
                jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(runOnResponse != null) runOnResponse.apply(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("APICall error", error.toString());
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

    public String getMethod(){
        return this.method;
    }
}
