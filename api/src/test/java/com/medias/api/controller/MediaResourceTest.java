package com.medias.api.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medias.api.dto.MediaDTO;
import com.medias.api.model.Media;
import com.medias.api.repository.MediaRepository;
import com.medias.api.service.AmazonClient;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
class MediaResourceTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper mapper;

	@MockBean
	MediaRepository mediaRepository;
	
	@MockBean
	AmazonClient amazonClient;

	private Media media1;

	private Media media2;

	private String endpointUrl = "https://register-medias.s3-sa-east-1.amazonaws.com";


	@BeforeAll
	void setup() throws Exception {
		media1 = new Media(1, "video1", "www.video1", 120, new Date(), false);
		media2 = new Media(2, "video2", "www.file2", 60, new Date(), true);
		
	}

	@Test
	void whenListMediaWithTrueDeletedParameterThenShouldReturnAllMediaRegistered() throws Exception {
		Mockito.when(mediaRepository.findAll()).thenReturn(Arrays.asList(media1, media2));
		mockMvc.perform(get("/medias?deleted=true")).andExpect(status().isOk()).andExpect(jsonPath("$[*]", hasSize(2)))
				.andExpect(jsonPath("$[0].id", is(1))).andExpect(jsonPath("$[0].name", is("video1"))).andExpect(jsonPath("$[0].deleted", is(false)))
				.andExpect(jsonPath("$[1].id", is(2))).andExpect(jsonPath("$[1].name", is("video2"))).andExpect(jsonPath("$[1].deleted", is(true)));

	}
	
	@Test
	void whenListMediaWithFalseDeletedParameterThenShouldReturnAllNonDeletedMediaRegistered() throws Exception {
		Mockito.when(mediaRepository.findByDeleted(false)).thenReturn(Arrays.asList(media1));
		mockMvc.perform(get("/medias?deleted=false")).andExpect(status().isOk()).andExpect(jsonPath("$[*]", hasSize(1)))
				.andExpect(jsonPath("$[0].id", is(1))).andExpect(jsonPath("$[0].name", is("video1")))
				.andExpect(jsonPath("$[0].deleted", is(false)));

	}
	
	@Test
	void whenPostMediaWithNameAttributeThenShouldReturnOk() throws Exception {
		MediaDTO mediaDTO = new MediaDTO("fileName",55);
		Media media = mediaDTO.convertDTOtoMedia(mediaDTO);
		String url = endpointUrl+ "/" + mediaDTO.getName()+".txt";
		Mockito.when(amazonClient.createURL(media)).thenReturn(url);

		mockMvc.perform(post("/medias").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(media))).andExpect(status().isOk());
	}

	@Test
	void whenPostMediaWithoutNameAttributeThenShouldReturnBadRequest() throws Exception {
		MediaDTO mediaDTO = new MediaDTO();
		mockMvc.perform(post("/medias").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(mediaDTO))).andExpect(status().isBadRequest());
	}

	


}
