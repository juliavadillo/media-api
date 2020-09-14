package com.medias.api.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

@RestController
@Validated
@RequestMapping(value = "/medias")
public class MediaResource {

	@Autowired
	MediaRepository mediaRepository;

	AmazonClient amazonClient;

	@Autowired
	public MediaResource(AmazonClient amazonClient) {
		this.amazonClient = amazonClient;
	}

	@GetMapping
	public List<Media> listMedias(@RequestParam(value = "deleted") String deleted) {
		if (deleted.equals("false")) {
			return mediaRepository.findByDeleted(false);
		} else {
			return mediaRepository.findAll();
		}
	}

	@GetMapping("/{id}")
	public Optional<Media> listMediaById(@PathVariable(value = "id") Integer id) {
		return mediaRepository.findById(id);
	}

	@PostMapping
	public ResponseEntity<Media> saveMedia(@Valid @RequestBody MediaDTO mediaDto) {
		Media media = mediaDto.convertDTOtoMedia(mediaDto);
		String url = this.amazonClient.uploadMedia(media);
		media.setUrl(url);
		mediaRepository.save(media);
		return ResponseEntity.ok(media);
	}

	@DeleteMapping("/{id}")
	public void deleteMedia(@PathVariable(value = "id") Integer id) {
		Optional<Media> resultMedia = mediaRepository.findById(id);
		if (resultMedia.isPresent()) {
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

			String url = this.amazonClient.updateMediaFile(media, registeredFileName);
			media.setUrl(url);
			mediaRepository.save(media);
			return ResponseEntity.ok(media);
		} else {
			return ResponseEntity.badRequest().build();
		}
	}

}
