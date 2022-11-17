package uni.project.mylocalbooking.api;

import uni.project.mylocalbooking.models.AppUser;
import uni.project.mylocalbooking.models.Client;
import uni.project.mylocalbooking.models.ManualSlotBlueprint;
import uni.project.mylocalbooking.models.PeriodicSlotBlueprint;
import uni.project.mylocalbooking.models.Provider;
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
}
