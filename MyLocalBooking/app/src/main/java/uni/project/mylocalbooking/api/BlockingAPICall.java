package uni.project.mylocalbooking.api;

import com.android.volley.toolbox.RequestFuture;

import java.util.concurrent.ExecutionException;

public class BlockingAPICall<T> extends APICall<T> implements IAPICall<T> {
    private final RequestFuture<T> future = RequestFuture.newFuture();
    public BlockingAPICall(String jwt, String method, String requestBody, String url, boolean isArray) {
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
