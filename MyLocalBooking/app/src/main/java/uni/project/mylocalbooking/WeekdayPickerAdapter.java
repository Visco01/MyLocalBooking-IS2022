package uni.project.mylocalbooking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
                .inflate(R.layout.week, parent, false);
        return new ViewHolder(view, viewModel);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocalDate startOfWeek = viewModel.getMinStartOfWeek().getValue().plusWeeks(position);
        holder.setStartOfWeek(startOfWeek);
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final List<ConstraintLayout> days = new ArrayList<>();

        public ViewHolder(@NonNull View itemView, SlotListViewModel viewModel) {
            super(itemView);
            LinearLayout weekRoot = (LinearLayout) itemView.getRootView();

            for(int i = 1; i <= 7; i++) {
                ConstraintLayout dayView = (ConstraintLayout) LayoutInflater.from(weekRoot.getContext())
                        .inflate(R.layout.weekday, weekRoot, false);

                final DayOfWeek dow = DayOfWeek.of(i);
                String name = dow.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                ((TextView) dayView.findViewById(R.id.weekday_name)).setText(name);
                ((Button) dayView.findViewById(R.id.weekday_button)).setOnClickListener(view -> {
                    viewModel.setCurrentDay(dow);
                });
                days.add(dayView);
                weekRoot.addView(dayView);
            }
        }

        public void setStartOfWeek(LocalDate startOfWeek) {
            for(int i = 0; i < 7; i++) {
                int dayOfMonth = startOfWeek.plusDays(i).getDayOfMonth();
                TextView textView = ((TextView) days.get(i).findViewById(R.id.weekday_number));
                textView.setText(Integer.toString(dayOfMonth));
            }
        }
    }
}
