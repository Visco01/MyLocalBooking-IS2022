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
                editor.putLong("id", user.getId());
            editor.putString("cellphone", user.cellphone);
            editor.putString("firstname", user.firstname);
            editor.putString("lastname", user.lastname);
            editor.putString("email", user.email);
            editor.putLong("dob", user.dob.toEpochDay());
            editor.putString("password", user.password);

            if(user instanceof Client){
                Client client = (Client) user;
                editor.putLong("subclass_id", client.getSubclassId());
                editor.putString("usertype", "client");
                putDouble(editor, "lat", client.position.latitude);
                putDouble(editor, "lng", client.position.longitude);
            }else if(user instanceof Provider){
                Provider provider = (Provider) user;
                editor.putLong("subclass_id", provider.getSubclassId());
                editor.putBoolean("verified", provider.verified);
                editor.putString("usertype", "provider");
                editor.putString("companyname", provider.companyName);
                editor.putInt("maxstrikes", provider.maxStrikes);
            }
            editor.commit();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private static SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }


    public static Double getDouble(String key, Double defaultValue) {
        if(defaultValue != null)
            return Double.longBitsToDouble(theInstance.getLong(key, Double.doubleToLongBits(defaultValue)));

        if(theInstance.contains(key))
            return Double.longBitsToDouble(theInstance.getLong(key, 0L));

        return null;
    }
    
    public static Map<String, ?> getUserPrefs(){
        return theInstance.getAll();
    }

    public static void deleteSessionPreferences(){
        getInstance().edit().clear().commit();
    }
}
