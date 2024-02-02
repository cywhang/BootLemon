package com.blue.service;

import java.util.List;
import java.util.Objects;

import com.blue.mapper.AlarmMapper;
import com.blue.mapper.PostMapper;
import com.blue.mapper.ReplyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blue.dto.AlarmVO;
import com.blue.dto.PostVO;
import com.blue.dto.ReplyVO;

@Service
public class AlarmServiceImpl implements AlarmService {

	@Autowired
	private AlarmMapper alarmMapper;
	@Autowired
	private PostMapper postMapper;
	@Autowired
	private ReplyMapper replyMapper;
	
	@Override
	public void insertAlarm(AlarmVO alarmVO) {
		String result1 = postMapper.checkZeroPostSeq();
		if(result1 == null) {
			PostVO postVO = new PostVO();
			postVO.setPost_Seq(0);
			postVO.setMember_Id("admin");
			postVO.setPost_Content("알람 등록을 위한 게시글");
			postVO.setPost_Public("n");
			postVO.setPost_Image_Count(0);
			postMapper.insertPost(postVO);
		}

		String result2 = replyMapper.checkZeroReplySeq();
		if(result2 == null) {
			ReplyVO replyVO = new ReplyVO();
			replyVO.setPost_Seq(0);
			replyVO.setReply_Seq(0);
			replyVO.setMember_Id("admin");
			replyVO.setReply_Content("알람 등록을 위한 댓글");
			replyMapper.insertReply(replyVO);
		}

		String from = alarmVO.getFrom_Mem();
		String to = alarmVO.getTo_Mem();
		if(!Objects.equals(from, to)){
			alarmMapper.insertAlarm(alarmVO);
		}
	}
	
	@Override
	public List<AlarmVO> getAllAlarm(String member_Id) {
		return alarmMapper.selectAlarm(member_Id);
	}

	@Override
	public int getOneAlarm_Seq(AlarmVO alarmVO) {
		String result = alarmMapper.getOneAlarm_Seq(alarmVO);
		if(result == null) {
			return 0;
		} else {
			return Integer.parseInt(result);
		}
    }

	@Override
	public void deleteAlarm(int alarm_Seq) {
		alarmMapper.deleteAlarm(alarm_Seq);
	}

}
