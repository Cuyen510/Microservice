package com.userservice.command.command;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserCommand {
    @TargetAggregateIdentifier
    private String id;

    private String fullname;

    private String phoneNumber;

    private String address;

    private String password;

    private boolean active;

    private Date dateOfBirth;

    private String roleId;
}
