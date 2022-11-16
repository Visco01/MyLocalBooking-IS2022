package uni.project.mylocalbooking.api;

import android.util.Log;
import java.util.Collection;

import uni.project.mylocalbooking.SessionPreferences;
import uni.project.mylocalbooking.models.AppUser;
import uni.project.mylocalbooking.models.Client;
import uni.project.mylocalbooking.models.Coordinates;
import uni.project.mylocalbooking.models.Establishment;
import uni.project.mylocalbooking.models.Provider;
import uni.project.mylocalbooking.models.Slot;
import uni.project.mylocalbooking.models.SlotBlueprint;

class MyLocalBookingAPI implements IMyLocalBookingAPI {
    private static String jwt = null;


    public MyLocalBookingAPI() {
        loginAPI();
    }

    private void loginAPI(){
        RequestQueueSingleton.getInstance().add(LoginAPI.getLoginRequest());
    }

    public static void setJWT(String jwt){
        MyLocalBookingAPI.jwt = jwt;
        Log.i("auth request", MyLocalBookingAPI.jwt);
    }

    @Override
    public void register(AppUser user, String password) {
        String url = "https://mylocalbooking-api-o1he.onrender.com/api/app_users";

        try {
            password = AESCrypt.encrypt(password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String requestBody = "{" +
                "\"cellphone\": \"" + user.cellphone + "\", " +
                "\"firstname\": \"" + user.firstname + "\", " +
                "\"email\": \"" + user.email + "\", " +
                "\"lastname\": \"" + user.lastname + "\", " +
                "\"dob\": \"" + "2001-04-20" + "\", " +
                "\"password_digest\": \"" + password + "\", ";

        if(user instanceof Client){
            Client client = (Client) user;
            requestBody += "\"Client\": {" +
                    "\"lat\": " + client.position.latitude + ", " +
                    "\"lng\": " + client.position.longitude +
                    "}}";
        } else {
            Provider provider = (Provider) user;
            requestBody += "\"Provider\": {" +
                    "\"isverified\": " + (provider.verified ? 1 : 0) + ", " +
                    "\"maxstrikes\": " + provider.maxStrikes + ", " +
                    "\"companyname\": \"" + provider.companyName +
                    "\"}}";
        }

        if(MyLocalBookingAPI.jwt != null){
            APICall call = new APICall(MyLocalBookingAPI.jwt, "POST", requestBody, url);
            RequestQueueSingleton.getInstance().add(call.getRequest());
        }else{
            LoginAPI.addWaitingRequest(requestBody, url, "POST");
        }

        //passare lambda come parametro di APICall.
        SessionPreferences.setUserPrefs(user);
    }

    @Override
    public void changeUserPassword(String new_password) {
        String cellphone = (String) SessionPreferences.getUserPrefs().get("cellphone");
        String url = "https://mylocalbooking-api-o1he.onrender.com/api/change_user_password/" + cellphone;

        try {
            new_password = AESCrypt.encrypt(new_password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String requestBody = "{\"new_password\": \"" + new_password + "\"}";

        if(MyLocalBookingAPI.jwt != null){
            APICall call = new APICall(MyLocalBookingAPI.jwt, "PATCH", requestBody, url);
            RequestQueueSingleton.getInstance().add(call.getRequest());
        }else{
            LoginAPI.addWaitingRequest(requestBody, url, "PATCH");
        }
    }

    @Override
    public Slot setSlotPassword(String password, Slot slot) {
        return null;
    }

    @Override
    public long addBlueprint(SlotBlueprint blueprint) {
        return 0;
    }

    @Override
    public Establishment addEstablishment(Establishment establishment) {
        return null;
    }

    @Override
    public Collection<Establishment> GetOwnedEstablishments() {
        return null;
    }

    @Override
    public void banUser(Client client) {

    }

    @Override
    public void unbanUser(Client client) {

    }

    @Override
    public void strikeUser(Client client) {

    }

    @Override
    public void setMaxStrikes(int max) {

    }

    @Override
    public void addReservation(Slot slot, String password) {

    }

    @Override
    public void cancelReservation(Slot slot) {

    }

    @Override
    public Collection<Establishment> getClosestEstablishments() {
        return null;
    }

    @Override
    public void setPreferredPosition(Coordinates position) {

    }

    @Override
    public void rateEstablishment(Establishment establishment, int rating, String comment) {

    }
}