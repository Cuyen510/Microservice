package com.userservice.controller;

import com.userservice.dto.UserLoginDTO;
import com.userservice.model.User;
import com.userservice.exceptions.DataNotFoundException;
import com.userservice.dto.UserDTO;
import com.userservice.response.UserLoginResponse;
import com.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<?> getUserDetails(@PathVariable Long id,  HttpServletRequest request) throws DataNotFoundException {
        String role = request.getHeader("X-auth-role");
        Long userId = Long.valueOf(request.getHeader("X-auth-userId"));
        if(!role.equals("admin")|| role == null)
            if(!userId.equals(id)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UnAuthorize");
            }
        return ResponseEntity.ok().body(userService.getUserDetails(id));
    }

    @GetMapping("")
    public ResponseEntity<?> getAllUsers(HttpServletRequest request) throws DataNotFoundException {
        String role = request.getHeader("X-auth-role");
        if(!role.equals("admin")|| role == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UnAuthorize");
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,@RequestBody UserDTO userDTO, HttpServletRequest request) throws DataNotFoundException {
        String role = request.getHeader("X-auth-role");
        Long userId = Long.valueOf(request.getHeader("X-auth-userId"));
        if(!role.equals("admin")|| role == null)
            if(!userId.equals(id)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UnAuthorize");
            }
        return ResponseEntity.ok().body(userService.updateUser(id, userDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id, HttpServletRequest request){
        String role = request.getHeader("X-auth-role");
        if(!role.equals("admin")|| role == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UnAuthorize");
        userService.DeleteUser(id);
        return ResponseEntity.ok().body("User deleted");
    }
}
