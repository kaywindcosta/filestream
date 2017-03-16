package org.murygin.archive.client.test;

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

import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.kaywin.service.FileService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FileStreamApplication.class)
@WebAppConfiguration
@IntegrationTest
public class CustomFileTest {

    private static final Logger LOG = Logger.getLogger(CustomFileTest.class);
    
    private static final String TEST_FILE_DIR = "test-images";

    ApplicationContext applicationContext;
    FileService client;

    @Before
    public void setUp() throws IOException {
        client = new ArchiveServiceClient();
        testUpload();
    }

    @After
    public void tearDown() {
        deleteDirectory(new File(FileSystemDocumentDao.DIRECTORY));
    }

    @Test
    public void testFindDocuments() {
        List<DocumentMetadata> result = client.findDocuments(getPersonName(), null);
        assertNotNull("Result is null", result);
        assertTrue("Result is empty", !result.isEmpty());
        for (DocumentMetadata documentMetadata : result) {
            assertEquals("Person name is not : " + getPersonName(), getPersonName(), documentMetadata.getPersonName());
        }
    }

    @Test
    public void testUpload() throws IOException {
        List<String> fileList = getFileList();
        for (String fileName : fileList) {
            uploadFile(fileName);
        }
        testFindDocuments();
    }

    private void uploadFile(String fileName) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(TEST_FILE_DIR).append(File.separator).append(fileName);
        System.out.println(sb.toString());
        Path path = Paths.get(sb.toString());
        System.out.println(path.toString());
        byte[] fileData = Files.readAllBytes(path);
        Date today = Calendar.getInstance().getTime();
        String personName = getPersonName();        
        DocumentMetadata metadata = client.save(new Document(fileData, fileName, today, personName));
        if (LOG.isDebugEnabled()) {
            LOG.debug("Document saved, uuid: " + metadata.getUuid());
        }
    }

    private String getPersonName() {
        return this.getClass().getSimpleName();
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
}
