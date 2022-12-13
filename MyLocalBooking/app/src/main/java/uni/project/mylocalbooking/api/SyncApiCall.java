package uni.project.mylocalbooking.api;

import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.RequestFuture;

import java.util.concurrent.ExecutionException;

public class SyncApiCall <T> extends APICall<T> implements IApiCall<T> {
    private final RequestFuture<T> future = RequestFuture.newFuture();
    public SyncApiCall(String jwt, String method, String requestBody, String url, boolean isArray) {
        super(jwt, method, requestBody, url, isArray);
    }

    public T waitResponse() {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void call() {
        super.call(future, future);
    }
}
