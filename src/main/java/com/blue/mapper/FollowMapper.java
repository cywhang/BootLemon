package com.blue.mapper;

import com.blue.dto.FollowVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface FollowMapper {

    List<FollowVO> getFollowing(String member_Id);
    List<FollowVO> getFollower(String member_Id);
    List<FollowVO> getMoreFollowing(FollowVO vo);
    List<FollowVO> getMoreFollower(FollowVO vo);
}
