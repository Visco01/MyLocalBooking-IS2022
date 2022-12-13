package uni.project.mylocalbooking.api;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    private void getUserByCellphone(String cellphone, APICallBack<AppUser> onSuccess, APICallBack<StatusCode> onError){
        String url = MyLocalBookingAPI.apiPrefix + "app_user_by_cellphone/" + cellphone;
        Utility.callAPI(MyLocalBookingAPI.jwt, null, url, "GET", (RunOnResponse<JSONObject>) response -> {
            try {
                String status = response.getString("status");
                if (status.equals("OK")) {
                    AppUser user;
                    String category = response.getString("category");
                    String appUserId = response.getString("app_user_id");
                    String concreteUserId = response.getString("concrete_user_id");
                    String password = response.getString("password_digest");
                    String email = response.getString("email");
                    String firstName = response.getString("firstname");
                    String lastName = response.getString("lastname");
                    String[] dob = response.getString("dob").split("-");
                    if (category.equals("client")) {
                        String lat = response.getString("lat");
                        String lng = response.getString("lng");
                        user = new Client(Long.valueOf(concreteUserId), new Coordinates(Float.parseFloat(lat), Float.parseFloat(lng)), Long.valueOf(appUserId), cellphone, email, firstName, lastName, LocalDate.of(Integer.parseInt(dob[0]), Integer.parseInt(dob[1]), Integer.parseInt(dob[2])), password);
                    } else {
                        String isVerified = response.getString("isverified");
                        String maxStrikes = response.getString("maxstrikes");
                        String companyName = response.getString("companyname");
                        user = new Provider(Long.valueOf(concreteUserId), Boolean.parseBoolean(isVerified), companyName, Integer.valueOf(maxStrikes), null, Long.valueOf(appUserId), cellphone, email, firstName, lastName, LocalDate.of(Integer.parseInt(dob[0]), Integer.parseInt(dob[1]), Integer.parseInt(dob[2])), password);
                    }
                    Log.i("user login", user.toString());
                    if (onSuccess != null) onSuccess.apply(user);
                } else {
                    if (onError != null) onError.apply(StatusCode.NOT_FOUND);
                }
            } catch (Exception e) {
                if (onError != null) onError.apply(StatusCode.JSONOBJECT_PARSE_ERROR);
                e.printStackTrace();
            }
        }, false);
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
    public void login(String cellphone, String password, APICallBack<AppUser> onSuccess, APICallBack<StatusCode> onError){
        getUserByCellphone(cellphone, data -> {
            try {
                if(AESCrypt.encrypt(password).equals(data.password)){
                    SessionPreferences.setUserPrefs(data);
                    if(onSuccess != null) onSuccess.apply(data);
                }else{
                    if(onError != null) onError.apply(StatusCode.UNAUTHORIZED);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, data -> {
            if(onError != null) onError.apply(data);
        });
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
        new_password = Utility.generateEncryptedPassword(new_password);
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
    public void getOwnedEstablishments(MutableLiveData<Collection<Establishment>> establishmentsLivedata) {
        Long appUserId = (Long) SessionPreferences.getUserPrefs().get("id");
        if(appUserId == null)
            return;
        
        Runnable runnable = () -> {
            Long providerId = getProviderByAppUserId(appUserId);
            if(providerId == null) {
                return;
            }

            String url = MyLocalBookingAPI.apiPrefix + "establishments_by_provider_id/" + providerId;
            SyncApiCall<JSONArray> call = new SyncApiCall<>(MyLocalBookingAPI.jwt, "GET", null, url, true);
            call.call();
            Collection<Establishment> ownedEstablishments = Utility.getOwnedEstablishmentData(call.waitResponse());

            List<Thread> threads = new ArrayList<>();
            for(Establishment establishment : ownedEstablishments){
                Thread t = new Thread(() -> getSlotsByBlueprint(establishment.blueprints));
                threads.add(t);
                t.start();
            }

            for(Thread t : threads) {
                try {
                    t.join();
                } catch (InterruptedException ignored) {}
            }

            establishmentsLivedata.postValue(ownedEstablishments);
        };

        new Thread(runnable).start();
    }

    private void getSlotsByBlueprint(Collection<SlotBlueprint> blueprints) {
        List<Thread> threads = new ArrayList<>();
        for(SlotBlueprint blueprint : blueprints) {
            String type = (blueprint instanceof ManualSlotBlueprint) ? "manual" : "periodic";
            String url = MyLocalBookingAPI.apiPrefix + "concrete_slot_by_blueprint_id/" + type + "/" + ((IDatabaseSubclassModel) blueprint).getSubclassId();

            Thread t = new Thread(() -> {
                SyncApiCall<JSONArray> call = new SyncApiCall<>(MyLocalBookingAPI.jwt, "GET", null, url, true);
                call.call();
                blueprint.slots = Utility.getSlots(call.waitResponse(), blueprint);
                getReservationsBySlot(blueprint.slots);
            });
            threads.add(t);
            t.start();
        }

        for(Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException ignored) {}
        }
    }

    private void getReservationsBySlot(Collection<Slot> slots){
        ApiCallBatch<JSONArray> batch = new ApiCallBatch<>();

        for(Slot elem : slots){
            String url = MyLocalBookingAPI.apiPrefix + "reservations_by_slot_id/" + elem.getId();
            batch.add(new SyncApiCall<>(MyLocalBookingAPI.jwt, "GET", null, url, true), response -> {
                elem.reservations = Utility.getReservations(response);
            });
        }

        batch.run();
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
    public void addReservation(Slot slot, String password, APICallBack<Slot> onSuccess, APICallBack<StatusCode> onError) {
        Map<String, ?> prefs = SessionPreferences.getUserPrefs();
        int currentUserId = (int) prefs.get("id");

        if(slot.getId() != null){
            if(currentUserId == slot.owner.getId())
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
        SyncApiCall<JSONObject> call = Utility.callAPI(MyLocalBookingAPI.jwt, null, url, "GET", false);
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
