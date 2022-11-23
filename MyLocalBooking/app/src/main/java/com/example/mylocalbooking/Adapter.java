package com.example.mylocalbooking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private List<Model> userList;

    public Adapter(List<Model> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.black_list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
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

        }

        public void setData(int resources, String name, String email) {
            imageView.setImageResource(resources);
            textView1.setText(name);
            textView2.setText(email);
        }
    }
}
