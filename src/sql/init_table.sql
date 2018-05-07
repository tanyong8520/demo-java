
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

-- 用户信息
DROP TABLE IF EXISTS `t_user_info`;
CREATE TABLE `t_user_info` (
  `uid` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) DEFAULT NULL COMMENT 'code',
  `password` varchar(100) DEFAULT NULL COMMENT 'password',
  `salt` varchar(100) DEFAULT NULL COMMENT '盐',
  `state` int(6) DEFAULT NULL COMMENT 'state',
  `username` varchar(100) DEFAULT NULL COMMENT 'username',
  `roleId` bigint(20) NOT NULL COMMENT '组织id',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

insert `t_user_info` (`name`, `password`, `salt`, `state`, `username`, `roleId`) VALUES ('test','test','test','1','test','1');

-- 组织
DROP TABLE IF EXISTS `t_role_info`;
CREATE TABLE `t_role_info` (
  `rid` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `available` int(6) DEFAULT NULL COMMENT 'available',
  `description` varchar(100) DEFAULT NULL COMMENT 'description',
  `role` varchar(100) DEFAULT NULL COMMENT 'rolename',
  PRIMARY KEY (`rid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
insert `t_role_info` (`available`, `description`, `role`) VALUES ('1','test','test');

-- 组织
DROP TABLE IF EXISTS `t_permission_info`;
CREATE TABLE `t_permission_info` (
  `pid` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `roleId` bigint(20) DEFAULT NULL COMMENT 'roleId',
  `available` int(6) DEFAULT NULL COMMENT 'available',
  `url` varchar(100) DEFAULT NULL COMMENT 'url',
  `name` varchar(100) DEFAULT NULL COMMENT 'name',
  PRIMARY KEY (`rid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
insert `t_permission_info` (`roleId`, `available`, `url`,`name`) VALUES ('1','1','\tany\index','test');
insert `t_permission_info` (`roleId`, `available`, `url`,`name`) VALUES ('1','1','\tany\test','test');