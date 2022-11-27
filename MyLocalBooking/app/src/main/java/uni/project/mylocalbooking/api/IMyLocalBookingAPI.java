package uni.project.mylocalbooking.api;

import java.util.Collection;

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

    void login(String cellphone, String password, APICallBack<AppUser> onSuccess, APICallBack<StatusCode> onError);
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
    Establishment addEstablishment(Establishment establishment);
    Collection<Establishment> GetOwnedEstablishments();

    /*
     * pre:
     *      client.id != null
     * post:
     *       blacklists the user in the db
     * */
    void banUser(Client client);
    /*
     * pre:
     *      client.id != null
     * post:
     *       removes the user from the blacklist in the db
     * */
    void unbanUser(Client client);
    /*
     * pre:
     *      client.id != null
     * post:
     *       strikes the user in the db
     * */
    void strikeUser(Client client);
    /*
     * post:
     *       sets the CURRENT_USER.maxstrikes in the db to max
     * */
    void setMaxStrikes(int max);

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
    Collection<Establishment> getClosestEstablishments();
    void setPreferredPosition(Coordinates position);
    void rateEstablishment(Establishment establishment, int rating, String comment/*, boolean anonymous*/);



    // left to implement:

    // void deleteBlueprint(SlotBlueprint blueprint);
    // void editBlueprint(SlotBlueprint blueprint);
    // void removeEstablishment(Establishment establishment);
    // edit and delete rating?
}
