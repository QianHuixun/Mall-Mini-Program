package cn.tofocus.lejia.bean.dto.data;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ReportOrderDTO {
	BigDecimal amto;
	BigDecimal postage;
	BigDecimal cardAmt;
	BigDecimal refundAmt;
}
