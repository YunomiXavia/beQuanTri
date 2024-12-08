package com.example.beQuanTri.configuration.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class JwtTokenUtil {
    final Set<String> blacklistedTokens = new HashSet<>();
    @Value("${jwt.signerKey}")
    String signerKey;

    // Generate Token
    public String generateToken(
            String username,
            String userId,
            String role,
            String email,
            long expirationTime)
            throws JOSEException {
        JWSSigner signer = new MACSigner(signerKey);

        // ClaimSet token
        JWTClaimsSet claimsSet = new JWTClaimsSet
                .Builder()
                .issuer("vinshop")
                .subject(username)
                .audience("vinshop-cms")
                .jwtID("vinshop-cms")
                .notBeforeTime(new Date())
                .issueTime(new Date())
                .claim("role", "ROLE_" + role)
                .claim("userId", userId)
                .claim("email", email)
                .expirationTime(new Date(new Date().getTime() + expirationTime))
                .build();

        // Sign token
        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256), claimsSet
        );
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    // Validate Token
    public boolean validateToken(String token)
            throws ParseException {
        if (blacklistedTokens.contains(token)) {
            log.warn("Token is blacklisted");
            return false;
        }
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            boolean isVerified = signedJWT.verify(new MACVerifier(signerKey));
            boolean isNotExpired = new Date().before(
                    signedJWT.getJWTClaimsSet().getExpirationTime()
            );

            log.info("Token Verification: {}", isVerified);
            log.info("Token Expiration Check: {}", isNotExpired);

            return isVerified && isNotExpired;
        } catch (JOSEException e) {
            log.info("Validate Token failed. The error is: {}", e.getMessage());
            return false;
        }
    }

    // Add Token to Black List Token
    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }

    public String getUsernameFromToken(String token)
            throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        return signedJWT.getJWTClaimsSet().getSubject();
    }

    public String getRoleFromToken(String token)
            throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        return signedJWT.getJWTClaimsSet().getClaim("role").toString();
    }

    public String getUserIdFromToken(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        return signedJWT.getJWTClaimsSet().getStringClaim("userId");
    }

    public String getEmailFromToken(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        return signedJWT.getJWTClaimsSet().getStringClaim("email");
    }
}
