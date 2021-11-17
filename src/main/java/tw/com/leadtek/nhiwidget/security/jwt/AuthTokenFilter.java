/**
 * Created on 2020/10/27.
 */
package tw.com.leadtek.nhiwidget.security.jwt;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Claims;
import tw.com.leadtek.nhiwidget.security.service.UserDetailsServiceImpl;
import tw.com.leadtek.nhiwidget.service.UserService;

public class AuthTokenFilter extends OncePerRequestFilter {

  protected Logger logger = LogManager.getLogger();

  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private UserDetailsServiceImpl userDetailsService;

  @Autowired
  private UserService userService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    try {
      String jwt = parseJwt(request);
      if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
        Claims claims = jwtUtils.getClaimsFromToken(jwt);
        String username = jwtUtils.getUsernameFromClaims(claims);
        // 確認用戶是否要編輯病歷
        boolean isEditing = request.getRequestURI().indexOf("/nhixml/mr/") >= 0 && "POST".equals(request.getMethod());
        if (!userService.updateUserAlive(username, jwt, isEditing)) {
          response.sendError(HttpStatus.UNAUTHORIZED.value(), "token已失效");
        }
        // UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UserDetails userDetails =
            userDetailsService.build(username, jwtUtils.getRoleFromClaims(claims), jwtUtils.getUserIdFromClaims(claims));
        // for (GrantedAuthority authority : userDetails.getAuthorities()) {
        // logger.info("authority:" + authority.getAuthority());
        // }
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
      } else if (request.getRequestURI().startsWith("/auth")) {
        // white list
      } else {
        //response.sendError(HttpStatus.UNAUTHORIZED.value(), "未登入");
      }
    } catch (Exception e) {
      logger.error("Cannot set user authentication: {}", e);
    }

    try {
      filterChain.doFilter(request, response);
    } catch (AccessDeniedException e) {
      // token 有誤
      e.printStackTrace();
    }
  }

  private String parseJwt(HttpServletRequest request) {
    String headerAuth = request.getHeader("Authorization");

    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
      return headerAuth.substring(7, headerAuth.length());
    }

    return null;
  }
}
