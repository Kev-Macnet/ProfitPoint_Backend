/**
 * Created on 2021/1/26.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.sql.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tw.com.leadtek.nhiwidget.model.rdb.OP_D;

public interface OP_DDao extends JpaRepository<OP_D, Long> {

  public List<OP_D> findByOptId(Long optId);
  
  @Query(value = "SELECT SEQ_NO, ID, ROC_ID, FUNC_DATE, MR_ID FROM OP_D WHERE OPT_ID= ?1 ", nativeQuery = true)
  public List<Object[]> findByOptIdSimple(Long optId);
  
  @Query(value = "SELECT * FROM OP_D WHERE ID IN (SELECT D_ID FROM MR WHERE DATA_FORMAT = ?1 AND MR_DATE BETWEEN ?2 AND ?3) ", nativeQuery = true)
  public List<OP_D> findByIDFromMR(String dataFormat, Date sDate, Date eDate);
  
  @Query(value = "SELECT DISTINCT(PRSN_ID) , FUNC_TYPE "
      + "FROM OP_D WHERE PRSN_ID LIKE '%***%' AND FUNC_TYPE IS NOT NULL ORDER BY FUNC_TYPE", nativeQuery = true)
  public List<Object[]> findDepartmentAndDoctor();
}
