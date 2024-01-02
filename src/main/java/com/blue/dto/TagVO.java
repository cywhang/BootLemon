package com.blue.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TagVO {

	private int    tag_Seq; 
	private int    post_Seq;
	private String tag_Content;
	private double tag_Count;
	private String session_Id;
}
