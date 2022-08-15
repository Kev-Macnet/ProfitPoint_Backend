package tw.com.leadtek.nhiwidget.model.rdb;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

@Table(name = "MR_SO")
@Entity
public class MR_SO {

  @Id
  @ApiModelProperty(value = "MR table ID", example = "123", required = true)
  @Column(name = "MR_ID", nullable = false)
  private Long mrId;
  
  @ApiModelProperty(value = "病歷編號", example = "O21100800017", required = false)
  @Column(name = "INH_NO", nullable = false)
  private String inhNo;
  
  @ApiModelProperty(value = "主觀自覺徵候", example = "no more vomiting ,no tarry stool.epigastralgia at night", required = false)
  @Column(name = "SUBJECT_TEXT")
  private String subjectText;
  
  @ApiModelProperty(value = "醫療人員的客觀檢查發現", example = "2013/05/08  胃鏡 CLO test", required = false)
  @Column(name = "OBJECT_TEXT")
  private String objectText;
  
  @ApiModelProperty(value = "出院摘要", example = "", required = false)
  @Column(name = "DISCHARGE_TEXT")
  private String dischargeText;
  
  @ApiModelProperty(value = "匯入SO日期", example = "", required = false)
  @Column(name = "CREATE_AT")  
  @JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
  private Date createAt;

  public Long getMrId() {
    return mrId;
  }

  public void setMrId(Long mrId) {
    this.mrId = mrId;
  }

  public String getInhNo() {
    return inhNo;
  }

  public void setInhNo(String inhNo) {
    this.inhNo = inhNo;
  }

  public String getSubjectText() {
    return subjectText;
  }

  public void setSubjectText(String subjectText) {
    this.subjectText = subjectText;
  }

  public String getObjectText() {
    return objectText;
  }

  public void setObjectText(String objectText) {
    this.objectText = objectText;
  }

  public String getDischargeText() {
    return dischargeText;
  }

  public void setDischargeText(String dischargeText) {
    this.dischargeText = dischargeText;
  }

  public Date getCreateAt() {
    return createAt;
  }

  public void setCreateAt(Date createAt) {
    this.createAt = createAt;
  }
  
}
