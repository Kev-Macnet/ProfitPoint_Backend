/**
 * Created on 2020/10/28.
 */
package tw.com.leadtek.nhiwidget.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import tw.com.leadtek.nhiwidget.security.jwt.AuthEntryPointJwt;
import tw.com.leadtek.nhiwidget.security.jwt.AuthTokenFilter;
import tw.com.leadtek.nhiwidget.security.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  protected Logger logger = LogManager.getLogger();

  @Autowired
  private UserDetailsServiceImpl userDetailsService;

  @Autowired
  private AuthEntryPointJwt unauthorizedHandler;

  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter();
  }

  @Override
  public void configure(AuthenticationManagerBuilder authenticationManagerBuilder)
      throws Exception {
    authenticationManagerBuilder.userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder());
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // http.cors().and().csrf().disable()
    // .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
    // .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
    // .authorizeRequests()
    // .antMatchers("/auth/**").permitAll()
    // // 允许对于网站静态资源的无授权访问
    // .antMatchers(HttpMethod.GET, "/", "/*.html", "/favicon.ico", "/**/*.html", "/**/*.css",
    // "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.svg", "/fonts/*")
    // .permitAll()
    // .antMatchers("/**").permitAll()
    // .antMatchers("/swagger-ui.html").permitAll()
    // .antMatchers("/resources/**").permitAll()
    // .antMatchers("/webjars/**").permitAll()
    // .antMatchers("/swagger-resources/**").permitAll()
    // .antMatchers("/v3/**").permitAll()
    // .anyRequest().authenticated();
    http.csrf().disable().exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
        .authorizeRequests().antMatchers("/auth/**").permitAll()
        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        // 允许对于网站静态资源的无授权访问
        .antMatchers(HttpMethod.GET, "/", "/*.html", "/favicon.ico", "/**/*.html", "/**/*.css",
            "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.svg", "/fonts/*").permitAll()
        .antMatchers("/**").permitAll()
        .antMatchers("/swagger-ui.html").permitAll()
        .antMatchers("/resources/**").permitAll().antMatchers("/webjars/**").permitAll()
        .antMatchers("/swagger-resources/**").permitAll().antMatchers("/v3/**").permitAll()
        .anyRequest().authenticated();

    http.addFilterBefore(authenticationJwtTokenFilter(),
        UsernamePasswordAuthenticationFilter.class);
    http.cors();
  }
  
  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.addAllowedOrigin("*");
    config.addAllowedHeader("*");
    config.addAllowedMethod("OPTIONS");
    config.addAllowedMethod("GET");
    config.addAllowedMethod("POST");
    config.addAllowedMethod("PUT");
    config.addAllowedMethod("DELETE");
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
  }

}
