package uni.project.mylocalbooking.api;

import com.android.volley.toolbox.JsonRequest;

public interface IBatchNode <T,K extends JsonRequest<?>> {
    void run() throws InterruptedException;
}
