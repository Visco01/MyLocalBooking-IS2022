package uni.project.mylocalbooking.api;

import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

class APICall<T> {
    private final String jwt;
    private boolean isArrayRequest = false;
    private JsonObjectRequest objectRequest = null;
    private JsonArrayRequest arrayRequest = null;
    private final String method;
    private final String requestBody;
    private final String url;
    private RunOnResponse<T> runOnResponse = null;

    public APICall(String jwt, String method, String requestBody, String url, RunOnResponse<T> runOnResponse){
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
                get();
                break;
            case "POST":
                post();
                break;
            case "PATCH":
                patch();
                break;
            case "DELETE":
                delete();
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

    private void get(){
        call(Request.Method.GET);
    }

    private void post(){
        call(Request.Method.POST);
    }

    private void patch(){
        call(Request.Method.PATCH);
    }

    private void delete(){
        call(Request.Method.DELETE);
    }

    private void call(int requestMethod){
        JSONObject jsonBody = null;
        if(requestMethod != Request.Method.GET){
            jsonBody = getJsonBody(this.requestBody);
        }

        this.objectRequest = new JsonObjectRequest(
                requestMethod,
                APICall.this.url,
                jsonBody,
                response -> {
                    if(runOnResponse != null) runOnResponse.apply((T) response);
                },
                error -> Log.i("APICall error", error.toString())
        ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + jwt);
                if(requestMethod != Method.GET){
                    headers.put("Content-Type", "application/json");
                    headers.put("User-Agent", "Mozilla/5.0");
                    headers.put("Accept", "*/*");
                    headers.put("Accept-Encoding", "gzip, deflate, br");
                    headers.put("Connection", "keep-alive");
                }
                return headers;
            }
        };
    }

    private void call(){
        this.arrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                APICall.this.url,
                null,
                response -> {
                    if(runOnResponse != null) runOnResponse.apply((T) response);
                },
                error -> Log.i("APICall error", error.toString())
        ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + jwt);
                return headers;
            }
        };
    }

    public JsonObjectRequest getRequest(){
        return this.objectRequest;
    }
}
