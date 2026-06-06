package com.example.finalpj.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finalpj.R;
import com.example.finalpj.data.db.entity.User;
import com.example.finalpj.data.repository.AuthRepository;
import com.example.finalpj.ui.auth.LoginActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {
    private AuthRepository authRepository;
    private User currentUser;
    private TextInputEditText etName;
    private TextView tvEmail;
    private CircleImageView imgProfile;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    imgProfile.setImageURI(uri);
                    if (currentUser != null) {
                        currentUser.avatarUrl = uri.toString();
                        // Lưu xuống DB (giả lập server)
                        Executors.newSingleThreadExecutor().execute(() -> authRepository.updateUser(currentUser));
                    }
                }
            }
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authRepository = new AuthRepository(requireActivity().getApplication());

        etName = view.findViewById(R.id.et_profile_name);
        tvEmail = view.findViewById(R.id.tv_profile_email);
        imgProfile = view.findViewById(R.id.img_profile);

        authRepository.getCurrentUserLive().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                currentUser = user;
                etName.setText(user.fullName);
                tvEmail.setText(user.email);
                if (user.avatarUrl != null && !user.avatarUrl.isEmpty()) {
                    Picasso.get().load(user.avatarUrl).into(imgProfile);
                }
            }
        });

        imgProfile.setOnClickListener(v -> mGetContent.launch("image/*"));

        view.findViewById(R.id.btn_save_profile).setOnClickListener(v -> saveProfile());
        view.findViewById(R.id.btn_logout_profile).setOnClickListener(v -> logout());
    }

    private void saveProfile() {
        String newName = etName.getText().toString().trim();
        if (newName.isEmpty()) {
            etName.setError("Tên không được để trống");
            return;
        }

        currentUser.fullName = newName;
        Executors.newSingleThreadExecutor().execute(() -> {
            authRepository.updateUser(currentUser);
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "Đã cập nhật thông tin!", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void logout() {
        authRepository.logout();
        startActivity(new Intent(requireContext(), LoginActivity.class));
        requireActivity().finish();
    }
}
