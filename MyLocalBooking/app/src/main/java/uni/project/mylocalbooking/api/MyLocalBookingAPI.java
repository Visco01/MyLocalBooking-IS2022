package uni.project.mylocalbooking.api;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.helpers.Util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.SessionPreferences;
import uni.project.mylocalbooking.models.AppUser;
import uni.project.mylocalbooking.models.Client;
import uni.project.mylocalbooking.models.Coordinates;
import uni.project.mylocalbooking.models.Establishment;
import uni.project.mylocalbooking.models.IDatabaseSubclassModel;
import uni.project.mylocalbooking.models.ManualSlot;
import uni.project.mylocalbooking.models.ManualSlotBlueprint;
import uni.project.mylocalbooking.models.PeriodicSlot;
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
    public AppUser getUserByCellphone(String cellphone){
        String url = MyLocalBookingAPI.apiPrefix + "app_user_by_cellphone/" + cellphone;
        JSONObject data = new BlockingAPICall<JSONObject>(MyLocalBookingAPI.jwt, "GET", null, url, false).call().waitResponse();

        try {
            return AppUser.fromJson(data);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void register(AppUser user, String password, APICallBack<AppUser> onSuccess, APICallBack<StatusCode> onError) {
        String url = MyLocalBookingAPI.apiPrefix + "app_users";
        password = Utility.generateEncryptedPassword(password);
        String requestBody = JSONBodyGenerator.generateRegisterBody(user, password);
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "POST", (RunOnResponse<JSONObject>) response -> {
            try {
                user.setId(Long.valueOf(response.getString("app_user_id")));
                if(user instanceof Client){
                    Client client = (Client) user;
                    client.setSubclassId(Long.valueOf(response.getString("concrete_user_id")));
                }else{
                    Provider provider = (Provider) user;
                    provider.setSubclassId(Long.valueOf(response.getString("concrete_user_id")));
                }
                if(user.getId() != null && onSuccess != null) onSuccess.apply(user);
            } catch (Exception e) {
                if(onError != null) onError.apply(StatusCode.JSONOBJECT_PARSE_ERROR);
            }
            SessionPreferences.setUserPrefs(user);
        }, false);
    }

    @Override
    public void login(String cellphone, String password, MutableLiveData<AppUser> loginOutcome){
        AppUser user = getUserByCellphone(cellphone);
        if(user == null) {
            loginOutcome.postValue(null);
        }
        try {
            if(AESCrypt.encrypt(password).equals(user.password)){
                SessionPreferences.setUserPrefs(user);
                loginOutcome.postValue(user);
            }else{
                loginOutcome.postValue(null);
            }
        } catch (Exception encryptException) {
            encryptException.printStackTrace();
            loginOutcome.postValue(null);
        }
    }

    //dato id est popolare collection
    @Override
    public void changeUserPassword(String new_password, APICallBack<Void> onSuccess, APICallBack<StatusCode> onError) {
        String cellphone = (String) SessionPreferences.getUserPrefs().get("cellphone");
        if(cellphone == null && onError != null){
            onError.apply(StatusCode.SESSION_PREFERENCES_NOT_FOUND);
            return;
        }
        String url = MyLocalBookingAPI.apiPrefix + "change_user_password/" + cellphone;
        System.out.println(new_password);
        new_password = Utility.generateEncryptedPassword(new_password);
        System.out.println(new_password);
        String requestBody = JSONBodyGenerator.generateNewPasswordBody(new_password);
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "PATCH", (RunOnResponse<JSONObject>) response -> {
            try {
                String status = response.getString("status");
                if(status.equals("OK")){
                    if(onSuccess != null) onSuccess.apply(null);
                }else{
                    if(onError != null) onError.apply(StatusCode.UNPROCESSABLE_ENTITY);
                }
            } catch (JSONException e) {
                if(onError != null) onError.apply(StatusCode.JSONOBJECT_PARSE_ERROR);
            }
        }, false);
    }

    @Override
    public void setSlotPassword(String new_password, Slot slot, APICallBack<Slot> onSuccess, APICallBack<StatusCode> onError) {
        String url = MyLocalBookingAPI.apiPrefix + "change_slot_password/" + slot.getId();
        new_password = Utility.generateEncryptedPassword(new_password);
        String requestBody = JSONBodyGenerator.generateNewPasswordBody(new_password);
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "PATCH", (RunOnResponse<JSONObject>) response -> {
            try {
                String status = response.getString("status");
                if(status.equals("OK")){
                    slot.passwordProtected = true;
                    if(onSuccess != null) onSuccess.apply(slot);
                }else{
                    if(onError != null) onError.apply(StatusCode.UNPROCESSABLE_ENTITY);
                }
            } catch (JSONException e) {
                if(onError != null) onError.apply(StatusCode.JSONOBJECT_PARSE_ERROR);
            }
        }, false);
    }

    @Override
    public void addBlueprint(SlotBlueprint blueprint, APICallBack<SlotBlueprint> onSuccess, APICallBack<StatusCode> onError) {
        String url = MyLocalBookingAPI.apiPrefix + "slot_blueprints";
        String requestBody = JSONBodyGenerator.generateAddBlueprintBody(blueprint);
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "POST", (RunOnResponse<JSONObject>) response -> {
            try {
                String status = response.getString("status");
                if(status.equals("OK")){
                    blueprint.setId(Long.valueOf(response.getString("slot_blueprint_id")));
                    if(blueprint instanceof PeriodicSlotBlueprint){
                        PeriodicSlotBlueprint pBlueprint = (PeriodicSlotBlueprint) blueprint;
                        pBlueprint.setSubclassId(Long.valueOf(response.getString("concrete_blueprint_id")));
                    }else{
                        ManualSlotBlueprint mBlueprint = (ManualSlotBlueprint) blueprint;
                        mBlueprint.setSubclassId(Long.valueOf(response.getString("concrete_blueprint_id")));
                    }
                    if(onSuccess != null) onSuccess.apply(blueprint);
                }else{
                    if(onError != null) onError.apply(StatusCode.UNPROCESSABLE_ENTITY);
                }
            } catch (JSONException e) {
                if(onError != null) onError.apply(StatusCode.JSONOBJECT_PARSE_ERROR);
            }
        }, false);
    }

    private void addSlot(Slot slot, String password, APICallBack<Slot> onSuccess, APICallBack<StatusCode> onError){
        String url = MyLocalBookingAPI.apiPrefix + "slots";
        String requestBody = JSONBodyGenerator.generateAddSlotBody(slot, password);
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "POST", (RunOnResponse<JSONObject>) response -> {
            try {
                String status = response.getString("status");
                if(status.equals("OK")){
                    slot.setId(Long.valueOf(response.getString("slot_id")));
                    if(slot instanceof PeriodicSlot){
                        PeriodicSlot pSlot = (PeriodicSlot) slot;
                        pSlot.setSubclassId(Long.valueOf(response.getString("concrete_slot_id")));
                    }else{
                        ManualSlot mSlot = (ManualSlot) slot;
                        mSlot.setSubclassId(Long.valueOf(response.getString("concrete_slot_id")));
                    }
                    if(onSuccess != null) onSuccess.apply(slot);
                }else{
                    if(onError != null) onError.apply(StatusCode.UNPROCESSABLE_ENTITY);
                }
            } catch (JSONException e) {
                if(onError != null) onError.apply(StatusCode.JSONOBJECT_PARSE_ERROR);
            }
        }, false);
    }

    @Override
    public void addEstablishment(Establishment establishment, APICallBack<Establishment> onSuccess, APICallBack<StatusCode> onError) {
        String url = MyLocalBookingAPI.apiPrefix + "create_establishments";
        String requestBody = JSONBodyGenerator.generateAddEstablishmentBody(establishment);
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "POST", (RunOnResponse<JSONObject>) data -> {
            try {
                String status = data.getString("status");
                if(status.equals("Created")){
                    establishment.setId(data.getLong("establishment_id"));
                    if(onSuccess != null) onSuccess.apply(establishment);
                }else{
                    if(onError != null) onError.apply(StatusCode.UNPROCESSABLE_ENTITY);
                }
            } catch (JSONException e) {
                if(onError != null) onError.apply(StatusCode.JSONOBJECT_PARSE_ERROR);
            }
        }, false);
    }

    @Override
    public void getOwnedEstablishments(APICallBack<Collection<Establishment>> onSuccess, APICallBack<StatusCode> onError) {
        Long appUserId = (Long) SessionPreferences.getUserPrefs().get("id");;
        if(appUserId == null)
            return;

        Long providerId = getProviderByAppUserId(appUserId);
        if(providerId == null) {
            return;
        }
        String url = MyLocalBookingAPI.apiPrefix + "establishments_by_provider_id/" + providerId; // TODO: conform API response to new json models
        new CallbackAPICall<JSONArray>(MyLocalBookingAPI.jwt, "GET", null, url, data -> {
            Collection<Establishment> establishments = new ArrayList<>();
            for(int i = 0; i < data.length(); i++) {
                try {
                    establishments.add(new Establishment(data.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                    if(onError != null) onError.apply(StatusCode.JSONOBJECT_PARSE_ERROR);
                    return;
                }
            }

            if(onSuccess != null) onSuccess.apply(establishments);
        }, true).call();
    }

    @Override
    public void banUser(Client client, APICallBack<StatusCode> onSuccess, APICallBack<StatusCode> onError) {
        Long providerId = (Long) SessionPreferences.getUserPrefs().get("subclass_id");
        //providerId = 377L;
        if(providerId == null)
            return;

        String url = MyLocalBookingAPI.apiPrefix + "blacklists";
        String requestBody = JSONBodyGenerator.generateStrikeUserBody(providerId, client.cellphone);
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "POST", (RunOnResponse<JSONObject>) data -> {
            if(data.has("id"))
                if(onSuccess != null) onSuccess.apply(StatusCode.CREATED);
            else
                if(onError != null) onError.apply(StatusCode.UNPROCESSABLE_ENTITY);
        }, false);
    }

    @Override
    public void unbanUser(Client client, APICallBack<StatusCode> onSuccess, APICallBack<StatusCode> onError) {
        Long providerId = (Long) SessionPreferences.getUserPrefs().get("subclass_id");
        //providerId = 377L;
        if(providerId == null)
            return;

        String url = MyLocalBookingAPI.apiPrefix + "providers/delete_blacklist_by_params";
        String requestBody = JSONBodyGenerator.generateUnbanUserBody(providerId, client.cellphone);
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "POST", (RunOnResponse<JSONObject>) data -> {
            if(data.has("error"))
                if(onError != null) onError.apply(StatusCode.NOT_FOUND);
            else
                if(onSuccess != null) onSuccess.apply(StatusCode.DELETED);
        }, false);
    }

    @Override
    public void strikeUser(Client client, APICallBack<StatusCode> onSuccess, APICallBack<StatusCode> onError) {
        Long providerId = (Long) SessionPreferences.getUserPrefs().get("subclass_id");
        //providerId = 378L;
        if(providerId == null)
            return;

        String url = MyLocalBookingAPI.apiPrefix + "strikes";
        String requestBody = JSONBodyGenerator.generateStrikeUserBody(providerId, client.cellphone);
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "POST", (RunOnResponse<JSONObject>) data -> {
            if(data.has("id"))
                if(onSuccess != null) onSuccess.apply(StatusCode.CREATED);
            else
                if(onError != null) onError.apply(StatusCode.UNPROCESSABLE_ENTITY);
        }, false);
    }

    @Override
    public void setMaxStrikes(int max, APICallBack<StatusCode> onSuccess, APICallBack<StatusCode> onError) {
        Long providerId = (Long) SessionPreferences.getUserPrefs().get("subclass_id");
        //providerId = 377L;
        if(providerId == null)
            return;

        String url = MyLocalBookingAPI.apiPrefix + "providers/" + providerId + "/set_max_strikes?new_max_strikes=" + max;
        Utility.callAPI(MyLocalBookingAPI.jwt, "{\"sample\": \"sample\"}", url, "POST", (RunOnResponse<JSONObject>) data -> {
            if(data.has("error"))
                if(onError != null) onError.apply(StatusCode.NOT_FOUND);
            if(data.has("success"))
                if(onSuccess != null) onSuccess.apply(StatusCode.CREATED);
            else
                if(onError != null) onError.apply(StatusCode.UNPROCESSABLE_ENTITY);
        }, false);
    }

    @Override
    public void addReservation(Slot slot, String password, APICallBack<Slot> onSuccess, APICallBack<StatusCode> onError) {
        Map<String, ?> prefs = SessionPreferences.getUserPrefs();
        Long currentUserId = (Long) prefs.get("id");


        if(slot.getId() == null){
            if(slot.isOwner(MyLocalBooking.getCurrentUser()))
                addSlot(slot, password, onSuccess, onError);
            else
                addSlot(slot, null, onSuccess, onError);
        } else {
            if(slot.passwordProtected){
                getSlotPasswordById(slot.getId(), data -> {
                    if(data.equals(Utility.generateEncryptedPassword(password))){
                        getClientByAppUserId((long) currentUserId, clientId -> callAddReservation(clientId, slot, onSuccess, onError), onError);
                    }
                }, onError);
            } else {
                getClientByAppUserId((long) currentUserId, clientId -> callAddReservation(clientId, slot, onSuccess, onError), onError);
            }
        }
    }

    private void callAddReservation(Long clientId, Slot slot, APICallBack<Slot> onSuccess, APICallBack<StatusCode> onError){
        String url = MyLocalBookingAPI.apiPrefix + "reservations";
        String requestBody = JSONBodyGenerator.generateReservationBody(clientId, slot.getId());
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "POST", (RunOnResponse<JSONObject>) response -> {
            try {
                String status = response.getString("status");
                if(status.equals("OK")){
                    if(onSuccess != null) onSuccess.apply(slot);
                }else{
                    if(onError != null) onError.apply(StatusCode.UNPROCESSABLE_ENTITY);
                }
            } catch (JSONException e) {
                if(onError != null) onError.apply(StatusCode.JSONOBJECT_PARSE_ERROR);
            }
        }, false);
    }

    private void getClientByAppUserId(Long appUserId, APICallBack<Long> onSuccess, APICallBack<StatusCode> onError){
        String url = MyLocalBookingAPI.apiPrefix + "client_by_app_user_id/" + appUserId.toString();
        Utility.callAPI(MyLocalBookingAPI.jwt, null, url, "GET", (RunOnResponse<JSONObject>) response -> {
            try {
                String status = response.getString("status");
                if(status.equals("OK")){
                    if(onSuccess != null) onSuccess.apply(Long.valueOf(response.getString("client_id")));
                }else{
                    if(onError != null) onError.apply(StatusCode.NOT_FOUND);
                }
            } catch (JSONException e) {
                if(onError != null) onError.apply(StatusCode.JSONOBJECT_PARSE_ERROR);
            }
        }, false);
    }

    private Long getProviderByAppUserId(Long appUserId){
        String url = MyLocalBookingAPI.apiPrefix + "provider_by_app_user_id/" + appUserId.toString();
        BlockingAPICall<JSONObject> call = Utility.callAPI(MyLocalBookingAPI.jwt, null, url, "GET", false);
        call.call();
        try {
            return Long.valueOf(call.waitResponse().getString("provider_id"));
        } catch (JSONException e) {
            return null;
        }
    }

    private void getSlotPasswordById(Long slotId, APICallBack<String> onSuccess, APICallBack<StatusCode> onError){
        String url = MyLocalBookingAPI.apiPrefix + "slot_password_by_id/" + slotId;
        Utility.callAPI(MyLocalBookingAPI.jwt, null, url, "GET", (RunOnResponse<JSONObject>) response -> {
            try {
                String status = response.getString("status");
                if(status.equals("OK")){
                    if(onSuccess != null) onSuccess.apply(response.getString("password_digest"));
                }else{
                    if(onError != null) onError.apply(StatusCode.NOT_FOUND);
                }
            } catch (JSONException e) {
                if(onError != null) onError.apply(StatusCode.JSONOBJECT_PARSE_ERROR);
            }
        }, false);
    }

    @Override
    public void cancelReservation(Slot slot, APICallBack<Slot> onSuccess, APICallBack<StatusCode> onError) {
        Map<String, ?> prefs = SessionPreferences.getUserPrefs();
        int currentUserId = (int) prefs.get("id");

        getClientByAppUserId((long) currentUserId, clientId -> {
            String url = MyLocalBookingAPI.apiPrefix + "delete_reservation_by_ids";
            String requestBody = JSONBodyGenerator.generateReservationBody(clientId, slot.getId());
            Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "POST", (RunOnResponse<JSONObject>) response -> {
                try {
                    String status = response.getString("status");
                    if(status.equals("OK")){
                        boolean slot_id = Long.parseLong(response.getString("slot_id")) != -1;
                        if(!slot_id) slot.setId(null);
                        if(onSuccess != null) onSuccess.apply(slot);
                    }else{
                        if(onError != null) onError.apply(StatusCode.NOT_FOUND);
                    }
                } catch (JSONException e) {
                    if(onError != null) onError.apply(StatusCode.JSONOBJECT_PARSE_ERROR);
                }
            }, false);
        }, onError);
    }

    @Override
    public void getClosestEstablishments(APICallBack<Collection<Establishment>> onSuccess, APICallBack<StatusCode> onError) {
        Long appUserId = (Long) SessionPreferences.getUserPrefs().get("id");
        Double lat = SessionPreferences.getDouble("lat", null);
        Double lng = SessionPreferences.getDouble("lng", null);
        int range = 500000000; // TODO: change testing range
        if(appUserId == null || lat == null || lng == null)
            return;

        String url = MyLocalBookingAPI.apiPrefix + "closest_establishments?" + "lat=" + lat + "&lng=" + lng + "&range=" + range;
        new CallbackAPICall<JSONArray>(MyLocalBookingAPI.jwt, "GET", null, url, data -> {
            Collection<Establishment> establishments = new ArrayList<>();
            for(int i = 0; i < data.length(); i++) {
                try {
                    establishments.add(new Establishment(data.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                    if(onError != null) onError.apply(StatusCode.JSONOBJECT_PARSE_ERROR);
                    return;
                }
            }

            if(onSuccess != null) onSuccess.apply(establishments);
        }, true).call();
    }

    @Override
    public void setPreferredPosition(Coordinates position, APICallBack<Void> onSuccess, APICallBack<StatusCode> onError) {
        Long clientId = (Long) SessionPreferences.getUserPrefs().get("subclass_id");
        if(clientId == -1 && onError != null){
            onError.apply(StatusCode.SESSION_PREFERENCES_NOT_FOUND);
            return;
        }
        String url = MyLocalBookingAPI.apiPrefix + "set_preferred_position/" + clientId;
        String requestBody = JSONBodyGenerator.getSetPreferredPositionBody(position);
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "PATCH", new RunOnResponse<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    Log.i("client id on set position", String.valueOf(clientId));
                    if(status.equals("OK") && onSuccess != null) onSuccess.apply(null);
                    else if(onError != null) onError.apply(StatusCode.UNPROCESSABLE_ENTITY);
                } catch (JSONException e) {
                    if(onError != null) onError.apply(StatusCode.JSONOBJECT_PARSE_ERROR);
                }

            }
        }, false);
    }

    @Override
    public void rateEstablishment(Establishment establishment, float rating, String comment, APICallBack<StatusCode> onSuccess, APICallBack<StatusCode> onError) {
        Long clientId = (Long) SessionPreferences.getUserPrefs().get("subclass_id");
        clientId = 1296L;
        if(clientId == -1 && onError != null){
            onError.apply(StatusCode.SESSION_PREFERENCES_NOT_FOUND);
            return;
        }
        String url = MyLocalBookingAPI.apiPrefix + "ratings";
        String requestBody = JSONBodyGenerator.generateRatingBody(clientId, establishment.getId(), rating, comment);
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "POST", (RunOnResponse<JSONObject>) data -> {
            if(data.has("success"))
                if(onSuccess != null) onSuccess.apply(StatusCode.CREATED);
            else
                if(onError != null) onError.apply(StatusCode.UNPROCESSABLE_ENTITY);
        }, false);
    }

    @Override
    public boolean getReservations(Establishment establishment, LocalDate date) {
        String url = MyLocalBookingAPI.apiPrefix + "slots?establishment_id=" + establishment.getId() + "&date='" + date.toString() + "'";
        HashMap<Long, SlotBlueprint> blueprints = new HashMap<>();
        for(SlotBlueprint b : establishment.blueprints)
            blueprints.put(b.getId(), b);

        JSONArray response = new BlockingAPICall<JSONArray>(MyLocalBookingAPI.jwt, "GET", null, url, true).call().waitResponse();
        try {
            for(int i = 0; i < response.length(); i++)
                Slot.fromJson(response.getJSONObject(i), blueprints);
        } catch (JSONException e) {
            e.printStackTrace();
            // results must be complete, partial results must be deleted
            establishment.blueprints.forEach(blueprint -> blueprint.slots.remove(date));
            return false;
        }

        return true;
    }
}
