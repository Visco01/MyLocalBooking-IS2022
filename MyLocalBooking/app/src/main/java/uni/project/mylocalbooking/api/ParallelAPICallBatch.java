package uni.project.mylocalbooking.api;

import java.util.ArrayList;
import java.util.List;

public class ParallelAPICallBatch<T> {
    private final List<BlockingAPICall<T>> batch = new ArrayList<>();
    private final List<RunOnResponse<T>> callbacks = new ArrayList<>();

    public void add(BlockingAPICall<T> node, RunOnResponse<T> runOnResponse) {
        batch.add(node);
        callbacks.add(runOnResponse);
    }

    public synchronized void run() {
        for (BlockingAPICall<T> node : batch)
            node.call();

        for(int i = 0; i < batch.size(); i++) {
            BlockingAPICall<T> node = batch.get(i);
            RunOnResponse<T> callback = callbacks.get(i);
            callback.onResponse(node.waitResponse());
        }
    }
}
