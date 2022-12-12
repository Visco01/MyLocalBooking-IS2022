package uni.project.mylocalbooking.api;

import com.android.volley.toolbox.JsonRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ApiCallBatch<T,K extends JsonRequest<T>> {
    private final List<SyncApiCall<T,K>> batch = new ArrayList<>();

    public void add(SyncApiCall<T,K> node) {
        batch.add(node);
    }

    public synchronized void run(RunOnResponse<T> runOnResponse) throws InterruptedException {
        for (SyncApiCall<T,K> node : batch)
            node.call();

        for (SyncApiCall<T,K> node : batch) {
            try {
                T response = node.waitResponse();
                runOnResponse.onResponse(response);
            } catch (ExecutionException e) {
                e.printStackTrace(); // TODO: how to handle this?
            }
        }
    }
}
