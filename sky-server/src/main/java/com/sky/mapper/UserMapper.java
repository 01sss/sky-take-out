package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author: Tan
 * @createTime: 2024/09/12 16:08
 * @description:
 */
@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 插入数据
     * @param user
     */
    void insert(User user);

    /**
     * 根据id查询用户数据
     * @param userId
     * @return
     */
    @Select("select * from user where id = #{id}")
    User getById(Long userId);

    /**
     * 查询当前日期区间内的新增用户数量
     * @param beginTime
     * @param endTime
     * @return
     */
    @Select("select count(id) from user where create_time > #{beginTime} and create_time < #{endTime}")
    Integer getNewUserByTime(LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 查询当前日期区间内的总用户数量
     * @param endTime
     * @return
     */
    @Select("select count(id) from user where create_time < #{endTime}")
    Integer getTotalUserByTime(LocalDateTime endTime);


    /**
     * 计算新增用户数
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
