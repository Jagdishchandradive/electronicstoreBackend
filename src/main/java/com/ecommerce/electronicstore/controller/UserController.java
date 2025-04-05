package com.ecommerce.electronicstore.controller;

import com.ecommerce.electronicstore.dto.ApiResponse;
import com.ecommerce.electronicstore.dto.ImageResponse;
import com.ecommerce.electronicstore.dto.PageableResponse;
import com.ecommerce.electronicstore.dto.UserDto;
import com.ecommerce.electronicstore.service.FileService;
import com.ecommerce.electronicstore.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private Logger logger= LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final FileService fileService;

    @Value("${user.profile.image.path}")
    private String imageUploadPath;

    @Autowired
    public UserController(UserService userService,FileService fileService) {
        this.userService = userService;
        this.fileService=fileService;
    }

    @PostMapping("/create-user")
    public ResponseEntity<UserDto>createUser(@Valid @RequestBody UserDto userDto){
        logger.info("Creating new user with data: {}", userDto);
        UserDto createUserDto = this.userService.createUser(userDto);
        logger.info("User created successfully with ID: {}", createUserDto);
        return new ResponseEntity<>(createUserDto, HttpStatus.CREATED);
    }
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserDto userDto, @PathVariable String userId) {
        logger.info("Updating user with ID: {}", userId);
        UserDto userDto1 = this.userService.updateUser(userDto, userId);
        logger.info("User with ID: {} updated successfully.", userId);
        return new ResponseEntity<>(userDto1,HttpStatus.OK);
    }
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable String userId){
        logger.warn("Deleting user with ID: {}", userId);
        userService.deleteUser(userId);
        logger.info("User with ID: {} deleted successfully.", userId);
        ApiResponse message= ApiResponse
                .builder()
                .message("User Deleted")
                .success(true)
                .status(HttpStatus.OK)
                .build();
        return new ResponseEntity<ApiResponse>(message,HttpStatus.OK);
    }

    @GetMapping("/all-users")
    public ResponseEntity<PageableResponse<UserDto>>getAllUsers(
            @RequestParam(value="pageNumber",defaultValue = "0",required = false) int pageNumber,
            @RequestParam(value = "pageSize",defaultValue = "10",required=false) int pageSize,
            @RequestParam(value="sortBy",defaultValue = "name",required = false) String sortBy,
            @RequestParam(value = "sortDir",defaultValue = "asc",required=false) String sortDir
    ){
        logger.info("Fetching all users: pageNumber={}, pageSize={}, sortBy={}, sortDir={}",
                pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(userService.getAllUser(pageNumber,pageSize,sortBy,sortDir),HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto>getUser(@PathVariable String userId){
        logger.info("Fetching user by ID: {}", userId);
        return new ResponseEntity<>(userService.getUserById(userId),HttpStatus.OK);
    }
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto>getUserByEmail(@PathVariable String email){
        logger.info("Fetching user by email: {}", email);
        return new ResponseEntity<>(userService.getUserByEmail(email),HttpStatus.OK);
    }
    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<UserDto>>searchByKeyword(@PathVariable String keyword){
        logger.info("Searching users by keyword: {}", keyword);
        List<UserDto> result = userService.searchUser(keyword);
        logger.info("Found {} users for keyword: {}", result.size(), keyword);
        return new ResponseEntity<>(result, HttpStatus.FOUND);
    }
    //upload user image
    @PostMapping("/image/{userId}")
    public ResponseEntity<ImageResponse>uploadUserImage(
            @RequestParam("userImage")MultipartFile image,
            @PathVariable String userId) throws IOException {
        logger.info("Uploading image for user ID: {}", userId);
        String imageName = fileService.UploadFile(image, imageUploadPath);
        logger.info("Image uploaded successfully: {}", imageName);
        UserDto user=userService.getUserById(userId);
        user.setImageName(imageName);
        UserDto userDto=userService.updateUser(user,userId);
        ImageResponse imageResponse=ImageResponse.builder()
                .imageName(imageName).success(true)
                .status(HttpStatus.CREATED)
                .build();
        return new ResponseEntity<>(imageResponse,HttpStatus.CREATED);
    }
    // serve user image
    @GetMapping("/image/{userId}")
    public void serveUserimage(@PathVariable String userId, HttpServletResponse response) throws IOException {
        logger.info("Serving image for user ID: {}", userId);
        UserDto userById = userService.getUserById(userId);
        logger.info("User image name:{}",userById.getImageName());
        InputStream resource = fileService.getResource(imageUploadPath, userById.getImageName());

        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource,response.getOutputStream());
    }

}
