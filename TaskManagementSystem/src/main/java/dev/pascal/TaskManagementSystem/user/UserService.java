package dev.pascal.TaskManagementSystem.user;

import org.springframework.stereotype.Service;

@Service
public interface UserService {

    //These are all the services that i wish to render on the user entity

    UserDTO createUser(User user);
    UserDTO getUserById(Long id);
    UserDTO convertToDTO(User user);

    User getUserByUsername(String username);
}
