package com.kaywin;

import java.util.Date;

import javax.servlet.MultipartConfigElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.kaywin.file.controller.model.CustomFileMetaData;
import com.kaywin.repository.FileRepository;


@SpringBootApplication
@EnableScheduling
@Import( MailConfiguration.class)
public class FileStreamApplication extends SpringBootServletInitializer implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(FileStreamApplication.class, args);
	}
	
	  @Bean
	    public MultipartConfigElement multipartConfigElement() {
		  //multipart-file max size set to 5mb
			MultipartConfigFactory factory = new MultipartConfigFactory();
	        factory.setMaxFileSize("5120KB");
	        factory.setMaxRequestSize("5120KB");
	        return factory.createMultipartConfig();
	    }
	  
	  @Autowired
	  FileRepository repository;

	@Override
	public void run(String... arg0) throws Exception {
		// Test for data insert to database
		/*
		repository.save(new CustomFileMetaData("AA001", new Date(), "file1"));
		repository.save(new CustomFileMetaData("AA002", new Date(), "file2"));
		repository.save(new CustomFileMetaData("AA003", new Date(), "file3"));
		
		
		for (CustomFileMetaData fi : repository.findAll()) {
			System.out.println(fi.getId());
			System.out.print("  "+fi.getUuid());
			System.out.print("  "+fi.getFileName());			
			System.out.print("  "+fi.getPersonName());
			System.out.print("  "+fi.getDocumentDate());
			System.out.print("  "+fi.getFileName());
			
		}
		*/
	}
}
