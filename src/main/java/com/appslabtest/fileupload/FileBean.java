package com.appslabtest.fileupload;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@SessionScope
@Component
public class FileBean {
	
	private UploadedFile sourceFile;
	private UploadedFile targetFile;
	private String message;

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

	public void handleUploadedFile_Source(FileUploadEvent e) {
		
		if(sourceFile != null) {
			targetFile = e.getFile();
			
		}else {
			sourceFile = e.getFile();
		}
		
		if(sourceFile != null && targetFile != null) {
			setMessage("Files: " + sourceFile.getFileName() + " and " + targetFile.getFileName() + " were uploaded");
			PrimeFaces.current().executeScript("PF('confirmButton').jq.click();"); //Dynamically click a button on page
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Files: " + sourceFile.getFileName() + " and " + targetFile.getFileName() + " were uploaded", null);
			FacesContext ctx = FacesContext.getCurrentInstance();
			ctx.addMessage(null, message);
		}
		
	}
	
	public void processFiles() {
		System.out.println(getMessage());
		
	}

}
