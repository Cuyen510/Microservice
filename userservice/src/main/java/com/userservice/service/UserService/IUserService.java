package com.userservice.service.UserService;

import com.userservice.dto.AddUserDTO;
import com.userservice.dto.UserDTO;
import com.userservice.exceptions.DataNotFoundException;
import com.userservice.model.User;
import com.userservice.response.UserLoginResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IUserService {
    User registerUser(UserDTO userDTO);

    User addUser(AddUserDTO addUserDTO);
    Page<User> getAllUsers(PageRequest pageRequest);
    String getUserAddress(Long id) throws DataNotFoundException;
    User getUserDetails(Long id) throws DataNotFoundException;
    UserLoginResponse login(String phoneNumber, String password) throws DataNotFoundException;
    User updateUser(Long id, UserDTO userDTO) throws DataNotFoundException;
    void DeleteUser(Long id);
}
