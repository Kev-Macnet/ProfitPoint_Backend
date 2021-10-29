/**
 * Created on 2020/10/27.
 */
package tw.com.leadtek.nhiwidget.security.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import tw.com.leadtek.nhiwidget.constant.ROLE_TYPE;
import tw.com.leadtek.nhiwidget.model.rdb.USER;

public class UserDetailsImpl implements UserDetails {

  private static final long serialVersionUID = -1160482074161059306L;

  protected static Logger logger = LogManager.getLogger();

  private Long id;

  private String username;

  private String email;

  private String displayName;

  @JsonIgnore
  private String password;
  
  private String role;
  
  private long loginTime;

  private Collection<? extends GrantedAuthority> authorities;

  public UserDetailsImpl(Long id, String username, String displayName, String email,
      String password, Collection<? extends GrantedAuthority> authorities, String role) {
    this.id = id;
    this.username = username;
    this.displayName = displayName;
    this.email = email;
    this.password = password;
    this.authorities = authorities;
    this.role = role;
    this.loginTime = System.currentTimeMillis();
  }

  public static UserDetailsImpl build(USER user) {
    logger.info("UserDetailsImpl build user:" + user.getUsername());
    return new UserDetailsImpl(user.getId(), user.getUsername(), user.getDisplayName(),
        user.getEmail(), user.getPassword(), getAuthority(user.getRole()), user.getRole());
  }
  
  public static List<GrantedAuthority> getAuthority(String role) {
    List<GrantedAuthority> result = new ArrayList<GrantedAuthority>();
    if (role.equals("Z")) {
      result.add(new SimpleGrantedAuthority(role));
      result.add(new SimpleGrantedAuthority("A"));
      result.add(new SimpleGrantedAuthority("B"));
      result.add(new SimpleGrantedAuthority("C"));
      result.add(new SimpleGrantedAuthority("D"));
      result.add(new SimpleGrantedAuthority("E"));
    } else if (role.equals("A")) {
      result.add(new SimpleGrantedAuthority("A"));
      result.add(new SimpleGrantedAuthority("B"));
      result.add(new SimpleGrantedAuthority("C"));
      result.add(new SimpleGrantedAuthority("D"));
      result.add(new SimpleGrantedAuthority("E"));
    } else if (role.equals("B") || role.equals("E")) {
      result.add(new SimpleGrantedAuthority("B"));
      result.add(new SimpleGrantedAuthority("E"));
    } else if (role.equals("C")) {
      result.add(new SimpleGrantedAuthority("B"));
      result.add(new SimpleGrantedAuthority("C"));
      result.add(new SimpleGrantedAuthority("D"));
      result.add(new SimpleGrantedAuthority("E"));
    }  else if (role.equals("D")) {
      result.add(new SimpleGrantedAuthority("B"));
      result.add(new SimpleGrantedAuthority("D"));
      result.add(new SimpleGrantedAuthority("E"));
    } 
    return result;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  public Long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getPassword() {
    return password;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
  
  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }
  
  public long getLoginTime() {
    return loginTime;
  }

  public void setLoginTime(long loginTime) {
    this.loginTime = loginTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    UserDetailsImpl user = (UserDetailsImpl) o;
    return Objects.equals(id, user.id);
  }
}
