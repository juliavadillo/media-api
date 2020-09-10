package com.medias.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medias.api.model.Media;

public interface MediaRepository extends JpaRepository<Media, Integer>{

}
