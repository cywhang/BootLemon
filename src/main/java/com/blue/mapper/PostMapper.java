package com.blue.mapper;

import com.blue.dto.LikeVO;
import com.blue.dto.PostVO;
import com.blue.dto.TagVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface PostMapper {
    ArrayList<PostVO> listPost(String member_Id);

    String getLikeYN(PostVO voForLikeYN);

    void changeLike(LikeVO vo);

    List<PostVO> getHottestFeed();

    void insertPost(PostVO vo);

    int postLikeCount(int post_Seq);

    int postReplyCount(int post_Seq);

    PostVO postDetail(int post_Seq);

    ArrayList<PostVO> getMyPost(String member_Id);

    ArrayList<PostVO> getMemberPost(PostVO vo);

    ArrayList<PostVO> getAllPost();

    void deletePost(int post_Seq);

    int postSeqCheck();

    void insertTag(TagVO vo);

    ArrayList<TagVO> getHashtagList(int post_Seq);

    void deleteOneMemsTag(String member_Id);

    PostVO selectPostDetail(int post_Seq);

    ArrayList<PostVO> getHashTagPost(String hashTag);

    ArrayList<TagVO> getTodaysTag();

    void updatePost(PostVO vo);

    void deleteTag(int post_Seq);

    String getPostWriter(int post_Seq);

    List<Integer> seqForUser(String member_Id);

    String checkZeroPostSeq();

    String checkLike(LikeVO vo);

    void addLike(LikeVO vo);

    void delLike(LikeVO vo);
}
