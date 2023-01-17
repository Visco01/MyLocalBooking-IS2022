package uni.project.mylocalbooking.api;

import java.util.ArrayList;
import java.util.Collection;

public class WaitingRequestsSingleton {
    private static Collection<IAPICall> theInstance = null;

    private WaitingRequestsSingleton(){

    }

    public static Collection<IAPICall> getInstance(){
        if(theInstance == null){
            theInstance = new ArrayList<>();
        }
        return theInstance;
    }
}
