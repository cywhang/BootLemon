package com.blue.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.blue.dto.AlarmVO;
import com.blue.mapper.LikeMapper;
import com.blue.mapper.ReplyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.blue.dto.LikeVO;
import com.blue.dto.ReplyVO;

@Service
public class ReplyServiceImpl implements ReplyService {

	@Autowired
	private ReplyMapper replyMapper;
	@Autowired
	private AlarmService alarmService;
	@Autowired
	private LikeMapper likeMapper;

	// 각 게시글 댓글 3개까지 조회
	@Override
	public ArrayList<ReplyVO> getReplyPreview(int post_Seq) {
		Date currentTime = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		List<ReplyVO> resultList = replyMapper.replyPreview(post_Seq);
		for(ReplyVO vo : resultList) {
			String wroteTimeString = dateFormat.format(vo.getReply_Date());
			try {
				Date wroteTime = dateFormat.parse(wroteTimeString);
				long timeDiff = currentTime.getTime() - wroteTime.getTime();
				long minutesDiff = timeDiff / (60 * 1000);
				long hoursDiff = minutesDiff / 60;
				long daysDiff = hoursDiff / 24;
				if (minutesDiff <= 60) {
					vo.setReply_WhenDid(minutesDiff + " minutes ago");
				} else if (minutesDiff > 60 & minutesDiff <= 1440){
					vo.setReply_WhenDid(hoursDiff + " hours ago");
				} else {
					vo.setReply_WhenDid(daysDiff + " days ago");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		ArrayList<ReplyVO> arrayList = new ArrayList<ReplyVO>(resultList);

		return arrayList;
	}

	// 게시글 상세보기 모달창의 댓글 리스트
	@Override
	public ArrayList<ReplyVO> getListReply(int post_Seq) {
		Date currentTime = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		List<ReplyVO> resultList = replyMapper.listReply(post_Seq);

		for(ReplyVO vo : resultList) {
			String wroteTimeString = dateFormat.format(vo.getReply_Date());
			try {
				Date wroteTime = dateFormat.parse(wroteTimeString);
				long timeDiff = currentTime.getTime() - wroteTime.getTime();
				long minutesDiff = timeDiff / (60 * 1000);
				long hoursDiff = minutesDiff / 60;
				long daysDiff = hoursDiff / 24;

				if (minutesDiff <= 60) {
					vo.setReply_WhenDid(minutesDiff + " minutes ago");
				} else if (minutesDiff > 60 & minutesDiff <= 1440){
					vo.setReply_WhenDid(hoursDiff + " hours ago");
				} else {
					vo.setReply_WhenDid(daysDiff + " days ago");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		ArrayList<ReplyVO> arrayList = new ArrayList<ReplyVO>(resultList);

		return arrayList;
	}

	// 댓글 좋아요 여부 확인
	@Override
	public String getCheckReplyLike(LikeVO voForReplyCheck) {
		String check = replyMapper.checkReplyLike(voForReplyCheck);
		if (check == null) {
			check = "N";
		} else {
			check = "Y";
		}
		return check;
	}

	// 댓글 좋아요 처리
	@Override
	public void changeReplyLike(LikeVO vo) {
		String check = replyMapper.checkReplyLike(vo);

		// 알람
		String reply_Writer = replyMapper.replyWriter(vo.getReply_Seq());
		AlarmVO alarmVO = new AlarmVO();
		alarmVO.setKind(4);
		alarmVO.setFrom_Mem(vo.getMember_Id());
		alarmVO.setTo_Mem(reply_Writer);
		alarmVO.setPost_Seq(vo.getPost_Seq());
		alarmVO.setReply_Seq(vo.getReply_Seq());

		// 알람 테이블에 해당 알람 있나 확인
		int alarmResult = alarmService.getOneAlarm_Seq(alarmVO);

		if(check == null) {
			replyMapper.addReplyLike(vo);

			if(alarmResult == 0) {
				alarmService.insertAlarm(alarmVO);
			} else {}
		} else {
			replyMapper.delReplyLike(vo);

			if(alarmResult == 0) {
			} else {
				alarmService.deleteAlarm(alarmResult);
			}
		}
	}

	@Override
	public void insertReply(ReplyVO vo) {
		replyMapper.insertReply(vo);
	}

	@Override
	public int getMaxReply_Seq() {
		return replyMapper.getMaxReply_Seq();
	}

	@Override
	public void deleteReply(ReplyVO vo) {
		replyMapper.deleteReply(vo);
	}

	@Override
	public void deleteReplyLike(ReplyVO vo) {
		likeMapper.replyDelete(vo);
	}	

}
