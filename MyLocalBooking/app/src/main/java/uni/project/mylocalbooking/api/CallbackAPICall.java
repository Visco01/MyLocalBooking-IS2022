package uni.project.mylocalbooking.api;

import android.util.Log;

public class CallbackAPICall<T> extends APICall<T> {
    protected RunOnResponse<T> runOnResponse;

    public CallbackAPICall(String jwt, String method, String requestBody, String url, RunOnResponse<T> runOnResponse, boolean isArray) {
        super(jwt, method, requestBody, url, isArray);
        this.runOnResponse = runOnResponse;
    }

    @Override
    public void call() {
        super.call(runOnResponse, error -> Log.i("APICall error", error.toString()));
    }
}
