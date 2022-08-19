package tw.com.leadtek.nhiwidget.constant;

public enum LogType {
	
	FORGOT_PASSWORD("申請密碼清單"),

	MEDICAL_RECORD_STATUS_CHANGE("-1:疑問標示案件數, -2:比對警示待確認案件數, 2:優化完成案件數, 3:評估不調整案件數"),
	
	MEDICAL_RECORD_NOTIFYED("被通知次數記錄"),
	
	MEDICAL_RECORD_UNREAD("未讀取次數記錄"),
	
	IMPORT("資料匯入次數"),
	
	EXPORT("資料匯出次數"),
	
	ACTION_C("使用者操作紀錄(新增)"),
	
	ACTION_U("使用者操作紀錄(更新)"),
	
	ACTION_D("使用者操作紀錄(刪除)"),
	
	SIGNIN("登出入時間");
	
	private String desc;

	private LogType(String desc) {
		this.desc = desc;
	}
	
}
