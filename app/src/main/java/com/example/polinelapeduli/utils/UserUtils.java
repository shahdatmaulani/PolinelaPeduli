package com.example.polinelapeduli.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserUtils {

    public interface OnFullNameReceivedListener {
        void onFullNameReceived(String fullName);
    }

    public static void getCurrentFullName(final OnFullNameReceivedListener listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String fullName = document.getString("fullName");
                                listener.onFullNameReceived(fullName);
                            } else {
                                listener.onFullNameReceived(null);
                            }
                        } else {
                            listener.onFullNameReceived(null);
                        }
                    });
        } else {
            listener.onFullNameReceived(null);
        }
    }
}
