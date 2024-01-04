package com.blue.mapper;

import com.blue.dto.ReplyVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LikeMapper {

    int postLikeCount(int post_Seq);

    void replyDelete(ReplyVO vo);
}
