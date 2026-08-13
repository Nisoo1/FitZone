package com.anis.fitzone.activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.anis.fitzone.R;
import com.anis.fitzone.dao.HistoriqueDao;
import com.anis.fitzone.dao.ProgramDao;
import com.anis.fitzone.databinding.ActivityProgramDetailBinding;
import com.anis.fitzone.modeles.Program;
import com.anis.fitzone.reseau.RetrofitClient;
import com.anis.fitzone.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProgramDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PROGRAM_ID = "extra_program_id";

    private ActivityProgramDetailBinding binding;
    private String programId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProgramDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        programId = getIntent().getStringExtra(EXTRA_PROGRAM_ID);
        binding.buttonBack.setOnClickListener(v -> finish());

        binding.cardSeances.setOnClickListener(v -> {
            Intent intent = new Intent(this, SeanceListActivity.class);
            intent.putExtra(SeanceListActivity.EXTRA_PROGRAM_ID, programId);
            startActivity(intent);
        });

        binding.cardQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(this, QuizListActivity.class);
            intent.putExtra(QuizListActivity.EXTRA_PROGRAM_ID, programId);
            startActivity(intent);
        });

        loadProgram();
    }

    private void loadProgram() {
        RetrofitClient.getApiService().getProgram(programId).enqueue(new Callback<Program>() {
            @Override
            public void onResponse(Call<Program> call, Response<Program> response) {
                if (response.isSuccessful() && response.body() != null) {
                    render(response.body());
                } else {
                    Program cached = new ProgramDao(ProgramDetailActivity.this).getById(programId);
                    if (cached != null) {
                        render(cached);
                    }
                }
            }

            @Override
            public void onFailure(Call<Program> call, Throwable t) {
                Program cached = new ProgramDao(ProgramDetailActivity.this).getById(programId);
                if (cached != null) {
                    render(cached);
                }
            }
        });
    }

    private void render(Program program) {
        binding.textCode.setText(program.getCode());
        binding.textTitle.setText(program.getTitle());
        binding.textCoachSession.setText(getString(R.string.coach_label, program.getCoach())
                + "  •  " + program.getSession());
        binding.textDescription.setText(program.getDescription());

        binding.containerAnnouncements.removeAllViews();
        boolean hasAnnouncements = program.getAnnonces() != null && !program.getAnnonces().isEmpty();
        binding.textNoAnnouncements.setVisibility(hasAnnouncements ? View.GONE : View.VISIBLE);
        if (hasAnnouncements) {
            for (String annonce : program.getAnnonces()) {
                TextView tv = new TextView(this);
                tv.setText("•  " + annonce);
                tv.setTextColor(getColor(R.color.text_primary));
                tv.setPadding(0, 8, 0, 8);
                binding.containerAnnouncements.addView(tv);
            }
        }

        String userId = new SessionManager(this).getUserId();
        if (userId != null) {
            new HistoriqueDao(this).log(userId, "programme", program.getId(),
                    program.getCode() + " - " + program.getTitle());
        }
    }
}
