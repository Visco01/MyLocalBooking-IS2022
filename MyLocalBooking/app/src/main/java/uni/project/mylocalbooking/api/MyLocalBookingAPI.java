package uni.project.mylocalbooking.api;

import android.util.Log;
import com.android.volley.Request;
import java.util.Collection;
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
        String requestBody = "{" +
                "\"cellphone\": " + user.cellphone +
                "\"firstname\": " + user.firstname +
                "\"lastname\": " + user.lastname +
                "\"dob\": " + user.dob +
                "\"password_digest\": " + password;

        if(user instanceof Client){
            Client client = (Client) user;
            requestBody += "\"Client\": {" +
                    "\"lat\": " + client.position.latitude +
                    "\"lng\": " + client.position.longitude +
                    "}}";
        } else {
            Provider provider = (Provider) user;
            requestBody += "\"Client\": {" +
                    "\"isverified\": " + provider.verified +
                    "\"maxstrikes\": " + provider.maxStrikes +
                    "\"companyname\": " + provider.companyName +
                    "}}";
        }

        APICall call = new APICall(MyLocalBookingAPI.jwt);
        call.post(requestBody, "http://mylocalbooking-api-o1he.onrender.com/api/app_users");
        RequestQueueSingleton.getInstance().add(call.getRequest());
    }

    @Override
    public void changeUserPassword(String password) {

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
