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

    PostVO postDetail(int post_Seq);

    List<PostVO> getHottestFeed();

    void insertPost(PostVO vo);

    void deletePost(int post_Seq);

    String checkLike(LikeVO vo);

    void addLike(LikeVO vo);

    void delLike(LikeVO vo);

    ArrayList<PostVO> MemberPost(PostVO vo);

    ArrayList<PostVO> getAllPost();

    int postSeqCheck();

    void insertTag(TagVO vo);

    TagVO postHashtag(int post_Seq);

    void deleteOneMemsTag(String member_Id);

    PostVO selectPostDetail(int post_Seq);

    ArrayList<PostVO> getHashTagPost(String hashTag);

    ArrayList<TagVO> getTodaysTag();

    void updatePost(PostVO vo);

    void deleteTag(int post_Seq);

    String PostWriter(int post_Seq);

    String checkZeroPostSeq();
}
