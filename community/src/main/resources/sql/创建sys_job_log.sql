/*Table structure for table `sys_job_log` */

DROP TABLE IF EXISTS `sys_job_log`;

CREATE TABLE `sys_job_log` (
  `job_log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) DEFAULT NULL COMMENT '日志信息',
  `status` char(1) DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) DEFAULT '' COMMENT '异常信息',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='定时任务调度日志表';

/*Data for the table `sys_job_log` */

insert  into `sys_job_log`(`job_log_id`,`job_name`,`job_group`,`invoke_target`,`job_message`,`status`,`exception_info`,`create_time`) values 
(1,'系统默认（无参）','DEFAULT','ryTask.ryNoParams','系统默认（无参） 总共耗时：5毫秒','0','','2020-12-08 06:30:39'),
(2,'系统默认（无参）','DEFAULT','ryTask.ryNoParams','系统默认（无参） 总共耗时：1毫秒','0','','2020-12-08 06:31:09'),
(3,'系统默认（无参）','DEFAULT','ryTask.ryNoParams','系统默认（无参） 总共耗时：0毫秒','0','','2020-12-08 06:31:29'),
(4,'系统默认（无参）','DEFAULT','ryTask.ryNoParams','系统默认（无参） 总共耗时：0毫秒','0','','2020-12-08 06:31:30');
