
/*Table structure for table `sys_dict_data` */

DROP TABLE IF EXISTS `sys_dict_data`;

CREATE TABLE `sys_dict_data` (
  `dict_code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int(4) DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='字典数据表';

/*Data for the table `sys_dict_data` */

insert  into `sys_dict_data`(`dict_code`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) values 
(1,1,'男','0','sys_user_sex','','','Y','0','admin','2020-12-07 01:53:06','',NULL,'性别男'),
(2,2,'女','1','sys_user_sex','','','N','0','admin','2020-12-07 01:53:06','',NULL,'性别女'),
(3,3,'未知','2','sys_user_sex','','','N','0','admin','2020-12-07 01:53:06','',NULL,'性别未知'),
(4,1,'显示','0','sys_show_hide','','primary','Y','0','admin','2020-12-07 01:53:06','',NULL,'显示菜单'),
(5,2,'隐藏','1','sys_show_hide','','danger','N','0','admin','2020-12-07 01:53:06','',NULL,'隐藏菜单'),
(6,1,'正常','0','sys_normal_disable','','primary','Y','0','admin','2020-12-07 01:53:06','',NULL,'正常状态'),
(7,2,'停用','1','sys_normal_disable','','danger','N','0','admin','2020-12-07 01:53:06','',NULL,'停用状态'),
(8,1,'正常','0','sys_job_status','','primary','Y','0','admin','2020-12-07 01:53:06','',NULL,'正常状态'),
(9,2,'暂停','1','sys_job_status','','danger','N','0','admin','2020-12-07 01:53:06','',NULL,'停用状态'),
(10,1,'默认','DEFAULT','sys_job_group','','','Y','0','admin','2020-12-07 01:53:06','',NULL,'默认分组'),
(11,2,'系统','SYSTEM','sys_job_group','','','N','0','admin','2020-12-07 01:53:06','',NULL,'系统分组'),
(12,1,'是','Y','sys_yes_no','','primary','Y','0','admin','2020-12-07 01:53:06','',NULL,'系统默认是'),
(13,2,'否','N','sys_yes_no','','danger','N','0','admin','2020-12-07 01:53:06','',NULL,'系统默认否'),
(14,1,'通知','1','sys_notice_type','','warning','Y','0','admin','2020-12-07 01:53:06','',NULL,'通知'),
(15,2,'公告','2','sys_notice_type','','success','N','0','admin','2020-12-07 01:53:06','',NULL,'公告'),
(16,1,'正常','0','sys_notice_status','','primary','Y','0','admin','2020-12-07 01:53:06','',NULL,'正常状态'),
(17,2,'关闭','1','sys_notice_status','','danger','N','0','admin','2020-12-07 01:53:06','',NULL,'关闭状态'),
(18,1,'新增','1','sys_oper_type','','info','N','0','admin','2020-12-07 01:53:06','',NULL,'新增操作'),
(19,2,'修改','2','sys_oper_type','','info','N','0','admin','2020-12-07 01:53:06','',NULL,'修改操作'),
(20,3,'删除','3','sys_oper_type','','danger','N','0','admin','2020-12-07 01:53:06','',NULL,'删除操作'),
(21,4,'授权','4','sys_oper_type','','primary','N','0','admin','2020-12-07 01:53:06','',NULL,'授权操作'),
(22,5,'导出','5','sys_oper_type','','warning','N','0','admin','2020-12-07 01:53:06','',NULL,'导出操作'),
(23,6,'导入','6','sys_oper_type','','warning','N','0','admin','2020-12-07 01:53:06','',NULL,'导入操作'),
(24,7,'强退','7','sys_oper_type','','danger','N','0','admin','2020-12-07 01:53:06','',NULL,'强退操作'),
(25,8,'生成代码','8','sys_oper_type','','warning','N','0','admin','2020-12-07 01:53:06','',NULL,'生成操作'),
(26,9,'清空数据','9','sys_oper_type','','danger','N','0','admin','2020-12-07 01:53:06','',NULL,'清空操作'),
(27,1,'成功','0','sys_common_status','','primary','N','0','admin','2020-12-07 01:53:06','',NULL,'正常状态'),
(28,2,'失败','1','sys_common_status','','danger','N','0','admin','2020-12-07 01:53:06','',NULL,'停用状态'),
(29,1,'未出售','none','room_state',NULL,NULL,'N','0','admin','2020-12-14 08:34:23','',NULL,NULL),
(30,2,'已交房','has_give','room_state',NULL,NULL,'N','0','admin','2020-12-14 08:34:38','',NULL,NULL),
(31,3,'未入住','none_stay','room_state',NULL,NULL,'N','0','admin','2020-12-14 08:34:56','',NULL,NULL),
(32,4,'已入住','has_stay','room_state',NULL,NULL,'N','0','admin','2020-12-14 08:35:12','',NULL,NULL),
(33,1,'开间','open_house','house_state',NULL,NULL,'N','0','admin','2020-12-14 09:04:09','admin','2020-12-14 09:04:16',NULL),
(34,2,'一室一厅一卫','one_house','house_state',NULL,NULL,'N','0','admin','2020-12-14 09:04:35','',NULL,NULL),
(35,3,'两室一厅一卫','two_house','house_state',NULL,NULL,'N','0','admin','2020-12-14 09:04:56','',NULL,NULL),
(36,4,'三室两厅两卫','three_house','house_state',NULL,NULL,'N','0','admin','2020-12-14 09:05:13','',NULL,NULL),
(37,5,'其他','other','house_state',NULL,NULL,'N','0','admin','2020-12-14 09:05:33','',NULL,NULL),
(38,0,'业主','yz','owner_state',NULL,NULL,'N','0','admin','2020-12-15 08:01:36','',NULL,NULL),
(39,1,'租户','zh','owner_state',NULL,NULL,'N','0','admin','2020-12-15 08:01:48','',NULL,NULL),
(40,2,'亲属','qs','owner_state',NULL,NULL,'N','0','admin','2020-12-15 08:02:02','',NULL,NULL),
(41,3,'其他','qt','owner_state',NULL,NULL,'N','0','admin','2020-12-15 08:02:16','',NULL,NULL);
