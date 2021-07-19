/**
 * Created on 2021/2/2.
 */
package tw.com.leadtek.nhiwidget.model.redis;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderCode extends CodeBaseLongId {

  /**
   * 支付點數
   */
  protected int p;

  /**
   * 生效日
   */
  protected Date sDate;

  /**
   * 失效日
   */
  protected Date eDate;

  /**
   * 項目細項
   */
  protected String detail;

  /**
   * 細項分類
   */
  protected String detailCat;

  /**
   * 醫療等級
   */
  protected String level;

  /**
   * 離島
   */
  protected String outIsland;

  /**
   * 法規
   */
  protected String law;

  /**
   * 條件
   */
  protected String con;

  public OrderCode() {

  }

  public OrderCode(long id, String code, String desc, String descEn) {
    this.id = new Long(id);
    setCode(code);
    if (descEn != null && descEn.length() > 0) {
      setDescEn(descEn);
    }
    setDesc(desc);
    this.category = "ORDER";
  }

  public int getP() {
    return p;
  }

  public void setP(int p) {
    this.p = p;
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

  public String getDetail() {
    return detail;
  }

  public void setDetail(String detail) {
    if (detail == null || detail.length() == 0) {
      return;
    }
    this.detail = detail;
  }

  public String getDetailCat() {
    return detailCat;
  }

  public void setDetailCat(String detailCat) {
    if (detailCat == null || detailCat.length() == 0) {
      return;
    }
    this.detailCat = detailCat;
  }

  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = level;
  }

  public String getOutIsland() {
    return outIsland;
  }

  public void setOutIsland(String outIsland) {
    if (outIsland == null || outIsland.length() == 0) {
      return;
    }
    this.outIsland = outIsland;
  }

  public String getLaw() {
    return law;
  }

  public void setLaw(String law) {
    if (law == null || law.length() == 0) {
      return;
    }
    this.law = law;
  }

  public String getCon() {
    return con;
  }

  public void setCon(String con) {
    if (con == null || con.length() == 0) {
      return;
    }
    this.con = con;
  }
  
  @Override
  public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append("OrderCode [id=");
      sb.append(id);
      sb.append(", code=");
      sb.append(code);
      sb.append(", desc=");
      sb.append(desc);
      sb.append(", descEn=");
      sb.append(descEn);
      sb.append(", catetory=");
      sb.append(category);
      sb.append(", p=");
      sb.append(p);
      sb.append(", sDate=");
      sb.append(sDate);
      sb.append(", eDate=");
      sb.append(eDate);
      sb.append(", detail=");
      sb.append(detail);
      sb.append(", detailCat=");
      sb.append(detailCat);
      sb.append(", level=");
      sb.append(level);
      sb.append(", outIsland=");
      sb.append(outIsland);
      sb.append(", law=");
      sb.append(law);
      sb.append(", con=");
      sb.append(con);
      sb.append("]");
      return sb.toString();
  }

}
