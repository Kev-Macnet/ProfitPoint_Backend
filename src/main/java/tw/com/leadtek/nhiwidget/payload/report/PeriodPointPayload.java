/**
 * Created on 2021/11/4.
 */
package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;

@ApiModel("費用業務-指定區間點數分佈")
public class PeriodPointPayload extends BaseResponse implements Serializable {

	private static final long serialVersionUID = 6506547774224019609L;

	@ApiModelProperty(value = "門急診+住院總件數", required = true)
	private Long quantityAll;

	@ApiModelProperty(value = "門急診總件數", required = true)
	private Long quantityOpAll;

	@ApiModelProperty(value = "門診總件數", required = true)
	private Long quantityOp;

	@ApiModelProperty(value = "急診總件數", required = true)
	private Long quantityEm;

	@ApiModelProperty(value = "住院總件數", required = true)
	private Long quantityIp;

	@ApiModelProperty(value = "原始總點數", required = true)
	private Long pointAll;

	@ApiModelProperty(value = "原始門急診點數", required = true)
	private Long pointOpAll;

	@ApiModelProperty(value = "原始門診點數", required = true)
	private Long pointOp;

	@ApiModelProperty(value = "原始急診點數", required = true)
	private Long pointEm;

	@ApiModelProperty(value = "原始住院點數", required = true)
	private Long pointIp;

	@ApiModelProperty(value = "累計申報總點數", required = true)
	private Long applPointAll;

	@ApiModelProperty(value = "累計申報門急診點數", required = true)
	private Long applPointOpAll;

	@ApiModelProperty(value = "累計申報門診點數", required = true)
	private Long applPointOp;

	@ApiModelProperty(value = "累計申報急診點數", required = true)
	private Long applPointEm;

	@ApiModelProperty(value = "累計申報住院點數", required = true)
	private Long applPointIp;

	@ApiModelProperty(value = "申請總點數", required = true)
	private Long applNoPartPointAll;

	@ApiModelProperty(value = "申請門急診點數", required = true)
	private Long applNoPartPointOpAll;

	@ApiModelProperty(value = "申請門診點數", required = true)
	private Long applNoPartPointOp;

	@ApiModelProperty(value = "申請急診點數", required = true)
	private Long applNoPartPointEm;

	@ApiModelProperty(value = "申請住院點數", required = true)
	private Long applNoPartPointIp;

	@ApiModelProperty(value = "全部部分負擔金額", required = true)
	private Long partPointAll;

	@ApiModelProperty(value = "門急診部分負擔金額", required = true)
	private Long partPointOpAll;

	@ApiModelProperty(value = "門診部分負擔金額", required = true)
	private Long partPointOp;

	@ApiModelProperty(value = "急診部分負擔金額", required = true)
	private Long partPointEm;

	@ApiModelProperty(value = "住院部分負擔金額", required = true)
	private Long partPointIp;

	@ApiModelProperty(value = "全部自費金額", required = true)
	private Long ownExpAll;

	@ApiModelProperty(value = "門急診自費金額", required = true)
	private Long ownExpOpAll;

	@ApiModelProperty(value = "門診自費金額", required = true)
	private Long ownExpOp;

	@ApiModelProperty(value = "急診自費金額", required = true)
	private Long ownExpEm;

	@ApiModelProperty(value = "住院自費金額", required = true)
	private Long ownExpIp;

	@ApiModelProperty(value = "全部不申報點數", required = true)
	private Long noApplAll;

	@ApiModelProperty(value = "門急診不申報點數", required = true)
	private Long noApplOpAll;

	@ApiModelProperty(value = "門診不申報點數", required = true)
	private Long noApplOp;

	@ApiModelProperty(value = "急診不申報點數", required = true)
	private Long noApplEm;

	@ApiModelProperty(value = "住院不申報點數", required = true)
	private Long noApplIp;

	@ApiModelProperty(value = "各科申報總點數", required = true)
	private PointQuantityList applByFuncType;

	@ApiModelProperty(value = "各科部分負擔總金額", required = true)
	private PointQuantityList partByFuncType;

	@ApiModelProperty(value = "各科自費總金額", required = true)
	private PointQuantityList ownExpByFuncType;

	@ApiModelProperty(value = "醫療費用比例與點數", required = true)
	private PointQuantityList payByOrderType;

	@ApiModelProperty(value = "醫療費用自費比例與金額", required = true)
	private PointQuantityList ownExpByOrderType;

	@ApiModelProperty(value = "醫療費用比例與點數列表", required = true)
	private List<Map<String, Object>> payByOrderTypeList;

	@ApiModelProperty(value = "醫療費用自費比例與金額列表", required = true)
	private List<Map<String, Object>> ownExpByOrderTypeList;

	@ApiModelProperty(value = "門診總件數(早)", required = true)
	private Long quantityOpM;

	@ApiModelProperty(value = "門診總件數(中)", required = true)
	private Long quantityOpN;

	@ApiModelProperty(value = "門診總件數(晚)", required = true)
	private Long quantityOpE;

	@ApiModelProperty(value = "原始門診點數(早)", required = true)
	private Long pointOpM;

	@ApiModelProperty(value = "原始門診點數(中)", required = true)
	private Long pointOpN;

	@ApiModelProperty(value = "原始門診點數(晚)", required = true)
	private Long pointOpE;

	@ApiModelProperty(value = "累計申報門診點數(早)", required = true)
	private Long applPointOpM;

	@ApiModelProperty(value = "累計申報門診點數(中)", required = true)
	private Long applPointOpN;

	@ApiModelProperty(value = "累計申報門診點數(晚)", required = true)
	private Long applPointOpE;

	@ApiModelProperty(value = "申請門診點數(早)", required = true)
	private Long applNoPartPointOpM;

	@ApiModelProperty(value = "申請門診點數(中)", required = true)
	private Long applNoPartPointOpN;

	@ApiModelProperty(value = "申請門診點數(晚)", required = true)
	private Long applNoPartPointOpE;

	@ApiModelProperty(value = "門診部分負擔金額(早)", required = true)
	private Long partPointOpM;

	@ApiModelProperty(value = "門診部分負擔金額(中)", required = true)
	private Long partPointOpN;

	@ApiModelProperty(value = "門診部分負擔金額(晚)", required = true)
	private Long partPointOpE;

	@ApiModelProperty(value = "門診自費金額(早)", required = true)
	private Long ownExpOpM;

	@ApiModelProperty(value = "門診自費金額(中)", required = true)
	private Long ownExpOpN;

	@ApiModelProperty(value = "門診自費金額(晚)", required = true)
	private Long ownExpOpE;

	@ApiModelProperty(value = "門診不申報點數(早)", required = true)
	private Long noApplOpM;

	@ApiModelProperty(value = "門診不申報點數(中)", required = true)
	private Long noApplOpN;

	@ApiModelProperty(value = "門診不申報點數(晚)", required = true)
	private Long noApplOpE;

	public PeriodPointPayload() {

	}

	public Long getQuantityAll() {
		return quantityAll;
	}

	public void setQuantityAll(Long quantityAll) {
		this.quantityAll = quantityAll;
	}

	public Long getQuantityOpAll() {
		return quantityOpAll;
	}

	public void setQuantityOpAll(Long quantityOpAll) {
		this.quantityOpAll = quantityOpAll;
	}

	public Long getQuantityOp() {
		return quantityOp;
	}

	public void setQuantityOp(Long quantityOp) {
		this.quantityOp = quantityOp;
	}

	public Long getQuantityEm() {
		return quantityEm;
	}

	public void setQuantityEm(Long quantityEm) {
		this.quantityEm = quantityEm;
	}

	public Long getQuantityIp() {
		return quantityIp;
	}

	public void setQuantityIp(Long quantityIp) {
		this.quantityIp = quantityIp;
	}

	public Long getPointAll() {
		return pointAll;
	}

	public void setPointAll(Long pointAll) {
		this.pointAll = pointAll;
	}

	public Long getPointOpAll() {
		return pointOpAll;
	}

	public void setPointOpAll(Long pointOpAll) {
		this.pointOpAll = pointOpAll;
	}

	public Long getPointOp() {
		return pointOp;
	}

	public void setPointOp(Long pointOp) {
		this.pointOp = pointOp;
	}

	public Long getPointEm() {
		return pointEm;
	}

	public void setPointEm(Long pointEm) {
		this.pointEm = pointEm;
	}

	public Long getPointIp() {
		return pointIp;
	}

	public void setPointIp(Long pointIp) {
		this.pointIp = pointIp;
	}

	public Long getApplPointAll() {
		return applPointAll;
	}

	public void setApplPointAll(Long applPointAll) {
		this.applPointAll = applPointAll;
	}

	public Long getApplPointOpAll() {
		return applPointOpAll;
	}

	public void setApplPointOpAll(Long applPointOpAll) {
		this.applPointOpAll = applPointOpAll;
	}

	public Long getApplPointOp() {
		return applPointOp;
	}

	public void setApplPointOp(Long applPointOp) {
		this.applPointOp = applPointOp;
	}

	public Long getApplPointEm() {
		return applPointEm;
	}

	public void setApplPointEm(Long applPointEm) {
		this.applPointEm = applPointEm;
	}

	public Long getApplPointIp() {
		return applPointIp;
	}

	public void setApplPointIp(Long applPointIp) {
		this.applPointIp = applPointIp;
	}

	public Long getPartPointAll() {
		return partPointAll;
	}

	public void setPartPointAll(Long partPointAll) {
		this.partPointAll = partPointAll;
	}

	public Long getPartPointOpAll() {
		return partPointOpAll;
	}

	public void setPartPointOpAll(Long partPointOpAll) {
		this.partPointOpAll = partPointOpAll;
	}

	public Long getPartPointOp() {
		return partPointOp;
	}

	public void setPartPointOp(Long partPointOp) {
		this.partPointOp = partPointOp;
	}

	public Long getPartPointEm() {
		return partPointEm;
	}

	public void setPartPointEm(Long partPointEm) {
		this.partPointEm = partPointEm;
	}

	public Long getPartPointIp() {
		return partPointIp;
	}

	public void setPartPointIp(Long partPointIp) {
		this.partPointIp = partPointIp;
	}

	public Long getOwnExpAll() {
		return ownExpAll;
	}

	public void setOwnExpAll(Long ownExpAll) {
		this.ownExpAll = ownExpAll;
	}

	public Long getOwnExpOpAll() {
		return ownExpOpAll;
	}

	public void setOwnExpOpAll(Long ownExpOpAll) {
		this.ownExpOpAll = ownExpOpAll;
	}

	public Long getOwnExpOp() {
		return ownExpOp;
	}

	public void setOwnExpOp(Long ownExpOp) {
		this.ownExpOp = ownExpOp;
	}

	public Long getOwnExpEm() {
		return ownExpEm;
	}

	public void setOwnExpEm(Long ownExpEm) {
		this.ownExpEm = ownExpEm;
	}

	public Long getOwnExpIp() {
		return ownExpIp;
	}

	public void setOwnExpIp(Long ownExpIp) {
		this.ownExpIp = ownExpIp;
	}

	public Long getNoApplAll() {
		return noApplAll;
	}

	public void setNoApplAll(Long noApplAll) {
		this.noApplAll = noApplAll;
	}

	public Long getNoApplOpAll() {
		return noApplOpAll;
	}

	public void setNoApplOpAll(Long noApplOpAll) {
		this.noApplOpAll = noApplOpAll;
	}

	public Long getNoApplOp() {
		return noApplOp;
	}

	public void setNoApplOp(Long noApplOp) {
		this.noApplOp = noApplOp;
	}

	public Long getNoApplEm() {
		return noApplEm;
	}

	public void setNoApplEm(Long noApplEm) {
		this.noApplEm = noApplEm;
	}

	public Long getNoApplIp() {
		return noApplIp;
	}

	public void setNoApplIp(Long noApplIp) {
		this.noApplIp = noApplIp;
	}

	public PointQuantityList getApplByFuncType() {
		return applByFuncType;
	}

	public void setApplByFuncType(PointQuantityList applByFuncType) {
		this.applByFuncType = applByFuncType;
	}

	public PointQuantityList getPartByFuncType() {
		return partByFuncType;
	}

	public void setPartByFuncType(PointQuantityList partByFuncType) {
		this.partByFuncType = partByFuncType;
	}

	public PointQuantityList getOwnExpByFuncType() {
		return ownExpByFuncType;
	}

	public void setOwnExpByFuncType(PointQuantityList ownExpByFuncType) {
		this.ownExpByFuncType = ownExpByFuncType;
	}

	public PointQuantityList getPayByOrderType() {
		return payByOrderType;
	}

	public void setPayByOrderType(PointQuantityList payByOrderType) {
		this.payByOrderType = payByOrderType;
	}

	public PointQuantityList getOwnExpByOrderType() {
		return ownExpByOrderType;
	}

	public void setOwnExpByOrderType(PointQuantityList ownExpByOrderType) {
		this.ownExpByOrderType = ownExpByOrderType;
	}

	public Long getApplNoPartPointAll() {
		return applNoPartPointAll;
	}

	public void setApplNoPartPointAll(Long applNoPartPointAll) {
		this.applNoPartPointAll = applNoPartPointAll;
	}

	public Long getApplNoPartPointOpAll() {
		return applNoPartPointOpAll;
	}

	public void setApplNoPartPointOpAll(Long applNoPartPointOpAll) {
		this.applNoPartPointOpAll = applNoPartPointOpAll;
	}

	public Long getApplNoPartPointOp() {
		return applNoPartPointOp;
	}

	public void setApplNoPartPointOp(Long applNoPartPointOp) {
		this.applNoPartPointOp = applNoPartPointOp;
	}

	public Long getApplNoPartPointEm() {
		return applNoPartPointEm;
	}

	public void setApplNoPartPointEm(Long applNoPartPointEm) {
		this.applNoPartPointEm = applNoPartPointEm;
	}

	public Long getApplNoPartPointIp() {
		return applNoPartPointIp;
	}

	public void setApplNoPartPointIp(Long applNoPartPointIp) {
		this.applNoPartPointIp = applNoPartPointIp;
	}

	public List<Map<String, Object>> getPayByOrderTypeList() {
		return payByOrderTypeList;
	}

	public void setPayByOrderTypeList(List<Map<String, Object>> payByOrderTypeList) {
		this.payByOrderTypeList = payByOrderTypeList;
	}

	public List<Map<String, Object>> getOwnExpByOrderTypeList() {
		return ownExpByOrderTypeList;
	}

	public void setOwnExpByOrderTypeList(List<Map<String, Object>> ownExpByOrderTypeList) {
		this.ownExpByOrderTypeList = ownExpByOrderTypeList;
	}

	public Long getQuantityOpM() {
		return quantityOpM;
	}

	public void setQuantityOpM(Long quantityOpM) {
		this.quantityOpM = quantityOpM;
	}

	public Long getQuantityOpN() {
		return quantityOpN;
	}

	public void setQuantityOpN(Long quantityOpN) {
		this.quantityOpN = quantityOpN;
	}

	public Long getQuantityOpE() {
		return quantityOpE;
	}

	public void setQuantityOpE(Long quantityOpE) {
		this.quantityOpE = quantityOpE;
	}

	public Long getPointOpM() {
		return pointOpM;
	}

	public void setPointOpM(Long pointOpM) {
		this.pointOpM = pointOpM;
	}

	public Long getPointOpN() {
		return pointOpN;
	}

	public void setPointOpN(Long pointOpN) {
		this.pointOpN = pointOpN;
	}

	public Long getPointOpE() {
		return pointOpE;
	}

	public void setPointOpE(Long pointOpE) {
		this.pointOpE = pointOpE;
	}

	public Long getApplPointOpM() {
		return applPointOpM;
	}

	public void setApplPointOpM(Long applPointOpM) {
		this.applPointOpM = applPointOpM;
	}

	public Long getApplPointOpN() {
		return applPointOpN;
	}

	public void setApplPointOpN(Long applPointOpN) {
		this.applPointOpN = applPointOpN;
	}

	public Long getApplPointOpE() {
		return applPointOpE;
	}

	public void setApplPointOpE(Long applPointOpE) {
		this.applPointOpE = applPointOpE;
	}

	public Long getApplNoPartPointOpM() {
		return applNoPartPointOpM;
	}

	public void setApplNoPartPointOpM(Long applNoPartPointOpM) {
		this.applNoPartPointOpM = applNoPartPointOpM;
	}

	public Long getApplNoPartPointOpN() {
		return applNoPartPointOpN;
	}

	public void setApplNoPartPointOpN(Long applNoPartPointOpN) {
		this.applNoPartPointOpN = applNoPartPointOpN;
	}

	public Long getApplNoPartPointOpE() {
		return applNoPartPointOpE;
	}

	public void setApplNoPartPointOpE(Long applNoPartPointOpE) {
		this.applNoPartPointOpE = applNoPartPointOpE;
	}

	public Long getPartPointOpM() {
		return partPointOpM;
	}

	public void setPartPointOpM(Long partPointOpM) {
		this.partPointOpM = partPointOpM;
	}

	public Long getPartPointOpN() {
		return partPointOpN;
	}

	public void setPartPointOpN(Long partPointOpN) {
		this.partPointOpN = partPointOpN;
	}

	public Long getPartPointOpE() {
		return partPointOpE;
	}

	public void setPartPointOpE(Long partPointOpE) {
		this.partPointOpE = partPointOpE;
	}

	public Long getOwnExpOpM() {
		return ownExpOpM;
	}

	public void setOwnExpOpM(Long ownExpOpM) {
		this.ownExpOpM = ownExpOpM;
	}

	public Long getOwnExpOpN() {
		return ownExpOpN;
	}

	public void setOwnExpOpN(Long ownExpOpN) {
		this.ownExpOpN = ownExpOpN;
	}

	public Long getOwnExpOpE() {
		return ownExpOpE;
	}

	public void setOwnExpOpE(Long ownExpOpE) {
		this.ownExpOpE = ownExpOpE;
	}

	public Long getNoApplOpM() {
		return noApplOpM;
	}

	public void setNoApplOpM(Long noApplOpM) {
		this.noApplOpM = noApplOpM;
	}

	public Long getNoApplOpN() {
		return noApplOpN;
	}

	public void setNoApplOpN(Long noApplOpN) {
		this.noApplOpN = noApplOpN;
	}

	public Long getNoApplOpE() {
		return noApplOpE;
	}

	public void setNoApplOpE(Long noApplOpE) {
		this.noApplOpE = noApplOpE;
	}

}
