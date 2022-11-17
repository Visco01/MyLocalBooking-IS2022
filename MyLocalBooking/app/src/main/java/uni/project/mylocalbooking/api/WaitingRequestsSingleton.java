package uni.project.mylocalbooking.api;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collection;

import kotlin.Triple;

public class WaitingRequestsSingleton {
    private static Collection<WaitingRequest> theInstance = null;

    private WaitingRequestsSingleton(){

    }

    public static Collection<WaitingRequest> getInstance(){
        if(theInstance == null){
            theInstance = new ArrayList<>();
        }
        return theInstance;
    }
}
