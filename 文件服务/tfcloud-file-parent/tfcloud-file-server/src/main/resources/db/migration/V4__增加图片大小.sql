ALTER TABLE `file_record`
ADD `width` int(11) DEFAULT NULL COMMENT '宽' after `content_type`,
ADD `height` int(11) DEFAULT NULL COMMENT '高' after `width`;