/*Table structure for table `sys_dept` */

DROP TABLE IF EXISTS `sys_dept`;

CREATE TABLE `sys_dept` (
  `dept_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父部门id',
  `ancestors` varchar(50) DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) DEFAULT '' COMMENT '部门名称',
  `order_num` int(4) DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(20) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `status` char(1) DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=111 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='部门表';

/*Data for the table `sys_dept` */

INSERT INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader`, `phone`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES
(1, 0, '', '总经理办公室', 1, '张总', '13812345678', 'zhang@example.com', '0', '0', 'admin', NOW(), 'admin', NOW()),
(2, 1, '1', '运营管理部', 2, '李经理', '13823456789', 'li@example.com', '0', '0', 'admin', NOW(), 'admin', NOW()),
(3, 1, '1', '人力资源部', 3, '王主管', '13834567890', 'wang@example.com', '0', '0', 'admin', NOW(), 'admin', NOW()),
(4, 1, '1', '财务部', 4, '赵会计', '13845678901', 'zhao@example.com', '0', '0', 'admin', NOW(), 'admin', NOW()),
(5, 1, '1', '法务部', 5, '孙律师', '13856789012', 'sun@example.com', '0', '0', 'admin', NOW(), 'admin', NOW()),
(6, 0, '', '客户服务部', 6, '钱客服', '13867890123', 'qian@example.com', '0', '0', 'admin', NOW(), 'admin', NOW()),
(7, 6, '6', '投诉处理部', 7, '吴经理', '13878901234', 'wu@example.com', '0', '0', 'admin', NOW(), 'admin', NOW()),
(8, 6, '6', '物业维修部', 8, '郑维修', '13889012345', 'zheng@example.com', '0', '0', 'admin', NOW(), 'admin', NOW()),
(9, 0, '', '技术支持部', 9, '冯技术', '13890123456', 'feng@example.com', '0', '0', 'admin', NOW(), 'admin', NOW()),
(10, 9, '9', 'IT服务部', 10, '陈IT', '13801234567', 'chen@example.com', '0', '0', 'admin', NOW(), 'admin', NOW()),
(11, 9, '9', '安全监控部', 11, '郑安监', '13812345678', 'zhengan@example.com', '0', '0', 'admin', NOW(), 'admin', NOW()),
(12, 0, '', '市场部', 12, '林市场', '13823456789', 'lin@example.com', '0', '0', 'admin', NOW(), 'admin', NOW()),
(13, 12, '12', '广告宣传部', 13, '刘宣传', '13834567890', 'liu@example.com', '0', '0', 'admin', NOW(), 'admin', NOW()),
(14, 12, '12', '公关部', 14, '郭公关', '13845678901', 'guo@example.com', '0', '0', 'admin', NOW(), 'admin', NOW()),
(15, 0, '', '后勤支持部', 15, '彭后勤', '13856789012', 'peng@example.com', '0', '0', 'admin', NOW(), 'admin', NOW()),
(16, 15, '15', '保洁服务部', 16, '杨保洁', '13867890123', 'yang@example.com', '0', '0', 'admin', NOW(), 'admin', NOW()),
(17, 15, '15', '绿化部门', 17, '胡绿化', '13878901234', 'hu@example.com', '0', '0', 'admin', NOW(), 'admin', NOW()),
(18, 15, '15', '设施维护部', 18, '高维护', '13889012345', 'gao@example.com', '0', '0', 'admin', NOW(), 'admin', NOW()),
(19, 0, '', '行政部', 19, '丁行政', '13890123456', 'ding@example.com', '0', '0', 'admin', NOW(), 'admin', NOW()),
(20, 0, '', '员工培训部', 20, '邹培训', '13801234567', 'zou@example.com', '0', '0', 'admin', NOW(), 'admin', NOW());


-- insert  into `sys_dept`(`dept_id`,`parent_id`,`ancestors`,`dept_name`,`order_num`,`leader`,`phone`,`email`,`status`,`del_flag`,`create_by`,`create_time`,`update_by`,`update_time`) values 
-- (100,0,'0','数字孪生物业管理系统',0,'spike','15888888888','msb@163.com','0','0','admin','2020-12-07 01:53:06','',NULL),
-- (101,100,'0,100','北京总公司',1,'spike','15888888888','msb@163.com','0','0','admin','2020-12-07 01:53:06','',NULL),
-- (102,100,'0,100','长沙分公司',2,'spike','15888888888','msb@163.com','0','0','admin','2020-12-07 01:53:06','',NULL),
-- (103,101,'0,100,101','研发部门',1,'spike','15888888888','msb@163.com','0','0','admin','2020-12-07 01:53:06','',NULL),
-- (104,101,'0,100,101','市场部门',2,'spike','15888888888','msb@163.com','0','0','admin','2020-12-07 01:53:06','',NULL),
-- (105,101,'0,100,101','测试部门',3,'spike','15888888888','msb@163.com','0','0','admin','2020-12-07 01:53:06','',NULL),
-- (106,101,'0,100,101','财务部门',4,'spike','15888888888','msb@163.com','0','0','admin','2020-12-07 01:53:06','',NULL),
-- (107,101,'0,100,101','运维部门',5,'spike','15888888888','msb@163.com','0','0','admin','2020-12-07 01:53:06','',NULL),
-- (108,102,'0,100,102','市场部门',1,'spike','15888888888','msb@163.com','0','0','admin','2020-12-07 01:53:06','',NULL),
-- (109,102,'0,100,102','财务部门',2,'spike','15888888888','msb@163.com','0','0','admin','2020-12-07 01:53:06','',NULL),
-- (110,100,'0,100','恒大城',3,NULL,NULL,NULL,'0','0','admin','2020-12-11 05:35:50','',NULL);
