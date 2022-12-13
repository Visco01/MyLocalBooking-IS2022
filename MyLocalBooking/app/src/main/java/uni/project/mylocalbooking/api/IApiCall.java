package uni.project.mylocalbooking.api;

import com.android.volley.toolbox.JsonRequest;

public interface IApiCall<T> {
    void setJwt(String jwt);
    JsonRequest<T> getRequest();
}
