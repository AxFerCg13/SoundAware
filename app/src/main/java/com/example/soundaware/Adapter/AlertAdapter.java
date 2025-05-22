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

import java.util.ArrayList;
import java.util.List;

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.ViewHolder> {

    private List<Alert> alertList;
    private Context context;

    public AlertAdapter(List<Alert> alertList, Context context) {
        this.alertList = alertList != null ? alertList : new ArrayList<>();
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
        Alert alert = alertList.get(position);

        // Cargar imagen
        if (alert.getIconPath() != null && !alert.getIconPath().isEmpty()) {
            try {
                int drawableId = context.getResources().getIdentifier(
                        alert.getIconPath(),
                        "drawable",
                        context.getPackageName());

                if (drawableId != 0) {
                    Glide.with(context)
                            .load(drawableId)
                            .into(holder.cardIcon);
                } else {
                    Glide.with(context)
                            .load(alert.getIconPath())
                            .into(holder.cardIcon);
                }
            } catch (Exception e) {
            }
        }
        holder.alertDate.setText(alert.getDate() != null ? alert.getDate() : "");
        holder.alertClassification.setText(alert.getClassfication() != null ? alert.getDescription() : "");
        holder.alertPriority.setText(alert.getPriority());
        holder.alertDescription.setText(alert.getDescription() != null ? alert.getDescription() : "");


    }

    @Override
    public int getItemCount() {
        return alertList.size();
    }

    public void updateData(List<Alert> newAlerts) {
        this.alertList = newAlerts != null ? newAlerts : new ArrayList<>();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView cardIcon;
        private TextView alertDate;
        private TextView alertDescription;

        private TextView alertClassification;

        private TextView alertPriority;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardIcon = itemView.findViewById(R.id.card_icon);
            alertDate = itemView.findViewById(R.id.alert_date);
            alertDescription = itemView.findViewById(R.id.alert_description);
            alertClassification = itemView.findViewById(R.id.alert_type_value);
            alertPriority = itemView.findViewById(R.id.alert_level_value);
        }
    }
}
