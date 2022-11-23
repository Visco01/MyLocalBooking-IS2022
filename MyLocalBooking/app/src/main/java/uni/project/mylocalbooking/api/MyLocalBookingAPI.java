package uni.project.mylocalbooking.api;

import android.util.Log;
import org.json.JSONException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import uni.project.mylocalbooking.SessionPreferences;
import uni.project.mylocalbooking.models.AppUser;
import uni.project.mylocalbooking.models.Client;
import uni.project.mylocalbooking.models.Coordinates;
import uni.project.mylocalbooking.models.Establishment;
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

    private void getUserByCellphone(String cellphone, APICallBack<AppUser> onSuccess, APICallBack<String> onError){
        String url = MyLocalBookingAPI.apiPrefix + "app_user_by_cellphone/" + cellphone;
        Utility.callAPI(MyLocalBookingAPI.jwt, null, url, "GET", response -> {
            try {
                String status = response.getString("status");
                if(status.equals("OK")){
                    AppUser user;
                    String category = response.getString("category");
                    String appUserId = response.getString("app_user_id");
                    String concreteUserId = response.getString("concrete_user_id");
                    String password = response.getString("password_digest");
                    String email = response.getString("email");
                    String firstName = response.getString("firstname");
                    String lastName = response.getString("lastname");
                    String[] dob = response.getString("dob").split("-");
                    if(category.equals("client")){
                        String lat = response.getString("lat");
                        String lng = response.getString("lng");
                        user = new Client(Long.valueOf(concreteUserId), new Coordinates(Float.valueOf(lat), Float.valueOf(lng)), Long.valueOf(appUserId), cellphone, email, firstName, lastName, LocalDate.of(Integer.valueOf(dob[0]), Integer.valueOf(dob[1]), Integer.valueOf(dob[2])), password);
                    }else{
                        String isVerified = response.getString("isverified");
                        String maxStrikes = response.getString("maxstrikes");
                        String companyName = response.getString("companyname");
                        user = new Provider(Long.valueOf(concreteUserId), Boolean.valueOf(isVerified), companyName, Integer.valueOf(maxStrikes), null, Long.valueOf(appUserId), cellphone, email, firstName, lastName, LocalDate.of(Integer.valueOf(dob[0]), Integer.valueOf(dob[1]), Integer.valueOf(dob[2])), password);
                    }
                    Log.i("user login", user.toString());
                    if(onSuccess != null) onSuccess.apply(user);
                }else{
                    if(onError != null) onError.apply("user with cellphone " + cellphone + " not found");
                }
            } catch (Exception e) {
                if(onError != null) onError.apply(e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @Override
    public void register(AppUser user, String password, APICallBack<AppUser> callBack) {
        String url = MyLocalBookingAPI.apiPrefix + "app_users";
        password = Utility.generateEncryptedPassword(password);
        String requestBody = JSONBodyGenerator.generateRegisterBody(user, password);
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "POST", response -> {
            try {
                user.setId(Long.valueOf(response.getString("app_user_id")));
                if(user instanceof Client){
                    Client client = (Client) user;
                    client.setSubclassId(Long.valueOf(response.getString("concrete_user_id")));
                }else{
                    Provider provider = (Provider) user;
                    provider.setSubclassId(Long.valueOf(response.getString("concrete_user_id")));
                }
                if(callBack != null) callBack.apply(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
            SessionPreferences.setUserPrefs(user);
        });
    }

    @Override
    public void login(String cellphone, String password, APICallBack<AppUser> onSuccess, APICallBack<String> onError){
        getUserByCellphone(cellphone, data -> {
            try {
                if(AESCrypt.encrypt(password).equals(data.password)){
                    SessionPreferences.setUserPrefs(data);
                    if(onSuccess != null) onSuccess.apply(data);
                }else{
                    if(onError != null) onError.apply("given password is not valid");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, data -> {
            if(onError != null) onError.apply(data);
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
    public void setSlotPassword(String new_password, Slot slot, APICallBack<Slot> callBack) {
        String url = MyLocalBookingAPI.apiPrefix + "change_slot_password/" + slot.getId();
        new_password = Utility.generateEncryptedPassword(new_password);
        String requestBody = JSONBodyGenerator.generateNewPasswordBody(new_password);
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "PATCH", response -> {
            slot.passwordProtected = true;
            if(callBack != null) callBack.apply(slot);
        });
    }

    @Override
    public void addBlueprint(SlotBlueprint blueprint, APICallBack<SlotBlueprint> callBack) {
        String url = MyLocalBookingAPI.apiPrefix + "slot_blueprints";
        String requestBody = JSONBodyGenerator.generateAddBlueprintBody(blueprint);
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "POST", response -> {
            try {
                blueprint.setId(Long.valueOf(response.getString("slot_blueprint_id")));
                if(blueprint instanceof PeriodicSlotBlueprint){
                    PeriodicSlotBlueprint pBlueprint = (PeriodicSlotBlueprint) blueprint;
                    pBlueprint.setSubclassId(Long.valueOf(response.getString("concrete_blueprint_id")));
                }else{
                    ManualSlotBlueprint mBlueprint = (ManualSlotBlueprint) blueprint;
                    mBlueprint.setSubclassId(Long.valueOf(response.getString("concrete_blueprint_id")));
                }
                if(callBack != null) callBack.apply(blueprint);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void addSlot(Slot slot, String password, APICallBack<Slot> callBack){
        String url = MyLocalBookingAPI.apiPrefix + "slots";
        String requestBody = JSONBodyGenerator.generateAddSlotBody(slot, password);
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "POST", response -> {
            try {
                slot.setId(Long.valueOf(response.getString("slot_id")));
                if(slot instanceof PeriodicSlot){
                    PeriodicSlot pSlot = (PeriodicSlot) slot;
                    pSlot.setSubclassId(Long.valueOf(response.getString("concrete_slot_id")));
                }else{
                    ManualSlot mSlot = (ManualSlot) slot;
                    mSlot.setSubclassId(Long.valueOf(response.getString("concrete_slot_id")));
                }
                if(callBack != null) callBack.apply(slot);
            } catch (JSONException e) {
                e.printStackTrace();
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

    @Override
    public void addReservation(Slot slot, String password, APICallBack<Slot> callBack) {
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
                    if(data.equals(Utility.generateEncryptedPassword(password))){
                        getClientByAppUserId((long) currentUserId, clientId -> callAddReservation(clientId, slot, callBack));
                    }
                });
            } else {
                getClientByAppUserId((long) currentUserId, clientId -> callAddReservation(clientId, slot, callBack));
            }
        }
    }

    private void callAddReservation(Long clientId, Slot slot, APICallBack<Slot> callBack){
        String url = MyLocalBookingAPI.apiPrefix + "reservations";
        String requestBody = JSONBodyGenerator.generateReservationBody(clientId, slot.getId());
        Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "POST", response -> {
            if(callBack != null) callBack.apply(slot);
        });
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
                if(callBack != null) callBack.apply(response.getString("password_digest"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void cancelReservation(Slot slot, APICallBack<Slot> callBack) {
        Map<String, ?> prefs = SessionPreferences.getUserPrefs();
        int currentUserId = (int) prefs.get("id");

        getClientByAppUserId((long) currentUserId, clientId -> {
            String url = MyLocalBookingAPI.apiPrefix + "delete_reservation_by_ids";
            String requestBody = JSONBodyGenerator.generateReservationBody(clientId, slot.getId());
            Utility.callAPI(MyLocalBookingAPI.jwt, requestBody, url, "POST", response -> {
                try {
                    boolean slot_id = Long.valueOf(response.getString("slot_id")) != -1;
                    if(!slot_id) slot.setId(null);
                    if(callBack != null) callBack.apply(slot);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
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
