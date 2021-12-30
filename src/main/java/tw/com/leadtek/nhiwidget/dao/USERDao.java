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
    
  public List<USER> findByRole(String Role);

  @Query(value = "SELECT * FROM USER WHERE ROLE IN ('C', 'D')", nativeQuery = true)
  public List<USER> findApplUser();
  
  @Query(value = "SELECT * FROM USER WHERE PASSWORD IS NOT NULL", nativeQuery = true)
  public List<USER> findAccount();
  
}
