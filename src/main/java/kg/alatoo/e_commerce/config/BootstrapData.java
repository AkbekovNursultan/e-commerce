package kg.alatoo.e_commerce.config;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import kg.alatoo.e_commerce.entity.Customer;
import kg.alatoo.e_commerce.entity.User;
import kg.alatoo.e_commerce.entity.Worker;
import kg.alatoo.e_commerce.enums.Role;
import kg.alatoo.e_commerce.repository.CustomerRepository;
import kg.alatoo.e_commerce.repository.UserRepository;
import kg.alatoo.e_commerce.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class BootstrapData implements CommandLineRunner {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        loadUsers();
        loadWorkers();
        loadCustomers();
    }

    private void loadUsers() throws IOException, CsvException {
        CSVReader reader = new CSVReader(new FileReader(new ClassPathResource("data/users.csv").getFile()));
        List<String[]> users = reader.readAll();
        for (String[] userData : users) {
            Long id = Long.valueOf(userData[0]);
            String username = userData[1];
            String password = userData[2];
            String email = userData[3];
            Role role = Role.valueOf(userData[4]);
            String encodedPassword = passwordEncoder.encode(password);

            User user = new User();
            user.setId(id);
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(encodedPassword);
            user.setRole(role);
            userRepository.save(user);
        }
    }

    private void loadWorkers() throws IOException, CsvException {

        CSVReader reader = new CSVReader(new FileReader(new ClassPathResource("data/workers.csv").getFile()));
        List<String[]> workers = reader.readAll();
        for (String[] workerData : workers) {
            Long userId = Long.valueOf(workerData[0]);

            Worker worker = new Worker();
            Optional<User> user = userRepository.findById(userId);
            worker.setUser(user.get());
            workerRepository.save(worker);
        }
    }

    private void loadCustomers() throws IOException, CsvException {
        CSVReader reader = new CSVReader(new FileReader(new ClassPathResource("data/customers.csv").getFile()));
        List<String[]> customers = reader.readAll();

        for (String[] customerData : customers) {
            Long userId = Long.valueOf(customerData[7]);
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                System.err.println("User with ID " + userId + " not found! Skipping...");
                continue;
            }

            Customer customer = new Customer();
            customer.setCountry(customerData[0]);
            customer.setCity(customerData[1]);
            customer.setAddress(customerData[2]);
            customer.setAdditionalInfo(customerData[3]);
            customer.setPhone(customerData[4]);
            customer.setZipCode(customerData[5]);
            customer.setBalance(Double.parseDouble(customerData[6]));
            customer.setUser(userOpt.get());

            customerRepository.save(customer);
        }
    }
//    private void loadCategories() throws IOException, CsvException {
//        CSVReader reader = new CSVReader(new FileReader(new ClassPathResource("categories.csv").getFile()));
//        List<String[]> categories = reader.readAll();
//        for (String[] categoryData : categories) {
//            String categoryName = categoryData[0];
//            Category category = new Category();
//            category.setName(categoryName);
//            categoryRepository.save(category);
//        }
//    }

//    private void loadProducts() throws IOException, CsvException {
//        CSVReader reader = new CSVReader(new FileReader(new ClassPathResource("products.csv").getFile()));
//        List<String[]> products = reader.readAll();
//        title,price,code,description,colors,category,quantity
//        for (String[] productData : products) {
//            String productName = productData[0];
//            Double price = Double.valueOf(productData[1]);
//            String code = productData[2];
//            String description = productData[3];
//            String colors = productData[4];
//            String category = productData[5];
//
//            Long categoryId = Long.valueOf(productData[2]);
//
//            Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found"));
//            Product product = new Product();
//            product.setTitle(productName);
//            product.setPrice(price);
//            product.setCategory(category);
//            productRepository.save(product);
//        }
//    }

}
