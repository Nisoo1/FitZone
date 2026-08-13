package com.anis.fitzone.adaptateurs;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anis.fitzone.R;
import com.anis.fitzone.databinding.ItemSeanceBinding;
import com.anis.fitzone.modeles.Seance;
import com.anis.fitzone.utils.StatusUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SeanceAdapter extends RecyclerView.Adapter<SeanceAdapter.ViewHolder> {

    public interface OnSeanceClickListener {
        void onSeanceClick(Seance seance);
    }

    private final List<Seance> items;
    private final OnSeanceClickListener listener;

    public SeanceAdapter(List<Seance> items, OnSeanceClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSeanceBinding binding = ItemSeanceBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Seance s = items.get(position);
        holder.binding.textTitle.setText(s.getTitle());
        String status = s.getDisplayStatus();
        holder.binding.textStatus.setText(StatusUtils.labelFor(holder.itemView.getContext(), status));
        int color = StatusUtils.colorFor(holder.itemView.getContext(), status);
        holder.binding.textStatus.setTextColor(color);
        holder.binding.textStatus.getBackground().setTint(withAlpha(color, 30));
        holder.binding.textDueDate.setText(holder.itemView.getContext().getString(
                R.string.due_date_label, formatDate(s.getDueDate())));

        if (Seance.STATUT_VALIDEE.equals(s.getStatus()) && s.getGrade() != null) {
            holder.binding.textGrade.setVisibility(View.VISIBLE);
            holder.binding.textGrade.setText(holder.itemView.getContext().getString(
                    R.string.grade_label, s.getGrade(), s.getTotalPoints()));
        } else {
            holder.binding.textGrade.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onSeanceClick(s));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private int withAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00FFFFFF);
    }

    private String formatDate(String isoDate) {
        if (TextUtils.isEmpty(isoDate)) {
            return "";
        }
        try {
            SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
            SimpleDateFormat out = new SimpleDateFormat("d MMMM yyyy", Locale.FRENCH);
            return out.format(in.parse(isoDate));
        } catch (Exception e) {
            return isoDate;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemSeanceBinding binding;

        ViewHolder(ItemSeanceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
