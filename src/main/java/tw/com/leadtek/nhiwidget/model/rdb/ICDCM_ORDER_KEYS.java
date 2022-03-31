package tw.com.leadtek.nhiwidget.model.rdb;

import java.io.Serializable;

import javax.persistence.Embeddable;
/**
 * 主鍵 
 * 代表複合主鍵的實體bean需要實現Serializable介面
 */
@Embeddable
public class ICDCM_ORDER_KEYS implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 診斷碼
	 */
	private String icdcm;
	/**
	 * 醫令代碼
	 */
	private String orderCode;
	/**
	 * 資料格式  
	 * 10: 門急診
	 * 20: 住院
	 */
	private String dataFormat;
	
	
	
	public ICDCM_ORDER_KEYS() {}
	
	public ICDCM_ORDER_KEYS(String icdcm, String orderCode, String dataFormat) {
		this.icdcm = icdcm;
		this.orderCode = orderCode;
		this.dataFormat = dataFormat;
	}
	@Override
	public String toString() {
		return "ICDCM_ORDER_KEYS {icdcm="+icdcm+" ,orderCode="+orderCode+" ,dataFormat="+dataFormat+ "}";
		
	}

	public String getIcdcm() {
		return icdcm;
	}

	public void setIcdcm(String icdcm) {
		this.icdcm = icdcm;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getDataFormat() {
		return dataFormat;
	}

	public void setDataFormat(String dataFormat) {
		this.dataFormat = dataFormat;
	}
}
