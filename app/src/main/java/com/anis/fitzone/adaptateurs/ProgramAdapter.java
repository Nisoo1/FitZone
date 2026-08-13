package com.anis.fitzone.adaptateurs;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anis.fitzone.databinding.ItemProgramBinding;
import com.anis.fitzone.modeles.Program;

import java.util.List;
import java.util.Locale;

public class ProgramAdapter extends RecyclerView.Adapter<ProgramAdapter.ViewHolder> {

    public interface OnProgramClickListener {
        void onProgramClick(Program program);
    }

    private final List<Program> items;
    private final OnProgramClickListener listener;

    public ProgramAdapter(List<Program> items, OnProgramClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProgramBinding binding = ItemProgramBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Program p = items.get(position);
        String code = p.getCode() == null ? "FZ" : p.getCode();
        String initials = code.length() >= 2 ? code.substring(0, 2).toUpperCase(Locale.CANADA) : code.toUpperCase(Locale.CANADA);
        holder.binding.textInitials.setText(initials);
        holder.binding.textTitle.setText(p.getTitle());
        holder.binding.textCode.setText(p.getCode());
        holder.binding.textCoach.setText(holder.itemView.getContext().getString(
                com.anis.fitzone.R.string.coach_label, p.getCoach()));
        holder.binding.textSession.setText(holder.itemView.getContext().getString(
                com.anis.fitzone.R.string.session_label, p.getSession()));
        holder.itemView.setOnClickListener(v -> listener.onProgramClick(p));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemProgramBinding binding;

        ViewHolder(ItemProgramBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
