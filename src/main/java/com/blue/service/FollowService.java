package com.blue.service;

import java.util.List;

import com.blue.dto.FollowVO;

public interface FollowService {
	
	List<FollowVO> getFollowing(String member_Id);
	
	List<FollowVO> getFollower(String member_Id);
	
	List<FollowVO> getMoreFollowing(FollowVO vo);
	
	List<FollowVO> getMoreFollower(FollowVO vo);
}
