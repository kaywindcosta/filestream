package com.kaywin.file.controller.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Properties;

public class CustomFile extends CustomFileMetaData implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7580680731359225755L;
	/**
	 * File information
	 */
	
	private byte[] fileData;
	
	 public CustomFile( byte[] fileData, String fileName, Date documentDate, String personName) {
	        super(fileName, documentDate, personName);
	        this.fileData = fileData;
	    }
	 
	 public CustomFile(Properties properties) {
	        super(properties);
	    }
	    
	    public CustomFile(CustomFileMetaData metadata) {
	        super(metadata.getUuid(), metadata.getFileName(), metadata.getDocumentDate(), metadata.getPersonName());
	    }

	    public byte[] getFileData() {
	        return fileData;
	    }
	    public void setFileData(byte[] fileData) {
	        this.fileData = fileData;
	    }
	    
	    public CustomFileMetaData getMetadata() {
	        return new CustomFileMetaData(getUuid(), getFileName(), getDocumentDate(), getPersonName());
	    }
	    
}
