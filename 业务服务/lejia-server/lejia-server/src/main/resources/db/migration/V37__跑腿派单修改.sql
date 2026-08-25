ALTER TABLE mkt_supply ADD settlement_method tinyint(4) DEFAULT 1 NOT NULL COMMENT '结算方式';
ALTER TABLE mkt_supply CHANGE settlement_method settlement_method tinyint(4) DEFAULT 1 NOT NULL COMMENT '结算方式'  AFTER purchasing_price;


ALTER TABLE mkt_supply ADD commission_rate1 decimal(11,2) DEFAULT NULL COMMENT '佣金费率1';
ALTER TABLE mkt_supply CHANGE commission_rate1 commission_rate1 decimal(11,2) DEFAULT NULL COMMENT '佣金费率1'  AFTER purchasing_price;


ALTER TABLE mkt_supply ADD commission_rate2 decimal(11,2) DEFAULT NULL COMMENT '佣金费率2';
ALTER TABLE mkt_supply CHANGE commission_rate2 commission_rate2 decimal(11,2) DEFAULT NULL COMMENT '佣金费率2' AFTER commission_rate1;

alter table mkt_supply MODIFY column purchasing_price decimal(19,2) DEFAULT NULL COMMENT '采购价';

