package com.blue.mapper;

import com.blue.dto.AlarmVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AlarmMapper {

    void insertAlarm(AlarmVO alarmVO);

    List<AlarmVO> selectAlarm(String member_Id);

    String getOneAlarm_Seq(AlarmVO alarmVO);

    void deleteAlarm(int alarm_Seq);
}
