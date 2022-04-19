package tw.com.leadtek.nhiwidget.model.rdb;

import java.io.Serializable;

import javax.persistence.Embeddable;

/**
 * 主鍵 代表複合主鍵的實體bean需要實現Serializable介面
 */
@Embeddable
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
	 * 資料格式 10: 門急診 20: 住院
	 */
	private String dataFormat;

	public ICDCM_DRUG_KEYS() {
	}

	public ICDCM_DRUG_KEYS(String icdcm, String drug, String dataformat) {
		this.icdcm = icdcm;
		this.drug = drug;
		this.dataFormat = dataformat;
	}

	@Override
	public String toString() {
		return "ICDCM_DRUG_KEYS {icdcm=" + icdcm + " ,drug=" + drug + " ,dataformat=" + dataFormat + "}";

	}

	@Override
	public int hashCode() {
		return icdcm.hashCode() + drug.hashCode() + dataFormat.hashCode();
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

	public String getDataFormat() {
		return dataFormat;
	}

	public void setDataFormat(String dataFormat) {
		this.dataFormat = dataFormat;
	}

}
