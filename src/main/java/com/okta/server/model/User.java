package com.okta.server.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Getter
@Setter
@NoArgsConstructor
public class User {
    Profile profile;
    Credentials credentials;
}

@Getter
@Setter
@NoArgsConstructor
class Profile {
    private String firstName;
    private String lastName;
    private String email;
    private String login;
    private String mobilePhone;
}
@Getter
@Setter
@NoArgsConstructor
class Credentials {
    Password password;
}

@Data
@NoArgsConstructor
class Password {
    String value;
}
