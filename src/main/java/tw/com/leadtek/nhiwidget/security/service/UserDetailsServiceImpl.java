/**
 * Created on 2020/10/27.
 */
package tw.com.leadtek.nhiwidget.security.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tw.com.leadtek.nhiwidget.model.rdb.USER;
import tw.com.leadtek.nhiwidget.service.UserService;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  protected static Logger logger = LogManager.getLogger();

  @Autowired
  private UserService userService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    //logger.info("UserDetailsServiceImpl loadUserByUsername:" + username);
    USER user = userService.findUser(username);
    if (user == null) {
      throw new UsernameNotFoundException("User Not Found with username: " + username);
    }
    return UserDetailsImpl.build(user);
  }

  public UserDetails build(String username, String role, Long id) {
    return new UserDetailsImpl(id, username, null, null, null, UserDetailsImpl.getAuthority(role),
        role);
  }
}
