package uni.project.mylocalbooking.api;

import com.android.volley.toolbox.JsonRequest;

public interface IAPICall<T> {
    void setJwt(String jwt);
    JsonRequest<T> getRequest();
}
