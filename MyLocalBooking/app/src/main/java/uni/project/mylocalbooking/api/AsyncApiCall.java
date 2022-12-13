package uni.project.mylocalbooking.api;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonRequest;

public class AsyncApiCall<T> extends APICall<T> {
    protected RunOnResponse<T> runOnResponse;
    private Boolean callLock = false;

    public AsyncApiCall(String jwt, String method, String requestBody, String url, RunOnResponse<T> runOnResponse, boolean isArray) {
        super(jwt, method, requestBody, url, isArray);
        this.runOnResponse = runOnResponse;
    }

    public void run() {
        call();
    }

    public synchronized void waitNode() throws InterruptedException {
        while(!callLock)
            wait();
    }

    @Override
    public void call() {
        final Object thisInstance = this;
        runOnResponse = (response) -> {
            if (runOnResponse != null)
                runOnResponse.onResponse(response);

            synchronized (thisInstance) {
                callLock = true;
                notify();
            }
        };

        super.call(runOnResponse, error -> Log.i("APICall error", error.toString()));
    }
}
