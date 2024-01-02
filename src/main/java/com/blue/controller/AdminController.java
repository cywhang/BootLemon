package com.blue.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.blue.dto.AlarmVO;
import com.blue.dto.MemberVO;
import com.blue.dto.PostVO;
import com.blue.dto.QnaVO;
import com.blue.dto.ReplyVO;
import com.blue.dto.TagVO;
import com.blue.service.AlarmService;
import com.blue.service.MemberService;
import com.blue.service.PostService;
import com.blue.service.QnaService;
import com.blue.service.ReplyService;

import javax.servlet.http.HttpSession;

@Controller
//컨트롤러에서 'loginUser', 'profileMap' 이라는 이름으로 모델 객체를 생성할때 세션에 동시에 저장한다.
@SessionAttributes({"loginUser", "profileMap"})
public class AdminController {

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

	// index 페이지 로드
	@GetMapping("/admin_Index")
	public String getRecommendMember(Model model, HttpSession session) {

		if(((MemberVO) session.getAttribute("loginUser")).getMember_Id().equals("admin")) {

			// 회원 가입 추세
			List<Integer> memberTendencyDayByDay = memberService.getMemberTendency();
			List<Integer> memberTendency = new ArrayList<Integer>();
			int sum = 0;
			for(int i = 0; i < memberTendencyDayByDay.size(); i++) {
				sum += memberTendencyDayByDay.get(i);
				memberTendency.add(sum);
			}

			// 오늘의 해시태그
			List<TagVO> todaysTag = postService.getTodaysTag();
			List<String> todaysTagContent = new ArrayList<String>();
			List<String> todaysTagPercent = new ArrayList<String>();
			double totalCount = 0;

			for(int i = 0; i < todaysTag.size(); i++) {
				totalCount += todaysTag.get(i).getTag_Count();
			}

			for(int i = 0; i < todaysTag.size(); i++) {
				// todaysTag.get(i).getTag_Content()을 "todaysTag.get(i).getTag_Content()"로 만들기
				String temp = "\"" + todaysTag.get(i).getTag_Content() + "\"";
				todaysTagContent.add(temp);
				double div = (todaysTag.get(i).getTag_Count() / totalCount) * 100;
				String divResult = String.format("%.2f", div);
				todaysTagPercent.add(divResult);
			}

			model.addAttribute("memberTendency", memberTendency);
			model.addAttribute("todaysTagContent",todaysTagContent);
			model.addAttribute("todaysTagPercent", todaysTagPercent);

			return "admin_Index";
		} else {

			model.addAttribute("message", "관리자로 로그인 해주세요");

			return "login";
		}
	}

	@GetMapping("/member_Table")
	public String member_Table(Model model, HttpSession session) throws ParseException {

		if(((MemberVO) session.getAttribute("loginUser")).getMember_Id().equals("admin")) {

			List<MemberVO> allMember = memberService.getAllMember();

			for(int i = 0; i < allMember.size(); i++) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String joinDate = sdf.format(allMember.get(i).getMember_Join_Date());
				allMember.get(i).setMember_Join_Date_String(joinDate);
			}

			model.addAttribute("allMember", allMember);

			return "member_Table";
		} else {

			model.addAttribute("message", "관리자로 로그인 해주세요");

			return "login";
		}
	}

	@GetMapping("/member_Delete_By_Admin")
	public String member_Delete_By_Admin(String member_Id, HttpSession session, Model model) {
		if(((MemberVO) session.getAttribute("loginUser")).getMember_Id().equals("admin")) {
			postService.deleteOneMemsTag(member_Id);
			memberService.deleteMember(member_Id);
			return "redirect:member_Table";
		} else {
			model.addAttribute("message", "관리자로 로그인 해주세요");
			return "login";
		}
	}

	@GetMapping("/post_Table")
	public String post_Table(Model model, HttpSession session) {
		if(((MemberVO) session.getAttribute("loginUser")).getMember_Id().equals("admin")) {
			// 모든 회원의 게시글을 담는부분
			ArrayList<PostVO> postlist = postService.getAllPost();

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

				// i번째의 게시글의 댓글을 map에 매핑하는 작업
				replymap.put(i, replylist);

				// i번째 게시글의 해시태그 체크    hashmap
				ArrayList<TagVO> hash = postService.getHashtagList(post_Seq);
				hashmap.put(post_Seq, hash);
			}

			// 전체 회원 프로필 이미지 조회
			HashMap<String, String> profilemap = memberService.getMemberProfile();

			int postListSize = postlist.size();

			model.addAttribute("profileMap", profilemap);
			model.addAttribute("postList", postlist);
			model.addAttribute("hashMap", hashmap);
			model.addAttribute("postListSize", postListSize);

			return "post_Table";
		} else {
			model.addAttribute("message", "관리자로 로그인 해주세요");
			return "login";
		}
	}

	// 관리자 페이지에서 게시글 상세보기
	@GetMapping("/post_Detail")
	public String post_detail(Model model, int post_Seq, HttpSession session) {
		if(((MemberVO) session.getAttribute("loginUser")).getMember_Id().equals("admin")) {

			// PostVO 에 post_seq에 대한 게시글을 담는다.
			PostVO postDetail = postService.selectPostDetail(post_Seq);
			postDetail.setPost_Content(postDetail.getPost_Content().replace("\n", "<br>"));

			// ReplyVO 에 post_seq에 대한 댓글 담는다.
			ArrayList<ReplyVO> replyList = replyService.getListReply(post_Seq);

			// TagVO 에 post_seq에 대한 해시태그를 담는다.
			ArrayList<TagVO> hash = postService.getHashtagList(post_Seq);

			model.addAttribute("post", postDetail);
			model.addAttribute("reply", replyList);
			model.addAttribute("hash", hash);

			return "post_Detail";
		} else {
			model.addAttribute("message", "관리자로 로그인 해주세요");
			return "login";
		}
	}

	// 게시글 삭제 (관리자용 -> 삭제 후 관리자 페이지에 머뭄)
	@GetMapping("/deletePost")
	public String deletePost(@RequestParam(value="post_Seq") int post_Seq, HttpSession session, Model model) {
		if(((MemberVO) session.getAttribute("loginUser")).getMember_Id().equals("admin")) {
			postService.deletePost(post_Seq);

			return "redirect:post_Table";
		} else {
			model.addAttribute("message", "관리자로 로그인 해주세요");
			return "login";
		}
	}

	@GetMapping("/qna_Table")
	public String qna_Table(Model model, HttpSession session) {
		if(((MemberVO) session.getAttribute("loginUser")).getMember_Id().equals("admin")) {

			List<QnaVO> allQna = qnaService.getAllQna();
			model.addAttribute("allQna", allQna);
			return "qna_Table";
		} else {
			model.addAttribute("message", "관리자로 로그인 해주세요");
			return "login";
		}
	}

	@GetMapping("qna_Detail")
	public String qna_Detail(Model model, int qna_Seq, HttpSession session) {
		if(((MemberVO) session.getAttribute("loginUser")).getMember_Id().equals("admin")) {
			QnaVO qnaDetail = qnaService.getQnaDetail(qna_Seq);
			qnaDetail.setQna_Message(qnaDetail.getQna_Message().replace("\n", "<br>"));
			if(qnaDetail.getQna_Done().equals("2")) {
				qnaDetail.setQna_Answer(qnaDetail.getQna_Answer().replace("\n", "<br>"));
			} else {

			}
			model.addAttribute("qnaDetail", qnaDetail);
			return "qna_Detail";
		} else {
			model.addAttribute("message", "관리자로 로그인 해주세요");
			return "login";
		}
	}

	@PostMapping("/qna_Answer")
	public String qnaAnswer(@RequestParam("qna_Answer") String qna_Answer,
							@RequestParam("qna_Seq") Integer qna_Seq,
							@RequestParam("member_Id") String member_Id, HttpSession session, Model model) {
		if(((MemberVO) session.getAttribute("loginUser")).getMember_Id().equals("admin")) {
			QnaVO voForUpdate = qnaService.getQnaDetail(qna_Seq);
			voForUpdate.setQna_Answer(qna_Answer);
			qnaService.updateQnaAnswer(voForUpdate);
			AlarmVO alarmVO = new AlarmVO();
			alarmVO.setKind(5);
			alarmVO.setFrom_Mem("admin");
			alarmVO.setTo_Mem(member_Id);
			alarmVO.setPost_Seq(0);
			alarmVO.setReply_Seq(0);
			alarmService.insertAlarm(alarmVO);
			return "redirect:qna_Detail?qna_Seq=" + qna_Seq;
		} else {
			model.addAttribute("message", "관리자로 로그인 해주세요");
			return "login";
		}
	}

	@GetMapping("/deleteQna_ByAdmin")
	public String deleteQna_ByAdmin(@RequestParam("qna_Seq") int qna_Seq, HttpSession session, Model model) {
		if(session.getAttribute("loginUser") == null) {
			model.addAttribute("message", "로그인을 해주세요");
			return "login";
		} else {
			qnaService.deleteQna(qna_Seq);
			return "qna_Table";
		}
	}
}
