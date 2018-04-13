package com.tany.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.tany.demo.mapper.TestMapper;
import com.tany.demo.entity.TestEntity;
import com.tany.demo.service.TestService;



@Service("testService")
public class TestServiceImpl implements TestService {
	@Autowired
	private TestMapper testMapper;
	
	@Override
	public TestEntity queryObject(Long id){
		return testMapper.queryObject(id);
	}
	
	@Override
	public List<TestEntity> queryList(Map<String, Object> map){
		return testMapper.queryList(map);
	}
	
	@Override
	public int queryTotal(Map<String, Object> map){
		return testMapper.queryTotal(map);
	}
	
	@Override
	public void save(TestEntity test){
		testMapper.save(test);
	}
	
	@Override
	public void update(TestEntity test){
		testMapper.update(test);
	}
	
	@Override
	public void delete(Long id){
		testMapper.delete(id);
	}
	
	@Override
	public void deleteBatch(Long[] ids){
		testMapper.deleteBatch(ids);
	}
	
}
