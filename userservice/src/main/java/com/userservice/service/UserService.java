package com.userservice.service;

import com.userservice.model.User;
import com.userservice.repository.UserRepository;
import com.userservice.exceptions.DataNotFoundException;
import com.userservice.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User addUser(UserDTO userDTO){
        String phoneNumber = userDTO.getPhoneNumber();
        if(userRepository.existsByPhoneNumber(phoneNumber)){
            throw new DataIntegrityViolationException("phone number already exists");
        }
        User user = UserDTO.dtoToEntity(userDTO);
        user.setActive(true);
        return userRepository.save(user);
    }

    public User getUserDetails(Long id) throws DataNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Can't find user with id:" + id));
    }

    public String login(String phoneNumber, String password) throws DataNotFoundException {
        User user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(()->new DataNotFoundException("Wrong phone number or password"));
        if(!password.equals(user.getPassword())){
            throw new DataNotFoundException("Wrong phone number or password");
        }
        if(!user.isActive()){
            return "Account not available";
        }
        return "Login successful";
    }

    public User updateUser(Long id, UserDTO userDTO) throws DataNotFoundException {
        User user =  userRepository.findById(id).orElseThrow(()->new DataNotFoundException("Wrong phone number or password"));
        BeanUtils.copyProperties(userDTO, user);
        return userRepository.save(user);
    }

    public void DeleteUser(Long id){
        userRepository.deleteById(id);
    }
}
