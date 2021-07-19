/**
 * Created on 2021/3/12.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tw.com.leadtek.nhiwidget.model.rdb.MR;

public interface MRDao extends JpaRepository<MR, Long>, JpaSpecificationExecutor<MR> {

  public List<MR> findByMrDateBetween(Date startDate, Date endDate);
  
  public List<MR> findByDataFormatAndMrDateBetween(String dataFormat, Date startDate, Date endDate);
  
  public List<MR> findByStatusAndMrDateBetween(Integer status, Date startDate, Date endDate);
  
  /**
   * 取得指定日期區間的病歷數, 申請病歷數及申請總點數.
   * @return
   */
  @Query(value = "SELECT b.OP_TOTAL_MR, c.IP_TOTAL_MR, b.OP_DOT, c.IP_DOT FROM " + 
      // 1. 門診申請件數 , 申請總點數  
      "(SELECT count(mr.ID) AS OP_TOTAL_MR, COALESCE(SUM(opd.T_APPL_DOT), 0) AS OP_DOT " + 
      "  FROM MR mr, OP_D opd WHERE mr.MR_DATE BETWEEN ?1 AND ?2 " + 
      "  AND mr.DATA_FORMAT = '10' AND mr.D_ID = opd.id) b," + 
      // 2. 住院申請件數 , 申請總點數\r\n" + 
      "(SELECT count(mr.ID) AS IP_TOTAL_MR, COALESCE(SUM(ipd.APPL_DOT), 0) AS IP_DOT " + 
      "  FROM MR mr, IP_D ipd WHERE mr.MR_DATE BETWEEN ?3 AND ?4 " + 
      "  AND mr.DATA_FORMAT = '20' AND mr.D_ID = ipd.id ) c", nativeQuery = true)
  public List<Map<String, Object>> queryMRRecordCount(Date sDate, Date eDate, Date sDate2, Date eDate2);
  
  /**
   * 取得指定日期區間及科別的病歷數, 申請病歷數及申請總點數.
   * @return
   */
  @Query(value = "SELECT b.OP_TOTAL_MR, c.IP_TOTAL_MR, b.OP_DOT , c.IP_DOT FROM " + 
      "(SELECT count(DISTINCT(mr.ID)) AS OP_TOTAL_MR, COALESCE(sum(opp.TOTAL_DOT), 0) AS OP_DOT " + 
      "FROM MR mr, OP_D opd, OP_P opp WHERE mr.MR_DATE BETWEEN ?1 AND ?2 AND mr.FUNC_TYPE =?3 " + 
      "AND mr.DATA_FORMAT  = '10' AND mr.D_ID = opd.id AND opd.id = opp.OPD_ID AND opp.APPL_STATUS = 1) b," + 
      "(SELECT count(DISTINCT(mr.ID)) AS IP_TOTAL_MR, COALESCE(sum(ipp.TOTAL_DOT), 0) AS IP_DOT FROM " + 
      "MR mr, IP_D ipd, IP_P ipp WHERE mr.MR_DATE BETWEEN ?4 AND ?5 AND mr.FUNC_TYPE =?6 " + 
      "AND mr.DATA_FORMAT = '20' AND mr.D_ID = ipd.id AND ipd.id = ipp.IPD_ID AND ipp.APPL_STATUS = 1) c", nativeQuery = true)
  public List<Map<String, Object>> queryMRRecordCountByFuncType(Date sDate, Date eDate, String funcType, 
      Date sDate2, Date eDate2, String funcType2);
  
  /**
   * 取得指定日期區間的各個病歷確認狀態總數
   * @param sDate
   * @param eDate
   * @return
   */
  @Query(value = "SELECT STATUS, COUNT(STATUS) AS STATUS_SUM FROM MR "
      + "WHERE DATA_FORMAT IN (:dataFormat) AND MR_DATE BETWEEN :sDate AND :eDate GROUP BY STATUS", nativeQuery = true)
  public List<Map<String, Object>> queryMRStatusCount(@Param("dataFormat") List<String> dataFormat, 
      @Param("sDate") Date sDate,  @Param("eDate") Date eDate);
  
  /**
   * 依科別取得指定日期區間科別的各個病歷確認狀態總數
   * @param sDate
   * @param eDate
   * @return
   */
  @Query(value = "SELECT STATUS, COUNT(STATUS) AS STATUS_SUM FROM MR WHERE MR_DATE BETWEEN "
      + "?1 AND ?2 AND FUNC_TYPE = ?3 GROUP BY STATUS ", nativeQuery = true)
  public List<Map<String, Object>> queryMRStatusCount(Date sDate, Date eDate, String funcType);
  
  /**
   * 依申報人員id取得指定日期區間的各個病歷確認狀態總數
   * @param sDate
   * @param eDate
   * @return
   */
  @Query(value = "SELECT STATUS, COUNT(STATUS) AS STATUS_SUM FROM MR WHERE MR_DATE BETWEEN "
      + "?1 AND ?2 AND APPL_ID IN (?3) GROUP BY STATUS ", nativeQuery = true)
  public List<Map<String, Object>> queryMRStatusCountByApplId(Date sDate, Date eDate, List<String> applId);
  
}