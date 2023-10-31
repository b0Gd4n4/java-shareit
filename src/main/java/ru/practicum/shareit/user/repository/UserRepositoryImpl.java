package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EmailExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private long nextId = 0;

    private final Map<Long, User> userMap = new HashMap<>();
    private final Set<String> emails = new HashSet<>();

    @Override
    public User get(long userId) {

        if (userMap.containsKey(userId)) {
            return userMap.get(userId);
        } else {
            throw new NotFoundException(User.class, "User id " + userId + " not found.");
        }
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(userMap.values());
    }


    @Override
    public User add(User user) {
        final String email = user.getEmail();
        if (emails.contains(email)) {
		throw new EmailExistException("Email: " + email + " already exists");
        }
        long id = getNextFreeId();
        user.setId(id);
        userMap.put(user.getId(), user);
        emails.add(email);
        return user;

    }



    @Override
    public User update(User user, long userId) {
        emails.clear();
        User newUser = userMap.get(userId);
        final String email = user.getEmail();

        if (user.getName() != null) {
            newUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            for (User userCheckEmail : getAll()) {
                if (userCheckEmail.getEmail().equals(user.getEmail()) && userCheckEmail.getId() != userId) {
                    throw new EmailExistException("there is already a user with an email " + user.getEmail());
                }
            }

            newUser.setEmail(user.getEmail());
        }

        userMap.put(userId, newUser);
        emails.add(email);
        return userMap.get(user.getId());
    }

    @Override
    public void delete(User user) {

        if (!userMap.containsValue(user)) {
            throw new NotFoundException(User.class, "User id " + user.getId() + " not found.");
        }

        userMap.remove(user.getId());
    }

    public Long getNextFreeId() {
        return ++nextId;
    }
}