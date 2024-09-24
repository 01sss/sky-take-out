package com.sky.service;

import com.sky.vo.TurnoverReportVO;

import java.time.LocalDate;

/**
 * @author: Tan
 * @createTime: 2024/09/24 10:25
 * @description:
 */
public interface ReportService {

    /**
     * 统计指定时间区间内的营业额
     * @param begin
     * @param end
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);
}
