/**
 * Created on 2021/9/29.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.persistence.Column;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_THRESHOLD;

@ApiModel("罕見ICD詳細資料")
public class RareICDPayload extends StartEndPayload implements Serializable {

  protected static final long serialVersionUID = 747203358608875831L;

  public final static int CODE_TYPE_ICD = 1;

  public final static int CODE_TYPE_ORDER = 2;

  public final static int CODE_TYPE_DRUG = 3;

  @ApiModelProperty(value = "代碼", example = "J10.01", required = true)
  protected String code;

  @ApiModelProperty(value = "中文說明", example = "其他確認流感病毒所致流行性感冒併其他呼吸道表徵", required = true)
  protected String name;

  @ApiModelProperty(value = "門急診就醫", example = "true", required = false)
  protected Boolean op;

  @ApiModelProperty(value = "住院就醫", example = "true", required = false)
  protected Boolean ip;

  @ApiModelProperty(value = "門急診及住院皆適用", example = "true", required = false)
  protected Boolean both;

  @ApiModelProperty(value = "是否啟用門診月份應用超過幾次", example = "true", required = false)
  protected Boolean opTimesMStatus;

  @ApiModelProperty(value = "門診月份應用超過幾次", example = "100", required = false)
  protected Integer opTimesM;

  @ApiModelProperty(value = "是否啟用門診月份應用超過前6個月平均值幾次", example = "true", required = false)
  protected Boolean opTimes6MStatus;

  @ApiModelProperty(value = "門診月份應用超過前6個月平均值幾次", example = "100", required = false)
  protected Integer opTimes6M;

  @ApiModelProperty(value = "是否啟用住院月份應用超過幾次", example = "true", required = false)
  protected Boolean ipTimesMStatus;

  @ApiModelProperty(value = "住院月份應用超過幾次", example = "100", required = false)
  @Column(name = "IP_TIMES_M")
  protected Integer ipTimesM;

  @ApiModelProperty(value = "是否啟用住院月份應用超過前6個月平均值幾次", example = "true", required = false)
  protected Boolean ipTimes6MStatus;

  @ApiModelProperty(value = "住院月份應用超過前6個月平均值幾次", example = "100", required = false)
  protected Integer ipTimes6M;

  @ApiModelProperty(value = "設定啟用狀態", example = "true", required = false)
  protected Boolean status;

  public RareICDPayload() {

  }

  public RareICDPayload(CODE_THRESHOLD rareIcd) {
    id = rareIcd.getId();
    code = rareIcd.getCode();
    name = rareIcd.getDescChi();
    sdate = rareIcd.getStartDate();
    edate = rareIcd.getEndDate();
    ip = rareIcd.getDataFormat().equals("20");
    op = rareIcd.getDataFormat().equals("10");

    opTimesMStatus = (rareIcd.getOpTimesMStatus().intValue() == 1);
    opTimesM = rareIcd.getOpTimesM();
    opTimes6MStatus = (rareIcd.getOpTimes6mStatus().intValue() == 1);
    opTimes6M = rareIcd.getOpTimes6m();

    ipTimesMStatus = (rareIcd.getIpTimesMStatus().intValue() == 1);
    ipTimesM = rareIcd.getIpTimesM();
    ipTimes6MStatus = (rareIcd.getIpTimes6mStatus().intValue() == 1);
    ipTimes6M = rareIcd.getIpTimes6m();
    status = rareIcd.getStatus().intValue() == 1;

    if (CODE_THRESHOLD.DATA_FORMAT_OP_IP_OWNS.equals(rareIcd.getDataFormat())) {
      both = false;
      ip = true;
      op = true;
    }
    if (rareIcd.getDataFormat().equals("00")) {
      if ((opTimesM != null && ipTimesM != null) && (opTimesM.intValue() == ipTimesM.intValue())
          && (opTimes6M != null && ipTimes6M != null) && opTimes6M.intValue() == ipTimes6M.intValue()) {
        both = true;
      } else {
        both = false;
        ip = true;
        op = true;
      }
    }

  }

  public CODE_THRESHOLD toDB() {
    CODE_THRESHOLD result = new CODE_THRESHOLD();
    if (id != null) {
      result.setId(id);
    }
    result.setCodeType(Integer.valueOf(CODE_TYPE_ICD));
    result.setCode(code);
    result.setDescChi(name);

    result.setStartDate(sdate);
    result.setEndDate(edate);
    if ((both != null && both.booleanValue())) {
      result.setDataFormat("00");
    } else if ((ip && op)) {
      result.setDataFormat(CODE_THRESHOLD.DATA_FORMAT_OP_IP_OWNS);
    } else if (ip) {
      result.setDataFormat("20");
    } else if (op) {
      result.setDataFormat("10");
    } else {
      result.setDataFormat("-1");
    }

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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean getOp() {
    return op;
  }

  public void setOp(Boolean op) {
    this.op = op;
  }

  public Boolean getIp() {
    return ip;
  }

  public void setIp(Boolean ip) {
    this.ip = ip;
  }

  public Boolean getBoth() {
    return both;
  }

  public void setBoth(Boolean both) {
    this.both = both;
  }

  public Boolean getOpTimesMStatus() {
    return opTimesMStatus;
  }

  public void setOpTimesMStatus(Boolean opTimesMStatus) {
    this.opTimesMStatus = opTimesMStatus;
  }

  public Integer getOpTimesM() {
    return opTimesM;
  }

  public void setOpTimesM(Integer opTimesM) {
    this.opTimesM = opTimesM;
  }

  public Boolean getOpTimes6MStatus() {
    return opTimes6MStatus;
  }

  public void setOpTimes6MStatus(Boolean opTimes6MStatus) {
    this.opTimes6MStatus = opTimes6MStatus;
  }

  public Integer getOpTimes6M() {
    return opTimes6M;
  }

  public void setOpTimes6M(Integer opTimes6M) {
    this.opTimes6M = opTimes6M;
  }

  public Boolean getIpTimesMStatus() {
    return ipTimesMStatus;
  }

  public void setIpTimesMStatus(Boolean ipTimesMStatus) {
    this.ipTimesMStatus = ipTimesMStatus;
  }

  public Integer getIpTimesM() {
    return ipTimesM;
  }

  public void setIpTimesM(Integer ipTimesM) {
    this.ipTimesM = ipTimesM;
  }

  public Boolean getIpTimes6MStatus() {
    return ipTimes6MStatus;
  }

  public void setIpTimes6MStatus(Boolean ipTimes6MStatus) {
    this.ipTimes6MStatus = ipTimes6MStatus;
  }

  public Integer getIpTimes6M() {
    return ipTimes6M;
  }

  public void setIpTimes6M(Integer ipTimes6M) {
    this.ipTimes6M = ipTimes6M;
  }

  public Boolean getStatus() {
    return status;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }

}
