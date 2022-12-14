package uni.project.mylocalbooking;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.lifecycle.LiveData;

import com.google.maps.GaeRequestHandler;
import com.google.maps.GeoApiContext;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import uni.project.mylocalbooking.models.AppUser;
import uni.project.mylocalbooking.models.Client;
import uni.project.mylocalbooking.models.Coordinates;
import uni.project.mylocalbooking.models.Establishment;
import uni.project.mylocalbooking.models.Provider;

public class MyLocalBooking extends Application {
    private static Context context;
    private static AppUser currentUser;
    public static HashMap<Long, Establishment> establishments;
    public static GeoApiContext geoApiContext;

    public void onCreate() {
        super.onCreate();
        MyLocalBooking.context = getApplicationContext();
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(),
                    PackageManager.GET_META_DATA);
            MyLocalBooking.geoApiContext = new GeoApiContext.Builder()
                    .apiKey(ai.metaData.getString("com.google.android.geo.API_KEY"))
                    .build();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Context getAppContext() {
        return MyLocalBooking.context;
    }

    private static AppUser buildCurrentUser() {
        Map<String, ?> prefs = SessionPreferences.getUserPrefs();
        Long id = (Long) prefs.get("id");
        String cellphone = (String) prefs.get("cellphone");
        String firstname = (String) prefs.get("firstname");
        String lastname = (String) prefs.get("lastname");
        String email = (String) prefs.get("email");
        String password = (String) prefs.get("password");
        Long subclassId = (Long) prefs.get("subclass_id");
        LocalDate dob = LocalDate.ofEpochDay((Long) prefs.get("dob"));

        if(((String) prefs.get("usertype")).equals("client")) {
            Float lat = (Float) prefs.get("lat");
            Float lng = (Float) prefs.get("lng");

            Coordinates coordinates = lat == null || lng == null ? null : new Coordinates(lat, lng);
            currentUser = new Client(subclassId, coordinates, id, cellphone, email, firstname, lastname, dob, password);
        }
        else {
            Boolean verified = (Boolean) prefs.get("verified");
            String company = (String) prefs.get("companyname");
            Integer maxStrikes = (Integer) prefs.get("maxstrikes");

            currentUser = new Provider(subclassId, verified, company, maxStrikes, null, id, cellphone, email, firstname, lastname, dob, password);
        }

        return currentUser;
    }

    public static AppUser getCurrentUser() {
        return currentUser == null ? buildCurrentUser() : currentUser;
    }
}
