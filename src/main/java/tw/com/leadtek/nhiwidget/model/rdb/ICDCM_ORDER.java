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

@Table(name = "ICDCM_ORDER")
@Entity
public class ICDCM_ORDER {
	/**
	 * 主鍵 複合主鍵不能用@Id，需要用@EmbeddedId。插入資料的時候必須手工賦值
	 */
	@EmbeddedId
	private ICDCM_ORDER_KEYS icdcmorderPK;

	/**
	 * 診斷碼出現該醫令的平均數
	 */
	@Column(name = "AVERAGE")
	private float average;

	/**
	 * 上限
	 */
	@Column(name = "ULIMIT")
	private float ulimit;

	/**
	 * 下限
	 */
	@Column(name = "LLIMIT")
	private float llimit;

	/**
	 * 建立時間
	 */
	@Column(name = "UPDATE_AT")
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateAT;

	public float getAverage() {
		return average;
	}

	public void setAverage(float average) {
		this.average = average;
	}

	public float getUlimit() {
		return ulimit;
	}

	public void setUlimit(float ulimit) {
		this.ulimit = ulimit;
	}

	public float getLlimit() {
		return llimit;
	}

	public void setLlimit(float llimit) {
		this.llimit = llimit;
	}

	public Date getUpdateAT() {
		return updateAT;
	}

	public void setUpdateAT(Date updateAT) {
		this.updateAT = updateAT;
	}

	public ICDCM_ORDER_KEYS getIcdcmorderPK() {
		return icdcmorderPK;
	}

	public void setIcdcmorderPK(ICDCM_ORDER_KEYS icdcmorderPK) {
		this.icdcmorderPK = icdcmorderPK;
	}
	
}
