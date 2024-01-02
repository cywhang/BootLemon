package com.blue.mapper;

import com.blue.dto.LikeVO;
import com.blue.dto.ReplyVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

@Mapper
public interface ReplyMapper {
    ArrayList<ReplyVO> replyPreview(int post_Seq);

    ArrayList<ReplyVO> listReply(int post_Seq);

    String checkReplyLike(LikeVO vo);

    void changeReplyLike(LikeVO vo);

    void insertReply(ReplyVO vo);

    int getMaxReply_Seq();

    void deleteReply(ReplyVO vo);

    void deleteReplyLike(ReplyVO vo);

    String checkZeroReplySeq();

    String replyWriter(int reply_Seq);

    void addReplyLike(LikeVO vo);

    void delReplyLike(LikeVO vo);
}
