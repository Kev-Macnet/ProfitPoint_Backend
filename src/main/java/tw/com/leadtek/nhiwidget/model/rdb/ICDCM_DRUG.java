/**
 * Created on 2022/03/30.
 */
package tw.com.leadtek.nhiwidget.model.rdb;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

@Table(name = "ICDCM_DRUG")
@Entity
public class ICDCM_DRUG {
	/**
	 * 主鍵 複合主鍵不能用@Id，需要用@EmbeddedId。插入資料的時候必須手工賦值
	 */
	@EmbeddedId
	private ICDCM_DRUG_KEYS icdcmdrugPK;

	/**
	 * 是否為藥品 1: 藥品 2: 衛材
	 */
	@Column(name = "IS_DRUG")
	private int isdurug;

	/**
	 * 該診斷碼出現該藥品/衛材的總次數
	 */
	@Column(name = "TOTAL")
	private int total;

	/**
	 * 該藥品/衛材出現次數百分比，值為0~100
	 */
	@Column(name = "PERCENT")
	private int percent;

	/**
	 * 建立時間
	 */
	@Column(name = "UPDATE_AT")
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateAT;

	public int getIsdurug() {
		return isdurug;
	}

	public void setIsdurug(int isdurug) {
		this.isdurug = isdurug;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getPercent() {
		return percent;
	}

	public void setPercent(int percent) {
		this.percent = percent;
	}

	public Date getUpdateAT() {
		return updateAT;
	}

	public void setUpdateAT(Date updateAT) {
		this.updateAT = updateAT;
	}

	public ICDCM_DRUG_KEYS getIcdcmdrugPK() {
		return icdcmdrugPK;
	}

	public void setIcdcmdrugPK(ICDCM_DRUG_KEYS icdcmdrugPK) {
		this.icdcmdrugPK = icdcmdrugPK;
	}
	
	

}
