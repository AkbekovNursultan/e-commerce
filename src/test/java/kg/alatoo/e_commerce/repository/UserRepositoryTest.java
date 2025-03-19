package kg.alatoo.e_commerce.repository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import kg.alatoo.e_commerce.repository.*;
import kg.alatoo.e_commerce.entity.*;
import kg.alatoo.e_commerce.enums.*;

import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY) // Or Replace.NONE if you want to use a real database
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByUsername() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setRole(Role.CUSTOMER);
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByUsername("testuser");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void testSaveUser() {
        User user = new User();
        user.setId(2L);
        user.setUsername("newuser");
        user.setPassword("newpassword");
        user.setEmail("new@example.com");
        user.setRole(Role.WORKER);

        User savedUser = userRepository.save(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("newuser");
    }

    @Test
    void testDeleteUser() {
        User user = new User();
        user.setId(3L);
        user.setUsername("todelete");
        user.setPassword("password");
        user.setEmail("delete@example.com");
        user.setRole(Role.CUSTOMER);
        userRepository.save(user);

        userRepository.delete(user);
        Optional<User> deletedUser = userRepository.findById(3L);

        assertThat(deletedUser).isEmpty();
    }

    @Test
    void testFindById() {
        User user = new User();
        user.setId(4L);
        user.setUsername("findme");
        user.setPassword("password");
        user.setEmail("findme@example.com");
        user.setRole(Role.CUSTOMER);
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findById(4L);

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("findme");
    }

    @Test
    void testFindAllUsers() {
        // Given
        User user1 = new User();
        user1.setId(5L);
        user1.setUsername("user1");
        user1.setPassword("password");
        user1.setEmail("user1@example.com");
        user1.setRole(Role.CUSTOMER);
        userRepository.save(user1);

        User user2 = new User();
        user2.setId(6L);
        user2.setUsername("user2");
        user2.setPassword("password");
        user2.setEmail("user2@example.com");
        user2.setRole(Role.WORKER);
        userRepository.save(user2);

        List<User> allUsers = userRepository.findAll();

        assertThat(allUsers).hasSizeGreaterThanOrEqualTo(2);
        assertThat(allUsers).anyMatch(user -> user.getUsername().equals("user1"));
        assertThat(allUsers).anyMatch(user -> user.getUsername().equals("user2"));
    }
}