package com.blue.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.blue.dto.AlarmVO;
import com.blue.dto.LikeVO;
import com.blue.mapper.LikeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blue.mapper.PostMapper;
import com.blue.dto.PostVO;
import com.blue.dto.TagVO;

@Service
public class PostServiceImpl implements PostService {

	@Autowired
	private PostMapper postMapper;
	@Autowired
	private LikeMapper likeMapper;
	@Autowired
	private AlarmService alarmService;

	// 게시글 가져오기
	@Override
	public ArrayList<PostVO> getlistPost(String member_Id) {
		List<PostVO> resultList = postMapper.listPost(member_Id);
		ArrayList<PostVO> arrayList = new ArrayList<PostVO>(resultList);
		return arrayList;
	}

	// 게시글 좋아요 여부 체크
	@Override
	public String getLikeYN(PostVO voForLikeYN) {
		LikeVO vo = new LikeVO();
		vo.setMember_Id(voForLikeYN.getMember_Id());
		vo.setPost_Seq(voForLikeYN.getPost_Seq());
		String check = postMapper.checkLike(vo);
		if (check == null) {
			check = "N";
		} else {
			check = "Y";
		}
		return check;
	}

	// 게시글 좋아요 처리
	@Override
	public void changeLike(LikeVO vo) {
		String check = postMapper.checkLike(vo);

		// 알람
		String post_Writer = postMapper.postWriter(vo.getPost_Seq());
		AlarmVO alarmVO = new AlarmVO();
		alarmVO.setKind(2);
		alarmVO.setFrom_Mem(vo.getMember_Id());
		alarmVO.setTo_Mem(post_Writer);
		alarmVO.setPost_Seq(vo.getPost_Seq());
		alarmVO.setReply_Seq(0);

		// 알람 테이블에 해당 알람 있나 확인
		int alarmResult = alarmService.getOneAlarm_Seq(alarmVO);

		// 1. 해당 게시글에 좋아요를 누른 상태가 아닐때
		if(check == null) {
			// 좋아요 추가
			postMapper.addLike(vo);

			// 알람 테이블에서 해당 게시글의 알람이 없으면  알람 추가
			if(alarmResult == 0) {
				alarmService.insertAlarm(alarmVO);
			}
			// 2. 해당 게시글에 좋아요를 누른 상태일때
		} else {
			// 좋아요 취소
			postMapper.delLike(vo);

			// 게시글 작성자가 알람을 확인하기전에 게시글의 좋아요를 취소했을때의 경우에 알림을 삭제 처리하는 부분
			if(alarmResult == 0) {
				// 게시글 작성자가 알람을 확인하지 않았다면 해당 알람을 삭제 처리하는 부분
			} else {
				alarmService.deleteAlarm(alarmResult);
			}
		}
	}

	// 인기글 조회
	@Override
	public List<PostVO> getHottestFeed() {
		return postMapper.getHottestFeed();
	}

	// 게시글 작성
	@Override
	public void insertPost(PostVO vo) {
		postMapper.insertPost(vo);
	}

	@Override
	public int getPost_Like_Count(int post_Seq) {
		return likeMapper.postLikeCount(post_Seq);
	}

	// 게시글 상세보기
	@Override
	public PostVO getpostDetail(int post_Seq) {
		return postMapper.postDetail(post_Seq);
	}

	@Override
	public ArrayList<PostVO> getMemberPost(PostVO vo) {
		List<PostVO> result = postMapper.memberPost(vo);
		ArrayList<PostVO> memberPostList = new ArrayList<PostVO>(result);
		return memberPostList;
	}

	// 관리자용 전체 게시글 조회
	@Override
	public ArrayList<PostVO> getAllPost() {
		List<PostVO> result = postMapper.getAllPost();
		ArrayList<PostVO> getAllPost = new ArrayList<PostVO>(result);
		return getAllPost;
	}

	@Override
	public void deletePost(int post_Seq) {
		postMapper.deletePost(post_Seq);
	}

	@Override
	public int postSeqCheck() {
		// 게시글 인서트시 시퀀스 NEXTVAL 값 예측 연산처리
		int Seq = postMapper.postSeqCheck() + 1;
		return Seq;
	}

	@Override
	public void insertTag(TagVO vo) {
		postMapper.insertTag(vo);
	}

	@Override
	public ArrayList<TagVO> getHashtagList(int post_Seq) {
		List<TagVO> result = postMapper.postHashtag(post_Seq);
		ArrayList<TagVO> hashList = new ArrayList<TagVO>(result);
		return hashList;
	}

	@Override
	public void deleteOneMemsTag(String member_Id) {
		postMapper.deleteOneMemsTag(member_Id);
	}

	@Override
	public PostVO selectPostDetail(int post_Seq) {
		return postMapper.selectPostDetail(post_Seq);
	}

	@Override
	public ArrayList<PostVO> getHashTagPost(String hashTag) {
		List<PostVO> result = postMapper.getHashTagPost(hashTag);
		ArrayList<PostVO> memberPostList = new ArrayList<PostVO>(result);
		return memberPostList;
	}

	@Override
	public ArrayList<TagVO> getTodaysTag() {
		List<TagVO> result = postMapper.getTodaysTag();
		ArrayList<TagVO> todaysTag = new ArrayList<TagVO>(result);
		return todaysTag;
	}

	@Override
	public void updatePost(PostVO vo) {
		postMapper.updatePost(vo);
	}

	@Override
	public void deleteTag(int post_Seq) {
		postMapper.deleteTag(post_Seq);
	}

	@Override
	public String getPostWriter(int post_Seq) {
		return postMapper.postWriter(post_Seq);
	}

	@Override
	public int postImgCount(int post_Seq) {
		return postMapper.postImgCount(post_Seq);
	}

	@Override
	public HashMap<Integer, Integer> seqForUser(String member_Id){
		List<HashMap<Integer, Integer>> resultList = postMapper.seqForUser(member_Id);
		HashMap<Integer, Integer> postSeqCount = new HashMap<>();

		for (HashMap<Integer, Integer> row : resultList) {
			int post_Seq = row.get("postSeq");
			int post_Image_Count = row.get("postImageCount");
			postSeqCount.put(post_Seq, post_Image_Count);
		}
		return postSeqCount;
	}


}
