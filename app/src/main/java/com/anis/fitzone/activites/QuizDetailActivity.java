package com.anis.fitzone.activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.anis.fitzone.R;
import com.anis.fitzone.dao.HistoriqueDao;
import com.anis.fitzone.dao.QuizResultDao;
import com.anis.fitzone.databinding.ActivityQuizDetailBinding;
import com.anis.fitzone.modeles.Question;
import com.anis.fitzone.modeles.Quiz;
import com.anis.fitzone.modeles.QuizResult;
import com.anis.fitzone.modeles.User;
import com.anis.fitzone.reseau.RetrofitClient;
import com.anis.fitzone.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizDetailActivity extends AppCompatActivity {

    public static final String EXTRA_QUIZ_ID = "extra_quiz_id";

    private ActivityQuizDetailBinding binding;
    private Quiz quiz;
    private int currentIndex = 0;
    private int[] selectedAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonBack.setOnClickListener(v -> finish());
        binding.buttonNext.setOnClickListener(v -> onNextClicked());

        String quizId = getIntent().getStringExtra(EXTRA_QUIZ_ID);
        RetrofitClient.getApiService().getQuiz(quizId).enqueue(new Callback<Quiz>() {
            @Override
            public void onResponse(Call<Quiz> call, Response<Quiz> response) {
                if (response.isSuccessful() && response.body() != null) {
                    quiz = response.body();
                    selectedAnswers = new int[quiz.getQuestions().size()];
                    java.util.Arrays.fill(selectedAnswers, -1);
                    binding.textTitle.setText(quiz.getTitle());
                    showQuestion(0);
                } else {
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Quiz> call, Throwable t) {
                finish();
            }
        });
    }

    private void showQuestion(int index) {
        currentIndex = index;
        List<Question> questions = quiz.getQuestions();
        Question q = questions.get(index);

        binding.textProgress.setText(getString(R.string.quiz_progress, index + 1, questions.size()));
        binding.textQuestion.setText(q.getQuestion());
        binding.textWarning.setVisibility(View.GONE);

        binding.radioGroupOptions.removeAllViews();
        List<String> options = q.getOptions();
        for (int i = 0; i < options.size(); i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(options.get(i));
            radioButton.setId(View.generateViewId());
            radioButton.setTextColor(getColor(R.color.text_primary));
            radioButton.setPadding(0, 16, 0, 16);
            radioButton.setTag(i);
            binding.radioGroupOptions.addView(radioButton);
            if (selectedAnswers[index] == i) {
                radioButton.setChecked(true);
            }
        }

        boolean isLast = index == questions.size() - 1;
        binding.buttonNext.setText(isLast ? R.string.finish_quiz : R.string.next_question);
    }

    private void onNextClicked() {
        int checkedId = binding.radioGroupOptions.getCheckedRadioButtonId();
        if (checkedId == View.NO_ID) {
            binding.textWarning.setVisibility(View.VISIBLE);
            return;
        }
        RadioButton checked = findViewById(checkedId);
        selectedAnswers[currentIndex] = (int) checked.getTag();

        if (currentIndex < quiz.getQuestions().size() - 1) {
            showQuestion(currentIndex + 1);
        } else {
            finishQuiz();
        }
    }

    private void finishQuiz() {
        int score = 0;
        List<Question> questions = quiz.getQuestions();
        for (int i = 0; i < questions.size(); i++) {
            if (selectedAnswers[i] == questions.get(i).getCorrectOption()) {
                score++;
            }
        }
        int total = questions.size();

        SessionManager sessionManager = new SessionManager(this);
        String userId = sessionManager.getUserId();
        new QuizResultDao(this).saveResult(userId, quiz.getId(), score, total);
        new HistoriqueDao(this).log(userId, "quiz", quiz.getId(), quiz.getTitle());

        syncResultToServer(userId, score, total);

        Intent intent = new Intent(this, QuizResultActivity.class);
        intent.putExtra(QuizResultActivity.EXTRA_SCORE, score);
        intent.putExtra(QuizResultActivity.EXTRA_TOTAL, total);
        startActivity(intent);
        finish();
    }

    private void syncResultToServer(String userId, int score, int total) {
        RetrofitClient.getApiService().getUser(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    return;
                }
                User user = response.body();
                List<QuizResult> results = user.getQuizResults() != null
                        ? new ArrayList<>(user.getQuizResults()) : new ArrayList<>();
                boolean replaced = false;
                for (int i = 0; i < results.size(); i++) {
                    if (results.get(i).getQuizId().equals(quiz.getId())) {
                        results.set(i, new QuizResult(quiz.getId(), score, total));
                        replaced = true;
                        break;
                    }
                }
                if (!replaced) {
                    results.add(new QuizResult(quiz.getId(), score, total));
                }

                Map<String, Object> fields = new HashMap<>();
                fields.put("quizResults", results);
                RetrofitClient.getApiService().updateUser(userId, fields).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        // Synchronisé.
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        // Restera dans le cache local (quiz_results) en attendant une reconnexion.
                    }
                });
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Hors ligne : le résultat reste enregistré localement.
            }
        });
    }
}
