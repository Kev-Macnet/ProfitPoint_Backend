/**
 * Created on 2020/10/27.
 */
package tw.com.leadtek.nhiwidget.security.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import tw.com.leadtek.nhiwidget.security.service.UserDetailsImpl;

/**
 * Reference: https://bezkoder.com/spring-boot-jwt-authentication/
 */
@Component
public class JwtUtils {

  protected Logger logger = LogManager.getLogger();

  private static final String CLAIM_KEY_USERNAME = "sub";

  private static final String CLAIM_KEY_CREATED = "created";

  private static final String CLAIM_KEY_USER_ID = "uid";
  
  private static final String CLAIM_KEY_ROLE = "role";

  private static final String CLAIM_KEY_USER_IP = "uip";

  private static final SecretKey KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

  public final static String TOKEN_HEADER = "Authorization";

  public final static String TOKEN_HEAD = "Bearer ";

  public final static String BASIC_HEAD = "Basic ";

  private String jwtSecret;

  @Value("${jwt.expiration}")
  private Long jwtExpirationMs;

  public String generateJwtToken(Authentication authentication) {

    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

    // return Jwts.builder()
    // .setSubject((userPrincipal.getUsername()))
    // .setIssuedAt(new Date())
    // .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
    // .signWith(SignatureAlgorithm.HS512, jwtSecret)
    // .compact();
    Map<String, Object> claims = new HashMap<>(16);
    claims.put(CLAIM_KEY_USERNAME, userPrincipal.getUsername());
    claims.put(CLAIM_KEY_USER_ID, userPrincipal.getId());
    claims.put(CLAIM_KEY_ROLE, userPrincipal.getRole());
    // claims.put(CLAIM_KEY_USER_IP, clientIP);
    return Jwts.builder().setClaims(claims)
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs)).signWith(KEY)
        .compact();
  }

  public Key getSigningKey() {
    // 指定 jwt 使用的密碼，若不呼叫此 function則為動態密碼
    byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * 根據token獲取username
   */
  public String getUsernameFromToken(String token) throws ExpiredJwtException, SignatureException {
    Claims claims = getClaimsFromToken(token);
    if (claims == null) {
      return null;
    }
    return claims.getSubject();
  }
  
  /**
   * 根據Claims獲取username
   */
  public String getUsernameFromClaims(Claims claims) throws ExpiredJwtException, SignatureException {
    return claims.getSubject();
  }
  
  public Long getUserIdFromClaims(Claims claims)  throws ExpiredJwtException, SignatureException {
    return ((Double) claims.get(CLAIM_KEY_USER_ID)).longValue();
  }
  
  /**
   * 根據Claims獲取role
   */
  public String getRoleFromClaims(Claims claims) throws ExpiredJwtException, SignatureException {
    return (String) claims.get(CLAIM_KEY_ROLE);
  }

  public String getUserID(String token) {
    String result = String.valueOf((Double)getClaimsFromToken(token).get(CLAIM_KEY_USER_ID));
    if (result != null && result.endsWith(".0")) {
      result = result.substring(0, result.length() - 2);
    }
    return result;
  }

  /**
   * 解析JWT
   */
  public Claims getClaimsFromToken(String token) {
    try {
      return Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token).getBody();
    } catch (SignatureException e) {
      //logger.error("Invalid JWT signature: {}", e.getMessage());
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
    }
    return null;
  }

  public boolean validateJwtToken(String authToken) {
    try {
      Claims claims = getClaimsFromToken(authToken);
      return (claims != null) ? true : false;
    } catch (SignatureException e) {
      logger.error("Invalid JWT signature: {}", e.getMessage());
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false;
  }

  /**
   * 驗證JWT
   */
  public Boolean validateToken(String token, UserDetails userDetails, String clientIP) {
    UserDetailsImpl user = (UserDetailsImpl) userDetails;
    Claims claims = getClaimsFromToken(token);
    // if (!clientIP.equals((String) claims.get(CLAIM_KEY_USER_IP))) {
    // return false;
    // }
    String username = claims.getSubject();
    return (username.equals(user.getUsername()) && !isTokenExpired(token));
  }

  /**
   * 獲取token的過期時間
   */
  public Date getExpirationDateFromToken(String token) {
    Date expiration = getClaimsFromToken(token).getExpiration();
    return expiration;
  }
  
  /**
   * 獲取token的建立時間
   */
  public long getLoginTimeFromToken(String token) {
    return getClaimsFromToken(token).getExpiration().getTime() - jwtExpirationMs;
  }

  /**
   * 獲取token是否過期
   */
  public Boolean isTokenExpired(String token) {
    Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }

}
