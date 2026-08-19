package com.henry.auth.services;

import com.henry.auth.dto.LoginRequest;
import com.henry.auth.dto.TokenResponse;

public interface AuthService {

    TokenResponse autenticar(LoginRequest request) throws Exception;
}
