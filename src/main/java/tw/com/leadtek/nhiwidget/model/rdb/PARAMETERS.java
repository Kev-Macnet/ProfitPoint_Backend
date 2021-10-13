/**
 * Created on 2020/9/23.
 */
package tw.com.leadtek.nhiwidget.model.rdb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.service.ParametersService;

@ApiModel("參數設定")
@Table(name = "PARAMETERS")
@Entity
public class PARAMETERS {
  
  public static final int TYPE_INTEGER = 1;
  
  public static final int TYPE_FLOAT = 2;
  
  public static final int TYPE_STRING = 3;
  
  public static final int TYPE_LONG = 4;

  @Id
  @ApiModelProperty(value = "參數id", example = "1", required = true)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", nullable = false)
  protected Long id;
  
  @ApiModelProperty(value = "參數類別", example = "SYS", required = true)
  @Column(name = "CAT", length = 30)
  private String cat;
  
  @ApiModelProperty(value = "參數名稱", example = "PageCount", required = true)
  @Column(name = "NAME", length = 50)
  private String name;
  
  @ApiModelProperty(value = "參數值", example = "50", required = true)
  @Column(name = "VAL", length = 300)
  private String value;
  
  @ApiModelProperty(value = "參數值資料型態", example = "50", required = true)
  @Column(name = "DATA_TYPE", length = 300)
  private Integer dataType;
  
  @ApiModelProperty(value = "參數說明", example = "每頁預設顯示筆數", required = false)
  @Column(name = "NOTE", length = 100)
  private String note;
  
  @ApiModelProperty(value = "生效日", example = "1", required = false)
  @Column(name = "START_DATE")
  private Date startDate;
  
  @ApiModelProperty(value = "失效日", example = "1", required = false)
  @Column(name = "END_DATE")
  private Date endDate;
  
  @ApiModelProperty(value = "更新時間", example = "2021/05/14 11:23:00", required = true)
  @Column(name = "UPDATE_AT", length = 100)
  private Date updateAt;
  
  public PARAMETERS() {
    
  }
  
  public PARAMETERS(String cat, String name, String value, int dataType, String note) {
    this.cat = cat;
    this.dataType = dataType;
    this.name = name;
    this.value = value;
    this.note = note;
    this.updateAt = new Date();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public String getCat() {
    return cat;
  }

  public void setCat(String cat) {
    this.cat = cat;
  }

  public Integer getDataType() {
    return dataType;
  }

  public void setDataType(Integer dataType) {
    this.dataType = dataType;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public Date getUpdateAt() {
    return updateAt;
  }

  public void setUpdateAt(Date updateAt) {
    this.updateAt = updateAt;
  }
  
  public void setStartEndDate(String start, String end) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    try {
      startDate = sdf.parse(start);
      endDate = sdf.parse(end);
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

}
