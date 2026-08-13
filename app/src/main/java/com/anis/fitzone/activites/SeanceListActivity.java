package com.anis.fitzone.activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.anis.fitzone.adaptateurs.SeanceAdapter;
import com.anis.fitzone.dao.SeanceDao;
import com.anis.fitzone.databinding.ActivitySeanceListBinding;
import com.anis.fitzone.modeles.Seance;
import com.anis.fitzone.reseau.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeanceListActivity extends AppCompatActivity {

    public static final String EXTRA_PROGRAM_ID = "extra_program_id";

    private ActivitySeanceListBinding binding;
    private String programId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySeanceListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        programId = getIntent().getStringExtra(EXTRA_PROGRAM_ID);
        binding.buttonBack.setOnClickListener(v -> finish());
        binding.recyclerSeances.setLayoutManager(new LinearLayoutManager(this));

        loadSeances();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Le statut a pu changer si on revient d'un écran de soumission.
        render(new SeanceDao(this).getByProgram(programId));
    }

    private void loadSeances() {
        RetrofitClient.getApiService().getSeancesByProgram(programId).enqueue(new Callback<List<Seance>>() {
            @Override
            public void onResponse(Call<List<Seance>> call, Response<List<Seance>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new SeanceDao(SeanceListActivity.this).saveAllForProgram(programId, response.body());
                }
                render(new SeanceDao(SeanceListActivity.this).getByProgram(programId));
            }

            @Override
            public void onFailure(Call<List<Seance>> call, Throwable t) {
                render(new SeanceDao(SeanceListActivity.this).getByProgram(programId));
            }
        });
    }

    private void render(List<Seance> seances) {
        binding.recyclerSeances.setAdapter(new SeanceAdapter(seances, seance -> {
            Intent intent = new Intent(this, SeanceDetailActivity.class);
            intent.putExtra(SeanceDetailActivity.EXTRA_SEANCE_ID, seance.getId());
            startActivity(intent);
        }));
        binding.textEmpty.setVisibility(seances.isEmpty() ? View.VISIBLE : View.GONE);
        binding.recyclerSeances.setVisibility(seances.isEmpty() ? View.GONE : View.VISIBLE);
    }
}
