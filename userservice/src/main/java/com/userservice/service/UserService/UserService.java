package com.userservice.service.UserService;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.userservice.model.User;
import com.userservice.repository.RoleRepository;
import com.userservice.repository.UserRepository;
import com.userservice.exceptions.DataNotFoundException;
import com.userservice.dto.UserDTO;
import com.userservice.response.UserLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.createCart}")
    private String createCart;

    @Value("${security.jwt.secret}")
    private String jwtSecret;


    public User addUser(UserDTO userDTO){
        String phoneNumber = userDTO.getPhoneNumber();
        if(userRepository.existsByPhoneNumber(phoneNumber)){
            throw new DataIntegrityViolationException("phone number already exists");
        }
        User user = UserDTO.dtoToEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setActive(true);
        user.setRole(roleRepository.findByName("user"));

        userRepository.save(user);
        kafkaTemplate.send(createCart, String.valueOf(user.getId()));
        return user;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public String getUserAddress(Long id) throws DataNotFoundException {
        User user = userRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Cant find user"));
        return user.getAddress();
    }
    public User getUserDetails(Long id) throws DataNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Can't find user with id:" + id));
    }

    public UserLoginResponse login(String phoneNumber, String password) throws DataNotFoundException {
        User user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(()->new DataNotFoundException("Wrong phone number or password"));
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new DataNotFoundException("Wrong phone number or password");
        }else{
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
            String access_token = JWT.create()
                    .withSubject(user.getPhoneNumber())
                    .withClaim("role", user.getRole().getName())
                    .withClaim("userId", user.getId().toString())
                    .withExpiresAt(new Date(System.currentTimeMillis()+ (24*60*60*1000)))
                    .sign(algorithm);


//            String refresh_token = JWT.create()
//                    .withSubject(user.getPhoneNumber())
//                    .withClaim("role", user.getRole().getName())
//                    .withClaim("userId", user.getId().toString())
//                    .withExpiresAt(new Date(System.currentTimeMillis()+ (7*24*60*60*1000)))
//                    .sign(algorithm);

            return UserLoginResponse.builder()
                    .access_token(access_token)
                    .user(user)
                    .build();
        }
    }

    public User updateUser(Long id, UserDTO userDTO) throws DataNotFoundException {
        User user =  userRepository.findById(id).orElseThrow(()->new DataNotFoundException("Cant find user"));
        BeanUtils.copyProperties(userDTO, user);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return userRepository.save(user);
    }

    public void DeleteUser(Long id){
        userRepository.deleteById(id);
    }
}
