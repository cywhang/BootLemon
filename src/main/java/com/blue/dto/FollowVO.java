package com.blue.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FollowVO {
	private String follower;
	private String following;
	
	private int	FollowingLocalPageFirstNum;
	private int	FollowingLocalPageLastNum;
	private int	FollowerLocalPageFirstNum;
	private int	FollowerLocalPageLastNum;
}
