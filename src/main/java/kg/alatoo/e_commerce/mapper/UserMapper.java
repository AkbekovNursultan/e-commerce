package kg.alatoo.e_commerce.mapper;

import kg.alatoo.e_commerce.dto.user.CustomerInfoResponse;
import kg.alatoo.e_commerce.dto.user.WorkerInfoResponse;
import kg.alatoo.e_commerce.entity.Customer;
import kg.alatoo.e_commerce.entity.Worker;

public interface UserMapper {
    CustomerInfoResponse toDto(Customer customer);
    WorkerInfoResponse toDtoWorker(Worker worker);
}
