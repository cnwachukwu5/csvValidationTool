package com.appslabtest.readcsv;

import java.util.Map;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.primefaces.model.UploadedFile;


public class ProcessCSVFiles {
	
	private List<CSVRecord> edw_Records = new ArrayList<>(); //Source
	private List<CSVRecord> odw_Records = new ArrayList<>(); //Target
	private List<CSVRecord> records_In_EDW_Not_In_ODW = new ArrayList<>();//List of records in EDW but not in ODW
	private List<CSVRecord> records_In_ODW_Not_In_EDW = new ArrayList<>();//List of records in ODW but not in EDW
	private List<CSVRecord> records_In_ODW_And_In_EDW = new ArrayList<>();//List of records in ODW and in EDW - Print and pass to beyond compare
	private List<CSVRecord> records_In_EDW_And_In_ODW = new ArrayList<>();//List of records in EDW and in EDW - Print and pass to beyond compare
	private UploadedFile currentFile;
	
	Hashtable<Integer, String> recordFinder = new Hashtable<Integer, String>(); 
	
	public UploadedFile getCurrentFile() {
		return currentFile;
	}

	public void setCurrentFile(UploadedFile currentFile) {
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

	public List<CSVRecord> getRecords_In_EDW_Not_In_ODW() {
		return records_In_EDW_Not_In_ODW;
	}

	public void setRecords_In_EDW_Not_In_ODW(List<CSVRecord> records_In_EDW_Not_In_ODW) {
		this.records_In_EDW_Not_In_ODW = records_In_EDW_Not_In_ODW;
	}

	public List<CSVRecord> getRecords_In_ODW_Not_In_EDW() {
		return records_In_ODW_Not_In_EDW;
	}

	public void setRecords_In_ODW_Not_In_EDW(List<CSVRecord> records_In_ODW_Not_In_EDW) {
		this.records_In_ODW_Not_In_EDW = records_In_ODW_Not_In_EDW;
	}

	public List<CSVRecord> getRecords_In_ODW_And_In_EDW() {
		return records_In_ODW_And_In_EDW;
	}

	public void setRecords_In_ODW_And_In_EDW(List<CSVRecord> records_In_ODW_And_In_EDW) {
		this.records_In_ODW_And_In_EDW = records_In_ODW_And_In_EDW;
	}

	public List<CSVRecord> getRecords_In_EDW_And_In_ODW() {
		return records_In_EDW_And_In_ODW;
	}

	public void setRecords_In_EDW_And_In_ODW(List<CSVRecord> records_In_EDW_And_In_ODW) {
		this.records_In_EDW_And_In_ODW = records_In_EDW_And_In_ODW;
	}

	public Hashtable<Integer, String> getRecordFinder() {
		return recordFinder;
	}

	public void setRecordFinder(Hashtable<Integer, String> recordFinder) {
		this.recordFinder = recordFinder;
	}

	private CSVParser getCSVParser(InputStream inputStream) {
		/**
		 * Takes uploaded csv file as inputstream and returns a CSVParser instance. 
		 * The parser contains all the records (rows) in the excel csv file.
		 * @Param excel csv records as inputstream
		 * @Return CSVParser
		 */
		
		CSVParser parser = null;
		try {
			parser = new CSVParser(new InputStreamReader(inputStream), CSVFormat.EXCEL.withSkipHeaderRecord(false));
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return parser;
	}
	
	public List<CSVRecord> parseExcelCSV(InputStream inputStream){
		/**
		 * Converts CSVParser instance and returns a list of all the rows in the stream
		 * @Param inputStream - the uploaded file as stream to convert to List of CSVRecords
		 * @Return rows - List of rows in the inputstream
		 */
		
		List<CSVRecord> rows = new ArrayList<>();
		CSVParser parser = getCSVParser(inputStream);
		
		for(CSVRecord record : parser) {
			rows.add(record);
		}
		
		return rows;
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
	
	public List<CSVRecord> sortData(List<CSVRecord> records) {
		//Sort the records
		
		CSVRecord[] recordsArr = convertListAndReturnArray(records);
		return Arrays.asList(mergeSort(recordsArr));
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
					records_In_ODW_And_In_EDW.add(odw_Records.get(i));
					//recordFinder.remove(i);
				}else {
					records_In_ODW_Not_In_EDW.add(odw_Records.get(i));
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
					records_In_EDW_And_In_ODW.add(edw_Records.get(i));
					//recordFinder.remove(i);
				}else {
					records_In_EDW_Not_In_ODW.add(edw_Records.get(i));
					//recordFinder.remove(i);
				}
			}			
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
		
		if(currentFile.getFileName().contains("ALF00") || currentFile.getFileName().contains("GAF00")) {
			rowValues = record.get(0) + record.get(1).trim(); //+ record.get(5).trim() + record.get(13).trim() + record.get(14).trim() + 
					//record.get(15).trim();
		}else if(currentFile.getFileName().contains("ALF02") || currentFile.getFileName().contains("GAF02")) {
			rowValues = record.get(0) + record.get(1).trim() + record.get(8).trim() + record.get(14).trim() + record.get(15).trim();
		}else if(currentFile.getFileName().contains("GAF08") || currentFile.getFileName().contains("ALF08")) {
			rowValues = record.get(0) + record.get(1).trim() + record.get(6).trim() + record.get(7).trim();
		}else if(currentFile.getFileName().contains("GAF09") || currentFile.getFileName().contains("ALF09")) {
			rowValues = record.get(0) + record.get(1).trim() + record.get(8).trim() + record.get(9).trim();
		}else if(currentFile.getFileName().contains("ALF24") || currentFile.getFileName().contains("GAF24")) {
			rowValues = record.get(0) + record.get(1).trim() + record.get(5).trim() + record.get(6).trim() + record.get(7).trim();
		}else if(currentFile.getFileName().contains("FIA") || currentFile.getFileName().contains("EIA")) {
			rowValues = record.get(0) + record.get(1).trim();
		}else {
			rowValues = record.get(0) + record.get(2).trim();//IPOLYFiles
		}
		
		return rowValues;
	}
	


}
