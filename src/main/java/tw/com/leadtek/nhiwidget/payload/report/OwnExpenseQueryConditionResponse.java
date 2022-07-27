package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;

@ApiModel("自費項目清單資料返回")
public class OwnExpenseQueryConditionResponse extends BaseResponse implements Serializable{

	private static final long serialVersionUID = 2501694119701057733L;
	
	@ApiModelProperty(value = "自費項目清單資料返回", required = false)
	List<OwnExpenseQueryCondition> data;

	public List<OwnExpenseQueryCondition> getData() {
		return data;
	}

	public void setData(List<OwnExpenseQueryCondition> data) {
		this.data = data;
	}

}
