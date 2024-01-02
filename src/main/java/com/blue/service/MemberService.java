package com.blue.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.web.bind.annotation.SessionAttributes;

import com.blue.dto.FollowVO;
import com.blue.dto.MemberVO;

@SessionAttributes("loginUser")
public interface MemberService {

	// 회원 ID를 조건으로 회원 조회
	MemberVO getMember(String member_Id);

	// 회원 ID를 조건으로 회원 조회(정보만)
	MemberVO getMemberInfo(String member_Id);

	// 회원 존재 여부 조회
	int confirmID(String member_Id);

	// 회원 Insert
	void insertMember(MemberVO vo);

	// 로그인 처리
	int doLogin(MemberVO vo);

	// 회원정보 수정
	void updateMember(MemberVO vo);

	// 회원정보 수정2
	void updateMember2(MemberVO vo);

	// 아이디 중복체크
	boolean checkDuplicate(String member_Id);

	// 회원 탈퇴
	void deleteMember(String member_Id);

	// 아이디 찾기
	String searchId(MemberVO vo);

	// 패스워드 찾기
	MemberVO findPassword(MemberVO vo);

	// 패스워드 변경
	void updatePassword(MemberVO vo);

	// 패스워드 체크
	boolean checkPassword(String member_Id, String member_Password);

	// 아이디 이메일 확인
	String selectPwdByIdNameEmail(MemberVO vo);

	// 회원검색
	List<MemberVO> searchMembers(String keyword);

	// 나를 팔로우 했는데 나는 팔로우 안한 사람 추천
	List<MemberVO> getRecommendMember(String member_Id);

	// 팔로우 / 언팔로우 처리
	void changeFollow(FollowVO vo);

	// 전체 회원의 프로필 이미지 조회
	HashMap<String, String> getMemberProfile();

	// 팔로워가 가장 많은 멤버 조회
	List<MemberVO> getMostFamousMember();

	// 팔로워의 이미지
	List<MemberVO> getFollowers(String member_Id);

	// 팔로잉의 이미지
	List<MemberVO> getFollowings(String member_Id);

	// 관리자 - 전체 회원 조회
	List<MemberVO> getAllMember();

	// 내가 상대 팔로우 했나 여부
	String checkFollow(FollowVO check_Vo);

	// 관리자 - 회원 가입 현황 그래프
	List<Integer> getMemberTendency();
}