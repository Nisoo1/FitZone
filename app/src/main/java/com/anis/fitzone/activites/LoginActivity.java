package com.anis.fitzone.activites;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.anis.fitzone.R;
import com.anis.fitzone.databinding.ActivityLoginBinding;
import com.anis.fitzone.modeles.User;
import com.anis.fitzone.reseau.RetrofitClient;
import com.anis.fitzone.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            goToMain();
            return;
        }

        binding.buttonLogin.setOnClickListener(v -> attemptLogin());
        binding.textGoRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void attemptLogin() {
        hideError();
        String email = text(binding.editEmail);
        String password = text(binding.editPassword);

        boolean valid = true;
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.layoutEmail.setError(getString(R.string.invalid_email));
            valid = false;
        } else {
            binding.layoutEmail.setError(null);
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
                setLoading(false);
                User match = null;
                if (response.isSuccessful() && response.body() != null) {
                    for (User u : response.body()) {
                        if (email.equalsIgnoreCase(u.getEmail()) && password.equals(u.getPassword())) {
                            match = u;
                            break;
                        }
                    }
                }
                if (match != null) {
                    sessionManager.saveSession(match);
                    goToMain();
                } else {
                    showError(getString(R.string.login_error));
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                setLoading(false);
                showError(getString(R.string.network_error));
            }
        });
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.buttonLogin.setEnabled(!loading);
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
