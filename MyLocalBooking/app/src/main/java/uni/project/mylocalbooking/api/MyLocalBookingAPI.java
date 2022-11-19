package uni.project.mylocalbooking.api;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.helpers.Util;

import java.util.Collection;
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
    private static final String apiPrefix = "https://mylocalbooking-api-o1he.onrender.com/api/";

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
    public void getUserIdByCellphone(String cellphone, AppUser user, APICallBack<Void> callBack){
        String url = MyLocalBookingAPI.apiPrefix + "app_user_by_cellphone/" + cellphone;
        Utility.callAPI(MyLocalBookingAPI.jwt, null, url, "GET", response -> {
            try {
                String response_id = response.getString("app_user_id");
                user.setId(Long.valueOf(response_id));
                if(callBack != null) callBack.apply(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void register(AppUser user, String password, APICallBack<Void> callBack) {
        String url = MyLocalBookingAPI.apiPrefix + "app_users";
        password = Utility.generateEncryptedPassword(password);
        String requestBody = JSONBodyGenerator.generateRegisterBody(user, password);
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "POST", response -> {
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
                if(callBack != null) callBack.apply(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            SessionPreferences.setUserPrefs(user);
        });
    }

    @Override
    public void changeUserPassword(String new_password, APICallBack<Void> callBack) {
        String cellphone = (String) SessionPreferences.getUserPrefs().get("cellphone");
        String url = MyLocalBookingAPI.apiPrefix + "change_user_password/" + cellphone;
        new_password = Utility.generateEncryptedPassword(new_password);
        String requestBody = JSONBodyGenerator.generateNewPasswordBody(new_password);
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "PATCH", response -> {
            if(callBack != null) callBack.apply(null);
        });
    }

    @Override
    public void setSlotPassword(String new_password, Slot slot, APICallBack<Void> callBack) {
        String url = MyLocalBookingAPI.apiPrefix + "change_slot_password/" + slot.getId();
        new_password = Utility.generateEncryptedPassword(new_password);
        String requestBody = JSONBodyGenerator.generateNewPasswordBody(new_password);
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "PATCH", response -> {
            slot.passwordProtected = true;
            if(callBack != null) callBack.apply(null);
        });
    }

    @Override
    public void addBlueprint(SlotBlueprint blueprint, APICallBack<Void> callBack) {
        String url = MyLocalBookingAPI.apiPrefix + "slot_blueprints";
        String requestBody = JSONBodyGenerator.generateAddBlueprintBody(blueprint);
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "POST", response -> {
            try {
                blueprint.setId(Long.valueOf(response.getString("slot_blueprint_id")));
                if(blueprint instanceof PeriodicSlotBlueprint){
                    PeriodicSlotBlueprint pBlueprint = (PeriodicSlotBlueprint) blueprint;
                    pBlueprint.setId(Long.valueOf(response.getString("concrete_blueprint_id")));
                }else{
                    ManualSlotBlueprint mBlueprint = (ManualSlotBlueprint) blueprint;
                    mBlueprint.setId(Long.valueOf(response.getString("concrete_blueprint_id")));
                }
                if(callBack != null) callBack.apply(null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void addSlot(Slot slot, String password, APICallBack<Void> callBack){
        String url = MyLocalBookingAPI.apiPrefix + "slots";
        String requestBody = JSONBodyGenerator.generateAddSlotBody(slot, password);
        Log.i("Json Body", requestBody);
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "POST", response -> {
            if(callBack != null) callBack.apply(null);
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

    @Override
    public void addReservation(Slot slot, String password, APICallBack<Void> callBack) {
        Map<String, ?> prefs = SessionPreferences.getUserPrefs();
        int currentUserId = (int) prefs.get("id");

        if(slot.getId() != null){
            if(currentUserId == slot.owner.getId())
                addSlot(slot, password, callBack);
            else
                addSlot(slot, null, callBack);
        } else {
            if(slot.passwordProtected){
                getSlotPasswordById(slot.getId(), data -> {
                    if(data.equals(password)){
                        getClientByAppUserId((long) currentUserId, clientId -> callAddReservation(clientId, slot.getId()));
                    }
                });
            } else {
                getClientByAppUserId((long) currentUserId, clientId -> callAddReservation(clientId, slot.getId()));
            }
        }
    }

    private void callAddReservation(Long clientId, Long slotId){
        String url = MyLocalBookingAPI.apiPrefix + "reservations";
        String requestBody = JSONBodyGenerator.generateReservationBody(clientId, slotId);
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "POST", null);
    }

    private void getClientByAppUserId(Long appUserId, APICallBack<Long> callBack){
        String url = MyLocalBookingAPI.apiPrefix + "client_by_app_user_id/" + appUserId.toString();
        Utility.callAPI(MyLocalBookingAPI.jwt, null, url, "GET", response -> {
            try {
                if(callBack != null) callBack.apply(Long.valueOf(response.getString("client_id")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void getSlotPasswordById(Long slotId, APICallBack<String> callBack){
        String url = MyLocalBookingAPI.apiPrefix + "slots/" + slotId;
        Utility.callAPI(MyLocalBookingAPI.jwt, null, url, "GET", response -> {
            try {
                if(callBack != null) callBack.apply(AESCrypt.decrypt(response.getString("password_digest")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void cancelReservation(Slot slot, APICallBack<Void> callBack) {
        Map<String, ?> prefs = SessionPreferences.getUserPrefs();
        int currentUserId = (int) prefs.get("id");

        getClientByAppUserId(Long.valueOf(currentUserId), new APICallBack<Long>() {
            @Override
            public void apply(Long clientId) {
                String url = MyLocalBookingAPI.apiPrefix + "delete_reservation_by_ids";
                String requestBody = JSONBodyGenerator.generateReservationBody(clientId, slot.getId());
                Log.i("delete body", requestBody);
                Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "POST", new RunOnResponse<JSONObject>() {
                    @Override
                    public void apply(JSONObject response) {
                        Log.i("delete", "delete");
                        if(callBack != null) callBack.apply(null);
                    }
                });
            }
        });
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
