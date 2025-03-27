package com.ecommerce.electronicstore.service.impl;

import com.ecommerce.electronicstore.dto.UserDto;
import com.ecommerce.electronicstore.entity.User;
import com.ecommerce.electronicstore.exception.ResourceNotFoundException;
import com.ecommerce.electronicstore.repository.UserRepository;
import com.ecommerce.electronicstore.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        String userId = UUID.randomUUID().toString();
        userDto.setUserId(userId);
        User user = modelMapper.map(userDto, User.class);
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDto.class);
    }


    @Override
    public UserDto updateUser(UserDto userDto, String userId) {
        User oldUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("For Update, the User with ID " + userId + " not found"));
        oldUser.setName(userDto.getName());
        oldUser.setGender(userDto.getGender());
        oldUser.setAbout(userDto.getAbout());
        oldUser.setEmail(userDto.getEmail());
        oldUser.setPassword(userDto.getPassword());
        oldUser.setImageName(userDto.getImageName());

        User updatedUser=userRepository.save(oldUser);
        return modelMapper.map(updatedUser,UserDto.class);
    }

    @Override
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
        this.userRepository.delete(user);

    }

    @Override
    public List<UserDto> getAllUser() {
        List<User> users = this.userRepository.findAll();

        List<UserDto> userDto = users.stream()
                .map(user -> modelMapper.map(user, UserDto.class)) // Correct mapping
                .collect(Collectors.toList());
        return userDto;
    }

    @Override
    public UserDto getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
        return modelMapper.map(user, UserDto.class);
    }


    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("Email"+email+"Not Found"));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public List<UserDto> searchUser(String keyword) {
        List<User> users = this.userRepository.findByNameContaining(keyword);
        List<UserDto> userDto = users.stream()
                .map(user -> modelMapper.map(user, UserDto.class)) // Correct mapping
                .collect(Collectors.toList());
        return userDto;

    }
}
