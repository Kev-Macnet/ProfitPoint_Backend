package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;

@ApiModel("核刪資料返回")
public class DeductedPayloadResponse extends BaseResponse implements Serializable {

	private static final long serialVersionUID = -2715574499997878549L;
	
	@ApiModelProperty(value = "核刪返回資料", required = false)
	List<DeductedPayload> data;

	public List<DeductedPayload> getData() {
		return data;
	}

	public void setData(List<DeductedPayload> data) {
		this.data = data;
	}

	
	
	

}
