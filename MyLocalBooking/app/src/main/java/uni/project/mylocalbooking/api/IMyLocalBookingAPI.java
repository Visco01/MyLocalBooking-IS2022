package uni.project.mylocalbooking.api;

import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import uni.project.mylocalbooking.models.AppUser;
import uni.project.mylocalbooking.models.Client;
import uni.project.mylocalbooking.models.Coordinates;
import uni.project.mylocalbooking.models.Establishment;
import uni.project.mylocalbooking.models.Provider;
import uni.project.mylocalbooking.models.Slot;
import uni.project.mylocalbooking.models.SlotBlueprint;

public interface IMyLocalBookingAPI{

    static IMyLocalBookingAPI getApiInstance(){
        return new MyLocalBookingAPI();
    }

    // GENERIC
    /*
     * pre:
     *       user.id == null
     *       user.cellphone not already registered in the db
     * post:
     *       registers the user in the db with password_digest as the encrypted password
     *       sets the CURRENT_USER to the inserted user
     * */
    void register(AppUser user, String password, APICallBack<AppUser> onSuccess, APICallBack<StatusCode> onError);

    void login(String cellphone, String password, MutableLiveData<AppUser> loginOutcome);
    /*
     * pre:
     *       CURRENT_USER.id != null
     * post:
     *       updates the app_user's password_digest to the encrypted password
     * */
    void changeUserPassword(String password, APICallBack<Void> onSuccess, APICallBack<StatusCode> onError);



    // all the following calls implicitly have the preconditions:
    //      CURRENT_USER != null
    //      CURRENT_USER.id != null


    /*
    * pre:
    *       slot.id != null
    *       slot.owner == CURRENT_USER
    * post:
    *       updates the slot's password_digest
    *       sets slot.passwordProtected to true
    * */
    void setSlotPassword(String password, Slot slot, APICallBack<Slot> onSuccess, APICallBack<StatusCode> onError);

    // PROVIDER
    // all calls in this section implicitly have the precondition
    //      CURRENT_USER instanceof Provider

    /*
     * pre:
     *      blueprint.id == null
     * post:
     *       updates the slot's password_digest
     *       sets slot.passwordProtected to true
     * */
    void addBlueprint(SlotBlueprint blueprint, APICallBack<SlotBlueprint> onSuccess, APICallBack<StatusCode> onError);

    /*
     * pre:
     *      establishment.id == null
     * post:
     *       inserts the establishment in the db
     *       returns inserted establishment
     * */
    void addEstablishment(Establishment establishment, APICallBack<Establishment> onSuccess, APICallBack<StatusCode> onError);
    void getOwnedEstablishments(APICallBack<Collection<Establishment>> onSuccess, APICallBack<StatusCode> onError);

    /*
     * pre:
     *      client.id != null
     * post:
     *       blacklists the user in the db
     * */
    void banUser(Client client, APICallBack<StatusCode> onSuccess, APICallBack<StatusCode> onError);
    /*
     * pre:
     *      client.id != null
     * post:
     *       removes the user from the blacklist in the db
     * */
    void unbanUser(Client client, APICallBack<StatusCode> onSuccess, APICallBack<StatusCode> onError);
    /*
     * pre:
     *      client.id != null
     * post:
     *       strikes the user in the db
     * */
    void strikeUser(Client client, APICallBack<StatusCode> onSuccess, APICallBack<StatusCode> onError);
    /*
     * post:
     *       sets the CURRENT_USER.maxstrikes in the db to max
     * */
    void setMaxStrikes(int max, APICallBack<StatusCode> onSuccess, APICallBack<StatusCode> onError);

    // CLIENT
    // all calls in this section implicitly have the precondition
    //      CURRENT_USER instanceof Client
    /*
    *   pre:
    *       slot.id != null
    *       password is a valid password or null
    *   post:
    *       CASES:
    *           1) the slot id is null:
    *               1.1) CURRENT_USER.id == slot.owner.id
    *                   insert slot in the db, with password_digest as the provided password
    *               1.2) otherwise
    *                   insert slot in the db, ignore password argument
    *           2) the slot id is NOT null:
    *               1.1) !slot.passwordProtected
    *                   ignore password argument
    *                   insert reservation in the db
    *               1.2) slot.passwordProtected and valid password and password matches
    *                   insert reservation in the db
    */
    void addReservation(Slot slot, String password, APICallBack<Slot> onSuccess, APICallBack<StatusCode> onError);
    /*
     *   post:
     *       removes the reservation if exists
     */
    void cancelReservation(Slot slot, APICallBack<Slot> onSuccess, APICallBack<StatusCode> onError);

    void getClosestEstablishments(APICallBack<Collection<Establishment>> onSuccess, APICallBack<StatusCode> onError);

    void setPreferredPosition(Coordinates position, APICallBack<Void> onSuccess, APICallBack<StatusCode> onError);

    void rateEstablishment(Establishment establishment, float rating, String comment, APICallBack<StatusCode> onSuccess, APICallBack<StatusCode> onError);

    boolean getReservations(Establishment establishment, LocalDate date);

    void getClientReservations(Collection<Establishment> establishments, Long clientId, MutableLiveData<List<Slot>> slotsLivedata);

    AppUser getUserByCellphone(String cellphone);

        // left to implement:

    // void deleteBlueprint(SlotBlueprint blueprint);
    // void editBlueprint(SlotBlueprint blueprint);
    // void removeEstablishment(Establishment establishment);
    // edit and delete rating?
}
