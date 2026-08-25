package cn.tofocus.lejia.bean.dto.app.market;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class AppMemberSignOnList {

	
	private List<Integer> signDates= new ArrayList<>();
	
	private Integer signNum;
	
	// 对应页面上的积分
	private Integer points;
	private Boolean nowDays;
//	// 全部积分信息
//	private List<Map<String,Object>> allPoints = new ArrayList<>();
//	// 收入积分信息
//	private List<Map<String,Object>> incomePoints = new ArrayList<>();
//	// 支出积分信息
//	private List<Map<String,Object>> expenditurePoints = new ArrayList<>();
}
