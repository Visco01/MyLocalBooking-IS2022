package uni.project.mylocalbooking.api;

import com.android.volley.toolbox.JsonRequest;

public interface IApiCall<K extends JsonRequest<?>> {
    void setJwt(String jwt);
    String getJwt();
    K getRequest();
}
