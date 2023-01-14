package uni.project.mylocalbooking.activities.provider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.models.Establishment;

public class Adapter_myEstablishment extends RecyclerView.Adapter<Adapter_myEstablishment.ViewHolder3> {

    public interface EstablishmentSelected {
        void onEstablishmentSelected(Establishment establishment);
    }


    private List<Establishment> providerEstablishments;
    private EstablishmentSelected establishmentSelected;

    public Adapter_myEstablishment(List<Establishment> providerEstablishment, EstablishmentSelected establishmentSelected) {
        this.establishmentSelected = establishmentSelected;
        this.providerEstablishments = providerEstablishment;
    }

    @NonNull
    @Override
    public Adapter_myEstablishment.ViewHolder3 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_establishment_row, parent, false);
        return new ViewHolder3(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_myEstablishment.ViewHolder3 holder, int position) {

        int resource = R.drawable.logo;
        holder.setData(resource, providerEstablishments.get(position));
    }

    @Override
    public int getItemCount() {
        return providerEstablishments.size();
    }

    public class ViewHolder3 extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textViewTitle;
        private TextView textViewLocation;
        private Establishment establishment;

        public ViewHolder3(@NonNull View itemView) {
            super(itemView);

            itemView.findViewById(R.id.myEstablishmentOldPrenotation).setOnClickListener(view -> {
                establishmentSelected.onEstablishmentSelected(establishment);
            });

            imageView = itemView.findViewById(R.id.myEstablishment_imageview1);
            textViewTitle = itemView.findViewById(R.id.myEstablishment_textView1);
            textViewLocation = itemView.findViewById(R.id.myEstablishment_textView2);

        }

        public void setData(int resource, Establishment establishment) {
            this.establishment = establishment;
            imageView.setImageResource(resource);
            textViewTitle.setText(establishment.name);
            textViewLocation.setText(establishment.address);
        }
    }

}
