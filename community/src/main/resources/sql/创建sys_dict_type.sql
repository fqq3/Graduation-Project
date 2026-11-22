/*Table structure for table `sys_dict_type` */

DROP TABLE IF EXISTS `sys_dict_type`;

CREATE TABLE `sys_dict_type` (
  `dict_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`) USING BTREE,
  UNIQUE KEY `dict_type` (`dict_type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='字典类型表';

/*Data for the table `sys_dict_type` */

insert  into `sys_dict_type`(`dict_id`,`dict_name`,`dict_type`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) values 
(1,'用户性别','sys_user_sex','0','admin','2020-12-07 01:53:06','',NULL,'用户性别列表'),
(2,'菜单状态','sys_show_hide','0','admin','2020-12-07 01:53:06','',NULL,'菜单状态列表'),
(3,'系统开关','sys_normal_disable','0','admin','2020-12-07 01:53:06','',NULL,'系统开关列表'),
(4,'任务状态','sys_job_status','0','admin','2020-12-07 01:53:06','',NULL,'任务状态列表'),
(5,'任务分组','sys_job_group','0','admin','2020-12-07 01:53:06','',NULL,'任务分组列表'),
(6,'系统是否','sys_yes_no','0','admin','2020-12-07 01:53:06','',NULL,'系统是否列表'),
(7,'通知类型','sys_notice_type','0','admin','2020-12-07 01:53:06','',NULL,'通知类型列表'),
(8,'通知状态','sys_notice_status','0','admin','2020-12-07 01:53:06','',NULL,'通知状态列表'),
(9,'操作类型','sys_oper_type','0','admin','2020-12-07 01:53:06','',NULL,'操作类型列表'),
(10,'系统状态','sys_common_status','0','admin','2020-12-07 01:53:06','',NULL,'登录状态列表'),
(11,'房屋状态','room_state','0','admin','2020-12-14 08:33:36','',NULL,NULL),
(12,'房屋户型','house_state','0','admin','2020-12-14 09:03:38','admin','2020-12-15 08:00:15',NULL),
(13,'业主类型','owner_state','0','admin','2020-12-15 08:01:14','',NULL,NULL);

