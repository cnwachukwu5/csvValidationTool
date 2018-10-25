package com.appslabtest.readcsv;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class ReadAndProcessCSVFile {
	
	private List<CSVRecord> edw_Records = new ArrayList<>();
	private List<CSVRecord> odw_Records = new ArrayList<>();
	private List<CSVRecord> records_In_EWD_Not_In_ODW = new ArrayList<>();//List of records in EDW but not in ODW
	private List<CSVRecord> records_In_OWD_Not_In_EDW = new ArrayList<>();//List of records in ODW but not in EDW
	private List<CSVRecord> records_In_OWD_And_In_EDW = new ArrayList<>();//List of records in ODW and in EDW - Print and pass to beyond compare
	private List<CSVRecord> records_In_EWD_And_In_ODW = new ArrayList<>();//List of records in EDW and in EDW - Print and pass to beyond compare
	
	Hashtable<Integer, String> recordFinder = new Hashtable<Integer, String>(); 
	
	private File currentFile;
	
	
	public File getCurrentFile() {
		return currentFile;
	}



	public void setCurrentFile(File currentFile) {
		this.currentFile = currentFile;
	}



	public List<CSVRecord> getEdw_Records() {
		return edw_Records;
	}



	public void setEdw_Records(List<CSVRecord> edw_Records) {
		this.edw_Records = edw_Records;
	}



	public List<CSVRecord> getOdw_Records() {
		return odw_Records;
	}



	public void setOdw_Records(List<CSVRecord> odw_Records) {
		this.odw_Records = odw_Records;
	}



	public List<CSVRecord> getRecords_In_EWD_Not_In_ODW() {
		return records_In_EWD_Not_In_ODW;
	}



	public void setRecords_In_EWD_Not_In_ODW(List<CSVRecord> records_In_EWD_Not_In_ODW) {
		this.records_In_EWD_Not_In_ODW = records_In_EWD_Not_In_ODW;
	}



	public List<CSVRecord> getRecords_In_OWD_Not_In_EDW() {
		return records_In_OWD_Not_In_EDW;
	}



	public void setRecords_In_OWD_Not_In_EDW(List<CSVRecord> records_In_OWD_Not_In_EDW) {
		this.records_In_OWD_Not_In_EDW = records_In_OWD_Not_In_EDW;
	}



	public List<CSVRecord> getRecords_In_OWD_And_In_EDW() {
		return records_In_OWD_And_In_EDW;
	}



	public void setRecords_In_OWD_And_In_EDW(List<CSVRecord> records_In_OWD_And_In_EDW) {
		this.records_In_OWD_And_In_EDW = records_In_OWD_And_In_EDW;
	}



	public List<CSVRecord> getRecords_In_EWD_And_In_ODW() {
		return records_In_EWD_And_In_ODW;
	}



	public void setRecords_In_EWD_And_In_ODW(List<CSVRecord> records_In_EWD_And_In_ODW) {
		this.records_In_EWD_And_In_ODW = records_In_EWD_And_In_ODW;
	}



	public List<CSVRecord> parseExcelCSV(File file){
		/**
		 * Reads parses a csv file and returns a list of all the rows in the file
		 * @Param file - the csv file to read
		 * @Return rows - List of rows in the csv file
		 */
		List<CSVRecord> rows = new ArrayList<>();
				
			CSVParser parser = getCSVParser(file);
			for(CSVRecord record : parser) {
				rows.add(record);
			}
			
		return rows;
	}
	
	
	
	public int getNumberOfRows(File file) {
		/**
		 * This method returns the number of rows in the file
		 * @Param File object to check the number of rows
		 * @Return number of rows
		 */
		int counter = 0;
		try {
			CSVParser parser  = getCSVParser(file);
			for(CSVRecord record : parser) {
				counter++;
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return counter;
	}
	
	public CSVParser getCSVParser(File file) {
		/**
		 * Takes a excel csv file and returns a CSVParser instance. 
		 * The parser contains all the records (rows) in the excel file.
		 * It skips the header if header exists in the file.
		 * @Param excel csv file
		 * @Return CSVParser
		 */
		
		CSVFormat csvFormat = null;
		CSVParser parser = null;
		
		try {
			FileReader reader = new FileReader(file);
			if(!(file.getName().contains("CONV"))) {
				csvFormat = CSVFormat.EXCEL.withSkipHeaderRecord(false);
			}else {
				csvFormat = CSVFormat.EXCEL.withSkipHeaderRecord(false);
				//System.out.println("Header not skipped skipped");
			}
			
			parser = new CSVParser(reader, csvFormat);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return parser;
	}
	
	public void populate_ODW_EDW_Records(File file) {
		/**
		 * Add records to the edw_Records list for EDW file.
		 * To avoid 'Out of memory or Stack overflow error' 
		 * check the number of records before adding to the list
		 * @Param file (odw or edw)
		 */
		long NumberOfRecords = getNumberOfRows(file);
		
		if(NumberOfRecords < 400000) {
			if(file.getName().contains("CONV")) {
				setOdw_Records(parseExcelCSV(file));
			}else {
				setEdw_Records(parseExcelCSV(file));
			}
		}else {
			System.out.println("The file is too large");
		}
	}
	
	public int searchRecord(CSVRecord record) {
		/**
		 * Search and return specified record if exists, otherwise return -1
		 * Using Hash table because the record is large and the record objects do not implement comparable
		 * @Param - records: list of all records in the csv file, record: a record stand to search for in the List of records
		 * @Return - index of the record in the collection or -1 if it does not exist
		 */
		//Create a concatenated string of the all row values in the search record
		String searchValue = concateRecord(record);
		
		Integer key = -1;
		
		for(Map.Entry entry:recordFinder.entrySet()) {
			if(searchValue.equals(entry.getValue())) {
				key = (Integer)entry.getKey();
				System.out.println("Key: " + key);
				break;
			}
		}

		return key;
	}
	
	public String concateRecord(CSVRecord record) {
		/**
		 * Takes record representing a row in the csv file
		 * concatenate the values at cell 0 and cell 1 to create a unique record in the hashtable
		 * @Param record - a row in a csv file
		 * @Return rowValues - a String representing concatenation of first and second cell values in the row
		 */
		
		String rowValues = "";
		
		if(currentFile.getName().contains("ALF00") || currentFile.getName().contains("GAF00")) {
			rowValues = record.get(0) + record.get(1).trim(); //+ record.get(5).trim() + record.get(13).trim() + record.get(14).trim() + 
					//record.get(15).trim();
		}else if(currentFile.getName().contains("ALF02") || currentFile.getName().contains("GAF02")) {
			rowValues = record.get(0) + record.get(1).trim() + record.get(8).trim() + record.get(14).trim() + record.get(15).trim();
		}else if(currentFile.getName().contains("GAF08") || currentFile.getName().contains("ALF08")) {
			rowValues = record.get(0) + record.get(1).trim() + record.get(6).trim() + record.get(7).trim();
		}else if(currentFile.getName().contains("GAF09") || currentFile.getName().contains("ALF09")) {
			rowValues = record.get(0) + record.get(1).trim() + record.get(8).trim() + record.get(9).trim();
		}else if(currentFile.getName().contains("ALF24") || currentFile.getName().contains("GAF24")) {
			rowValues = record.get(0) + record.get(1).trim() + record.get(5).trim() + record.get(6).trim() + record.get(7).trim();
		}else if(currentFile.getName().contains("FIA") || currentFile.getName().contains("EIA")) {
			rowValues = record.get(0) + record.get(1).trim();
		}else {
			rowValues = record.get(0) + record.get(2).trim();
		}
		
		return rowValues;
	}
	
	public void searchAndRemoveRecordsNotInOtherList(List<CSVRecord> edw_Records, List<CSVRecord> odw_Records) {
		/**
		 * Check each records in edw_records, check it exists in ODW, 
		 * similarly for each record in odw_records, check that it exists in EDW.
		 * Move the records to the respective lists
		 * @Param edw_Records, odw_Records - List of all records in edw file and odw file
		 * @Return null
		 */
		
		System.out.println("Started executing searchAndRemoveRecordsNotInOtherList ...");
		if(!(edw_Records.isEmpty()) && !(odw_Records.isEmpty())) {
			
			System.out.println("The lists are not empty ...");
			
			for(int i = 0; i < edw_Records.size(); i++) {
				recordFinder.put(i, concateRecord(edw_Records.get(i)));
			}
			
			System.out.println(recordFinder.get(0));
			
			System.out.println("HashTable is populated ...");
			
			for(int i = 0; i < odw_Records.size(); i++) {
				int indexInEdwRecord = searchRecord(odw_Records.get(i));				
				if(indexInEdwRecord >= 0) {
					records_In_OWD_And_In_EDW.add(odw_Records.get(i));
					//recordFinder.remove(i);
				}else {
					records_In_OWD_Not_In_EDW.add(odw_Records.get(i));
					//recordFinder.remove(i);
				}
			}
			
			System.out.println("Completed ODW List ...");
			
			System.out.println("Started EDW List ...");
			
			recordFinder.clear();
			
			for(int i = 0; i < odw_Records.size(); i++) {			
				recordFinder.put(i, concateRecord(odw_Records.get(i)));
			}
			
			System.out.println(recordFinder.get(0));
			System.out.println("HashTable is populated ...");
			
			for(int i = 0; i < edw_Records.size(); i++) {
				int indexInOdwRecord = searchRecord(edw_Records.get(i));
				if(indexInOdwRecord >= 0) {
					records_In_EWD_And_In_ODW.add(edw_Records.get(i));
					//recordFinder.remove(i);
				}else {
					records_In_EWD_Not_In_ODW.add(edw_Records.get(i));
					//recordFinder.remove(i);
				}
			}			
		}
	}
	
	
	public CSVRecord[] convertListAndReturnArray(List<CSVRecord> records) {
		
		//Convert the list object to an array
		CSVRecord[] recordsArray = new CSVRecord[records.size()];
		recordsArray = records.toArray(recordsArray);
		return recordsArray;
	}
	
	public CSVRecord[] mergeSort(CSVRecord[] records) {
		
		/**
		 * Takes an array of CSVRecords and returns a sorted array using merge sort.
		 * @Param records: array to sort
		 * @Return sorted array
		 */
		//Base case
		if(records.length < 2)
			return records;
		
		//Get the midIndex of the array
		int midIndex = records.length / 2;
		
		CSVRecord[] left = Arrays.copyOfRange(records, 0, midIndex);
		CSVRecord[] right = Arrays.copyOfRange(records, midIndex, records.length);
		
		CSVRecord[] sortedLeft = mergeSort(left);
		CSVRecord[] sortedRight = mergeSort(right);
		
		int currentSortedLeftIndex = 0;
		int currentSortedRightIndex = 0;
		
		CSVRecord[] sortedArray = new CSVRecord[records.length];
		
		for(int currentIndex = 0; currentIndex < records.length; currentIndex++) {
			if(currentSortedLeftIndex < sortedLeft.length && (currentSortedRightIndex >= sortedRight.length 
					|| sortedLeft[currentSortedLeftIndex].get(1).compareTo(sortedRight[currentSortedRightIndex].get(1)) < 0)) {
				sortedArray[currentIndex] = sortedLeft[currentSortedLeftIndex];
				currentSortedLeftIndex++;
			}else {
				sortedArray[currentIndex] = sortedRight[currentSortedRightIndex];
				currentSortedRightIndex++;
			}
		}
		
		return sortedArray;
	}
	
	public void writeCSVRecordToFile(File filename, CSVRecord[] theRecord, Object[] fileHeader) {
		/**Create a csv file with the filename specified and write the contents of the CSVRecord array to the csv file
		 * @Param filename: used to create a file
		 * @Param theRecord: CSVRecord array to write to the file
		 * @Param fileHeader: column headers
		 * @Return void
		 */
		
		final String NEW_LINE_SEPARATOR = "\n";
		
		FileWriter fileWriter = null;
		CSVPrinter csvFilePrinter = null;
		
		CSVFormat csvFileFormat = CSVFormat.EXCEL.withRecordSeparator(NEW_LINE_SEPARATOR);
		
		try {
			fileWriter = new FileWriter(filename);
			csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
			
			if(fileHeader != null)
				csvFilePrinter.printRecord(fileHeader);
			
			for(CSVRecord record : theRecord) {
				csvFilePrinter.printRecord(record);
			}
		}catch(Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		}finally {
			try {
				fileWriter.flush();
				fileWriter.close();
				csvFilePrinter.close();
			}catch(Exception e) {
				System.out.println("Error while flushing/closing fileWriter/csvPrinter !!!");
				e.printStackTrace();
			}
		}		
	}
	
	public void writeAllRecords(File file, String validatingMonth) {
		String PATH = System.getProperty("user.home");
		String directiroryName = PATH.concat("/" + validatingMonth);
		String filename = file.getName();
		
		File directory = new File(directiroryName);
		
		if(! directory.exists()) {
			directory.mkdir();
		}
		
		Object[] fileHeader = fileHeader(file.getName());
		
		//Writing Records_In_OWD_Not_In_EDW to CSV file
		if(! getRecords_In_OWD_Not_In_EDW().isEmpty()) {
			File records_In_OWD_Not_In_EDW = new File(directiroryName + "/" + filename.concat("_records_In_ODW_Not_In_EDW.csv"));
			CSVRecord[]  records_In_OWD_Not_In_EDW_In_ArrayForm = convertListAndReturnArray(getRecords_In_OWD_Not_In_EDW());
			records_In_OWD_Not_In_EDW_In_ArrayForm = mergeSort(records_In_OWD_Not_In_EDW_In_ArrayForm);
			writeCSVRecordToFile(records_In_OWD_Not_In_EDW, records_In_OWD_Not_In_EDW_In_ArrayForm, fileHeader);
		}
		
		if(! getRecords_In_EWD_And_In_ODW().isEmpty()) {
			File records_In_EWD_And_In_ODW = new File(directiroryName + "/" + filename.concat("_records_In_EDW_And_In_ODW.csv"));
			CSVRecord[]  records_In_EWD_And_In_ODW_In_ArrayForm = convertListAndReturnArray(getRecords_In_EWD_And_In_ODW());
			records_In_EWD_And_In_ODW_In_ArrayForm = mergeSort(records_In_EWD_And_In_ODW_In_ArrayForm);
			writeCSVRecordToFile(records_In_EWD_And_In_ODW, records_In_EWD_And_In_ODW_In_ArrayForm, fileHeader);
		}
		
		if(! getRecords_In_EWD_Not_In_ODW().isEmpty()) {
			File records_In_EWD_Not_In_ODW = new File(directiroryName + "/" + filename.concat("_records_In_EDW_Not_In_ODW.csv"));
			CSVRecord[]  records_In_EWD_Not_In_ODW_In_ArrayForm = convertListAndReturnArray(getRecords_In_EWD_Not_In_ODW());
			records_In_EWD_Not_In_ODW_In_ArrayForm = mergeSort(records_In_EWD_Not_In_ODW_In_ArrayForm);
			writeCSVRecordToFile(records_In_EWD_Not_In_ODW, records_In_EWD_Not_In_ODW_In_ArrayForm, fileHeader);
		}
		
		if(! getRecords_In_OWD_And_In_EDW().isEmpty()) {
			File records_In_OWD_And_In_EDW = new File(directiroryName + "/" + filename.concat("_records_In_ODW_And_In_EDW.csv"));
			CSVRecord[]  records_In_OWD_And_In_EDW_In_ArrayForm = convertListAndReturnArray(getRecords_In_OWD_And_In_EDW());
			records_In_OWD_And_In_EDW_In_ArrayForm = mergeSort(records_In_OWD_And_In_EDW_In_ArrayForm);
			writeCSVRecordToFile(records_In_OWD_And_In_EDW, records_In_OWD_And_In_EDW_In_ArrayForm, fileHeader);
		}
	}
	
	private Object[] fileHeader(String filename) {
		
		
		if(filename.contains("ALF00")) {
			Object[] ALF00_fileHeader = {"CO","POLICYNO","RECTYP","ACTYR","ACTMO","ADMINCODE","RT","ISAGE","SEX","CLS","DBO","ISST","RST","ISDY","ISYR", 
					"ISMO","WAGENCY","AGENTNO","ST","MODE","QUALIFIED","TARPRMANN","PREMST","CDYR","CDMO","CDFREEDAY","CDTERM","RLOB","PLOB","SIINTCODE",
					"GUARRATE1","GUARRATE2","GUARRTYR1","GUARRTMO1","GUARRTYR2","GUARRTMO2","ORIGISDD","ORIGISYR","ORIGISMO","ORGCO","ORGPOLNO",
					"ORGADMIN","ORGPRJPLN","SOP051IND","SOP5CHGYR","SOP5CHGMO"};
			return ALF00_fileHeader;
		}else if(filename.contains("ALF02")) {
			Object[] ALF02_fileHeader = {"CO","POLICYNO","RECTYP","ACTYR","ACTMO","PRMCOL1YR","PRMCOLREN","LOADCHRG","INTEREST","VARAPPREC","SIPBCRED",
					"PBACCR1","ACTDBMONV","NARMONV","FVCURRME","DUALFV","BONUSFUND","DBFLOOR","LMPSUM1YR","LMPSUMREN","MINDB1PREM","MINDB2PREM","MINDBYR",
					"MINDBMO","MINDB2YR","MINDB2MO","PYMTSAVAIL","MINDBSW","MINDB2SW","ROPACCRUAL","CVCURRME","ACQBONUS","MVACHG","FREEPART"};
			return ALF02_fileHeader;
		}else if(filename.contains("ALF08")) {
			Object[] ALF08_fileHeader = {"CO","POLICYNO","RECTYP","ACTYR","ACTMO","WITHTYPE","CASHPAY","FUNDREL","SURCHGREL","MVACHG"};
			return ALF08_fileHeader;
		}else if(filename.contains("ALF09")) {
			Object[] ALF09_fileHeader = {"CO","POLICYNO","RECTYP","ACTYR","ACTMO","TERMYR","TERMMO","TERMDAY","CASHPAY","FUNDREL","SURCHGREL","DEATHPAY","TERMTYPE","REINST",
					"MVACHG"};
			return ALF09_fileHeader;
		}else if(filename.contains("ALF24")) {
			Object[] ALF24_fileHeader = {"CO","POLICYNO","RECTYP","ACTYR","ACTMO","DEPEFFYR","DEPEFFMO","DEPEFFDAY","DEPLDGYR","DEPLDGMO","DEPLDGDAY",
					"DEPAMOUNT","PREMIND"};
			return ALF24_fileHeader;
		}else if(filename.contains("GAF06")) {
			Object[] GAF06_fileHeader = {"CO","POLICYNO","RECTYP","ACTYR","ACTMO","TARGETTYPE","FND","FNDTYP","GCEASEYR","GCEASEMO","GCEASEDAY",
					"FUNDID","GUARPERCNT","AMOUNT","DEPOSIT","DEPOSITYR","DEPOSITMO","DEPOSITDAY","FNDHIGHPCT","FNDHIGHAMT","SURRCHGITM"};
			return GAF06_fileHeader;
		}else {
			return null;
		}
	}
	
	
}//End of class

