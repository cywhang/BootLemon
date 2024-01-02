package com.blue.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostVO {

	private int 	post_Seq;
	private String  member_Id;
	private String  post_Content;
	private String 	post_Date;
	private String  post_Public;
	private int 	post_Image_Count;
	private int 	post_Like_Count;
	private int 	post_Reply_Count;
	private String  post_Hashtag;
	private String	post_LikeYN;
	private String  session_Id;
}
