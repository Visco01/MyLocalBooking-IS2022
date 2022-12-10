package uni.project.mylocalbooking.api;

import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

class APICall<T, K extends JsonRequest<?>> implements IBatchNode<T,K> {
    private final String jwt;
    private K request;
    private final Response.Listener<T> runOnResponse;
    private final String method;
    private final String requestBody;
    private final String url;
    private final Boolean isArray;
    private Boolean callLock = false;
    private RequestFuture<T> future;

    public APICall(String jwt, String method, String requestBody, String url, Response.Listener<T> ror, boolean isArray){
        this.jwt = jwt;
        this.method = method;
        this.requestBody = requestBody;
        this.url = url;

        final Object thisInstance = this;
        this.runOnResponse = (response) -> {
            if (ror != null)
                ror.onResponse(response);

            synchronized (thisInstance) {
                callLock = true;
                notify();
            }
        };

        this.isArray = isArray;

        if (isArray)
            this.request = generateRequest();
        else
            this.request = init();
    }
    public APICall(String jwt, String method, String requestBody, String url, boolean isArray) {
        this(jwt, method, requestBody, url,(RequestFuture<T>) RequestFuture.newFuture(), isArray);
    }

    private K init(){
        switch (this.method){
            case "GET":
                return generateRequest(Request.Method.GET);
            case "POST":
                return generateRequest(Request.Method.POST);
            case "PATCH":
                return generateRequest(Request.Method.PATCH);
            case "DELETE":
                return generateRequest(Request.Method.DELETE);
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

    private K generateRequest(int requestMethod){
        JSONObject jsonBody = null;
        if(requestMethod != Request.Method.GET){
            jsonBody = getJsonBody(this.requestBody);
        }

        return (K) new JsonObjectRequest(
                requestMethod,
                APICall.this.url,
                jsonBody,
                response -> {
                    if(runOnResponse != null) runOnResponse.onResponse((T) response);
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

    private K generateRequest(){
        return (K) new JsonArrayRequest(
                Request.Method.GET,
                APICall.this.url,
                null,
                (JSONArray response) -> {
                    if(runOnResponse != null) runOnResponse.onResponse((T) response);
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

    public K getRequest(){
        return this.request;
    }

    public void call(){
        if(jwt != null){
            if(isArray){
                RequestQueueSingleton.getInstance().add((JsonArrayRequest) getRequest());
            }
            else{
                RequestQueueSingleton.getInstance().add((JsonObjectRequest) getRequest());
            }
        }else{
            LoginAPI.addWaitingRequest(requestBody, url, method, runOnResponse, isArray);
        }
    }

    @Override
    public void run() {
        call();
    }

    @Override
    public synchronized void waitNode() throws InterruptedException {
        while(!callLock)
            wait();
    }
}
