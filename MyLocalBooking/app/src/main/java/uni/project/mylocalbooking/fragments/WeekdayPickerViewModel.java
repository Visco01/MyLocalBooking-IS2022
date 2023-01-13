package uni.project.mylocalbooking.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;

public class WeekdayPickerViewModel extends ViewModel {
    private final MutableLiveData<HashSet<DayOfWeek>> selectedDaysOfWeek = new MutableLiveData<>();
    private final MutableLiveData<DayOfWeek> selectedDayOfWeek = new MutableLiveData<>();
    private final MutableLiveData<LocalDate> startOfWeek = new MutableLiveData<>();
    private final MutableLiveData<DayOfWeek> currentDayOfWeek = new MutableLiveData<>();
    private final MutableLiveData<LocalDate> currentDay = new MutableLiveData<>();

    public LiveData<HashSet<DayOfWeek>> getSelectedDaysOfWeek() {
        return selectedDaysOfWeek;
    }

    void toggleSelectedDayOfWeek(DayOfWeek dow) {
        HashSet<DayOfWeek> daysOfWeek = selectedDaysOfWeek.getValue();
        if(daysOfWeek.contains(dow))
            daysOfWeek.remove(dow);
        else
            daysOfWeek.add(dow);

        selectedDaysOfWeek.setValue(selectedDaysOfWeek.getValue());
    }

    void setSelectedDayOfWeek(DayOfWeek dow) {
        selectedDayOfWeek.setValue(dow);
    }

    public LiveData<LocalDate> getStartOfWeek() {
        return startOfWeek;
    }

    public void setStartOfWeek(LocalDate date) {
        startOfWeek.setValue(date);
    }

    public LiveData<LocalDate> getSelectedDate() {
        return selectedDate;
    }
}
