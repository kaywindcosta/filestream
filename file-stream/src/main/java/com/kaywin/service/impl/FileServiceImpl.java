package com.kaywin.service.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kaywin.dao.FileDao;
import com.kaywin.dao.impl.FileDaoImpl;
import com.kaywin.file.controller.model.CustomFile;
import com.kaywin.file.controller.model.CustomFileMetaData;
import com.kaywin.service.FileService;

@Service("FileService")
public class FileServiceImpl implements FileService, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3204349849518522318L;

	private static final Logger LOG = Logger.getLogger(FileServiceImpl.class);

	@Autowired
	private FileDao fileDao;
	
	public  FileServiceImpl(){
		//for running test cases not mocking real tests, need to remove before boot run
		fileDao=new FileDaoImpl();
	}

	public FileDao getFileDao() {
		return fileDao;
	}

	public void setFileDao(FileDao fileDao) {
		this.fileDao = fileDao;
	}

	@Override
	public CustomFileMetaData save(CustomFile file) {
		//System.out.println(file.getFileName());
		getFileDao().insert(file);
		return file.getMetadata();
	}

	@Override
	public List<CustomFileMetaData> findFiles(String personName, Date date) {
		return getFileDao().findByPersonNameDate(personName, date);
	}

	@Override
	public byte[] getFile(String id) {
		CustomFile file = getFileDao().load(id);
		if (file != null) {
			return file.getFileData();
		} else {
			return null;
		}
	}

	@Override
	public CustomFileMetaData getFileInfo(Long id) {
		return fileDao.loadFileInfo(id);
	}

}
