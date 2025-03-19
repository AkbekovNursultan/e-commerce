package kg.alatoo.e_commerce.repository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import kg.alatoo.e_commerce.entity.Worker;
import kg.alatoo.e_commerce.entity.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class WorkerRepositoryTest {

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testSaveWorker() {

        User user = new User();
        user.setId(1L);
        entityManager.persistAndFlush(user);

        Worker worker = new Worker();
        worker.setUser(user);

        Worker savedWorker = workerRepository.save(worker);
        entityManager.persistAndFlush(savedWorker);

        assertThat(savedWorker.getId()).isNotNull();

        Worker retrievedWorker = entityManager.find(Worker.class, savedWorker.getId());
        assertThat(retrievedWorker.getUser()).isEqualTo(user);
    }

    @Test
    public void testFindWorkerById() {
        User user = new User();
        user.setId(1L);
        entityManager.persistAndFlush(user);

        Worker worker = new Worker();
        worker.setUser(user);
        entityManager.persistAndFlush(worker);

        Worker foundWorker = workerRepository.findById(worker.getId()).orElse(null);

        assertThat(foundWorker).isNotNull();
        assertThat(foundWorker.getUser()).isEqualTo(user);
    }

    @Test
    public void testFindAllWorkers() {
        User user1 = new User();
        user1.setId(1L);
        entityManager.persistAndFlush(user1);

        Worker worker1 = new Worker();
        worker1.setUser(user1);
        entityManager.persistAndFlush(worker1);

        User user2 = new User();
        user2.setId(2L);
        entityManager.persistAndFlush(user2);

        Worker worker2 = new Worker();
        worker2.setUser(user2);
        entityManager.persistAndFlush(worker2);

        List<Worker> workers = workerRepository.findAll();

        assertThat(workers).hasSize(2);
    }

    @Test
    public void testDeleteWorker() {
        User user = new User();
        user.setId(1L);
        entityManager.persistAndFlush(user);

        Worker worker = new Worker();
        worker.setUser(user);
        entityManager.persistAndFlush(worker);

        workerRepository.delete(worker);
        entityManager.flush();

        Worker deletedWorker = entityManager.find(Worker.class, worker.getId());
        assertThat(deletedWorker).isNull();
    }
}