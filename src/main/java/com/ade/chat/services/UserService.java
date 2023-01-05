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

    public void createUser(User newUser) {
        Optional<User> userByName = userRepo.findByName(newUser.getName());
        if (userByName.isPresent()) {
            throw new IllegalStateException("This name has been already taken");
        }

        userRepo.save(newUser);
    }

    public List<Chat> getUserChats(Long id) {
        Optional<User> userOptional = userRepo.findById(id);
        if (userOptional.isEmpty()) {
            throw new IllegalStateException("No user with such id:" + id);
        }
        return List.copyOf(userOptional.get().getChats());
    }
}
