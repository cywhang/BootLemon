package com.blue.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blue.dto.*;

import com.blue.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpSession;
@Controller
//컨트롤러에서 'loginUser', 'profileMap' 이라는 이름으로 모델 객체를 생성할때 세션에 동시에 저장한다.
@SessionAttributes({"loginUser", "profileMap"})
public class MainController {

	@Autowired
	private MemberService memberService;
	@Autowired
	private PostService postService;
	@Autowired
	private ReplyService replyService;
	@Autowired
	private QnaService qnaService;
	@Autowired
	private AlarmService alarmService;

	// 로그인 페이지로 이동
	@GetMapping(value="/")
	public String login(){

		return "login";
	}
	
	// 카카오 로그인 코드 테스트
	@ResponseBody
	@GetMapping("/kakao")
	public void  kakaoCallback(@RequestParam String code) {

		System.out.println(code);

	}

	// 로그인 처리
	@PostMapping("loginProc")
	public String LoginAction(Model model, @ModelAttribute("vo") MemberVO vo) {
		int result = memberService.doLogin(vo);

		if(result == 1) {
			if(vo.getMember_Id().equals("admin")) {
				model.addAttribute("loginUser", memberService.getMember(vo.getMember_Id()));
				return "redirect:admin_Index";
			} else {
				model.addAttribute("loginUser", memberService.getMember(vo.getMember_Id()));
				return "redirect:index";
			}
		} else {
			return "login_fail";
		}
	}

	// 로그아웃 처리
	@GetMapping("/logout")
	public String logout(SessionStatus status) {
		status.setComplete();
		return "login";
	}

	// index 페이지 로드
	@RequestMapping("index")
	public String getRecommendMember(Model model, HttpSession session) {

		if(session.getAttribute("loginUser") == null) {
			model.addAttribute("message", "로그인을 해주세요");
			return "login";
		} else {
			/* index페이지의 팔로우 부분 */
			String member_Id = ((MemberVO) session.getAttribute("loginUser")).getMember_Id();

			String profileImage = memberService.getMemberInfo(member_Id).getMember_Profile_Image();

			List<MemberVO> recommendMember = memberService.getRecommendMember(member_Id);
			List<PostVO> hottestFeed = postService.getHottestFeed();

			// 알람 리스트를 담는 부분
			List<AlarmVO> alarmList = alarmService.getAllAlarm(member_Id);

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

			/* index페이지의 뉴스피드 부분 */
			// 자신, 팔로잉한 사람들의 게시글을 담는부분
			ArrayList<PostVO> postlist = postService.getlistPost(member_Id);
			for(int i = 0; i < postlist.size(); i++) {
				postlist.get(i).setPost_Content(postlist.get(i).getPost_Content().replace("\n", "<br>"));
			}

			// 각 post_seq에 대한 댓글들을 매핑할 공간.
			Map<Integer, ArrayList<ReplyVO>> replymap = new HashMap<>();

			// 각 post_seq에 대한 해시태그들을 매핑할 공간.
			Map<Integer, ArrayList<TagVO>> hashmap = new HashMap<>();

			// 정렬된 postlist의 인덱스 순으로 댓글 리스트를 매핑함.
			// 동시에 각 게시글의 좋아요 카운트와 댓글 카운트를 저장.
			for(int i=0; i<postlist.size(); i++) {
				// 자신, 팔로잉한 사람들의 게시글의 post_seq를 불러온다.
				int post_Seq = postlist.get(i).getPost_Seq();

				// i번째 게시글의 댓글 리스트를 담음
				ArrayList<ReplyVO> replylist = replyService.getReplyPreview(post_Seq);

				// i번째 게시글의 댓글 좋아요 여부 체크
				for(int k = 0; k < replylist.size(); k++) {
					String realReply_Member_Id = replylist.get(k).getMember_Id();
					ReplyVO KVO = replylist.get(k);
					LikeVO check = new LikeVO();
					check.setMember_Id(member_Id);
					check.setPost_Seq(KVO.getPost_Seq());
					check.setReply_Seq(KVO.getReply_Seq());
					String reply_LikeYN = replyService.getCheckReplyLike(check);
					replylist.get(k).setReply_LikeYN(reply_LikeYN);
					replylist.get(k).setMember_Id(realReply_Member_Id);
				}

				// i번째의 게시글의 댓글을 map에 매핑하는 작업
				replymap.put(i, replylist);

				// i번째 게시글의 좋아요 여부 체크
				PostVO voForLikeYN = new PostVO();
				voForLikeYN.setMember_Id(member_Id);
				voForLikeYN.setPost_Seq(post_Seq);
				String post_LikeYN = postService.getLikeYN(voForLikeYN);
				postlist.get(i).setPost_LikeYN(post_LikeYN);

				// i번째 게시글의 해시태그 체크    hashmap
				ArrayList<TagVO> hash = postService.getHashtagList(post_Seq);
				hashmap.put(post_Seq, hash);
			}

			// 전체 회원 프로필 이미지 조회
			HashMap<String, String> profilemap = memberService.getMemberProfile();

			model.addAttribute("profileImage", profileImage);
			model.addAttribute("alarmList", alarmList);
			model.addAttribute("alarmListSize", alarmListSize);
			model.addAttribute("profileMap", profilemap);
			model.addAttribute("postList", postlist);
			model.addAttribute("replyMap", replymap);
			model.addAttribute("recommendMember", recommendMember);
			model.addAttribute("hottestFeed", hottestFeed);
			model.addAttribute("member_Id", member_Id);
			model.addAttribute("hashMap", hashmap);

			return "index";
		}
	}

	// PEOPLE 탭 List 가져오기
	@GetMapping("/people_List")
	public ResponseEntity<Map<String, Object>> people_List(HttpSession session, Model model) {

		String member_Id = ((MemberVO) session.getAttribute("loginUser")).getMember_Id();

		List<MemberVO> canFollow = memberService.getRecommendMember(member_Id);

		List<MemberVO> mostFamous = memberService.getMostFamousMember();
		for(int i = 0 ; i < mostFamous.size() ; i++) {
			String check_Id = mostFamous.get(i).getMember_Id();
			FollowVO check_Vo = new FollowVO();
			check_Vo.setFollower(member_Id);
			check_Vo.setFollowing(check_Id);
			String followCheck = memberService.checkFollow(check_Vo);
			mostFamous.get(i).setFollow_Check(followCheck);
		}
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("canFollow", canFollow);
		responseData.put("mostFamous", mostFamous);
		responseData.put("member_Id", member_Id);

		System.out.println("canFollow" + canFollow.toString());
		System.out.println("mostFamous" + mostFamous.toString());

		return ResponseEntity.ok(responseData);
	}

	// Profile 이동
	@GetMapping("/profile")
	public String makeProfile(@RequestParam("member_Id") String member_Id, Model model, HttpSession session) {
		if(session.getAttribute("loginUser") == null) {
			model.addAttribute("message", "로그인을 해주세요");
			return "login";
		} else {
			MemberVO member = memberService.getMember(member_Id);
			String loginUser_Id = ((MemberVO) session.getAttribute("loginUser")).getMember_Id();

			FollowVO checkVo = new FollowVO();
			checkVo.setFollower(loginUser_Id);
			checkVo.setFollowing(member_Id);
			String followCheck = memberService.checkFollow(checkVo);
			member.setFollow_Check(followCheck);

			// 팔로워 팔로우 숫자 밑에 작은 동그라미 이미지들 채울 용도
			List<MemberVO> followers = memberService.getFollowers(member_Id);
			int followers_Size = followers.size();
			List<MemberVO> followings = memberService.getFollowings(member_Id);
			int followings_Size = followings.size();
			model.addAttribute("followers", followers);
			model.addAttribute("followers_Size", followers_Size);
			model.addAttribute("followings", followings);
			model.addAttribute("followings_Size", followings_Size);


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

			// 1. 이동한 프로필 페이지가 세션아이디와 같다면 public여부 상관없이 전체 조회
			// 2. 이동한 프로필 페이지가 세션아이디와 다르다면 public = 'y' 게시글만 조회
			PostVO pvo = new PostVO();
			pvo.setMember_Id(member_Id);
			pvo.setSession_Id(session_Id);

			ArrayList<PostVO> postlist = postService.getMemberPost(pvo);

			// 각 post_seq에 대한 댓글들을 매핑할 공간.
			Map<Integer, ArrayList<ReplyVO>> replymap = new HashMap<>();

			// 각 post_seq에 대한 해시태그들을 매핑할 공간.
			Map<Integer, ArrayList<TagVO>> hashmap = new HashMap<>();

			// 정렬된 postlist의 인덱스 순으로 댓글 리스트를 매핑함.
			// 동시에 각 게시글의 좋아요 카운트와 댓글 카운트를 저장.
			for(int i = 0; i < postlist.size(); i++) {

				postlist.get(i).setPost_Content(postlist.get(i).getPost_Content().replace("\n", "<br>"));

				int post_Seq = postlist.get(i).getPost_Seq();

				// i번째 게시글의 댓글 리스트를 담음
				ArrayList<ReplyVO> replylist = replyService.getReplyPreview(post_Seq);
				// i번째 게시글의 댓글 좋아요 여부 체크
				for(int k = 0; k < replylist.size(); k++) {
					String realReply_Member_Id = replylist.get(k).getMember_Id();
					ReplyVO KVO = replylist.get(k);
					LikeVO check = new LikeVO();
					check.setMember_Id(member_Id);
					check.setPost_Seq(KVO.getPost_Seq());
					check.setReply_Seq(KVO.getReply_Seq());
					String reply_LikeYN = replyService.getCheckReplyLike(check);
					replylist.get(k).setReply_LikeYN(reply_LikeYN);
					replylist.get(k).setMember_Id(realReply_Member_Id);
				}

				// i번째의 게시글의 댓글을 map에 매핑하는 작업
				replymap.put(i, replylist);

				// i번째 게시글의 좋아요 여부 체크
				PostVO voForLikeYN = new PostVO();
				voForLikeYN.setMember_Id(loginUser_Id);
				voForLikeYN.setPost_Seq(post_Seq);
				String post_LikeYN = postService.getLikeYN(voForLikeYN);
				int post_Like_Count = postService.getPost_Like_Count(post_Seq);
				postlist.get(i).setPost_Like_Count(post_Like_Count);
				postlist.get(i).setPost_LikeYN(post_LikeYN);

				// i번째 게시글의 해시태그 체크    hashmap
				ArrayList<TagVO> hash = postService.getHashtagList(post_Seq);
				hashmap.put(post_Seq, hash);

			}

			// 전체 회원 프로필 이미지 조회
			HashMap<String, String> profilemap = memberService.getMemberProfile();

			// 화면 우측 Hottest Feed
			List<PostVO> hottestFeed = postService.getHottestFeed();

			model.addAttribute("profileImage", profileImage);
			model.addAttribute("loginUser_Id", loginUser_Id);
			model.addAttribute("member", member);
			model.addAttribute("member_Id", member_Id);
			model.addAttribute("postlist", postlist);
			model.addAttribute("profileMap", profilemap);
			model.addAttribute("replyMap", replymap);
			model.addAttribute("hottestFeed", hottestFeed);
			model.addAttribute("hashMap", hashmap);
			model.addAttribute("alarmList", alarmList);
			model.addAttribute("alarmListSize", alarmListSize);

			return "profile";
		}
	}


	// Profile 이동
	@PostMapping("/profileInfinite")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> MoreProfilePost(@RequestBody Map<String, String> requestBody, Model model, HttpSession session) {

		String member_Id = requestBody.get("member_Id");

		String loginUser_Id = ((MemberVO) session.getAttribute("loginUser")).getMember_Id();

		// 1. 이동한 프로필 페이지가 세션아이디와 같다면 public여부 상관없이 전체 조회
		// 2. 이동한 프로필 페이지가 세션아이디와 다르다면 public = 'y' 게시글만 조회
		PostVO pvo = new PostVO();
		pvo.setMember_Id(member_Id);
		pvo.setSession_Id(loginUser_Id);

		ArrayList<PostVO> postlist = postService.getMemberPost(pvo);

		// 각 post_seq에 대한 댓글들을 매핑할 공간.
		Map<Integer, ArrayList<ReplyVO>> replymap = new HashMap<>();

		// 각 post_seq에 대한 해시태그들을 매핑할 공간.
		Map<Integer, ArrayList<TagVO>> hashmap = new HashMap<>();

		// 정렬된 postlist의 인덱스 순으로 댓글 리스트를 매핑함.
		// 동시에 각 게시글의 좋아요 카운트와 댓글 카운트를 저장.
		for(int i = 0; i < postlist.size(); i++) {

			int post_Seq = postlist.get(i).getPost_Seq();

			// i번째 게시글의 댓글 리스트를 담음
			ArrayList<ReplyVO> replylist = replyService.getReplyPreview(post_Seq);
			// i번째 게시글의 댓글 좋아요 여부 체크
			for(int k = 0; k < replylist.size(); k++) {
				String realReply_Member_Id = replylist.get(k).getMember_Id();
				ReplyVO KVO = replylist.get(k);
				LikeVO check = new LikeVO();
				check.setMember_Id(member_Id);
				check.setPost_Seq(KVO.getPost_Seq());
				check.setReply_Seq(KVO.getReply_Seq());
				String reply_LikeYN = replyService.getCheckReplyLike(check);
				replylist.get(k).setReply_LikeYN(reply_LikeYN);
				replylist.get(k).setMember_Id(realReply_Member_Id);
			}

			// i번째의 게시글의 댓글을 map에 매핑하는 작업
			replymap.put(i, replylist);

			// i번째 게시글의 좋아요 여부 체크
			PostVO voForLikeYN = new PostVO();
			voForLikeYN.setMember_Id(loginUser_Id);
			voForLikeYN.setPost_Seq(post_Seq);
			String post_LikeYN = postService.getLikeYN(voForLikeYN);
			int post_Like_Count = postService.getPost_Like_Count(post_Seq);
			postlist.get(i).setPost_Like_Count(post_Like_Count);
			postlist.get(i).setPost_LikeYN(post_LikeYN);

			// i번째 게시글의 해시태그 체크    hashmap
			ArrayList<TagVO> hash = postService.getHashtagList(post_Seq);
			hashmap.put(post_Seq, hash);
		}

		// 전체 회원 프로필 이미지 조회
		HashMap<String, String> profilemap = memberService.getMemberProfile();

		Map<String, Object> responseData = new HashMap<>();

		responseData.put("session_Id", loginUser_Id);
		responseData.put("postlist", postlist);
		responseData.put("profileMap", profilemap);
		responseData.put("replyMap", replymap);
		responseData.put("hashMap", hashmap);

		return ResponseEntity.ok(responseData);
	}

	@GetMapping("trending_List")
	public ResponseEntity<Map<String, Object>> trending_List(HttpSession session, Model model) {

		String member_Id = ((MemberVO) session.getAttribute("loginUser")).getMember_Id();

		List<PostVO> hottestFeed = postService.getHottestFeed();

		// 각 post_seq에 대한 댓글들을 매핑할 공간.
		Map<Integer, ArrayList<ReplyVO>> replymap = new HashMap<>();

		// 각 post_seq에 대한 해시태그들을 매핑할 공간.
		Map<Integer, ArrayList<TagVO>> hashmap = new HashMap<>();

		// 정렬된 postlist의 인덱스 순으로 댓글 리스트를 매핑함.
		// 동시에 각 게시글의 좋아요 카운트와 댓글 카운트를 저장.
		for(int i=0; i<hottestFeed.size(); i++) {
			// 자신, 팔로잉한 사람들의 게시글의 post_seq를 불러온다.
			int post_Seq = hottestFeed.get(i).getPost_Seq();

			// i번째 게시글의 댓글 리스트를 담음
			ArrayList<ReplyVO> replylist = replyService.getReplyPreview(post_Seq);

			// i번째 게시글의 댓글 좋아요 여부 체크
			for(int k = 0; k < replylist.size(); k++) {
				String realReply_Member_Id = replylist.get(k).getMember_Id();
				ReplyVO KVO = replylist.get(k);
				LikeVO check = new LikeVO();
				check.setMember_Id(member_Id);
				check.setPost_Seq(KVO.getPost_Seq());
				check.setReply_Seq(KVO.getReply_Seq());
				String reply_LikeYN = replyService.getCheckReplyLike(check);
				replylist.get(k).setReply_LikeYN(reply_LikeYN);
				replylist.get(k).setMember_Id(realReply_Member_Id);
			}

			// i번째의 게시글의 댓글을 map에 매핑하는 작업
			replymap.put(i, replylist);

			// i번째 게시글의 좋아요 여부 체크
			PostVO voForLikeYN = new PostVO();
			voForLikeYN.setMember_Id(member_Id);
			voForLikeYN.setPost_Seq(post_Seq);
			String post_LikeYN = postService.getLikeYN(voForLikeYN);
			hottestFeed.get(i).setPost_LikeYN(post_LikeYN);

			// i번째 게시글의 해시태그 체크    hashmap
			ArrayList<TagVO> hash = postService.getHashtagList(post_Seq);
			hashmap.put(post_Seq, hash);
		}

		// 전체 회원 프로필 이미지 조회
		HashMap<String, String> profilemap = memberService.getMemberProfile();

		Map<String, Object> responseData = new HashMap<>();

		responseData.put("session_Id", member_Id);
		responseData.put("trending_profileMap", profilemap);
		responseData.put("trending_postList", hottestFeed);
		responseData.put("trending_replyMap", replymap);
		responseData.put("hashMap", hashmap);

		return ResponseEntity.ok(responseData);
	}

	// Contact Form
	@GetMapping("/contact")
	public String contactPage(HttpSession session, Model model) {

		String member_Id = ((MemberVO) session.getAttribute("loginUser")).getMember_Id();

		if(session.getAttribute("loginUser") == null) {
			model.addAttribute("message", "로그인을 해주세요");
			return "login";
		} else {

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

			// 우측 Hottest Feed
			List<PostVO> hottestFeed = postService.getHottestFeed();
			model.addAttribute("hottestFeed", hottestFeed);

			// 내가 작성한 qna
			List<QnaVO> qnaList = qnaService.getMyQna(member_Id);

			for(int i = 0; i < qnaList.size(); i++) {
				qnaList.get(i).setQna_Message(qnaList.get(i).getQna_Message().replace("\n", "<br>"));
				if(qnaList.get(i).getQna_Done().equals("2")) {
					qnaList.get(i).setQna_Answer(qnaList.get(i).getQna_Answer().replace("\n", "<br>"));
				} else {

				}
			}

			model.addAttribute("profileImage", profileImage);
			model.addAttribute("qnaList", qnaList);
			model.addAttribute("alarmList", alarmList);
			model.addAttribute("alarmListSize", alarmListSize);

			return "contact";

		}
	}

	// Contact Form
	@GetMapping("/alarmContact")
	public String alarmContact(HttpSession session, Model model, @RequestParam("alarm_Seq") int alarm_Seq) {

		String member_Id = ((MemberVO) session.getAttribute("loginUser")).getMember_Id();

		if(session.getAttribute("loginUser") == null) {
			model.addAttribute("message", "로그인을 해주세요");
			return "login";
		} else {

			alarmService.deleteAlarm(alarm_Seq);

			String session_Id = ((MemberVO) session.getAttribute("loginUser")).getMember_Id();

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

			model.addAttribute("alarmList", alarmList);
			model.addAttribute("alarmListSize", alarmListSize);

			// 우측 Hottest Feed
			List<PostVO> hottestFeed = postService.getHottestFeed();
			model.addAttribute("hottestFeed", hottestFeed);
			// 내가 작성한 qna
			List<QnaVO> qnaList = qnaService.getMyQna(member_Id);
			model.addAttribute("qnaList", qnaList);


			return "contact";
		}
	}

	@PostMapping("/qna")
	public String qnaSending(@RequestParam("member_Id") String member_Id,
							 @RequestParam("qna_Title") String qna_Title,
							 @RequestParam("qna_Message") String qna_Message, HttpSession session, Model model) {

		if(session.getAttribute("loginUser") == null) {
			model.addAttribute("message", "로그인을 해주세요");
			return "login";
		} else {
			QnaVO vo = new QnaVO();
			int qna_Seq = qnaService.checkMaxSeq() + 1;
			vo.setQna_Seq(qna_Seq);
			vo.setMember_Id(member_Id);

			vo.setQna_Title(qna_Title.replace("<", "{"));
			vo.setQna_Message(qna_Message.replace("<", "{"));
			System.out.println("/qna 잡아옴 qnaVO : " + vo);
			qnaService.insertQna(vo);

			return "redirect:contact";
		}
	}

	// index 페이지 로드
	@GetMapping("/feedInfinite")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> feedInfinite(HttpSession session) {

		String member_Id = ((MemberVO) session.getAttribute("loginUser")).getMember_Id();

		/* index페이지의 뉴스피드 부분 */
		// 자신, 팔로잉한 사람들의 게시글을 담는부분
		ArrayList<PostVO> postlist = postService.getlistPost(member_Id);
		for(int i = 0; i < postlist.size(); i++) {
			postlist.get(i).setPost_Content(postlist.get(i).getPost_Content().replace("\n", "<br>"));
		}

		// 각 post_seq에 대한 댓글들을 매핑할 공간.
		Map<Integer, ArrayList<ReplyVO>> replymap = new HashMap<>();

		// 각 post_seq에 대한 해시태그들을 매핑할 공간.
		Map<Integer, ArrayList<TagVO>> hashmap = new HashMap<>();

		// 정렬된 postlist의 인덱스 순으로 댓글 리스트를 매핑함.
		// 동시에 각 게시글의 좋아요 카운트와 댓글 카운트를 저장.
		for(int i=0; i<postlist.size(); i++) {
			// 자신, 팔로잉한 사람들의 게시글의 post_seq를 불러온다.
			int post_Seq = postlist.get(i).getPost_Seq();

			// i번째 게시글의 댓글 리스트를 담음
			ArrayList<ReplyVO> replylist = replyService.getReplyPreview(post_Seq);

			// i번째 게시글의 댓글 좋아요 여부 체크
			for(int k = 0; k < replylist.size(); k++) {
				String realReply_Member_Id = replylist.get(k).getMember_Id();
				ReplyVO KVO = replylist.get(k);
				LikeVO check = new LikeVO();
				check.setMember_Id(member_Id);
				check.setPost_Seq(KVO.getPost_Seq());
				check.setReply_Seq(KVO.getReply_Seq());
				String reply_LikeYN = replyService.getCheckReplyLike(check);
				replylist.get(k).setReply_LikeYN(reply_LikeYN);
				replylist.get(k).setMember_Id(realReply_Member_Id);
			}

			// i번째의 게시글의 댓글을 map에 매핑하는 작업
			replymap.put(i, replylist);

			// i번째 게시글의 좋아요 여부 체크
			PostVO voForLikeYN = new PostVO();
			voForLikeYN.setMember_Id(member_Id);
			voForLikeYN.setPost_Seq(post_Seq);
			String post_LikeYN = postService.getLikeYN(voForLikeYN);
			postlist.get(i).setPost_LikeYN(post_LikeYN);

			// i번째 게시글의 해시태그 체크    hashmap
			ArrayList<TagVO> hash = postService.getHashtagList(post_Seq);
			hashmap.put(post_Seq, hash);
		}

		// 전체 회원 프로필 이미지 조회
		HashMap<String, String> profilemap = memberService.getMemberProfile();

		Map<String, Object> responseData = new HashMap<>();

		responseData.put("profileMap", profilemap);
		responseData.put("postList", postlist);
		responseData.put("replyMap", replymap);
		responseData.put("session_Id", member_Id);
		responseData.put("hashMap", hashmap);

		return ResponseEntity.ok(responseData);

	}

	@PostMapping("/deleteAlarm")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteAlarm(@RequestBody Map<String, Integer> requestBody){

		int alarm_Seq = requestBody.get("alarm_Seq");

		alarmService.deleteAlarm(alarm_Seq);

		Map<String, Object> responseData = new HashMap<>();

		responseData.put("message", "알람 삭제 성공");

		return ResponseEntity.ok(responseData);
	}

	@GetMapping("/alarmIndex")
	public String alarmIndex(Model model, HttpSession session,@RequestParam("post_Seq") int P_Seq, @RequestParam("alarm_Seq") int alarm_Seq) {

		if(session.getAttribute("loginUser") == null) {
			model.addAttribute("message", "로그인을 해주세요");
			return "login";
		} else {

			// 클릭한 알람을 삭제
			alarmService.deleteAlarm(alarm_Seq);

			/* index페이지의 팔로우 부분 */
			String member_Id = ((MemberVO) session.getAttribute("loginUser")).getMember_Id();

			List<MemberVO> recommendMember = memberService.getRecommendMember(member_Id);

			List<PostVO> hottestFeed = postService.getHottestFeed();

			// 알람 리스트를 담는 부분
			List<AlarmVO> alarmList = alarmService.getAllAlarm(member_Id);

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

			/* index페이지의 뉴스피드 부분 */
			// 자신, 팔로잉한 사람들의 게시글을 담는부분
			ArrayList<PostVO> postlist = new ArrayList<PostVO>();

			postlist.add(postService.selectPostDetail(P_Seq));

			// 각 post_seq에 대한 댓글들을 매핑할 공간.
			Map<Integer, ArrayList<ReplyVO>> replymap = new HashMap<>();

			// 각 post_seq에 대한 해시태그들을 매핑할 공간.
			Map<Integer, ArrayList<TagVO>> hashmap = new HashMap<>();

			// 정렬된 postlist의 인덱스 순으로 댓글 리스트를 매핑함.
			// 동시에 각 게시글의 좋아요 카운트와 댓글 카운트를 저장.
			for(int i=0; i<postlist.size(); i++) {
				// 자신, 팔로잉한 사람들의 게시글의 post_seq를 불러온다.
				int post_Seq = postlist.get(i).getPost_Seq();

				// i번째 게시글의 댓글 리스트를 담음
				ArrayList<ReplyVO> replylist = replyService.getReplyPreview(post_Seq);
				// i번째 게시글의 댓글 좋아요 여부 체크
				for(int k = 0; k < replylist.size(); k++) {
					String realReply_Member_Id = replylist.get(k).getMember_Id();
					ReplyVO KVO = replylist.get(k);
					LikeVO check = new LikeVO();
					check.setMember_Id(member_Id);
					check.setPost_Seq(KVO.getPost_Seq());
					check.setReply_Seq(KVO.getReply_Seq());
					String reply_LikeYN = replyService.getCheckReplyLike(check);
					replylist.get(k).setReply_LikeYN(reply_LikeYN);
					replylist.get(k).setMember_Id(realReply_Member_Id);
				}

				// i번째의 게시글의 댓글을 map에 매핑하는 작업
				replymap.put(i, replylist);

				// i번째 게시글의 좋아요 여부 체크
				PostVO voForLikeYN = new PostVO();
				voForLikeYN.setMember_Id(member_Id);
				voForLikeYN.setPost_Seq(post_Seq);
				String post_LikeYN = postService.getLikeYN(voForLikeYN);
				postlist.get(i).setPost_LikeYN(post_LikeYN);

				// i번째 게시글의 해시태그 체크    hashmap
				ArrayList<TagVO> hash = postService.getHashtagList(post_Seq);
				hashmap.put(post_Seq, hash);
			}

			// 전체 회원 프로필 이미지 조회
			HashMap<String, String> profilemap = memberService.getMemberProfile();

			model.addAttribute("alarmList", alarmList);
			model.addAttribute("alarmListSize", alarmListSize);
			model.addAttribute("profileMap", profilemap);
			model.addAttribute("postList", postlist);
			model.addAttribute("replyMap", replymap);
			model.addAttribute("recommendMember", recommendMember);
			model.addAttribute("hottestFeed", hottestFeed);
			model.addAttribute("member_Id", member_Id);
			model.addAttribute("hashMap", hashmap);

			return "index";
		}

	}

	@GetMapping("/deleteQna")
	public String deleteQna(@RequestParam("qna_Seq") int qna_Seq, HttpSession session, Model model) {
		if(session.getAttribute("loginUser") == null) {
			model.addAttribute("message", "로그인을 해주세요");
			return "login";
		} else {
			qnaService.deleteQna(qna_Seq);

			return "redirect:contact";
		}
	}

}
