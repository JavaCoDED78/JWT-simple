package io.github.javacoded78.jwthumble;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenServiceImplTest {

    private static final String SECRET_KEY = "c29tZWxvbmdzZWNyZXRzdHJpbmdmb3JleGFtcGxlYW5kaXRuZWVkc3RvYmVsb25nDQo=";
    private static TokenServiceImpl tokenService;

    private static String subject;
    private static Duration duration;

    @BeforeAll
    static void setup() {
        tokenService = new TokenServiceImpl(SECRET_KEY);
        subject = "testSubject";
        duration = Duration.ofMinutes(30);
    }

    @Test
    void getSubject_ReturnsValidToken() {
        TokenParameters params = TokenParameters.builder(
                        subject,
                        duration
                )
                .build();
        String token = tokenService.create(params);

        assertNotNull(token);
        assertEquals(subject, tokenService.getSubject(token));
    }

    @Test
    void isExpired_ValidToken_ReturnsFalse() {
        TokenParameters params = TokenParameters.builder(
                        subject,
                        duration
                )
                .build();
        String token = tokenService.create(params);

        assertFalse(tokenService.isExpired(token));
    }

    @Test
    void isExpired_ExpiredToken_ReturnsTrue() {
        Duration duration = Duration.ofSeconds(1);

        TokenParameters params = TokenParameters.builder(
                        subject,
                        duration
                )
                .build();
        String token = tokenService.create(params);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
        }

        assertTrue(tokenService.isExpired(token));
    }

    @Test
    void has_ValidClaim_ReturnsTrue() {
        TokenParameters params = TokenParameters.builder(subject, duration)
                .claim("testKey", "testValue")
                .build();

        String token = tokenService.create(params);
        assertTrue(tokenService.has(token, "testKey", "testValue"));
    }

    @Test
    void has_InvalidClaim_ReturnsFalse() {
        TokenParameters params = TokenParameters.builder(subject, duration)
                .claim("testKey", "testValue")
                .build();

        String token = tokenService.create(params);
        assertFalse(tokenService.has(token, "testKey", "invalidValue"));
    }

    @Test
    void claims_ReturnsCorrectClaims() {
        String subject = "testSubject";
        Duration duration = Duration.ofMinutes(30);
        Map<String, Object> customClaims = new HashMap<>();
        customClaims.put("key1", "value1");
        customClaims.put("key2", 123);

        TokenParameters params = TokenParameters.builder(subject, duration)
                .claims(customClaims)
                .build();

        String token = tokenService.create(params);
        Map<String, Object> claims = tokenService.claims(token);

        assertNotNull(claims);
        assertEquals("value1", claims.get("key1"));
        assertEquals(123, claims.get("key2"));
    }

}