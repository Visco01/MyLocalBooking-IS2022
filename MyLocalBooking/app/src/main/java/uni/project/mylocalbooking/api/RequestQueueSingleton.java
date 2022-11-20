package uni.project.mylocalbooking.api;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import uni.project.mylocalbooking.MyLocalBooking;

class RequestQueueSingleton {
    private static RequestQueue theInstance = null;

    private RequestQueueSingleton(){

    }

    public static RequestQueue getInstance(){
        if(theInstance == null){
            theInstance = Volley.newRequestQueue(MyLocalBooking.getAppContext());
        }
        return theInstance;
    }
}
