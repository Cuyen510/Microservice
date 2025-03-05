package com.userservice.command.event;

import com.userservice.command.data.User;
import com.userservice.command.data.UserRepository;
import com.userservice.exceptions.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventsHandler {
    private final UserRepository userRepository;

    @EventHandler
    public void on(UserCreatedEvent event){
        if(userRepository.existsByPhoneNumber(event.getPhoneNumber())){
            throw  new DataIntegrityViolationException("Phone number already exist");
        }
        User user = new User();
        BeanUtils.copyProperties(event,user);
        userRepository.save(user);
    }
    @EventHandler
    public void on(UserUpdatedEvent event) throws DataNotFoundException {
        User user = userRepository.findById(event.getId()).orElseThrow(() -> new DataNotFoundException("Cannot find user with id: " + event.getId()));
        user.setFullname(event.getFullname());
        user.setAddress(event.getAddress());
        user.setActive(event.isActive());
        user.setPassword(event.getPassword());
        user.setDateOfBirth(event.getDateOfBirth());
        userRepository.save(user);
    }

    @EventHandler
    public void on(UserLoginEvent event) throws DataNotFoundException{
        User user = userRepository.findByPhoneNumber(event.getPhoneNumber()).orElseThrow(() -> new DataNotFoundException("Wrong phone number or password"));
        if(event.getRoleId() != user.getRoleId()){
            throw new DataNotFoundException("Can't find user with role:" + event.getRoleId());
        }
    }

    @EventHandler
    public void on(UserDeletedEvent event) {
        userRepository.deleteById(event.getId());
    }

}
