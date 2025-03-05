package com.userservice.command.controller;
import com.userservice.command.command.CreateUserCommand;
import com.userservice.command.command.DeleteUserCommand;
import com.userservice.command.command.LoginCommand;
import com.userservice.command.command.UpdateUserCommand;
import com.userservice.command.model.UserRequestModel;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserCommandController {
    private final CommandGateway commandGateway;

    @PostMapping
    public String addUser(@RequestBody UserRequestModel model){
        CreateUserCommand command = new CreateUserCommand(UUID.randomUUID().toString(), model.getFullname(),model.getPhoneNumber()
                ,model.getAddress(),model.getPassword(), model.isActive(), model.getDateOfBirth(),model.getRoleId());
        commandGateway.sendAndWait(command);
        return "Added user";
    }

    @PostMapping("/login")
    public String login(@RequestBody UserRequestModel model){
        LoginCommand command = new LoginCommand(model.getPhoneNumber(), model.getPassword(), model.getRoleId());
        commandGateway.sendAndWait(command);
        return "logged in";
    }




    @PutMapping("/{id}")
    public String updateUser(@RequestBody UserRequestModel model){
        UpdateUserCommand command = new UpdateUserCommand(model.getId(), model.getFullname(),model.getPhoneNumber()
                ,model.getAddress(),model.getPassword(),model.isActive(),model.getDateOfBirth(),model.getRoleId());
        commandGateway.sendAndWait(command);
        return "User updated";
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable String id){
        DeleteUserCommand command = new DeleteUserCommand(id);
        commandGateway.sendAndWait(command);
        return "User deleted";
    }

}
