/**
 * Created on 2022/02/24 by GenerateSqlByClass().
 */
package tw.com.leadtek.nhiwidget.model.rdb;

import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("處理下載或上傳的檔案進度")
@Table(name = "FILE_DOWNLOAD")
@Entity
public class FILE_DOWNLOAD {

  @ApiModelProperty(value = "序號", required = false)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  private Long id;

  @ApiModelProperty(value = "檔名", required = false)
  @Column(name = "FILENAME", length = 50)
  private String filename;

  @ApiModelProperty(value = "檔案類型", required = false)
  @Column(name = "FILE_TYPE", length = 10)
  private String fileType;

  @ApiModelProperty(value = "首次觸發下載USER ID", required = false)
  @Column(name = "USER_ID")
  private Long userId;

  @ApiModelProperty(value = "完成進度百分比，0 ~ 100", required = false)
  @Column(name = "PROGRESS")
  private Integer progress;
  
  @ApiModelProperty(value = "筆數", required = false)
  @Column(name = "RECORD")
  private Integer record;
  
  @ApiModelProperty(value = "開始處理時間", required = false)
  @Column(name = "START_AT")
  private Date startAt;
  
  @ApiModelProperty(value = "處理結束時間", required = false)
  @Column(name = "END_AT")
  private Date endAt;
  
  @ApiModelProperty(value = "使用秒數", required = false)
  @Column(name = "USED_TIME")
  private Integer usedTime;
  
  @ApiModelProperty(value = "更新日期", required = false)
  @Column(name = "UPDATE_AT")
  private Date updateAt;

  /**
   * 序號
   */
  public Long getId() {
    return id;
  }

  /**
   * 序號
   */
  public void setId(Long ID) {
    id = ID;
  }

  /**
   * 檔名
   */
  public String getFilename() {
    return filename;
  }

  /**
   * 檔名
   */
  public void setFilename(String FILENAME) {
    filename = FILENAME;
  }

  /**
   * 檔案類型
   */
  public String getFileType() {
    return fileType;
  }

  /**
   * 檔案類型
   */
  public void setFileType(String FILE_TYPE) {
    fileType = FILE_TYPE;
  }

  /**
   * 首次觸發下載USER ID
   */
  public Long getUserId() {
    return userId;
  }

  /**
   * 首次觸發下載USER ID
   */
  public void setUserId(Long USER_ID) {
    userId = USER_ID;
  }

  /**
   * 完成進度百分比，0 ~ 100
   */
  public Integer getProgress() {
    return progress;
  }

  /**
   * 完成進度百分比，0 ~ 100
   */
  public void setProgress(Integer PROGRESS) {
    progress = PROGRESS;
  }

  /**
   * 更新日期
   */
  public Date getUpdateAt() {
    return updateAt;
  }

  /**
   * 更新日期
   */
  public void setUpdateAt(Date UPDATE_AT) {
    updateAt = UPDATE_AT;
  }

  public Integer getRecord() {
    return record;
  }

  public void setRecord(Integer record) {
    this.record = record;
  }

  public Date getStartAt() {
    return startAt;
  }

  public void setStartAt(Date startAt) {
    this.startAt = startAt;
  }

  public Date getEndAt() {
    return endAt;
  }

  public void setEndAt(Date endAt) {
    this.endAt = endAt;
  }

  public Integer getUsedTime() {
    return usedTime;
  }

  public void setUsedTime(Integer usedTime) {
    this.usedTime = usedTime;
  }
}