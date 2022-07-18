package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
@ApiModel("資料庫核刪資料返回")
public class DeductedNoteQueryConditionResponse extends BaseResponse implements Serializable {

	private static final long serialVersionUID = -3737369377543268476L;
	
	@ApiModelProperty(value = "核刪返回資料", required = false)
	List<DeductedNoteQueryCondition> data;

	public List<DeductedNoteQueryCondition> getData() {
		return data;
	}

	public void setData(List<DeductedNoteQueryCondition> data) {
		this.data = data;
	}

}
