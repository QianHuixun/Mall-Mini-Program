ALTER TABLE `sys_user`
DROP INDEX `idx_phone` ,
DROP INDEX `idx_email` ,
DROP INDEX `idx_userid`,
ADD INDEX `idx_phone` (`bind_phone`) USING BTREE,
ADD INDEX `idx_email` (`bind_email`) USING BTREE,
ADD INDEX `idx_userid` (`userid`) USING BTREE
