package uni.project.mylocalbooking.api;

import android.util.Log;

import org.slf4j.helpers.Util;

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
        password = Utility.generateEncryptedPassword(password);
        String requestBody = JSONBodyGenerator.generateRegisterBody(user, password);
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "POST");
        //passare lambda come parametro di APICall.
        SessionPreferences.setUserPrefs(user);
    }

    @Override
    public void changeUserPassword(String new_password) {
        String cellphone = (String) SessionPreferences.getUserPrefs().get("cellphone");
        String url = "https://mylocalbooking-api-o1he.onrender.com/api/change_user_password/" + cellphone;
        new_password = Utility.generateEncryptedPassword(new_password);
        String requestBody = JSONBodyGenerator.generateNewPasswordBody(new_password);
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "PATCH");
    }

    //How to add new slots? How to get the id once the slot is created?
    //I assume the slot's id is already given (?)

    @Override
    public Slot setSlotPassword(String new_password, Slot slot) {
        String url = "https://mylocalbooking-api-o1he.onrender.com/api/change_slot_password/" + slot.getId();
        new_password = Utility.generateEncryptedPassword(new_password);
        String requestBody = JSONBodyGenerator.generateNewPasswordBody(new_password);
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "PATCH");
        //Why return the slot?
        return slot;
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
