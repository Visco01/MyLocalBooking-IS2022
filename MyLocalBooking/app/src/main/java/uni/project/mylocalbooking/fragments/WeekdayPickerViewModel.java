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
    private final MutableLiveData<LocalDate> selectedDate = new MutableLiveData<>();

    public LiveData<HashSet<DayOfWeek>> getSelectedDaysOfWeek() {
        return selectedDaysOfWeek;
    }

    public WeekdayPickerViewModel() {
        selectedDaysOfWeek.setValue(new HashSet<>());
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
        LocalDate weekStart = getStartOfWeek().getValue();

        if(weekStart != null) {
            LocalDate date = weekStart.plusDays(selectedDayOfWeek.getValue().getValue() - 1);
            selectedDate.setValue(date);
        }
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
