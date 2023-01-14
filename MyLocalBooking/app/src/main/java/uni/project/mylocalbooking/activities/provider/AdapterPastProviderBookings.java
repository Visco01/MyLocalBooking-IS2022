package uni.project.mylocalbooking.activities.provider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.models.Slot;
import uni.project.mylocalbooking.models.SlotBlueprint;

public class AdapterPastProviderBookings extends RecyclerView.Adapter<AdapterPastProviderBookings.ViewHolder5> {

    private final RVInterface rvInterface;
    private List<Slot> slots;

    public AdapterPastProviderBookings(RVInterface rvInterface, ArrayList<Slot> slots) {
        this.rvInterface = rvInterface;
        this.slots = slots;
    }

    @NonNull
    @Override
    public ViewHolder5 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.past_provider_bookings_row, parent, false);
        return new ViewHolder5(view, rvInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder5 holder, int position) {
        holder.setData();
    }

    @Override
    public int getItemCount() {
        return slots.size();
    }


    public static class ViewHolder5 extends RecyclerView.ViewHolder {
        private TextView titleView;
        private TextView whoBookedView;
        private TextView dateView;


        public ViewHolder5(@NonNull View itemView, RVInterface rvInterface) {
            super(itemView);
            titleView = itemView.findViewById(R.id.titlePastProviderBookings);
            whoBookedView = itemView.findViewById(R.id.whoBookProviderBookings);
            dateView = itemView.findViewById(R.id.DateProviderBookings);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (rvInterface != null) {
                        int pos = getAbsoluteAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            rvInterface.onItemClick(pos);
                        }

                    }

                }
            });

        }

        public void setData() {
            titleView.setText();
            whoBookedView.setText();
            dateView.setText();
        }



    }

}
