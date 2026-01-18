package com.experiment.pojo;

import lombok.Data;

@Data
public class UserChangePwdDTO {
    private String username;

    private String oldPassword;

    private String newPassword;

    private String role;
}
