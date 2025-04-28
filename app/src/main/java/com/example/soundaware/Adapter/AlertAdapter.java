package com.example.soundaware.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.soundaware.R;

import java.util.List;

public class AlertAdapter extends RecyclerView .Adapter<AlertAdapter.ViewHolder>{

    private List<Alert> alertList;
    private Context context;

    public AlertAdapter(List<Alert> alertList, Context context) {
        this.alertList = alertList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.alertDate.setText(alertList.get(position).getDate());
        holder.alertDescription.setText(alertList.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return alertList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView cardIcon;
        private TextView alertDate;
        private TextView alertDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardIcon = itemView.findViewById(R.id.card_icon);
            alertDate = itemView.findViewById(R.id.alert_date);
            alertDescription = itemView.findViewById(R.id.alert_description);
        }
    }
}
