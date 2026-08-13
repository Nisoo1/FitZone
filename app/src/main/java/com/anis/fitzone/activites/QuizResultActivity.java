package com.anis.fitzone.activites;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.anis.fitzone.R;
import com.anis.fitzone.databinding.ActivityQuizResultBinding;

public class QuizResultActivity extends AppCompatActivity {

    public static final String EXTRA_SCORE = "extra_score";
    public static final String EXTRA_TOTAL = "extra_total";

    private ActivityQuizResultBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int score = getIntent().getIntExtra(EXTRA_SCORE, 0);
        int total = getIntent().getIntExtra(EXTRA_TOTAL, 0);
        binding.textScore.setText(getString(R.string.quiz_score, score, total));

        binding.buttonBackToQuiz.setOnClickListener(v -> finish());
    }
}
