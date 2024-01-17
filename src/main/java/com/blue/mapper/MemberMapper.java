package com.blue.mapper;

import com.blue.dto.FollowVO;
import com.blue.dto.MemberVO;
import com.blue.service.AlarmService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface MemberMapper {

    MemberVO getMember(String member_Id);

    MemberVO getMemberInfo(String member_Id);

    String confirmID(String member_Id);

    void updatePassword(MemberVO vo);

    void insertMember(MemberVO vo);

    void memberUpdate(MemberVO vo);

    void memberUpdate2(MemberVO vo);

    String searchId(MemberVO vo);

    String PwdByIdNameEmail(MemberVO vo);

    void memberDelete(String member_Id);

    List<MemberVO> getRecommendMember(String member_Id);

    String checkFollow(FollowVO check_Vo);

    void addFollow(FollowVO vo);

    void delFollow(FollowVO vo);

    List<HashMap<String, String>> memberProfile();

    List<MemberVO> MostFamous();

    List<MemberVO> getFollowers(String member_Id);

    List<MemberVO> getFollowings(String member_Id);

    List<MemberVO> getAllMember();

    List<MemberVO> searchMembers(String keyword);

    List<Integer> getMemberTendency();

    String checkSocial(String id, String type);

    int checkSeq();

    void insertSocial(String id, String userid, String type);
}
