package uni.project.mylocalbooking.activites.client;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import uni.project.mylocalbooking.models.SlotBlueprint;

public class SlotListViewModel extends ViewModel {
    public final MutableLiveData<LocalDate> minStartOfWeek = new MutableLiveData<>();
    private final MutableLiveData<LocalDate> startOfWeek = new MutableLiveData<>();
    private final MutableLiveData<DayOfWeek> currentDay = new MutableLiveData<>();
    private final MutableLiveData<List<SlotBlueprint>> blueprints = new MutableLiveData<>();

    public void setBlueprints(List<SlotBlueprint> blueprints) {
        this.blueprints.setValue(blueprints);
    }

    public List<SlotBlueprint> getBlueprints(LocalDate date) {
        return blueprints.getValue().stream().filter(b -> b.fromDate.compareTo(date) <= 0 && b.toDate.compareTo(date) > 0).collect(Collectors.toList());
    }

    public LiveData<LocalDate> getStartOfWeek() {
        return startOfWeek;
    }

    public LiveData<LocalDate> getMinStartOfWeek() {
        return minStartOfWeek;
    }

    public void setStartOfWeek(LocalDate date) {
        startOfWeek.setValue(date);
    }

    public LiveData<DayOfWeek> getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(DayOfWeek dow) {
        currentDay.setValue(dow);
    }
}
