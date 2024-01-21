package com.blue.service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blue.mapper.MemberMapper;
import com.blue.dto.FollowVO;
import com.blue.dto.MemberVO;
import com.blue.dto.AlarmVO;
import org.springframework.transaction.annotation.Transactional;

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
	@Transactional
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

	@Override
	public String getKaKaoAccessToken(String code) {
		String access_Token="";
		String refresh_Token ="";
		String reqURL = "https://kauth.kakao.com/oauth/token";

		try{
			URL url = new URL(reqURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			//POST 요청을 위해 기본값이 false인 setDoOutput을 true로
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);

			//POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
			StringBuilder sb = new StringBuilder();
			sb.append("grant_type=authorization_code");
			sb.append("&client_id=824a9e398566d9c877c7b186c2881e7e"); // TODO REST_API_KEY 입력
			sb.append("&redirect_uri=http://localhost:9999/kakao"); // TODO 인가코드 받은 redirect_uri 입력
			sb.append("&code=" + code);
			bw.write(sb.toString());
			bw.flush();

			//결과 코드가 200이라면 성공
			int responseCode = conn.getResponseCode();
			System.out.println("responseCode : " + responseCode);
			//요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			String result = "";

			while ((line = br.readLine()) != null) {
				result += line;
			}
			System.out.println("response body : " + result);

			//Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(result);

			access_Token = element.getAsJsonObject().get("access_token").getAsString();
			refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

			System.out.println("access_token : " + access_Token);
			System.out.println("refresh_token : " + refresh_Token);

			br.close();
			bw.close();
		}catch (IOException e) {
			e.printStackTrace();
		}

		return access_Token;
	}

	// 카카오 로그인 access token을 이용한 회원정보 반환받기
	@Override
	public HashMap<Integer, String> createKakaoUser(String token) {
		String reqURL = "https://kapi.kakao.com/v2/user/me";

		//access_token을 이용하여 사용자 정보 조회
		try {
			URL url = new URL(reqURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Authorization", "Bearer " + token); //전송할 header 작성, access_token전송

			//결과 코드가 200이라면 성공
			int responseCode = conn.getResponseCode();
			System.out.println("responseCode : " + responseCode);

			//요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			String result = "";

			while ((line = br.readLine()) != null) {
				result += line;
			}
			System.out.println("response body : " + result);

			//Gson 라이브러리로 JSON파싱
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(result);

			// boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
			// System.out.println(hasEmail);
			//email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();

			// 0. 회원정보 객체 (필수 아이디, 선택 이메일)
			HashMap<Integer, String> map = new HashMap<>();
			// 1. 고유 아이디 설정
			String id = element.getAsJsonObject().get("id").getAsString();
			map.put(1, id);

			// 2. 필수 닉네임 설정
			String name = element.getAsJsonObject().get("properties").getAsJsonObject().get("nickname").getAsString();
			map.put(2, name);

			// 3. 선택 이메일 설정
			String email = "";
			// 선택항목인 이메일여부를 검사하고 값이 있다면 email값 세팅
			if (element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email") != null) {
				email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
				map.put(3, email);
			} else {
				map.put(3, "정보없음@");
			}


			System.out.println("id : " + id);
			System.out.println("email : " + email);
			System.out.println("nickname : " + name);

			br.close();

			return map;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getNaverAccessToken(String code){
		String access_Token="";
		String refresh_Token ="";
		String reqURL = "https://nid.naver.com/oauth2.0/token";
		try{
			URL url = new URL(reqURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			//POST 요청을 위해 기본값이 false인 setDoOutput을 true로
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);

			//POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
			StringBuilder sb = new StringBuilder();
			sb.append("grant_type=authorization_code");
			sb.append("&client_id=JEjqn2g57dyBOadXOKoP"); // TODO REST_API_KEY 입력
			sb.append("&redirect_uri=http://localhost:9999/naver"); // TODO 인가코드 받은 redirect_uri 입력
			sb.append("&client_secret=LuZDSS3e7s");
			sb.append("&code=" + code);
			bw.write(sb.toString());
			bw.flush();

			//결과 코드가 200이라면 성공
			int responseCode = conn.getResponseCode();
			System.out.println("responseCode : " + responseCode);
			//요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			String result = "";

			while ((line = br.readLine()) != null) {
				result += line;
			}
			System.out.println("response body : " + result);

			//Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(result);

			access_Token = element.getAsJsonObject().get("access_token").getAsString();
			refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

			System.out.println("access_token : " + access_Token);
			System.out.println("refresh_token : " + refresh_Token);

			br.close();
			bw.close();
		}catch (IOException e) {
			e.printStackTrace();
		}

		return access_Token;
	}


	// 카카오 로그인 access token을 이용한 회원정보 반환받기
	@Override
	public HashMap<Integer, String> createNaverUser(String token) {
		String reqURL = "https://openapi.naver.com/v1/nid/me";

		//access_token을 이용하여 사용자 정보 조회
		try {
			URL url = new URL(reqURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Authorization", "Bearer " + token); //전송할 header 작성, access_token전송

			//결과 코드가 200이라면 성공
			int responseCode = conn.getResponseCode();
			System.out.println("responseCode : " + responseCode);

			//요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			String result = "";

			while ((line = br.readLine()) != null) {
				result += line;
			}
			System.out.println("response body : " + result);

			//Gson 라이브러리로 JSON파싱
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(result);

			// 0. 회원정보 객체 (필수 아이디, 선택 이메일)
			HashMap<Integer, String> map = new HashMap<>();
			// 1. 고유 아이디 설정
			String id = element.getAsJsonObject().get("response").getAsJsonObject().get("id").getAsString();
			map.put(1, id);

			// 2. 필수 닉네임 설정
			String name = element.getAsJsonObject().get("response").getAsJsonObject().get("name").getAsString();
			String decodeString = StringEscapeUtils.unescapeJava(name);
			System.out.println("decoding: " + decodeString);
			map.put(2, decodeString);

			// 3. 선택 이메일 설정
			String email = "";
			// 선택항목인 이메일여부를 검사하고 값이 있다면 email값 세팅
			if (element.getAsJsonObject().get("response").getAsJsonObject().get("email") != null) {
				email = element.getAsJsonObject().get("response").getAsJsonObject().get("email").getAsString();
				map.put(3, email);
			} else {
				map.put(3, "정보없음@");
			}


			System.out.println("id : " + id);
			System.out.println("email : " + email);
			System.out.println("nickname : " + name);

			br.close();

			return map;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String checkSocial(String id, String type){
		// 값이 있을때
		if(memberMapper.checkSocial(id, type) != null){
			return memberMapper.checkSocial(id, type);
		} else { // 값이 없을때
			return null;
		}
	}

	@Override
	public int checkSeq(){
		return memberMapper.checkSeq();
	}

	@Override
	@Transactional
	public void insertSocial(String id, String userid, String type){
		memberMapper.insertSocial(id, userid, type);
	}
}
