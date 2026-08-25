DROP TABLE IF EXISTS `sys_employee`;
DROP TABLE IF EXISTS `sys_bank`;
DROP TABLE IF EXISTS `sys_bank_dept`;
DROP TABLE IF EXISTS `sys_company`;
DROP TABLE IF EXISTS `sys_company_dept`;
DROP TABLE IF EXISTS `sys_market`;
DROP TABLE IF EXISTS `sys_market_dept`;
DROP TABLE IF EXISTS `sys_producer`;
DROP TABLE IF EXISTS `sys_producer_dept`;
DROP TABLE IF EXISTS `sys_supervise`;
DROP TABLE IF EXISTS `sys_supervise_dept`;
DROP TABLE IF EXISTS `sys_area`;

RENAME TABLE `sys_merchant` TO `sys_org`;
RENAME TABLE `sys_merchant_store` TO `sys_org_dept`;

ALTER TABLE `sys_org`
DROP COLUMN `area`,
DROP COLUMN `industry`,
DROP COLUMN `manager`,
DROP COLUMN `manager_id_number`,
DROP COLUMN `manager_id_address`,
DROP COLUMN `manager_phone`,
DROP COLUMN `contact`,
DROP COLUMN `contact_phone`,
DROP COLUMN `bank_account`,
DROP COLUMN `bank_man`,
DROP COLUMN `bank_card`,
DROP COLUMN `business_license_code`,
DROP COLUMN `business_org_code`,
DROP COLUMN `business_trc_code`,
DROP COLUMN `market_info`;

ALTER TABLE `sys_org`
ADD `type` tinyint(4) COMMENT '机构类型';

update `sys_org` set `type` = 3;

ALTER TABLE `sys_org_dept`
DROP COLUMN `address`,
DROP COLUMN `gps`;

ALTER TABLE `sys_org_dept`
ADD `type` tinyint(4) COMMENT '机构类型';

update `sys_org_dept` set `type` = 3;