package com.example.polinelapeduli.utils;

import android.content.Context;
import android.content.Intent;

import com.example.polinelapeduli.activity.SignInActivity;
import com.example.polinelapeduli.model.User;
import com.example.polinelapeduli.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserValidator {
    public static User validateUser(Context context) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser == null) {
            redirectToSignIn(context);
            return null;
        }

        UserRepository userRepository = new UserRepository(context);
        User userLogin = userRepository.getUserByEmail(firebaseUser.getEmail());

        if (userLogin == null || !userLogin.isActive()) {
            redirectToSignIn(context);
            return null;
        }

        return userLogin;
    }

    private static void redirectToSignIn(Context context) {
        Intent intent = new Intent(context, SignInActivity.class);
        context.startActivity(intent);
    }
}

