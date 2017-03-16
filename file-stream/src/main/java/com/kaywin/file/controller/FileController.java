package com.kaywin.file.controller;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kaywin.file.controller.model.CustomFile;
import com.kaywin.file.controller.model.CustomFileMetaData;
import com.kaywin.file.controller.model.Greeting;
import com.kaywin.service.FileService;

@RestController
public class FileController {

	private static final Logger LOG = Logger.getLogger(FileController.class);

	@Autowired
	FileService fileService;

	public FileService getFileService() {
		return fileService;
	}

	public void setFileService(FileService fileService) {
		this.fileService = fileService;
	}

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	/*
	 * Test service to greet
	 * 
	 * * Url: /greeting
	 */

	@RequestMapping("/greeting")
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}
	

	/**
	 * Adds a File.
	 * 
	 * Url: /upload?file={file}&person={person}&date={date} [POST]
	 * 
	 * @param file
	 *            A file posted in a multipart request
	 * @param person
	 *            The name of the uploading person
	 * @param date
	 *            The date of the file
	 * @return The meta data of the added document
	 */

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody CustomFileMetaData handleFileUpload(
			@RequestParam(value = "file", required = true) MultipartFile file,
			@RequestParam(value = "person", required = true) String person,
			@RequestParam(value = "date", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {

		try {
			CustomFile document = new CustomFile(file.getBytes(), file.getOriginalFilename(), date, person);
			getFileService().save(document);
			return document.getMetadata();
		} catch (RuntimeException e) {
			LOG.error("Error while uploading.", e);
			throw e;
		} catch (Exception e) {
			LOG.error("Error while uploading.", e);
			throw new RuntimeException(e);
		}
	}

	@RequestMapping(value = "/files", method = RequestMethod.GET)
	public HttpEntity<List<CustomFileMetaData>> findDocument(
			@RequestParam(value = "person", required = false) String person,
			@RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		HttpHeaders httpHeaders = new HttpHeaders();
		System.out.println("getting documents");
		return new ResponseEntity<List<CustomFileMetaData>>(getFileService().findFiles(person, date), httpHeaders,
				HttpStatus.OK);
	}

	/**
	 * Returns the file content downloaded with the given UUID.
	 * 
	 * Url: /file/{id} [GET]
	 * 
	 * @param id
	 *            The UUID of a File
	 * @return The file
	 */
	@RequestMapping(value = "/file/{id}", method = RequestMethod.GET)
	public HttpEntity<byte[]> getFileDownloadContent(@PathVariable String id) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.IMAGE_JPEG);
		return new ResponseEntity<byte[]>(getFileService().getFile(id), httpHeaders, HttpStatus.OK);
	}
	/**
	 * Returns the file Info with the given id.
	 * 
	 * Url: /fileInfo/{id} [GET]
	 * 
	 * @param id
	 *            The id of a File in db
	 * @return The file
	 */
	@RequestMapping(value = "/fileInfo/{id}", method = RequestMethod.GET)
	public HttpEntity<CustomFileMetaData> getFileInfoFromDb(@PathVariable Long id) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return new ResponseEntity<CustomFileMetaData>(getFileService().getFileInfo(id), httpHeaders,
				HttpStatus.OK);
	}
	
}
