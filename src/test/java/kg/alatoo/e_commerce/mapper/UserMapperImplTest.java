package kg.alatoo.e_commerce.mapper;

import kg.alatoo.e_commerce.dto.user.CustomerInfoResponse;
import kg.alatoo.e_commerce.dto.user.WorkerInfoResponse;
import kg.alatoo.e_commerce.entity.Customer;
import kg.alatoo.e_commerce.entity.User;
import kg.alatoo.e_commerce.entity.Worker;
import kg.alatoo.e_commerce.mapper.impl.UserMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserMapperImplTest {

    private UserMapperImpl userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapperImpl();
    }

    @Test
    void toDto_ShouldMapCustomerToCustomerInfoResponse() {
        Customer customer = new Customer();
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        customer.setUser(user);
        customer.setCountry("Test Country");
        customer.setAddress("Test Address");
        customer.setCity("Test City");
        customer.setZipCode("12345");
        customer.setPhone("123-456-7890");
        customer.setAdditionalInfo("Additional Info");
        customer.setBalance(100.0);

        CustomerInfoResponse response = userMapper.toDto(customer);

        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        assertEquals(100.0, response.getBalance());
        assertEquals("Test Country", response.getCountry());
        assertEquals("Test Address", response.getAddress());
        assertEquals("Test City", response.getCity());
        assertEquals("12345", response.getZipCode());
        assertEquals("123-456-7890", response.getPhone());
        assertEquals("Additional Info", response.getAdditionalInfo());
    }

    @Test
    void toDtoWorker_ShouldMapWorkerToWorkerInfoResponse() {
        Worker worker = new Worker();
        User user = new User();
        user.setId(2L);
        user.setUsername("testworker");
        user.setEmail("worker@example.com");
        worker.setUser(user);

        WorkerInfoResponse response = userMapper.toDtoWorker(worker);

        assertEquals(2L, response.getId());
        assertEquals("testworker", response.getUsername());
        assertEquals("worker@example.com", response.getEmail());
    }
}