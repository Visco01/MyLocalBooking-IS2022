package uni.project.mylocalbooking.api;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import uni.project.mylocalbooking.SessionPreferences;
import uni.project.mylocalbooking.models.AppUser;
import uni.project.mylocalbooking.models.Client;
import uni.project.mylocalbooking.models.Coordinates;
import uni.project.mylocalbooking.models.Establishment;
import uni.project.mylocalbooking.models.ManualSlotBlueprint;
import uni.project.mylocalbooking.models.PeriodicSlotBlueprint;
import uni.project.mylocalbooking.models.Provider;
import uni.project.mylocalbooking.models.Slot;
import uni.project.mylocalbooking.models.SlotBlueprint;

class MyLocalBookingAPI implements IMyLocalBookingAPI {
    private static String jwt = null;
    private static String apiPrefix = "https://mylocalbooking-api-o1he.onrender.com/api/";


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
        String url = MyLocalBookingAPI.apiPrefix + "app_users";
        password = Utility.generateEncryptedPassword(password);
        String requestBody = JSONBodyGenerator.generateRegisterBody(user, password);
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "POST", new RunOnResponse<JSONObject>() {
            @Override
            public void apply(JSONObject response) {
                try {
                    Log.i("bea", "bea");
                    user.setId(Long.valueOf(response.getString("app_user_id")));
                    if(user instanceof Client){
                        Client client = (Client) user;
                        client.setId(Long.valueOf(response.getString("concrete_user_id")));
                    }else{
                        Provider provider = (Provider) user;
                        provider.setId(Long.valueOf(response.getString("concrete_user_id")));
                        Log.i("provider_id", String.valueOf(provider.getId()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SessionPreferences.setUserPrefs(user);
            }
        });
    }

    @Override
    public void changeUserPassword(String new_password) {
        String cellphone = (String) SessionPreferences.getUserPrefs().get("cellphone");
        String url = MyLocalBookingAPI.apiPrefix + "change_user_password/" + cellphone;
        new_password = Utility.generateEncryptedPassword(new_password);
        String requestBody = JSONBodyGenerator.generateNewPasswordBody(new_password);
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "PATCH", null);
    }

    @Override
    public void setSlotPassword(String new_password, Slot slot) {
        String url = MyLocalBookingAPI.apiPrefix + "change_slot_password/" + slot.getId();
        new_password = Utility.generateEncryptedPassword(new_password);
        String requestBody = JSONBodyGenerator.generateNewPasswordBody(new_password);
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "PATCH", new RunOnResponse<JSONObject>() {
            @Override
            public void apply(JSONObject response) {
                slot.passwordProtected = true;
            }
        });
    }

    @Override
    public void addBlueprint(SlotBlueprint blueprint) {
        String url = MyLocalBookingAPI.apiPrefix + "slot_blueprints";
        String requestBody = JSONBodyGenerator.generateAddBlueprintBody(blueprint);
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "POST", new RunOnResponse<JSONObject>() {
            @Override
            public void apply(JSONObject response) {
                try {
                    blueprint.setId(Long.valueOf(response.getString("slot_blueprint_id")));
                    if(blueprint instanceof PeriodicSlotBlueprint){
                        PeriodicSlotBlueprint pBlueprint = (PeriodicSlotBlueprint) blueprint;
                        pBlueprint.setId(Long.valueOf(response.getString("concrete_blueprint_id")));
                    }else{
                        ManualSlotBlueprint mBlueprint = (ManualSlotBlueprint) blueprint;
                        mBlueprint.setId(Long.valueOf(response.getString("concrete_blueprint_id")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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

    // X
    @Override
    public void addReservation(Slot slot, String password) {
        if(slot.getId() != null){
            Map<String, ?> prefs = SessionPreferences.getUserPrefs();
            //if(prefs.get(""))
        }
    }

    // X
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
