package com.anis.fitzone.adaptateurs;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anis.fitzone.R;
import com.anis.fitzone.databinding.ItemNutritionBinding;
import com.anis.fitzone.modeles.NutritionTip;

import java.util.List;

public class NutritionAdapter extends RecyclerView.Adapter<NutritionAdapter.ViewHolder> {

    private final List<NutritionTip> items;

    public NutritionAdapter(List<NutritionTip> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNutritionBinding binding = ItemNutritionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NutritionTip tip = items.get(position);
        holder.binding.textEmoji.setText(tip.getEmoji());
        holder.binding.textAliment.setText(tip.getAliment());
        holder.binding.textDescription.setText(tip.getDescription());
        holder.binding.textCalories.setText(holder.itemView.getContext().getString(R.string.calories_label, tip.getCalories()));
        holder.binding.textMoment.setText(tip.getMoment());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemNutritionBinding binding;

        ViewHolder(ItemNutritionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
