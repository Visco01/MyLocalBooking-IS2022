package uni.project.mylocalbooking;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

import uni.project.mylocalbooking.models.AppUser;
import uni.project.mylocalbooking.models.Client;
import uni.project.mylocalbooking.models.Provider;

public class SessionPreferences {
    private static Context context = MyLocalBooking.getAppContext();
    private static String PREFS_KEY = "user_details";
    private static SharedPreferences theInstance = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);

    private SessionPreferences(){}

    private static SharedPreferences getInstance(){
        return theInstance;
    }

    private static SharedPreferences.Editor getEditor(){
        return theInstance.edit();
    }

    public static void setUserPrefs(AppUser user){
        try {
            SharedPreferences.Editor editor = getEditor();
            if(user.getId() != null)
                editor.putInt("id", Math.toIntExact(user.getId()));
            editor.putString("cellphone", user.cellphone);
            editor.putString("username", user.firstname);
            editor.putString("lastname", user.lastname);

            if(user instanceof Client){
                Client client = (Client) user;
                editor.putString("usertype", "client");
                editor.putFloat("lat", client.position.latitude);
                editor.putFloat("lng", client.position.longitude);
            }else if(user instanceof Provider){
                Provider provider = (Provider) user;
                editor.putString("usertype", "provider");
                editor.putString("companyname", provider.companyName);
                editor.putInt("maxstrikes", provider.maxStrikes);
            }
            editor.commit();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public static Map<String, ?> getUserPrefs(){
        return theInstance.getAll();
    }
}
