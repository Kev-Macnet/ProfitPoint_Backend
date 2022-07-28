package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;

@ApiModel("DRG案件數分佈佔率與定額、實際點數返回資料")
public class DrgQueryCoditionResponse extends BaseResponse implements Serializable{

	private static final long serialVersionUID = 4218027709192255755L;

	
	@ApiModelProperty(value = "DRG案件數分佈佔率與定額、實際點數返回資料", required = false)
	List<DrgQueryCondition> data;

	public List<DrgQueryCondition> getData() {
		return data;
	}

	public void setData(List<DrgQueryCondition> data) {
		this.data = data;
	}
}
