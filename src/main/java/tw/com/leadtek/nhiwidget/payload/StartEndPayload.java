/**
 * Created on 2021/10/7.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("id,生效起訖日")
public class StartEndPayload implements Serializable {

  private static final long serialVersionUID = 3721279925499143892L;

  @ApiModelProperty(value = "序號", example = "1", required = false)
  protected Long id;

  @ApiModelProperty(value = "生效日", example = "2022/01/01", required = false)
  @JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
  protected Date sdate;

  @ApiModelProperty(value = "生效訖日", example = "2022/06/30", required = false)
  @JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
  protected Date edate;

  public StartEndPayload() {

  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Date getSdate() {
    return sdate;
  }

  public void setSdate(Date sdate) {
    this.sdate = sdate;
  }

  public Date getEdate() {
    return edate;
  }

  public void setEdate(Date edate) {
    this.edate = edate;
  }

}
