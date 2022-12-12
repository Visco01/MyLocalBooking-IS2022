package uni.project.mylocalbooking.api;

import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.RequestFuture;

import java.util.concurrent.ExecutionException;

public class SyncApiCall <T,K extends JsonRequest<T>> extends APICall<T,K> implements IApiCall<K> {
    private final RequestFuture<T> future = RequestFuture.newFuture();
    public SyncApiCall(String jwt, String method, String requestBody, String url, boolean isArray) {
        super(jwt, method, requestBody, url, isArray);
    }

    public T waitResponse() throws ExecutionException, InterruptedException {
        return future.get();
    }

    @Override
    public void call() {
        super.call(future, future);
    }
}
