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

    void insertMember(MemberVO vo);

    String doLogin(MemberVO vo);

    void updateMember(MemberVO vo);

    void updateMember2(MemberVO vo);

    boolean checkPassword(String member_Id, String member_Password);

    void deleteMember(String member_Id);

    MemberVO loginUser(MemberVO vo);

    String searchId(MemberVO vo);

    MemberVO findPassword(MemberVO vo);

    void updatePassword(MemberVO vo);

    String PwdByIdNameEmail(MemberVO vo);

    List<MemberVO> searchMembers(String keyword);

    List<MemberVO> getRecommendMember(String member_Id);

    void changeFollow(FollowVO vo);

    List<HashMap<String, String>> memberProfile();

    List<MemberVO> getMostFamousMember();

    List<MemberVO> getFollowers(String member_Id);

    List<MemberVO> getFollowings(String member_Id);

    List<MemberVO> getAllMember();

    String checkFollow(FollowVO check_Vo);

    List<Integer> getMemberTendency();
}
