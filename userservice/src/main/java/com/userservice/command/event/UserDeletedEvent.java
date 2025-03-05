package com.userservice.command.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDeletedEvent {
    private String id;
}
