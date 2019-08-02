package com.oristar.mapper;

import com.oristar.domain.ActivityRule;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author lgp
 * 数据库连接类
 */
@Mapper
public interface ActivityRuleMapper {

    ActivityRule selectByPrimaryKey(Integer id);

    List<ActivityRule> selectAll();

}