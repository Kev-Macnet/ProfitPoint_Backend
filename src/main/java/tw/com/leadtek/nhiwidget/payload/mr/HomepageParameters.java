/**
 * Created on 2021/12/29.
 */
package tw.com.leadtek.nhiwidget.payload.mr;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.apache.poi.ss.usermodel.DateUtil;
import tw.com.leadtek.nhiwidget.payload.MRCount;
import tw.com.leadtek.tools.DateTool;

/**
 * 首頁 request 用到的參數
 * 
 * @author kenlai
 *
 */
public class HomepageParameters {

  /**
   * 申報年，格式西元年 yyyy
   */
  protected String applY;

  /**
   * 申報月，格式 M
   */
  protected String applM;

  /**
   * 申報民國年月
   */
  protected String applYM;

  /**
   * 起始日期，格式 yyyy/MM/dd
   */
  protected String sdate;

  /**
   * 結束日期，格式 yyyy/MM/dd
   */
  protected String edate;

  protected Date sDate;

  protected Date eDate;

  /**
   * 就醫類別，門急診:10，住院:20，不分: 00
   */
  protected String dataFormat;

  /**
   * 科別，00:不分科，01:家醫科，02:內科，03:外科...
   */
  protected String funcType;

  /**
   * 醫護姓名
   */
  protected String prsnName;

  public HomepageParameters() {

  }

  public HomepageParameters(String applY, String applM, String sdate, String edate,
      String dataFormat, String funcType, String prsnName) {
    initialApplYM(applY, applM);
    
    if (sdate != null && edate != null) {
      SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
       try {
        sDate = new Date(sdf.parse(sdate).getTime());
         eDate = new Date(sdf.parse(edate).getTime());
      } catch (ParseException e) {
        e.printStackTrace();
      }    
    }
    if (dataFormat == null || "00".equals(dataFormat) || dataFormat.indexOf(',') > 0 || dataFormat.indexOf(' ') > 0) {
      this.dataFormat = null;
    } else {
      this.dataFormat = dataFormat;
    }
    this.funcType = funcType;
    this.prsnName = prsnName;
  }
  
  public void initialApplYM(String applY, String applM) {
    if (applY == null || applY.length() == 0) {
      return;
    }
    int applYMInteger = 0;
    int minusYear = (applY.length() == 4) ? 1911 : 0;
    int year = Integer.parseInt(applY) - minusYear;
    if (applM != null && applM.length() > 0) {
      applYMInteger = year * 100 + Integer.parseInt(applM);
      applYM = String.valueOf(applYMInteger);
    }
  }

  public String getApplY() {
    return applY;
  }

  public void setApplY(String applY) {
    this.applY = applY;
  }

  public String getApplM() {
    return applM;
  }

  public void setApplM(String applM) {
    this.applM = applM;
  }

  public String getApplYM() {
    return applYM;
  }

  public void setApplYM(String applYM) {
    this.applYM = applYM;
  }

  public String getSdate() {
    return sdate;
  }

  public void setSdate(String sdate) {
    this.sdate = sdate;
  }

  public String getEdate() {
    return edate;
  }

  public void setEdate(String edate) {
    this.edate = edate;
  }

  public Date getsDate() {
    return sDate;
  }

  public void setsDate(Date sDate) {
    this.sDate = sDate;
  }

  public Date geteDate() {
    return eDate;
  }

  public void seteDate(Date eDate) {
    this.eDate = eDate;
  }

  public String getDataFormat() {
    return dataFormat;
  }

  public void setDataFormat(String dataFormat) {
    this.dataFormat = dataFormat;
  }

  public String getFuncType() {
    return funcType;
  }

  public void setFuncType(String funcType) {
    this.funcType = funcType;
  }

  public String getPrsnName() {
    return prsnName;
  }

  public void setPrsnName(String prsnName) {
    this.prsnName = prsnName;
  }

}
