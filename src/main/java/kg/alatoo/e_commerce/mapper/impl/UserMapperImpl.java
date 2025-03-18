package kg.alatoo.e_commerce.mapper.impl;

import kg.alatoo.e_commerce.dto.user.CustomerInfoResponse;
import kg.alatoo.e_commerce.dto.user.WorkerInfoResponse;
import kg.alatoo.e_commerce.entity.Customer;
import kg.alatoo.e_commerce.entity.User;
import kg.alatoo.e_commerce.entity.Worker;
import kg.alatoo.e_commerce.mapper.UserMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {
    @Override
    public CustomerInfoResponse toDto(Customer customer) {
        CustomerInfoResponse response = new CustomerInfoResponse();
        User user = customer.getUser();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setCountry(customer.getCountry());
        response.setAddress(customer.getAddress());
        response.setCity(customer.getCity());
        response.setZipCode(customer.getZipCode());
        response.setPhone(customer.getPhone());
        response.setAdditionalInfo(customer.getAdditionalInfo());
        response.setBalance(customer.getBalance());

        return response;
    }

    @Override
    public WorkerInfoResponse toDtoWorker(Worker worker) {
        WorkerInfoResponse response = new WorkerInfoResponse();
        User user = worker.getUser();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        return response;
    }
}
