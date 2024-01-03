package com.blue.mapper;

import com.blue.dto.LikeVO;
import com.blue.dto.ReplyVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

@Mapper
public interface ReplyMapper {
    ArrayList<ReplyVO> listReply(int post_Seq);

    String checkReplyLike(LikeVO vo);

    ArrayList<ReplyVO> replyPreview(int post_Seq);

    void addReplyLike(LikeVO vo);

    void delReplyLike(LikeVO vo);

    int postReplyCount(int post_Seq);

    void insertReply(ReplyVO vo);

    String replyWriter(int reply_Seq);

    int getMaxReply_Seq();

    void deleteReply(ReplyVO vo);

    String checkZeroReplySeq();
}
