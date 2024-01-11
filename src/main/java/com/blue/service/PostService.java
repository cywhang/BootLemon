package com.blue.service;

import java.util.ArrayList;
import java.util.List;

import com.blue.dto.LikeVO;
import com.blue.dto.PostVO;
import com.blue.dto.TagVO;


public interface PostService {

	// 게시글 전체 조회 (뉴스피드)
	ArrayList<PostVO> getlistPost(String member_Id);

	// 게시글 좋아요 여부 체크
	String getLikeYN(PostVO voForLikeYN);

	// 게시글 좋아요 처리
	void changeLike(LikeVO vo);

	// 인기글 조회 (우측)
	List<PostVO> getHottestFeed();

	// 게시글 작성 (모달창)
	void insertPost(PostVO vo);

	// 게시글 상세보기(모달창)
	PostVO getpostDetail(int post_Seq);

	// 프로필의 개인 포스트들
	ArrayList<PostVO> getMemberPost(PostVO vo);

	// 게시글 좋아요 조회
	int getPost_Like_Count(int post_Seq);

	// 관리자 - 전체 게시글 조회
	ArrayList<PostVO> getAllPost();

	// 게시글 삭제
	void deletePost(int post_Seq);

	// 게시글 추가 시 필요한 가장높은 시퀀스 조회
	int postSeqCheck();

	// 게시글 추가의 해시태그 인서트
	void insertTag(TagVO vo);

	// 게시글 해시태그 조회
	ArrayList<TagVO> getHashtagList(int post_Seq);

	// 한 회원의 전체 해시태그 삭제
	void deleteOneMemsTag(String member_Id);

	// 관리자 게시글 상세정보
	PostVO selectPostDetail(int post_Seq);

	// 해쉬 태그 검색 기능
	ArrayList<PostVO> getHashTagPost(String hashTag);

	// 관리자 페이지 오늘의 해시태그
	ArrayList<TagVO> getTodaysTag();

	// 게시글 수정 처리
	void updatePost(PostVO vo);

	// 게시글 해시태그 수정전 삭제 처리
	void deleteTag(int post_Seq);

	// 알람을 위해 게시글 작성자 추출
	String getPostWriter(int post_Seq);

	// 게시글 이미지 삭제를 위한 사용자가 작성한 게시글의 시퀀스 번호
	List<Integer> seqForUser(String member_Id);
}
