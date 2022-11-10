package uni.project.mylocalbooking.api;

import com.android.volley.Request;
import java.util.Collection;
import uni.project.mylocalbooking.IMyLocalBookingAPI;
import uni.project.mylocalbooking.models.AppUser;
import uni.project.mylocalbooking.models.Client;
import uni.project.mylocalbooking.models.Coordinates;
import uni.project.mylocalbooking.models.Establishment;
import uni.project.mylocalbooking.models.Slot;
import uni.project.mylocalbooking.models.SlotBlueprint;

public class MyLocalBookingAPI implements IMyLocalBookingAPI {
    private Request loginRequest = null;

    public MyLocalBookingAPI() {
        setLoginRequest();
    }

    private void setLoginRequest(){
        this.loginRequest = LoginAPI.getLoginRequest();
    }

    public Request getLoginRequest(){
        return this.loginRequest;
    }

    @Override
    public void register(AppUser user, String password) {

    }

    @Override
    public void changeUserPassword(String password) {

    }

    @Override
    public Slot setSlotPassword(String password, Slot slot) {
        return null;
    }

    @Override
    public long addBlueprint(SlotBlueprint blueprint) {
        return 0;
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
    public void addReservation(Slot slot, String password) {

    }

    @Override
    public void cancelReservation(Slot slot) {

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
