package com.kaywin.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kaywin.file.controller.model.CustomFileMetaData;

public interface FileRepository extends JpaRepository<CustomFileMetaData, Long> {

	@Query("select b from CustomFileMetaData b " + "where b.documentDate between ?1 and ?2")
	List<CustomFileMetaData> findByDatesBetween(Date start, Date end);

}
