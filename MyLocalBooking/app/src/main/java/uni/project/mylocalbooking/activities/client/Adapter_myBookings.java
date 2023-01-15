package uni.project.mylocalbooking.activities.client;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.models.Establishment;
import uni.project.mylocalbooking.models.Slot;

public class Adapter_myBookings extends RecyclerView.Adapter<Adapter_myBookings.ViewHolder2>{

    MutableLiveData<List<Slot>> userBookings;
    List<Establishment> userEstablishmentBooked;

    public Adapter_myBookings(MutableLiveData<List<Slot>> userBookings, List<Establishment> userEstablishmentBooked) {
        this.userBookings = userBookings;
        this.userEstablishmentBooked = userEstablishmentBooked;
    }

    @NonNull
    @Override
    public Adapter_myBookings.ViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mybookings_row, parent, false);
        return new ViewHolder2(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_myBookings.ViewHolder2 holder, int position) {
        int resource = R.drawable.logo;
        holder.setData(resource, userBookings.getValue().get(position), userEstablishmentBooked.get(position));
    }

    @Override
    public int getItemCount() {
        return userBookings.getValue().size();
    }



    public class ViewHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView imageView;
        private TextView textViewTittle;
        private TextView textViewLocation;
        private TextView textViewHour;

        public ViewHolder2(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.my_bookings_image);
            textViewTittle = itemView.findViewById(R.id.name_location_myBookings);
            textViewLocation = itemView.findViewById(R.id.position_myBookings);
            textViewHour = itemView.findViewById(R.id.hour_myBookings);

            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        public void setData(int resource, Slot slot, Establishment establishment) {
            imageView.setImageResource(resource);
            textViewTittle.setText(establishment.name);
            textViewLocation.setText(establishment.address);
            textViewHour.setText(slot.blueprint.fromDate.toString() + " " + slot.blueprint.toDate.toString());
        }

        @Override
        public void onClick(View view) {
            final Context context = MyLocalBooking.getAppContext();
            Intent intent = new Intent(context, ReservationDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

    }

}
