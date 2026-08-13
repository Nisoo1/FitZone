package com.anis.fitzone.activites;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.anis.fitzone.R;
import com.anis.fitzone.databinding.FragmentProfileBinding;
import com.anis.fitzone.modeles.User;
import com.anis.fitzone.reseau.RetrofitClient;
import com.anis.fitzone.utils.SessionManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private SessionManager sessionManager;
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(requireContext());

        binding.buttonSave.setOnClickListener(v -> save());
        binding.buttonLogout.setOnClickListener(v -> confirmLogout());

        loadUser();
    }

    private void loadUser() {
        String userId = sessionManager.getUserId();
        RetrofitClient.getApiService().getUser(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body();
                    render(currentUser);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Hors ligne : on affiche au moins les informations de session.
                binding.editPrenom.setText(sessionManager.getPrenom());
                binding.editNom.setText(sessionManager.getNom());
                binding.editEmail.setText(sessionManager.getEmail());
                binding.editPhone.setText(sessionManager.getTelephone());
                binding.editPhotoUrl.setText(sessionManager.getPhotoUrl());
                binding.textFullName.setText(sessionManager.getPrenom() + " " + sessionManager.getNom());
                binding.textEmailHeader.setText(sessionManager.getEmail());
            }
        });
    }

    private void render(User user) {
        binding.textFullName.setText(user.getFullName());
        binding.textEmailHeader.setText(user.getEmail());
        binding.editPrenom.setText(user.getPrenom());
        binding.editNom.setText(user.getNom());
        binding.editEmail.setText(user.getEmail());
        binding.editPhone.setText(user.getTelephone());
        binding.editPhotoUrl.setText(user.getPhotoUrl());
    }

    private void save() {
        String prenom = text(binding.editPrenom);
        String nom = text(binding.editNom);
        String phone = text(binding.editPhone);
        String photoUrl = text(binding.editPhotoUrl);
        String newPassword = text(binding.editPassword);

        if (TextUtils.isEmpty(prenom) || TextUtils.isEmpty(nom)) {
            Toast.makeText(requireContext(), R.string.field_required, Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> fields = new HashMap<>();
        fields.put("prenom", prenom);
        fields.put("nom", nom);
        fields.put("telephone", phone);
        fields.put("photoUrl", photoUrl);
        if (!TextUtils.isEmpty(newPassword)) {
            fields.put("password", newPassword);
        }

        String userId = sessionManager.getUserId();
        RetrofitClient.getApiService().updateUser(userId, fields).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body();
                    sessionManager.saveSession(currentUser);
                    render(currentUser);
                    binding.editPassword.setText("");
                    Toast.makeText(requireContext(), R.string.profile_updated, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(requireContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmLogout() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.logout_confirm_title)
                .setMessage(R.string.logout_confirm_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    sessionManager.clear();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .show();
    }

    private String text(com.google.android.material.textfield.TextInputEditText edit) {
        return edit.getText() == null ? "" : edit.getText().toString().trim();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
