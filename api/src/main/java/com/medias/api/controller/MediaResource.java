package com.medias.api.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.medias.api.dto.MediaDTO;
import com.medias.api.model.Media;
import com.medias.api.repository.MediaRepository;
import com.medias.api.service.AmazonClient;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
@CrossOrigin(origins = "*")
@RestController
@Validated
@RequestMapping(value = "/medias")
@Api(value="Medias API REST")
public class MediaResource {

	@Autowired
	MediaRepository mediaRepository;

	AmazonClient amazonClient;

	@Autowired
	public MediaResource(AmazonClient amazonClient) {
		this.amazonClient = amazonClient;
	}
	
	@ApiOperation(value="Retorna uma lista de Medias registradas, se deleted=true, retorna as que ja foram deletadas, se deleted=false retorna somente as que não foram deletadas")
	@GetMapping
	public List<Media> listMedias(@RequestParam(value = "deleted") String deleted) {
		if (deleted.equals("false")) {
			return mediaRepository.findByDeleted(false);
		} else {
			return mediaRepository.findAll();
		}
	}
	
	@ApiOperation(value="Retorna uma media referente ao id filtrado")
	@GetMapping("/{id}")
	public Optional<Media> listMediaById(@PathVariable(value = "id") Integer id) {
		return mediaRepository.findById(id);
	}

	@ApiOperation(value="Insere uma nova media")
	@PostMapping
	public ResponseEntity<Media> saveMedia(@Valid @RequestBody MediaDTO mediaDto) {
		Media media = mediaDto.convertDTOtoMedia(mediaDto);
		media.setUrl(this.amazonClient.createURL(media));
		mediaRepository.save(media);
		this.amazonClient.uploadMedia(media);
		return ResponseEntity.ok(media);
	}

	@DeleteMapping("/{id}")
	public void deleteMedia(@PathVariable(value = "id") Integer id) {
		Optional<Media> resultMedia = mediaRepository.findById(id);
		if (resultMedia.isPresent() && !resultMedia.get().getDeleted()) {
			Media media = resultMedia.get();
			this.amazonClient.deleteFile(media.getName());
			media.setDeleted(true);
			media.setUrl("");
			mediaRepository.save(media);
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<Media> updateMedia(@PathVariable(value = "id") Integer id, @RequestBody MediaDTO mediaDto) {
		Optional<Media> resultMedia = mediaRepository.findById(id);
		if (resultMedia.isPresent()) {
			Media media = resultMedia.get();
			String registeredFileName = media.getName();
			media.setName(mediaDto.getName());
			media.setDuration(mediaDto.getDuration());
			media.setUploadDate(mediaDto.getUploadDate());
			media.setUrl(this.amazonClient.createURL(media));
			mediaRepository.save(media);
			this.amazonClient.updateMediaFile(media, registeredFileName);
			return ResponseEntity.ok(media);
		} else {
			return ResponseEntity.badRequest().build();
		}
	}

}
