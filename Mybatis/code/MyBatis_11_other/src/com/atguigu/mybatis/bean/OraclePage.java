package com.atguigu.mybatis.bean;

import java.util.List;

/**
 * 封装分页查询数据
 * @author lfy
 *
 */
public class OraclePage {
	
	private int start;
	private int end;
	private int count;
	private List<Employee> emps;
	
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public List<Employee> getEmps() {
		return emps;
	}
	public void setEmps(List<Employee> emps) {
		this.emps = emps;
	}
	
	

}
