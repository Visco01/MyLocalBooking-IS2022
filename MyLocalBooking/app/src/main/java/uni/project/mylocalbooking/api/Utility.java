package uni.project.mylocalbooking.api;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import uni.project.mylocalbooking.models.Client;
import uni.project.mylocalbooking.models.Coordinates;
import uni.project.mylocalbooking.models.Establishment;
import uni.project.mylocalbooking.models.ManualSlot;
import uni.project.mylocalbooking.models.ManualSlotBlueprint;
import uni.project.mylocalbooking.models.PeriodicSlot;
import uni.project.mylocalbooking.models.PeriodicSlotBlueprint;
import uni.project.mylocalbooking.models.Slot;
import uni.project.mylocalbooking.models.SlotBlueprint;

class Utility {

    public static String generateEncryptedPassword(String password){
        try {
            password = AESCrypt.encrypt(password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return password;
    }

    public static <T> void callAPI(String jwt, String requestBody, String url, String method, RunOnResponse<T> runOnResponse, boolean isArray){
        if(jwt != null){
            APICall call;
            if(isArray){
                call = new APICall<JSONArray, JsonArrayRequest>(jwt, method, requestBody, url, (RunOnResponse<JSONArray>) runOnResponse, true);
                RequestQueueSingleton.getInstance().add((Request<JsonArrayRequest>) call.getRequest());
            }
            else{
                call = new APICall<JSONObject, JsonObjectRequest>(jwt, method, requestBody, url, (RunOnResponse<JSONObject>) runOnResponse, false);
                RequestQueueSingleton.getInstance().add((Request<JsonObjectRequest>) call.getRequest());
            }
        }else{
            LoginAPI.addWaitingRequest(requestBody, url, method, runOnResponse, isArray);
        }
    }

    //mancano hashset weekdays, owner establishment

    public static Collection<Establishment> getOwnedEstablishmentData(JSONArray response) {
        Collection<Establishment> ownedEstablishments = new ArrayList<>();
        try {
            for(int i = 0; i < response.length(); i++) {
                JSONObject jsonEstablishment = response.getJSONObject(i);
                ownedEstablishments.add(getEstablishmentByJSONObject(jsonEstablishment));
            }

        } catch (JSONException e){
            e.printStackTrace();
        }
        return ownedEstablishments;
    }
    
    private static Establishment getEstablishmentByJSONObject(JSONObject object) throws JSONException{
        Establishment newEstablishment;
        Long establishmentId = object.getLong("id");
        String name = object.getString("name");
        float lat = (float) object.getDouble("lat");
        float lng = (float) object.getDouble("lng");
        String address = object.getString("address");
        String placeId = object.getString("place_id");

        newEstablishment = new Establishment(establishmentId, name, address, new Coordinates(lat, lng), placeId);
        newEstablishment.blueprints = new ArrayList<>();

        JSONArray slotBlueprints = object.getJSONArray("slot_blueprints");
        for(int j = 0; j < slotBlueprints.length(); j++){
            JSONObject jsonSlotBlueprint = slotBlueprints.getJSONObject(j);
            newEstablishment.blueprints.add(getSlotBlueprintByJSONObject(jsonSlotBlueprint, newEstablishment));
        }
        return newEstablishment;
    }
    
    private static SlotBlueprint getSlotBlueprintByJSONObject(JSONObject object, Establishment establishment) throws JSONException{
        SlotBlueprint result;
        Long slotBlueprintId = object.getLong("id");
        int weekDays = object.getInt("weekdays");
        int reservationLimit = object.getInt("reservationlimit");
        String[] fromDate = object.getString("fromdate").split("-");
        String[] toDate = object.getString("todate").split("-");

        JSONObject concreteBlueprint = object.getJSONObject("periodic_slot_blueprint");

        Long concreteBlueprintId = concreteBlueprint.getLong("id");
        if(concreteBlueprintId != null){
            String fromTime = concreteBlueprint.getString("fromtime");
            String toTime = concreteBlueprint.getString("totime");
            result = new PeriodicSlotBlueprint(concreteBlueprintId, getTimeByString(fromTime), getTimeByString(toTime), slotBlueprintId, establishment, reservationLimit, /*getDaysOfWeek(weekDays)*/null, LocalDate.of(Integer.valueOf(fromDate[0]), Integer.valueOf(fromDate[1]), Integer.valueOf(fromDate[2])), LocalDate.of(Integer.valueOf(toDate[0]), Integer.valueOf(toDate[1]), Integer.valueOf(toDate[2])));
        } else {
            concreteBlueprint = object.getJSONObject("manual_slot_blueprint");
            concreteBlueprintId = concreteBlueprint.getLong("id");
            String openTime = concreteBlueprint.getString("opentime");
            String closeTime = concreteBlueprint.getString("closetime");
            int maxDuration = concreteBlueprint.getInt("maxduration");
            result = new ManualSlotBlueprint(concreteBlueprintId, getTimeByString(openTime), getTimeByString(closeTime), Duration.ofMinutes(maxDuration), slotBlueprintId, establishment, reservationLimit, /*getDaysOfWeek(weekDays)*/null, LocalDate.of(Integer.valueOf(fromDate[0]), Integer.valueOf(fromDate[1]), Integer.valueOf(fromDate[2])), LocalDate.of(Integer.valueOf(toDate[0]), Integer.valueOf(toDate[1]), Integer.valueOf(toDate[2])));
        }
        return result;
    }

    private static LocalTime getTimeByString(String time){
        time = time.substring(11, 16);
        String[] res = time.split(":");
        return LocalTime.of(Integer.valueOf(res[0]), Integer.valueOf(res[1]));
    }

    private static HashSet<DayOfWeek> getDaysOfWeek(int weekDays){
        HashSet<DayOfWeek> map = new HashSet<>();
        for(int i = 1; i <= 64; i*=2){
            if((i & weekDays) != 0)
                map.add(DayOfWeek.of(6 - i));
        }
        return map;
    }

    public static Collection<Slot> getSlots(JSONArray response, SlotBlueprint blueprint) {
        Collection<Slot> slots = new ArrayList<>();
        try {
            for(int i = 0; i < response.length(); i++){
                JSONObject jsonSlot = response.getJSONObject(i);
                slots.add(getSlot(jsonSlot, blueprint));
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return slots;
    }

    private static Slot getSlot(JSONObject object, SlotBlueprint blueprint) throws JSONException{
        Slot result = null;
        //Long id, Long slot_id, LocalDate date, AppUser owner, boolean passwordProtected, HashSet<Client> reservations, SlotBlueprint blueprint
        //Long id, LocalTime fromTime, LocalTime toTime, Long slot_id, LocalDate date, AppUser owner, boolean passwordProtected, HashSet<Client> reservations, SlotBlueprint blueprint
        JSONObject slot = object.getJSONObject("slot");
        Long slotId = slot.getLong("id");
        String password = slot.getString("password_digest");
        String[] date = slot.getString("date").split("-");
        boolean passwordProtected = false;

        if(password != null) passwordProtected = true;
        if(blueprint instanceof PeriodicSlotBlueprint){
            Long id = object.getLong("id");
            result = new PeriodicSlot(id, slotId, LocalDate.of(Integer.valueOf(date[0]), Integer.valueOf(date[1]), Integer.valueOf(date[2])), null, passwordProtected, null, (PeriodicSlotBlueprint) blueprint);
        }else{
            Long id = object.getLong("id");
            String fromTime = object.getString("fromtime");
            String toTime = object.getString("totime");
            result = new ManualSlot(id, getTimeByString(fromTime), getTimeByString(toTime), slotId, LocalDate.of(Integer.valueOf(date[0]), Integer.valueOf(date[1]), Integer.valueOf(date[2])), null, passwordProtected, null, (ManualSlotBlueprint) blueprint);
        }
        return result;
    }

    public static HashSet<Client> getReservations(JSONArray response) {
        HashSet<Client> reservations = new HashSet<>();
        try {
            for(int i = 0; i < response.length(); i++){
                JSONObject jsonReservation = response.getJSONObject(i);
                reservations.add(getReservation(jsonReservation));
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return reservations;
    }

    private static Client getReservation(JSONObject object) throws JSONException {
        Long clientId = object.getLong("client_id");
        JSONObject client = object.getJSONObject("client");
        Float lat = null;
        Float lng = null;

        if(!client.isNull("lat"))
            lat = (float) client.getDouble("lat");
        if(!client.isNull("lng"))
            lng = (float) client.getDouble("lng");

        Coordinates coordinates = null;
        if(lng != null && lat != null) coordinates = new Coordinates(lat, lng);
        JSONObject appUser = client.getJSONObject("app_user");
        Long appUserId = appUser.getLong("id");
        String cellphone = appUser.getString("cellphone");
        String password = appUser.getString("password_digest");
        String email = appUser.getString("email");
        String firstName = appUser.getString("firstname");
        String lastName = appUser.getString("lastname");
        String[] dob = appUser.getString("dob").split("-");
        return new Client(clientId, coordinates, appUserId, cellphone, email, firstName, lastName, LocalDate.of(Integer.valueOf(dob[0]), Integer.valueOf(dob[1]), Integer.valueOf(dob[2])), password);
    }
}
