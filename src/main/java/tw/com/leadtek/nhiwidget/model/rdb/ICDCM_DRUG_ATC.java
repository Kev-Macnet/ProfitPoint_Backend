/**
 * Created on 2022/5/12.
 */
package tw.com.leadtek.nhiwidget.model.rdb;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;

@ApiModel("診斷碼搭配藥品衛材的出現次數")
@Table(name = "ICDCM_DRUG_ATC")
@Entity
@IdClass(ICDCM_DRUG_ATC_KEYS.class)
public class ICDCM_DRUG_ATC implements Serializable {

  private static final long serialVersionUID = 8138687062517004852L;

    /**
     * 診斷碼
     */
    @Id
    @Column(name = "ICDCM")
    private String icdcm;
    
    /**
     * 藥品、衛材代碼
     */
    @Id
    @Column(name = "DRUG")
    private String drug;
    
    /**
     * 資料格式 10: 門急診 20: 住院
     */
    @Id
    @Column(name = "DATA_FORMAT")
    private String dataFormat;

    /**
     * ATC代碼
     */
    @Column(name = "ATC")
    private String atc;

    /**
     * 該診斷碼出現病歷數
     */
    @Column(name = "MR_COUNT")
    private int mrCount;
    
    /**
     * 該診斷碼出現該藥品的次數
     */
    @Column(name = "DRUG_COUNT")
    private int drugCount;

    /**
     * 該藥品出現在該ATC分類下的次數百分比，值為0~100
     */
    @Column(name = "ATC_COUNT")
    private int atcCount;

    /**
     * 最新一筆病歷日期
     */
    @Column(name = "LATEST_DATE", nullable = false)
    @JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
    private Date latestDate;
    
    public ICDCM_DRUG_ATC() {
      
    }
    
    public ICDCM_DRUG_ATC(String dataFormat, Map<String, Object> map, java.sql.Date endDate, boolean isDrug) {
      this.dataFormat = dataFormat;
      this.atc = isDrug ? (String) map.get("ATC") : "MATERIAL";
      this.drug = (String) map.get("DRUG_NO");
      this.icdcm = (String) map.get("ICDCM1");
      
      if (map.get("ICDCM_COUNT") instanceof BigInteger) {
        this.drugCount = ((BigInteger) map.get("DRUG_COUNT")).intValue();
        this.mrCount = ((BigInteger) map.get("ICDCM_COUNT")).intValue();
      } else {
        this.drugCount = (Integer) map.get("DRUG_COUNT");
        this.mrCount = (Integer) map.get("ICDCM_COUNT");
      }
      this.latestDate = new Date(endDate.getTime());
    }

    public Date getLatestDate() {
      return latestDate;
    }

    public void setLatestDate(Date latestDate) {
      this.latestDate = latestDate;
    }

    public String getIcdcm() {
      return icdcm;
    }

    public void setIcdcm(String icdcm) {
      this.icdcm = icdcm;
    }

    public String getDrug() {
      return drug;
    }

    public void setDrug(String drug) {
      this.drug = drug;
    }

    public String getDataFormat() {
      return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
      this.dataFormat = dataFormat;
    }

    public String getAtc() {
      return atc;
    }

    public void setAtc(String atc) {
      this.atc = atc;
    }

    public int getMrCount() {
      return mrCount;
    }

    public void setMrCount(int mrCount) {
      this.mrCount = mrCount;
    }

    public int getDrugCount() {
      return drugCount;
    }

    public void setDrugCount(int drugCount) {
      this.drugCount = drugCount;
    }

    public int getAtcCount() {
      return atcCount;
    }

    public void setAtcCount(int atcCount) {
      this.atcCount = atcCount;
    }

  }

