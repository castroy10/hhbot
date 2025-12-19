package ru.castroy10.hhbot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TokensConfig {

    private String accessToken;
    private String refreshToken;

}
