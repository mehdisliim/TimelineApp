package com.example.timelineapp;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.timelineapp.DataBase.User;
import com.example.timelineapp.DataBase.UserDatabase;
import com.example.timelineapp.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private UserDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        db = UserDatabase.getInstance(getApplicationContext());

        if (db.UserDao().getUser(GameViewModel.USER_ID) != null) {
            finish();
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.etEmail.getText().toString();
                String username = binding.etUsername.getText().toString();
                String fullName = binding.etFullname.getText().toString();
                String findGameOn = null;

                if (binding.rbFriend.isChecked()) {
                    findGameOn = binding.rbFriend.getText().toString();
                }

                if (binding.rbOther.isChecked()) {
                    findGameOn = binding.rbOther.getText().toString();
                }

                if (binding.rbPlayStore.isChecked()) {
                    findGameOn = binding.rbPlayStore.getText().toString();
                }

                if (email.isEmpty()) {
                    showDialogFor("Please write your E-mail");
                } else if (username.isEmpty()) {
                    showDialogFor("Please write your Username");
                } else if (fullName.isEmpty()) {
                    showDialogFor("Please write your Full Name");
                } else if (findGameOn == null || findGameOn.isEmpty()) {
                    showDialogFor("Please select where you found the game");
                } else {
                    User user = new User(GameViewModel.USER_ID, 0, fullName, username, email, findGameOn);
                    db.UserDao().insertUser(user);

                    finish();
                    Intent intent = new Intent(view.getContext(), ProfileActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public void showDialogFor(String inputFieldRequiredText) {
        new AlertDialog.Builder(RegisterActivity.this)
                .setMessage(inputFieldRequiredText)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }
}