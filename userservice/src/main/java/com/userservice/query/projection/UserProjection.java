package com.userservice.query.projection;

import com.userservice.command.data.User;
import com.userservice.command.data.UserRepository;
import com.userservice.exceptions.DataNotFoundException;
import com.userservice.query.model.UserResponseModel;
import com.userservice.query.queries.GetAllUsersQuery;
import com.userservice.query.queries.GetUserQuery;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserProjection {
    private final UserRepository userRepository;

    @QueryHandler
    public UserResponseModel handle(GetUserQuery getUserQuery) throws DataNotFoundException {
        UserResponseModel model = new UserResponseModel();
        User user = userRepository
                .findById(getUserQuery.getId()).orElseThrow(()-> new DataNotFoundException("Can't find user with id: "+getUserQuery.getId()));
        BeanUtils.copyProperties(user, model);
        return model;
    }

    @QueryHandler
    public List<UserResponseModel> handle(GetAllUsersQuery getAllUsersQuery){
        List<User> userList = userRepository.findAll();
        List<UserResponseModel> productResponseModelList = new ArrayList<>();
        userList.forEach(user -> {
            UserResponseModel userResponseModel = new UserResponseModel();
            BeanUtils.copyProperties(user, userResponseModel);
            productResponseModelList.add(userResponseModel);
        });
        return productResponseModelList;
    }
}
