package uni.project.mylocalbooking.api;

import com.android.volley.toolbox.JsonRequest;

import java.util.ArrayList;
import java.util.List;

public class BatchApiCall<T,K extends JsonRequest<?>> implements IBatchNode<T,K> {
    private final List<IBatchNode<T,K>> batch = new ArrayList<>();
    private Boolean lock = false;

    public void add(IBatchNode<T,K> node) {
        batch.add(node);
    }

    @Override
    public synchronized void run() throws InterruptedException {
        for (IBatchNode<T, K> node : batch)
            node.run();

        for (IBatchNode<T, K> node : batch)
            node.waitNode();

        synchronized (this) {
            lock = true;
            notify();
        }
    }

    @Override
    public synchronized void waitNode() throws InterruptedException {
        while(!lock)
            wait();
    }
}
