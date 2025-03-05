package com.userservice.command.event;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
public class UserCreatedEvent {
    private String id;

    private String fullname;

    private String phoneNumber;

    private String address;

    private String password;

    private boolean active;

    private Date dateOfBirth;

    private String roleId;
}
