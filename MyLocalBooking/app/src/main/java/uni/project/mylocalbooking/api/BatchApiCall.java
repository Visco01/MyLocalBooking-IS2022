package uni.project.mylocalbooking.api;

import com.android.volley.toolbox.JsonRequest;

import java.util.ArrayList;
import java.util.List;

public class BatchApiCall<T,K extends JsonRequest<?>> implements IBatchNode<T,K> {
    private final List<IBatchNode<T,K>> batch = new ArrayList<>();

    public void add(IBatchNode<T,K> node) {
        batch.add(node);
    }

    @Override
    public void run() throws InterruptedException {
        for(int i = 0; i < batch.size(); i++) {
            IBatchNode<T,K> call = batch.get(i);
            call.run();
        }

        for(IBatchNode<T,K> node : batch)
            node.wait();

        this.notify();
    }
}
