package com.blue.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blue.mapper.QnaMapper;
import com.blue.dto.QnaVO;

@Service
public class QnaServiceImpl implements QnaService {

	@Autowired
	private QnaMapper qnaMapper;
	
	@Override
	public void insertQna(QnaVO vo) {
		qnaMapper.insertQna(vo);
	}

	@Override
	public List<QnaVO> getMyQna(String member_Id) {
		return qnaMapper.getMyQna(member_Id);
	}

	@Override
	public int checkMaxSeq() {
		return qnaMapper.checkMaxSeq();
	}

	@Override
	public List<QnaVO> getAllQna() {
		return qnaMapper.getAllQna();
	}

	@Override
	public QnaVO getQnaDetail(int qna_Seq) {
		return qnaMapper.getQnaDetail(qna_Seq);
	}

	@Override
	public void updateQnaAnswer(QnaVO vo) {
		qnaMapper.updateQnaAnswer(vo);
		
	}

	@Override
	public void deleteQna(int qna_Seq) {
		qnaMapper.deleteQna(qna_Seq);
	}

}
