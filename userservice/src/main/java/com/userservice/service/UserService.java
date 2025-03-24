package com.userservice.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.userservice.model.User;
import com.userservice.repository.UserRepository;
import com.userservice.exceptions.DataNotFoundException;
import com.userservice.dto.UserDTO;
import com.userservice.response.UserLoginResponse;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.math.ec.rfc8032.Ed448;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;



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

    public UserLoginResponse login(String phoneNumber, String password) throws DataNotFoundException {
        User user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(()->new DataNotFoundException("Wrong phone number or password"));
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new DataNotFoundException("Wrong phone number or password");
        }else{
            Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
            String access_token = JWT.create()
                    .withSubject(user.getPhoneNumber())
                    .withExpiresAt(new Date(System.currentTimeMillis()+ (24*60*60*1000)))
                    .sign(algorithm);
            return UserLoginResponse.builder()
                    .access_token(access_token)
                    .build();
        }
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
