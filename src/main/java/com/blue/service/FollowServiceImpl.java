package com.blue.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blue.mapper.FollowMapper;
import com.blue.dto.FollowVO;


@Service
public class FollowServiceImpl implements FollowService {

	@Autowired
	private FollowMapper followMapper;
	
	@Override
	public List<FollowVO> getFollowing(String member_Id) {
		return followMapper.getFollowing(member_Id);
	}

	@Override
	public List<FollowVO> getFollower(String member_Id) {
		return followMapper.getFollower(member_Id);
	}

	@Override
	public List<FollowVO> getMoreFollowing(FollowVO vo) {
		return followMapper.getMoreFollowing(vo);
	}

	@Override
	public List<FollowVO> getMoreFollower(FollowVO vo) {
		return followMapper.getMoreFollower(vo);
	}

}
