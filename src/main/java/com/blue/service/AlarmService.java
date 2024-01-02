package com.blue.service;

import java.util.List;

import com.blue.dto.AlarmVO;

public interface AlarmService {

	public void insertAlarm(AlarmVO alarmVO);

	// 한 회원의 전체 알람 가져오기
	List<AlarmVO> getAllAlarm(String member_Id);

	// 해당 알람의 seq 가져오기
	int getOneAlarm_Seq(AlarmVO alarmVO);

	// 알람 삭제
	void deleteAlarm(int alarm_Seq);
}
