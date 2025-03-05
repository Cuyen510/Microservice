package com.userservice.command.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginEvent {
    private String phoneNumber;
    private String password;
    private String roleId;
}
