package uni.project.mylocalbooking.api;

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
        new CallbackAPICall<T>(jwt, method, requestBody, url, runOnResponse, isArray).call();
    }

    public static <T> BlockingAPICall<T> callAPI(String jwt, String requestBody, String url, String method, boolean isArray){
        BlockingAPICall<T> call = new BlockingAPICall<T>(jwt, method, requestBody, url, isArray);
        call.call();
        return call;
    }

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
            SlotBlueprint newBlueprint = getSlotBlueprintByJSONObject(jsonSlotBlueprint, newEstablishment);
        }
        return newEstablishment;
    }
    
    private static SlotBlueprint getSlotBlueprintByJSONObject(JSONObject object, Establishment establishment) throws JSONException{
        SlotBlueprint result;
        Long slotBlueprintId = object.getLong("id");
        HashSet<DayOfWeek> weekDays = getDaysOfWeek(object.getInt("weekdays"));
        Integer reservationLimit = object.optInt("reservationlimit", -1);
        if (reservationLimit < 0)
            reservationLimit = null;
        LocalDate fromDate = extractDate(object, "fromdate");
        LocalDate toDate = extractDate(object, "todate");

        JSONObject concreteBlueprint = object.optJSONObject("periodic_slot_blueprint");
        if(concreteBlueprint != null){
            Long concreteBlueprintId = concreteBlueprint.getLong("id");
            String fromTime = concreteBlueprint.getString("fromtime");
            String toTime = concreteBlueprint.getString("totime");
            result = new PeriodicSlotBlueprint(concreteBlueprintId, getTimeByString(fromTime), getTimeByString(toTime), slotBlueprintId, establishment, reservationLimit, weekDays, fromDate, toDate);
        } else {
            concreteBlueprint = object.getJSONObject("manual_slot_blueprint");
            Long concreteBlueprintId = concreteBlueprint.getLong("id");
            String openTime = concreteBlueprint.getString("opentime");
            String closeTime = concreteBlueprint.getString("closetime");
            int maxDuration = concreteBlueprint.getInt("maxduration");
            result = new ManualSlotBlueprint(concreteBlueprintId, getTimeByString(openTime), getTimeByString(closeTime), Duration.ofMinutes(maxDuration), slotBlueprintId, establishment, reservationLimit, weekDays, fromDate, toDate);
        }
        return result;
    }

    private static LocalDate extractDate(JSONObject jsonBlueprint, String key) {
        String strDate = jsonBlueprint.optString(key, "");
        if (strDate.isEmpty())
            return null;
        String[] arrDate = strDate.split("-");
        return LocalDate.of(Integer.valueOf(arrDate[0]), Integer.valueOf(arrDate[1]), Integer.valueOf(arrDate[2]));
    }

    private static LocalTime getTimeByString(String time){
        time = time.substring(11, 16);
        String[] res = time.split(":");
        return LocalTime.of(Integer.valueOf(res[0]), Integer.valueOf(res[1]));
    }

    private static HashSet<DayOfWeek> getDaysOfWeek(int weekDays){
        HashSet<DayOfWeek> map = new HashSet<>();
        for(int i = 0; i < 7; i++){
            if(((int)Math.pow(2, i) & weekDays) != 0)
                map.add(DayOfWeek.of(7 - i));
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
        String[] date = slot.getString("date").split("-");
        String password = slot.optString("password_digest", "");
        boolean passwordProtected = !password.isEmpty();

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
