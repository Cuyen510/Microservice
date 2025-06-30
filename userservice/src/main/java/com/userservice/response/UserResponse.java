package com.userservice.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.userservice.model.Role;
import com.userservice.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;

    private String fullname;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private String address;

    private String password;

    private boolean active;

    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    private Role role;

    public static UserResponse fromUser(User user){
        return UserResponse.builder()
                .id(user.getId())
                .fullname(user.getFullname())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .password(user.getPassword())
                .active(user.isActive())
                .dateOfBirth(user.getDateOfBirth())
                .role(user.getRole())
                .build();
    }

}
