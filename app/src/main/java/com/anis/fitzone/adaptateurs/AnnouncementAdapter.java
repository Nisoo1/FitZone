package com.anis.fitzone.adaptateurs;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anis.fitzone.databinding.ItemAnnouncementBinding;

import java.util.List;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {

    public static class Item {
        public final String programTitle;
        public final String text;

        public Item(String programTitle, String text) {
            this.programTitle = programTitle;
            this.text = text;
        }
    }

    private final List<Item> items;

    public AnnouncementAdapter(List<Item> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAnnouncementBinding binding = ItemAnnouncementBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.binding.textProgramName.setText(item.programTitle);
        holder.binding.textAnnouncement.setText(item.text);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemAnnouncementBinding binding;

        ViewHolder(ItemAnnouncementBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
