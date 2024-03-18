package com.blue.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.blue.dto.AlarmVO;
import com.blue.dto.FollowVO;
import com.blue.dto.MemberVO;
import com.blue.dto.PostVO;
import com.blue.dto.RequestVO;
import com.blue.service.AlarmService;
import com.blue.service.FollowService;
import com.blue.service.MemberService;
import com.blue.service.PostService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpSession;

@Controller
//컨트롤러에서 'loginUser', 'profileMap' 이라는 이름으로 모델 객체를 생성할때 세션에 동시에 저장한다.
@SessionAttributes({"loginUser", "profileMap", "S3Path"})
public class FollowController {

	@Autowired
	private MemberService memberService;
	@Autowired
	private PostService postService;
	@Autowired
	private FollowService followService;
	@Autowired
	private AlarmService alarmService;

	@GetMapping("/follow")
	public String getFollow(Model model, HttpSession session, @RequestParam String member_Id) {

		if(session.getAttribute("loginUser") == null) {

			model.addAttribute("message", "로그인을 해주세요");
			return "login";
		} else {

			String session_Id = ((MemberVO) session.getAttribute("loginUser")).getMember_Id();

			String profileImage = memberService.getMemberInfo(member_Id).getMember_Profile_Image();
			// 알람 리스트를 담는 부분
			List<AlarmVO> alarmList = alarmService.getAllAlarm(session_Id);

			int alarmListSize = alarmList.size();

			// 알람의 종류를 파악하는 부분
			for(int j=0; j<alarmList.size(); j++) {

				int kind = alarmList.get(j).getKind();
				if(kind == 1) {
					alarmList.get(j).setMessage(alarmList.get(j).getFrom_Mem() + "님께서 회원님을 팔로우 <br>하였습니다.");
				} else if(kind == 2) {
					alarmList.get(j).setMessage(alarmList.get(j).getFrom_Mem() + "님께서 회원님의 게시글에 <br>좋아요를 눌렀습니다.");
				} else if(kind == 3) {
					alarmList.get(j).setMessage(alarmList.get(j).getFrom_Mem() + "님께서 회원님의 게시글에 <br>댓글을 달았습니다.");
				} else if(kind == 4) {
					alarmList.get(j).setMessage(alarmList.get(j).getFrom_Mem() + "님께서 회원님의 댓글에 <br>좋아요를 눌렀습니다.");
				} else if(kind == 5) {
					alarmList.get(j).setMessage("회원님께서 문의하신 질문에 <br>답글이 달렸습니다.");
				}
			}
			List<FollowVO> following_Id = followService.getFollowing(member_Id);
			List<FollowVO> follower_Id = followService.getFollower(member_Id);
			List<MemberVO> following_info = new ArrayList<MemberVO>();
			List<MemberVO> follower_info = new ArrayList<MemberVO>();

			for(FollowVO id : following_Id) {

				MemberVO following_member = memberService.getMemberInfo(id.getFollowing());
				if(following_member == null) {
				} else {
					following_info.add(following_member);
				}
			}

			for(FollowVO id : follower_Id) {

				MemberVO follower_member = memberService.getMemberInfo(id.getFollower());

				if(follower_member == null) {
				} else {
					follower_info.add(follower_member);
				}
			}

			int followingTotalPageNum = 1;

			if(following_info.size() % 10 != 0 && following_info.size() > 10) {
				followingTotalPageNum = following_info.size() / 10 + 1;
			} else if(following_info.size() % 10 != 0 && following_info.size() < 10) {
				followingTotalPageNum = 1;
			} else if(following_info.size() % 10 == 0) {
				followingTotalPageNum = following_info.size() / 10;
			}

			int followerTotalPageNum = 1;

			if(follower_info.size() % 10 != 0 && follower_info.size() > 10) {
				followerTotalPageNum = follower_info.size() / 10 + 1;
			} else if(follower_info.size() % 10 != 0 && follower_info.size() < 10) {
				followerTotalPageNum = 1;
			} else if(follower_info.size() % 10 == 0) {
				followerTotalPageNum = follower_info.size() / 10;
			}

			int followingLoadRow = 10;

			if(following_info.size() <= 10) {
				followingLoadRow = following_info.size();
			}

			int followerLoadRow = 10;

			if(follower_info.size() <= 10) {
				followerLoadRow = follower_info.size();
			}

			for(int i = 0; i < followerLoadRow; i++) {
				for(int j = 0; j < followingLoadRow; j++) {
					if(follower_info.get(i).getMember_Id().equals(following_info.get(j).getMember_Id())) {
						follower_info.get(i).setBothFollow(1);
					}
				}
			}

			model.addAttribute("following", following_info);
			model.addAttribute("followingLoadRow", followingLoadRow);
			model.addAttribute("followingTotalPageNum", followingTotalPageNum);
			model.addAttribute("followingPageNum", 1);
			model.addAttribute("follower", follower_info);
			model.addAttribute("followerLoadRow", followerLoadRow);
			model.addAttribute("followerTotalPageNum", followerTotalPageNum);
			model.addAttribute("followerPageNum", 1);

			// 화면 우측 Hottest Feed
			List<PostVO> hottestFeed = postService.getHottestFeed();
			model.addAttribute("hottestFeed", hottestFeed);
			model.addAttribute("profileImage", profileImage);
			model.addAttribute("member_Id", member_Id);
			model.addAttribute("alarmList", alarmList);
			model.addAttribute("alarmListSize", alarmListSize);

			return "follow";

		}
	}

	// 팔로우 변경
	@PostMapping("/changeFollow")
	@ResponseBody
	public String changeFollow(@RequestBody String member_Id, HttpSession session) {
		String follower = ((MemberVO) session.getAttribute("loginUser")).getMember_Id();
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(member_Id);

			String following = jsonNode.get("member_Id").asText();

			FollowVO vo = new FollowVO();
			vo.setFollower(follower);
			vo.setFollowing(following);

			memberService.changeFollow(vo);

			return "success";
		} catch (Exception e) {
			// JSON 파싱 오류 처리
			e.printStackTrace();
			return "error";
		}
	}

	@PostMapping("/moreLoadFollowing")
	@ResponseBody
	public Map<String, Object> moreLoadFollowing(@RequestBody RequestVO request, HttpSession session) {

		// 0. ajax요청에 대한 response값 전달을 위한 Map 변수 선언
		Map<String, Object> dataMap = new HashMap<>();

		// 1. ajax에서 받아온 객체 받아놓기
		int followingTotalPageNum = request.getFollowingTotalPageNum();
		int followingPageNum = request.getFollowingPageNum();
		String member_Id = request.getMember_Id();

		String sessionId = ((MemberVO) session.getAttribute("loginUser")).getMember_Id();

		int LocalPageFirstNum = followingPageNum*10+1;
		int LocalPageLastNum = followingPageNum*10+10;

		if(followingPageNum < followingTotalPageNum) {
			followingPageNum++;
		} else {
			dataMap.put("message", "불러올 데이터가 존재하지 않습니다1");
			return dataMap;
		}

		FollowVO followVo = new FollowVO();

		followVo.setFollower(member_Id);
		followVo.setFollowingLocalPageFirstNum(LocalPageFirstNum);
		followVo.setFollowingLocalPageLastNum(LocalPageLastNum);

		// 팔로워 추가 로드하기(행~행 조건으로 조회)
		List<FollowVO> following_Id  = followService.getMoreFollowing(followVo);

		if(following_Id == null) {
			dataMap.put("message", "불러올 데이터가 존재하지 않습니다2");
			return dataMap;
		} else if(following_Id.size() == 0) {
			dataMap.put("message", "불러올 데이터가 존재하지 않습니다3");
			return dataMap;
		}

		List<MemberVO> following_info = new ArrayList<MemberVO>();

		for(FollowVO id : following_Id) {

			MemberVO following_member = memberService.getMemberInfo(id.getFollowing());

			if(following_member == null) {
			} else {
				following_info.add(following_member);
			}
		}

		dataMap.put("following_info", following_info);
		dataMap.put("following_size", following_Id.size());
		dataMap.put("followingPageNum", followingPageNum);
		dataMap.put("followingTotalPageNum", followingTotalPageNum);

		return dataMap;
	}

	@PostMapping("/moreLoadFollower")
	@ResponseBody
	public Map<String, Object> moreLoadFollower(@RequestBody RequestVO request, HttpSession session) {
		// 0. ajax요청에 대한 response값 전달을 위한 Map 변수 선언
		Map<String, Object> dataMap = new HashMap<>();

		// 1. ajax에서 받아온 객체 받아놓기
		int followerTotalPageNum = request.getFollowerTotalPageNum();
		int followerPageNum = request.getFollowerPageNum();
		String member_Id = request.getMember_Id();

		String sessionId = ((MemberVO) session.getAttribute("loginUser")).getMember_Id();

		int LocalPageFirstNum = followerPageNum*10+1;
		int LocalPageLastNum = followerPageNum*10+10;

		if(followerPageNum < followerTotalPageNum) {
			followerPageNum++;
		} else {
			dataMap.put("message", "불러올 데이터가 존재하지 않습니다1");
			return dataMap;
		}

		FollowVO followVo = new FollowVO();

		followVo.setFollowing(member_Id);
		followVo.setFollowerLocalPageFirstNum(LocalPageFirstNum);
		followVo.setFollowerLocalPageLastNum(LocalPageLastNum);

		// 팔로워 추가 로드하기(행~행 조건으로 조회)
		List<FollowVO> follower_Id  = followService.getMoreFollower(followVo);

		if(follower_Id == null) {
			dataMap.put("message", "불러올 데이터가 존재하지 않습니다2");
			return dataMap;
		} else if(follower_Id.size() == 0) {
			dataMap.put("message", "불러올 데이터가 존재하지 않습니다3");
			return dataMap;
		}

		List<MemberVO> follower_info = new ArrayList<MemberVO>();

		for(FollowVO id : follower_Id) {

			MemberVO follower_member = memberService.getMemberInfo(id.getFollower());

			if(follower_member == null) {
			} else {
				follower_info.add(follower_member);
			}
		}

		//로드한 팔로워에서 내가 팔로잉한 사람이 있는지 비교하기 위해 팔로잉 멤버를 조회
		List<FollowVO> following_Id = followService.getFollowing(sessionId);

		dataMap.put("follower_size", follower_Id.size());
		dataMap.put("followerPageNum", followerPageNum);
		dataMap.put("followerTotalPageNum", followerTotalPageNum);
		dataMap.put("follower_info", follower_info);
		dataMap.put("following_Id", following_Id);
		dataMap.put("following_size", following_Id.size());

		return dataMap;
	}

	@GetMapping("/alarmFollow")
	public String alarmFollow(Model model, HttpSession session, @RequestParam String member_Id, @RequestParam int alarm_Seq) {

		if(session.getAttribute("loginUser") == null) {

			model.addAttribute("message", "로그인을 해주세요");

			return "login";
		} else {

			//알람 삭제
			alarmService.deleteAlarm(alarm_Seq);

			String session_Id = ((MemberVO) session.getAttribute("loginUser")).getMember_Id();

			String profileImage = memberService.getMemberInfo(session_Id).getMember_Profile_Image();

			// 알람 리스트를 담는 부분
			List<AlarmVO> alarmList = alarmService.getAllAlarm(session_Id);

			int alarmListSize = alarmList.size();

			// 알람의 종류를 파악하는 부분
			for(int j=0; j<alarmList.size(); j++) {
				int kind = alarmList.get(j).getKind();
				if(kind == 1) {
					alarmList.get(j).setMessage(alarmList.get(j).getFrom_Mem() + "님께서 회원님을 팔로우 <br>하였습니다.");
				} else if(kind == 2) {
					alarmList.get(j).setMessage(alarmList.get(j).getFrom_Mem() + "님께서 회원님의 게시글에 <br>좋아요를 눌렀습니다.");
				} else if(kind == 3) {
					alarmList.get(j).setMessage(alarmList.get(j).getFrom_Mem() + "님께서 회원님의 게시글에 <br>댓글을 달았습니다.");
				} else if(kind == 4) {
					alarmList.get(j).setMessage(alarmList.get(j).getFrom_Mem() + "님께서 회원님의 댓글에 <br>좋아요를 눌렀습니다.");
				} else if(kind == 5) {
					alarmList.get(j).setMessage("회원님께서 문의하신 질문에 <br>답글이 달렸습니다.");
				}
			}

			List<FollowVO> following_Id = followService.getFollowing(member_Id);
			List<FollowVO> follower_Id = followService.getFollower(member_Id);
			List<MemberVO> following_info = new ArrayList<MemberVO>();
			List<MemberVO> follower_info = new ArrayList<MemberVO>();

			for(FollowVO id : following_Id) {

				MemberVO following_member = memberService.getMemberInfo(id.getFollowing());
				if(following_member == null) {
				} else {
					following_info.add(following_member);
				}
			}

			for(FollowVO id : follower_Id) {

				MemberVO follower_member = memberService.getMemberInfo(id.getFollower());

				if(follower_member == null) {
				} else {
					follower_info.add(follower_member);
				}
			}

			int followingTotalPageNum = 1;

			if(following_info.size() % 10 != 0 && following_info.size() > 10) {
				followingTotalPageNum = following_info.size() / 10 + 1;
			} else if(following_info.size() % 10 != 0 && following_info.size() < 10) {
				followingTotalPageNum = 1;
			} else if(following_info.size() % 10 == 0) {
				followingTotalPageNum = following_info.size() / 10;
			}

			int followerTotalPageNum = 1;

			if(follower_info.size() % 10 != 0 && follower_info.size() > 10) {
				followerTotalPageNum = follower_info.size() / 10 + 1;
			} else if(follower_info.size() % 10 != 0 && follower_info.size() < 10) {
				followerTotalPageNum = 1;
			} else if(follower_info.size() % 10 == 0) {
				followerTotalPageNum = follower_info.size() / 10;
			}

			int followingLoadRow = 10;

			if(following_info.size() <= 10) {
				followingLoadRow = following_info.size();
			}

			int followerLoadRow = 10;

			if(follower_info.size() <= 10) {
				followerLoadRow = follower_info.size();
			}

			for(int i = 0; i < followerLoadRow; i++) {
				for(int j = 0; j < followingLoadRow; j++) {
					if(follower_info.get(i).getMember_Id().equals(following_info.get(j).getMember_Id())) {
						follower_info.get(i).setBothFollow(1);
					}
				}
			}

			model.addAttribute("following", following_info);
			model.addAttribute("followingLoadRow", followingLoadRow);
			model.addAttribute("followingTotalPageNum", followingTotalPageNum);
			model.addAttribute("followingPageNum", 1);

			model.addAttribute("follower", follower_info);
			model.addAttribute("followerLoadRow", followerLoadRow);
			model.addAttribute("followerTotalPageNum", followerTotalPageNum);
			model.addAttribute("followerPageNum", 1);

			// 화면 우측 Hottest Feed
			List<PostVO> hottestFeed = postService.getHottestFeed();
			model.addAttribute("hottestFeed", hottestFeed);

			model.addAttribute("profileImage", profileImage);
			model.addAttribute("alarmList", alarmList);
			model.addAttribute("alarmListSize", alarmListSize);

			return "follow";

		}
	}
}
