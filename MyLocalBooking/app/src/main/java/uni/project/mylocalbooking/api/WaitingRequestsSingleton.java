package uni.project.mylocalbooking.api;

import java.util.ArrayList;
import java.util.Collection;

public class WaitingRequestsSingleton {
    private static Collection<IApiCall> theInstance = null;

    private WaitingRequestsSingleton(){

    }

    public static Collection<IApiCall> getInstance(){
        if(theInstance == null){
            theInstance = new ArrayList<>();
        }
        return theInstance;
    }
}
