package vn.vmgmedia.youtobe.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.opencsv.CSVWriter;

import vn.vmgmedia.youtobe.model.InfoVideoUpload;


public class ExportDataUntil {

	private static final Logger logger = Logger.getLogger(ExportDataUntil.class);
	
	public JSONObject exportJson(String path, String type, Map<String, InfoVideoUpload> listVideo) {
		JSONObject jsonData = new JSONObject();
		String fileExport = path;
		try {
			jsonData.put("fileExport", fileExport);
			jsonData.append("type", type);
			for (Map.Entry<String, InfoVideoUpload> video : listVideo.entrySet()) {
				jsonData.append("data", new Gson().toJson(video.getValue()));
			}
			return jsonData;
			
		} catch (JSONException e) {
			logger.error("Write file error", e.getCause());
			return null;
		}
	}
	
	public String wirteFileCSV(String path, Map<String, InfoVideoUpload> listVideo) {
		File fileFolder = new File(path);
		
		System.out.println(fileFolder.exists());
		if (!fileFolder.exists()) {
			fileFolder.mkdirs();
		}
		File fileData = new File(fileFolder.getAbsolutePath().concat(path));
		System.out.println(fileData.getAbsolutePath());
		if (fileData.exists()) {
			return "exist file";
		} else {
			try {
				System.setProperty("file.encoding", "UTF-8");
				FileOutputStream fileStream = new FileOutputStream(fileData);
				OutputStreamWriter writerStream = new OutputStreamWriter(fileStream, "UTF-8");
				CSVWriter writer = new CSVWriter(writerStream);
				String [] header = {"Id", "Tên video", "�?ộ dài", "Kênh", "Playlist"};
				writer.writeNext(header);
				
				for (Map.Entry<String, InfoVideoUpload> video: listVideo.entrySet()) {
					String [] data = {String.valueOf(video.getValue().getId()), video.getValue().getNameVideo(), video.getValue().getTimeVideo(), video.getValue().getChanel(), video.getValue().getPlayList()};
					writer.writeNext(data);
				}
				writer.flush();
				writer.close();
				return fileData.getAbsolutePath();
			} catch (IOException e) {
				logger.error("Write csv file error: ", e.getCause());
				return "write erroe";
			}
		}
	}
	
	public String wirteFileExel(String folder, String fileName, Map<String, InfoVideoUpload> listVideo) {
		
		Workbook workbook = new XSSFWorkbook();
		 
		File fileFolder = new File(folder);
		
		if (!fileFolder.exists()) {
			fileFolder.mkdirs();
		}
		File fileData = new File(fileFolder.getAbsolutePath().concat(fileName));
		
		if (fileData.exists()) {
			return fileData.getAbsolutePath();
			
		} else {
			Sheet sheet = workbook.createSheet("VideoInfo");
			Font headerFont = workbook.createFont();
	        headerFont.setBold(true);
	        headerFont.setFontHeightInPoints((short) 14);
	        headerFont.setColor(IndexedColors.RED.getIndex());
	        Row headerRow = sheet.createRow(0);
	        CellStyle headerCellStyle = workbook.createCellStyle();
	        headerCellStyle.setFont(headerFont);
			
			String [] header = {"Id", "Tên video", "Độ dài", "Kênh", "Playlist", "Link"};
			
			 for(int i = 0; i < header.length; i++) {
		            Cell cell = headerRow.createCell(i);
		            cell.setCellValue(header[i]);
		            cell.setCellStyle(headerCellStyle);
		     }
			 
			int rowNum = 1;
			for (Map.Entry<String, InfoVideoUpload> video: listVideo.entrySet()) {
				Row row = sheet.createRow(rowNum++);
				video.getValue().setId(rowNum-1);
				row.createCell(0).setCellValue(rowNum-1);
				row.createCell(1).setCellValue(video.getValue().getNameVideo());
				row.createCell(2).setCellValue(video.getValue().getTimeVideo());
				row.createCell(3).setCellValue(video.getValue().getChanel());
				row.createCell(4).setCellValue(video.getValue().getPlayList());
				row.createCell(5).setCellValue(video.getValue().getLinkVideo());
			}
			
			for(int i = 0; i < header.length; i++) {
	            sheet.autoSizeColumn(i);
	        }
			
			try {
				FileOutputStream fileStream = new FileOutputStream(fileData);
				OutputStreamWriter writerStream = new OutputStreamWriter(fileStream, "UTF-8");
				workbook.write(fileStream);
				fileStream.close();
				writerStream.close();
				return fileData.getAbsolutePath();
			} catch (Exception e) {
				logger.error(e.getMessage(),  e.getCause());
				return "write file error";
			} 
		}
	}
	
	public String getCurrentDate () {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        return dateFormat.format(date).toString();
	}
	
}
