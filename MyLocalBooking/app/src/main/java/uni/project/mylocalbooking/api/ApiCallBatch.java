package uni.project.mylocalbooking.api;

import com.android.volley.toolbox.JsonRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ApiCallBatch<T> {
    private final List<SyncApiCall<T>> batch = new ArrayList<>();
    private final List<RunOnResponse<T>> callbacks = new ArrayList<>();

    public void add(SyncApiCall<T> node, RunOnResponse<T> runOnResponse) {
        batch.add(node);
        callbacks.add(runOnResponse);
    }

    public synchronized void run() {
        for (SyncApiCall<T> node : batch)
            node.call();

        for(int i = 0; i < batch.size(); i++) {
            SyncApiCall<T> node = batch.get(i);
            RunOnResponse<T> callback = callbacks.get(i);
            callback.onResponse(node.waitResponse());
        }
    }
}
