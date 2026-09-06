package ru.vsu.zhertvydedlina.aiservice.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.vsu.zhertvydedlina.aiservice.user.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    @Query(nativeQuery = true, value = "SELECT * FROM users WHERE username = :input OR u.email = :input")
    Optional<User> findByUsernameOrEmail(@Param("input") String usernameOrEmail);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
