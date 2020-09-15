package com.medias.api.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import org.joda.time.LocalDate;

import com.medias.api.model.Media;

public class MediaDTO {
	@NotNull(message = "Name is mandatory")
	@Size(max=512, message="The max length for name attribute is 512")
	private String name;
	@PositiveOrZero
	private Integer duration;
	private Date uploadDate = new LocalDate().toDate();	
	
	public MediaDTO(String name, Integer duration) {
		this.name = name;
		this.duration = duration;
	}
	public MediaDTO() {
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	public Date getUploadDate() {
		return uploadDate;
	}
	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}
	public Media convertDTOtoMedia(MediaDTO mediaDto) {
		return new Media(mediaDto.getName(), mediaDto.getDuration(), mediaDto.uploadDate, false);
	}
	
	
}
