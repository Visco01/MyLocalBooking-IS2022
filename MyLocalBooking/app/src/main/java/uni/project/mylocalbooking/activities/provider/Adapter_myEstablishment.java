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

    private final RVInterface rvInterface;
    List<Establishment> providerEstablishment;

    public Adapter_myEstablishment(List<Establishment> providerEstablishment, RVInterface rvInterface) {
        this.rvInterface = rvInterface;
        this.providerEstablishment = providerEstablishment;
    }

    @NonNull
    @Override
    public Adapter_myEstablishment.ViewHolder3 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_establishment_row, parent, false);
        return new ViewHolder3(view, rvInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_myEstablishment.ViewHolder3 holder, int position) {

        int resource = R.drawable.logo;
        holder.setData(resource, providerEstablishment.get(position));
    }

    @Override
    public int getItemCount() {
        return providerEstablishment.size();
    }

    public static class ViewHolder3 extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textViewTitle;
        private TextView textViewLocation;
        private Establishment establishment;

        public ViewHolder3(@NonNull View itemView, RVInterface rvInterface) {
            super(itemView);
            imageView = itemView.findViewById(R.id.myEstablishment_imageview1);
            textViewTitle = itemView.findViewById(R.id.myEstablishment_textView1);
            textViewLocation = itemView.findViewById(R.id.myEstablishment_textView2);

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

        public void setData(int resource, Establishment establishment) {
            this.establishment = establishment;
            imageView.setImageResource(resource);
            textViewTitle.setText(establishment.name);
            textViewLocation.setText(establishment.address);
        }
    }

}
