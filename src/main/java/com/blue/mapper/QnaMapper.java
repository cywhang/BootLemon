package com.blue.mapper;

import com.blue.dto.QnaVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QnaMapper {
    void insertQna(QnaVO vo);

    List<QnaVO> getMyQna(String member_Id);

    int checkMaxSeq();

    List<QnaVO> getAllQna();

    QnaVO getQnaDetail(int qna_Seq);

    void updateQnaAnswer(QnaVO vo);

    void deleteQna(int qna_Seq);
}
