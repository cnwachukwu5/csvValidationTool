package com.appslabtest.fileupload;


import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import org.primefaces.PrimeFaces;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.appslabtest.readcsv.ProcessCSVFiles;

@SessionScope
@Component
public class FileBean {
	
	private UploadedFile sourceFile;
	private UploadedFile targetFile;
	private InputStream targetStream;
	private InputStream sourceStream;
	private ProcessCSVFiles instance = new ProcessCSVFiles();
	
	private String message;
	private String currentProcess;
	
	
	public ProcessCSVFiles getInstance() {
		return instance;
	}


	public void setInstance(ProcessCSVFiles instance) {
		this.instance = instance;
	}

	public UploadedFile getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(UploadedFile sourceFile) {
		this.sourceFile = sourceFile;
	}
	
	public UploadedFile getTargetFile() {
		return targetFile;
	}

	public void setTargetFile(UploadedFile targetFile) {
		this.targetFile = targetFile;
	}	

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public InputStream getTargetStream() {
		return targetStream;
	}

	public void setTargetStream(InputStream targetStream) {
		this.targetStream = targetStream;
	}

	public InputStream getSourceStream() {
		return sourceStream;
	}

	public void setSourceStream(InputStream sourceStream) {
		this.sourceStream = sourceStream;
	}
	
	public String getCurrentProcess() {
		return currentProcess;
	}

	public void setCurrentProcess(String currentProcess) {
		this.currentProcess = currentProcess;
	}

	public void handleUploadedFile_Source(FileUploadEvent e) throws IOException {
		
		if(sourceFile != null) {
			targetFile = e.getFile();
			targetStream = targetFile.getInputstream();
			
		}else {
			sourceFile = e.getFile();
			sourceStream = sourceFile.getInputstream();
		}
		
		if(sourceFile != null && targetFile != null) {
			setMessage("Files: " + sourceFile.getFileName() + " and " + targetFile.getFileName() + " were uploaded. \r\n Please click 'Yes' to proceed");
			PrimeFaces.current().executeScript("PF('confirmButton').jq.click();"); //Dynamically click a button on page
//			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Files: " + sourceFile.getFileName() + " and " + targetFile.getFileName() + " were uploaded", null);
//			FacesContext ctx = FacesContext.getCurrentInstance();
//			ctx.addMessage(null, message);
		}
		
	}
	
	public void processFiles() throws IOException {
		
		try {
			instance.setCurrentFile(sourceFile);
			List<CSVRecord> sourceEDW = instance.parseExcelCSV(getSourceStream());
			List<CSVRecord> targetODW = instance.parseExcelCSV(getTargetStream());
			
			//Remove headers in records
			removeHeader(sourceEDW);
			removeHeader(targetODW);
			
			instance.setEdw_Records(sourceEDW);
			instance.setOdw_Records(targetODW);
			
			
			//Check number of records in files
			if(sourceEDW.size() > 400000 || targetODW.size() > 400000) {
				PrimeFaces.current().executeScript("PF('errorReport').jq.click();");
				
			}else {
				PrimeFaces.current().executeScript("PF('sortRecords').jq.click();");
				clearRecords();
				instance.searchAndRemoveRecordsNotInOtherList(instance.getEdw_Records(), instance.getOdw_Records());
			}
			
			
			resetStreams();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void clearRecords() {
		instance.getRecords_In_EDW_And_In_ODW().clear();
		instance.getRecords_In_EDW_Not_In_ODW().clear();
		instance.getRecords_In_ODW_And_In_EDW().clear();
		instance.getRecords_In_ODW_Not_In_EDW().clear();
	}
	
	public void sortRecords() {
		//Sort the records
		instance.setEdw_Records(instance.sortData(instance.getEdw_Records()));
		
		instance.setOdw_Records(instance.sortData(instance.getOdw_Records()));
	}
	
	private void removeHeader(List<CSVRecord> records) {
		//Check for header in file and remove if exists
		if(records.size() > 0) {
			CSVRecord header = records.get(0);
			if(header.get(0).equals("CO")) {
				records.remove(0);
			}
		}
		
	}
	
	public void resetStreams() {
		sourceFile = null;
		targetFile = null;
		setSourceStream(null);
		setTargetStream(null);
		
	}
	
	public String showMessage() {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Simple validation tool", "Files too large");
		PrimeFaces.current().dialog().showMessageDynamic(message);
		return "";
	}
	
	public void downloadRecord(List<CSVRecord> fileRecord, String filename) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
		response.reset();
		response.setContentType("text/csv");
		response.setHeader("Content-disposition", "atachment; filename=\"" + filename +"\"");
		
		final String NEW_LINE_SEPARATOR = "\n";
		
		OutputStreamWriter streamWriter = null;
		CSVPrinter csvFilePrinter = null;
		
		CSVFormat csvFileFormat = CSVFormat.EXCEL.withRecordSeparator(NEW_LINE_SEPARATOR);
		
		try {
			OutputStream output = externalContext.getResponseOutputStream();
			streamWriter = new OutputStreamWriter(output);
			csvFilePrinter = new CSVPrinter(streamWriter, csvFileFormat);
			
			for(CSVRecord record : fileRecord) {
				csvFilePrinter.printRecord(record);
			}
			
			facesContext.responseComplete();
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				streamWriter.flush();
				streamWriter.close();
				csvFilePrinter.close();
			}catch(Exception e) {
				System.out.println("Error while flushing/closing fileWriter/csvPrinter !!!");
				e.printStackTrace();
			}
		}
		
	}

}
