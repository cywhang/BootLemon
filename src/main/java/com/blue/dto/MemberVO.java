package com.blue.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberVO {

	private String member_Id;
	private String member_Name;
	private String member_Email;
	private String member_Password;
	private String member_Phone;
	private Date   member_Join_Date;
	private String member_Birthday;
	private String member_Gender;
	private String member_Profile_Image;
	private String member_Country;
	private String member_Mbti;
	private String member_Follow_Count;
	private String member_Following_Count;	
	private int bothFollow;
	private String member_Join_Date_String;
	private String follow_Check;
}
