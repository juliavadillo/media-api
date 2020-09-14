package com.medias.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medias.api.model.Media;

public interface MediaRepository extends JpaRepository<Media, Integer>{

	List<Media> findByDeleted(boolean deleted);

}
