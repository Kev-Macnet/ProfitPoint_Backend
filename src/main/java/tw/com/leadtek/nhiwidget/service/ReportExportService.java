package tw.com.leadtek.nhiwidget.service;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.io.Files;

import tw.com.leadtek.nhiwidget.model.rdb.POINT_MONTHLY;
import tw.com.leadtek.nhiwidget.payload.report.AchievementWeekly;
import tw.com.leadtek.nhiwidget.payload.report.NameValueList;
import tw.com.leadtek.nhiwidget.payload.report.NameValueList2;
import tw.com.leadtek.nhiwidget.payload.report.NameValueList3;
import tw.com.leadtek.nhiwidget.payload.report.PointMRPayload;
import tw.com.leadtek.nhiwidget.payload.report.VisitsVarietyPayload;
import tw.com.leadtek.tools.DateTool;

/**
 * 報表匯出專用service
 *
 */
@Service
public class ReportExportService {

	@Autowired
	private ReportService reportService;

	public final static String FILE_PATH = "download";

	/**
	 * 取得單月各科健保申報量與人次報表-匯出
	 * 
	 * @param year
	 * @param month
	 * @param response
	 * @throws IOException
	 */
	public void getMonthlyReportApplCountExport(int year, int month, HttpServletResponse response) throws IOException {
		String monthStr = "";
		if (month < 10) {
			monthStr = "0" + month;
		} else {
			monthStr = String.valueOf(month);
		}

		String inputDate = DateTool.convertToChineseYear(year + monthStr + "00");
		/// 轉成民國年月
		String endDate = inputDate.substring(0, inputDate.length() - 2);
		/// 呼叫上面api
		PointMRPayload pointData = reportService.getMonthlyReportApplCount(year, month);
		String[] tableHeaderNum = { "門急診/住院", "門急診", "門診(早)", "門診(中)", "門診(晚)", "急診", "住院", "出院" };
		String[] tableCellHeader = { "單月各科人次比\n門急診/住院(含手術)", "人次", "比例", "", "單月各科人次比\n門急診(含手術)", "人次", "比例", "" };

		POINT_MONTHLY model = pointData.getCurrent();
		String sheetName = "單月各科健保申報量與人次報表" + "_" + endDate;

		// 建立新工作簿 sheet1
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 新建工作表
		HSSFSheet sheet = workbook.createSheet("單月各科健保申報量與人次報表");
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		Font font = workbook.createFont();
		font.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
		// 建立行,行號作為引數傳遞給createRow()方法,第一行從0開始計算
		HSSFRow row = sheet.createRow(0);
		// 建立單元格,row已經確定了行號,列號作為引數傳遞給createCell(),第一列從0開始計算
		HSSFCell cell = row.createCell(0);
		// 設定單元格的值,即A1的值(第一行,第一列)
		cell.setCellValue("統計月份");

		HSSFRow row2 = sheet.createRow(2);
		for (int i = 0; i < tableHeaderNum.length; i++) {
			HSSFCell cell2 = row2.createCell(1 + i);
			cell2.setCellValue(tableHeaderNum[i]);
			cell2.setCellStyle(cellStyle);
		}

		HSSFRow row3 = sheet.createRow(3);
		HSSFCell cell3 = row3.createCell(0);
		cell3.setCellValue("申報總點數");
		for (int i = 0; i < 8; i++) {
			HSSFCell cell3_2 = row3.createCell(1 + i);
			cell3_2.setCellStyle(cellStyle);
			switch (i) {
			case 0:
				cell3_2.setCellValue(model.getTotalAll());
				break;
			case 1:
				cell3_2.setCellValue(model.getTotalOpAll());
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				break;
			case 5:
				cell3_2.setCellValue(model.getTotalEm());
				break;
			case 6:
				cell3_2.setCellValue(model.getTotalIp());
				break;
			case 7:
				cell3_2.setCellValue("-");
				break;
			default:
				break;

			}

		}

		HSSFRow row4 = sheet.createRow(4);
		HSSFCell cell4 = row4.createCell(0);
		cell4.setCellValue("總人次(含手術)");
		// todo
		for (int i = 0; i < 8; i++) {
			HSSFCell cell4_2 = row4.createCell(1 + i);
			cell4_2.setCellStyle(cellStyle);
			switch (i) {
			case 0:
				cell4_2.setCellValue(pointData.getPatient_total_count());
				break;
			case 1:
				cell4_2.setCellValue(pointData.getPatient_op_count());
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				break;
			case 5:
				cell4_2.setCellValue(model.getPatientEm());
				break;
			case 6:
				cell4_2.setCellValue(pointData.getPatient_ip_count());
				break;
			case 7:
				cell4_2.setCellValue(model.getPatientIp());
				break;
			default:
				break;

			}

		}

		HSSFRow row6 = sheet.createRow(6);
		HSSFCell cell6 = row6.createCell(0);
		cell6.setCellValue("單月各科人次比\n門急診/住院(含手術)");
		HSSFCellStyle cellStyle6 = workbook.createCellStyle();
		cellStyle6.setWrapText(true);
		cell6.setCellStyle(cellStyle6);
		row6.setHeightInPoints(2 * sheet.getDefaultRowHeightInPoints());
		for (int i = 0; i < pointData.getTotalPieCountData().size(); i++) {
			HSSFCell cell6_2 = row6.createCell(1 + i);
			Map<String, Object> map = pointData.getTotalPieCountData().get(i);
			cell6_2.setCellValue(map.get("DESC_CHI").toString());
			HSSFCellStyle cellStyle6_2 = workbook.createCellStyle();
			cellStyle6_2.setFont(font);
			cell6_2.setCellStyle(cellStyle6_2);
		}

		HSSFRow row7 = sheet.createRow(7);
		HSSFCell cell7 = row7.createCell(0);
		cell7.setCellValue("人次");
		for (int i = 0; i < pointData.getTotalPieCountData().size(); i++) {
			HSSFCell cell7_2 = row7.createCell(1 + i);
			Map<String, Object> map = pointData.getTotalPieCountData().get(i);
			cell7_2.setCellValue(Integer.valueOf(map.get("COUNT").toString()));
		}

		HSSFRow row8 = sheet.createRow(8);
		HSSFCell cell8 = row8.createCell(0);
		cell8.setCellValue("比例");
		for (int i = 0; i < pointData.getTotalPieCountData().size(); i++) {
			HSSFCell cell8_2 = row8.createCell(1 + i);
			Map<String, Object> map = pointData.getTotalPieCountData().get(i);
			float f = Float.valueOf(map.get("PERCENT").toString());
			String str = String.format("%.02f", f);
			cell8_2.setCellValue(str + "%");
		}

		HSSFRow row10 = sheet.createRow(10);
		HSSFCell cell10 = row10.createCell(0);
		cell10.setCellValue("單月各科人次比\n門急診(含手術)");
		HSSFCellStyle cellStyle10 = workbook.createCellStyle();
		cellStyle10.setWrapText(true);
		cell10.setCellStyle(cellStyle6);
		row10.setHeightInPoints(2 * sheet.getDefaultRowHeightInPoints());
		for (int i = 0; i < pointData.getOpPieCountData().size(); i++) {
			HSSFCell cell10_2 = row10.createCell(1 + i);
			Map<String, Object> map = pointData.getOpPieCountData().get(i);
			cell10_2.setCellValue(map.get("DESC_CHI").toString());
			HSSFCellStyle cellStyle10_2 = workbook.createCellStyle();
			cellStyle10_2.setFont(font);
			cell10_2.setCellStyle(cellStyle10_2);
		}

		HSSFRow row11 = sheet.createRow(11);
		HSSFCell cell11 = row11.createCell(0);
		cell11.setCellValue("人次");
		for (int i = 0; i < pointData.getOpPieCountData().size(); i++) {
			HSSFCell cell11_2 = row11.createCell(1 + i);
			Map<String, Object> map = pointData.getOpPieCountData().get(i);
			cell11_2.setCellValue(Integer.valueOf(map.get("COUNT").toString()));
		}
		HSSFRow row12 = sheet.createRow(12);
		HSSFCell cell12 = row12.createCell(0);
		cell12.setCellValue("比例");
		for (int i = 0; i < pointData.getOpPieCountData().size(); i++) {
			HSSFCell cell12_2 = row12.createCell(1 + i);
			Map<String, Object> map = pointData.getOpPieCountData().get(i);
			float f = Float.valueOf(map.get("PERCENT").toString());
			String str = String.format("%.02f", f);
			cell12_2.setCellValue(str + "%");
		}
		///
		HSSFRow row14 = sheet.createRow(14);
		HSSFCell cell14 = row14.createCell(0);
		cell14.setCellValue("單月各科人次比\n住院(含手術)");
		HSSFCellStyle cellStyle14 = workbook.createCellStyle();
		cellStyle14.setWrapText(true);
		cell14.setCellStyle(cellStyle14);
		row14.setHeightInPoints(2 * sheet.getDefaultRowHeightInPoints());
		for (int i = 0; i < pointData.getIpPieCountData().size(); i++) {
			HSSFCell cell14_2 = row14.createCell(1 + i);
			Map<String, Object> map = pointData.getIpPieCountData().get(i);
			cell14_2.setCellValue(map.get("DESC_CHI").toString());
			HSSFCellStyle cellStyle14_2 = workbook.createCellStyle();
			cellStyle14_2.setFont(font);
			cell14_2.setCellStyle(cellStyle14_2);
		}

		HSSFRow row15 = sheet.createRow(15);
		HSSFCell cell15 = row15.createCell(0);
		cell15.setCellValue("人次");
		for (int i = 0; i < pointData.getIpPieCountData().size(); i++) {
			HSSFCell cell15_2 = row15.createCell(1 + i);
			Map<String, Object> map = pointData.getIpPieCountData().get(i);
			cell15_2.setCellValue(Integer.valueOf(map.get("COUNT").toString()));
		}
		HSSFRow row16 = sheet.createRow(16);
		HSSFCell cell16 = row16.createCell(0);
		cell16.setCellValue("比例");
		for (int i = 0; i < pointData.getIpPieCountData().size(); i++) {
			HSSFCell cell16_2 = row16.createCell(1 + i);
			Map<String, Object> map = pointData.getIpPieCountData().get(i);
			float f = Float.valueOf(map.get("PERCENT").toString());
			String str = String.format("%.02f", f);
			cell16_2.setCellValue(str + "%");
		}
		///
		HSSFRow row18 = sheet.createRow(18);
		HSSFCell cell18 = row18.createCell(0);
		cell18.setCellValue("單月各科人次比\n出院(含手術)");
		HSSFCellStyle cellStyle18 = workbook.createCellStyle();
		cellStyle18.setWrapText(true);
		cell18.setCellStyle(cellStyle18);
		row18.setHeightInPoints(2 * sheet.getDefaultRowHeightInPoints());
		for (int i = 0; i < pointData.getIpPieOutCountData().size(); i++) {
			HSSFCell cell18_2 = row18.createCell(1 + i);
			Map<String, Object> map = pointData.getIpPieOutCountData().get(i);
			cell18_2.setCellValue(map.get("DESC_CHI").toString());
			HSSFCellStyle cellStyle18_2 = workbook.createCellStyle();
			cellStyle18_2.setFont(font);
			cell18_2.setCellStyle(cellStyle18_2);
		}

		HSSFRow row19 = sheet.createRow(19);
		HSSFCell cell19 = row19.createCell(0);
		cell19.setCellValue("人次");
		for (int i = 0; i < pointData.getIpPieOutCountData().size(); i++) {
			HSSFCell cell19_2 = row19.createCell(1 + i);
			Map<String, Object> map = pointData.getIpPieOutCountData().get(i);
			cell19_2.setCellValue(Integer.valueOf(map.get("COUNT").toString()));
		}
		HSSFRow row20 = sheet.createRow(20);
		HSSFCell cell20 = row20.createCell(0);
		cell20.setCellValue("比例");
		for (int i = 0; i < pointData.getIpPieOutCountData().size(); i++) {
			HSSFCell cell20_2 = row20.createCell(1 + i);
			Map<String, Object> map = pointData.getIpPieOutCountData().get(i);
			float f = Float.valueOf(map.get("PERCENT").toString());
			String str = String.format("%.02f", f);
			cell20_2.setCellValue(str + "%");
		}

		///
		HSSFRow row22 = sheet.createRow(22);
		HSSFCell cell22 = row22.createCell(0);
		cell22.setCellValue("單月各科申報點數比\n門急診/出院(含手術)");
		HSSFCellStyle cellStyle22 = workbook.createCellStyle();
		cellStyle22.setWrapText(true);
		cell22.setCellStyle(cellStyle22);
		row22.setHeightInPoints(2 * sheet.getDefaultRowHeightInPoints());
		for (int i = 0; i < pointData.getTotalPieDotData().size(); i++) {
			HSSFCell cell22_2 = row22.createCell(1 + i);
			Map<String, Object> map = pointData.getTotalPieDotData().get(i);
			cell22_2.setCellValue(map.get("DESC_CHI").toString());
			HSSFCellStyle cellStyle22_2 = workbook.createCellStyle();
			cellStyle22_2.setFont(font);
			cell22_2.setCellStyle(cellStyle22_2);
		}

		HSSFRow row23 = sheet.createRow(23);
		HSSFCell cell23 = row23.createCell(0);
		cell23.setCellValue("點數");
		for (int i = 0; i < pointData.getTotalPieDotData().size(); i++) {
			HSSFCell cell23_2 = row23.createCell(1 + i);
			Map<String, Object> map = pointData.getTotalPieDotData().get(i);
			cell23_2.setCellValue(Integer.valueOf(map.get("SUM").toString()));
		}
		HSSFRow row24 = sheet.createRow(24);
		HSSFCell cell24 = row24.createCell(0);
		cell24.setCellValue("比例");
		for (int i = 0; i < pointData.getTotalPieDotData().size(); i++) {
			HSSFCell cell24_2 = row24.createCell(1 + i);
			Map<String, Object> map = pointData.getTotalPieDotData().get(i);
			float f = Float.valueOf(map.get("PERCENT").toString());
			String str = String.format("%.02f", f);
			cell24_2.setCellValue(str + "%");
		}

		///
		HSSFRow row26 = sheet.createRow(26);
		HSSFCell cell26 = row26.createCell(0);
		cell26.setCellValue("單月各科申報點數比\n門急診(含手術)");
		HSSFCellStyle cellStyle26 = workbook.createCellStyle();
		cellStyle26.setWrapText(true);
		cell26.setCellStyle(cellStyle26);
		row26.setHeightInPoints(2 * sheet.getDefaultRowHeightInPoints());
		for (int i = 0; i < pointData.getOpPieDotData().size(); i++) {
			HSSFCell cell26_2 = row26.createCell(1 + i);
			Map<String, Object> map = pointData.getOpPieDotData().get(i);
			cell26_2.setCellValue(map.get("DESC_CHI").toString());
			HSSFCellStyle cellStyle26_2 = workbook.createCellStyle();
			cellStyle26_2.setFont(font);
			cell26_2.setCellStyle(cellStyle26_2);
		}

		HSSFRow row27 = sheet.createRow(27);
		HSSFCell cell27 = row27.createCell(0);
		cell27.setCellValue("點數");
		for (int i = 0; i < pointData.getOpPieDotData().size(); i++) {
			HSSFCell cell27_2 = row27.createCell(1 + i);
			Map<String, Object> map = pointData.getOpPieDotData().get(i);
			cell27_2.setCellValue(Integer.valueOf(map.get("SUM").toString()));
		}
		HSSFRow row28 = sheet.createRow(28);
		HSSFCell cell28 = row28.createCell(0);
		cell28.setCellValue("比例");
		for (int i = 0; i < pointData.getOpPieDotData().size(); i++) {
			HSSFCell cell28_2 = row28.createCell(1 + i);
			Map<String, Object> map = pointData.getOpPieDotData().get(i);
			float f = Float.valueOf(map.get("PERCENT").toString());
			String str = String.format("%.02f", f);
			cell28_2.setCellValue(str + "%");
		}

		///
		HSSFRow row30 = sheet.createRow(30);
		HSSFCell cell30 = row30.createCell(0);
		cell30.setCellValue("單月各科申報點數比\n住院(含手術)");
		HSSFCellStyle cellStyle30 = workbook.createCellStyle();
		cellStyle30.setWrapText(true);
		cell30.setCellStyle(cellStyle30);
		row30.setHeightInPoints(2 * sheet.getDefaultRowHeightInPoints());
		for (int i = 0; i < pointData.getIpPieDotData().size(); i++) {
			HSSFCell cell30_2 = row30.createCell(1 + i);
			Map<String, Object> map = pointData.getIpPieDotData().get(i);
			cell30_2.setCellValue(map.get("DESC_CHI").toString());
			HSSFCellStyle cellStyle30_2 = workbook.createCellStyle();
			cellStyle30_2.setFont(font);
			cell30_2.setCellStyle(cellStyle30_2);
		}

		HSSFRow row31 = sheet.createRow(31);
		HSSFCell cell31 = row31.createCell(0);
		cell31.setCellValue("點數");
		for (int i = 0; i < pointData.getIpPieDotData().size(); i++) {
			HSSFCell cell31_2 = row31.createCell(1 + i);
			Map<String, Object> map = pointData.getIpPieDotData().get(i);
			cell31_2.setCellValue(Integer.valueOf(map.get("SUM").toString()));
		}
		HSSFRow row32 = sheet.createRow(32);
		HSSFCell cell32 = row32.createCell(0);
		cell32.setCellValue("比例");
		for (int i = 0; i < pointData.getIpPieDotData().size(); i++) {
			HSSFCell cell32_2 = row32.createCell(1 + i);
			Map<String, Object> map = pointData.getIpPieDotData().get(i);
			float f = Float.valueOf(map.get("PERCENT").toString());
			String str = String.format("%.02f", f);
			cell32_2.setCellValue(str + "%");
		}
		String[] tableHeader = { "門急診/住院申報總點數趨勢圖", "", "", "門急診申報總點數趨勢圖", "", "", "住院申報總點數趨勢圖", "", "", "門急診人數趨勢圖", "",
				"住院人數趨勢圖", "", "出院人數趨勢圖", "" };
		String[] tableHeader2 = { "週數", "點數", "案件數", "週數", "點數", "案件數", "週數", "點數", "案件數", "週數", "人次", "週數", "人次", "週數",
				"人次" };
		VisitsVarietyPayload model2 = pointData.getVisitsVarietyPayload();
		List<String> functypes = pointData.getFuncTypes();
		for (String str : functypes) {
			if (str.equals("不分科")) {
				int cellIndex = 0;
				/// 第二頁籤 sheet2
				sheet = workbook.createSheet("申報點數趨勢圖(全院)");
				// 建立行,行號作為引數傳遞給createRow()方法,第一行從0開始計算
				row = sheet.createRow(0);
				// 建立單元格,row已經確定了行號,列號作為引數傳遞給createCell(),第一列從0開始計算
				cell = row.createCell(0);
				// 設定單元格的值,即A1的值(第一行,第一列)
				cell.setCellValue("全院");
				HSSFRow row1 = sheet.createRow(1);
				CellStyle style1 = workbook.createCellStyle();
				style1.setAlignment(HorizontalAlignment.CENTER);// 水平置中
				style1.setVerticalAlignment(VerticalAlignment.CENTER);

				for (int i = 0; i < tableHeader.length; i++) {
					HSSFCell cell1 = row1.createCell(i);

					switch (i) {
					case 0:
						cell1 = row1.createCell(i);
						cell1.setCellValue(tableHeader[i]);
						cell1.setCellStyle(style1);
						sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 2));
						break;
					case 3:
						cell1 = row1.createCell(i);
						cell1.setCellValue(tableHeader[i]);
						cell1.setCellStyle(style1);
						sheet.addMergedRegion(new CellRangeAddress(1, 1, 3, 5));
						break;
					case 6:
						cell1 = row1.createCell(i);
						cell1.setCellValue(tableHeader[i]);
						cell1.setCellStyle(style1);
						sheet.addMergedRegion(new CellRangeAddress(1, 1, 6, 8));
						break;
					case 9:
						cell1 = row1.createCell(i);
						cell1.setCellValue(tableHeader[i]);
						cell1.setCellStyle(style1);
						sheet.addMergedRegion(new CellRangeAddress(1, 1, 9, 10));
						break;
					case 11:
						cell1 = row1.createCell(i);
						cell1.setCellValue(tableHeader[i]);
						cell1.setCellStyle(style1);
						sheet.addMergedRegion(new CellRangeAddress(1, 1, 11, 12));
						break;
					case 13:
						cell1 = row1.createCell(i);
						cell1.setCellValue(tableHeader[i]);
						cell1.setCellStyle(style1);
						sheet.addMergedRegion(new CellRangeAddress(1, 1, 13, 14));
						break;
					default:
						break;
					}
				}
				row2 = sheet.createRow(2);
				for (int i = 0; i < tableHeader2.length; i++) {
					HSSFCell cell2 = row2.createCell(i);
					cell2.setCellValue(tableHeader2[i]);
					cell2.setCellStyle(style1);
				}
				cellIndex = 0;
				NameValueList3 nvlAll = model2.getAllMap3().get(str);
				NameValueList3 nvlOp = model2.getOpemMap3().get(str);
				NameValueList3 nvlip = model2.getIpMap3().get(str);
				NameValueList3 nvlLeave = model2.getLeaveMap3().get(str);
				HSSFRow rows = sheet.createRow(3);
				/// 不知為何，poi如果直向寫入會發生值消失問題，這邊用一般橫向資料增長
				for (int i = 0; i < nvlAll.getNames().size(); i++) {
					HSSFCell cells = rows.createCell(cellIndex + i);
					cells.setCellValue(nvlAll.getNames().get(i));
					cellIndex++;
					cells = rows.createCell(cellIndex + i);
					cells.setCellValue(nvlAll.getValues2().get(i));
					cellIndex++;
					cells = rows.createCell(cellIndex + i);
					cells.setCellValue(nvlAll.getValues().get(i));
					cellIndex++;
					cells = rows.createCell(cellIndex + i);
					cells.setCellValue(nvlOp.getNames().get(i));
					cellIndex++;
					cells = rows.createCell(cellIndex + i);
					cells.setCellValue(nvlOp.getValues2().get(i));
					cellIndex++;
					cells = rows.createCell(cellIndex + i);
					cells.setCellValue(nvlOp.getValues().get(i));
					cellIndex++;
					cells = rows.createCell(cellIndex + i);
					cells.setCellValue(nvlip.getNames().get(i));
					cellIndex++;
					cells = rows.createCell(cellIndex + i);
					cells.setCellValue(nvlip.getValues2().get(i));
					cellIndex++;
					cells = rows.createCell(cellIndex + i);
					cells.setCellValue(nvlip.getValues().get(i));
					cellIndex++;
					cells = rows.createCell(cellIndex + i);
					cells.setCellValue(nvlOp.getNames().get(i));
					cellIndex++;
					cells = rows.createCell(cellIndex + i);
					cells.setCellValue(nvlOp.getValues().get(i));
					cellIndex++;
					cells = rows.createCell(cellIndex + i);
					cells.setCellValue(nvlip.getNames().get(i));
					cellIndex++;
					cells = rows.createCell(cellIndex + i);
					cells.setCellValue(nvlip.getValues().get(i));
					cellIndex++;
					cells = rows.createCell(cellIndex + i);
					cells.setCellValue(nvlLeave.getNames().get(i));
					cellIndex++;
					cells = rows.createCell(cellIndex + i);
					cells.setCellValue(nvlLeave.getValues().get(i));
					rows = sheet.createRow(4 + i);
					cellIndex = 0;
					cellIndex--;
					if (i >= 1) {
						cellIndex -= i;
					}

				}

			}
		}
		try {
			for (String str : functypes) {
				if (!str.equals("不分科")) {
					/// 略過沒有資料的科別
					if (model2.getAllMap3().get(str) == null) {
						continue;
					}
					int cellIndex = 0;
					/// sheet3.....n
					sheet = workbook.createSheet("申報點數趨勢圖(" + str + ")");
					// 建立行,行號作為引數傳遞給createRow()方法,第一行從0開始計算
					row = sheet.createRow(0);
					// 建立單元格,row已經確定了行號,列號作為引數傳遞給createCell(),第一列從0開始計算
					cell = row.createCell(0);
					// 設定單元格的值,即A1的值(第一行,第一列)
					cell.setCellValue(str);
					HSSFRow row1 = sheet.createRow(1);
					CellStyle style1 = workbook.createCellStyle();
					style1.setAlignment(HorizontalAlignment.CENTER);// 水平置中
					style1.setVerticalAlignment(VerticalAlignment.CENTER);
					for (int i = 0; i < tableHeader.length; i++) {
						HSSFCell cell1 = row1.createCell(i);

						switch (i) {
						case 0:
							cell1 = row1.createCell(i);
							cell1.setCellValue(tableHeader[i]);
							cell1.setCellStyle(style1);
							sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 2));
							break;
						case 3:
							cell1 = row1.createCell(i);
							cell1.setCellValue(tableHeader[i]);
							cell1.setCellStyle(style1);
							sheet.addMergedRegion(new CellRangeAddress(1, 1, 3, 5));
							break;
						case 6:
							cell1 = row1.createCell(i);
							cell1.setCellValue(tableHeader[i]);
							cell1.setCellStyle(style1);
							sheet.addMergedRegion(new CellRangeAddress(1, 1, 6, 8));
							break;
						case 9:
							cell1 = row1.createCell(i);
							cell1.setCellValue(tableHeader[i]);
							cell1.setCellStyle(style1);
							sheet.addMergedRegion(new CellRangeAddress(1, 1, 9, 10));
							break;
						case 11:
							cell1 = row1.createCell(i);
							cell1.setCellValue(tableHeader[i]);
							cell1.setCellStyle(style1);
							sheet.addMergedRegion(new CellRangeAddress(1, 1, 11, 12));
							break;
						case 13:
							cell1 = row1.createCell(i);
							cell1.setCellValue(tableHeader[i]);
							cell1.setCellStyle(style1);
							sheet.addMergedRegion(new CellRangeAddress(1, 1, 13, 14));
							break;
						default:
							break;
						}
					}
					row2 = sheet.createRow(2);
					for (int i = 0; i < tableHeader2.length; i++) {
						HSSFCell cell2 = row2.createCell(i);
						cell2.setCellValue(tableHeader2[i]);
						cell2.setCellStyle(style1);
					}
					cellIndex = 0;
					NameValueList3 nvlAll = model2.getAllMap3().get(str);
					NameValueList3 nvlOp = model2.getOpemMap3().get(str);
					NameValueList3 nvlip = model2.getIpMap3().get(str);
					NameValueList3 nvlLeave = model2.getLeaveMap3().get(str);
					HSSFRow rows = sheet.createRow(3);
					/// 不知為何，poi如果直向寫入會發生值消失問題，這邊用一般橫向資料增長
					for (int i = 0; i < nvlAll.getNames().size(); i++) {
						HSSFCell cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlAll.getNames().get(i));
						cellIndex++;
						cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlAll.getValues2().get(i));
						cellIndex++;
						cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlAll.getValues().get(i));
						cellIndex++;
						cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlOp.getNames().get(i));
						cellIndex++;
						cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlOp.getValues2().get(i));
						cellIndex++;
						cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlOp.getValues().get(i));
						cellIndex++;
						cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlip.getNames().get(i));
						cellIndex++;
						cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlip.getValues2().get(i));
						cellIndex++;
						cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlip.getValues().get(i));
						cellIndex++;
						cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlOp.getNames().get(i));
						cellIndex++;
						cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlOp.getValues().get(i));
						cellIndex++;
						cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlip.getNames().get(i));
						cellIndex++;
						cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlip.getValues().get(i));
						cellIndex++;
						cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlLeave.getNames().get(i));
						cellIndex++;
						cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlLeave.getValues().get(i));
						rows = sheet.createRow(4 + i);
						cellIndex = 0;
						cellIndex--;
						if (i >= 1) {
							cellIndex -= i;
						}

					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String fileNameStr = "單月各科健保申報量與人次報表" + "_" + endDate;
		String fileName = URLEncoder.encode(fileNameStr, "UTF-8");
		String filepath = (System.getProperty("os.name").toLowerCase().startsWith("windows"))
				? FILE_PATH + "\\" + fileName
				: FILE_PATH + "/" + fileName;
		File file = new File(filepath);
		response.reset();
		response.setHeader("Content-Disposition",
				"attachment; filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + ".csv");
		response.setContentType("application/vnd.ms-excel;charset=utf8");

		workbook.write(response.getOutputStream());
		workbook.close();
		Files.copy(file, response.getOutputStream());

	}

	/**
	 * 取得健保點數月報表-匯出
	 * 
	 * @param year
	 * @param month
	 * @param response
	 * @throws IOException
	 */
	public void getMonthlyReportExport(int year, int month, HttpServletResponse response) throws IOException {

		String monthStr = "";
		if (month < 10) {
			monthStr = "0" + month;
		} else {
			monthStr = String.valueOf(month);
		}

		String inputDate = DateTool.convertToChineseYear(year + monthStr + "00");
		/// 轉成民國年月
		String endDate = inputDate.substring(0, inputDate.length() - 2);

		PointMRPayload pointData = reportService.getMonthlyReport(year, month);
		POINT_MONTHLY cModel = pointData.getCurrent();
		POINT_MONTHLY yModel = pointData.getLastY();
		POINT_MONTHLY mModel = pointData.getLastM();

		String dateStr = year + "/" + monthStr;

		String[] tableCellHeaders1 = { "門急診/住院", "門急診", "門診", "急診", "住院" };
		String[] tableRowHeaders1 = { "統計月份", "慢籤額度條件", "分配額度", "申請點數(含慢籤預留)", "部分負擔點數", "慢籤預留總額度", "剩餘額度", "", "",
				"累計部分負擔", "累計申請點數", "當月申報點數", "分配額度點數", "總額達成率" };

		// 建立新工作簿
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 新建工作表
		HSSFSheet sheet = workbook.createSheet("單月健保點數總表");
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellStyle.setBorderTop(BorderStyle.MEDIUM);
		cellStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellStyle.setBorderRight(BorderStyle.MEDIUM);

		HSSFCellStyle cellHeadStyle = workbook.createCellStyle();
		cellHeadStyle.setAlignment(HorizontalAlignment.CENTER);
		cellHeadStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellHeadStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellHeadStyle.setBorderTop(BorderStyle.MEDIUM);
		cellHeadStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellHeadStyle.setBorderRight(BorderStyle.MEDIUM);

		HSSFCellStyle cellHeadDataStyle = workbook.createCellStyle();
		cellHeadDataStyle.setAlignment(HorizontalAlignment.LEFT);
		cellHeadDataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellHeadDataStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellHeadDataStyle.setBorderTop(BorderStyle.MEDIUM);
		cellHeadDataStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellHeadDataStyle.setBorderRight(BorderStyle.MEDIUM);

		int cellIndex = 0;
		int rowIndex = 0;

		for (int y = 0; y < tableRowHeaders1.length; y++) {
			HSSFRow rows = sheet.createRow(y);
			HSSFCell cells = rows.createCell(cellIndex);
			cells.setCellValue(tableRowHeaders1[y]);
			cells.setCellStyle(cellStyle);

			cellIndex++;
			cells = rows.createCell(cellIndex);
			switch (y) {
			case 0:
				cells.setCellValue(dateStr);
				cells.setCellStyle(cellHeadDataStyle);
				break;
			case 1:
				cells.setCellValue("");
				cells.setCellStyle(cellHeadDataStyle);
				break;
			case 2:
				cells.setCellValue(cModel.getAssignedAll());
				cells.setCellStyle(cellHeadDataStyle);
				break;
			case 3:
				cells.setCellValue(cModel.getApplAll());
				cells.setCellStyle(cellHeadDataStyle);
				break;
			case 4:
				cells.setCellValue(cModel.getPartAll());
				cells.setCellStyle(cellHeadDataStyle);
				break;
			case 5:
				cells.setCellValue(cModel.getChronic());
				cells.setCellStyle(cellHeadDataStyle);
				break;
			case 6:
				cells.setCellValue(cModel.getRemaining());
				cells.setCellStyle(cellHeadDataStyle);
				break;

			case 8:
				for (int x = 0; x < tableCellHeaders1.length; x++) {
					cells = rows.createCell(cellIndex + x);
					cells.setCellValue(tableCellHeaders1[x]);
					cells.setCellStyle(cellHeadStyle);
				}
				break;
			case 9:
				for (int x = 0; x < tableCellHeaders1.length; x++) {
					cells = rows.createCell(cellIndex + x);
					switch (x) {
					case 0:
						cells.setCellValue(cModel.getPartAll());
						break;
					case 1:
						cells.setCellValue(cModel.getPartOpAll());
						break;
					case 2:
						cells.setCellValue(cModel.getPartOp());
						break;
					case 3:
						cells.setCellValue(cModel.getPartEm());
						break;
					case 4:
						cells.setCellValue(cModel.getPartIp());
						break;
					default:
						break;
					}
					cells.setCellStyle(cellHeadDataStyle);
				}
				break;
			case 10:
				for (int x = 0; x < tableCellHeaders1.length; x++) {
					cells = rows.createCell(cellIndex + x);
					switch (x) {
					case 0:
						cells.setCellValue(cModel.getApplAll());
						break;
					case 1:
						cells.setCellValue(cModel.getApplOpAll());
						break;
					case 2:
						cells.setCellValue(cModel.getApplOp());
						break;
					case 3:
						cells.setCellValue(cModel.getApplEm());
						break;
					case 4:
						cells.setCellValue(cModel.getApplIp());
						break;
					default:
						break;
					}
					cells.setCellStyle(cellHeadDataStyle);
				}
				break;
			case 11:
				for (int x = 0; x < tableCellHeaders1.length; x++) {
					cells = rows.createCell(cellIndex + x);
					switch (x) {
					case 0:
						cells.setCellValue(cModel.getTotalAll());
						break;
					case 1:
						cells.setCellValue(cModel.getTotalOpAll());
						break;
					case 2:
						cells.setCellValue(cModel.getTotalOp());
						break;
					case 3:
						cells.setCellValue(cModel.getTotalEm());
						break;
					case 4:
						cells.setCellValue(cModel.getTotalIp());
						break;
					default:
						break;
					}
					cells.setCellStyle(cellHeadDataStyle);
				}
				break;
			case 12:
				for (int x = 0; x < tableCellHeaders1.length; x++) {
					cells = rows.createCell(cellIndex + x);
					switch (x) {
					case 0:
						cells.setCellValue(cModel.getAssignedAll());
						break;
					case 1:
						cells.setCellValue(cModel.getAssignedOpAll());
						break;
					case 2:
						cells.setCellValue("-");
						break;
					case 3:
						cells.setCellValue("-");
						break;
					case 4:
						cells.setCellValue(cModel.getAssignedIp());
						break;
					default:
						break;
					}
					cells.setCellStyle(cellHeadDataStyle);
				}
				break;
			case 13:
				for (int x = 0; x < tableCellHeaders1.length; x++) {
					cells = rows.createCell(cellIndex + x);
					switch (x) {
					case 0:
						cells.setCellValue(String.valueOf(cModel.getRateAll()) + "%");
						break;
					case 1:
						cells.setCellValue(String.valueOf(cModel.getRateOpAll()) + "%");
						break;
					case 2:
						cells.setCellValue("-");
						break;
					case 3:
						cells.setCellValue("-");
						break;
					case 4:
						cells.setCellValue(String.valueOf(cModel.getRateIp()) + "%");
						break;
					default:
						break;
					}
					cells.setCellStyle(cellHeadDataStyle);
				}
				break;
			default:
				break;
			}

			cellIndex = 0;
		}
		/// 最後設定autosize
		for (int i = 0; i < tableRowHeaders1.length; i++) {
			sheet.autoSizeColumn(i);
			sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
		}
		cellIndex = 0;
		rowIndex = 0;
		String[] tableCellHeders2 = { "單月", "上月", "去年同期" };
		String[] tableRowHeders2 = { "門急診／住院", "申報點數", "分配點數", "上月差額", "去年同期差額", "門急診", "申報點數", "分配點數", "上月差額",
				"去年同期差額", "門診", "申報點數", "分配點數", "上月差額", "去年同期差額", "急診", "申報點數", "分配點數", "上月差額", "去年同期差額", "住院", "申報點數",
				"分配點數", "上月差額", "去年同期差額" };
		// 新建工作表 sheet2
		sheet = workbook.createSheet("門急診和住院");
		Font font = workbook.createFont();
//		font.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
		font.setBold(true);

		HSSFCellStyle cellStyle2 = workbook.createCellStyle();
		cellStyle2.setBorderBottom(BorderStyle.MEDIUM);
		cellStyle2.setBorderTop(BorderStyle.MEDIUM);
		cellStyle2.setBorderLeft(BorderStyle.MEDIUM);
		cellStyle2.setBorderRight(BorderStyle.MEDIUM);
		cellStyle2.setAlignment(HorizontalAlignment.LEFT);
		cellStyle2.setVerticalAlignment(VerticalAlignment.CENTER);

		HSSFCellStyle cellTitleStyle2 = workbook.createCellStyle();
		cellTitleStyle2.setBorderBottom(BorderStyle.MEDIUM);
		cellTitleStyle2.setBorderTop(BorderStyle.MEDIUM);
		cellTitleStyle2.setBorderLeft(BorderStyle.MEDIUM);
		cellTitleStyle2.setBorderRight(BorderStyle.MEDIUM);
		cellTitleStyle2.setAlignment(HorizontalAlignment.LEFT);
		cellTitleStyle2.setVerticalAlignment(VerticalAlignment.CENTER);
		cellTitleStyle2.setFont(font);

		for (int y = 0; y < tableRowHeders2.length; y++) {
			HSSFRow rows = sheet.createRow(y);
			HSSFCell cells = rows.createCell(cellIndex);
			cells.setCellValue(tableRowHeders2[y]);
			cells.setCellStyle(cellStyle);

			cellIndex++;
			cells = rows.createCell(cellIndex);
			switch (y) {
			case 0:
				for (int x = 0; x < tableCellHeders2.length; x++) {
					HSSFCell cellss = rows.createCell(cellIndex + x);
					cellss.setCellValue(tableCellHeders2[x]);
					cellss.setCellStyle(cellStyle2);
				}
				cells.setCellStyle(cellTitleStyle2);
				break;
			case 1:
				for (int x = 0; x < tableCellHeders2.length; x++) {
					HSSFCell cellss = rows.createCell(cellIndex + x);
					switch (x) {
					case 0:
						cellss.setCellValue(cModel.getTotalAll());
						break;
					case 1:
						cellss.setCellValue(mModel.getTotalAll());
						break;
					case 2:
						cellss.setCellValue(yModel.getTotalAll());
						break;
					default:
						break;
					}
					cellss.setCellStyle(cellStyle2);
				}
				break;
			case 2:
				for (int x = 0; x < tableCellHeders2.length; x++) {
					HSSFCell cellss = rows.createCell(cellIndex + x);
					switch (x) {
					case 0:
						cellss.setCellValue(cModel.getAssignedAll());
						break;
					case 1:
						cellss.setCellValue(mModel.getAssignedAll());
						break;
					case 2:
						cellss.setCellValue(yModel.getAssignedAll());
						break;
					default:
						break;
					}
					cellss.setCellStyle(cellStyle2);
				}
				break;
			case 3:
				for (int x = 0; x < tableCellHeders2.length; x++) {
					HSSFCell cellss = rows.createCell(cellIndex + x);
					switch (x) {
					case 0:
						cellss.setCellValue(pointData.getDiffAllLastM());
						break;
					case 1:
						cellss.setCellValue("-");
						break;
					case 2:
						cellss.setCellValue("-");
						break;
					default:
						break;
					}
					cellss.setCellStyle(cellStyle2);
				}
				break;
			case 4:
				for (int x = 0; x < tableCellHeders2.length; x++) {
					HSSFCell cellss = rows.createCell(cellIndex + x);
					switch (x) {
					case 0:
						cellss.setCellValue(pointData.getDiffAllLastY());
						break;
					case 1:
						cellss.setCellValue("-");
						break;
					case 2:
						cellss.setCellValue("-");
						break;
					default:
						break;
					}
					cellss.setCellStyle(cellStyle2);
				}
				break;
			case 5:
				for (int x = 0; x < tableCellHeders2.length; x++) {
					HSSFCell cellss = rows.createCell(cellIndex + x);
					cellss.setCellValue(tableCellHeders2[x]);
					cellss.setCellStyle(cellStyle2);
				}
				cells.setCellStyle(cellTitleStyle2);
				break;
			case 6:
				for (int x = 0; x < tableCellHeders2.length; x++) {
					HSSFCell cellss = rows.createCell(cellIndex + x);
					switch (x) {
					case 0:
						cellss.setCellValue(cModel.getTotalOpAll());
						break;
					case 1:
						cellss.setCellValue(mModel.getTotalOpAll());
						break;
					case 2:
						cellss.setCellValue(yModel.getTotalOpAll());
						break;
					default:
						break;
					}
					cellss.setCellStyle(cellStyle2);
				}
				break;
			case 7:
				for (int x = 0; x < tableCellHeders2.length; x++) {
					HSSFCell cellss = rows.createCell(cellIndex + x);
					switch (x) {
					case 0:
						cellss.setCellValue(cModel.getAssignedOpAll());
						break;
					case 1:
						cellss.setCellValue(mModel.getAssignedOpAll());
						break;
					case 2:
						cellss.setCellValue(yModel.getAssignedOpAll());
						break;
					default:
						break;
					}
					cellss.setCellStyle(cellStyle2);
				}
				break;
			case 8:
				for (int x = 0; x < tableCellHeders2.length; x++) {
					HSSFCell cellss = rows.createCell(cellIndex + x);
					switch (x) {
					case 0:
						cellss.setCellValue(pointData.getDiffOpAllLastM());
						break;
					case 1:
						cellss.setCellValue("-");
						break;
					case 2:
						cellss.setCellValue("-");
						break;
					default:
						break;
					}
					cellss.setCellStyle(cellStyle2);
				}
				break;
			case 9:
				for (int x = 0; x < tableCellHeders2.length; x++) {
					HSSFCell cellss = rows.createCell(cellIndex + x);
					switch (x) {
					case 0:
						cellss.setCellValue(pointData.getDiffOpAllLastY());
						break;
					case 1:
						cellss.setCellValue("-");
						break;
					case 2:
						cellss.setCellValue("-");
						break;
					default:
						break;
					}
					cellss.setCellStyle(cellStyle2);
				}
				break;
			case 10:
				for (int x = 0; x < tableCellHeders2.length; x++) {
					HSSFCell cellss = rows.createCell(cellIndex + x);
					cellss.setCellValue(tableCellHeders2[x]);
					cellss.setCellStyle(cellStyle2);
				}
				cells.setCellStyle(cellTitleStyle2);
				break;
			case 11:
				for (int x = 0; x < tableCellHeders2.length; x++) {
					HSSFCell cellss = rows.createCell(cellIndex + x);
					switch (x) {
					case 0:
						cellss.setCellValue(cModel.getTotalOp());
						break;
					case 1:
						cellss.setCellValue(mModel.getTotalOp());
						break;
					case 2:
						cellss.setCellValue(yModel.getTotalOp());
						break;
					default:
						break;
					}
					cellss.setCellStyle(cellStyle2);
				}
				break;
			case 12:
				for (int x = 0; x < tableCellHeders2.length; x++) {
					HSSFCell cellss = rows.createCell(cellIndex + x);
					switch (x) {
					case 0:
						cellss.setCellValue("-");
						break;
					case 1:
						cellss.setCellValue("-");
						break;
					case 2:
						cellss.setCellValue("-");
						break;
					default:
						break;
					}
					cellss.setCellStyle(cellStyle2);
				}
				break;
			case 13:
				for (int x = 0; x < tableCellHeders2.length; x++) {
					HSSFCell cellss = rows.createCell(cellIndex + x);
					switch (x) {
					case 0:
						cellss.setCellValue(pointData.getDiffOpLastM());
						break;
					case 1:
						cellss.setCellValue("-");
						break;
					case 2:
						cellss.setCellValue("-");
						break;
					default:
						break;
					}
					cellss.setCellStyle(cellStyle2);
				}
				break;
			case 14:
				for (int x = 0; x < tableCellHeders2.length; x++) {
					HSSFCell cellss = rows.createCell(cellIndex + x);
					switch (x) {
					case 0:
						cellss.setCellValue(pointData.getDiffOpLastY());
						break;
					case 1:
						cellss.setCellValue("-");
						break;
					case 2:
						cellss.setCellValue("-");
						break;
					default:
						break;
					}
					cellss.setCellStyle(cellStyle2);
				}
				break;
			case 15:
				for (int x = 0; x < tableCellHeders2.length; x++) {
					HSSFCell cellss = rows.createCell(cellIndex + x);
					cellss.setCellValue(tableCellHeders2[x]);
					cellss.setCellStyle(cellStyle2);
				}
				cells.setCellStyle(cellTitleStyle2);
				break;
			case 16:
				for (int x = 0; x < tableCellHeders2.length; x++) {
					HSSFCell cellss = rows.createCell(cellIndex + x);
					switch (x) {
					case 0:
						cellss.setCellValue(cModel.getTotalEm());
						break;
					case 1:
						cellss.setCellValue(mModel.getTotalEm());
						break;
					case 2:
						cellss.setCellValue(yModel.getTotalEm());
						break;
					default:
						break;
					}
					cellss.setCellStyle(cellStyle2);
				}
				break;
			case 17:
				for (int x = 0; x < tableCellHeders2.length; x++) {
					HSSFCell cellss = rows.createCell(cellIndex + x);
					switch (x) {
					case 0:
						cellss.setCellValue("-");
						break;
					case 1:
						cellss.setCellValue("-");
						break;
					case 2:
						cellss.setCellValue("-");
						break;
					default:
						break;
					}
					cellss.setCellStyle(cellStyle2);
				}
				break;
			case 18:
				for (int x = 0; x < tableCellHeders2.length; x++) {
					HSSFCell cellss = rows.createCell(cellIndex + x);
					switch (x) {
					case 0:
						cellss.setCellValue(pointData.getDiffEmLastM());
						break;
					case 1:
						cellss.setCellValue("-");
						break;
					case 2:
						cellss.setCellValue("-");
						break;
					default:
						break;
					}
					cellss.setCellStyle(cellStyle2);
				}
				break;
			case 19:
				for (int x = 0; x < tableCellHeders2.length; x++) {
					HSSFCell cellss = rows.createCell(cellIndex + x);
					switch (x) {
					case 0:
						cellss.setCellValue(pointData.getDiffEmLastY());
						break;
					case 1:
						cellss.setCellValue("-");
						break;
					case 2:
						cellss.setCellValue("-");
						break;
					default:
						break;
					}
					cellss.setCellStyle(cellStyle2);
				}
				break;
			case 20:
				for (int x = 0; x < tableCellHeders2.length; x++) {
					HSSFCell cellss = rows.createCell(cellIndex + x);
					cellss.setCellValue(tableCellHeders2[x]);
					cellss.setCellStyle(cellStyle2);
				}
				cells.setCellStyle(cellTitleStyle2);
				break;
			case 21:
				for (int x = 0; x < tableCellHeders2.length; x++) {
					HSSFCell cellss = rows.createCell(cellIndex + x);
					switch (x) {
					case 0:
						cellss.setCellValue(cModel.getTotalIp());
						break;
					case 1:
						cellss.setCellValue(mModel.getTotalIp());
						break;
					case 2:
						cellss.setCellValue(yModel.getTotalIp());
						break;
					default:
						break;
					}
					cellss.setCellStyle(cellStyle2);
				}
				break;
			case 22:
				for (int x = 0; x < tableCellHeders2.length; x++) {
					HSSFCell cellss = rows.createCell(cellIndex + x);
					switch (x) {
					case 0:
						cellss.setCellValue(cModel.getAssignedIp());
						break;
					case 1:
						cellss.setCellValue(mModel.getAssignedIp());
						break;
					case 2:
						cellss.setCellValue(yModel.getAssignedIp());
						break;
					default:
						break;
					}
					cellss.setCellStyle(cellStyle2);
				}
				break;
			case 23:
				for (int x = 0; x < tableCellHeders2.length; x++) {
					HSSFCell cellss = rows.createCell(cellIndex + x);
					switch (x) {
					case 0:
						cellss.setCellValue(pointData.getDiffIpLastM());
						break;
					case 1:
						cellss.setCellValue("-");
						break;
					case 2:
						cellss.setCellValue("-");
						break;
					default:
						break;
					}
					cellss.setCellStyle(cellStyle2);
				}
				break;
			case 24:
				for (int x = 0; x < tableCellHeders2.length; x++) {
					HSSFCell cellss = rows.createCell(cellIndex + x);
					switch (x) {
					case 0:
						cellss.setCellValue(pointData.getDiffIpLastY());
						break;
					case 1:
						cellss.setCellValue("-");
						break;
					case 2:
						cellss.setCellValue("-");
						break;
					default:
						break;
					}
					cellss.setCellStyle(cellStyle2);
				}
				break;
			default:
				break;
			}
			cellIndex = 0;
		}

		/// 最後設定autosize
		for (int i = 0; i < tableRowHeders2.length; i++) {
			sheet.autoSizeColumn(i);
			sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
		}

		/// sheet3
		String[] tableRowHeaders3 = { "", "單月", "上月差額", "去年同期差額" };
		String[] tableCellHeaders3 = { "門診", "急診", "出院" };
		cellIndex = 0;
		rowIndex = 0;
		// 新建工作表 sheet3
		sheet = workbook.createSheet("人次");

		HSSFCellStyle cellStyle3 = workbook.createCellStyle();
		cellStyle3.setBorderBottom(BorderStyle.MEDIUM);
		cellStyle3.setBorderTop(BorderStyle.MEDIUM);
		cellStyle3.setBorderLeft(BorderStyle.MEDIUM);
		cellStyle3.setBorderRight(BorderStyle.MEDIUM);
		cellStyle3.setAlignment(HorizontalAlignment.LEFT);
		cellStyle3.setVerticalAlignment(VerticalAlignment.CENTER);

		for (int y = 0; y < tableRowHeaders3.length; y++) {
			HSSFRow rows = sheet.createRow(y);
			HSSFCell cells = rows.createCell(cellIndex);
			cells.setCellValue(tableRowHeaders3[y]);
			cells.setCellStyle(cellStyle);

			cellIndex++;
			cells = rows.createCell(cellIndex);
			switch (y) {
			case 0:
				for (int x = 0; x < tableCellHeaders3.length; x++) {
					cells = rows.createCell(cellIndex + x);
					cells.setCellValue(tableCellHeaders3[x]);
					cells.setCellStyle(cellStyle3);
				}
				break;
			case 1:
				for (int x = 0; x < tableCellHeaders3.length; x++) {
					cells = rows.createCell(cellIndex + x);
					switch (x) {
					case 0:
						cells.setCellValue(cModel.getPatientOp());
						break;
					case 1:
						cells.setCellValue(cModel.getPatientIp());
						break;
					case 2:
						cells.setCellValue(cModel.getPatientEm());
						break;
					default:
						break;
					}
					cells.setCellStyle(cellStyle3);
				}
				break;
			case 2:
				for (int x = 0; x < tableCellHeaders3.length; x++) {
					cells = rows.createCell(cellIndex + x);
					switch (x) {
					case 0:
						cells.setCellValue(cModel.getPatientOp() - mModel.getPatientOp());
						break;
					case 1:
						cells.setCellValue(cModel.getPatientEm() - mModel.getPatientEm());
						break;
					case 2:
						cells.setCellValue(cModel.getPatientIp() - mModel.getPatientIp());
						break;
					default:
						break;
					}
					cells.setCellStyle(cellStyle3);
				}
				break;
			case 3:
				for (int x = 0; x < tableCellHeaders3.length; x++) {
					cells = rows.createCell(cellIndex + x);
					switch (x) {
					case 0:
						cells.setCellValue(cModel.getPatientOp() - yModel.getPatientOp());
						break;
					case 1:
						cells.setCellValue(cModel.getPatientEm() - yModel.getPatientEm());
						break;
					case 2:
						cells.setCellValue(cModel.getPatientIp() - yModel.getPatientIp());
						break;
					default:
						break;
					}
					cells.setCellStyle(cellStyle3);
				}
				break;
			default:
				break;
			}
			cellIndex = 0;
		}

		/// 最後設定autosize
		for (int i = 0; i < tableRowHeaders3.length; i++) {
			sheet.autoSizeColumn(i);
			sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
		}

		String fileNameStr = "單月健保點數總表" + "_" + endDate;
		String fileName = URLEncoder.encode(fileNameStr, "UTF-8");
		String filepath = (System.getProperty("os.name").toLowerCase().startsWith("windows"))
				? FILE_PATH + "\\" + fileName
				: FILE_PATH + "/" + fileName;
		File file = new File(filepath);
		response.reset();
		response.setHeader("Content-Disposition",
				"attachment; filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + ".csv");
		response.setContentType("application/vnd.ms-excel;charset=utf8");

		workbook.write(response.getOutputStream());
		workbook.close();

	}

	public void getAchievementRateExport(String year, String week, HttpServletResponse response) throws IOException {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, Integer.parseInt(year));
		cal.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(week));
		// 指定週的最後一天
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

		String dateStr = year + week + "w";

		AchievementWeekly awData = reportService.getAchievementWeekly(cal);

		// 建立新工作簿
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 新建工作表
		HSSFSheet sheet = workbook.createSheet("基本數值");
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.LEFT);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellStyle.setBorderTop(BorderStyle.MEDIUM);
		cellStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellStyle.setBorderRight(BorderStyle.MEDIUM);

		HSSFCellStyle cellTitleStyle = workbook.createCellStyle();
		cellTitleStyle.setAlignment(HorizontalAlignment.CENTER);
		cellTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellTitleStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellTitleStyle.setBorderTop(BorderStyle.MEDIUM);
		cellTitleStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellTitleStyle.setBorderRight(BorderStyle.MEDIUM);

		int cellIndex = 0;
		int rowIndex = 0;

		String[] tableRowHeaders = { "統計月份", "慢籤額度條件", "本月申報點數", "本月分配額度", "總額達成率" };

		for (int y = 0; y < tableRowHeaders.length; y++) {
			HSSFRow rows = sheet.createRow(y);
			HSSFCell cells = rows.createCell(cellIndex);
			cells.setCellValue(tableRowHeaders[y]);
			cells.setCellStyle(cellStyle);

			cellIndex++;
			HSSFCell cellss = rows.createCell(cellIndex);
			switch (y) {
			case 0:
				cellss = rows.createCell(cellIndex);
				cellss.setCellValue(dateStr);
				cellss.setCellStyle(cellStyle);
				break;
			case 1:
				cellss = rows.createCell(cellIndex);
				cellss.setCellValue("");
				cellss.setCellStyle(cellStyle);
				break;
			case 2:
				cellss = rows.createCell(cellIndex);
				cellss.setCellValue(awData.getMonthTotal());
				cellss.setCellStyle(cellStyle);
				break;
			case 3:
				cellss = rows.createCell(cellIndex);
				cellss.setCellValue(awData.getMonthAssigned());
				cellss.setCellStyle(cellStyle);
				break;
			case 4:
				cellss = rows.createCell(cellIndex);
				cellss.setCellValue(awData.getAchievementRate());
				cellss.setCellStyle(cellStyle);
				break;
			default:
				break;
			}
			cellIndex = 0;
		}

		/// 最後設定autosize
		for (int i = 0; i < tableRowHeaders.length; i++) {
			sheet.autoSizeColumn(i);
			sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
		}
		/// sheet2
		String[] sheetHeaders = { "門急診住院總額總點數趨勢圖", "門急診總額總點數趨勢圖", "住院總額總點數趨勢圖", "門診急診總額總點數" };
		String[] tableCellHeaders = { "週數", "總額點數", "週數", "分配點數", "實際申報點數", "達成率" };
		String[] tableCellHeaders5_1 = { "門診申報點數趨勢圖", "", "急診申報點數趨勢圖", "" };
		String[] tableCellHeaders5_2 = { "週數", "總額點數", "週數", "總額點數" };

		NameValueList nvlAll = null;
		NameValueList nvlAssingAll = null;
		NameValueList2 nvlActualAll = null;
		/// 重點： nvlAssingAll物件長度與nvlActualAll相同

		cellIndex = 0;
		rowIndex = 1;
		boolean isContinue = false;
		try {
			for (String sheetName : sheetHeaders) {
				switch (sheetName) {
				case "門急診住院總額總點數趨勢圖":
					nvlAll = awData.getAll();
					nvlAssingAll = awData.getAssignedAll();
					nvlActualAll = awData.getActualAll();
					break;
				case "門急診總額總點數趨勢圖":
					nvlAll = awData.getOpAll();
					nvlAssingAll = awData.getAssignedOpAll();
					nvlActualAll = awData.getActualOpAll();
					break;
				case "住院總額總點數趨勢圖":
					nvlAll = awData.getIp();
					nvlAssingAll = awData.getAssignedIp();
					nvlActualAll = awData.getActualIp();
					break;
				case "門診急診總額總點數":
					nvlAll = awData.getOp();
					nvlAssingAll = awData.getEm();
					break;
				default:
					break;
				}
				if (!sheetHeaders[3].equals(sheetName)) {
					sheet = workbook.createSheet(sheetName);
					HSSFRow rows = sheet.createRow(0);
					HSSFCell cells = rows.createCell(cellIndex);
					for (int i = 0; i <= 5; i++) {
						cells = rows.createCell(i);
						cells.setCellStyle(cellTitleStyle);
						if (i == 0) {

							cells.setCellValue(sheetName);
						}
					}

					sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

					/// headers
					HSSFRow rowss = sheet.createRow(1);
					for (int x = 0; x < tableCellHeaders.length; x++) {
						HSSFCell cellss = rowss.createCell(x + cellIndex);
						cellss.setCellValue(tableCellHeaders[x]);
						cellss.setCellStyle(cellStyle);
					}
					cellIndex = 0;
					rowIndex = 2;
					rowss = sheet.createRow(rowIndex);
					/// values
					for (int v1 = 0; v1 < nvlAll.getNames().size(); v1++) {
						String v1Name = nvlAll.getNames().get(v1);
						Long v1Val = nvlAll.getValues().get(v1);

						HSSFCell cellss = rowss.createCell(cellIndex);
						cellss.setCellValue(v1Name);
						cellss.setCellStyle(cellStyle);
						cellIndex++;
						cellss = rowss.createCell(cellIndex);
						cellss.setCellValue(v1Val);
						cellss.setCellStyle(cellStyle);
						cellIndex = 2;
						for (int v2 = 0; v2 < nvlAssingAll.getNames().size(); v2++) {
							String v2Name = nvlAssingAll.getNames().get(v2);
							Long v2Val = nvlAssingAll.getValues().get(v2);
							if (v1Name.equals(v2Name)) {
								cellss = rowss.createCell(cellIndex);
								cellss.setCellValue(v2Name);
								cellss.setCellStyle(cellStyle);
								cellIndex++;
								cellss = rowss.createCell(cellIndex);
								cellss.setCellValue(v2Val);
								cellss.setCellStyle(cellStyle);
								cellIndex++;
								cellss = rowss.createCell(cellIndex);
								Long v3Val = nvlActualAll.getValues().get(v2);
								Long v3Val2 = nvlActualAll.getValues2().get(v2);
								cellss.setCellValue(v3Val);
								cellss.setCellStyle(cellStyle);
								cellIndex++;
								cellss = rowss.createCell(cellIndex);
								cellss.setCellValue(String.valueOf(v3Val2) + "%");
								cellss.setCellStyle(cellStyle);

							} else {
								if (!isContinue) {
									cellss = rowss.createCell(cellIndex);
									cellss.setCellValue("");
									cellss.setCellStyle(cellStyle);
									cellIndex++;
									cellss = rowss.createCell(cellIndex);
									cellss.setCellValue("");
									cellss.setCellStyle(cellStyle);
									cellIndex++;
									cellss = rowss.createCell(cellIndex);
									cellss.setCellValue("");
									cellss.setCellStyle(cellStyle);
									cellIndex++;
									cellss = rowss.createCell(cellIndex);
									cellss.setCellValue("");
									cellss.setCellStyle(cellStyle);
									isContinue = true;
								} else {
									continue;
								}

								isContinue = true;
							}
							cellss.setCellStyle(cellStyle);
							cellIndex = 2;
						}
						isContinue = false;
						rowss = sheet.createRow(rowIndex + v1);
						cellIndex = 0;

					}
					cellIndex = 0;
					rowIndex = 0;
				} else {
					sheet = workbook.createSheet(sheetName);
					HSSFRow rows = sheet.createRow(0);
					HSSFCell cells = rows.createCell(cellIndex);
					for (int i = 0; i < tableCellHeaders5_1.length; i++) {
						cells = rows.createCell(i);
						cells.setCellStyle(cellTitleStyle);
						if (i == 0) {
							cells.setCellValue(tableCellHeaders5_1[i]);
						} else if (i == 2) {
							cells.setCellValue(tableCellHeaders5_1[i]);
						}
					}

					sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
					sheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 3));

					HSSFRow rowss = sheet.createRow(1);
					for (int x = 0; x < tableCellHeaders5_2.length; x++) {
						HSSFCell cellss = rowss.createCell(x + cellIndex);
						cellss.setCellValue(tableCellHeaders5_2[x]);
						cellss.setCellStyle(cellStyle);
					}
					rowss = sheet.createRow(2);
					rowIndex = 3;
					for (int v = 0; v < nvlAll.getNames().size(); v++) {
						String v1Name = nvlAll.getNames().get(v);
						Long v1Val = nvlAll.getValues().get(v);
						String v2Name = nvlAssingAll.getNames().get(v);
						Long v2Val = nvlAssingAll.getValues().get(v);

						HSSFCell cellss = rowss.createCell(cellIndex);
						cellss.setCellValue(v1Name);
						cellss.setCellStyle(cellStyle);
						cellIndex++;
						cellss = rowss.createCell(cellIndex);
						cellss.setCellValue(v1Val);
						cellss.setCellStyle(cellStyle);
						cellIndex++;
						cellss = rowss.createCell(cellIndex);
						cellss.setCellValue(v2Name);
						cellss.setCellStyle(cellStyle);
						cellIndex++;
						cellss = rowss.createCell(cellIndex);
						cellss.setCellValue(v2Val);
						cellss.setCellStyle(cellStyle);
						rowss = sheet.createRow(rowIndex + v);
						cellIndex = 0;
					}
				}
				/// 最後設定autosize
				for (int i = 0; i < tableCellHeaders.length; i++) {
					sheet.autoSizeColumn(i);
					sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String fileNameStr = "健保申報總額達成趨勢圖" + "_" + dateStr;
		String fileName = URLEncoder.encode(fileNameStr, "UTF-8");
		String filepath = (System.getProperty("os.name").toLowerCase().startsWith("windows"))
				? FILE_PATH + "\\" + fileName
				: FILE_PATH + "/" + fileName;
		File file = new File(filepath);
		response.reset();
		response.setHeader("Content-Disposition",
				"attachment; filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + ".csv");
		response.setContentType("application/vnd.ms-excel;charset=utf8");

		workbook.write(response.getOutputStream());
		workbook.close();

	}
}
