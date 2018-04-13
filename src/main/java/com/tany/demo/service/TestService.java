package com.tany.demo.service;

import com.tany.demo.entity.TestEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author tany
 * @date 2018-04-13
 */
public interface TestService {
	
	TestEntity queryObject(Long id);
	
	List<TestEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(TestEntity test);
	
	void update(TestEntity test);
	
	void delete(Long id);
	
	void deleteBatch(Long[] ids);
}
