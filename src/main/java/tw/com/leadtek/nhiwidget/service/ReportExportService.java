package tw.com.leadtek.nhiwidget.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

import tw.com.leadtek.nhiwidget.dao.CODE_TABLEDao;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;
import tw.com.leadtek.nhiwidget.model.rdb.POINT_MONTHLY;
import tw.com.leadtek.nhiwidget.payload.report.AchievementQuarter;
import tw.com.leadtek.nhiwidget.payload.report.AchievementWeekly;
import tw.com.leadtek.nhiwidget.payload.report.DRGMonthlyPayload;
import tw.com.leadtek.nhiwidget.payload.report.DRGMonthlySectionPayload;
import tw.com.leadtek.nhiwidget.payload.report.NameCodePoint;
import tw.com.leadtek.nhiwidget.payload.report.NameCodePointQuantity;
import tw.com.leadtek.nhiwidget.payload.report.NameValueList;
import tw.com.leadtek.nhiwidget.payload.report.NameValueList2;
import tw.com.leadtek.nhiwidget.payload.report.NameValueList3;
import tw.com.leadtek.nhiwidget.payload.report.PointMRPayload;
import tw.com.leadtek.nhiwidget.payload.report.PointPeriod;
import tw.com.leadtek.nhiwidget.payload.report.QuarterData;
import tw.com.leadtek.nhiwidget.payload.report.VisitsPeriod;
import tw.com.leadtek.nhiwidget.payload.report.VisitsPeriodDetail;
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
	@Autowired
	private CODE_TABLEDao codeTableDao;

	public final static String FILE_PATH = "download";

	
	public void addRowCell(HSSFRow row,int num,String value,HSSFCellStyle cellStyle) {
			// 建立單元格,row已經確定了行號,列號作為引數傳遞給createCell(),第一列從0開始計算
			HSSFCell cell = row.createCell(num);
			// 設定單元格的值,即A1的值(第一行,第一列)
			cell.setCellValue(value);
		  if(cellStyle!=null) {
			  cell.setCellStyle(cellStyle);
		  }
	  }
	
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
		
		cell = row.createCell(1);
		cell.setCellValue(year + "/" + monthStr);

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

		/// 最後設定autosize
		for (int i = 0; i < pointData.getTotalPieDotData().size(); i++) {
			sheet.autoSizeColumn(i);
			sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
		}
		/// 新建工作表
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
				///auto size
				for (int i = 0; i < tableHeader.length; i++) {
					sheet.autoSizeColumn(i);
					sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
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
					///auto size
					for (int i = 0; i < tableHeader.length; i++) {
						sheet.autoSizeColumn(i);
						sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
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
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "*");
		response.setHeader("Content-Disposition",
				"attachment; filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + ".csv");
		response.setContentType("application/octet-stream;charset=utf8");
//		response.setContentType("application/vnd.ms-excel;charset=utf8");

		/// 最後由outputstream輸出
		OutputStream out = response.getOutputStream();

		workbook.write(out);
		out.flush();
		out.close();
		workbook.close();

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
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "*");
		response.setHeader("Content-Disposition",
				"attachment; filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + ".csv");
		response.setContentType("application/octet-stream;charset=utf8");
//		response.setContentType("application/vnd.ms-excel;charset=utf8");

		/// 最後由outputstream輸出
		OutputStream out = response.getOutputStream();

		workbook.write(out);
		out.flush();
		out.close();
		workbook.close();

	}

	/**
	 * 取得健保總額累積達成率-匯出
	 * 
	 * @param year
	 * @param week
	 * @param response
	 * @throws IOException
	 */
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
					/// 不是 門診急診總額總點數走這
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
					/// 門診急診總額總點數走這
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
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "*");
		response.setHeader("Content-Disposition",
				"attachment; filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + ".csv");
		response.setContentType("application/octet-stream;charset=utf8");
//		response.setContentType("application/vnd.ms-excel;charset=utf8");

		/// 最後由outputstream輸出
		OutputStream out = response.getOutputStream();

		workbook.write(out);
		out.flush();
		out.close();
		workbook.close();

	}

	/**
	 * 取得健保總額累積達成率-匯出
	 * 
	 * @param year
	 * @param quarter
	 * @param response
	 * @throws IOException
	 */
	public void getAchievementQuarterExport(String year, String quarter, HttpServletResponse response)
			throws IOException {

		AchievementQuarter aqData = reportService.getAchievementQuarter(year, quarter);
		List<QuarterData> qdAllList = aqData.getAll();
		List<QuarterData> qdOpList = aqData.getOp();
		List<QuarterData> qdIpList = aqData.getIp();

		// 建立新工作簿
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 樣式
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.LEFT);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellStyle.setBorderTop(BorderStyle.MEDIUM);
		cellStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellStyle.setBorderRight(BorderStyle.MEDIUM);

		HSSFCellStyle cellTitleStyle = workbook.createCellStyle();
		cellTitleStyle.setAlignment(HorizontalAlignment.LEFT);
		cellTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellTitleStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellTitleStyle.setBorderTop(BorderStyle.MEDIUM);
		cellTitleStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellTitleStyle.setBorderRight(BorderStyle.MEDIUM);
		Font font = workbook.createFont();
		font.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
		cellTitleStyle.setFont(font);

		int cellIndex = 0;
		int rowIndex = 0;

		/// 如過沒資料就會出空的
		if (qdAllList.size() != 0) {

			if (qdAllList.size() < 2) {
				/// 單季度資料匯出
				String[] tableCellHeaders = { "申報與分配點數與達成率", "病歷總點數(含自費)", "申報總點數", "分配額度總點數", "超額總點數", "總額達成率" };
				String[] tableRowHeaders = { "門急診/住院", "門急診", "住院" };
				String[] tableCellHeaders2 = { "", "分配點數", "申報總額點數" };
				HSSFSheet sheet = workbook.createSheet("單季度");
				/// 欄位A1
				HSSFRow row = sheet.createRow(0);
				HSSFCell cell = row.createCell(0);
				cell.setCellValue("申報季度");
				cell.setCellStyle(cellStyle);

				/// 欄位B1
				cell = row.createCell(1);
				cell.setCellValue(qdAllList.get(0).getName());
				cell.setCellStyle(cellStyle);

				/// 欄位A3
				row = sheet.createRow(2);
				for (int x = 0; x < tableCellHeaders.length; x++) {
					cell = row.createCell(x);
					cell.setCellValue(tableCellHeaders[x]);
					cell.setCellStyle(cellStyle);

				}
				/// 欄位A4
				row = sheet.createRow(3);
				rowIndex = 4;
				for (int y = 0; y < tableRowHeaders.length; y++) {
					cell = row.createCell(cellIndex);
					cell.setCellValue(tableRowHeaders[y]);
					cell.setCellStyle(cellStyle);
					cellIndex = 1;
					HSSFCell cells = row.createCell(cellIndex);
					switch (y) {
					case 0:
						for (int x = 0; x < tableCellHeaders.length - 1; x++) {
							cells = row.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cells.setCellValue(qdAllList.get(0).getOriginal());
								break;
							case 1:
								cells.setCellValue(qdAllList.get(0).getActual());
								break;
							case 2:
								cells.setCellValue(qdAllList.get(0).getAssigned());
								break;
							case 3:
								cells.setCellValue(qdAllList.get(0).getOver());
								break;
							case 4:
								cells.setCellValue(qdAllList.get(0).getPercent() + "%");
								break;
							default:
								break;
							}
							cells.setCellStyle(cellStyle);
						}
						break;
					case 1:
						for (int x = 0; x < tableCellHeaders.length - 1; x++) {
							cells = row.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cells.setCellValue(qdOpList.get(0).getOriginal());
								break;
							case 1:
								cells.setCellValue(qdOpList.get(0).getActual());
								break;
							case 2:
								cells.setCellValue(qdOpList.get(0).getAssigned());
								break;
							case 3:
								cells.setCellValue(qdOpList.get(0).getOver());
								break;
							case 4:
								cells.setCellValue(qdOpList.get(0).getPercent() + "%");
								break;
							default:
								break;
							}
							cells.setCellStyle(cellStyle);
						}
						break;
					case 2:
						for (int x = 0; x < tableCellHeaders.length - 1; x++) {
							cells = row.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cells.setCellValue(qdIpList.get(0).getOriginal());
								break;
							case 1:
								cells.setCellValue(qdIpList.get(0).getActual());
								break;
							case 2:
								cells.setCellValue(qdIpList.get(0).getAssigned());
								break;
							case 3:
								cells.setCellValue(qdIpList.get(0).getOver());
								break;
							case 4:
								cells.setCellValue(qdIpList.get(0).getPercent() + "%");
								break;
							default:
								break;
							}
							cells.setCellStyle(cellStyle);
						}
						break;
					default:
						break;

					}
					row = sheet.createRow(rowIndex + y);
					cellIndex = 0;
				}
				/// 欄位A8
				cellIndex = 0;
				row = sheet.createRow(7);
				for (int x = 0; x < tableCellHeaders2.length; x++) {
					cell = row.createCell(x);
					cell.setCellValue(tableCellHeaders2[x]);
					cell.setCellStyle(cellStyle);
				}
				/// 欄位A9
				row = sheet.createRow(8);
				rowIndex = 9;
				for (int y = 0; y < tableRowHeaders.length; y++) {
					cell = row.createCell(cellIndex);
					cell.setCellValue(tableRowHeaders[y]);
					cell.setCellStyle(cellStyle);
					cellIndex = 1;
					HSSFCell cells = row.createCell(cellIndex);
					switch (y) {
					case 0:
						for (int x = 0; x < tableCellHeaders2.length - 1; x++) {
							cells = row.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cells.setCellValue(qdAllList.get(0).getAssigned());
								break;
							case 1:
								cells.setCellValue(qdAllList.get(0).getActual());
								break;
							default:
								break;
							}
							cells.setCellStyle(cellStyle);
						}
						break;
					case 1:
						for (int x = 0; x < tableCellHeaders2.length - 1; x++) {
							cells = row.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cells.setCellValue(qdOpList.get(0).getAssigned());
								break;
							case 1:
								cells.setCellValue(qdOpList.get(0).getActual());
								break;
							default:
								break;
							}
							cells.setCellStyle(cellStyle);
						}
						break;
					case 2:
						for (int x = 0; x < tableCellHeaders2.length - 1; x++) {
							cells = row.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cells.setCellValue(qdIpList.get(0).getAssigned());
								break;
							case 1:
								cells.setCellValue(qdIpList.get(0).getActual());
								break;
							default:
								break;
							}
							cells.setCellStyle(cellStyle);
						}
						break;
					default:
						break;

					}
					row = sheet.createRow(rowIndex + y);
					cellIndex = 0;
				}
				/// 最後設定autosize
				for (int i = 0; i < tableCellHeaders.length; i++) {
					sheet.autoSizeColumn(i);
					sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
				}

			} else {
				/// 多季度資料匯出
				String[] tableCellHeaders = { "申報與分配點數與達成率", "病歷總點數(含自費)", "申報總點數", "分配額度總點數", "超額總點數", "總額達成率" };
				HSSFSheet sheet = workbook.createSheet("多季度");
				cellIndex = 0;
				rowIndex = 0;
				/// 欄位A1
				HSSFRow row = sheet.createRow(0);
				HSSFCell cell = row.createCell(0);
				cell.setCellValue("申報季度");
				cell.setCellStyle(cellStyle);
				cellIndex = 1;
				for (int v = 0; v < qdAllList.size(); v++) {
					cell = row.createCell(cellIndex + v);
					cell.setCellValue(qdAllList.get(v).getName());
					cell.setCellStyle(cellStyle);
				}

				/// 欄位A3
				row = sheet.createRow(2);
				cell = row.createCell(0);
				cell.setCellValue("門急診/住院");
				cell.setCellStyle(cellStyle);

				/// 欄位A4
				row = sheet.createRow(3);
				for (int x = 0; x < tableCellHeaders.length; x++) {
					cell = row.createCell(x);
					cell.setCellValue(tableCellHeaders[x]);
					cell.setCellStyle(cellStyle);
				}

				/// 欄位A5
				row = sheet.createRow(4);
				cellIndex = 0;
				rowIndex = 5;
				for (int v = 0; v < qdAllList.size(); v++) {
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdAllList.get(v).getName());
					cell.setCellStyle(cellTitleStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdAllList.get(v).getOriginal());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdAllList.get(v).getActual());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdAllList.get(v).getAssigned());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdAllList.get(v).getOver());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdAllList.get(v).getPercent() + "%");
					cell.setCellStyle(cellStyle);

					rowIndex += v;
					row = sheet.createRow(rowIndex);
					cellIndex = 0;
				}

				/// 欄位 rowIndex
				row = sheet.createRow(rowIndex);
				cell = row.createCell(0);
				cell.setCellValue("門急診");
				cell.setCellStyle(cellStyle);
				rowIndex++;

				/// 欄位 rowIndex
				row = sheet.createRow(rowIndex);
				for (int x = 0; x < tableCellHeaders.length; x++) {
					cell = row.createCell(x);
					cell.setCellValue(tableCellHeaders[x]);
					cell.setCellStyle(cellStyle);
				}
				rowIndex++;

				/// 欄位 rowIndex
				row = sheet.createRow(rowIndex);
				cellIndex = 0;
				rowIndex++;
				for (int v = 0; v < qdAllList.size(); v++) {
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdAllList.get(v).getName());
					cell.setCellStyle(cellTitleStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdAllList.get(v).getOriginal());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdAllList.get(v).getActual());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdAllList.get(v).getAssigned());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdAllList.get(v).getOver());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdAllList.get(v).getPercent() + "%");
					cell.setCellStyle(cellStyle);

					rowIndex += v;
					row = sheet.createRow(rowIndex);
					cellIndex = 0;
				}

				/// 欄位 rowIndex
				row = sheet.createRow(rowIndex);
				cell = row.createCell(0);
				cell.setCellValue("住院");
				cell.setCellStyle(cellStyle);
				rowIndex++;

				/// 欄位 rowIndex
				row = sheet.createRow(rowIndex);
				for (int x = 0; x < tableCellHeaders.length; x++) {
					cell = row.createCell(x);
					cell.setCellValue(tableCellHeaders[x]);
					cell.setCellStyle(cellStyle);
				}
				rowIndex++;

				/// 欄位 rowIndex
				row = sheet.createRow(rowIndex);
				cellIndex = 0;
				rowIndex++;
				for (int v = 0; v < qdAllList.size(); v++) {
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdAllList.get(v).getName());
					cell.setCellStyle(cellTitleStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdAllList.get(v).getOriginal());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdAllList.get(v).getActual());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdAllList.get(v).getAssigned());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdAllList.get(v).getOver());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdAllList.get(v).getPercent() + "%");
					cell.setCellStyle(cellStyle);

					rowIndex += v;
					row = sheet.createRow(rowIndex);
					cellIndex = 0;
				}
				/// 最後設定autosize
				for (int i = 0; i < tableCellHeaders.length; i++) {
					sheet.autoSizeColumn(i);
					sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
				}
			}
		} else {
			HSSFSheet sheet = workbook.createSheet("工作表");
		}

		String fileNameStr = "健保總額累積達成率";
		String fileName = URLEncoder.encode(fileNameStr, "UTF-8");
		String filepath = (System.getProperty("os.name").toLowerCase().startsWith("windows"))
				? FILE_PATH + "\\" + fileName
				: FILE_PATH + "/" + fileName;
		File file = new File(filepath);
		response.reset();
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "*");
		response.setHeader("Content-Disposition",
				"attachment; filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + ".csv");
		response.setContentType("application/octet-stream;charset=utf8");
//		response.setContentType("application/vnd.ms-excel;charset=utf8");

		/// 最後由outputstream輸出
		OutputStream out = response.getOutputStream();

		workbook.write(out);
		out.flush();
		out.close();
		workbook.close();
	}

	/**
	 * 取得DRG分配比例月報表-匯出
	 * 
	 * @param year
	 * @param month
	 * @param response
	 * @throws IOException
	 */
	public void getDrgMonthlyExport(int year, int month, HttpServletResponse response) throws IOException {

		String monthStr = "";
		if (month < 10) {
			monthStr = "0" + month;
		} else {
			monthStr = String.valueOf(month);
		}

		String inputDate = DateTool.convertToChineseYear(year + monthStr + "00");
		/// 轉成民國年月
		String endDate = inputDate.substring(0, inputDate.length() - 2);

		String dateStr = year + "/" + month;

		DRGMonthlyPayload drgData = reportService.getDrgMonthly(year, month);

		// 建立新工作簿
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 樣式
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

		if (drgData != null) {
			/// 新建工作表
			HSSFSheet sheet = workbook.createSheet("DRG分配比例");
			String[] tableCellHeaders = { "總住院案件數(含)手術", "住院案件申報總點數 ", "住院案件病歷總點數(不含自費)" };
			String[] tableCellHeaders2 = { "DRG總案件數(含)手術", "DRG案件申報總點數", "DRG案件病歷總點數(不含自費)" };
			String[] tableCellHeaders3 = { "DRG件數佔率", "DRG費用佔率", "DRG案件支付差額點數" };

			/// 欄位A1
			HSSFRow row = sheet.createRow(0);
			HSSFCell cell = row.createCell(0);
			cell.setCellValue("統計月份");
			cell.setCellStyle(cellStyle);

			/// 欄位B1
			cell = row.createCell(1);
			cell.setCellValue(dateStr);
			cell.setCellStyle(cellStyle);

			/// 欄位A3
			row = sheet.createRow(2);
			for (int x = 0; x < tableCellHeaders.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A4
			row = sheet.createRow(3);
			for (int x = 0; x < tableCellHeaders.length; x++) {
				cell = row.createCell(x);
				switch (x) {
				case 0:
					cell.setCellValue(drgData.getQuantityIp());
					break;
				case 1:
					cell.setCellValue(drgData.getApplPointIp());
					break;
				case 2:
					cell.setCellValue(drgData.getPointIp());
					break;
				default:
					break;
				}
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A5
			row = sheet.createRow(4);
			for (int x = 0; x < tableCellHeaders2.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A6
			row = sheet.createRow(5);
			for (int x = 0; x < tableCellHeaders2.length; x++) {
				cell = row.createCell(x);
				switch (x) {
				case 0:
					cell.setCellValue(drgData.getQuantityDrg());
					break;
				case 1:
					cell.setCellValue(drgData.getApplPointDrg());
					break;
				case 2:
					cell.setCellValue(drgData.getPointDrg());
					break;
				default:
					break;
				}
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A7
			row = sheet.createRow(6);
			for (int x = 0; x < tableCellHeaders3.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A8
			row = sheet.createRow(7);
			for (int x = 0; x < tableCellHeaders3.length; x++) {
				cell = row.createCell(x);
				switch (x) {
				case 0:
					cell.setCellValue(drgData.getRateDrg() + "%");
					break;
				case 1:
					cell.setCellValue(drgData.getRatePointDrg() + "%");
					break;
				case 2:
					cell.setCellValue(drgData.getDiffDrg());
					break;
				default:
					break;
				}
				cell.setCellStyle(cellStyle);
			}

			/// 最後設定autosize
			for (int i = 0; i < tableCellHeaders.length; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}

			/// 新建工作表
			tableCellHeaders = new String[] { "", "DRG案件數", "非DRG案件數" };
			String[] tableRowHeaders = { "件數", "比例" };
			sheet = workbook.createSheet("DRG比例");

			/// 欄位A1
			row = sheet.createRow(0);
			cell = row.createCell(0);
			cell.setCellValue("DRG件數佔率");
			cell.setCellStyle(cellStyle);

			/// 欄位A2
			row = sheet.createRow(1);
			for (int x = 0; x < tableCellHeaders.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A3
			row = sheet.createRow(2);
			rowIndex = 3;
			for (int y = 0; y < tableRowHeaders.length; y++) {
				cell = row.createCell(0);
				cell.setCellValue(tableRowHeaders[y]);
				cell.setCellStyle(cellStyle);

				cellIndex = 1;
				switch (y) {
				case 0:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getQuantityDrg());
							break;
						case 1:
							cell.setCellValue(drgData.getQuantityIp() - drgData.getQuantityDrg());
							break;
						default:
							break;
						}
						cell.setCellStyle(cellStyle);
					}
					break;
				case 1:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getRateDrg() + "%");
							break;
						case 1:
							cell.setCellValue(100 - drgData.getRateDrg() + "%");
							break;
						default:
							break;
						}
						cell.setCellStyle(cellStyle);
					}
					break;
				default:
					break;

				}

				row = sheet.createRow(rowIndex + y);
			}

			/// 欄位A6
			row = sheet.createRow(5);
			cell = row.createCell(0);
			cell.setCellValue("DRG費用佔率");
			cell.setCellStyle(cellStyle);

			/// 欄位A7
			row = sheet.createRow(6);
			for (int x = 0; x < tableCellHeaders.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A8
			cellIndex = 1;
			row = sheet.createRow(7);
			rowIndex = 8;
			tableRowHeaders = new String[] { "申報點數", "比例" };
			for (int y = 0; y < tableRowHeaders.length; y++) {
				cell = row.createCell(0);
				cell.setCellValue(tableRowHeaders[y]);
				cell.setCellStyle(cellStyle);

				cellIndex = 1;
				switch (y) {
				case 0:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getApplPointDrg());
							break;
						case 1:
							cell.setCellValue(drgData.getApplPointIp() - drgData.getApplPointDrg());
							break;
						default:
							break;
						}
						cell.setCellStyle(cellStyle);
					}
					break;
				case 1:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getRatePointDrg() + "%");
							break;
						case 1:
							cell.setCellValue(100 - drgData.getRatePointDrg() + "%");
							break;
						default:
							break;
						}
						cell.setCellStyle(cellStyle);
					}
					break;
				default:
					break;

				}

				row = sheet.createRow(rowIndex + y);
			}

			/// 最後設定autosize
			for (int i = 0; i < tableCellHeaders.length; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}

			/// 新建工作表
			tableCellHeaders = new String[] { "DRG案件數比例趨勢圖", "", "", "DRG點數比例趨勢圖", "", "" };
			tableCellHeaders2 = new String[] { "週數", "DRG案件數", "非DRG案件數", "週數", "DRG案件點數", "非DRG案件點數" };
			sheet = workbook.createSheet("趨勢圖(全院)");
			cellIndex = 0;
			rowIndex = 0;

			/// 欄位A1
			row = sheet.createRow(0);
			for (int i = 0; i < tableCellHeaders.length; i++) {
				cell = row.createCell(i);
				cell.setCellStyle(cellTitleStyle);
				if (i == 0) {
					cell.setCellValue(tableCellHeaders[i]);
				} else if (i == 3) {
					cell.setCellValue(tableCellHeaders[i]);
				}
			}
			/// 合併欄位
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 3, 5));

			/// 欄位A2
			row = sheet.createRow(1);
			for (int x = 0; x < tableCellHeaders2.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders2[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A3
			row = sheet.createRow(2);
			rowIndex = 3;
			cellIndex = 0;
			NameValueList2 nvlQ = drgData.getQuantityMap().get("00");
			NameValueList2 nvlD = drgData.getPointMap().get("00");
			/// values
			for (int v1 = 0; v1 < nvlQ.getNames().size(); v1++) {
				String v1Name = nvlQ.getNames().get(v1);
				Long v1Val = nvlQ.getValues().get(v1);
				Long v1Val2 = nvlQ.getValues2().get(v1);
				String v2Name = nvlD.getNames().get(v1);
				Long v2Val = nvlD.getValues().get(v1);
				Long v2Val2 = nvlD.getValues2().get(v1);
				cell = row.createCell(cellIndex);
				cell.setCellValue(v1Name);
				cell.setCellStyle(cellStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				String v1Str = (v1Val == 0) ? "" : String.valueOf(v1Val);
				cell.setCellValue(v1Str);
				cell.setCellStyle(cellStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				String v1Str2 = (v1Val2 == 0) ? "" : String.valueOf(v1Val2);
				cell.setCellValue(v1Str2);
				cell.setCellStyle(cellStyle);

				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(v2Name);
				cell.setCellStyle(cellStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				String v2Str = (v2Val == 0) ? "" : String.valueOf(v2Val);
				cell.setCellValue(v2Str);
				cell.setCellStyle(cellStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				String v2Str2 = (v2Val2 == 0) ? "" : String.valueOf(v2Val2);
				cell.setCellValue(v2Str2);
				cell.setCellStyle(cellStyle);

				row = sheet.createRow(rowIndex + v1);
				cellIndex = 0;
			}
			/// 最後設定autosize
			for (int i = 0; i < tableCellHeaders.length; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}

		} else {
			HSSFSheet sheet = workbook.createSheet("工作表");
		}

		String fileNameStr = "DRG分配比例月報表_" + endDate;
		String fileName = URLEncoder.encode(fileNameStr, "UTF-8");
		String filepath = (System.getProperty("os.name").toLowerCase().startsWith("windows"))
				? FILE_PATH + "\\" + fileName
				: FILE_PATH + "/" + fileName;
		File file = new File(filepath);
		response.reset();
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "*");
		response.setHeader("Content-Disposition",
				"attachment; filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + ".csv");
		response.setContentType("application/octet-stream;charset=utf8");
//		response.setContentType("application/vnd.ms-excel;charset=utf8");

		/// 最後由outputstream輸出
		OutputStream out = response.getOutputStream();

		workbook.write(out);
		out.flush();
		out.close();
		workbook.close();

	}

	/**
	 * 取得DRG各科分配比例月報表-匯出
	 * 
	 * @param year
	 * @param month
	 * @param response
	 * @throws IOException
	 */
	public void getDrgMonthlyAllFuncTypeExport(int year, int month, HttpServletResponse response) throws IOException {
		String monthStr = "";
		if (month < 10) {
			monthStr = "0" + month;
		} else {
			monthStr = String.valueOf(month);
		}

		String inputDate = DateTool.convertToChineseYear(year + monthStr + "00");
		/// 轉成民國年月
		String endDate = inputDate.substring(0, inputDate.length() - 2);

		String dateStr = year + "/" + month;

		DRGMonthlyPayload drgData = reportService.getDrgMonthlyAllFuncType(year, month);

		List<CODE_TABLE> ctModelList = codeTableDao.findByCat("FUNC_TYPE");

		// 建立新工作簿
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 樣式
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

		if (drgData != null) {
			/// 新建工作表
			HSSFSheet sheet = workbook.createSheet("DRG各科分配(全院)");
			String[] tableCellHeaders = { "總住院案件數(含)手術", "住院案件申報總點數 ", "住院案件病歷總點數(不含自費)" };
			String[] tableCellHeaders2 = { "DRG總案件數(含)手術", "DRG案件申報總點數", "DRG案件病歷總點數(不含自費)" };
			String[] tableCellHeaders3 = { "DRG件數佔率", "DRG費用佔率", "DRG案件支付差額點數" };

			/// 欄位A1
			HSSFRow row = sheet.createRow(0);
			HSSFCell cell = row.createCell(0);
			cell.setCellValue("統計月份");
			cell.setCellStyle(cellStyle);

			/// 欄位B1
			cell = row.createCell(1);
			cell.setCellValue(dateStr);
			cell.setCellStyle(cellStyle);

			/// 欄位A3
			row = sheet.createRow(2);
			for (int x = 0; x < tableCellHeaders.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A4
			row = sheet.createRow(3);
			for (int x = 0; x < tableCellHeaders.length; x++) {
				cell = row.createCell(x);
				switch (x) {
				case 0:
					cell.setCellValue(drgData.getQuantityIp());
					break;
				case 1:
					cell.setCellValue(drgData.getApplPointIp());
					break;
				case 2:
					cell.setCellValue(drgData.getPointIp());
					break;
				default:
					break;
				}
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A5
			row = sheet.createRow(4);
			for (int x = 0; x < tableCellHeaders2.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A6
			row = sheet.createRow(5);
			for (int x = 0; x < tableCellHeaders2.length; x++) {
				cell = row.createCell(x);
				switch (x) {
				case 0:
					cell.setCellValue(drgData.getQuantityDrg());
					break;
				case 1:
					cell.setCellValue(drgData.getApplPointDrg());
					break;
				case 2:
					cell.setCellValue(drgData.getPointDrg());
					break;
				default:
					break;
				}
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A7
			row = sheet.createRow(6);
			for (int x = 0; x < tableCellHeaders3.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A8
			row = sheet.createRow(7);
			for (int x = 0; x < tableCellHeaders3.length; x++) {
				cell = row.createCell(x);
				switch (x) {
				case 0:
					cell.setCellValue(drgData.getRateDrg() + "%");
					break;
				case 1:
					cell.setCellValue(drgData.getRatePointDrg() + "%");
					break;
				case 2:
					cell.setCellValue(drgData.getDiffDrg());
					break;
				default:
					break;
				}
				cell.setCellStyle(cellStyle);
			}

			/// 最後設定autosize
			for (int i = 0; i < tableCellHeaders.length; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}

			/// 新建工作表
			tableCellHeaders = new String[] { "", "DRG案件數", "非DRG案件數" };
			String[] tableRowHeaders = { "件數", "比例" };
			sheet = workbook.createSheet("DRG比例(全院)");

			/// 欄位A1
			row = sheet.createRow(0);
			cell = row.createCell(0);
			cell.setCellValue("DRG件數佔率");
			cell.setCellStyle(cellStyle);

			/// 欄位A2
			row = sheet.createRow(1);
			for (int x = 0; x < tableCellHeaders.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A3
			row = sheet.createRow(2);
			rowIndex = 3;
			for (int y = 0; y < tableRowHeaders.length; y++) {
				cell = row.createCell(0);
				cell.setCellValue(tableRowHeaders[y]);
				cell.setCellStyle(cellStyle);

				cellIndex = 1;
				switch (y) {
				case 0:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getQuantityDrg());
							break;
						case 1:
							cell.setCellValue(drgData.getQuantityIp() - drgData.getQuantityDrg());
							break;
						default:
							break;
						}
						cell.setCellStyle(cellStyle);
					}
					break;
				case 1:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getRateDrg() + "%");
							break;
						case 1:
							cell.setCellValue(100 - drgData.getRateDrg() + "%");
							break;
						default:
							break;
						}
						cell.setCellStyle(cellStyle);
					}
					break;
				default:
					break;

				}

				row = sheet.createRow(rowIndex + y);
			}

			/// 欄位A6
			row = sheet.createRow(5);
			cell = row.createCell(0);
			cell.setCellValue("DRG費用佔率");
			cell.setCellStyle(cellStyle);

			/// 欄位A7
			row = sheet.createRow(6);
			for (int x = 0; x < tableCellHeaders.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A8
			cellIndex = 1;
			row = sheet.createRow(7);
			rowIndex = 8;
			tableRowHeaders = new String[] { "申報點數", "比例" };
			for (int y = 0; y < tableRowHeaders.length; y++) {
				cell = row.createCell(0);
				cell.setCellValue(tableRowHeaders[y]);
				cell.setCellStyle(cellStyle);

				cellIndex = 1;
				switch (y) {
				case 0:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getApplPointDrg());
							break;
						case 1:
							cell.setCellValue(drgData.getApplPointIp() - drgData.getApplPointDrg());
							break;
						default:
							break;
						}
						cell.setCellStyle(cellStyle);
					}
					break;
				case 1:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getRatePointDrg() + "%");
							break;
						case 1:
							cell.setCellValue(100 - drgData.getRatePointDrg() + "%");
							break;
						default:
							break;
						}
						cell.setCellStyle(cellStyle);
					}
					break;
				default:
					break;

				}

				row = sheet.createRow(rowIndex + y);
			}

			/// 最後設定autosize
			for (int i = 0; i < tableCellHeaders.length; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}

			/// 新建工作表
			tableCellHeaders = new String[] { "DRG案件數比例趨勢圖", "", "", "DRG點數比例趨勢圖", "", "" };
			tableCellHeaders2 = new String[] { "週數", "DRG案件數", "非DRG案件數", "週數", "DRG案件點數", "非DRG案件點數" };
			cellIndex = 0;
			rowIndex = 0;

			List<String> funcTypes = drgData.getFuncTypes();

			for (String funcName : funcTypes) {
				if (funcName.equals("不分科")) {
					sheet = workbook.createSheet("趨勢圖(全院)");
					/// 欄位A1
					row = sheet.createRow(0);
					for (int i = 0; i < tableCellHeaders.length; i++) {
						cell = row.createCell(i);
						cell.setCellStyle(cellTitleStyle);
						if (i == 0) {
							cell.setCellValue(tableCellHeaders[i]);
						} else if (i == 3) {
							cell.setCellValue(tableCellHeaders[i]);
						}
					}
					/// 合併欄位
					sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
					sheet.addMergedRegion(new CellRangeAddress(0, 0, 3, 5));

					/// 欄位A2
					row = sheet.createRow(1);
					for (int x = 0; x < tableCellHeaders2.length; x++) {
						cell = row.createCell(x);
						cell.setCellValue(tableCellHeaders2[x]);
						cell.setCellStyle(cellStyle);
					}

					/// 欄位A3
					row = sheet.createRow(2);
					rowIndex = 3;
					cellIndex = 0;
					NameValueList2 nvlQ = drgData.getQuantityMap().get("00");
					NameValueList2 nvlD = drgData.getPointMap().get("00");
					/// values
					for (int v1 = 0; v1 < nvlQ.getNames().size(); v1++) {
						String v1Name = nvlQ.getNames().get(v1);
						Long v1Val = nvlQ.getValues().get(v1);
						Long v1Val2 = nvlQ.getValues2().get(v1);
						String v2Name = nvlD.getNames().get(v1);
						Long v2Val = nvlD.getValues().get(v1);
						Long v2Val2 = nvlD.getValues2().get(v1);
						cell = row.createCell(cellIndex);
						cell.setCellValue(v1Name);
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						String v1Str = (v1Val == 0) ? "" : String.valueOf(v1Val);
						cell.setCellValue(v1Str);
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						String v1Str2 = (v1Val2 == 0) ? "" : String.valueOf(v1Val2);
						cell.setCellValue(v1Str2);
						cell.setCellStyle(cellStyle);

						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(v2Name);
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						String v2Str = (v2Val == 0) ? "" : String.valueOf(v2Val);
						cell.setCellValue(v2Str);
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						String v2Str2 = (v2Val2 == 0) ? "" : String.valueOf(v2Val2);
						cell.setCellValue(v2Str2);
						cell.setCellStyle(cellStyle);

						row = sheet.createRow(rowIndex + v1);
						cellIndex = 0;
					}
					cellIndex = 0;
					rowIndex = 0;
					/// 最後設定autosize
					for (int i = 0; i < tableCellHeaders.length; i++) {
						sheet.autoSizeColumn(i);
						sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
					}
				} else {
					String funcCode = "";
					/// 先用funcType中文取得對應的code
					for (CODE_TABLE model : ctModelList) {
						if (model.getDescChi().equals(funcName)) {
							funcCode = model.getCode();
						}
					}
					NameValueList2 nvlQ = drgData.getQuantityMap().get(funcCode);
					NameValueList2 nvlD = drgData.getPointMap().get(funcCode);
					if (nvlQ == null) {
						continue;
					}
					sheet = workbook.createSheet("趨勢圖(" + funcName + ")");

					/// 欄位A1
					row = sheet.createRow(0);
					cell = row.createCell(0);
					cell.setCellValue(funcName);

					/// 欄位A2
					row = sheet.createRow(1);
					for (int i = 0; i < tableCellHeaders.length; i++) {
						cell = row.createCell(i);
						cell.setCellStyle(cellTitleStyle);
						if (i == 0) {
							cell.setCellValue(tableCellHeaders[i]);
						} else if (i == 3) {
							cell.setCellValue(tableCellHeaders[i]);
						}
					}
					/// 合併欄位
					sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 2));
					sheet.addMergedRegion(new CellRangeAddress(1, 1, 3, 5));

					/// 欄位A3
					row = sheet.createRow(2);
					for (int x = 0; x < tableCellHeaders2.length; x++) {
						cell = row.createCell(x);
						cell.setCellValue(tableCellHeaders2[x]);
						cell.setCellStyle(cellStyle);
					}

					/// 欄位A4
					row = sheet.createRow(3);
					rowIndex = 4;
					cellIndex = 0;

					/// values
					for (int v1 = 0; v1 < nvlQ.getNames().size(); v1++) {
						String v1Name = nvlQ.getNames().get(v1);
						Long v1Val = nvlQ.getValues().get(v1);
						Long v1Val2 = nvlQ.getValues2().get(v1);
						String v2Name = nvlD.getNames().get(v1);
						Long v2Val = nvlD.getValues().get(v1);
						Long v2Val2 = nvlD.getValues2().get(v1);
						cell = row.createCell(cellIndex);
						cell.setCellValue(v1Name);
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						String v1Str = (v1Val == 0) ? "" : String.valueOf(v1Val);
						cell.setCellValue(v1Str);
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						String v1Str2 = (v1Val2 == 0) ? "" : String.valueOf(v1Val2);
						cell.setCellValue(v1Str2);
						cell.setCellStyle(cellStyle);

						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(v2Name);
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						String v2Str = (v2Val == 0) ? "" : String.valueOf(v2Val);
						cell.setCellValue(v2Str);
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						String v2Str2 = (v2Val2 == 0) ? "" : String.valueOf(v2Val2);
						cell.setCellValue(v2Str2);
						cell.setCellStyle(cellStyle);

						row = sheet.createRow(rowIndex + v1);
						cellIndex = 0;
					}
					cellIndex = 0;
					rowIndex = 0;
					/// 最後設定autosize
					for (int i = 0; i < tableCellHeaders.length; i++) {
						sheet.autoSizeColumn(i);
						sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
					}
				}
			}

		} else {
			HSSFSheet sheet = workbook.createSheet("工作表");
		}

		String fileNameStr = "DRG各科分配比例月報表_" + endDate;
		String fileName = URLEncoder.encode(fileNameStr, "UTF-8");
		String filepath = (System.getProperty("os.name").toLowerCase().startsWith("windows"))
				? FILE_PATH + "\\" + fileName
				: FILE_PATH + "/" + fileName;
		File file = new File(filepath);
		response.reset();
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "*");
		response.setHeader("Content-Disposition",
				"attachment; filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + ".csv");
		response.setContentType("application/octet-stream;charset=utf8");
//		response.setContentType("application/vnd.ms-excel;charset=utf8");

		/// 最後由outputstream輸出
		OutputStream out = response.getOutputStream();

		workbook.write(out);
		out.flush();
		out.close();
		workbook.close();
	}

	/**
	 * 取得DRG各區分配比例月報表-匯出
	 * 
	 * @param year
	 * @param month
	 * @param response
	 * @throws IOException
	 */
	public void getDrgMonthlySectionExport(int year, int month, HttpServletResponse response) throws IOException {
		String monthStr = "";
		if (month < 10) {
			monthStr = "0" + month;
		} else {
			monthStr = String.valueOf(month);
		}

		String inputDate = DateTool.convertToChineseYear(year + monthStr + "00");
		/// 轉成民國年月
		String endDate = inputDate.substring(0, inputDate.length() - 2);

		String dateStr = year + "/" + month;

		DRGMonthlySectionPayload drgData = reportService.getDrgMonthlySection(year, month);

		// 建立新工作簿
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 樣式
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

		if (drgData != null) {
			/// 新建工作表
			HSSFSheet sheet = workbook.createSheet("DRG各科分配(全院)");
			String[] tableCellHeaders = { "總住院案件數(含)手術", "住院案件申報總點數 ", "住院案件病歷總點數(不含自費)" };
			String[] tableCellHeaders2 = { "DRG總案件數(含)手術", "DRG案件申報總點數", "DRG案件病歷總點數(不含自費)" };
			String[] tableCellHeaders3 = { "DRG件數佔率", "DRG費用佔率", "DRG案件支付差額點數" };

			/// 欄位A1
			HSSFRow row = sheet.createRow(0);
			HSSFCell cell = row.createCell(0);
			cell.setCellValue("統計月份");
			cell.setCellStyle(cellStyle);

			/// 欄位B1
			cell = row.createCell(1);
			cell.setCellValue(dateStr);
			cell.setCellStyle(cellStyle);

			/// 欄位A3
			row = sheet.createRow(2);
			for (int x = 0; x < tableCellHeaders.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A4
			row = sheet.createRow(3);
			for (int x = 0; x < tableCellHeaders.length; x++) {
				cell = row.createCell(x);
				switch (x) {
				case 0:
					cell.setCellValue(drgData.getQuantityIp());
					break;
				case 1:
					cell.setCellValue(drgData.getApplPointIp());
					break;
				case 2:
					cell.setCellValue(drgData.getPointIp());
					break;
				default:
					break;
				}
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A5
			row = sheet.createRow(4);
			for (int x = 0; x < tableCellHeaders2.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A6
			row = sheet.createRow(5);
			for (int x = 0; x < tableCellHeaders2.length; x++) {
				cell = row.createCell(x);
				switch (x) {
				case 0:
					cell.setCellValue(drgData.getQuantityDrg());
					break;
				case 1:
					cell.setCellValue(drgData.getApplPointDrg());
					break;
				case 2:
					cell.setCellValue(drgData.getPointDrg());
					break;
				default:
					break;
				}
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A7
			row = sheet.createRow(6);
			for (int x = 0; x < tableCellHeaders3.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A8
			row = sheet.createRow(7);
			for (int x = 0; x < tableCellHeaders3.length; x++) {
				cell = row.createCell(x);
				switch (x) {
				case 0:
					cell.setCellValue(drgData.getRateDrg() + "%");
					break;
				case 1:
					cell.setCellValue(drgData.getRatePointDrg() + "%");
					break;
				case 2:
					cell.setCellValue(drgData.getDiffDrg());
					break;
				default:
					break;
				}
				cell.setCellStyle(cellStyle);
			}

			/// 最後設定autosize
			for (int i = 0; i < tableCellHeaders.length; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}

			/// 新建工作表
			tableCellHeaders = new String[] { "", "DRG案件數", "非DRG案件數" };
			String[] tableRowHeaders = { "件數", "比例" };
			sheet = workbook.createSheet("DRG比例");

			/// 欄位A1
			row = sheet.createRow(0);
			cell = row.createCell(0);
			cell.setCellValue("DRG件數佔率");
			cell.setCellStyle(cellStyle);

			/// 欄位A2
			row = sheet.createRow(1);
			for (int x = 0; x < tableCellHeaders.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A3
			row = sheet.createRow(2);
			rowIndex = 3;
			for (int y = 0; y < tableRowHeaders.length; y++) {
				cell = row.createCell(0);
				cell.setCellValue(tableRowHeaders[y]);
				cell.setCellStyle(cellStyle);

				cellIndex = 1;
				switch (y) {
				case 0:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getQuantityDrg());
							break;
						case 1:
							cell.setCellValue(drgData.getQuantityIp() - drgData.getQuantityDrg());
							break;
						default:
							break;
						}
						cell.setCellStyle(cellStyle);
					}
					break;
				case 1:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getRateDrg() + "%");
							break;
						case 1:
							cell.setCellValue(100 - drgData.getRateDrg() + "%");
							break;
						default:
							break;
						}
						cell.setCellStyle(cellStyle);
					}
					break;
				default:
					break;

				}

				row = sheet.createRow(rowIndex + y);
			}

			/// 欄位A6
			row = sheet.createRow(5);
			cell = row.createCell(0);
			cell.setCellValue("DRG費用佔率");
			cell.setCellStyle(cellStyle);

			/// 欄位A7
			row = sheet.createRow(6);
			for (int x = 0; x < tableCellHeaders.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A8
			cellIndex = 1;
			row = sheet.createRow(7);
			rowIndex = 8;
			tableRowHeaders = new String[] { "申報點數", "比例" };
			for (int y = 0; y < tableRowHeaders.length; y++) {
				cell = row.createCell(0);
				cell.setCellValue(tableRowHeaders[y]);
				cell.setCellStyle(cellStyle);

				cellIndex = 1;
				switch (y) {
				case 0:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getApplPointDrg());
							break;
						case 1:
							cell.setCellValue(drgData.getApplPointIp() - drgData.getApplPointDrg());
							break;
						default:
							break;
						}
						cell.setCellStyle(cellStyle);
					}
					break;
				case 1:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getRatePointDrg() + "%");
							break;
						case 1:
							cell.setCellValue(100 - drgData.getRatePointDrg() + "%");
							break;
						default:
							break;
						}
						cell.setCellStyle(cellStyle);
					}
					break;
				default:
					break;

				}

				row = sheet.createRow(rowIndex + y);
			}

			/// 最後設定autosize
			for (int i = 0; i < tableCellHeaders.length; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}

			/// 新建工作表
			tableCellHeaders = new String[] { "", "A 區", "B1 區", "B2 區", "C 區" };
			tableRowHeaders = new String[] { "DRG案件數", "比例", "DRG案件病歷總點數(不含自費)", "DRG案件申報總點數", "DRG案件支付差額點數",
					"DRG案件佔率" };
			cellIndex = 0;
			rowIndex = 0;
			sheet = workbook.createSheet("DRG各區差額(全院)");

			/// 欄位A1
			row = sheet.createRow(0);
			cell = row.createCell(0);
			for (int x = 0; x < tableCellHeaders.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellTitleStyle);
			}

			/// 欄位A2
			row = sheet.createRow(1);
			rowIndex = 2;
			cellIndex = 0;
			for (int y = 0; y < tableRowHeaders.length; y++) {
				cell = row.createCell(0);
				cell.setCellValue(tableRowHeaders[y]);
				cell.setCellStyle(cellStyle);
				cellIndex = 1;
				switch (y) {
				case 0:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getQuantityA());
							break;
						case 1:
							cell.setCellValue(drgData.getQuantityB1());
							break;
						case 2:
							cell.setCellValue(drgData.getQuantityB2());
							break;
						case 3:
							cell.setCellValue(drgData.getQuantityC());
							break;
						default:
							break;
						}
						cell.setCellStyle(cellStyle);
					}
					break;
				case 1:
					Long sum = drgData.getQuantityA() + drgData.getQuantityB1() + drgData.getQuantityB2()
							+ drgData.getQuantityC();
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						/// 四捨五入double型態取小數2位
						double d = Math.round(drgData.getQuantityA().doubleValue() / sum.doubleValue() * 100.0 * 100.0)
								/ 100.0;
						switch (x) {
						case 0:
							cell.setCellValue(d + "%");
							break;
						case 1:
							/// 四捨五入double型態取小數2位
							d = Math.round(drgData.getQuantityB1().doubleValue() / sum.doubleValue() * 100.0 * 100.0)
									/ 100.0;
							cell.setCellValue(d + "%");
							break;
						case 2:
							/// 四捨五入double型態取小數2位
							d = Math.round(drgData.getQuantityB2().doubleValue() / sum.doubleValue() * 100.0 * 100.0)
									/ 100.0;
							cell.setCellValue(d + "%");
							break;
						case 3:
							/// 四捨五入double型態取小數2位
							d = Math.round(drgData.getQuantityC().doubleValue() / sum.doubleValue() * 100.0 * 100.0)
									/ 100.0;
							cell.setCellValue(d + "%");
							break;
						default:
							break;
						}
						cell.setCellStyle(cellStyle);
					}
					break;
				case 2:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getActualA());
							break;
						case 1:
							cell.setCellValue(drgData.getActualB1());
							break;
						case 2:
							cell.setCellValue(drgData.getActualB2());
							break;
						case 3:
							cell.setCellValue(drgData.getActualC());
							break;
						default:
							break;
						}
						cell.setCellStyle(cellStyle);
					}
					break;
				case 3:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getApplA());
							break;
						case 1:
							cell.setCellValue(drgData.getApplB1());
							break;
						case 2:
							cell.setCellValue(drgData.getApplB2());
							break;
						case 3:
							cell.setCellValue(drgData.getApplC());
							break;
						default:
							break;
						}
						cell.setCellStyle(cellStyle);
					}
					break;
				case 4:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getDiffA());
							break;
						case 1:
							cell.setCellValue(drgData.getDiffB1());
							break;
						case 2:
							cell.setCellValue(drgData.getDiffB2());
							break;
						case 3:
							cell.setCellValue(drgData.getDiffC());
							break;
						default:
							break;
						}
						cell.setCellStyle(cellStyle);
					}
					break;
				case 5:
					sum = drgData.getApplA() + drgData.getApplB1() + drgData.getApplB2() + drgData.getApplC();
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						/// 四捨五入double型態取小數2位
						double d = Math.round(drgData.getApplA().doubleValue() / sum.doubleValue() * 100.0 * 100.0)
								/ 100.0;
						switch (x) {
						case 0:
							cell.setCellValue(d + "%");
							break;
						case 1:
							/// 四捨五入double型態取小數2位
							d = Math.round(drgData.getApplB1().doubleValue() / sum.doubleValue() * 100.0 * 100.0)
									/ 100.0;
							cell.setCellValue(d + "%");
							break;
						case 2:
							/// 四捨五入double型態取小數2位
							d = Math.round(drgData.getApplB2().doubleValue() / sum.doubleValue() * 100.0 * 100.0)
									/ 100.0;
							cell.setCellValue(d + "%");
							break;
						case 3:
							/// 四捨五入double型態取小數2位
							d = Math.round(drgData.getApplC().doubleValue() / sum.doubleValue() * 100.0 * 100.0)
									/ 100.0;
							cell.setCellValue(d + "%");
							break;
						default:
							break;
						}
						cell.setCellStyle(cellStyle);
					}
					break;
				default:
					break;
				}

				row = sheet.createRow(rowIndex + y);
				cellIndex = 1;
			}

			/// 最後設定autosize
			for (int i = 0; i < tableCellHeaders.length; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}

			/// 新建工作表
			List<String> tableCellHeadersList = new ArrayList<String>();
			List<String> tableCellHeadersList2 = new ArrayList<String>();
			tableCellHeaders = new String[] { "A區", "B1區", "B2區", "C區", };
			tableRowHeaders = new String[] { "DRG案件數", "DRG案件申報總點數", "DRG案件支付差額點數", "DRG案件佔率" };
			cellIndex = 0;
			rowIndex = 0;
			int y1, y2, x1, x2;
			sheet = workbook.createSheet("DRG各科別各區差額資訊(各科)");
			/// 以下資料筆數皆為相同
			List<NameCodePointQuantity> sectonA = drgData.getSectionA();
			List<NameCodePointQuantity> sectonB1 = drgData.getSectionB1();
			List<NameCodePointQuantity> sectonB2 = drgData.getSectionB2();
			List<NameCodePointQuantity> sectonC = drgData.getSectionC();
			List<NameCodePoint> diffB1 = drgData.getDiffB1FuncType();
			List<NameCodePoint> diffB2 = drgData.getDiffB2FuncType();
			List<NameCodePoint> diffC = drgData.getDiffCFuncType();
			/// 將表頭先拿出
			for (NameCodePointQuantity ncpq : sectonA) {
				tableCellHeadersList.add(ncpq.getName());
				/// 有幾個funcType就塞入幾次
				Collections.addAll(tableCellHeadersList2, tableCellHeaders);
			}
			y1 = 0;
			y2 = 0;
			x1 = 1;
			x2 = 4;
			row = sheet.createRow(0);
			cellIndex = 1;
			/// 欄位B1
			for (String name : tableCellHeadersList) {
				for (int i = 0; i < 4; i++) {
					cell = row.createCell(cellIndex + i);
					cell.setCellStyle(cellTitleStyle);
					if (i == 0) {

						cell.setCellValue(name);
					}
				}
				/// merge欄位
				sheet.addMergedRegion(new CellRangeAddress(y1, y2, x1, x2));
				x1 += 4;
				x2 += 4;
				cellIndex += 4;
			}

			/// 欄位A2
			cellIndex = 1;
			rowIndex = 0;
			row = sheet.createRow(1);
			for (String name : tableCellHeadersList2) {
				cell = row.createCell(cellIndex);
				cell.setCellValue(name);
				cell.setCellStyle(cellTitleStyle);
				cellIndex++;
			}

			/// 欄位A3
			row = sheet.createRow(2);
			rowIndex = 3;
			cellIndex = 1;
			for (int y = 0; y < tableRowHeaders.length; y++) {
				cell = row.createCell(0);
				cell.setCellValue(tableRowHeaders[y]);
				cell.setCellStyle(cellStyle);
				switch (y) {
				case 0:/// DRG案件數
					for (int x = 0; x < tableCellHeadersList.size(); x++) {
						if (tableCellHeadersList.get(x).equals(sectonA.get(x).getName())) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(sectonA.get(x).getQuantity());
							cell.setCellStyle(cellStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(sectonB1.get(x).getQuantity());
							cell.setCellStyle(cellStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(sectonB2.get(x).getQuantity());
							cell.setCellStyle(cellStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(sectonC.get(x).getQuantity());
							cell.setCellStyle(cellStyle);
							cellIndex++;
						}
					}
					cellIndex = 1;
					break;
				case 1:/// DRG案件申報總點數
					for (int x = 0; x < tableCellHeadersList.size(); x++) {
						if (tableCellHeadersList.get(x).equals(sectonA.get(x).getName())) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(sectonA.get(x).getPoint());
							cell.setCellStyle(cellStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(sectonB1.get(x).getPoint());
							cell.setCellStyle(cellStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(sectonB2.get(x).getPoint());
							cell.setCellStyle(cellStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(sectonC.get(x).getPoint());
							cell.setCellStyle(cellStyle);
							cellIndex++;
						}
					}
					cellIndex = 1;
					break;
				case 2:/// DRG案件支付差額點數
					for (int x = 0; x < tableCellHeadersList.size(); x++) {
						if (tableCellHeadersList.get(x).equals(sectonA.get(x).getName())) {
							cell = row.createCell(cellIndex);
							cell.setCellValue("");
							cell.setCellStyle(cellStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(diffB1.get(x).getPoint());
							cell.setCellStyle(cellStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(diffB2.get(x).getPoint());
							cell.setCellStyle(cellStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(diffC.get(x).getPoint());
							cell.setCellStyle(cellStyle);
							cellIndex++;
						}
					}
					cellIndex = 1;
					break;
				case 3:/// DRG案件佔率
					Long tPointA = 0L;
					Long tPointB1 = 0L;
					Long tPointB2 = 0L;
					Long tPointC = 0L;
					/// 總計各區總點數
					for (int v = 0; v < sectonA.size(); v++) {

						tPointA += sectonA.get(v).getPoint();
						tPointB1 += sectonA.get(v).getPoint();
						tPointB2 += sectonA.get(v).getPoint();
						tPointC += sectonA.get(v).getPoint();
					}

					for (int x = 0; x < tableCellHeadersList.size(); x++) {
						if (tableCellHeadersList.get(x).equals(sectonA.get(x).getName())) {
							cell = row.createCell(cellIndex);
							double d = Math.round(
									sectonA.get(x).getPoint().doubleValue() / tPointA.doubleValue() * 100.0 * 100.0)
									/ 100.0;
							cell.setCellValue(d + "%");
							cell.setCellStyle(cellStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							d = Math.round(
									sectonB1.get(x).getPoint().doubleValue() / tPointB1.doubleValue() * 100.0 * 100.0)
									/ 100.0;
							cell.setCellValue(d + "%");
							cell.setCellStyle(cellStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							d = Math.round(
									sectonB2.get(x).getPoint().doubleValue() / tPointB2.doubleValue() * 100.0 * 100.0)
									/ 100.0;
							cell.setCellValue(d + "%");
							cell.setCellStyle(cellStyle);
							cellIndex++;
							d = Math.round(
									sectonC.get(x).getPoint().doubleValue() / tPointC.doubleValue() * 100.0 * 100.0)
									/ 100.0;
							cell = row.createCell(cellIndex);
							cell.setCellValue(d + "%");
							cell.setCellStyle(cellStyle);
							cellIndex++;
						}
					}
					cellIndex = 1;
					break;
				default:
					break;
				}
				row = sheet.createRow(rowIndex + y);
			}
			/// 最後設定autosize
			for (int i = 0; i < tableCellHeaders.length; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}

			/// 新建工作表
			tableCellHeaders = new String[] { "A區", "B1區", "B2區", "C區" };
			tableCellHeaders2 = new String[] { "週數", "申報點數", "案件數", "週數", "申報點數", "案件數", "週數", "申報點數", "案件數", "週數",
					"申報點數", "案件數", };
			cellIndex = 0;
			rowIndex = 0;
			List<String> funcTypes = drgData.getFuncTypes();
			for (String funcName : funcTypes) {
				if (funcName.equals("不分科")) {
					sheet = workbook.createSheet("案件趨勢圖(全院)");

					/// 欄位A2
					row = sheet.createRow(1);
					cell = row.createCell(0);
					cell.setCellValue("全院");
					cell.setCellStyle(cellStyle);

					/// 欄位A3
					row = sheet.createRow(2);
					y1 = 0;
					y2 = 0;
					x1 = 0;
					x2 = 2;
					for (String name : tableCellHeaders) {
						for (int i = 0; i < 3; i++) {
							cell = row.createCell(cellIndex + i);
							cell.setCellStyle(cellTitleStyle);
							if (i == 0) {

								cell.setCellValue(name);
							}
						}
						/// merge欄位
						sheet.addMergedRegion(new CellRangeAddress(y1, y2, x1, x2));
						x1 += 3;
						x2 += 3;
						cellIndex += 3;
					}

					/// 欄位A4
					row = sheet.createRow(3);
					cell = row.createCell(0);
					cellIndex = 1;
					for (int i = 0; i < tableCellHeaders2.length; i++) {
						cell.setCellValue(tableCellHeaders2[i]);
						cell.setCellStyle(cellStyle);
						cell = row.createCell(cellIndex + i);
					}

					/// 欄位A5
					NameValueList2 nvlA = drgData.getWeeklyAMap().get("不分科");
					NameValueList2 nvlB1 = drgData.getWeeklyB1Map().get("不分科");
					NameValueList2 nvlB2 = drgData.getWeeklyB2Map().get("不分科");
					NameValueList2 nvlC = drgData.getWeeklyCMap().get("不分科");
					row = sheet.createRow(4);
					cellIndex = 0;
					rowIndex = 5;
					for (int v1 = 0; v1 < nvlA.getNames().size(); v1++) {
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlA.getNames().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlA.getValues().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlA.getValues2().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;

						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlB1.getNames().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlB1.getValues().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlB1.getValues2().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;

						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlB2.getNames().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlB2.getValues().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlB2.getValues2().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;

						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlC.getNames().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlC.getValues().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlC.getValues2().get(v1));
						cell.setCellStyle(cellStyle);

						row = sheet.createRow(rowIndex + v1);
						cellIndex = 0;
					}
					for (int i = 0; i < tableCellHeaders2.length; i++) {
						sheet.autoSizeColumn(i);
						sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
					}
					cellIndex = 0;
					rowIndex = 0;

				} else {
					if (drgData.getWeeklyAMap().get(funcName) == null) {
						continue;
					}
					sheet = workbook.createSheet("案件趨勢圖(" + funcName + ")");

					/// 欄位A2
					row = sheet.createRow(1);
					cell = row.createCell(0);
					cell.setCellValue(funcName);
					cell.setCellStyle(cellStyle);

					/// 欄位A3
					row = sheet.createRow(2);
					y1 = 0;
					y2 = 0;
					x1 = 0;
					x2 = 2;
					for (String name : tableCellHeaders) {
						for (int i = 0; i < 3; i++) {
							cell = row.createCell(cellIndex + i);
							cell.setCellStyle(cellTitleStyle);
							if (i == 0) {

								cell.setCellValue(name);
							}
						}
						/// merge欄位
						sheet.addMergedRegion(new CellRangeAddress(y1, y2, x1, x2));
						x1 += 3;
						x2 += 3;
						cellIndex += 3;
					}

					/// 欄位A4
					row = sheet.createRow(3);
					cell = row.createCell(0);
					cellIndex = 1;
					for (int i = 0; i < tableCellHeaders2.length; i++) {
						cell.setCellValue(tableCellHeaders2[i]);
						cell.setCellStyle(cellStyle);
						cell = row.createCell(cellIndex + i);
					}

					/// 欄位A5
					NameValueList2 nvlA = drgData.getWeeklyAMap().get("不分科");
					NameValueList2 nvlB1 = drgData.getWeeklyB1Map().get("不分科");
					NameValueList2 nvlB2 = drgData.getWeeklyB2Map().get("不分科");
					NameValueList2 nvlC = drgData.getWeeklyCMap().get("不分科");
					row = sheet.createRow(4);
					cellIndex = 0;
					rowIndex = 5;
					for (int v1 = 0; v1 < nvlA.getNames().size(); v1++) {
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlA.getNames().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlA.getValues().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlA.getValues2().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;

						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlB1.getNames().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlB1.getValues().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlB1.getValues2().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;

						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlB2.getNames().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlB2.getValues().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlB2.getValues2().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;

						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlC.getNames().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlC.getValues().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlC.getValues2().get(v1));
						cell.setCellStyle(cellStyle);

						row = sheet.createRow(rowIndex + v1);
						cellIndex = 0;
					}
					for (int i = 0; i < tableCellHeaders2.length; i++) {
						sheet.autoSizeColumn(i);
						sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
					}
					cellIndex = 0;
					rowIndex = 0;

				}
			}

		} else {
			HSSFSheet sheet = workbook.createSheet("工作表");
		}

		String fileNameStr = "DRG各科別各區分配資訊報表_" + endDate;
		String fileName = URLEncoder.encode(fileNameStr, "UTF-8");
		String filepath = (System.getProperty("os.name").toLowerCase().startsWith("windows"))
				? FILE_PATH + "\\" + fileName
				: FILE_PATH + "/" + fileName;
		File file = new File(filepath);
		response.reset();
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "*");
		response.setHeader("Content-Disposition",
				"attachment; filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + ".csv");
		response.setContentType("application/octet-stream;charset=utf8");
//		response.setContentType("application/vnd.ms-excel;charset=utf8");

		/// 最後由outputstream輸出
		OutputStream out = response.getOutputStream();

		workbook.write(out);
		out.flush();
		out.close();
		workbook.close();
	}
	
	/**
	 * 取得門急診/住院/出院人次變化-匯出
	 * 
	 * @param year
	 * @param quarter
	 * @param response
	 * @throws IOException
	 */
	public void getVisitsVarietyExport(VisitsVarietyPayload visitsVarietyPayload,HttpServletResponse response,String year,String week,String sdate,String edate) {
		
		StringBuilder yearWeek=new StringBuilder();
		yearWeek.append(year+"/"+week+"週");
		
		StringBuilder day=new StringBuilder();
		day.append(sdate+"~"+edate);
		
		//實際總點數
		PointPeriod actual= visitsVarietyPayload.getActual();
		//門急診-住院
		Long all=actual.getAll();
		//門急診
		Long opem=actual.getOpem();
		//門診 早
		Long opMorning=actual.getOpMorning();
		//門診 中
		Long opAfternoon=actual.getOpAfternoon();
		//門診 晚
		Long opNight=actual.getOpNight();
		//急診
		Long em=actual.getEm();
		//住院
		Long ip=actual.getIp();
		
		//申報總點數
		PointPeriod appl=visitsVarietyPayload.getAppl();
		//門急診-住院
		Long all_appl=appl.getAll();
		//門急診
		Long opem_appl=appl.getOpem();
		//門診 早
		Long opMorning_appl=appl.getOpMorning();
		//門診 中
		Long opAfternoon_appl=appl.getOpAfternoon();
		//門診 晚
		Long opNight_appl=appl.getOpNight();
		//急診
		Long em_appl=appl.getEm();
		//住院
		Long ip_appl=appl.getIp();
		
		//統計截止人次
		VisitsPeriod visitsPeriod=visitsVarietyPayload.getVisitsPeriod();
		//總人次
		VisitsPeriodDetail total=visitsPeriod.getTotal();
		Long total_all=total.getAll();
		Long total_opem=total.getOpem();
		Long total_opMorning=total.getOpMorning();
		Long total_opAfternoon=total.getOpAfternoon();
		Long total_opNight=total.getOpNight();
		Long total_em=total.getEm();
		Long total_ip=total.getIp();
		Long total_leave=total.getLeave();
		
		//手術人次
		VisitsPeriodDetail surgery=visitsPeriod.getSurgery();
		Long surgery_all=surgery.getAll();
		Long surgery_opem=surgery.getOpem();
		Long surgery_opMorning=surgery.getOpMorning();
		Long surgery_opAfternoon=surgery.getOpAfternoon();
		Long surgery_opNight=surgery.getOpNight();
		Long surgery_em=surgery.getEm();
		Long surgery_ip=surgery.getIp();
		Long surgery_leave=surgery.getLeave();
		
		//上月同區間總人次相比差額
		VisitsPeriodDetail diff=visitsPeriod.getDiff();
		Long diff_all=diff.getAll();
		Long diff_opem=diff.getOpem();
		Long diff_opMorning=diff.getOpMorning();
		Long diff_opAfternoon=diff.getOpAfternoon();
		Long diff_opNight=diff.getOpNight();
		Long diff_em=diff.getEm();
		Long diff_ip=diff.getIp();
		Long diff_leave=diff.getLeave();
		
		//上月同區間總差額率-門急診和住院
		Float percentAll=visitsPeriod.getPercentAll();
		//上月同區間總差額率-門急診
		Float percentOpem=visitsPeriod.getPercentOpem();
		//上月同區間總差額率-門診(早)
		Float percentOpMorning=visitsPeriod.getPercentOpMorning();
		//上月同區間總差額率-門診(中)
		Float percentOpAfternoon=visitsPeriod.getPercentOpAfternoon();
		//上月同區間總差額率-門診(晚)
		Float percentOpNight=visitsPeriod.getPercentOpNight();
		//上月同區間總差額率-急診
		Float percentEm=visitsPeriod.getPercentEm();
		//上月同區間總差額率-住院
		Float percentIp=visitsPeriod.getPercentIp();
		//上月同區間總差額率-出院
		Float percentLeave=visitsPeriod.getPercentLeave();
		
		//科名
		List<String> funcTypes=visitsVarietyPayload.getFuncTypes();
		//各科門急診人次
		Map<String, NameValueList> opemMap=visitsVarietyPayload.getOpemMap();
		//各科住院人次
		Map<String, NameValueList> ipMap=visitsVarietyPayload.getIpMap();
		//各科出院人次
		Map<String, NameValueList> leaveMap=visitsVarietyPayload.getLeaveMap();
		
		try {
		// 建立新工作簿
		HSSFWorkbook workbook = new HSSFWorkbook();
	  
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		
		HSSFCellStyle cellStyle_left = workbook.createCellStyle();
		cellStyle_left.setAlignment(HorizontalAlignment.LEFT);
		cellStyle_left.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle_left.setBorderBottom(BorderStyle.THIN);
		cellStyle_left.setBorderLeft(BorderStyle.THIN);
		cellStyle_left.setBorderRight(BorderStyle.THIN);
		cellStyle_left.setBorderTop(BorderStyle.THIN);
		
		HSSFCellStyle cellStyle_noBorder = workbook.createCellStyle();
		cellStyle_noBorder.setAlignment(HorizontalAlignment.CENTER);
		cellStyle_noBorder.setVerticalAlignment(VerticalAlignment.CENTER);

		Font font = workbook.createFont();
		font.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
		
		/*新建工作表 門急診/住院/出院人次變化*/
		HSSFSheet visitsVarietySheet = workbook.createSheet("門急診-住院-出院人次變化");
		
		for(int i=0;i<14;i++){
			if(i==0 || i==1) {
				visitsVarietySheet.addMergedRegion(new CellRangeAddress(i,i,0,2));
				visitsVarietySheet.addMergedRegion(new CellRangeAddress(i,i,3,4));
			}
			else if(i==2) {
				visitsVarietySheet.addMergedRegion(new CellRangeAddress(i,i,0,2));
			}
			else if(i>3 && i<8) {
				visitsVarietySheet.addMergedRegion(new CellRangeAddress(i,i,0,2));
			}
			else if(i>8 && i<14) {
				visitsVarietySheet.addMergedRegion(new CellRangeAddress(i,i,0,2));
			}
			else if(i==3) {
				visitsVarietySheet.addMergedRegion(new CellRangeAddress(i,i,0,9));
			}
			else if(i==8) {
				visitsVarietySheet.addMergedRegion(new CellRangeAddress(i,i,0,10));
			}
		}
	
		HSSFRow rowA0 = visitsVarietySheet.createRow(0);
		HSSFRow rowA1 = visitsVarietySheet.createRow(1);
		HSSFRow rowA2 = visitsVarietySheet.createRow(2);
		HSSFRow rowA3 = visitsVarietySheet.createRow(3);
		HSSFRow rowA4 = visitsVarietySheet.createRow(4);
		HSSFRow rowA5 = visitsVarietySheet.createRow(5);
		HSSFRow rowA6 = visitsVarietySheet.createRow(6);
		HSSFRow rowA7 = visitsVarietySheet.createRow(7);
		HSSFRow rowA8 = visitsVarietySheet.createRow(8);
		HSSFRow rowA9 = visitsVarietySheet.createRow(9);
		HSSFRow rowA10 = visitsVarietySheet.createRow(10);
		HSSFRow rowA11 = visitsVarietySheet.createRow(11);
		HSSFRow rowA12 = visitsVarietySheet.createRow(12);
		HSSFRow rowA13 = visitsVarietySheet.createRow(13);
		HSSFRow rowA14 = visitsVarietySheet.createRow(14);
		
		String[] titleHeader_point= {"門急診/住院","門急診","門診(早)","門診(中)","門診(晚)","急診","住院"};
		String[] titleHeader_people= {"門急診/住院","門急診","門診(早)","門診(中)","門診(晚)","急診","住院","出院"};
		
		addRowCell(rowA0, 0, "就醫日期區間", cellStyle);
		for(int i=1;i<3;i++) {
			addRowCell(rowA0, i, "", cellStyle);
		}
		addRowCell(rowA0, 3, day.toString(), cellStyle);
		addRowCell(rowA0, 4, "", cellStyle);
		
		addRowCell(rowA1, 0, "人次趨勢統計截止時間", cellStyle);
		for(int i=1;i<3;i++) {
			addRowCell(rowA1, i, "", cellStyle);
		}
		addRowCell(rowA1, 3, yearWeek.toString(), cellStyle);
		addRowCell(rowA1, 4, "", cellStyle);
		
		
		addRowCell(rowA3, 0, "就醫日期區間總點數", cellStyle);
		for(int i=1;i<10;i++) {
			addRowCell(rowA3, i, "", cellStyle);
		}
		
		for(int i=0;i<3;i++) {
			addRowCell(rowA4, i, "", cellStyle);
		}
		for(int i=0;i<titleHeader_point.length;i++) {
			addRowCell(rowA4, i+3, titleHeader_point[i], cellStyle);
		}
		
		addRowCell(rowA5, 0, "病歷總點數(含自費)", cellStyle);
		for(int i=1;i<3;i++) {
			addRowCell(rowA5, i, "", cellStyle);
		}
		addRowCell(rowA5, 3, String.valueOf(all), cellStyle);
		addRowCell(rowA5, 4, String.valueOf(opem), cellStyle);
		addRowCell(rowA5, 5, String.valueOf(opMorning), cellStyle);
		addRowCell(rowA5, 6, String.valueOf(opAfternoon), cellStyle);
		addRowCell(rowA5, 7, String.valueOf(opNight), cellStyle);
		addRowCell(rowA5, 8, String.valueOf(em), cellStyle);
		addRowCell(rowA5, 9, String.valueOf(ip), cellStyle);
		
		addRowCell(rowA6, 0, "申報總點數", cellStyle);
		for(int i=1;i<3;i++) {
			addRowCell(rowA6, i, "", cellStyle);
		}
		addRowCell(rowA6, 3, String.valueOf(all_appl), cellStyle);
		addRowCell(rowA6, 4, String.valueOf(opem_appl), cellStyle);
		addRowCell(rowA6, 5, String.valueOf(opMorning_appl), cellStyle);
		addRowCell(rowA6, 6, String.valueOf(opAfternoon_appl), cellStyle);
		addRowCell(rowA6, 7, String.valueOf(opNight_appl), cellStyle);
		addRowCell(rowA6, 8, String.valueOf(em_appl), cellStyle);
		addRowCell(rowA6, 9, String.valueOf(ip_appl), cellStyle);
		
		addRowCell(rowA8, 0, "趨勢統計截止人次", cellStyle);
		for(int i=1;i<11;i++) {
			addRowCell(rowA8, i, "", cellStyle);
		}
		
		for(int i=1;i<3;i++) {
			addRowCell(rowA9, i, "", cellStyle);
		}
		for(int i=0;i<titleHeader_people.length;i++) {
			addRowCell(rowA9, i+3, titleHeader_people[i], cellStyle);
		}
		
		addRowCell(rowA10, 0, "總人次", cellStyle);
		for(int i=1;i<3;i++) {
			addRowCell(rowA10, i, "", cellStyle);
		}
		addRowCell(rowA10, 3, total_all.toString(), cellStyle);
		addRowCell(rowA10, 4, total_opem.toString(), cellStyle);
		addRowCell(rowA10, 5, total_opMorning.toString(), cellStyle);
		addRowCell(rowA10, 6, total_opAfternoon.toString(), cellStyle);
		addRowCell(rowA10, 7, total_opNight.toString(), cellStyle);
		addRowCell(rowA10, 8, total_em.toString(), cellStyle);
		addRowCell(rowA10, 9, total_ip.toString(), cellStyle);
		addRowCell(rowA10, 10, total_leave.toString(), cellStyle);
		
		addRowCell(rowA11, 0, "手術人次", cellStyle);
		for(int i=1;i<3;i++) {
			addRowCell(rowA11, i, "", cellStyle);
		}
		addRowCell(rowA11, 3, surgery_all.toString(), cellStyle);
		addRowCell(rowA11, 4, surgery_opem.toString(), cellStyle);
		addRowCell(rowA11, 5, surgery_opMorning.toString(), cellStyle);
		addRowCell(rowA11, 6, surgery_opAfternoon.toString(), cellStyle);
		addRowCell(rowA11, 7, surgery_opNight.toString(), cellStyle);
		addRowCell(rowA11, 8, surgery_em.toString(), cellStyle);
		addRowCell(rowA11, 9, surgery_ip.toString(), cellStyle);
		addRowCell(rowA11, 10, surgery_leave.toString(), cellStyle);
		
		addRowCell(rowA12, 0, "上月同區間總人次相比差額", cellStyle);
		for(int i=1;i<3;i++) {
			addRowCell(rowA12, i, "", cellStyle);
		}
		addRowCell(rowA12, 3, diff_all.toString(), cellStyle);
		addRowCell(rowA12, 4, diff_opem.toString(), cellStyle);
		addRowCell(rowA12, 5, diff_opMorning.toString(), cellStyle);
		addRowCell(rowA12, 6, diff_opAfternoon.toString(), cellStyle);
		addRowCell(rowA12, 7, diff_opNight.toString(), cellStyle);
		addRowCell(rowA12, 8, diff_em.toString(), cellStyle);
		addRowCell(rowA12, 9, diff_ip.toString(), cellStyle);
		addRowCell(rowA12, 10, diff_leave.toString(), cellStyle);
		
		addRowCell(rowA13, 0, "上月同區間總差額率", cellStyle);
		for(int i=1;i<3;i++) {
			addRowCell(rowA13, i, "", cellStyle);
		}
		addRowCell(rowA13, 3, percentAll.toString(), cellStyle);
		addRowCell(rowA13, 4,  percentOpem.toString(), cellStyle);
		addRowCell(rowA13, 5, percentOpMorning.toString(), cellStyle);
		addRowCell(rowA13, 6, percentOpAfternoon.toString(), cellStyle);
		addRowCell(rowA13, 7, percentOpNight.toString(), cellStyle);
		addRowCell(rowA13, 8, percentEm.toString(), cellStyle);
		addRowCell(rowA13, 9, percentIp.toString(), cellStyle);
		addRowCell(rowA13, 10, percentLeave.toString(), cellStyle);

		/*新建工作表 人次趨勢圖(全院)*/
		HSSFSheet allClassSheet = workbook.createSheet("人次趨勢圖(全院)");
		
		//各科門急診人數趨勢
		for (Entry<String, NameValueList> entry : opemMap.entrySet()) {
			if(entry.getKey().equals("不分科")) {
				allClassSheet.addMergedRegion(new CellRangeAddress(0,0,0,1));
				allClassSheet.addMergedRegion(new CellRangeAddress(1,1,0,1));
				allClassSheet.addMergedRegion(new CellRangeAddress(1,1,2,3));
				allClassSheet.addMergedRegion(new CellRangeAddress(1,1,4,5));
				
				HSSFRow row = allClassSheet.createRow(0);
				addRowCell(row,0,entry.getKey(), cellStyle_noBorder);
				
				HSSFRow row_head = allClassSheet.createRow(1);
				addRowCell(row_head,0,"門急診人數趨勢圖", cellStyle);
				addRowCell(row_head,1,"", cellStyle);
				addRowCell(row_head,2,"住院人數趨勢圖", cellStyle);
				addRowCell(row_head,3,"", cellStyle);
				addRowCell(row_head,4,"出院人數趨勢圖", cellStyle);
				addRowCell(row_head,5,"", cellStyle);
	
				HSSFRow row_head2 = allClassSheet.createRow(2);
				addRowCell(row_head2,0,"週數", cellStyle_left);
				addRowCell(row_head2,1,"人次", cellStyle_left);
				addRowCell(row_head2,2,"週數", cellStyle_left);
				addRowCell(row_head2,3,"人次", cellStyle_left);
				addRowCell(row_head2,4,"週數", cellStyle_left);
				addRowCell(row_head2,5,"人次", cellStyle_left);
				
				List<String> names=entry.getValue().getNames();
				List<Long> values=entry.getValue().getValues();
				
				for(int i=0;i<names.size();i++) {
					HSSFRow row_head3 = allClassSheet.createRow(i+3);
					addRowCell(row_head3,0,names.get(i), cellStyle_left);
					addRowCell(row_head3,1,values.get(i).toString(), cellStyle_left);
				}
			}
			
		}
		
		//各科住院人數趨勢
		for (Entry<String, NameValueList> entry : ipMap.entrySet()) {
			if(entry.getKey().equals("不分科")) {
				List<String> names=entry.getValue().getNames();
				List<Long> values=entry.getValue().getValues();
				for(int i=0;i<names.size();i++) {
					HSSFRow row_head3 = allClassSheet.getRow(i+3);
					addRowCell(row_head3,2,names.get(i), cellStyle_left);
					addRowCell(row_head3,3,values.get(i).toString(), cellStyle_left);
				}
			}
		}
		
		//各科出院人數趨勢
		for (Entry<String, NameValueList> entry : leaveMap.entrySet()) {
			if(entry.getKey().equals("不分科")) {
				List<String> names=entry.getValue().getNames();
				List<Long> values=entry.getValue().getValues();
				for(int i=0;i<names.size();i++) {
					HSSFRow row_head3 = allClassSheet.getRow(i+3);
					addRowCell(row_head3,4,names.get(i), cellStyle_left);
					addRowCell(row_head3,5,values.get(i).toString(), cellStyle_left);
				}
			}
		}
		
		/*新建工作表 人次趨勢圖(單一科別名)*/
		HSSFSheet singleClassSheet = workbook.createSheet("人次趨勢圖(單一科別名)");
		
		int index=0;
		int title=0;
		//各科門急診人數趨勢
		for (Entry<String, NameValueList> entry : opemMap.entrySet()) {
			if(index!=0) {
				title=title+entry.getValue().getValues().size()+4;
			}
			singleClassSheet.addMergedRegion(new CellRangeAddress(title,title,0,1));
			singleClassSheet.addMergedRegion(new CellRangeAddress(title+1,title+1,0,1));
			singleClassSheet.addMergedRegion(new CellRangeAddress(title+1,title+1,2,3));
			singleClassSheet.addMergedRegion(new CellRangeAddress(title+1,title+1,4,5));
			
			HSSFRow row = singleClassSheet.createRow(title);
			addRowCell(row,0,entry.getKey(), cellStyle_noBorder);
			
			HSSFRow row_head = singleClassSheet.createRow(title+1);
			addRowCell(row_head,0,"門急診人數趨勢圖", cellStyle);
			addRowCell(row_head,1,"", cellStyle);
			addRowCell(row_head,2,"住院人數趨勢圖", cellStyle);
			addRowCell(row_head,3,"", cellStyle);
			addRowCell(row_head,4,"出院人數趨勢圖", cellStyle);
			addRowCell(row_head,5,"", cellStyle);

			HSSFRow row_head2 = singleClassSheet.createRow(title+2);
			addRowCell(row_head2,0,"週數", cellStyle_left);
			addRowCell(row_head2,1,"人次", cellStyle_left);
			addRowCell(row_head2,2,"週數", cellStyle_left);
			addRowCell(row_head2,3,"人次", cellStyle_left);
			addRowCell(row_head2,4,"週數", cellStyle_left);
			addRowCell(row_head2,5,"人次", cellStyle_left);
			
			List<String> names=entry.getValue().getNames();
			List<Long> values=entry.getValue().getValues();
			
			for(int i=0;i<names.size();i++) {
				HSSFRow row_head3 = singleClassSheet.createRow(title+i+3);
				addRowCell(row_head3,0,names.get(i), cellStyle_left);
				addRowCell(row_head3,1,values.get(i).toString(), cellStyle_left);
			}
			
			index++;
		}
		
		
		index=0;
		title=0;
		//各科住院人數趨勢
		for (Entry<String, NameValueList> entry : ipMap.entrySet()) {
			List<String> names=entry.getValue().getNames();
			List<Long> values=entry.getValue().getValues();
			if(index!=0) {
				title=title+entry.getValue().getValues().size()+4;
			}
			for(int i=0;i<names.size();i++) {
				HSSFRow row_head3 = singleClassSheet.getRow(title+i+3);
				addRowCell(row_head3,2,names.get(i), cellStyle_left);
				addRowCell(row_head3,3,values.get(i).toString(), cellStyle_left);
			}
			
			index++;
		}
		
		index=0;
		title=0;
		//各科出院人數趨勢
		for (Entry<String, NameValueList> entry : leaveMap.entrySet()) {
			List<String> names=entry.getValue().getNames();
			List<Long> values=entry.getValue().getValues();
			if(index!=0) {
				title=title+entry.getValue().getValues().size()+4;
			}
			for(int i=0;i<names.size();i++) {
				HSSFRow row_head3 = singleClassSheet.getRow(title+i+3);
				addRowCell(row_head3,4,names.get(i), cellStyle_left);
				addRowCell(row_head3,5,values.get(i).toString(), cellStyle_left);
			}
			
			index++;
		}
		
	  //產生報表
		String fileNameStr = "門急診_住院_出院人次變化" + "_" + year+"_"+week;
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

		} catch (Exception e) {
			// TODO: handle exception
//			e.printStackTrace();
		}
	}
	
}
