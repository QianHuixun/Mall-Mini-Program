
---------------------------------------------------
-----------------修改物品团购表---------------
--------------------------------------



ALTER TABLE mkt_goods ADD pickup_type  tinyint(1)  NULL COMMENT '是否自提' AFTER  guess_like

