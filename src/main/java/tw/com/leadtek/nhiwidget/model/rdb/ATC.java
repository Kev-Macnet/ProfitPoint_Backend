/**
 * Created on 2021/9/8.
 */
package tw.com.leadtek.nhiwidget.model.rdb;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("ATC分類")
@Table(name = "ATC")
@Entity
public class ATC {

  @Id
  @ApiModelProperty(value = "ATC分類碼", example = "R01AB07", required = true)
  @Column(name = "CODE", nullable = false)
  private String code;
  
  @ApiModelProperty(value = "存放在Redis中HASH的field值", example = "1", required = false)
  @Column(name = "REDIS_ID")
  @JsonIgnore
  private Integer redisId;
  
  @ApiModelProperty(value = "說明", example = "Oxymetazoline", required = false)
  @Column(name = "NOTE", nullable = false)
  private String note;
  
  @ApiModelProperty(value = "碼長", example = "3", required = false)
  @Column(name = "LENG")
  private Integer leng;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Integer getRedisId() {
    return redisId;
  }

  public void setRedisId(Integer redisId) {
    this.redisId = redisId;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public Integer getLeng() {
    return leng;
  }

  public void setLeng(Integer leng) {
    this.leng = leng;
  }
  
}
