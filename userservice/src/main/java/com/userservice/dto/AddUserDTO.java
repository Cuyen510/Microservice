package com.userservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.userservice.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddUserDTO {
    @JsonProperty("fullname")
    private String fullName;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private String address;

    private String password;

    @JsonProperty("retype_password")
    private String retypePassword;

    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    private String role;

    public static User dtoToEntity(AddUserDTO addUserDTO){
        User user = new User();
        user.setFullname(addUserDTO.getFullName());
        user.setAddress(addUserDTO.getAddress());
        user.setPhoneNumber(addUserDTO.getPhoneNumber());
        user.setDateOfBirth(addUserDTO.getDateOfBirth());
        return user;
    }
}
