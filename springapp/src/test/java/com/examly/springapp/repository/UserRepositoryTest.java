package com.examly.springapp.repository;

import com.examly.springapp.model.Role;
import com.examly.springapp.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindByEmail() {
        User user = User.builder()
                .name("Test User")
                .email("testrepo@example.com")
                .passwordHash("hash123")
                .role(Role.USER)
                .storageUsed(0L)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("testrepo@example.com");
        assertTrue(found.isPresent());
        assertEquals("Test User", found.get().getName());
        assertTrue(userRepository.existsByEmail("testrepo@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }
}
