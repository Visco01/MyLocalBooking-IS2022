package uni.project.mylocalbooking.api;

import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

abstract class APICall<T, K extends JsonRequest<T>> implements IApiCall<K> {
    public String jwt;
    protected K request;
    protected final String method;
    protected final String requestBody;
    protected final String url;
    protected final Boolean isArray;

    public APICall(String jwt, String method, String requestBody, String url,  boolean isArray){
        this.jwt = jwt;
        this.method = method;
        this.requestBody = requestBody;
        this.url = url;
        this.isArray = isArray;
    }

    @Override
    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    @Override
    public String getJwt() {
        return jwt;
    }

    private K init(Response.Listener<T> successListener, Response.ErrorListener errorListener){
        switch (this.method){
            case "GET":
                return generateRequest(successListener, errorListener, Request.Method.GET);
            case "POST":
                return generateRequest(successListener, errorListener, Request.Method.POST);
            case "PATCH":
                return generateRequest(successListener, errorListener, Request.Method.PATCH);
            case "DELETE":
                return generateRequest(successListener, errorListener, Request.Method.DELETE);
            default:
                Log.e("APICall constructor error", "Missing type");
        }
        return null;
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

    private K generateRequest(Response.Listener<T> successListener, Response.ErrorListener errorListener, int requestMethod){
        JSONObject jsonBody = null;
        if(requestMethod != Request.Method.GET){
            jsonBody = getJsonBody(this.requestBody);
        }

        return (K) new JsonObjectRequest(
                requestMethod,
                APICall.this.url,
                jsonBody,
                (Response.Listener<JSONObject>) successListener,
                (Response.ErrorListener) errorListener
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

    private K generateRequest(Response.Listener<T> successListener, Response.ErrorListener errorListener){
        return (K) new JsonArrayRequest(
                Request.Method.GET,
                APICall.this.url,
                null,
                (Response.Listener<JSONArray>) successListener,
                (Response.ErrorListener) errorListener
        ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + jwt);
                return headers;
            }
        };
    }

    public K getRequest(){
        return this.request;
    }

    public abstract void call();

    protected void call(Response.Listener<T> successListener, Response.ErrorListener errorListener){
        this.request = isArray ? generateRequest(successListener, errorListener) : init(successListener, errorListener);
        if(jwt != null){
            if(isArray){
                RequestQueueSingleton.getInstance().add((JsonArrayRequest) getRequest());
            }
            else{
                RequestQueueSingleton.getInstance().add((JsonObjectRequest) getRequest());
            }
        }else{
            LoginAPI.addWaitingRequest((IApiCall<JsonRequest<T>>) this);
        }
    }
}
