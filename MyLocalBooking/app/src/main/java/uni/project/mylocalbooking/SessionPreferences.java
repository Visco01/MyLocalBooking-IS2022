package uni.project.mylocalbooking;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionPreferences {
    private static Context context = MyLocalBooking.getAppContext();
    private static SharedPreferences theInstance = context.getSharedPreferences("user_details", Context.MODE_PRIVATE);

    private SessionPreferences(){}

    public SharedPreferences getInstance(){
        return theInstance;
    }

    public SharedPreferences.Editor getEditor(){
        return theInstance.edit();
    }
}
