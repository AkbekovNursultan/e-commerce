package kg.alatoo.e_commerce.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kg.alatoo.e_commerce.dto.user.UserLoginRequest;
import kg.alatoo.e_commerce.dto.user.UserLoginResponse;
import kg.alatoo.e_commerce.dto.user.UserRegisterRequest;
import kg.alatoo.e_commerce.entity.User;

import java.io.IOException;


public interface AuthService {
    UserLoginResponse convertToResponse(User user);

    void register(UserRegisterRequest userRegisterRequest);

    UserLoginResponse login(UserLoginRequest userLoginRequest);

    User getUserFromToken(String token);

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
