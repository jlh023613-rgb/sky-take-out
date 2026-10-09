package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.models.auth.In;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;
    /**
     * 统计指定时间区间内的营业额数据
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Double turnover = reportMapper.getTurnover(beginTime, endTime);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 统计指定时间区间内的用户数据
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //存放每天的新增用户数量
        List<Integer> newUserList = new ArrayList<>();
        //存放每天的总用户数量
        List<Integer> totalUserList = new ArrayList<>();


        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            //查询每天的新增用户数量
            Integer newUserCount = userMapper.getUserCount(beginTime, endTime);
            newUserList.add(newUserCount);
            //查询每天的总用户量
            Integer totalUserCount = userMapper.getTotalUserCount(endTime);
            totalUserList.add(totalUserCount);


        }
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();

    }

    /**
     * 统计指定时间区间内的订单数据
     */
    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        //获取begin到end的每日日期
        List<LocalDate> dateList = getDateList(begin, end);
        //总订单完成率
        Double totalOrderCompletionRate = 0.0;
        //订单数列表
        List<Integer> orderCountList = new ArrayList<>();
        //订单总数
        Integer totalOrderCounts = 0;
        //有效订单数
        Integer validOrderCounts = 0;
        //有效订单数列表
        List<Integer> validOrderCountList = new ArrayList<>();

        //遍历日期去查每天的数据
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer orderCount = orderMapper.getOrderCount(beginTime, endTime);
            Integer validOrderCount = orderMapper.getValidOrderCount(beginTime, endTime);
            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
            totalOrderCounts += orderCount;
            validOrderCounts += validOrderCount;
        }
        totalOrderCompletionRate = totalOrderCounts == 0 ? 0.0 : validOrderCounts.doubleValue() / totalOrderCounts;
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCounts)
                .validOrderCount(validOrderCounts)
                .orderCompletionRate(totalOrderCompletionRate)
                .build();

    }

    /**
     * 统计指定时间区间内的销量排名前10
     */
    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> goodsSalesDTOList = reportMapper.getTop10(beginTime, endTime);
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList()), ","))
                .numberList(StringUtils.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList()), ","))
                .build();

    }

    /**
     * 导出运营数据报表（最近30天营业额）
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        //1. 查询数据库，获取营业数据---查询最近30天的营业额
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);

        List<LocalDate> dateList = getDateList(dateBegin, dateEnd);
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Double turnover = reportMapper.getTurnover(beginTime, endTime);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }

        //2. 通过POI将数据写入到Excel文件中
        try (XSSFWorkbook excel = new XSSFWorkbook()) {
            XSSFSheet sheet = excel.createSheet("营业额统计");
            //表头
            XSSFRow header = sheet.createRow(0);
            header.createCell(0).setCellValue("日期");
            header.createCell(1).setCellValue("营业额");
            //数据
            for (int i = 0; i < dateList.size(); i++) {
                XSSFRow row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(dateList.get(i).toString());
                row.createCell(1).setCellValue(turnoverList.get(i));
            }

            //3. 通过输出流将Excel文件下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException("导出运营数据报表失败", e);
        }
    }

    /**
     * 获取从begin到end范围内的每一天的日期
     */
    private List<LocalDate> getDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate cur = begin;
        while (!cur.isAfter(end)) {
            dateList.add(cur);
            cur = cur.plusDays(1);
        }
        return dateList;
    }

    /**
     * 将日期集合拼接为逗号分隔的字符串
     */
    private String joinDateList(List<LocalDate> dateList) {
        return dateList.stream().map(LocalDate::toString).collect(Collectors.joining(","));
    }

    /**
     * 将数值集合拼接为逗号分隔的字符串
     */
    private String joinList(List<? extends Number> list) {
        return list.stream().map(String::valueOf).collect(Collectors.joining(","));
    }
}
