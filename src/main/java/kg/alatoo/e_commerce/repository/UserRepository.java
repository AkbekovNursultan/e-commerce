package kg.alatoo.e_commerce.repository;

import kg.alatoo.e_commerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByVerificationCode(String code);

    Optional<User> findByGithubId(String githubId);
}
