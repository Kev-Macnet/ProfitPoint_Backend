package tw.com.leadtek.nhiwidget.model.rdb;

import java.io.Serializable;

import javax.persistence.Embeddable;
/**
 * 主鍵 
 * 代表複合主鍵的實體bean需要實現Serializable介面
 */
@Embeddable
public class ICDCM_ICDOP_KEYS implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 診斷碼
	 */
	private String icdcm;
	/**
	 * 手術(處置)碼
	 */
	private String icdop;
	
	/**
	 * 資料格式
	   10: 門急診
	   20: 住院
	 */
	private String dataFormat;
	
	public ICDCM_ICDOP_KEYS() {}
	
	public ICDCM_ICDOP_KEYS(String icdcm, String icdop, String dataformat) {
		this.icdcm = icdcm;
		this.icdop = icdop;
		this.dataFormat = dataformat;
		
	}
	
	@Override
	public String toString() {
		return "ICDCM_ICDOP_KEYS {icdcm="+icdcm+" ,icdop="+icdop+" ,dataformat="+dataFormat+ "}";
		
	}

	public String getIcdcm() {
		return icdcm;
	}

	public void setIcdcm(String icdcm) {
		this.icdcm = icdcm;
	}

	public String getIcdop() {
		return icdop;
	}

	public void setIcdop(String icdop) {
		this.icdop = icdop;
	}

	public String getDataFormat() {
		return dataFormat;
	}

	public void setDataFormat(String dataFormat) {
		this.dataFormat = dataFormat;
	}

    @Override
    public int hashCode() {
      return icdcm.hashCode() + icdop.hashCode() + dataFormat.hashCode();
    }

    @Override
    public boolean equals(Object object) {
      if (object instanceof ICDCM_ICDOP_KEYS) {
        ICDCM_ICDOP_KEYS pk = (ICDCM_ICDOP_KEYS) object;
        return icdcm.equals(pk.getIcdcm()) && icdop.equals(pk.getIcdop())
            && dataFormat.equals(pk.getDataFormat());
      }
      return false;
    }
}
