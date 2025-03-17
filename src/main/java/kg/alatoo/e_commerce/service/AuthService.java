package kg.alatoo.e_commerce.service;

import kg.alatoo.e_commerce.dto.user.UserLoginRequest;
import kg.alatoo.e_commerce.dto.user.UserLoginResponse;
import kg.alatoo.e_commerce.dto.user.UserRegisterRequest;
import kg.alatoo.e_commerce.entity.User;


public interface AuthService {
    void register(UserRegisterRequest userRegisterRequest);

    UserLoginResponse login(UserLoginRequest userLoginRequest);

    User getUserFromToken(String token);

}
