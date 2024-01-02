package com.blue.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LikeVO {

	private String member_Id;
	private int    post_Seq;
	private int    reply_Seq;
}
