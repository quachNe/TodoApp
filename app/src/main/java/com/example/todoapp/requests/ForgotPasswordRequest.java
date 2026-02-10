package com.example.todoapp.requests;

public class ForgotPasswordRequest {
        private String email;
        private String username;

        public ForgotPasswordRequest(String email, String username) {
            this.email = email;
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public String getUsername() {
            return username;
        }
}
