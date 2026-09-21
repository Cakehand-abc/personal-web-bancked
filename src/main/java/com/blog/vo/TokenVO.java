package com.blog.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenVO {
    private String token;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private String username;
}
