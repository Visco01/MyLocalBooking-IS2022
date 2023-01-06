package uni.project.mylocalbooking.activities.client;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.api.IMyLocalBookingAPI;
import uni.project.mylocalbooking.api.StatusCode;
import uni.project.mylocalbooking.models.Establishment;
import uni.project.mylocalbooking.models.ISelectableSlot;
import uni.project.mylocalbooking.models.ManualSlot;
import uni.project.mylocalbooking.models.ManualSlotBlueprint;
import uni.project.mylocalbooking.models.PeriodicSlot;
import uni.project.mylocalbooking.models.PeriodicSlotBlueprint;
import uni.project.mylocalbooking.models.Slot;
import uni.project.mylocalbooking.models.SlotBlueprint;

public class SlotListViewModel extends ViewModel {
    private final MutableLiveData<LocalDate> startOfWeek = new MutableLiveData<>();
    private final MutableLiveData<DayOfWeek> currentDayOfWeek = new MutableLiveData<>();
    private final MutableLiveData<LocalDate> currentDay = new MutableLiveData<>();
    private final MutableLiveData<StatusCode> reservationOutcome = new MutableLiveData<>();

    public LiveData<LocalDate> getStartOfWeek() {
        return startOfWeek;
    }

    public void setStartOfWeek(LocalDate date) {
        startOfWeek.setValue(date);
    }

    public void setCurrentDayOfWeek(DayOfWeek dow) {
        currentDayOfWeek.setValue(dow);
        LocalDate weekStart = getStartOfWeek().getValue();
        LocalDate date = weekStart.plusDays(currentDayOfWeek.getValue().getValue() - 1);
        currentDay.setValue(date);
    }

    public LiveData<LocalDate> getCurrentDay() {
        return currentDay;
    }

    public void makeReservation(ManualSlotBlueprint blueprint, LocalTime fromTime, LocalTime toTime, String password) {
        makeReservation((Slot) new ManualSlot(fromTime, toTime, currentDay.getValue(), null, blueprint), password);
    }

    public LiveData<StatusCode> getReservationOutcome() {
        return reservationOutcome;
    }

    public void makeReservation(ISelectableSlot selectable, String password) {
        if(selectable instanceof Slot)
            makeReservation((Slot) selectable, password);
        else
            makeReservation((PeriodicSlotBlueprint) selectable, password);
    }

    private void makeReservation(Slot slot, String password) {
        if(slot instanceof PeriodicSlot)
            slot.setOwner(slot.blueprint.establishment.getProviderCellphone());
        else
            slot.setOwner(MyLocalBooking.getCurrentUser());

        IMyLocalBookingAPI.getApiInstance().addReservation(slot, password, s -> {
                    currentDay.setValue(currentDay.getValue());
                    reservationOutcome.setValue(null);
                },
                code -> {
                    slot.blueprint.slots.remove(slot);
                    reservationOutcome.setValue(code);
                });
    }

    private void makeReservation(PeriodicSlotBlueprint blueprint, String password) {
        makeReservation((Slot) new PeriodicSlot(currentDay.getValue(), blueprint.establishment.getProviderCellphone(), blueprint), password);
    }
}