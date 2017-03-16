package com.kaywin.service;

import java.util.Date;
import java.util.List;

import com.kaywin.file.controller.model.CustomFile;
import com.kaywin.file.controller.model.CustomFileMetaData;

public interface FileService {

	
	 CustomFileMetaData save(CustomFile file);
	 
	 List<CustomFileMetaData> findFiles(String personName, Date date);
	 
	 byte[] getFile(String id);
	 
	 CustomFileMetaData getFileInfo(Long id);
}
