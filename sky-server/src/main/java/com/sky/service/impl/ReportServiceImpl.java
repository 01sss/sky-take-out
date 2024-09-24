package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Tan
 * @createTime: 2024/09/24 10:25
 * @description:
 */
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 统计指定时间区间内的营业额  // 营业额统计的是状态为已完成的订单的金额
     * @param begin
     * @param end
     */
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        // 当前集合用于存放从begin日期到end日期之间的所有日期
        List<LocalDate> dateList = new ArrayList<>();
        List<Double> turnoverList = new ArrayList<>();
        Integer status = Orders.COMPLETED;

        // 使用StringBuilder来构建日期字符串
        StringBuilder dateStringBuilder = new StringBuilder();
        StringBuilder turnoverStringBuilder = new StringBuilder();

        try {
            while (!begin.isAfter(end)) {
                // 日期计算，计算指定日期的后一天对应的日期
                LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
                LocalDateTime endTime = LocalDateTime.of(begin, LocalTime.MAX);

                Double turnover = orderMapper.getTurnover(status, beginTime, endTime);
                if (turnover == null) {
                    turnover = 0.0; // 如果营业额为null，则默认为0.0
                }

                dateList.add(begin);
                turnoverList.add(turnover);

                // 构建日期字符串
                if (dateStringBuilder.length() > 0) {
                    dateStringBuilder.append(",");
                }
                dateStringBuilder.append(begin.toString());

                // 构建营业额字符串
                if (turnoverStringBuilder.length() > 0) {
                    turnoverStringBuilder.append(",");
                }
                turnoverStringBuilder.append(turnover);

                begin = begin.plusDays(1);
            }

            // 将日期集合转换为字符串形式，并且每个日期之间使用逗号隔开
            String date = dateStringBuilder.toString();

            // 将营业额集合转换为字符串形式，并且在金额之间使用逗号隔开
            String turnover = turnoverStringBuilder.toString();

            TurnoverReportVO turnoverReportVO = new TurnoverReportVO();
            turnoverReportVO.setDateList(date);
            turnoverReportVO.setTurnoverList(turnover);

            return turnoverReportVO;
        } catch (Exception e) {
            // 异常处理
            System.err.println("Error occurred while getting turnover statistics: " + e.getMessage());
            return null;
        }
    }

}
