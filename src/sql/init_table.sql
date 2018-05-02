
DROP TABLE IF EXISTS `t_test`;
CREATE TABLE `t_test` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `alert_id` bigint(20) DEFAULT NULL COMMENT '业务ID',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `data` text COMMENT '数据',
  `desc` varchar(500) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

INSERT INTO `alert`.`t_test` (`alert_id`, `create_time`, `data`, `desc`) VALUES ('1', '2018-04-13 10:00:00', '1', '1');
INSERT INTO `alert`.`t_test` (`alert_id`, `create_time`, `data`, `desc`) VALUES ('2', '2018-04-13 10:10:00', '1', '1');

DROP TABLE IF EXISTS `t_sum_talbe`;
CREATE TABLE `t_sum_table` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '业务ID',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `data` text COMMENT '数据',
  `desc` varchar(500) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

INSERT INTO `alert`.`t_sum_table` (`parent_id`, `create_time`, `data`, `desc`) VALUES ('1', '2018-04-13 10:00:00', '1', '1');
INSERT INTO `alert`.`t_sum_table` (`parent_id`, `create_time`, `data`, `desc`) VALUES ('1', '2018-04-13 10:01:00', '1', '1');

INSERT INTO `alert`.`t_sum_table` (`parent_id`, `create_time`, `data`, `desc`) VALUES ('2', '2018-04-13 10:00:00', '1', '1');
INSERT INTO `alert`.`t_sum_table` (`parent_id`, `create_time`, `data`, `desc`) VALUES ('2', '2018-04-13 10:01:00', '1', '1');