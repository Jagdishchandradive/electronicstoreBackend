package com.ecommerce.electronicstore.controller;

import com.ecommerce.electronicstore.dto.ApiResponse;
import com.ecommerce.electronicstore.dto.UserDto;
import com.ecommerce.electronicstore.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/create-user")
    public ResponseEntity<UserDto>createUser(@Valid @RequestBody UserDto userDto){
        UserDto createUserDto = this.userService.createUser(userDto);
        return new ResponseEntity<>(createUserDto, HttpStatus.CREATED);
    }
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserDto userDto, @PathVariable String userId) {
        UserDto userDto1 = this.userService.updateUser(userDto, userId);
        return new ResponseEntity<>(userDto1,HttpStatus.OK);
    }
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        ApiResponse message= ApiResponse
                .builder()
                .message("User Deleted")
                .success(true)
                .status(HttpStatus.OK)
                .build();
        return new ResponseEntity<ApiResponse>(message,HttpStatus.OK);
    }

    @GetMapping("/all-users")
    public ResponseEntity<List<UserDto>>getAllUsers(
            @RequestParam(value="pageNumber",defaultValue = "0",required = false) int pageNumber,
            @RequestParam(value = "pageSize",defaultValue = "10",required=false) int pageSize
    ){
       return new ResponseEntity<>(userService.getAllUser(pageNumber,pageSize),HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto>getUser(@PathVariable String userId){
        return new ResponseEntity<>(userService.getUserById(userId),HttpStatus.OK);
    }
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto>getUserByEmail(@PathVariable String email){
        return new ResponseEntity<>(userService.getUserByEmail(email),HttpStatus.OK);
    }
    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<UserDto>>searchByKeyword(@PathVariable String keyword){
        return new ResponseEntity<>(userService.searchUser(keyword),HttpStatus.FOUND);
    }
}
