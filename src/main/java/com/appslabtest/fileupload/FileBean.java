//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.appslabtest.fileupload;

import com.appslabtest.readcsv.ProcessCSVFiles;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.stereotype.Component;

@ViewScoped
@Component
public class FileBean {
	private UploadedFile sourceFile;
	private UploadedFile targetFile;
	private UploadedFile iPOLYFile;
	private InputStream targetStream;
	private InputStream sourceStream;
	private String[] ipolyArr = null;
	private List<CSVRecord> iPolyRecords = new ArrayList();
	private List<CSVRecord> iPolyF11Records = new ArrayList();
	private boolean disableF11;
	private boolean disableF10;
	private ProcessCSVFiles instance = new ProcessCSVFiles();
	private String message;
	private String currentProcess;

	public FileBean() {
	}

	@PostConstruct
	public void init() {
		this.setDisableF10(true);
		this.setDisableF11(true);
	}

	public ProcessCSVFiles getInstance() {
		return this.instance;
	}

	public void setInstance(ProcessCSVFiles instance) {
		this.instance = instance;
	}

	public UploadedFile getSourceFile() {
		return this.sourceFile;
	}

	public void setSourceFile(UploadedFile sourceFile) {
		this.sourceFile = sourceFile;
	}

	public UploadedFile getTargetFile() {
		return this.targetFile;
	}

	public void setTargetFile(UploadedFile targetFile) {
		this.targetFile = targetFile;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public InputStream getTargetStream() {
		return this.targetStream;
	}

	public void setTargetStream(InputStream targetStream) {
		this.targetStream = targetStream;
	}

	public InputStream getSourceStream() {
		return this.sourceStream;
	}

	public void setSourceStream(InputStream sourceStream) {
		this.sourceStream = sourceStream;
	}

	public String getCurrentProcess() {
		return this.currentProcess;
	}

	public void setCurrentProcess(String currentProcess) {
		this.currentProcess = currentProcess;
	}

	public UploadedFile getiPOLYFile() {
		return this.iPOLYFile;
	}

	public void setiPOLYFile(UploadedFile iPOLYFile) {
		this.iPOLYFile = iPOLYFile;
	}

	public String[] getIpolyArr() {
		return this.ipolyArr;
	}

	public void setIpolyArr(String[] ipolyArr) {
		this.ipolyArr = ipolyArr;
	}

	public List<CSVRecord> getiPolyRecords() {
		return this.iPolyRecords;
	}

	public void setiPolyRecords(List<CSVRecord> iPolyRecords) {
		this.iPolyRecords = iPolyRecords;
	}

	public List<CSVRecord> getiPolyF11Records() {
		return this.iPolyF11Records;
	}

	public void setiPolyF11Records(List<CSVRecord> iPolyF11Records) {
		this.iPolyF11Records = iPolyF11Records;
	}

	public boolean isDisableF11() {
		return this.disableF11;
	}

	public void setDisableF11(boolean disableF11) {
		this.disableF11 = disableF11;
	}

	public boolean isDisableF10() {
		return this.disableF10;
	}

	public void setDisableF10(boolean disableF10) {
		this.disableF10 = disableF10;
	}

	public void handleUploadedFile_Source(FileUploadEvent e) throws IOException {
		if (this.sourceFile != null) {
			this.targetFile = e.getFile();
			this.targetStream = this.targetFile.getInputstream();
		} else {
			this.sourceFile = e.getFile();
			this.sourceStream = this.sourceFile.getInputstream();
		}

		if (this.sourceFile != null && this.targetFile != null) {
			this.setMessage("Files: " + this.sourceFile.getFileName() + " and " + this.targetFile.getFileName() + " were uploaded. \r\n Please click 'Yes' to proceed");
			PrimeFaces.current().executeScript("PF('confirmButton').jq.click();");
		}

	}

	public String processFiles() throws IOException {
		try {
			this.instance.setCurrentFile(this.sourceFile);
			List<CSVRecord> sourceEDW = this.instance.parseExcelCSV(this.getSourceStream());
			List<CSVRecord> targetODW = this.instance.parseExcelCSV(this.getTargetStream());
			this.removeHeader(sourceEDW);
			this.removeHeader(targetODW);
			this.instance.setEdw_Records(sourceEDW);
			this.instance.setOdw_Records(targetODW);
			if (sourceEDW.size() <= 400000 && targetODW.size() <= 400000) {
				PrimeFaces.current().executeScript("PF('sortRecords').jq.click();");
				this.clearRecords();
				this.instance.searchAndRemoveRecordsNotInOtherList(this.instance.getEdw_Records(), this.instance.getOdw_Records());
			} else {
				PrimeFaces.current().executeScript("PF('errorReport').jq.click();");
			}

			this.resetStreams();
		} catch (Exception var3) {
			var3.printStackTrace();
		}

		return "";
	}

	private void clearRecords() {
		this.instance.getRecords_In_EDW_And_In_ODW().clear();
		this.instance.getRecords_In_EDW_Not_In_ODW().clear();
		this.instance.getRecords_In_ODW_And_In_EDW().clear();
		this.instance.getRecords_In_ODW_Not_In_EDW().clear();
	}

	public void sortRecords() {
		this.instance.setEdw_Records(this.instance.sortData(this.instance.getEdw_Records()));
		this.instance.setOdw_Records(this.instance.sortData(this.instance.getOdw_Records()));
	}

	private void removeHeader(List<CSVRecord> records) {
		if (records.size() > 0) {
			CSVRecord header = (CSVRecord)records.get(0);
			if (header.get(0).equals("CO")) {
				records.remove(0);
			}
		}

	}

	public void resetStreams() {
		this.sourceFile = null;
		this.targetFile = null;
		this.setSourceStream((InputStream)null);
		this.setTargetStream((InputStream)null);
	}

	public String showMessage(String msg) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Simple validation tool", msg);
		PrimeFaces.current().dialog().showMessageDynamic(message);
		return "";
	}

	public void downloadRecord(List<CSVRecord> fileRecord, String filename) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		HttpServletResponse response = (HttpServletResponse)externalContext.getResponse();
		response.reset();
		response.setContentType("text/csv");
		response.setHeader("Content-disposition", "atachment; filename=\"" + filename + "\"");
		String NEW_LINE_SEPARATOR = "\n";
		OutputStreamWriter streamWriter = null;
		CSVPrinter csvFilePrinter = null;
		CSVFormat csvFileFormat = CSVFormat.EXCEL.withRecordSeparator("\n");

		try {
			OutputStream output = externalContext.getResponseOutputStream();
			streamWriter = new OutputStreamWriter(output);
			csvFilePrinter = new CSVPrinter(streamWriter, csvFileFormat);
			Iterator var11 = fileRecord.iterator();

			while(var11.hasNext()) {
				CSVRecord record = (CSVRecord)var11.next();
				csvFilePrinter.printRecord(record);
			}

			facesContext.responseComplete();
		} catch (Exception var21) {
			var21.printStackTrace();
		} finally {
			try {
				streamWriter.flush();
				streamWriter.close();
				csvFilePrinter.close();
			} catch (Exception var20) {
				System.out.println("Error while flushing/closing fileWriter/csvPrinter !!!");
				var20.printStackTrace();
			}

		}

	}

	public void handleUploadedIPOLYFile(FileUploadEvent e) throws Exception {
		this.iPOLYFile = e.getFile();
		System.out.println("File name: " + this.iPOLYFile.getFileName());
		String[] temp = this.convertInputStreamToArray(this.iPOLYFile.getInputstream());
		List iPolyArrAsList;
		List csvRecords;
		if (this.iPOLYFile.getFileName().toLowerCase(Locale.US).contains("IPolyF10".toLowerCase(Locale.US))) {
			this.ipolyArr = this.splitIPOLYF10(temp);
			iPolyArrAsList = this.convertArrayToList(this.ipolyArr);
			System.out.println("Size iPOLYF10: " + iPolyArrAsList.size());
			csvRecords = this.convert_List_Strings_To_List_CSVRecord(iPolyArrAsList);
			this.setiPolyRecords(csvRecords);
			this.setDisableF10(false);
			this.setDisableF11(true);
		} else if (this.iPOLYFile.getFileName().toLowerCase(Locale.US).contains("IPolyF11".toLowerCase(Locale.US))) {
			this.ipolyArr = this.splitIPOLYF11(temp);
			iPolyArrAsList = this.convertArrayToList(this.ipolyArr);
			System.out.println("Size iPOLYF11: " + iPolyArrAsList.size());
			csvRecords = this.convert_List_Strings_To_List_CSVRecord(iPolyArrAsList);
			this.setiPolyF11Records(csvRecords);
			System.out.println(this.iPolyF11Records.size());
			this.setDisableF10(true);
			this.setDisableF11(false);
		} else {
			System.out.println("I got here");
			PrimeFaces.current().executeScript("PF('errorReport').jq.click();");
		}

	}

	private String[] convertInputStreamToArray(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = "";
		ArrayList ipolyArr = new ArrayList();

		while((line = reader.readLine()) != null) {
			ipolyArr.add(line);
		}

		String header = (String)ipolyArr.get(0);
		if (header.contains("CO")) {
			ipolyArr.remove(0);
		}

		return (String[])ipolyArr.toArray(new String[0]);
	}

	private String[] splitIPOLYF10(String[] strArray) {
		Pattern pattern = Pattern.compile("\\d{50,}");

		for(int i = 0; i < strArray.length; ++i) {
			String val = strArray[i];
			Matcher matcher = pattern.matcher(val);
			if (matcher.find()) {
				String value = matcher.group(0).trim();
				String pmt_amt = value.substring(0, 12);
				pmt_amt = this.format_payment_amt_Field(pmt_amt);
				String guar_dur = value.substring(12, 17);
				String pmt_mode = value.substring(17, 22);
				String pmt_nbr = value.substring(22, 26);
				String pmt_start_date = value.substring(26, 34);
				String pmt_stop_date = value.substring(34, 42);
				String post_retire_death_ben = value.substring(42, 51);
				String var_units = value.substring(51);
				String newValue = " " + pmt_amt + "  " + guar_dur + "  " + pmt_mode + "  " + pmt_nbr + "  " + pmt_start_date + "  " + pmt_stop_date + "  " + post_retire_death_ben + "  " + var_units + " ";
				String returnVal = val.replaceFirst(value, newValue);
				strArray[i] = returnVal;
			}
		}

		return strArray;
	}

	private String format_payment_amt_Field(String pymt) {
		Double newVal = (double)Integer.parseInt(pymt) / 100.0D;
		return newVal.toString();
	}

	private String[] splitIPOLYF11(String[] strArray) {
		Pattern pattern = Pattern.compile("\\d{50,}");

		for(int i = 0; i < strArray.length; ++i) {
			String val = strArray[i];
			Matcher matcher = pattern.matcher(val);
			if (matcher.find()) {
				System.out.println("Length group_0 " + matcher.group(0).length());
				String value = matcher.group(0).trim();
				String pmt_amt = value.substring(0, 12);
				pmt_amt = this.format_payment_amt_Field(pmt_amt);
				String guar_dur = value.substring(12, 17);
				String LI_CTG_PCT = value.substring(17, 29);
				String L2_CTG_PCT = value.substring(29, 38);
				String N_CERTAIN = value.substring(38, 42);
				String PMT_MODE = value.substring(42, 44);
				String pmt_nbr = value.substring(44, 48);
				String pmt_start_date = value.substring(48, 56);
				String pmt_stop_date = value.substring(56, 64);
				String pmt_TYPE = value.substring(64, 65);
				String post_retire_death_ben = value.substring(65);
				String newValue = " " + pmt_amt + "  " + guar_dur + "  " + LI_CTG_PCT + "  " + L2_CTG_PCT + "  " + N_CERTAIN + "  " + PMT_MODE + "  " + pmt_nbr + "  " + pmt_start_date + "  " + pmt_stop_date + "  " + pmt_TYPE + "  " + post_retire_death_ben + " ";
				String returnVal = val.replaceFirst(value, newValue);
				strArray[i] = returnVal;
			}
		}

		return strArray;
	}

	private List<String> convertArrayToList(String[] temp) {
		return Arrays.asList(temp);
	}

	private List<CSVRecord> convert_List_Strings_To_List_CSVRecord(List<String> records) throws Exception {
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(System.lineSeparator());
		List<CSVRecord> csvrecords = new ArrayList();
		Iterator var4 = records.iterator();

		while(var4.hasNext()) {
			String record = (String)var4.next();
			List<CSVRecord> csvrecord = CSVParser.parse(record, csvFileFormat).getRecords();
			csvrecords.add(csvrecord.get(0));
		}

		return csvrecords;
	}

	public void reset() {
		this.setiPolyF11Records((List)null);
		this.setiPolyRecords((List)null);
	}
}
