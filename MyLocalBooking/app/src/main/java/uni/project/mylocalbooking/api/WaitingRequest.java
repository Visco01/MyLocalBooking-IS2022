package uni.project.mylocalbooking.api;

import com.android.volley.Response;

import org.json.JSONObject;

public class WaitingRequest<T> {
    private final String url;
    private final String requestBody;
    private final String method;
    private final Response.Listener<T> runOnResponse;
    private final boolean isArray;

    public WaitingRequest(String url, String requestBody, String method, Response.Listener<T> runOnResponse, boolean isArray) {
        this.url = url;
        this.requestBody = requestBody;
        this.method = method;
        this.runOnResponse = runOnResponse;
        this.isArray = isArray;
    }

    public String getUrl(){
        return this.url;
    }

    public String getRequestBody(){
        return this.requestBody;
    }

    public String getMethod(){
        return this.method;
    }

    public Response.Listener<T> getRunOnResponse(){
        return this.runOnResponse;
    }

    public boolean isArray(){ return this.isArray; }
}
