package tw.com.leadtek.nhiwidget.constant;

public enum LogType {
	
	FORGOT_PASSWORD("申請密碼清單"),

	MEDICAL_RECORD_STATUS_$2("疑問標示案件數"),
	
	MEDICAL_RECORD_STATUS_$1("評估不調整案件數"),
	
	MEDICAL_RECORD_STATUS_2("優化完成案件數"),
	
	MEDICAL_RECORD_STATUS_3("比對警示待確認案件數"),
	
	MEDICAL_RECORD_NOTIFYED("被通知次數記錄"),
	
	MEDICAL_RECORD_UNREAD("未讀取次數記錄"),
	
	IMPORT("資料匯入次數"),
	
	EXPORT("資料匯出次數"),
	
	ACTION("使用者操作紀錄"),
	
	SIGNIN("登出入時間");
	
	private String desc;

	private LogType(String desc) {
		this.desc = desc;
	}
	
}
