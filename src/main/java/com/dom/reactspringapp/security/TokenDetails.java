package com.dom.reactspringapp.security;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.cert.CertPathBuilder;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDetails {

    private Long userId;
    private String token;
    private Date issuedAt;
    private Date expiresAt;


}
