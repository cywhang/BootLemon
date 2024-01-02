package com.blue.service;

import java.util.List;

import com.blue.dto.QnaVO;

public interface QnaService {

	void insertQna(QnaVO vo);

	List<QnaVO> getMyQna(String member_Id);
	
	int checkMaxSeq();
	
	List<QnaVO> getAllQna();
	
	QnaVO getQnaDetail(int qna_Seq);
	
	void updateQnaAnswer(QnaVO vo);
	
	void deleteQna(int qna_Seq);
}
