package com.anis.fitzone.adaptateurs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anis.fitzone.R;
import com.anis.fitzone.databinding.ItemQuizBinding;
import com.anis.fitzone.modeles.Quiz;
import com.anis.fitzone.modeles.QuizResult;

import java.util.List;
import java.util.Map;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.ViewHolder> {

    public interface OnQuizClickListener {
        void onQuizClick(Quiz quiz);
    }

    private final List<Quiz> items;
    private final Map<String, QuizResult> results;
    private final OnQuizClickListener listener;

    public QuizAdapter(List<Quiz> items, Map<String, QuizResult> results, OnQuizClickListener listener) {
        this.items = items;
        this.results = results;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemQuizBinding binding = ItemQuizBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Quiz quiz = items.get(position);
        holder.binding.textTitle.setText(quiz.getTitle());
        holder.binding.textQuestionCount.setText(holder.itemView.getContext().getString(
                R.string.questions_count, quiz.getQuestions() == null ? 0 : quiz.getQuestions().size()));

        QuizResult result = results.get(quiz.getId());
        boolean done = result != null;
        holder.binding.textStatus.setText(done ? R.string.quiz_status_done : R.string.quiz_status_not_started);
        int color = holder.itemView.getContext().getColor(done ? R.color.status_validated : R.color.status_todo);
        holder.binding.textStatus.setTextColor(color);
        holder.binding.textStatus.getBackground().setTint((30 << 24) | (color & 0x00FFFFFF));

        if (done) {
            holder.binding.textScore.setVisibility(View.VISIBLE);
            holder.binding.textScore.setText(holder.itemView.getContext().getString(
                    R.string.quiz_score, result.getScore(), result.getTotal()));
        } else {
            holder.binding.textScore.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onQuizClick(quiz));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemQuizBinding binding;

        ViewHolder(ItemQuizBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
