package com.rocasolida.entities;

import lombok.Data;

public @Data class Reply extends Comment{
	private Long userId;
	private String mensaje;
	private Long uTime;
}
