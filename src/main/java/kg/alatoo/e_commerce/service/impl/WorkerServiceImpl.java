package kg.alatoo.e_commerce.service.impl;

import kg.alatoo.e_commerce.dto.user.ChangePasswordRequest;
import kg.alatoo.e_commerce.dto.user.WorkerInfoResponse;
import kg.alatoo.e_commerce.entity.User;
import kg.alatoo.e_commerce.entity.Worker;
import kg.alatoo.e_commerce.enums.Role;
import kg.alatoo.e_commerce.exception.BadRequestException;
import kg.alatoo.e_commerce.mapper.UserMapper;
import kg.alatoo.e_commerce.repository.UserRepository;
import kg.alatoo.e_commerce.service.AuthService;
import kg.alatoo.e_commerce.service.WorkerService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class WorkerServiceImpl implements WorkerService{
    private UserMapper userMapper;
    private UserRepository userRepository;
    private AuthService authService;
    private PasswordEncoder encoder;

    @Override
    public WorkerInfoResponse workerInfo(String token) {
        User user = authService.getUserFromToken(token);
        if(!user.getRole().equals(Role.WORKER))
            throw new BadRequestException("You can't do this.");
        Worker worker = user.getWorker();
        return userMapper.toDtoWorker(worker);
    }

    @Override
    public void changePassword(String token, ChangePasswordRequest request) {
        User user = authService.getUserFromToken(token);
        if(!user.getRole().equals(Role.WORKER))
            throw new BadRequestException("You have no permission");
        if(!encoder.matches(request.getCurrentPassword(), (user.getPassword())))
            throw new BadRequestException("Incorrect password.");
        if(request.getNewPassword().equals(request.getCurrentPassword()))
            throw new BadRequestException("This password is already in use!.");
        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void update(String token, WorkerInfoResponse request) {
        User user = authService.getUserFromToken(token);
        Optional<User> user1 = userRepository.findByUsername(request.getUsername());
        if(!user.getRole().equals(Role.WORKER))
            throw new BadRequestException("You can't do this.");
        if(request.getUsername() != null){
            if(user1.isEmpty() || user1.get() == user)
                user.setUsername(request.getUsername());
            else
                throw new BadRequestException("This username already in use!");
        }

        userRepository.save(user);
    }
}
