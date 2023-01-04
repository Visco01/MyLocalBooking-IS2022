package uni.project.mylocalbooking.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

class JSONBodyGenerator {

    public static String generateRegisterBody(AppUser user, String password){
        String jsonBody = "{" +
                "\"cellphone\": \"" + user.cellphone + "\", " +
                "\"firstname\": \"" + user.firstname + "\", " +
                "\"email\": \"" + user.email + "\", " +
                "\"lastname\": \"" + user.lastname + "\", " +
                "\"dob\": \"" + "2001-04-20" + "\", " +
                "\"password_digest\": \"" + password + "\", ";

        if(user instanceof Client){
            Client client = (Client) user;
            jsonBody += "\"Client\": {" +
                    "\"lat\": " + client.position.latitude + ", " +
                    "\"lng\": " + client.position.longitude +
                    "}}";
        } else {
            Provider provider = (Provider) user;
            jsonBody += "\"Provider\": {" +
                    "\"isverified\": " + (provider.verified ? 1 : 0) + ", " +
                    "\"maxstrikes\": " + provider.maxStrikes + ", " +
                    "\"companyname\": \"" + provider.companyName +
                    "\"}}";
        }

        return jsonBody;
    }

    public static String generateNewPasswordBody(String new_password){
        return "{\"new_password\": \"" + new_password + "\"}";
    }

    public static String generateAddBlueprintBody(SlotBlueprint blueprint){
        String jsonBody = "{" +
                "\"establishment_id\": " + blueprint.establishment.getId() + ", " +
                "\"weekdays\": " + blueprint.weekdays.size() + ", " +
                "\"reservationlimit\": " + blueprint.reservationLimit + ", " +
                "\"fromdate\": \"" + blueprint.fromDate.toString() + "\", " +
                "\"todate\": \"" + blueprint.toDate.toString() + "\", ";

        if(blueprint instanceof PeriodicSlotBlueprint){
            PeriodicSlotBlueprint pBlueprint = (PeriodicSlotBlueprint) blueprint;
            jsonBody += "\"PeriodicSlotBlueprint\": {" +
                    "\"fromtime\": \"" + pBlueprint.fromTime.getHour() + ":" + pBlueprint.fromTime.getMinute() + "\", " +
                    "\"totime\": \"" + pBlueprint.toTime.getHour() + ":" + pBlueprint.toTime.getMinute() +
                    "\"}}";
        } else {
            ManualSlotBlueprint mBlueprint = (ManualSlotBlueprint) blueprint;
            jsonBody += "\"ManualSlotBlueprint\": {" +
                    "\"opentime\": \"" + mBlueprint.openTime.toString() + "\", " +
                    "\"closetime\": \"" + mBlueprint.closeTime.toString() + "\"" +
                    "\"}}";
        }
        return jsonBody;
    }

    public static String generateAddSlotBody(Slot slot, String password){
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String jsonBody = "{" +
                "\"app_user_id\": " + slot.getOwner().getId() + ", " +
                "\"date\": \"" + date + "\", " +
                "\"password_digest\": ";
        jsonBody += password != null ? ("\"" + password + "\", ") : "null, ";

        if(slot instanceof PeriodicSlot){
            PeriodicSlotBlueprint psBlueprint = (PeriodicSlotBlueprint) slot.blueprint;
            jsonBody += "\"PeriodicSlot\": {" +
                    "\"periodic_slot_blueprint_id\": " + psBlueprint.getSubclassId() + "}}";
        } else {
            ManualSlot mSlot = (ManualSlot) slot;
            ManualSlotBlueprint msBlueprint = (ManualSlotBlueprint) slot.blueprint;
            jsonBody += "\"fromtime\": \"" + mSlot.fromTime.toString() + "\", " +
                    "\"totime\": \"" + mSlot.toTime.toString() + "\", ";
            jsonBody += "\"PeriodicSlot\": {" +
                    "\"periodic_slot_blueprint_id\": " + msBlueprint.getSubclassId() + "}}";
        }
        return jsonBody;
    }

    public static String generateReservationBody(Long clientId, Long slotId){
        return "{" +
                "\"client_id\": " + clientId + ", " +
                "\"slot_id\": " + slotId + "}";
    }

    public static String generateAddEstablishmentBody(Establishment establishment) {
        return "{" +
                "\"name\": \"" + establishment.name + "\", " +
                "\"provider_id\": " + establishment.provider.getSubclassId() + ", " +
                "\"lat\": " + establishment.position.latitude + ", " +
                "\"lng\": " + establishment.position.longitude + ", " +
                "\"place_id\": \"" + establishment.placeId + "\", " +
                "\"address\": \"" + establishment.address + "\"" + "}";
    }

    public static String getSetPreferredPositionBody(Coordinates position) {
        return "{" +
                "\"lat\": " + position.latitude + ", " +
                "\"lng\": " + position.longitude + "}";
    }

    public static String generateStrikeUserBody(Long providerId, String cellphone) {
        return "{" +
                "\"provider_id\": " + providerId + ", " +
                "\"usercellphone\": \"" + cellphone + "\"}";
    }

    public static String generateUnbanUserBody(Long providerId, String cellphone) {
        return "{" +
                "\"provider_id\": " + providerId + ", " +
                "\"usercellphone\": \"" + cellphone + "\"}";
    }

    public static String generateRatingBody(Long clientId, Long establishment_id, float rating, String comment) {
        return "{" +
                "\"client_id\": " + clientId + ", " +
                "\"establishment_id\": " +  establishment_id + ", " +
                "\"rating\": " + rating + ", " +
                "\"comment\": " + comment + "}";
    }
}
