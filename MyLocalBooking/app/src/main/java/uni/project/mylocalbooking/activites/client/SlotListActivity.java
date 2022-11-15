package uni.project.mylocalbooking.activites.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.ListView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.fragments.ProvideSlotPasswordDialogFragment;
import uni.project.mylocalbooking.fragments.SetSlotPasswordDialogFragment;
import uni.project.mylocalbooking.models.Client;
import uni.project.mylocalbooking.models.Coordinates;
import uni.project.mylocalbooking.models.Establishment;
import uni.project.mylocalbooking.models.ISelectableSlot;
import uni.project.mylocalbooking.models.PeriodicSlot;
import uni.project.mylocalbooking.models.PeriodicSlotBlueprint;
import uni.project.mylocalbooking.models.Provider;
import uni.project.mylocalbooking.models.SlotBlueprint;

public class SlotListActivity extends AppCompatActivity implements SlotListAdapter.IListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSlotReservationToggled(ISelectableSlot selectableSlot) {

    }
}