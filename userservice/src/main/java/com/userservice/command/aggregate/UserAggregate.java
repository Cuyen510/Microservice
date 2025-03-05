package com.userservice.command.aggregate;

import com.userservice.command.command.CreateUserCommand;
import com.userservice.command.command.DeleteUserCommand;
import com.userservice.command.command.LoginCommand;
import com.userservice.command.command.UpdateUserCommand;
import com.userservice.command.event.UserCreatedEvent;
import com.userservice.command.event.UserDeletedEvent;
import com.userservice.command.event.UserLoginEvent;
import com.userservice.command.event.UserUpdatedEvent;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Aggregate
@NoArgsConstructor
@AllArgsConstructor
public class UserAggregate {
    @AggregateIdentifier
    private String id;

    private String fullname;

    private String phoneNumber;

    private String address;

    private String password;

    private boolean active;

    private Date dateOfBirth;

    private String roleId;

    @CommandHandler
    public UserAggregate(CreateUserCommand createUserCommand){
        UserCreatedEvent userCreatedEvent = new UserCreatedEvent();
        BeanUtils.copyProperties(createUserCommand, userCreatedEvent);
        AggregateLifecycle.apply(userCreatedEvent);
    }

    @CommandHandler
    public void handle(UpdateUserCommand updateUserCommand){
        UserUpdatedEvent userUpdatedEvent = new UserUpdatedEvent();
        BeanUtils.copyProperties(updateUserCommand, userUpdatedEvent);
        AggregateLifecycle.apply(userUpdatedEvent);
    }

    @CommandHandler
    public void handle(DeleteUserCommand deleteUserCommand){
        UserDeletedEvent userDeletedEvent = new UserDeletedEvent();
        BeanUtils.copyProperties(deleteUserCommand, userDeletedEvent);
        AggregateLifecycle.apply(userDeletedEvent);
    }

    @CommandHandler
    public void handle(LoginCommand loginCommand){
        UserLoginEvent userLoginEvent = new UserLoginEvent();
        BeanUtils.copyProperties(loginCommand, userLoginEvent);
        AggregateLifecycle.apply(userLoginEvent);
    }

    @EventSourcingHandler
    public void on(UserCreatedEvent userCreatedEvent){
        this.id = userCreatedEvent.getId();
        this.fullname = userCreatedEvent.getFullname();
        this.phoneNumber = userCreatedEvent.getPhoneNumber();
        this.address = userCreatedEvent.getAddress();
        this.password = userCreatedEvent.getPassword();
        this.active = userCreatedEvent.isActive();
        this.dateOfBirth = userCreatedEvent.getDateOfBirth();
        this.roleId = userCreatedEvent.getRoleId();
    }

    @EventSourcingHandler
    public void on(UserLoginEvent userLoginEvent){
        this.phoneNumber = userLoginEvent.getPhoneNumber();
        this.password = userLoginEvent.getPassword();
        this.roleId = userLoginEvent.getRoleId();
    }

    @EventSourcingHandler
    public void on(UserUpdatedEvent userUpdatedEvent){
        this.id = userUpdatedEvent.getId();
        this.fullname = userUpdatedEvent.getFullname();
        this.phoneNumber = userUpdatedEvent.getPhoneNumber();
        this.address = userUpdatedEvent.getAddress();
        this.password = userUpdatedEvent.getPassword();
        this.active = userUpdatedEvent.isActive();
        this.dateOfBirth = userUpdatedEvent.getDateOfBirth();
        this.roleId = userUpdatedEvent.getRoleId();
    }

    @EventSourcingHandler
    public void on(UserDeletedEvent userDeletedEvent){
        this.id = userDeletedEvent.getId();

    }
}
