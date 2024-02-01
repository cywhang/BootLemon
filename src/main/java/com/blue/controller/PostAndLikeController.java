package com.blue.controller;

import java.io.File;
import java.io.IOException;
import java.util.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.blue.dto.AlarmVO;
import com.blue.dto.LikeVO;
import com.blue.dto.MemberVO;
import com.blue.dto.PostVO;
import com.blue.dto.ReplyVO;
import com.blue.dto.TagVO;
import com.blue.service.AlarmService;
import com.blue.service.MemberService;
import com.blue.service.PostService;
import com.blue.service.ReplyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpSession;

@Controller
//컨트롤러에서 'loginUser', 'profileMap' 이라는 이름으로 모델 객체를 생성할때 세션에 동시에 저장한다.
@SessionAttributes({"loginUser", "profileMap"})
public class PostAndLikeController {

	@Autowired
	private PostService postService;
	@Autowired
	private ReplyService replyService;
	@Autowired
	private MemberService memberService;
	@Autowired
	private AlarmService alarmService;

	// 좋아요 변경(PostMapping)
	@PostMapping("/changeLike")
	@ResponseBody
	public String changeLike(@RequestBody Map<String, Integer> requestBody, HttpSession session) {
		int post_Seq = requestBody.get("post_Seq");
		String member_Id = ((MemberVO) session.getAttribute("loginUser")).getMember_Id();
		try {
			LikeVO vo = new LikeVO();
			vo.setMember_Id(member_Id);
			vo.setPost_Seq(post_Seq);

			postService.changeLike(vo);
			return "success";
		} catch (Exception e) {
			// JSON 파싱 오류 처리
			e.printStackTrace();
			return "error";
		}
	}

	// 미리보기 댓글 좋아요 변경
	@PostMapping("/changeReplyLike")
	@ResponseBody
	public String changeReplyLike(@RequestBody Map<String, Integer> requestBody, HttpSession session) {
		int post_Seq = requestBody.get("post_Seq");
		int reply_Seq = requestBody.get("reply_Seq");
		String member_Id = ((MemberVO) session.getAttribute("loginUser")).getMember_Id();
		try {
			LikeVO vo = new LikeVO();
			vo.setMember_Id(member_Id);
			vo.setPost_Seq(post_Seq);
			vo.setReply_Seq(reply_Seq);

			replyService.changeReplyLike(vo);
			return "success";
		} catch (Exception e) {
			// JSON 파싱 오류 처리
			e.printStackTrace();
			return "error";
		}
	}

	// 게시글 작성
	@PostMapping("insertPost")
	// @ResponseBody
	public String insertPost(PostVO vo, @RequestParam(value="attach_file", required = false) MultipartFile[] attach_file,
							 @RequestParam(value = "fileList[]", required = false) String[] fileList,
							 HttpSession session) {
		System.out.println("==================================게시글 작성=====================================");

		// MAX(post_Seq) + 1
		int nextSeq = postService.postSeqCheck();
		if(nextSeq == 0) {
			nextSeq = 1;
		}
		// nextval을 사용하지않고 강제로 시퀀스를 주입하기위한 postVO에 set해준다
		vo.setPost_Seq(nextSeq);

		// 바뀐 순서정보를 담는부분
		Map<Integer, Integer> index = new HashMap<>();

		// 1. 사용자가 이미지를 첨부했을 때
		if (attach_file != null && attach_file.length > 0) {

			// 1. 이미지 업로드 처리 부분
			String folderPath = "/home/ubuntu/fileUpload/img/uploads/post/";
			// 1. 업로드할 이미지 개수 vo 객체에 저장
			int imgCount = attach_file.length;
			vo.setPost_Image_Count(imgCount);


			if(imgCount == 0) { // 이미지를 업로드 하지 않았을때

			} else if (imgCount == 1 ){ // 1개의 이미지를 업로드 했을때
				MultipartFile file = attach_file[0];
				String fileName = nextSeq + "-" + 1 + ".png";
				try {
					// 파일을 지정된 경로에 저장
					file.transferTo(new File(folderPath + fileName));
				} catch (IOException e) {
					e.printStackTrace();
				}

			} else { // 2개 이상의 이미지를 업로드 했을때

				if(fileList != null) { // 이미지 순서 변경이 있을 경우
					for(int k=0; k < fileList.length; k++) {
						String file = fileList[k];
						int aa = Integer.parseInt(file.substring(4));
						index.put(k+1 , aa);
					}

					for(int i=1; i < (imgCount+1); i++) {
						int real = index.get(i);
						MultipartFile file = attach_file[real];
						String fileName = nextSeq + "-" + i + ".png";

						try {
							// 파일을 지정된 경로에 저장
							file.transferTo(new File(folderPath + fileName));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} else {  // 이미지 순서 변경이 없을 경우
					for(int j=1; j<imgCount+1; j++) {
						index.put(j, j);
					}

					for(int i=1; i < (imgCount+1); i++) {
						int real = (index.get(i)-1);
						MultipartFile file = attach_file[real];
						String fileName = nextSeq + "-" + i + ".png";

						try {
							// 파일을 지정된 경로에 저장
							file.transferTo(new File(folderPath + fileName));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		// 2. 게시글의 공개여부를 체크하지 않았다면 n값으로 set
		if (vo.getPost_Public() == null) {
			vo.setPost_Public("n");
		}

		// 3. 인서트 처리
		postService.insertPost(vo);

		// 4. 해시태그 처리 부분
		String hashTag = vo.getPost_Hashtag();
		if (hashTag != null && !hashTag.isEmpty()) {

			try { // 2-1. 사용자가 입력한 해시태그들을 json형태로 받아와서 사용할 수 있게 파싱하는 작업
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode jsonNode = objectMapper.readTree(hashTag);

				for (JsonNode node : jsonNode) {

					// n번째 해시태그 내용
					String value = node.get("value").asText();
					// 특수문자 변환
					value = value.replace("<", "《").replace(">", "》").replace("#", "");

					TagVO tvo = new TagVO();
					tvo.setPost_Seq(nextSeq);
					tvo.setTag_Content(value);
					postService.insertTag(tvo);
				}
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}

		return "redirect:index";
	}

	// 게시글 상세보기 페이지 (모달창)
	@GetMapping("/modal")
	@ResponseBody
	public Map<String, Object> modal(@RequestParam int post_Seq, HttpSession session) {
		// 0. 세션에 저장되어있는 아이디 불러옴
		String member_Id = ((MemberVO) session.getAttribute("loginUser")).getMember_Id();

		// 0. ajax요청에 대한 response값 전달을 위한 Map 변수 선언
		Map<String, Object> dataMap = new HashMap<>();

		// 1. 게시글 상세페이지에서 클릭한 게시글의 정보를 출력하기 위한 PostVO 값 저장
		PostVO postInfo = postService.getpostDetail(post_Seq);

		// 2. 게시글의 댓글리스트를 출력하기 위한 ArrayList<ReplyVO> 값 저장
		ArrayList<ReplyVO> replyList = replyService.getListReply(post_Seq);

		// 3. 전체 회원 프로필 이미지 조회
		HashMap<String, String> profileMap = (HashMap<String, String>) session.getAttribute("profileMap");

		// 4. 게시글의 좋아요 여부 체크
		// 조회를 위한 객체 생성
		PostVO LikeYN = new PostVO();
		LikeYN.setMember_Id(member_Id);
		LikeYN.setPost_Seq(post_Seq);

		// 조회 결과 담음
		String post_LikeYN = postService.getLikeYN(LikeYN);
		postInfo.setPost_LikeYN(post_LikeYN);

		// 5. 게시글 댓글의 좋아요 여부 체크
		for(int i=0; i<replyList.size(); i++) {
			// 조회를 위한 객체 생성
			LikeVO replyLikeYN = new LikeVO();
			replyLikeYN.setMember_Id(member_Id);
			replyLikeYN.setPost_Seq(post_Seq);
			replyLikeYN.setReply_Seq(replyList.get(i).getReply_Seq());

			// 조회 결과 담음
			String reply_LikeYN = replyService.getCheckReplyLike(replyLikeYN);
			replyList.get(i).setReply_LikeYN(reply_LikeYN);
		}

		// 6. 게시글의 해시태그
		ArrayList<TagVO> hashTag = postService.getHashtagList(post_Seq);

		// 게시글 상세정보 VO
		dataMap.put("post", postInfo);
		// 게시글의 댓글 리스트
		dataMap.put("replies", replyList);
		// 게시글의 해시태그
		dataMap.put("hashtag", hashTag);
		// 전체 회원의 프로필 이미지
		dataMap.put("profile", profileMap);
		// 로그인되어있는 아이디
		dataMap.put("member_Id", member_Id);

		return dataMap;
	}


	// 게시글 상세보기 페이지 (댓글 리스트, 본글 내용)
	@GetMapping("/replyModal")
	@ResponseBody
	public Map<String, Object> modalReply(@RequestParam int post_Seq, HttpSession session) {
		// 0. 세션에 저장되어있는 아이디 불러옴
		String member_Id = ((MemberVO) session.getAttribute("loginUser")).getMember_Id();

		// 0. ajax요청에 대한 response값 전달을 위한 Map 변수 선언
		Map<String, Object> dataMap = new HashMap<>();

		// 1. 게시글 상세페이지에서 클릭한 게시글의 정보를 출력하기 위한 PostVO 값 저장
		PostVO postInfo = postService.getpostDetail(post_Seq);

		// 2. 게시글의 댓글리스트를 출력하기 위한 ArrayList<ReplyVO> 값 저장
		ArrayList<ReplyVO> replyList = replyService.getListReply(post_Seq);

		// 3. 전체 회원 프로필 이미지 조회
		HashMap<String, String> profileMap = (HashMap<String, String>) session.getAttribute("profileMap");

		// 4. 게시글의 좋아요 여부 체크
		// 조회를 위한 객체 생성
		PostVO LikeYN = new PostVO();
		LikeYN.setMember_Id(member_Id);
		LikeYN.setPost_Seq(post_Seq);

		// 5. 게시글의 해시태그
		ArrayList<TagVO> hashTag = postService.getHashtagList(post_Seq);

		// 조회 결과 담음
		String post_LikeYN = postService.getLikeYN(LikeYN);
		postInfo.setPost_LikeYN(post_LikeYN);

		// 5. 게시글 댓글의 좋아요 여부 체크
		for(int i=0; i<replyList.size(); i++) {
			// 조회를 위한 객체 생성
			LikeVO replyLikeYN = new LikeVO();
			replyLikeYN.setMember_Id(member_Id);
			replyLikeYN.setPost_Seq(post_Seq);
			replyLikeYN.setReply_Seq(replyList.get(i).getReply_Seq());

			// 조회 결과 담음
			String reply_LikeYN = replyService.getCheckReplyLike(replyLikeYN);
			replyList.get(i).setReply_LikeYN(reply_LikeYN);
		}

		// 게시글 상세정보 VO
		dataMap.put("post", postInfo);
		// 게시글의 댓글 리스트
		dataMap.put("replies", replyList);
		// 게시글의 해시태그
		dataMap.put("hashtag", hashTag);
		// 전체 회원의 프로필 이미지
		dataMap.put("profile", profileMap);
		// 로그인되어있는 아이디
		dataMap.put("member_Id", member_Id);

		return dataMap;
	}

	// 댓글 인서트
	@GetMapping("/insertReply")
	@ResponseBody
	public Map<String, Object> insertReply(@RequestParam("post_Seq") int post_Seq,
										   @RequestParam("reply_Content") String reply_Content, HttpSession session) {

		// 0. ajax요청에 대한 response값 전달을 위한 Map 변수 선언
		Map<String, Object> dataMap = new HashMap<>();

		// 1. 세션객체인 'loginUfser'객체를 MemberVO 객체로 강제 파싱해서 getter 메소드인 getMember_Id를 호출해 아이디를 가져온다.
		String member_Id = ((MemberVO) session.getAttribute("loginUser")).getMember_Id();

		// 2. insert쿼리문에 파라미터 객체로 보낼 변수선언
		// 알림 등록을 동시에 진행하기 위해 reply_Seq도 nextVal에서 강제주입으로 변경
		int next_Reply_Seq = 0;
		next_Reply_Seq = replyService.getMaxReply_Seq() + 1;

		ReplyVO rep = new ReplyVO();
		rep.setReply_Seq(next_Reply_Seq);
		rep.setMember_Id(member_Id);
		rep.setPost_Seq(post_Seq);
		rep.setReply_Content(reply_Content);

		// 3. insert쿼리문 실행
		replyService.insertReply(rep);

		// 알람
		String postWriter = postService.getPostWriter(post_Seq);

		AlarmVO alarmVO = new AlarmVO();
		alarmVO.setKind(3);
		alarmVO.setFrom_Mem(member_Id);
		alarmVO.setTo_Mem(postWriter);
		alarmVO.setPost_Seq(post_Seq);
		alarmVO.setReply_Seq(next_Reply_Seq);

		alarmService.insertAlarm(alarmVO);

		// 4. 게시글의 댓글리스트를 출력하기 위한 ArrayList<ReplyVO> 값 저장
		ArrayList<ReplyVO> replyList = replyService.getListReply(post_Seq);

		// 5. 전체 회원의 이미지 Map을 세션에서 받아옴
		HashMap<String, String> profileMap = (HashMap<String, String>) session.getAttribute("profileMap");

		// 6. 해당 게시글의 상세정보를 조회해서 가져옴(게시글의 댓글 카운트 변경을 위함)
		PostVO postInfo = postService.getpostDetail(post_Seq);

		// 7. 게시글 댓글의 좋아요 여부 체크
		for(int i=0; i<replyList.size(); i++) {

			// 조회를 위한 객체 생성
			LikeVO replyLikeYN = new LikeVO();
			replyLikeYN.setMember_Id(member_Id);
			replyLikeYN.setPost_Seq(post_Seq);
			replyLikeYN.setReply_Seq(replyList.get(i).getReply_Seq());

			// 조회 결과 담음
			String reply_LikeYN = replyService.getCheckReplyLike(replyLikeYN);
			replyList.get(i).setReply_LikeYN(reply_LikeYN);
		}

		// 8. ajax의 응답성공(success)의 response로 들어갈 값들 매핑
		dataMap.put("postInfo", postInfo);
		dataMap.put("replies", replyList);
		dataMap.put("profile", profileMap);
		dataMap.put("member_Id", member_Id);
		dataMap.put("reply_Content", reply_Content);

		return dataMap;
	}

	// 게시글 삭제
	@GetMapping("/postDelete")
	public String postDelete(@RequestParam(value="post_Seq") int post_Seq, HttpSession session) {

		postService.deletePost(post_Seq);
		// 1. 이미지 업로드 실제경로
		String folderPath = "/home/ubuntu/fileUpload/img/uploads/post/";
		// 절대경로의 이미지 전체를 folder에 저장한다
		File folder = new File(folderPath);
		// folder의 파일들을 리스트화 시킨다.
		File[] files = folder.listFiles();

		if (files != null) {
			for (File file : files) {
				// 파일 경로에서 파일 이름을 추출합니다.
				String fileName = file.getPath().substring(file.getPath().lastIndexOf('\\') + 1);

				// 파일 이름을 "-"를 기준으로 분리합니다.
				String[] parts = fileName.split("-");

				// 파일 이름이 적어도 두 부분으로 이루어져 있고, 첫 번째 부분이 post_Seq와 일치하는지 확인합니다.
				if (parts.length >= 2 && parts[0].equals(String.valueOf(post_Seq))) {
					// 파일을 삭제합니다.
					if (file.delete()) {
						// 파일 삭제가 성공한 경우, 성공 메시지를 출력합니다.
					} else {
						// 파일 삭제가 실패한 경우, 실패 메시지를 출력합니다.
					}
				}
			}
		} else {
			// 기존 이미지 없음
		}
		return "redirect:/index";
	}

	// 게시글 수정 View
	@GetMapping("/postEditView")
	@ResponseBody
	public Map<String, Object> postEditView(HttpSession session, @RequestParam("post_Seq") int post_Seq){

		// ajax요청에 대한 응답을 보내기 위한 객체 생성
		Map<String, Object> dataMap = new HashMap<>();

		// 게시글 상세정보 얻어옴
		PostVO postInfo = postService.getpostDetail(post_Seq);

		// 해시태그 리스트
		ArrayList<TagVO> hashlist = postService.getHashtagList(post_Seq);

		dataMap.put("hashList", hashlist);
		dataMap.put("post", postInfo);
		return dataMap;
	}

	// 게시글 수정 Action
	@PostMapping("postEditAction")
	public String postEditAction(PostVO vo, @RequestParam(value = "editAttach_file", required = false) MultipartFile[] attach_file,
								 			@RequestParam(value = "deletedStrings", required = false) String[] deletedStrings,
								 			@RequestParam(value = "alreadyFileNo", required = false) int alreadyFileNo,
								 			@RequestParam(value = "currentEditFileNo", required = false) int currentEditFileNo,
								 			HttpSession session, int post_Seq) {



		vo.setPost_Seq(post_Seq);
		int deleteStrings = deletedStrings.length;

		// 1. 이미지 업로드 실제경로
		String folderPath = "/home/ubuntu/fileUpload/img/uploads/post/";
		int imgCount = (attach_file != null) ? attach_file.length : 0;
		vo.setPost_Image_Count(currentEditFileNo);

		for (int i = 0; i < deletedStrings.length; i++) {
			System.out.println(deletedStrings[i]);
		}

		System.out.println("alreadyFileNo: " + alreadyFileNo);
		System.out.println("currentEditFileNo: " + currentEditFileNo);


		if(imgCount == 0) { // 수정폼 제출시 이미지가 없을때

			// 기존 파일 삭제
			// 절대경로의 이미지 전체를 folder에 저장한다
			File folder = new File(folderPath);
			// folder의 파일들을 리스트화 시킨다.
			File[] files = folder.listFiles();

			// 기존이미지를 삭제했을때
			if (files != null && deletedStrings.length > 0) {
				for (String deletedString : deletedStrings) {
					String fileName = deletedString.substring(deletedString.lastIndexOf('/') + 1);
					String absoluteFilePath = folderPath + fileName;
					File fileToDelete = new File(absoluteFilePath);
					if (fileToDelete.delete()) {
//			            System.out.println("파일 삭제: " + fileName);

						// 삭제된 파일 이후의 파일들 이름 변경
						int deletedIndex = Integer.parseInt(fileName.split("-")[1].split("\\.")[0]);
						for (int i = deletedIndex + 1; i <= files.length; i++) {
							String originalFilePath = folderPath + post_Seq + "-" + i + ".png";
							String newFilePath = folderPath + post_Seq + "-" + (i - 1) + ".png";
							File originalFile = new File(originalFilePath);
							File newFile = new File(newFilePath);
							if (originalFile.renameTo(newFile)) {
//			                    System.out.println("파일 이름 변경: " + originalFilePath + " -> " + newFilePath);
							} else {
//			                    System.out.println("파일 이름 변경 실패: " + originalFilePath);
							}
						}
					} else {
//			            System.out.println("파일 삭제 실패: " + fileName);
					}
				}
			} else {
//				System.out.println("기존 이미지 없음");
			}

		} else if (imgCount == 1 ){ // 수정폼 제출시 이미지가 1개 일때
			// 실제 파일  설정 부분
			MultipartFile file = attach_file[0];
			String fileName = post_Seq + "-" + (alreadyFileNo-deleteStrings+1) + ".png";
//			System.out.println(fileName);
//			System.out.println("File Name: " + file.getOriginalFilename());

			// 기존파일 삭제 유무 체크후 처리
			File folder = new File(folderPath);
			File[] files = folder.listFiles();
			if (files != null && deletedStrings.length > 0) {
				for (String deletedString : deletedStrings) {
					String alreadyFileName = deletedString.substring(deletedString.lastIndexOf('/') + 1);
					String absoluteFilePath = folderPath + alreadyFileName;
					File fileToDelete = new File(absoluteFilePath);
					if (fileToDelete.delete()) {
//			            System.out.println("파일 삭제: " + alreadyFileName);

						// 삭제된 파일 이후의 파일들 이름 변경
						int deletedIndex = Integer.parseInt(alreadyFileName.split("-")[1].split("\\.")[0]);
						for (int i = deletedIndex + 1; i <= files.length; i++) {
							String originalFilePath = folderPath + post_Seq + "-" + i + ".png";
							String newFilePath = folderPath + post_Seq + "-" + (i - 1) + ".png";
							File originalFile = new File(originalFilePath);
							File newFile = new File(newFilePath);
							if (originalFile.renameTo(newFile)) {
//			                    System.out.println("파일 이름 변경: " + originalFilePath + " -> " + newFilePath);
							} else {
//			                    System.out.println("파일 이름 변경 실패: " + originalFilePath);
							}
						}

					} else {
//			            System.out.println("파일 삭제 실패: " + alreadyFileName);
					}
				}
			} else {
//				System.out.println("기존 이미지 없음");
			}
			try {
				// 실제 파일 저장 처리 부분
				file.transferTo(new File(folderPath + fileName));
//		        System.out.println("파일 저장 성공");
			} catch (IOException e) {
				e.printStackTrace();
//		        System.out.println("파일 저장 실패");
			}

		} else { // 수정폼 제출시 이미지가 2개 이상일때
//			System.out.println("이미지 " + imgCount + " 개 ");

			// 기존파일 삭제 유무 체크후 처리
			File folder = new File(folderPath);
			File[] files = folder.listFiles();
			if (files != null && deletedStrings.length > 0) {
				for (String deletedString : deletedStrings) {
					String alreadyFileName = deletedString.substring(deletedString.lastIndexOf('/') + 1);
					String absoluteFilePath = folderPath + alreadyFileName;
					File fileToDelete = new File(absoluteFilePath);
					if (fileToDelete.delete()) {
//			            System.out.println("파일 삭제: " + alreadyFileName);

						// 삭제된 파일 이후의 파일들 이름 변경
						int deletedIndex = Integer.parseInt(alreadyFileName.split("-")[1].split("\\.")[0]);
						for (int i = deletedIndex + 1; i <= files.length; i++) {
							String originalFilePath = folderPath + post_Seq + "-" + i + ".png";
							String newFilePath = folderPath + post_Seq + "-" + (i - 1) + ".png";
							File originalFile = new File(originalFilePath);
							File newFile = new File(newFilePath);
							if (originalFile.renameTo(newFile)) {
//			                    System.out.println("파일 이름 변경: " + originalFilePath + " -> " + newFilePath);
							} else {
//			                    System.out.println("파일 이름 변경 실패: " + originalFilePath);
							}
						}
					} else {
//			            System.out.println("파일 삭제 실패: " + alreadyFileName);
					}
				}
			} else {
//				System.out.println("기존 이미지 없음");
			}
			// 실제 파일 저장 처리 부분
			for(int i=0; i<attach_file.length; i++) {
//				System.out.println("추가된 이미지 " + imgCount + " 개");
				MultipartFile file = attach_file[i];
				String fileName = post_Seq + "-" + (alreadyFileNo-deleteStrings+(i+1)) + ".png";
//				System.out.println(fileName);
//				System.out.println("File Name: " + file.getOriginalFilename());

				try {
					// 파일을 지정된 경로에 저장
					file.transferTo(new File(folderPath + fileName));
//		            System.out.println("파일 저장 성공");
				} catch (IOException e) {
					e.printStackTrace();
//		            System.out.println("파일 저장 실패");
				}
			}
		}

		if (vo.getPost_Public() == null) {
			vo.setPost_Public("n");
		}

		// 2. 수정 처리
		postService.updatePost(vo);

		// 3. 해시태그 수정 전 삭제 처리
		postService.deleteTag(post_Seq);

		// 4. 해시태그 처리 부분
		String hashTag = vo.getPost_Hashtag();
		if (hashTag != null && !hashTag.isEmpty()) {
			try { // 2-1. 사용자가 입력한 해시태그들을 json형태로 받아와서 사용할 수 있게 파싱하는 작업
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode jsonNode = objectMapper.readTree(hashTag);

				for (JsonNode node : jsonNode) {
					// n번째 해시태그 내용
					String value = node.get("value").asText();
					// 특수문자 변환
					value = value.replace("<", "《").replace(">", "》").replace("#", "");

					TagVO tvo = new TagVO();
					tvo.setPost_Seq(post_Seq);
					tvo.setTag_Content(value);
					postService.insertTag(tvo);
				}
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		//return "index";
		return "redirect:index";
	}

	@GetMapping("/search_HashTag")
	public String search_HashTag(@RequestParam(value="tag_Content") String hashTag, HttpSession session, Model model){

		if(session.getAttribute("loginUser") == null) {
			model.addAttribute("message", "로그인을 해주세요");
			return "login";
		} else {

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
			ArrayList<PostVO> postlist = postService.getHashTagPost(hashTag);

			// 각 post_seq에 대한 댓글들을 매핑할 공간.
			Map<Integer, ArrayList<ReplyVO>> replymap = new HashMap<>();

			// 각 post_seq에 대한 해시태그들을 매핑할 공간.
			Map<Integer, ArrayList<TagVO>> hashmap = new HashMap<>();

			// 정렬된 postlist의 인덱스 순으로 댓글 리스트를 매핑함.
			// 동시에 각 게시글의 좋아요 카운트와 댓글 카운트를 저장.
			for(int i=0; i<postlist.size(); i++) {
				// 자신, 팔로잉한 사람들의 게시글의 post_seq를 불러온다.
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
				voForLikeYN.setMember_Id(member_Id);
				voForLikeYN.setPost_Seq(post_Seq);
				String post_LikeYN = postService.getLikeYN(voForLikeYN);
				postlist.get(i).setPost_LikeYN(post_LikeYN);

				// i번째 게시글의 해시태그 체크    hashmap
				ArrayList<TagVO> hash = postService.getHashtagList(post_Seq);
				hashmap.put(post_Seq, hash);
			}

			int postListSize = postlist.size();

			// 전체 회원 프로필 이미지 조회
			HashMap<String, String> profilemap = memberService.getMemberProfile();

			List<MemberVO> searchFollow = memberService.searchMembers(hashTag);

			List<MemberVO> myFollowing = memberService.getFollowings(member_Id);

			for(int i=0; i<myFollowing.size(); i++) {
				for(int j=0; j<searchFollow.size(); j++) {
					if(myFollowing.get(i).getMember_Id().equals(searchFollow.get(j).getMember_Id())) {
						searchFollow.get(j).setBothFollow(1);
					}
				}
			}

			List<MemberVO> mostFamous = memberService.getMostFamousMember();

			List<MemberVO> followingList = memberService.getFollowings(member_Id);

			for(int i = 0; i < mostFamous.size(); i++) {
				for(int j = 0; j < followingList.size(); j++) {
					if(mostFamous.get(i).getMember_Id().equals(followingList.get(j).getMember_Id())) {
						mostFamous.get(i).setBothFollow(1);
					}
				}
			}

			int searchFollowSize = searchFollow.size();

			model.addAttribute("searchFollow", searchFollow);
			model.addAttribute("mostFamous", mostFamous);
			model.addAttribute("searchFollowSize", searchFollowSize);
			model.addAttribute("alarmList", alarmList);
			model.addAttribute("alarmListSize", alarmListSize);
			model.addAttribute("hashTag", hashTag);
			model.addAttribute("profileMap", profilemap);
			model.addAttribute("postList", postlist);
			model.addAttribute("replyMap", replymap);
			model.addAttribute("recommendMember", recommendMember);
			model.addAttribute("hottestFeed", hottestFeed);
			model.addAttribute("member_Id", member_Id);
			model.addAttribute("hashMap", hashmap);
			model.addAttribute("hashTag", hashTag);
			model.addAttribute("postListSize", postListSize);

			return "searchIndex";
		}
	}

	@PostMapping("/getMoreSearchHashTag")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getMoreSearchHashTag(@RequestBody Map<String, String> requestbody, HttpSession session, Model model){

		String member_Id = ((MemberVO) session.getAttribute("loginUser")).getMember_Id();

		String hashTag = requestbody.get("hashTag");

		/* index페이지의 뉴스피드 부분 */
		// 자신, 팔로잉한 사람들의 게시글을 담는부분
		ArrayList<PostVO> postlist = postService.getHashTagPost(hashTag);

		// 각 post_seq에 대한 댓글들을 매핑할 공간.
		Map<Integer, ArrayList<ReplyVO>> replymap = new HashMap<>();

		// 각 post_seq에 대한 해시태그들을 매핑할 공간.
		Map<Integer, ArrayList<TagVO>> hashmap = new HashMap<>();

		// 정렬된 postlist의 인덱스 순으로 댓글 리스트를 매핑함.
		// 동시에 각 게시글의 좋아요 카운트와 댓글 카운트를 저장.
		for(int i=0; i<postlist.size(); i++) {
			// 자신, 팔로잉한 사람들의 게시글의 post_seq를 불러온다.
			int post_Seq = postlist.get(i).getPost_Seq();
			postlist.get(i).setPost_Content(postlist.get(i).getPost_Content().replace("\n", "<br>"));

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
			//System.out.println(i + "번째 게시글 댓글 여부" + replymap.get(i));

			// i번째 게시글의 좋아요 여부 체크
			PostVO voForLikeYN = new PostVO();
			voForLikeYN.setMember_Id(member_Id);
			voForLikeYN.setPost_Seq(post_Seq);
			//System.out.println("[좋아요 여부 확인 - 0] 게시글 번호 : " + post_Seq);
			//System.out.println("[좋아요 여부 확인 - 1] Setting 전 post_LikeYN = " + postlist.get(i).getPost_LikeYN());
			String post_LikeYN = postService.getLikeYN(voForLikeYN);
			postlist.get(i).setPost_LikeYN(post_LikeYN);
			//System.out.println("[좋아요 여부 확인 - 4] Setting 후 post_LikeYN = " + postlist.get(i).getPost_LikeYN());

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
		responseData.put("member_Id", member_Id);
		responseData.put("hashMap", hashmap);

		return ResponseEntity.ok(responseData);
	}

	@PostMapping("/selectOnePost")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> selectOnePost(@RequestBody Map<String, Integer> requestbody, HttpSession session){

		int post_Seq = requestbody.get("post_Seq");
		String member_Id = ((MemberVO) session.getAttribute("loginUser")).getMember_Id();

		// 1. 게시글 상세정보
		PostVO postVO = postService.getpostDetail(post_Seq);
		// 1-1. 게시글 좋아요 상태 set
		PostVO voForLikeYN = new PostVO();
		voForLikeYN.setMember_Id(member_Id);
		voForLikeYN.setPost_Seq(post_Seq);
		String post_LikeYN = postService.getLikeYN(voForLikeYN);
		postVO.setPost_LikeYN(post_LikeYN);

		// 2. 댓글 3개 만
		ArrayList<ReplyVO> replylist = replyService.getReplyPreview(post_Seq);

		// 2-1. 댓글 좋아요 상태 set
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

		// 3. 해쉬태그
		ArrayList<TagVO> hash = postService.getHashtagList(post_Seq);

		// 전체 회원의 이미지 Map을 세션에서 받아옴
		HashMap<String, String> profileMap = (HashMap<String, String>) session.getAttribute("profileMap");

		Map<String, Object> responseData = new HashMap<>();

		responseData.put("postVO", postVO);
		responseData.put("replylist", replylist);
		responseData.put("hash", hash);
		responseData.put("profileMap", profileMap);
		responseData.put("session_Id", member_Id);

		return ResponseEntity.ok(responseData);

	}

	// 댓글 삭제
	@PostMapping("deleteReply")
	@ResponseBody
	public Map<String, Object>  deleteReply(@RequestParam(value="post_Seq") int post_Seq,
											@RequestParam(value="reply_Seq") int reply_Seq, HttpSession session){

		// 0. ajax요청에 대한 response값 전달을 위한 Map 변수 선언
		Map<String, Object> dataMap = new HashMap<>();

		// 1. session 아이디값 받아오기
		String member_Id = ((MemberVO) session.getAttribute("loginUser")).getMember_Id();

		// 2. delete 쿼리문에 전달할 vo 객체 생성 주입
		ReplyVO rep = new ReplyVO();
		rep.setReply_Seq(reply_Seq);
		rep.setPost_Seq(post_Seq);

		// 3. delete쿼리문 실행
		replyService.deleteReply(rep);

		// - 좋아요 삭제 처리
		replyService.deleteReplyLike(rep);

		// 4. 글 작성자 받아오기
		String postWriter = postService.getPostWriter(post_Seq);

		// - 알람 삭제 처리
		AlarmVO aVO = new AlarmVO();
		aVO.setKind(3);
		aVO.setFrom_Mem(member_Id);
		aVO.setTo_Mem(postWriter);
		aVO.setPost_Seq(post_Seq);
		aVO.setReply_Seq(reply_Seq);
		int alarm_Seq = alarmService.getOneAlarm_Seq(aVO);

		if(alarm_Seq > 0) {
			alarmService.deleteAlarm(alarm_Seq);
		}

		// 5. 게시글의 댓글리스트를 출력하기 위한 ArrayList<ReplyVO> 값 저장
		ArrayList<ReplyVO> replyList = replyService.getListReply(post_Seq);

		// 6. 전체 회원의 이미지 Map을 세션에서 받아옴
		HashMap<String, String> profileMap = (HashMap<String, String>) session.getAttribute("profileMap");

		// 7. 해당 게시글의 상세정보를 조회해서 가져옴(게시글의 댓글 카운트 변경을 위함)
		PostVO postInfo = postService.getpostDetail(post_Seq);

		// 8. 게시글 댓글의 좋아요 여부 체크
		for(int i=0; i<replyList.size(); i++) {

			ReplyVO KVO = replyList.get(i);
			LikeVO check = new LikeVO();
			check.setMember_Id(member_Id);
			check.setPost_Seq(KVO.getPost_Seq());
			check.setReply_Seq(KVO.getReply_Seq());
			String reply_LikeYN = replyService.getCheckReplyLike(check);

			replyList.get(i).setReply_LikeYN(reply_LikeYN);
		}

		// 8. ajax의 응답성공(success)의 response로 들어갈 값들 매핑
		dataMap.put("postInfo", postInfo);
		dataMap.put("replies", replyList);
		dataMap.put("profile", profileMap);
		dataMap.put("member_Id", member_Id);

		return dataMap;
	}

	@GetMapping("/replyEditView")
	@ResponseBody
	public Map<String, Object> replyEditView(@RequestParam(value="post_Seq") int post_Seq,
											 @RequestParam(value="reply_Seq") int reply_Seq){

		// ajax요청에 대한 응답을 보내기 위한 객체 생성
		Map<String, Object> dataMap = new HashMap<>();

		// 댓글 내용을 조회
		String replyContent = replyService.replyContent(post_Seq, reply_Seq);

		dataMap.put("post_seq", post_Seq);
		dataMap.put("reply_seq", reply_Seq);
		dataMap.put("replycontent", replyContent);
		return dataMap;
	}

	@PostMapping("/replyUpdate")
	@ResponseBody
	public Map<String, Object>  deleteReply(@RequestParam(value="post_Seq") int post_Seq,
											@RequestParam(value="reply_Seq") int reply_Seq,
											@RequestParam(value="replyContent") String replyContent, HttpSession session){

		// 0. ajax요청에 대한 response값 전달을 위한 Map 변수 선언
		Map<String, Object> dataMap = new HashMap<>();

		// 1. session 아이디값 받아오기
		String member_Id = ((MemberVO) session.getAttribute("loginUser")).getMember_Id();

		// 2. update 쿼리문에 전달할 vo 객체 생성 주입
		ReplyVO rep = new ReplyVO();
		rep.setReply_Seq(reply_Seq);
		rep.setPost_Seq(post_Seq);
		rep.setReply_Content(replyContent);

		// 3. update쿼리문 실행
		replyService.updateReply(rep);

		// 4. 글 작성자 받아오기
		String postWriter = postService.getPostWriter(post_Seq);

		// 5. 게시글의 댓글리스트를 출력하기 위한 ArrayList<ReplyVO> 값 저장
		ArrayList<ReplyVO> replyList = replyService.getListReply(post_Seq);

		// 6. 전체 회원의 이미지 Map을 세션에서 받아옴
		HashMap<String, String> profileMap = (HashMap<String, String>) session.getAttribute("profileMap");

		// 7. 해당 게시글의 상세정보를 조회해서 가져옴(게시글의 댓글 카운트 변경을 위함)
		PostVO postInfo = postService.getpostDetail(post_Seq);

		// 8. 게시글 댓글의 좋아요 여부 체크
		for(int i=0; i<replyList.size(); i++) {

			ReplyVO KVO = replyList.get(i);
			LikeVO check = new LikeVO();
			check.setMember_Id(member_Id);
			check.setPost_Seq(KVO.getPost_Seq());
			check.setReply_Seq(KVO.getReply_Seq());
			String reply_LikeYN = replyService.getCheckReplyLike(check);

			replyList.get(i).setReply_LikeYN(reply_LikeYN);
		}

		// 8. ajax의 응답성공(success)의 response로 들어갈 값들 매핑
		dataMap.put("postInfo", postInfo);
		dataMap.put("replies", replyList);
		dataMap.put("profile", profileMap);
		dataMap.put("member_Id", member_Id);

		return dataMap;
	}
}
