package com.example.timelineapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.timelineapp.DataBase.User;
import com.example.timelineapp.DataBase.UserDatabase;
import com.example.timelineapp.databinding.ActivityProfileBinding;
import com.example.timelineapp.databinding.ActivityRegisterBinding;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private UserDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = UserDatabase.getInstance(getApplicationContext());
        User user = db.UserDao().getUser(GameViewModel.USER_ID);

        binding.tvHighestScore.setText("Highest Score : "+String.valueOf(user.experiencePoints) +" XP");
        binding.tvEmail.setText(user.email);
        binding.tvUsername.setText(user.username);
        binding.tvFullName.setText(user.fullName);

        binding.btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }
}