package com.atguigu.mybatis.dao;

import java.util.List;

import com.atguigu.mybatis.bean.Employee;
import com.atguigu.mybatis.bean.OraclePage;

public interface EmployeeMapper {
	
	public Employee getEmpById(Integer id);
	
	public List<Employee> getEmps();
	
	public Long addEmp(Employee employee);
	
	public void getPageByProcedure(OraclePage page);
}
