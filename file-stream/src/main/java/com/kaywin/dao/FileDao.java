package com.kaywin.dao;

import java.util.Date;
import java.util.List;

import com.kaywin.file.controller.model.CustomFile;
import com.kaywin.file.controller.model.CustomFileMetaData;

public interface FileDao {

	 void insert(CustomFile file);
	 
	 List<CustomFileMetaData> findByPersonNameDate(String personName, Date date);
	 
	 CustomFile load(String uuid);
	 
	 CustomFileMetaData loadFileInfo(Long id);
	 
	 
}
