package com.blue.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AlarmVO {

	private int alarm_Seq;
	private int kind;
	private String from_Mem;
	private String to_Mem;
	private int post_Seq;
	private int reply_Seq;
	private String message;
	
}