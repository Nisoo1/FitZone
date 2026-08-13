package com.anis.fitzone.activites;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.anis.fitzone.adaptateurs.ProgramAdapter;
import com.anis.fitzone.dao.ProgramDao;
import com.anis.fitzone.databinding.FragmentProgramsBinding;
import com.anis.fitzone.modeles.Program;
import com.anis.fitzone.modeles.Seance;
import com.anis.fitzone.modeles.User;
import com.anis.fitzone.reseau.RetrofitClient;
import com.anis.fitzone.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProgramsFragment extends Fragment {

    private FragmentProgramsBinding binding;
    private SessionManager sessionManager;

    private final List<Program> allEnrolled = new ArrayList<>();
    private final Set<String> completedProgramIds = new HashSet<>();
    private String currentQuery = "";
    private int currentFilter = 0; // 0=all, 1=active, 2=completed

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProgramsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(requireContext());
        binding.recyclerPrograms.setLayoutManager(new LinearLayoutManager(requireContext()));

        binding.editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentQuery = s.toString();
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.chipAll.setOnClickListener(v -> {
            currentFilter = 0;
            applyFilters();
        });
        binding.chipActive.setOnClickListener(v -> {
            currentFilter = 1;
            applyFilters();
        });
        binding.chipCompleted.setOnClickListener(v -> {
            currentFilter = 2;
            applyFilters();
        });

        binding.swipeRefresh.setOnRefreshListener(this::loadData);
        loadData();
    }

    private void loadData() {
        binding.swipeRefresh.setRefreshing(true);
        String userId = sessionManager.getUserId();
        RetrofitClient.getApiService().getUser(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loadPrograms(response.body());
                } else {
                    loadFromCache();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                loadFromCache();
            }
        });
    }

    private void loadPrograms(User user) {
        RetrofitClient.getApiService().getPrograms().enqueue(new Callback<List<Program>>() {
            @Override
            public void onResponse(Call<List<Program>> call, Response<List<Program>> response) {
                List<Program> all = response.isSuccessful() && response.body() != null
                        ? response.body() : new ArrayList<>();
                new ProgramDao(requireContext()).saveAll(all);

                allEnrolled.clear();
                for (Program p : all) {
                    if (user.getEnrolledProgramIds() != null && user.getEnrolledProgramIds().contains(p.getId())) {
                        allEnrolled.add(p);
                    }
                }
                computeCompletionAndRender();
            }

            @Override
            public void onFailure(Call<List<Program>> call, Throwable t) {
                loadFromCache();
            }
        });
    }

    private void loadFromCache() {
        allEnrolled.clear();
        allEnrolled.addAll(new ProgramDao(requireContext()).getAll());
        completedProgramIds.clear();
        applyFilters();
        binding.swipeRefresh.setRefreshing(false);
    }

    private void computeCompletionAndRender() {
        completedProgramIds.clear();
        if (allEnrolled.isEmpty()) {
            applyFilters();
            binding.swipeRefresh.setRefreshing(false);
            return;
        }
        int[] pending = {allEnrolled.size()};
        for (Program p : allEnrolled) {
            RetrofitClient.getApiService().getSeancesByProgram(p.getId()).enqueue(new Callback<List<Seance>>() {
                @Override
                public void onResponse(Call<List<Seance>> call, Response<List<Seance>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        boolean allValidated = true;
                        for (Seance s : response.body()) {
                            if (!Seance.STATUT_VALIDEE.equals(s.getStatus())) {
                                allValidated = false;
                                break;
                            }
                        }
                        if (allValidated) {
                            completedProgramIds.add(p.getId());
                        }
                    }
                    finishOne(pending);
                }

                @Override
                public void onFailure(Call<List<Seance>> call, Throwable t) {
                    finishOne(pending);
                }
            });
        }
    }

    private void finishOne(int[] pending) {
        pending[0]--;
        if (pending[0] <= 0 && binding != null) {
            applyFilters();
            binding.swipeRefresh.setRefreshing(false);
        }
    }

    private void applyFilters() {
        if (binding == null) {
            return;
        }
        List<Program> filtered = new ArrayList<>();
        String query = currentQuery.toLowerCase(Locale.CANADA).trim();
        for (Program p : allEnrolled) {
            boolean matchesQuery = query.isEmpty()
                    || (p.getTitle() != null && p.getTitle().toLowerCase(Locale.CANADA).contains(query))
                    || (p.getCode() != null && p.getCode().toLowerCase(Locale.CANADA).contains(query));
            boolean matchesFilter = currentFilter == 0
                    || (currentFilter == 1 && !completedProgramIds.contains(p.getId()))
                    || (currentFilter == 2 && completedProgramIds.contains(p.getId()));
            if (matchesQuery && matchesFilter) {
                filtered.add(p);
            }
        }

        binding.recyclerPrograms.setAdapter(new ProgramAdapter(filtered, program -> {
            Intent intent = new Intent(requireContext(), ProgramDetailActivity.class);
            intent.putExtra(ProgramDetailActivity.EXTRA_PROGRAM_ID, program.getId());
            startActivity(intent);
        }));
        binding.textEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        binding.recyclerPrograms.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
