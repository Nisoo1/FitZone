package com.anis.fitzone.activites;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.anis.fitzone.R;
import com.anis.fitzone.databinding.ActivityRegisterBinding;
import com.anis.fitzone.modeles.User;
import com.anis.fitzone.reseau.RetrofitClient;
import com.anis.fitzone.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        binding.buttonBack.setOnClickListener(v -> finish());
        binding.textGoLogin.setOnClickListener(v -> finish());
        binding.buttonRegister.setOnClickListener(v -> attemptRegister());
    }

    private void attemptRegister() {
        hideError();
        String prenom = text(binding.editPrenom);
        String nom = text(binding.editNom);
        String email = text(binding.editEmail);
        String phone = text(binding.editPhone);
        String photoUrl = text(binding.editPhotoUrl);
        String password = text(binding.editPassword);

        boolean valid = true;
        if (TextUtils.isEmpty(prenom)) {
            binding.layoutPrenom.setError(getString(R.string.field_required));
            valid = false;
        } else {
            binding.layoutPrenom.setError(null);
        }
        if (TextUtils.isEmpty(nom)) {
            binding.layoutNom.setError(getString(R.string.field_required));
            valid = false;
        } else {
            binding.layoutNom.setError(null);
        }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.layoutEmail.setError(getString(R.string.invalid_email));
            valid = false;
        } else {
            binding.layoutEmail.setError(null);
        }
        if (TextUtils.isEmpty(phone)) {
            binding.layoutPhone.setError(getString(R.string.field_required));
            valid = false;
        } else {
            binding.layoutPhone.setError(null);
        }
        if (TextUtils.isEmpty(password)) {
            binding.layoutPassword.setError(getString(R.string.field_required));
            valid = false;
        } else {
            binding.layoutPassword.setError(null);
        }
        if (!valid) {
            return;
        }

        setLoading(true);
        RetrofitClient.getApiService().getUserByEmail(email).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    setLoading(false);
                    showError(getString(R.string.email_taken));
                    return;
                }
                createUser(prenom, nom, email, phone, photoUrl, password);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                setLoading(false);
                showError(getString(R.string.network_error));
            }
        });
    }

    private void createUser(String prenom, String nom, String email, String phone, String photoUrl, String password) {
        User user = new User();
        user.setPrenom(prenom);
        user.setNom(nom);
        user.setUsername(prenom);
        user.setEmail(email);
        user.setTelephone(phone);
        user.setPhotoUrl(photoUrl);
        user.setPassword(password);

        RetrofitClient.getApiService().createUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegisterActivity.this, R.string.register_success, Toast.LENGTH_SHORT).show();
                    sessionManager.saveSession(response.body());
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    showError(getString(R.string.network_error));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                setLoading(false);
                showError(getString(R.string.network_error));
            }
        });
    }

    private void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.buttonRegister.setEnabled(!loading);
    }

    private void showError(String message) {
        binding.textError.setText(message);
        binding.textError.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        binding.textError.setVisibility(View.GONE);
    }

    private String text(com.google.android.material.textfield.TextInputEditText edit) {
        return edit.getText() == null ? "" : edit.getText().toString().trim();
    }
}
