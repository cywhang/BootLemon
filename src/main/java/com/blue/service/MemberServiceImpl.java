package com.blue.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blue.mapper.MemberMapper;
import com.blue.dto.FollowVO;
import com.blue.dto.MemberVO;
import com.blue.dto.AlarmVO;

@Service
public class MemberServiceImpl implements MemberService {

	@Autowired
	private MemberMapper memberMapper;
	@Autowired
	private AlarmService alarmService;


	@Override
	public MemberVO getMember(String member_id) {		
		return memberMapper.getMemberInfo(member_id);
	}

	@Override
	public MemberVO getMemberInfo(String member_Id) {
		return memberMapper.getMember(member_Id);
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
		String pwd = memberMapper.confirmID(vo.getMember_Id());
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
	public void updateMember(MemberVO vo) {
		memberMapper.memberUpdate(vo);
	}

	@Override
	public void updateMember2(MemberVO vo){
		memberMapper.memberUpdate2(vo);
	}

	@Override
	public boolean checkPassword(String member_Id, String member_Password) {
		MemberVO vo = getMember(member_Id);
		return vo != null && vo.getMember_Password().equals(member_Password);
	}

	@Override
	public void deleteMember(String member_Id) {
		memberMapper.memberDelete(member_Id);
	}

	@Override
	public String searchId(MemberVO vo) {
		return memberMapper.searchId(vo);
	}

	@Override
	public void updatePassword(MemberVO vo) {
		memberMapper.updatePassword(vo);
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
	public List<MemberVO> getRecommendMember(String member_Id) {
		return memberMapper.getRecommendMember(member_Id);
	}

	@Override
	public void changeFollow(FollowVO vo) {
		String check = memberMapper.checkFollow(vo);

		// 알람
		AlarmVO alarmVO = new AlarmVO();
		alarmVO.setKind(1);
		alarmVO.setFrom_Mem(vo.getFollower());
		alarmVO.setTo_Mem(vo.getFollowing());
		alarmVO.setPost_Seq(0);
		alarmVO.setReply_Seq(0);

		// 알람 테이블에 해당 알람 있나 확인
		int result = alarmService.getOneAlarm_Seq(alarmVO);

		// 팔로우 중이 아닌 경우
		if (check == null) {
			memberMapper.addFollow(vo);
			if(result == 0) {
				alarmService.insertAlarm(alarmVO);
			}
		} else {
			memberMapper.delFollow(vo);
			if(result != 0) {
				alarmService.deleteAlarm(result);
			}
		}
	}

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
		return memberMapper.MostFamous();
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
	public String checkFollow(FollowVO check_Vo) {
		String result;
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
