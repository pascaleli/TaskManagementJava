package dev.pascal.TaskManagementSystem.user;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    // Implementing the services right here , i will be handling user password so in addition to the Entity dtat
    //interface i also need decoder
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO createUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Override
    public UserDTO getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(()-> new UsernameNotFoundException("User Not found"));

        return convertToDTO(user);
    }

    @Override
    public UserDTO convertToDTO(User user) {
        UserDTO dto =new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());

        return dto;
    }



    @Override
    public User getUserByUsername(String username) {

        return userRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("User not found"));
    }
}
