package com.blue.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QnaVO {

	private int qna_Seq;
	private String member_Id;
	private String qna_Title;
	private String qna_Message;
	private String qna_Answer;
	private String qna_Date;
	private String qna_Done;
}
