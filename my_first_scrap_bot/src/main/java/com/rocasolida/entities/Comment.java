package com.rocasolida.entities;

import java.util.List;

import lombok.Data;

public @Data class Comment {
	private Long userId;
	private String mensaje;
	private Long uTime;
	private List<Reply> replies;
}