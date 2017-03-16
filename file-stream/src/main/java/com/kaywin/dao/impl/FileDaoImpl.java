package com.kaywin.dao.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kaywin.dao.FileDao;
import com.kaywin.file.controller.model.CustomFile;
import com.kaywin.file.controller.model.CustomFileMetaData;
import com.kaywin.repository.FileRepository;

@Service("FileDao")
public class FileDaoImpl implements FileDao, Serializable {

	/**
	 * Data Access layer file-system/repository/database
	 */
	private static final long serialVersionUID = -3016353638006189822L;
	private static final Logger LOG = Logger.getLogger(FileDaoImpl.class);
	public static final String DIRECTORY = "FILESLOCATION";
	public static final String META_DATA_FILE_NAME = "metadata.properties";

	@Autowired
	private FileRepository repository;

	public FileDaoImpl() {
		//for running test cases not mocking real tests, need to remove before boot run
			repository = new FileRepository() {

			@Override
			public <S extends CustomFileMetaData> S findOne(Example<S> arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends CustomFileMetaData> Page<S> findAll(Example<S> arg0, Pageable arg1) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends CustomFileMetaData> boolean exists(Example<S> arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public <S extends CustomFileMetaData> long count(Example<S> arg0) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public <S extends CustomFileMetaData> S save(S arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public CustomFileMetaData findOne(Long arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean exists(Long arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void deleteAll() {
				// TODO Auto-generated method stub

			}

			@Override
			public void delete(Iterable<? extends CustomFileMetaData> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void delete(CustomFileMetaData arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void delete(Long arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public long count() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Page<CustomFileMetaData> findAll(Pageable arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends CustomFileMetaData> S saveAndFlush(S arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends CustomFileMetaData> List<S> save(Iterable<S> arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public CustomFileMetaData getOne(Long arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void flush() {
				// TODO Auto-generated method stub

			}

			@Override
			public <S extends CustomFileMetaData> List<S> findAll(Example<S> arg0, Sort arg1) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends CustomFileMetaData> List<S> findAll(Example<S> arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<CustomFileMetaData> findAll(Iterable<Long> arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<CustomFileMetaData> findAll(Sort arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<CustomFileMetaData> findAll() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void deleteInBatch(Iterable<CustomFileMetaData> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void deleteAllInBatch() {
				// TODO Auto-generated method stub

			}

			@Override
			public List<CustomFileMetaData> findByDatesBetween(Date start, Date end) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	@Override
	@Transactional
	public void insert(CustomFile file) {
		try {
			createDirectory(file);
			saveFileData(file);
			saveMetaData(file);

			SaveCustomFileMetadataDb(file);

		} catch (IOException e) {
			String message = "Error while inserting File";
			LOG.error(message, e);
			throw new RuntimeException(message, e);
		}

	}

	private void SaveCustomFileMetadataDb(CustomFile file) {
		System.out.println(file.getMetadata().getFileName());
		System.out.print(file.getMetadata().getUuid());
		System.out.print(file.getMetadata().getDocumentDate());
		System.out.print(file.getMetadata().getPersonName());
		CustomFileMetaData metaData = new CustomFileMetaData(file.getMetadata().getUuid(),
				file.getMetadata().getFileName(), new Date(), file.getMetadata().getPersonName());
		repository.save(metaData);

	}

	private String createDirectory(CustomFile file) {
		String path = getDirectoryPath(file);
		createDirectory(path);
		return path;
	}

	private String getDirectoryPath(CustomFile document) {
		return getDirectoryPath(document.getUuid());
	}

	private String getDirectoryPath(String uuid) {
		StringBuilder sb = new StringBuilder();
		sb.append(DIRECTORY).append(File.separator).append(uuid);
		String path = sb.toString();
		return path;
	}

	private void createDirectory(String path) {
		File file = new File(path);
		file.mkdirs();
	}

	private void saveFileData(CustomFile file) throws IOException {
		String path = getDirectoryPath(file);
		BufferedOutputStream stream = new BufferedOutputStream(
				new FileOutputStream(new File(new File(path), file.getFileName())));
		stream.write(file.getFileData());
		stream.close();
	}

	public void saveMetaData(CustomFile file) throws IOException {
		String path = getDirectoryPath(file);
		Properties props = file.createProperties();
		File f = new File(new File(path), META_DATA_FILE_NAME);
		OutputStream out = new FileOutputStream(f);
		props.store(out, "Custom File meta data");
	}

	@Override
	public List<CustomFileMetaData> findByPersonNameDate(String personName, Date date) {
		try {
			return findInFileSystem(personName, date);
		} catch (IOException e) {
			String message = "Error while finding file, person name: " + personName + ", date:" + date;
			LOG.error(message, e);
			throw new RuntimeException(message, e);
		}
	}

	private List<CustomFileMetaData> findInFileSystem(String personName, Date date) throws IOException {
		List<String> uuidList = getUuidList();
		List<CustomFileMetaData> metadataList = new ArrayList<CustomFileMetaData>(uuidList.size());
		for (String uuid : uuidList) {
			CustomFileMetaData metadata = loadMetadataFromFileSystem(uuid);
			if (isMatched(metadata, personName, date)) {
				metadataList.add(metadata);
			}
		}
		return metadataList;
	}

	private List<String> getUuidList() {
		File file = new File(DIRECTORY);
		String[] directories = file.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		return Arrays.asList(directories);
	}

	private CustomFileMetaData loadMetadataFromFileSystem(String uuid) throws IOException {
		CustomFileMetaData document = null;
		String dirPath = getDirectoryPath(uuid);
		File file = new File(dirPath);
		if (file.exists()) {
			Properties properties = readProperties(uuid);
			document = new CustomFileMetaData(properties);

		}
		return document;
	}

	private boolean isMatched(CustomFileMetaData metadata, String personName, Date date) {
		if (metadata == null) {
			return false;
		}
		boolean match = true;
		if (personName != null) {
			match = (personName.equals(metadata.getPersonName()));
		}
		if (match && date != null) {
			match = (date.equals(metadata.getDocumentDate()));
		}
		return match;
	}

	private Properties readProperties(String uuid) throws IOException {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(new File(getDirectoryPath(uuid), META_DATA_FILE_NAME));
			prop.load(input);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return prop;
	}

	@Override
	public CustomFile load(String uuid) {
		try {
			return loadFromFileSystem(uuid);
		} catch (IOException e) {
			String message = "Error while loading File with id: " + uuid;
			LOG.error(message, e);
			throw new RuntimeException(message, e);
		}
	}

	private CustomFile loadFromFileSystem(String uuid) throws IOException {
		CustomFileMetaData metadata = loadMetadataFromFileSystem(uuid);
		if (metadata == null) {
			return null;
		}
		Path path = Paths.get(getFilePath(metadata));
		CustomFile document = new CustomFile(metadata);
		document.setFileData(Files.readAllBytes(path));
		return document;
	}

	private String getFilePath(CustomFileMetaData metadata) {
		String dirPath = getDirectoryPath(metadata.getUuid());
		StringBuilder sb = new StringBuilder();
		sb.append(dirPath).append(File.separator).append(metadata.getFileName());
		return sb.toString();
	}

	@Override
	public CustomFileMetaData loadFileInfo(Long id) {
		return repository.findOne(id);
	}

}
