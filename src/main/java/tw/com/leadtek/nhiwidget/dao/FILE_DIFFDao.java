/**
 * Created on 2022/3/1.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import tw.com.leadtek.nhiwidget.model.rdb.FILE_DIFF;

public interface FILE_DIFFDao extends JpaRepository<FILE_DIFF, Long> {

  public List<FILE_DIFF> findByMrId(Long mrid); 
  
  @Transactional
  @Modifying
  @Query(value = "DELETE FROM FILE_DIFF WHERE MR_ID=?1", nativeQuery = true)
  public void deleteByMrId(Long mrId);
}
