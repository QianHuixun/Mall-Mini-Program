
ALTER TABLE `file_ref`
ADD `ref_count` int(11) DEFAULT NULL COMMENT '引用数量' after `ref_url`;

Update `file_ref` set `ref_count` = 1, `ref_url` = '旧记录';