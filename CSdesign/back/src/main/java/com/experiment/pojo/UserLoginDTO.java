package com.experiment.pojo;

import lombok.Data;

@Data
public class UserLoginDTO {
    private String username;
    private String password;
    private String role;
    private Boolean remember;
}
