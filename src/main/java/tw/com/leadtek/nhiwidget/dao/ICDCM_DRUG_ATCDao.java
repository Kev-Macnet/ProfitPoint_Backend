/**
 * Created on 2022/5/12.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import tw.com.leadtek.nhiwidget.model.rdb.ICDCM_DRUG_ATC;
import tw.com.leadtek.nhiwidget.model.rdb.ICDCM_DRUG_ATC_KEYS;

public interface ICDCM_DRUG_ATCDao extends JpaRepository<ICDCM_DRUG_ATC, ICDCM_DRUG_ATC_KEYS> {

  @Transactional
  @Modifying
  @Query(value = "DELETE FROM ICDCM_DRUG_ATC WHERE DATA_FORMAT = ?1", nativeQuery = true)
  public void deleteByDataFormat(String dataFormat);
  
  /**
   * 取得用藥/衛品差異的ICD碼、藥品代碼及ATC代碼
   * @param dataFormat
   * @param percent
   * @return
   */
  @Query(value = "SELECT ICDCM_DRUG_ATC.* FROM ICDCM_DRUG_ATC, " + 
      "(SELECT ICDCM, DRUG FROM ICDCM_DRUG_ATC WHERE DATA_FORMAT = ?1 AND length(drug) = ?2 " +
      "AND ATC_COUNT > 30) DIFF " + 
      "WHERE ICDCM_DRUG_ATC.DATA_FORMAT = ?1 AND length(ICDCM_DRUG_ATC.drug) = ?2 " +
      "AND ICDCM_DRUG_ATC.ICDCM = DIFF.ICDCM AND ICDCM_DRUG_ATC.DRUG = DIFF.DRUG " +
      "ORDER BY ICDCM_DRUG_ATC.ICDCM, ICDCM_DRUG_ATC.ATC, ICDCM_DRUG_ATC.DRUG_COUNT DESC", nativeQuery = true)
  public List<ICDCM_DRUG_ATC> getDrugDiffList(String dataFormat, int drugLength, float percent);
  
}
