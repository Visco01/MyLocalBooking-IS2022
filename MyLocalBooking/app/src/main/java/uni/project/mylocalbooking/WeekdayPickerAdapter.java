package uni.project.mylocalbooking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WeekdayPickerAdapter extends RecyclerView.Adapter<WeekdayPickerAdapter.ViewHolder> {
    protected final SlotListViewModel viewModel;

    public WeekdayPickerAdapter(SlotListViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_weekday_picker, parent, false);
        return new ViewHolder(view, viewModel);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocalDate startOfWeek = viewModel.getStartOfWeek().getValue().plusWeeks(position);
        viewModel.setStartOfWeek(startOfWeek);

        for(int i = 0; i < 7; i++) {
            int dayOfMonth = startOfWeek.plusDays(i).getDayOfMonth();
            TextView textView = ((TextView) holder.days.get(i).findViewById(R.id.weekday_number));
            textView.setText(Integer.toString(dayOfMonth));
        }
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout pickerRoot;
        public final List<ConstraintLayout> days = new ArrayList<>();

        public ViewHolder(@NonNull View itemView, SlotListViewModel viewModel) {
            super(itemView);
            this.pickerRoot = (LinearLayout) itemView.getRootView();

            for(int i = 1; i <= 7; i++) {
                ConstraintLayout childView = (ConstraintLayout) LayoutInflater.from(pickerRoot.getContext())
                        .inflate(R.layout.fragment_weekday, pickerRoot, false);

                final DayOfWeek dow = DayOfWeek.of(i);
                String name = dow.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                ((TextView) childView.findViewById(R.id.weekday_name)).setText(name);
                ((Button) childView.findViewById(R.id.weekday_button)).setOnClickListener(view -> {
                    viewModel.setCurrentDay(dow);
                });
                days.add(childView);
                pickerRoot.addView(childView);
            }
        }
    }
}
