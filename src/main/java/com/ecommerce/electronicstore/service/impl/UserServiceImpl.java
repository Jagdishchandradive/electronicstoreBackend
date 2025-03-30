package com.ecommerce.electronicstore.service.impl;

import com.ecommerce.electronicstore.dto.PageableResponse;
import com.ecommerce.electronicstore.dto.UserDto;
import com.ecommerce.electronicstore.entity.User;
import com.ecommerce.electronicstore.exception.ResourceNotFoundException;
import com.ecommerce.electronicstore.helper.Helper;
import com.ecommerce.electronicstore.repository.UserRepository;
import com.ecommerce.electronicstore.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${user.profile.image.path}")
    private String imagePath;

    private Logger logger= LoggerFactory.getLogger(UserServiceImpl.class);

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

//    @Override
//    public void deleteUser(String userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
//        String fullImagePath = imagePath + user.getImageName();
//        try {
//            Path path = Paths.get(fullImagePath);
//            Files.delete(path);
//        }catch(NoSuchFileException ex){
//            logger.info("User Image Not Found In Folder");
//            ex.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        this.userRepository.delete(user);
//
//    }
@Override
public void deleteUser(String userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
    String fullImagePath = imagePath + user.getImageName();
    Path path = Paths.get(fullImagePath);
    try {
        File file = new File(fullImagePath);
        if (file.exists()) {
            logger.info("Attempting to delete user image: " + fullImagePath);
            boolean deleted = file.delete();
            if (!deleted) {
                logger.warn("Direct delete failed, retrying after GC hint...");
                System.gc();
                Thread.sleep(100);
                deleted = file.delete();
            }
            if (!deleted) {
                logger.error("Failed to delete image file: " + fullImagePath);
            }
        } else {
            logger.info("User image not found in folder, skipping deletion.");
        }
    } catch (InterruptedException e) {
        logger.error("Error deleting file: " + fullImagePath, e);
    }
    userRepository.delete(user);
}


    @Override
    public PageableResponse<UserDto> getAllUser(int pageNumber, int pageSize,
                                                String sortBy, String sortDir)
    {
        Sort sort= (sortDir.equalsIgnoreCase("desc"))?(Sort.by(sortBy).descending()):(Sort.by(sortBy).ascending());
        Pageable pageable= PageRequest.of(pageNumber, pageSize,sort);
        Page<User> page = this.userRepository.findAll(pageable);
        PageableResponse<UserDto> response = Helper.getPageableResponse(page, UserDto.class);
        return response;
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
