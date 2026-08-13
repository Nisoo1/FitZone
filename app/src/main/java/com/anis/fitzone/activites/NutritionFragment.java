package com.anis.fitzone.activites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.anis.fitzone.adaptateurs.NutritionAdapter;
import com.anis.fitzone.dao.NutritionDao;
import com.anis.fitzone.databinding.FragmentNutritionBinding;
import com.anis.fitzone.modeles.NutritionTip;
import com.anis.fitzone.reseau.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NutritionFragment extends Fragment {

    private FragmentNutritionBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNutritionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.recyclerNutrition.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.swipeRefresh.setOnRefreshListener(this::loadData);
        loadData();
    }

    private void loadData() {
        binding.swipeRefresh.setRefreshing(true);
        RetrofitClient.getApiService().getNutritionTips().enqueue(new Callback<List<NutritionTip>>() {
            @Override
            public void onResponse(Call<List<NutritionTip>> call, Response<List<NutritionTip>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new NutritionDao(requireContext()).saveAll(response.body());
                    render(response.body());
                } else {
                    render(new NutritionDao(requireContext()).getAll());
                }
                binding.swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<NutritionTip>> call, Throwable t) {
                render(new NutritionDao(requireContext()).getAll());
                binding.swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void render(List<NutritionTip> tips) {
        if (binding != null) {
            binding.recyclerNutrition.setAdapter(new NutritionAdapter(tips));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
