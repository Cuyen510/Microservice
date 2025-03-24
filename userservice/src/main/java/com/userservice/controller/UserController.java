package com.userservice.controller;

import com.userservice.dto.UserLoginDTO;
import com.userservice.model.User;
import com.userservice.exceptions.DataNotFoundException;
import com.userservice.dto.UserDTO;
import com.userservice.response.UserLoginResponse;
import com.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> addUser(@RequestBody UserDTO userDTO){
        return ResponseEntity.ok().body(userService.addUser(userDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginDTO userLoginDTO) throws DataNotFoundException {
        return ResponseEntity.ok().body(userService.login(userLoginDTO.getPhoneNumber(), userLoginDTO.getPassword()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserDetails(@PathVariable Long id) throws DataNotFoundException {
        return ResponseEntity.ok().body(userService.getUserDetails(id));
    }



    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id,@RequestBody UserDTO userDTO) throws DataNotFoundException {
        return ResponseEntity.ok().body(userService.updateUser(id, userDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        userService.DeleteUser(id);
        return ResponseEntity.ok().body("User deleted");
    }
    

}
