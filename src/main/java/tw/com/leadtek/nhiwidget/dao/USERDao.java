/**
 * Created on 2021/5/3.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tw.com.leadtek.nhiwidget.model.rdb.USER;

public interface USERDao extends JpaRepository<USER, Long> {

  public List<USER> findByUsername(String username);
  
  public List<USER> findByDisplayName(String displayName);
    
  public List<USER> findByRoleOrderByDisplayName(String Role);
  
  @Query(value = "SELECT * FROM user WHERE ROLE IN ('B', 'E') ORDER BY DISPLAY_NAME", nativeQuery = true)
  public List<USER> findDoctor();

  @Query(value = "SELECT * FROM user WHERE ROLE IN ('C', 'D') ORDER BY DISPLAY_NAME", nativeQuery = true)
  public List<USER> findApplUser();
  
  @Query(value = "SELECT * FROM user WHERE PASSWORD IS NOT NULL ORDER BY DISPLAY_NAME", nativeQuery = true)
  public List<USER> findAccount();
  
  public List<USER> findAllByOrderByRocId();
  
}
