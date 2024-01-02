package com.blue.service;

import java.util.ArrayList;

import com.blue.dto.LikeVO;
import com.blue.dto.ReplyVO;

public interface ReplyService {

	// 각 게시글 댓글 3개까지 조회
	ArrayList<ReplyVO> getReplyPreview(int post_Seq);

	// 게시글 상세보기 모달창의 댓글 리스트
	ArrayList<ReplyVO> getListReply(int post_Seq);

	// 댓글 좋아요 여부 조회
	String getCheckReplyLike(LikeVO vo);

	// 댓글 좋아요 처리
	void changeReplyLike(LikeVO vo);

	// 댓글 인서트
	void insertReply(ReplyVO vo);

	// DB상 가장 큰 reply_Seq 가져오기
	int getMaxReply_Seq();

	// 댓글 삭제
	void deleteReply(ReplyVO vo);

	// 댓글 삭제시 댓글에대한 좋아요 전체 삭제
	void deleteReplyLike(ReplyVO vo);
}
