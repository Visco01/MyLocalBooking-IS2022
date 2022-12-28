package uni.project.mylocalbooking.activities.provider;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;

public class BlacklistAdapter extends RecyclerView.Adapter<BlacklistAdapter.ViewHolder> {

    private List<Model> userList;

    public BlacklistAdapter(List<Model> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public BlacklistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.black_list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlacklistAdapter.ViewHolder holder, int position) {
        int resources = userList.get(position).getImagePeople();
        String name = userList.get(position).getBlackList_name();
        String email = userList.get(position).getBlackList_email();
        holder.setData(resources, name, email);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView1;
        private TextView textView2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imagePeople);
            textView1 = itemView.findViewById(R.id.blackList_name);
            textView2 = itemView.findViewById(R.id.balckList_email);

            FloatingActionButton unButton = itemView.findViewById(R.id.unblacklistButton);
            unButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: implement
                    MyLocalBooking.getAppContext().startActivity(new Intent(MyLocalBooking.getAppContext(), ProfileProviderActivity.class).setFlags(FLAG_ACTIVITY_NEW_TASK));
                }
            });

        }

        public void setData(int resources, String name, String email) {
            imageView.setImageResource(resources);
            textView1.setText(name);
            textView2.setText(email);
        }
    }
}
