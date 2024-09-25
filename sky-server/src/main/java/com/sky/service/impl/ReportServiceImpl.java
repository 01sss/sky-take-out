package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 统计指定时间区间内的营业额  // 营业额统计的是状态为已完成的订单的金额
     * @param begin
     * @param end
     */
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        // 当前集合用于存放从begin日期到end日期之间的所有日期以及营业额
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

    /**
     * 统计用户人数
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        // 使用StringBuilder来构建字符串 分别存放每日的日期、新增用户数量、总用户数量
        StringBuilder dateStringBuilder = new StringBuilder();
        StringBuilder newUserStringBuilder = new StringBuilder();
        StringBuilder totalUserStringBuilder = new StringBuilder();

        // 初始化分隔符
        String dateDelimiter = "";
        String newUserDelimiter = "";
        String totalUserDelimiter = "";

        while (!begin.isAfter(end)) {
            // 日期计算，计算指定日期的后一天对应的日期
            LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);  // 修改此处

            Integer newUser = userMapper.getNewUserByTime(beginTime, endTime);
            Integer totalUser = userMapper.getTotalUserByTime(endTime);

            // 构建日期字符串
            dateStringBuilder.append(dateDelimiter).append(begin.toString());
            dateDelimiter = ",";

            // 构建新增用户字符串
            newUserStringBuilder.append(newUserDelimiter).append(newUser != null ? newUser : 0);
            newUserDelimiter = ",";

            // 构建总用户字符串
            totalUserStringBuilder.append(totalUserDelimiter).append(totalUser != null ? totalUser : 0);
            totalUserDelimiter = ",";

            begin = begin.plusDays(1);
        }

        // 将日期集合转换为字符串形式，并且每个日期之间使用逗号隔开
        String dateList = dateStringBuilder.toString();

        // 将新增用户集合转换为字符串形式，并且每个日期之间使用逗号隔开
        String newUserList = newUserStringBuilder.toString();

        // 将总用户集合转换为字符串形式，并且每个日期之间使用逗号隔开
        String totalUserList = totalUserStringBuilder.toString();

        UserReportVO userReportVO = new UserReportVO();
        userReportVO.setDateList(dateList);
        userReportVO.setNewUserList(newUserList);
        userReportVO.setTotalUserList(totalUserList);

        return userReportVO;
    }

    /**
     * 统计指定时间区间内的订单数据
     * @param begin
     * @param end
     * @return
     */
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> vaildOrderCountList = new ArrayList<>();

        dateList.add(begin);
        while (!begin.isAfter(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 遍历dateList集合，查询每天的有效订单数和订单总数
        for (LocalDate localDate : dateList) {
            // 获取当前日期的开始时刻和终止时刻
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);

            // 查询每天的订单总数
            Integer orderCount = getOrderCount(beginTime, endTime, null);

            // 查询每天的有效订单数
            Integer vailOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);

            // 将每日的订单总数和有效订单数放入集合中
            orderCountList.add(orderCount==null?0:orderCount);
            vaildOrderCountList.add(vailOrderCount==null?0:vailOrderCount);

        }


        // 计算时间区间内的订单总数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();

        // 计算时间区间内的有效订单总数
        Integer vaildTotalOrderCount = vaildOrderCountList.stream().reduce(Integer::sum).get();

        // 计算订单完成率
        Double orderCompeletionRate = totalOrderCount==0?0.0:vaildTotalOrderCount.doubleValue()/totalOrderCount.doubleValue();

        OrderReportVO orderReportVO = OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(vaildTotalOrderCount, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(vaildTotalOrderCount)
                .orderCompletionRate(orderCompeletionRate)
                .build();


        return orderReportVO;
    }


    /**
     * 根据条件统计订单数量
     * @param begin
     * @param end
     * @param status
     * @return
     */
    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status){
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);

        return orderMapper.countByMap(map);
    }


    /**
     * 统计指定时间区间内销量排名前十的菜品数据
     * @param begin
     * @param end
     * @return
     */
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();

        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> goodsSalesDTO = orderMapper.getSalesTop10(beginTime, endTime);

        for (GoodsSalesDTO salesDTO : goodsSalesDTO) {
            nameList.add(salesDTO.getName());
            numberList.add(salesDTO.getNumber()==null?0:salesDTO.getNumber());
        }


        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList,","))
                .numberList(StringUtils.join(numberList,","))
                .build();
    }

    /**
     * 导出运营数据报表
     * @param response
     */
    public void exportBusinessData(HttpServletResponse response) {
        // 1.查询数据库，获取营业数据
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        // 获取近30天的营业数据
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(beginTime, endTime);

        // 2.通过POI将数据写入到Excel文件中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try {
            // 基于模板文件创建一个新的excel文件
            XSSFWorkbook excel = new XSSFWorkbook(in);

            // 获取表格的sheet页
            XSSFSheet sheet = excel.getSheet("sheet1");

            // 填充数据--时间
            sheet.getRow(1).getCell(1).setCellValue("时间：" + beginTime + "☞" + endTime);

            // 获得第四行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());

            // 获得第五行
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());

            // 填充明细数据
            for(int i = 0; i < 30; i++){
                LocalDate dateBegin = begin.plusDays(i);
                // 查询某一天的营业数据
                BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(dateBegin, LocalTime.MIN), LocalDateTime.of(dateBegin, LocalTime.MAX));

                // 获取某一行
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(dateBegin.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());

            }

            // 3.通过输出流将Excel文件下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

            // 关闭资源
            out.close();
            excel.close();

        } catch (IOException e) {
            e.printStackTrace();
        }





    }


}
