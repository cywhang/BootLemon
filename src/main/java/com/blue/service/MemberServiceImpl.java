package com.blue.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blue.mapper.MemberMapper;
import com.blue.dto.FollowVO;
import com.blue.dto.MemberVO;

@Service
public class MemberServiceImpl implements MemberService {

	@Autowired
	private MemberMapper memberMapper;

	@Override
	public MemberVO getMember(String member_id) {		
		return memberMapper.getMember(member_id);
	}

	@Override
	public MemberVO getMemberInfo(String member_Id) {
		return memberMapper.getMemberInfo(member_Id);
	}

	@Override
	public int confirmID(String member_id) {
		String member_Password = memberMapper.confirmID(member_id);
		if (member_Password != null) {
			return 1;
		} else {
			return -1;
		}
	}

	@Override
	public void insertMember(MemberVO vo) {
		memberMapper.insertMember(vo);
	}

	// 로그인 처리
	// confirmID로 비밀번호 조회해서 입력한 값과 비교
	// 리턴값 : ID가 존재하고 비밀번호가 같으면 1 / 비밀번호가 다르면 0 / ID가 없으면 -1
	@Override
	public int doLogin(MemberVO vo) {
		int result = -1;
		String pwd = memberMapper.doLogin(vo);
		// ID가 없는 경우
		if (pwd == null) {
			result = -1;
			// 정상 로그인
		} else if (pwd.equals(vo.getMember_Password())) {
			result = 1;
			// 비밀번호 틀림
		} else {
			result = 0;
		}
		return result;
	}

	@Override
	public List<MemberVO> getRecommendMember(String member_Id) {
		return memberMapper.getRecommendMember(member_Id);
	}

	@Override
	public void changeFollow(FollowVO vo) {
		memberMapper.changeFollow(vo);
	}
	
	@Override
	public void updateMember(MemberVO vo) {
		memberMapper.updateMember(vo);
	}

	@Override
	public boolean checkDuplicate(String member_Id) {
		String member_Password = memberMapper.confirmID(member_Id);
        return member_Password != null;
	}

	// 전체 회원 프로필 이미지 조회
	@Override
	public HashMap<String, String> getMemberProfile() {
		List<HashMap<String, String>> resultList = memberMapper.memberProfile();
		HashMap<String, String> profileMap = new HashMap<>();

		for (HashMap<String, String> row : resultList) {
			String member_Id = row.get("member_Id");
			String member_Profile_Image = row.get("member_Profile_Image");
			profileMap.put(member_Id, member_Profile_Image);
		}
		return profileMap;
	}

	@Override
	public List<MemberVO> getMostFamousMember() {
		return memberMapper.getMostFamousMember();
	}

	@Override
	public void deleteMember(String member_Id) {
		memberMapper.deleteMember(member_Id);
	}

	@Override
	public List<MemberVO> getFollowers(String member_Id) {
		return memberMapper.getFollowers(member_Id);
	}

	@Override
	public List<MemberVO> getFollowings(String member_Id) {
		return memberMapper.getFollowings(member_Id);
	}

	@Override
	public List<MemberVO> getAllMember() {
		return memberMapper.getAllMember();
	}

	@Override
	public String searchId(MemberVO vo) {
		return memberMapper.searchId(vo);
	}

	@Override
	public MemberVO findPassword(MemberVO vo) {
		return memberMapper.findPassword(vo);
	}

	@Override
	public void updatePassword(MemberVO vo) {
		memberMapper.updatePassword(vo);
	}
	
	@Override
	public void updateMember2(MemberVO vo) {
		memberMapper.updateMember2(vo);
	}

	@Override
	public boolean checkPassword(String member_Id, String member_Password) {
		MemberVO vo = getMember(member_Id);
		return vo != null && vo.getMember_Password().equals(member_Password);
	}

	@Override
	public String selectPwdByIdNameEmail(MemberVO vo) {
		return memberMapper.PwdByIdNameEmail(vo);
	}

	@Override
	public List<MemberVO> searchMembers(String keyword) {
		 return	memberMapper.searchMembers(keyword);
	}

	@Override
	public String checkFollow(FollowVO check_Vo) {
		String result = null;
		if(memberMapper.checkFollow(check_Vo) != null) {
			result = "y";
		} else {
			result = "n";
		}
		return result;
	}

	@Override
	public List<Integer> getMemberTendency() {
		return memberMapper.getMemberTendency();
	}
}
