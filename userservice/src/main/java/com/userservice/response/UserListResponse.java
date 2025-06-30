package com.userservice.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserListResponse {
    @JsonProperty("users")
    private List<UserResponse> users;

    @JsonProperty("totalPages")
    private int totalPages;
}
