package com.tany.demo.mapper;

import java.util.List;
import java.util.Map;

import com.tany.demo.entity.TestAndSumEntity;
import com.tany.demo.entity.TestEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * 
 * @author tany
 * @date 2018-04-13
 */
@Mapper
public interface TestMapper {

    TestAndSumEntity queryTestAndSum(Map<String, Object> map);

    TestEntity queryObject(Object id);

    List<TestEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(TestEntity t);

    void save(Map<String, Object> map);

    int update(TestEntity t);

    int update(Map<String, Object> map);

    int delete(Object value);

    int deleteBatch(Object[] ids);
}
