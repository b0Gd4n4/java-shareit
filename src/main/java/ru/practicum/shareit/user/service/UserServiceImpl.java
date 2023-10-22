package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public User addUser(User user) {
        return repository.add(user);
    }

    @Override
    public User updateUser(User user, long userId) {

        repository.get(userId);
        user.setId(userId);
        return repository.update(user, userId);
    }

    @Override
    public void deleteUser(long userId) {
        repository.delete(repository.get(userId));
    }

    @Override
    public User getUserById(long userId) {
        return repository.get(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return repository.getAll();
    }
}