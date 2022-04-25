package tw.com.leadtek.nhiwidget.model.rdb;

import java.io.Serializable;

import javax.persistence.Embeddable;
/**
 * 主鍵 
 * 代表複合主鍵的實體bean需要實現Serializable介面
 */
//@Embeddable
public class ICDCM_DRUG_KEYS implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 診斷碼
	 */
	private String icdcm;
	/**
	 * 藥品、衛材代碼
	 */
	private String drug;
	/**
	 * 資料格式
	   10: 門急診
	   20: 住院
	 */
	private String dataformat;
	
	public ICDCM_DRUG_KEYS() {}
	
	public ICDCM_DRUG_KEYS(String icdcm, String drug, String dataformat) {
		this.icdcm = icdcm;
		this.drug = drug;
		this.dataformat = dataformat;
	}
	
	@Override
	public String toString() {
		return "ICDCM_DRUG_KEYS {icdcm="+icdcm+" ,drug="+drug+" ,dataformat="+dataformat+ "}";
		
	}

	public String getIcdcm() {
		return icdcm;
	}

	public void setIcdcm(String icdcm) {
		this.icdcm = icdcm;
	}

	public String getDrug() {
		return drug;
	}

	public void setDrug(String drug) {
		this.drug = drug;
	}

	public String getDataformat() {
		return dataformat;
	}

	public void setDataformat(String dataformat) {
		this.dataformat = dataformat;
	}
	
    @Override
    public int hashCode() {
      return ("ICDCM_DRUG_KEYS {icdcm="+icdcm+" ,drug="+drug+" ,dataformat="+dataformat+ "}").hashCode();
    }

    @Override
    public boolean equals(Object object) {
      if (object instanceof ICDCM_DRUG_KEYS) {
        ICDCM_DRUG_KEYS pk = (ICDCM_DRUG_KEYS) object;
        return icdcm.equals(pk.getIcdcm()) && drug.equals(pk.getDrug()) && dataformat.equals(pk.getDataformat());
      } else {
        return false;
      }
    }

}
