package com.ade.chat.services;

import com.ade.chat.entities.Chat;
import com.ade.chat.entities.User;
import com.ade.chat.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class UserService {

    private final UserRepository userRepo;

    @Autowired
    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User getUserByIdOrException(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new IllegalStateException("No user with given id:" + id));
    }

    public void createUser(User newUser) {
        Boolean hasSuchUser = userRepo.existsByName(newUser.getName());
        if (hasSuchUser)
            throw new IllegalStateException("Name:" + newUser.getName() + " is taken already");

        userRepo.save(newUser);
    }

    public List<Chat> getUserChats(Long id) {
        return List.copyOf(getUserByIdOrException(id).getChats());
    }

    public User getUserByNameOrCreate(String name) {
        Optional<User> userOptional = userRepo.findByName(name);
        if (userOptional.isPresent())
            return userOptional.get();

        User newUser = new User(name);
        userRepo.save(newUser);
        return newUser;
    }
}
