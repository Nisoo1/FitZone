package com.anis.fitzone.activites;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.anis.fitzone.R;
import com.anis.fitzone.adaptateurs.AnnouncementAdapter;
import com.anis.fitzone.adaptateurs.UpcomingSeanceAdapter;
import com.anis.fitzone.dao.ProgramDao;
import com.anis.fitzone.databinding.FragmentDashboardBinding;
import com.anis.fitzone.modeles.Program;
import com.anis.fitzone.modeles.Quiz;
import com.anis.fitzone.modeles.QuizResult;
import com.anis.fitzone.modeles.Seance;
import com.anis.fitzone.modeles.User;
import com.anis.fitzone.reseau.RetrofitClient;
import com.anis.fitzone.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private SessionManager sessionManager;

    private final List<Seance> aggregatedSeances = new ArrayList<>();
    private final List<Quiz> aggregatedQuizzes = new ArrayList<>();
    private final Map<String, Program> programById = new HashMap<>();
    private int pendingCalls;
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(requireContext());

        binding.statPrograms.textStatLabel.setText(R.string.dashboard_programs_count);
        binding.statQuiz.textStatLabel.setText(R.string.dashboard_quiz_count);
        binding.statLate.textStatLabel.setText(R.string.dashboard_late_count);
        binding.statProgress.textStatLabel.setText(R.string.dashboard_progress);

        String greetingName = sessionManager.getPrenom();
        binding.textGreetingName.setText(TextUtils.isEmpty(greetingName) ? "" : greetingName);

        binding.swipeRefresh.setOnRefreshListener(this::loadData);
        loadData();
    }

    private void loadData() {
        binding.swipeRefresh.setRefreshing(true);
        String userId = sessionManager.getUserId();
        if (userId == null) {
            binding.swipeRefresh.setRefreshing(false);
            return;
        }

        RetrofitClient.getApiService().getUser(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body();
                    loadPrograms();
                } else {
                    binding.swipeRefresh.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                binding.swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void loadPrograms() {
        RetrofitClient.getApiService().getPrograms().enqueue(new Callback<List<Program>>() {
            @Override
            public void onResponse(Call<List<Program>> call, Response<List<Program>> response) {
                List<Program> all = response.isSuccessful() && response.body() != null
                        ? response.body() : new ArrayList<>();
                new ProgramDao(requireContext()).saveAll(all);

                programById.clear();
                List<Program> enrolled = new ArrayList<>();
                for (Program p : all) {
                    programById.put(p.getId(), p);
                    if (currentUser.getEnrolledProgramIds() != null
                            && currentUser.getEnrolledProgramIds().contains(p.getId())) {
                        enrolled.add(p);
                    }
                }

                binding.statPrograms.textStatValue.setText(String.valueOf(enrolled.size()));

                if (enrolled.isEmpty()) {
                    binding.statQuiz.textStatValue.setText("0");
                    binding.statLate.textStatValue.setText("0");
                    binding.statProgress.textStatValue.setText("0%");
                    renderUpcoming();
                    renderAnnouncements(enrolled);
                    binding.swipeRefresh.setRefreshing(false);
                    return;
                }

                aggregatedSeances.clear();
                aggregatedQuizzes.clear();
                pendingCalls = enrolled.size() * 2;

                for (Program p : enrolled) {
                    RetrofitClient.getApiService().getSeancesByProgram(p.getId()).enqueue(new Callback<List<Seance>>() {
                        @Override
                        public void onResponse(Call<List<Seance>> call, Response<List<Seance>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                aggregatedSeances.addAll(response.body());
                            }
                            onCallFinished(enrolled);
                        }

                        @Override
                        public void onFailure(Call<List<Seance>> call, Throwable t) {
                            onCallFinished(enrolled);
                        }
                    });

                    RetrofitClient.getApiService().getQuizzesByProgram(p.getId()).enqueue(new Callback<List<Quiz>>() {
                        @Override
                        public void onResponse(Call<List<Quiz>> call, Response<List<Quiz>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                aggregatedQuizzes.addAll(response.body());
                            }
                            onCallFinished(enrolled);
                        }

                        @Override
                        public void onFailure(Call<List<Quiz>> call, Throwable t) {
                            onCallFinished(enrolled);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Program>> call, Throwable t) {
                List<Program> cached = new ProgramDao(requireContext()).getAll();
                binding.statPrograms.textStatValue.setText(String.valueOf(cached.size()));
                binding.swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void onCallFinished(List<Program> enrolled) {
        pendingCalls--;
        if (pendingCalls <= 0 && binding != null) {
            computeStats();
            renderUpcoming();
            renderAnnouncements(enrolled);
            binding.swipeRefresh.setRefreshing(false);
        }
    }

    private void computeStats() {
        Map<String, QuizResult> resultsByQuiz = new HashMap<>();
        if (currentUser.getQuizResults() != null) {
            for (QuizResult r : currentUser.getQuizResults()) {
                resultsByQuiz.put(r.getQuizId(), r);
            }
        }

        int quizToComplete = 0;
        for (Quiz q : aggregatedQuizzes) {
            if (!resultsByQuiz.containsKey(q.getId())) {
                quizToComplete++;
            }
        }

        int late = 0;
        int done = 0;
        for (Seance s : aggregatedSeances) {
            if (s.isLateComputed()) {
                late++;
            }
            if (Seance.STATUT_SOUMISE.equals(s.getStatus()) || Seance.STATUT_VALIDEE.equals(s.getStatus())) {
                done++;
            }
        }

        int totalItems = aggregatedSeances.size() + aggregatedQuizzes.size();
        int doneItems = done + resultsByQuiz.size();
        int progress = totalItems == 0 ? 0 : Math.round((doneItems * 100f) / totalItems);

        binding.statQuiz.textStatValue.setText(String.valueOf(quizToComplete));
        binding.statLate.textStatValue.setText(String.valueOf(late));
        binding.statProgress.textStatValue.setText(progress + "%");
    }

    private void renderUpcoming() {
        List<Seance> upcoming = new ArrayList<>();
        for (Seance s : aggregatedSeances) {
            if (!Seance.STATUT_VALIDEE.equals(s.getStatus())) {
                upcoming.add(s);
            }
        }
        Collections.sort(upcoming, Comparator.comparing(Seance::getDueDate));
        List<Seance> top = upcoming.subList(0, Math.min(3, upcoming.size()));

        List<UpcomingSeanceAdapter.Item> items = new ArrayList<>();
        for (Seance s : top) {
            Program p = programById.get(s.getProgramId());
            String programLabel = p != null ? p.getCode() + " - " + p.getTitle() : "";
            items.add(new UpcomingSeanceAdapter.Item(s.getTitle(), programLabel, formatDate(s.getDueDate())));
        }

        binding.recyclerUpcoming.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerUpcoming.setAdapter(new UpcomingSeanceAdapter(items));
        binding.textNoUpcoming.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        binding.recyclerUpcoming.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void renderAnnouncements(List<Program> enrolled) {
        List<AnnouncementAdapter.Item> items = new ArrayList<>();
        for (Program p : enrolled) {
            if (p.getAnnonces() == null) {
                continue;
            }
            for (String a : p.getAnnonces()) {
                items.add(new AnnouncementAdapter.Item(p.getCode() + " - " + p.getTitle(), a));
                if (items.size() >= 5) {
                    break;
                }
            }
            if (items.size() >= 5) {
                break;
            }
        }

        binding.recyclerAnnouncements.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerAnnouncements.setAdapter(new AnnouncementAdapter(items));
        binding.textNoAnnouncements.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        binding.recyclerAnnouncements.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private String formatDate(String isoDate) {
        try {
            SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
            SimpleDateFormat out = new SimpleDateFormat("d MMM", Locale.FRENCH);
            return out.format(in.parse(isoDate));
        } catch (Exception e) {
            return isoDate == null ? "" : isoDate;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
