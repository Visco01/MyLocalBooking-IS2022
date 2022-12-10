package uni.project.mylocalbooking.api;

import android.util.Log;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

class APICall<T, K extends JsonRequest<?>> implements IBatchNode<T,K> {
    private final String jwt;
    private K request;
    private final RunOnResponse<T> runOnResponse;
    private final String method;
    private final String requestBody;
    private final String url;
    private final Boolean isArray;
    private Boolean callLock = false;

    public APICall(String jwt, String method, String requestBody, String url, RunOnResponse<T> ror, boolean isArray){
        this.jwt = jwt;
        this.method = method;
        this.requestBody = requestBody;
        this.url = url;

        this.runOnResponse = (response) -> {
            if (ror != null)
                ror.apply(response);

            synchronized (this) {
                callLock = true;
                notify();
            }
        };

        this.isArray = isArray;

        if (isArray)
            generateRequest();
        else
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
        generateRequest(Request.Method.GET);
    }

    private void post(){
        generateRequest(Request.Method.POST);
    }

    private void patch(){
        generateRequest(Request.Method.PATCH);
    }

    private void delete(){
        generateRequest(Request.Method.DELETE);
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

    private K generateRequest(){
        return (K) new JsonArrayRequest(
                Request.Method.GET,
                APICall.this.url,
                null,
                (JSONArray response) -> {
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
