/**
 * Created on 2021/4/1.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import javax.persistence.Column;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.IP_P;
import tw.com.leadtek.nhiwidget.model.rdb.OP_P;
import tw.com.leadtek.nhiwidget.service.CodeTableService;

@ApiModel("醫令，Medical Order")
public class MO implements Serializable {

  private static final long serialVersionUID = 5539370672047222230L;

  @ApiModelProperty(value = "醫令序號", example = "1", required = false)
  private Integer orderSeqNo;
  
  @ApiModelProperty(value = "院內碼", required = false)
  private String inhCode;
  
  @ApiModelProperty(value = "就醫科別", required = false)
  private String funcType;
  
  @ApiModelProperty(value = "會診科別", required = false)
  private String conFuncType;
  
  @ApiModelProperty(value = "醫令調劑方式", required = false)
  private String medType;
  
  @ApiModelProperty(value = "醫令類別", required = false)
  private String orderType;
  
  @ApiModelProperty(value = "醫令代碼", required = false)
  private String orderCode;
  
  @ApiModelProperty(value = "藥品(項目)代號(門急診)", required = false)
  private String drugNo;
  
  @ApiModelProperty(value = "藥品用量", required = false)
  private String drugUse;
  
  @ApiModelProperty(value = "總量", required = false)
  private Integer totalQ;
  
  @ApiModelProperty(value = "單價", required = false)
  private Float unitP;
  
  @ApiModelProperty(value = "點數", required = false)
  private Integer totalDot;
  
  @ApiModelProperty(value = "費用狀態，1:健保給付，2:自費，3:勞保，4:其他", required = false)
  private Integer payBy;
  
  @ApiModelProperty(value = "申報狀態，0:不申報，1:要申報", required = false)
  private Integer applStatus;
  
  @ApiModelProperty(value = "執行醫事人員代號", required = false)
  private String prsnId;
  
  @ApiModelProperty(value = "藥品給藥日份", required = false)
  private Integer drugDay;
  
  @ApiModelProperty(value = "藥品使用頻率", required = false)
  private String drugFre;
  
  @ApiModelProperty(value = "給藥途徑/作用部位", required = false)
  private String drugPath;
  
  @ApiModelProperty(value = "藥品批號", required = false)
  private String drugSerialNo;
  
  @ApiModelProperty(value = "事前審查受理編號", required = false)
  private String preNo;
  
  @ApiModelProperty(value = "診療之部位", required = false)
  private String curePath;
  
  @ApiModelProperty(value = "支付成數", required = false)
  private String payRate;
  
  @ApiModelProperty(value = "影像來源", required = false)
  private String imgSource;
  
  @ApiModelProperty(value = "未列項註記", required = false)
  private String nonListMark;
  
  @ApiModelProperty(value = "未列項名稱", required = false)
  private String nonListName;
  
  @ApiModelProperty(value = "慢性病連續處方箋、同一療程及排程檢查案件註記", example = "保險對象本次就醫係由他院轉診而來", required = false)
  private String chrMark;
  
  @ApiModelProperty(value = "委託或受託執行轉(代)檢醫事機構代號", required = false)
  private String commHospId;
  
  @ApiModelProperty(value = "自費特材群組序號/其他特殊註記", required = false)
  private String ownExpMtrNo;
  
  @ApiModelProperty(value = "病床號", required = false)
  private String bedNo;
  
  @ApiModelProperty(value = "TW-DRGS計算號", required = false)
  private Integer twDrgsCalcu;
  
  @ApiModelProperty(value = "切帳前筆資料", required = false)
  private String partAccoData;
  
  @ApiModelProperty(value = "器官捐贈者資料", required = false)
  private String donater;
  
  public Integer getOrderSeqNo() {
    return orderSeqNo;
  }

  public void setOrderSeqNo(Integer orderSeqNo) {
    this.orderSeqNo = orderSeqNo;
  }

  public String getInhCode() {
    return inhCode;
  }

  public void setInhCode(String inhCode) {
    this.inhCode = inhCode;
  }

  public String getFuncType() {
    return funcType;
  }

  public void setFuncType(String funcType) {
    this.funcType = funcType;
  }

  public String getMedType() {
    return medType;
  }

  public void setMedType(String medType) {
    this.medType = medType;
  }

  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }

  public String getDrugNo() {
    return drugNo;
  }

  public void setDrugNo(String drugNo) {
    this.drugNo = drugNo;
  }

  public String getDrugUse() {
    return drugUse;
  }

  public void setDrugUse(String drugUse) {
    this.drugUse = drugUse;
  }

  public Integer getTotalQ() {
    return totalQ;
  }

  public void setTotalQ(Integer totalQ) {
    this.totalQ = totalQ;
  }

  public Float getUnitP() {
    return unitP;
  }

  public void setUnitP(Float unitP) {
    this.unitP = unitP;
  }

  public Integer getTotalDot() {
    return totalDot;
  }

  public void setTotalDot(Integer totalDot) {
    this.totalDot = totalDot;
  }

  public Integer getPayBy() {
    return payBy;
  }

  public void setPayBy(Integer payBy) {
    this.payBy = payBy;
  }

  public Integer getApplStatus() {
    return applStatus;
  }

  public void setApplStatus(Integer applStatus) {
    this.applStatus = applStatus;
  }

  public String getPrsnId() {
    return prsnId;
  }

  public void setPrsnId(String prsnId) {
    this.prsnId = prsnId;
  }

  public Integer getDrugDay() {
    return drugDay;
  }

  public void setDrugDay(Integer drugDay) {
    this.drugDay = drugDay;
  }

  public String getDrugFre() {
    return drugFre;
  }

  public void setDrugFre(String drugFre) {
    this.drugFre = drugFre;
  }

  public String getDrugPath() {
    return drugPath;
  }

  public void setDrugPath(String drugPath) {
    this.drugPath = drugPath;
  }

  public String getDrugSerialNo() {
    return drugSerialNo;
  }

  public void setDrugSerialNo(String drugSerialNo) {
    this.drugSerialNo = drugSerialNo;
  }

  public String getPreNo() {
    return preNo;
  }

  public void setPreNo(String preNo) {
    this.preNo = preNo;
  }

  public String getCurePath() {
    return curePath;
  }

  public void setCurePath(String curePath) {
    this.curePath = curePath;
  }

  public String getPayRate() {
    return payRate;
  }

  public void setPayRate(String payRate) {
    this.payRate = payRate;
  }

  public String getImgSource() {
    return imgSource;
  }

  public void setImgSource(String imgSource) {
    this.imgSource = imgSource;
  }

  public String getNonListMark() {
    return nonListMark;
  }

  public void setNonListMark(String nonListMark) {
    this.nonListMark = nonListMark;
  }

  public String getNonListName() {
    return nonListName;
  }

  public void setNonListName(String nonListName) {
    this.nonListName = nonListName;
  }

  public String getChrMark() {
    return chrMark;
  }

  public void setChrMark(String chrMark) {
    this.chrMark = chrMark;
  }

  public String getCommHospId() {
    return commHospId;
  }

  public void setCommHospId(String commHospId) {
    this.commHospId = commHospId;
  }

  public String getOwnExpMtrNo() {
    return ownExpMtrNo;
  }

  public void setOwnExpMtrNo(String ownExpMtrNo) {
    this.ownExpMtrNo = ownExpMtrNo;
  }
  
  public String getConFuncType() {
    return conFuncType;
  }

  public void setConFuncType(String conFuncType) {
    this.conFuncType = conFuncType;
  }
  
  public String getOrderCode() {
    return orderCode;
  }

  public void setOrderCode(String orderCode) {
    this.orderCode = orderCode;
  }
  
  public String getBedNo() {
    return bedNo;
  }

  public void setBedNo(String bedNo) {
    this.bedNo = bedNo;
  }
  
  public Integer getTwDrgsCalcu() {
    return twDrgsCalcu;
  }

  public void setTwDrgsCalcu(Integer twDrgsCalcu) {
    this.twDrgsCalcu = twDrgsCalcu;
  }
  
  public String getPartAccoData() {
    return partAccoData;
  }

  public void setPartAccoData(String partAccoData) {
    this.partAccoData = partAccoData;
  }
  
  public String getDonater() {
    return donater;
  }

  public void setDonater(String donater) {
    this.donater = donater;
  }

  public void setOPPData(OP_P opp, CodeTableService cts) {
    this.orderSeqNo = opp.getOrderSeqNo();
    this.inhCode = opp.getInhCode();
    this.funcType = CodeTableService.getDesc(cts, "FUNC_TYPE", opp.getFuncType());
    this.medType = CodeTableService.getDesc(cts, "OP_MED_TYPE", opp.getMedType());
    this.orderType = CodeTableService.getDesc(cts, "ORDER_TYPE", opp.getOrderType());
    this.drugNo = opp.getDrugNo();
    this.drugUse = opp.getDrugUse();
    this.totalQ = opp.getTotalQ();
    this.unitP = opp.getUnitP();
    this.totalDot = opp.getTotalDot();
    this.payBy = opp.getPayBy();
    this.applStatus = opp.getApplStatus();
    this.prsnId = opp.getPrsnId();
    this.drugDay = opp.getDrugDay();
    this.drugFre = opp.getDrugFre();
    this.drugPath = opp.getDrugPath();
    this.drugSerialNo = opp.getDrugSerialNo();
    this.preNo = opp.getPreNo();
    this.curePath = opp.getCurePath();
    this.payRate = opp.getPayRate();
    this.imgSource = opp.getImgSource();
    this.nonListMark = opp.getNonListMark();
    this.nonListName = opp.getNonListName();
    this.chrMark = opp.getChrMark();
    this.commHospId = opp.getCommHospId();
    this.ownExpMtrNo = opp.getOwnExpMtrNo();
  }
  
  public void setIPPData(IP_P ipp, CodeTableService cts) {
    // 醫令序
    this.orderSeqNo = ipp.getOrderSeqNo();
    // 院內碼
    this.inhCode = ipp.getInhCode();
    // 就醫科別
    this.funcType = CodeTableService.getDesc(cts, "FUNC_TYPE", ipp.getFuncType());
    // 會診科別
    this.conFuncType = CodeTableService.getDesc(cts, "FUNC_TYPE", ipp.getConFuncType());
    // 醫令類別
    this.orderType = CodeTableService.getDesc(cts, "ORDER_TYPE", ipp.getOrderType());
    // 醫令代碼
    this.orderCode = CodeTableService.getDesc(cts, "ORDER", ipp.getOrderCode()); 
    // 藥品用量
    this.drugUse = ipp.getDrugUse();
    // 總量
    this.totalQ = ipp.getTotalQ();
    // 單價
    this.unitP = ipp.getUnitP();
    // 點數
    this.totalDot = ipp.getTotalDot();
    // 費用狀態
    this.payBy = ipp.getPayBy();
    // 申報狀態
    this.applStatus = ipp.getApplStatus();
    // 執行醫事人員代碼
    this.prsnId = ipp.getPrsnId();
    // 病床號
    this.bedNo = ipp.getBedNo();
    // 藥品使用頻率
    this.drugFre = ipp.getDrugFre();
    // 給藥途徑/作用部位
    this.drugPath = ipp.getDrugPath();
    // 藥品批號
    this.drugSerialNo = ipp.getDrugSerialNo();
    // TW-DRGs計算
    this.twDrgsCalcu = ipp.getTwDrgsCalcu();
    // 診療部位
    this.curePath = ipp.getCurePath();
    // 支付成數
    this.payRate = ipp.getPayRate();
    // 影像來源
    this.imgSource = ipp.getImgSource();
    // 未列像註記
    this.nonListMark = ipp.getNonListMark();
    // 未列像名稱
    this.nonListName = ipp.getNonListName();
    // 切帳前筆資料
    this.partAccoData = ipp.getPartAccoData();
    // 器官捐贈者資料
    this.donater = ipp.getDonater();
    // 委託或受託執行轉（代）檢醫事機構代號
    this.commHospId = ipp.getCommHospId();
    // 自費特材群組序號
    this.ownExpMtrNo = ipp.getOwnExpMtrNo();
    // 事前審查受理編號
    this.preNo = ipp.getPreNo();
  }
}
