package com.userservice.response;

import com.userservice.model.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginResponse {
    private String access_token;

    private User user;
}
