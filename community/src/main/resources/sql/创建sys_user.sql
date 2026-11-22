/*Table structure for table `sys_user` */

DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) DEFAULT '' COMMENT '手机号码',
  `sex` char(1) DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) DEFAULT '' COMMENT '密码',
  `status` char(1) DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(50) DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=103 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户信息表';

/*Data for the table `sys_user` */

insert  into `sys_user`(`user_id`,`dept_id`,`user_name`,`nick_name`,`user_type`,`email`,`phonenumber`,`sex`,`avatar`,`password`,`status`,`del_flag`,`login_ip`,`login_date`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) values 
(1,103,'admin','msb','00','msb@163.com','15888888888','0','','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','0','0','127.0.0.1','2020-12-07 01:53:06','admin','2020-12-07 01:53:06','','2021-03-19 10:06:50','管理员 admin123'),
(2,105,'jt','tuantuan','00','msb@qq.com','15666666666','1','','$2a$10$negHDmyk8QrsymSdjCgEXuEoPonSshoPBVKf1aPpUMVyzsrmScMkW','0','2','127.0.0.1','2020-12-07 01:53:06','admin','2020-12-07 01:53:06','jt','2020-12-25 02:23:22','测试员'),
(100,103,'test','test','00','sadad@qwdwq.com','13011111111','0','','$2a$10$oATgmRttyTCF2ZjBKh.4de7ii/OeEmGvWgmFSJIKgMZ3em5Iwxl7G','0','0','',NULL,'admin','2020-12-08 07:31:09','admin','2020-12-25 08:02:33',NULL),
(101,100,'sokie','spike','00','123@msb.com','13500008888','0','','$2a$10$9sO3zwwZvCsBx5RLFzdAhuqvnisgwNssT4PVBTeh1ufWG4.lbZIq.','0','2','',NULL,'admin','2023-02-20 15:39:15','admin','2023-02-20 15:39:39',NULL),
(102,100,'laoli',' wowowo','00','123456@msb.com','13522102210','0','','$2a$10$t4A7DhVsVd.XMjZDSOQ3RuXQwqFPSQ/DYFUZBJKbM9RJzaPh3SjIm','0','0','',NULL,'admin','2023-02-20 15:41:49','',NULL,NULL);
