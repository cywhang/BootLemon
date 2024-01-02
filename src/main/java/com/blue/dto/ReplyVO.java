package com.blue.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReplyVO {

	private int reply_Seq;
	private int post_Seq;
	private String member_Id;
	private String reply_Content;
	private Date reply_Date;
	private int reply_Like_Count;
	private String	reply_LikeYN;
	private String reply_WhenDid;
}
