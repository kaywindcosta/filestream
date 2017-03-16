package com.kaywin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.kaywin.dao.impl.FileDaoImpl;
import com.kaywin.file.controller.model.CustomFile;
import com.kaywin.file.controller.model.CustomFileMetaData;
import com.kaywin.service.FileService;
import com.kaywin.service.impl.FileServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = FileStreamApplication.class)
public class FileStreamApplicationTests {

	private static final Logger LOG = Logger.getLogger(FileStreamApplicationTests.class);

	private static final String TEST_FILE_DIR = "test-images";

	ApplicationContext applicationContext;
	FileService fileService;

	@Before
	public void setUp() throws IOException {
		fileService = new FileServiceImpl();
		// testUpload();
	}

	@After
	public void tearDown() {
		deleteDirectory(new File(FileDaoImpl.DIRECTORY));
	}

	public static boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	@Test
	public void sampleTest() {

		LOG.info("Tests for File Stream Application");

	}

	@Test
	public void testUpload() throws IOException {
		List<String> fileList = getFileList();
		for (String fileName : fileList) {
			uploadFile(fileName);
		}
		testFindDocuments();
	}

	private List<String> getFileList() {
		File file = new File(TEST_FILE_DIR);
		String[] files = file.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isFile();
			}
		});
		return Arrays.asList(files);
	}

	private void uploadFile(String fileName) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(TEST_FILE_DIR).append(File.separator).append(fileName);
		Path path = Paths.get(sb.toString());
		byte[] fileData = Files.readAllBytes(path);
		Date today = Calendar.getInstance().getTime();
		String personName = getPersonName();
		CustomFileMetaData metadata = fileService.save(new CustomFile(fileData, fileName, today, personName));
		if (LOG.isDebugEnabled()) {
			LOG.debug("File saved, uuid: " + metadata.getUuid());
		}
	}

	@Test
	public void testFindDocuments() {
		List<CustomFileMetaData> result = fileService.findFiles(getPersonName(), null);
		assertNotNull("Result is null", result);
		assertTrue("Result is empty", !result.isEmpty());
		for (CustomFileMetaData documentMetadata : result) {
			assertEquals("Person name is not : " + getPersonName(), getPersonName(), documentMetadata.getPersonName());
		}
	}

	private String getPersonName() {
		return this.getClass().getSimpleName();
	}

}
