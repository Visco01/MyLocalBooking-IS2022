package uni.project.mylocalbooking.api;

import org.json.JSONObject;

public class WaitingRequest {
    private final String url;
    private final String requestBody;
    private final String method;
    private final RunOnResponse<JSONObject> runOnResponse;

    public WaitingRequest(String url, String requestBody, String method, RunOnResponse<JSONObject> runOnResponse) {
        this.url = url;
        this.requestBody = requestBody;
        this.method = method;
        this.runOnResponse = runOnResponse;
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

    public RunOnResponse<JSONObject> getRunOnResponse(){
        return this.runOnResponse;
    }
}
