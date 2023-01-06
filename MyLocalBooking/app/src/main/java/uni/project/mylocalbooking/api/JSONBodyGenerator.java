package uni.project.mylocalbooking.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import uni.project.mylocalbooking.MyLocalBooking;
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
        JSONObject body = new JSONObject();
        try {
            body.put("cellphone", user.cellphone);
            body.put("firstname", user.firstname);
            body.put("email", user.email);
            body.put("lastname", user.lastname);
            body.put("dob", user.dob.toString());
            body.put("password_digest", password);

            JSONObject concreteBlueprint = new JSONObject();
            if(user instanceof Client) {
                Client client = (Client) user;
                if(client.position != null) {
                    concreteBlueprint.put("lat", client.position.latitude);
                    concreteBlueprint.put("lng", client.position.longitude);
                } else {
                    concreteBlueprint.put("lat", null);
                    concreteBlueprint.put("lng", null);
                }
                body.put("Client", concreteBlueprint);
            } else {
                Provider provider = (Provider) user;
                concreteBlueprint.put("isverified", provider.verified ? 1 : 0);
                concreteBlueprint.put("maxstrikes", provider.maxStrikes);
                concreteBlueprint.put("companyname", provider.companyName);
                body.put("Provider", concreteBlueprint);
            }

            return body.toString(4);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String generateNewPasswordBody(String new_password){
        JSONObject body = new JSONObject();
        try {
            body.put("new_password", new_password);
            return body.toString(4);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String generateAddBlueprintBody(SlotBlueprint blueprint){
        JSONObject body = new JSONObject();
        int weekdays = 0;
        try {
            body.put("establishment_id", blueprint.establishment.getId());
            body.put("weekdays", blueprint.getDaysOfWeekAsInt());
            body.put("reservationlimit", blueprint.reservationLimit);
            body.put("fromdate", blueprint.fromDate.toString());
            body.put("todate", blueprint.toDate.toString());

            JSONObject concreteBlueprint = new JSONObject();
            if(blueprint instanceof PeriodicSlotBlueprint) {
                PeriodicSlotBlueprint pBlueprint = (PeriodicSlotBlueprint) blueprint;
                concreteBlueprint.put("fromtime", pBlueprint.fromTime.toString());
                concreteBlueprint.put("totime", pBlueprint.toTime.toString());
                body.put("PeriodicSlotBlueprint", concreteBlueprint);
            } else {
                ManualSlotBlueprint mBlueprint = (ManualSlotBlueprint) blueprint;
                concreteBlueprint.put("fromtime", mBlueprint.openTime.toString());
                concreteBlueprint.put("totime", mBlueprint.closeTime.toString());
                body.put("ManualSlotBlueprint", concreteBlueprint);
            }

            return body.toString(4);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String generateAddSlotBody(Slot slot, String password){
        try {
            JSONObject body = new JSONObject();
            body.put("owner_cellphone", slot.getOwnerCellphone());
            body.put("date", slot.date.toString());
            body.put("password_digest", password);
            body.put("client_id", slot instanceof PeriodicSlot ? ((Client) MyLocalBooking.getCurrentUser()).getSubclassId() : null);
            JSONObject concreteSlot = new JSONObject();
            if(slot instanceof PeriodicSlot) {
                concreteSlot.put("periodic_slot_blueprint_id", ((PeriodicSlotBlueprint) slot.blueprint).getSubclassId());
                body.put("PeriodicSlot", concreteSlot);
            } else {
                ManualSlot mSlot = (ManualSlot) slot;
                body.put("fromtime", mSlot.fromTime.toString());
                body.put("totime", mSlot.toTime.toString());

                concreteSlot.put("manual_slot_blueprint_id", ((ManualSlotBlueprint) slot.blueprint).getSubclassId());
                body.put("ManualSlot", concreteSlot);
            }

            return body.toString(4);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String generateReservationBody(Long clientId, Long slotId){
        JSONObject body = new JSONObject();
        try {
            body.put("client_id", clientId);
            body.put("slot_id", slotId);

            return body.toString(4);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String generateAddEstablishmentBody(Establishment establishment) {
        JSONObject body = new JSONObject();
        try {
            body.put("name", establishment.name);
            body.put("provider_id", establishment.getProvider().getSubclassId()); //TODO: use cellphone instead
            body.put("lat", establishment.position.latitude);
            body.put("lng", establishment.position.longitude);
            body.put("place_id", establishment.placeId);
            body.put("address", establishment.address);

            return body.toString(4);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getSetPreferredPositionBody(Coordinates position) {
        JSONObject body = new JSONObject();
        try {
            body.put("lat", position.latitude);
            body.put("lng", position.longitude);

            return body.toString(4);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String generateStrikeUserBody(Long providerId, String cellphone) {
        JSONObject body = new JSONObject();
        try {
            body.put("provider_id", providerId);
            body.put("usercellphone", cellphone);

            return body.toString(4);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String generateUnbanUserBody(Long providerId, String cellphone) {
        JSONObject body = new JSONObject();
        try {
            body.put("provider_id", providerId);
            body.put("usercellphone", cellphone);

            return body.toString(4);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String generateRatingBody(Long clientId, Long establishment_id, float rating, String comment) {
        JSONObject body = new JSONObject();
        try {
            body.put("client_id", clientId);
            body.put("establishment_id", establishment_id);
            body.put("rating", rating);
            body.put("comment", comment);

            return body.toString(4);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
