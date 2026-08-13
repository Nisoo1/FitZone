package com.anis.fitzone.activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.anis.fitzone.adaptateurs.QuizAdapter;
import com.anis.fitzone.dao.QuizResultDao;
import com.anis.fitzone.databinding.ActivityQuizListBinding;
import com.anis.fitzone.modeles.Quiz;
import com.anis.fitzone.modeles.QuizResult;
import com.anis.fitzone.modeles.User;
import com.anis.fitzone.reseau.RetrofitClient;
import com.anis.fitzone.utils.SessionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizListActivity extends AppCompatActivity {

    public static final String EXTRA_PROGRAM_ID = "extra_program_id";

    private ActivityQuizListBinding binding;
    private String programId;
    private List<Quiz> quizzes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        programId = getIntent().getStringExtra(EXTRA_PROGRAM_ID);
        binding.buttonBack.setOnClickListener(v -> finish());
        binding.recyclerQuiz.setLayoutManager(new LinearLayoutManager(this));

        loadQuizzes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (quizzes != null) {
            render(quizzes);
        }
    }

    private void loadQuizzes() {
        RetrofitClient.getApiService().getQuizzesByProgram(programId).enqueue(new Callback<List<Quiz>>() {
            @Override
            public void onResponse(Call<List<Quiz>> call, Response<List<Quiz>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    quizzes = response.body();
                    render(quizzes);
                } else {
                    showEmpty();
                }
            }

            @Override
            public void onFailure(Call<List<Quiz>> call, Throwable t) {
                showEmpty();
            }
        });
    }

    private void render(List<Quiz> list) {
        String userId = new SessionManager(this).getUserId();
        Map<String, QuizResult> merged = new HashMap<>(new QuizResultDao(this).getBestResultsForUser(userId));

        RetrofitClient.getApiService().getUser(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getQuizResults() != null) {
                    for (QuizResult r : response.body().getQuizResults()) {
                        merged.putIfAbsent(r.getQuizId(), r);
                    }
                }
                display(list, merged);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                display(list, merged);
            }
        });
    }

    private void display(List<Quiz> list, Map<String, QuizResult> merged) {
        binding.recyclerQuiz.setAdapter(new QuizAdapter(list, merged, quiz -> {
            Intent intent = new Intent(this, QuizDetailActivity.class);
            intent.putExtra(QuizDetailActivity.EXTRA_QUIZ_ID, quiz.getId());
            startActivity(intent);
        }));
        binding.textEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        binding.recyclerQuiz.setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void showEmpty() {
        binding.textEmpty.setVisibility(View.VISIBLE);
        binding.recyclerQuiz.setVisibility(View.GONE);
    }
}
