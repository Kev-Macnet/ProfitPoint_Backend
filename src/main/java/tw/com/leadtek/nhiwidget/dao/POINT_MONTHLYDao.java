/**
 * Created on 2021/11/10.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tw.com.leadtek.nhiwidget.model.rdb.POINT_MONTHLY;

public interface POINT_MONTHLYDao extends JpaRepository<POINT_MONTHLY, Long> {

  public POINT_MONTHLY findByYm(int ym);
  
  @Query(value = "SELECT MIN(YM) FROM POINT_MONTHLY", nativeQuery = true)
  public Integer getMinYm();
  
  @Query(value = "SELECT MAX(YM) FROM POINT_MONTHLY", nativeQuery = true)
  public Integer getMaxYm();
  
  public List<POINT_MONTHLY> findByYmBetweenOrderByYm(int startYm, int endYm);
  
  /**
   * 取得輸入區間資料
   * @param ym
   * @return
   */
  @Query(value = "SELECT * FROM POINT_MONTHLY WHERE YM IN (?1)  ORDER BY YM ", nativeQuery = true)
  public List<POINT_MONTHLY> getByYmInOrderByYm(List<Integer> ym);
}

