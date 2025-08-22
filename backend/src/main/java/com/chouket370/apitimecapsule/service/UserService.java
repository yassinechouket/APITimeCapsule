package com.chouket370.apitimecapsule.service;



import com.chouket370.apitimecapsule.models.User;
import com.chouket370.apitimecapsule.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class UserService {

    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalStateException(id + " not found"));
    }
    public User findByUsername(String name) {
        return userRepository.findByUsername(name).orElseThrow(() -> new IllegalStateException(name + " not found"));
    }
    public User save(User user) {
        return userRepository.save(user);
    }
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }



}