package com.anis.fitzone.activites;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.anis.fitzone.R;
import com.anis.fitzone.dao.HistoriqueDao;
import com.anis.fitzone.dao.SeanceDao;
import com.anis.fitzone.databinding.ActivitySeanceDetailBinding;
import com.anis.fitzone.modeles.Seance;
import com.anis.fitzone.reseau.RetrofitClient;
import com.anis.fitzone.utils.SessionManager;
import com.anis.fitzone.utils.StatusUtils;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeanceDetailActivity extends AppCompatActivity {

    public static final String EXTRA_SEANCE_ID = "extra_seance_id";

    private ActivitySeanceDetailBinding binding;
    private SeanceDao seanceDao;
    private String seanceId;
    private Seance current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySeanceDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        seanceId = getIntent().getStringExtra(EXTRA_SEANCE_ID);
        seanceDao = new SeanceDao(this);

        binding.buttonBack.setOnClickListener(v -> finish());
        binding.buttonSubmit.setOnClickListener(v -> submit());

        Seance cached = seanceDao.getById(seanceId);
        if (cached != null) {
            current = cached;
            render(cached);
        }

        refreshFromNetwork();
    }

    private void refreshFromNetwork() {
        RetrofitClient.getApiService().getSeance(seanceId).enqueue(new Callback<Seance>() {
            @Override
            public void onResponse(Call<Seance> call, Response<Seance> response) {
                if (response.isSuccessful() && response.body() != null && seanceDao.isSynced(seanceId)) {
                    current = response.body();
                    seanceDao.upsert(current, true);
                    render(current);
                }
            }

            @Override
            public void onFailure(Call<Seance> call, Throwable t) {
                // Hors ligne : on garde l'affichage du cache local.
            }
        });
    }

    private void render(Seance s) {
        binding.textTitle.setText(s.getTitle());
        binding.textDescription.setText(s.getDescription());
        binding.textInstructions.setText(s.getInstructions());
        binding.textDueDate.setText(getString(R.string.due_date_label, formatDate(s.getDueDate())));

        String status = s.getDisplayStatus();
        binding.textStatus.setText(StatusUtils.labelFor(this, status));

        boolean isSubmittedOrValidated = Seance.STATUT_SOUMISE.equals(s.getStatus())
                || Seance.STATUT_VALIDEE.equals(s.getStatus());

        binding.sectionSubmissionForm.setVisibility(isSubmittedOrValidated ? View.GONE : View.VISIBLE);
        binding.sectionSubmissionRecap.setVisibility(isSubmittedOrValidated ? View.VISIBLE : View.GONE);

        if (isSubmittedOrValidated) {
            binding.textSubmittedOn.setText(TextUtils.isEmpty(s.getSubmittedAt())
                    ? "" : getString(R.string.submitted_on, s.getSubmittedAt()));
            boolean hasUrl = !TextUtils.isEmpty(s.getSubmissionUrl());
            boolean hasText = !TextUtils.isEmpty(s.getSubmissionText());
            binding.textRecapUrl.setVisibility(hasUrl ? View.VISIBLE : View.GONE);
            binding.textRecapUrl.setText(s.getSubmissionUrl());
            binding.textRecapText.setVisibility(hasText ? View.VISIBLE : View.GONE);
            binding.textRecapText.setText(s.getSubmissionText());
        }

        boolean isValidated = Seance.STATUT_VALIDEE.equals(s.getStatus());
        binding.sectionFeedback.setVisibility(isValidated ? View.VISIBLE : View.GONE);
        if (isValidated) {
            if (s.getGrade() != null) {
                binding.textGrade.setText(getString(R.string.grade_label, s.getGrade(), s.getTotalPoints()));
            }
            binding.textComment.setText(TextUtils.isEmpty(s.getComment())
                    ? getString(R.string.no_feedback) : s.getComment());
        }

        String userId = new SessionManager(this).getUserId();
        if (userId != null) {
            new HistoriqueDao(this).log(userId, "seance", s.getId(), s.getTitle());
        }
    }

    private void submit() {
        if (current == null) {
            return;
        }
        String url = text(binding.editSubmissionUrl);
        String text = text(binding.editSubmissionText);

        if (TextUtils.isEmpty(url) && TextUtils.isEmpty(text)) {
            binding.textSubmissionWarning.setVisibility(View.VISIBLE);
            return;
        }
        binding.textSubmissionWarning.setVisibility(View.GONE);

        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CANADA).format(new java.util.Date());
        current.setStatus(Seance.STATUT_SOUMISE);
        current.setSubmissionUrl(url);
        current.setSubmissionText(text);
        current.setSubmittedAt(now);

        seanceDao.upsert(current, false);
        render(current);

        Map<String, Object> fields = new HashMap<>();
        fields.put("status", Seance.STATUT_SOUMISE);
        fields.put("submissionUrl", url);
        fields.put("submissionText", text);
        fields.put("submittedAt", now);

        RetrofitClient.getApiService().updateSeance(seanceId, fields).enqueue(new Callback<Seance>() {
            @Override
            public void onResponse(Call<Seance> call, Response<Seance> response) {
                if (response.isSuccessful()) {
                    seanceDao.upsert(current, true);
                }
                Toast.makeText(SeanceDetailActivity.this, R.string.submitted_message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Seance> call, Throwable t) {
                Toast.makeText(SeanceDetailActivity.this, R.string.submitted_message, Toast.LENGTH_SHORT).show();
            }
        });
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

    private String text(com.google.android.material.textfield.TextInputEditText edit) {
        return edit.getText() == null ? "" : edit.getText().toString().trim();
    }
}
