/**
 * Created on 2021/10/6.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_THRESHOLD;

@ApiModel("應用比例偏高醫令")
public class HighRatioOrder extends RareICDPayload implements Serializable {

  private static final long serialVersionUID = -4672035786152451117L;

  @ApiModelProperty(value = "是否啟用門診單一就診記錄申報數超過幾次跳提示。false:不啟用，true:啟用", example = "true",
      required = false)
  private Boolean opTimesStatus;

  @ApiModelProperty(value = "門診單一就診記錄申報數超過幾次跳提示", example = "100", required = false)
  private Integer opTimes;

  @ApiModelProperty(value = "是否啟用門診單一病患幾天內記錄申報數超過幾次跳提示。false:不啟用，true:啟用", example = "true",
      required = false)
  private Boolean opTimesDStatus;

  @ApiModelProperty(value = "門診單一病患幾天內記錄申報數超過跳提示", example = "100", required = false)
  private Integer opTimesDay;

  @ApiModelProperty(value = "門診單一病患申報數超過次數跳提示", example = "100", required = false)
  private Integer opTimesD;

  @ApiModelProperty(value = "是否啟用住院單一就診記錄申報數超過幾次跳提示。false:不啟用，true:啟用", example = "true",
      required = false)
  private Boolean ipTimesStatus;

  @ApiModelProperty(value = "住院單一就診記錄申報數超過幾次跳提示", example = "10", required = false)
  private Integer ipTimes;

  @ApiModelProperty(value = "是否啟用住院單一病患幾天內記錄申報數超過幾次跳提示。false:不啟用，true:啟用", example = "true",
      required = false)
  private Boolean ipTimesDStatus;

  @ApiModelProperty(value = "住院單一病患幾天內記錄申報數超過跳提示", example = "3", required = false)
  private Integer ipTimesDay;

  @ApiModelProperty(value = "住院單一病患申報數超過次數跳提示", example = "100", required = false)
  private Integer ipTimesD;

  public HighRatioOrder() {

  }

  public HighRatioOrder(CODE_THRESHOLD ct) {
    id = ct.getId();
    code = ct.getCode();
    sdate = ct.getStartDate();
    edate = ct.getEndDate();
    ip = ct.getDataFormat().equals("20");
    op = ct.getDataFormat().equals("10");
    both = ct.getDataFormat().equals("00");

    opTimesStatus = (ct.getOpTimesMStatus().intValue() == 1);
    opTimes = ct.getOpTimes();
    opTimesDStatus = ct.getOpTimesDStatus().intValue() == 1;
    opTimesDay = ct.getOpTimesDay();
    opTimesD = ct.getOpTimesD();

    opTimesMStatus = (ct.getOpTimesMStatus().intValue() == 1);
    opTimesM = ct.getOpTimesM();
    opTimes6MStatus = (ct.getOpTimes6mStatus().intValue() == 1);
    opTimes6M = ct.getOpTimes6m();

    ipTimesStatus = (ct.getIpTimesMStatus().intValue() == 1);
    ipTimes = ct.getIpTimes();
    ipTimesDStatus = ct.getIpTimesDStatus().intValue() == 1;
    ipTimesDay = ct.getIpTimesDay();
    ipTimesD = ct.getIpTimesD();
    ipTimesMStatus = (ct.getIpTimesMStatus().intValue() == 1);
    ipTimesM = ct.getIpTimesM();
    ipTimes6MStatus = (ct.getIpTimes6mStatus().intValue() == 1);
    ipTimes6M = ct.getIpTimes6m();
    status = ct.getStatus().intValue() == 1;
  }

  public CODE_THRESHOLD toDB() {
    CODE_THRESHOLD result = new CODE_THRESHOLD();
    if (id != null) {
      result.setId(id);
    }
    result.setCodeType(new Integer(CODE_TYPE_ORDER));
    result.setCode(code);
    result.setDescChi(name);

    result.setStartDate(sdate);
    result.setEndDate(edate);
    
    if ((both != null && both.booleanValue())|| (ip && op)) {
      result.setDataFormat("00");
    } else if (ip) {
      result.setDataFormat("20");
    } else if (op) {
      result.setDataFormat("10");
    } else {
      result.setDataFormat("-1");
    }


    if (opTimesStatus != null && opTimesStatus.booleanValue()) {
      result.setOpTimesStatus(1);
    } else {
      result.setOpTimesStatus(0);
    }
    result.setOpTimes(opTimes);
    if (opTimesDStatus != null && opTimesDStatus.booleanValue()) {
      result.setOpTimesDStatus(1);
    } else {
      result.setOpTimesDStatus(0);
    }
    result.setOpTimesDay(opTimesDay);
    result.setOpTimesD(opTimesD);

    if (opTimesMStatus != null && opTimesMStatus.booleanValue()) {
      result.setOpTimesMStatus(1);
    } else {
      result.setOpTimesMStatus(0);
    }
    result.setOpTimesM(opTimesM);

    if (opTimes6MStatus != null && opTimes6MStatus.booleanValue()) {
      result.setOpTimes6mStatus(1);
    } else {
      result.setOpTimes6mStatus(0);
    }
    result.setOpTimes6m(opTimes6M);

    if (ipTimesStatus != null && ipTimesStatus.booleanValue()) {
      result.setIpTimesStatus(1);
    } else {
      result.setIpTimesStatus(0);
    }
    result.setIpTimes(ipTimes);
    if (ipTimesDStatus != null && ipTimesDStatus.booleanValue()) {
      result.setIpTimesDStatus(1);
    } else {
      result.setIpTimesDStatus(0);
    }
    result.setIpTimesDay(ipTimesDay);
    result.setIpTimesD(ipTimesD);

    if (ipTimesMStatus != null && ipTimesMStatus.booleanValue()) {
      result.setIpTimesMStatus(1);
    } else {
      result.setIpTimesMStatus(0);
    }
    result.setIpTimesM(ipTimesM);

    if (ipTimes6MStatus != null && ipTimes6MStatus.booleanValue()) {
      result.setIpTimes6mStatus(1);
    } else {
      result.setIpTimes6mStatus(0);
    }
    result.setIpTimes6m(ipTimes6M);

    result.setStatus((status != null && status) ? 1 : 0);
    return result;
  }

  public Boolean getOpTimesStatus() {
    return opTimesStatus;
  }

  public void setOpTimesStatus(Boolean opTimesStatus) {
    this.opTimesStatus = opTimesStatus;
  }

  public Integer getOpTimes() {
    return opTimes;
  }

  public void setOpTimes(Integer opTimes) {
    this.opTimes = opTimes;
  }

  public Boolean getOpTimesDStatus() {
    return opTimesDStatus;
  }

  public void setOpTimesDStatus(Boolean opTimesDStatus) {
    this.opTimesDStatus = opTimesDStatus;
  }

  public Integer getOpTimesDay() {
    return opTimesDay;
  }

  public void setOpTimesDay(Integer opTimesDay) {
    this.opTimesDay = opTimesDay;
  }

  public Integer getOpTimesD() {
    return opTimesD;
  }

  public void setOpTimesD(Integer opTimesD) {
    this.opTimesD = opTimesD;
  }

  public Boolean getIpTimesStatus() {
    return ipTimesStatus;
  }

  public void setIpTimesStatus(Boolean ipTimesStatus) {
    this.ipTimesStatus = ipTimesStatus;
  }

  public Integer getIpTimes() {
    return ipTimes;
  }

  public void setIpTimes(Integer ipTimes) {
    this.ipTimes = ipTimes;
  }

  public Boolean getIpTimesDStatus() {
    return ipTimesDStatus;
  }

  public void setIpTimesDStatus(Boolean ipTimesDStatus) {
    this.ipTimesDStatus = ipTimesDStatus;
  }

  public Integer getIpTimesDay() {
    return ipTimesDay;
  }

  public void setIpTimesDay(Integer ipTimesDay) {
    this.ipTimesDay = ipTimesDay;
  }

  public Integer getIpTimesD() {
    return ipTimesD;
  }

  public void setIpTimesD(Integer ipTimesD) {
    this.ipTimesD = ipTimesD;
  }


}
