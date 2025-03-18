package kg.alatoo.e_commerce.service;

import kg.alatoo.e_commerce.dto.user.ChangePasswordRequest;
import kg.alatoo.e_commerce.dto.user.WorkerInfoResponse;

public interface WorkerService {
    WorkerInfoResponse workerInfo(String token);

    void changePassword(String token, ChangePasswordRequest request);

    void update(String token, WorkerInfoResponse request);
}
