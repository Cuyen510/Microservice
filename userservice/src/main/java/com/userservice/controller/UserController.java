package com.userservice.controller;

import com.userservice.dto.UserLoginDTO;
import com.userservice.model.User;
import com.userservice.exceptions.DataNotFoundException;
import com.userservice.dto.UserDTO;
import com.userservice.response.UserAddressResponse;
import com.userservice.response.UserLoginResponse;
import com.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> addUser(@RequestBody UserDTO userDTO, BindingResult result){
        try {
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            return ResponseEntity.ok().body(userService.addUser(userDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO userLoginDTO, BindingResult result) {
        try {
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            return ResponseEntity.ok().body(userService.login(userLoginDTO.getPhoneNumber(), userLoginDTO.getPassword()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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

    @GetMapping("/userAddress")
    public ResponseEntity<?> getUserAddress(@RequestParam Long userId) throws DataNotFoundException {
            return ResponseEntity.ok().body(UserAddressResponse.builder().address(userService.getUserAddress(userId)).build());
    }

    @GetMapping("")
    public ResponseEntity<?> getAllUsers(HttpServletRequest request) {
            String role = request.getHeader("X-auth-role");
            if(!role.equals("admin"))
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UnAuthorize");
            return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,@RequestBody UserDTO userDTO, HttpServletRequest request, BindingResult result) throws DataNotFoundException {
        try {
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            String role = request.getHeader("X-auth-role");
            Long userId = Long.valueOf(request.getHeader("X-auth-userId"));
            if(!role.equals("admin")|| role == null)
                if(!userId.equals(id)){
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UnAuthorize");
                }
            return ResponseEntity.ok().body(userService.updateUser(id, userDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, HttpServletRequest request){
            String role = request.getHeader("X-auth-role");
            if(!role.equals("admin")|| role == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UnAuthorize");
            userService.DeleteUser(id);
            return ResponseEntity.ok().body("User deleted");
    }
}
