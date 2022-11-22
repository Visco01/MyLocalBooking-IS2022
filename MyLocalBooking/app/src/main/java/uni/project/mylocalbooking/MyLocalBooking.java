package uni.project.mylocalbooking;

import android.app.Application;
import android.content.Context;

public class MyLocalBooking extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        MyLocalBooking.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyLocalBooking.context;
    }
}
