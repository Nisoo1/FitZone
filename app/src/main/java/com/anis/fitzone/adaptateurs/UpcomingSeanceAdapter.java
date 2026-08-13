package com.anis.fitzone.adaptateurs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anis.fitzone.databinding.ItemUpcomingSeanceBinding;

import java.util.List;

public class UpcomingSeanceAdapter extends RecyclerView.Adapter<UpcomingSeanceAdapter.ViewHolder> {

    public static class Item {
        public final String seanceTitle;
        public final String programTitle;
        public final String dueDateDisplay;

        public Item(String seanceTitle, String programTitle, String dueDateDisplay) {
            this.seanceTitle = seanceTitle;
            this.programTitle = programTitle;
            this.dueDateDisplay = dueDateDisplay;
        }
    }

    private final List<Item> items;

    public UpcomingSeanceAdapter(List<Item> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUpcomingSeanceBinding binding = ItemUpcomingSeanceBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.binding.textSeanceTitle.setText(item.seanceTitle);
        holder.binding.textProgramName.setText(item.programTitle);
        holder.binding.textDueDate.setText(item.dueDateDisplay);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemUpcomingSeanceBinding binding;

        ViewHolder(ItemUpcomingSeanceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
