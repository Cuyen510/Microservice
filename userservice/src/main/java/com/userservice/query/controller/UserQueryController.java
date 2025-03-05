package com.userservice.query.controller;

import com.userservice.query.model.UserResponseModel;
import com.userservice.query.queries.GetAllUsersQuery;
import com.userservice.query.queries.GetUserQuery;
import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserQueryController {
    private final QueryGateway queryGateway;

    @GetMapping("/{id}")
    public UserResponseModel getUserDetail(@PathVariable String id){
        GetUserQuery getUserQuery = new GetUserQuery();
        getUserQuery.setId(id);
        UserResponseModel
                productResponseModel = queryGateway.query(getUserQuery, ResponseTypes.instanceOf(UserResponseModel.class)).join();
        return productResponseModel;
    }

    @GetMapping
    public List<UserResponseModel> getAllProduct(){
        GetAllUsersQuery getAllProductsQuery = new GetAllUsersQuery();
        List<UserResponseModel>
                list = queryGateway.query(getAllProductsQuery, ResponseTypes.multipleInstancesOf(UserResponseModel.class)).join();
        return list;
    }
}
