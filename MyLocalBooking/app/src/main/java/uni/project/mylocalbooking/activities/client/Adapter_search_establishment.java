package uni.project.mylocalbooking.activities.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.models.Establishment;

public class Adapter_search_establishment extends RecyclerView.Adapter<Adapter_search_establishment.ViewHolder> {
    public interface IEstablishmentSelectedListener {
        void onEstablishmentSelected(Establishment establishment);
    }

    private final List<Establishment> establishments;
    private final IEstablishmentSelectedListener listener;

    public Adapter_search_establishment(List<Establishment> establishments, IEstablishmentSelectedListener listener) {
        this.establishments = establishments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Adapter_search_establishment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.searchestablishment_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_search_establishment.ViewHolder holder, int position) {

        int resource = R.drawable.logo;

        holder.setData(resource, establishments.get(position));
    }

    @Override
    public int getItemCount() {
        return establishments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textViewTittle;
        private TextView textViewLocation;
        private Establishment establishment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.findViewById(R.id.goPrenotationEst).setOnClickListener(view -> {
                listener.onEstablishmentSelected(establishment);
            });

            imageView = itemView.findViewById(R.id.imageView1_se);
            textViewTittle = itemView.findViewById(R.id.tittle_se);
            textViewLocation = itemView.findViewById(R.id.location_se);

        }

        public void setData(int resource, Establishment establishment) {
            this.establishment = establishment;
            imageView.setImageResource(resource);
            textViewTittle.setText(establishment.name);
            textViewLocation.setText(establishment.address);
        }
    }
}
