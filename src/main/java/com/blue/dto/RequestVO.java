package com.blue.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RequestVO {
	private int followerTotalPageNum;
	private int followerPageNum;
	private int followingTotalPageNum;
	private int followingPageNum;
	private String member_Id;
}
