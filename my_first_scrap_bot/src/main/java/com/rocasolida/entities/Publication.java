package com.rocasolida.entities;

import java.util.Date;
import java.util.List;

import lombok.Data;

public @Data class Publication {
	private String owner;
	private Long timeStamp;
	private Date dateTime;
	private String titulo;
	private Integer cantShare;
	private Integer cantReproducciones;
	private List<Comment> comments;
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Publication [owner=").append(this.getOwner()).append(", timeStamp=").append(this.getTimeStamp())
				.append(", dateTime=").append(this.getDateTime()).append(", titulo=").append(this.getTitulo()).append(", cantShare=")
				.append(this.getCantShare()).append(", cantReproducciones=").append(this.getCantReproducciones());
		return builder.toString();
	}
	
	
}
