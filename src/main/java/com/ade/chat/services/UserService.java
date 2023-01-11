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

    public List<User> getUsers() {
        return userRepo.findAll();
    }

    public User getUserByIdOrException(Long id) {
        Optional<User> userOptional = userRepo.findById(id);
        if (userOptional.isEmpty()) {
            throw new IllegalStateException("No user with given id");
        }
        return userOptional.get();
    }

    public void createUser(User newUser) {
        Optional<User> userByName = userRepo.findByName(newUser.getName());
        if (userByName.isPresent()) {
            throw new IllegalStateException("This name has been already taken");
        }
        userRepo.save(newUser);
    }

    public List<Chat> getUserChats(Long id) {
        return List.copyOf(getUserByIdOrException(id).getChats());
    }

    public User getUserByNameOrCreate(String name) {
        Optional<User> userOptional = userRepo.findByName(name);
        if (userOptional.isEmpty()) {
            User newUser = new User(name);
            userRepo.save(newUser);
            return newUser;
        }
        return userOptional.get();
    }
}
