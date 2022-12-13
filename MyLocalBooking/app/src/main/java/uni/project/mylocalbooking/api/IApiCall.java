package uni.project.mylocalbooking.api;

import com.android.volley.toolbox.JsonRequest;

public interface IApiCall<T> {
    void setJwt(String jwt);
    String getJwt();
    JsonRequest<T> getRequest();
}
