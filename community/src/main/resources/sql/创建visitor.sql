
/*Table structure for table `visitor` */

DROP TABLE IF EXISTS `visitor`;

CREATE TABLE `visitor` (
  `visitor_id` bigint(20) NOT NULL COMMENT 'id',
  `community_id` bigint(20) DEFAULT NULL COMMENT '小区id',
  `visitor_name` varchar(32) DEFAULT NULL COMMENT '访客姓名',
  `visitor_phone_number` varchar(32) DEFAULT NULL COMMENT '访客手机号',
  `visitor_date` date DEFAULT NULL COMMENT '到访时间',
  `create_by_id` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `create_by_open_id` varchar(128) DEFAULT NULL COMMENT '创建人openid',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(1024) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`visitor_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='访客邀请 ';

/*Data for the table `visitor` */

-- insert  into `visitor`(`visitor_id`,`community_id`,`visitor_name`,`visitor_phone_number`,`visitor_date`,`create_by_id`,`create_by_open_id`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) values 
-- (1339800137985605634,1338423709557272577,'李四','15373604268','2020-12-18',1338485732744232962,'oo5qx4ue7md_S2mzNOtYgfcAQEBA',NULL,'2020-12-18 13:10:20',NULL,NULL,NULL),
-- (1339800756800634881,1338423709557272577,'王五','15373604268','2020-12-18',1338485732744232962,'oo5qx4ue7md_S2mzNOtYgfcAQEBA',NULL,'2020-12-18 13:12:47',NULL,NULL,NULL),
-- (1339801090516209665,1338423709557272577,'赵六','15376365248','2020-12-18',1338485732744232962,'oo5qx4ue7md_S2mzNOtYgfcAQEBA',NULL,'2020-12-18 13:14:07',NULL,NULL,NULL),
-- (1339801347446689793,1338423709557272577,'陈启','15373648597','2020-12-18',1338485732744232962,'oo5qx4ue7md_S2mzNOtYgfcAQEBA',NULL,'2020-12-18 13:15:08',NULL,NULL,NULL),
-- (1339801767145525250,1338423709557272577,'周年','15469857845','2020-12-18',1338485732744232962,'oo5qx4ue7md_S2mzNOtYgfcAQEBA',NULL,'2020-12-18 13:16:48',NULL,NULL,NULL),
-- (1339806635184795650,1338423709557272577,'哈喽','15636654789','2020-12-18',1338485732744232962,'oo5qx4ue7md_S2mzNOtYgfcAQEBA',NULL,'2020-12-18 13:36:09',NULL,NULL,NULL);


-- ============================================
-- 访客数据 (共20条)
-- ============================================

-- 天鹅湖花园 (community_id: 1212512251212121212)
INSERT INTO visitor (visitor_id, community_id, visitor_name, visitor_phone_number, visitor_date, create_by_id, create_by, create_time, update_by, update_time, remark) VALUES
(20001, 1212512251212121212, '李明', '13800001101', '2024-04-01', 1001, 'admin', '2024-04-01 09:30:00', 'admin', '2024-04-01 09:30:00', '拜访3栋502室张先生'),
(20002, 1212512251212121212, '王芳', '13900001102', '2024-04-02', 1002, 'admin', '2024-04-02 14:15:00', 'admin', '2024-04-02 14:15:00', '送快递'),
(20003, 1212512251212121212, '陈强', '13700001103', '2024-04-03', 1003, 'admin', '2024-04-03 10:45:00', 'admin', '2024-04-03 10:45:00', '维修空调'),
(20004, 1212512251212121212, '赵敏', '13600001104', '2024-04-05', 1004, 'admin', '2024-04-05 16:00:00', 'admin', '2024-04-05 16:00:00', '亲友聚会'),
(20005, 1212512251212121212, '孙伟', '13500001105', '2024-04-06', 1005, 'admin', '2024-04-06 08:20:00', 'admin', '2024-04-06 08:20:00', '送家具');

-- 南湖家园 (community_id: 1256522511225511225)
INSERT INTO visitor (visitor_id, community_id, visitor_name, visitor_phone_number, visitor_date, create_by_id, create_by, create_time, update_by, update_time, remark) VALUES
(20006, 1256522511225511225, '周杰', '13400001106', '2024-04-01', 1001, 'admin', '2024-04-01 11:00:00', 'admin', '2024-04-01 11:00:00', '看望老人'),
(20007, 1256522511225511225, '吴婷', '13300001107', '2024-04-02', 1002, 'admin', '2024-04-02 13:30:00', 'admin', '2024-04-02 13:30:00', '量房装修'),
(20008, 1256522511225511225, '郑宇', '13200001108', '2024-04-04', 1003, 'admin', '2024-04-04 09:15:00', 'admin', '2024-04-04 09:15:00', '中介带看'),
(20009, 1256522511225511225, '刘洋', '13100001109', '2024-04-06', 1004, 'admin', '2024-04-06 15:45:00', 'admin', '2024-04-06 15:45:00', '安装宽带'),
(20010, 1256522511225511225, '何静', '13000001110', '2024-04-07', 1005, 'admin', '2024-04-07 17:10:00', 'admin', '2024-04-07 17:10:00', '朋友拜访');

-- 宏福苑小区 (community_id: 1623660256618201090)
INSERT INTO visitor (visitor_id, community_id, visitor_name, visitor_phone_number, visitor_date, create_by_id, create_by, create_time, update_by, update_time, remark) VALUES
(20011, 1623660256618201090, '许峰', '18900001111', '2024-04-02', 1001, 'admin', '2024-04-02 10:30:00', 'admin', '2024-04-02 10:30:00', '送货上门'),
(20012, 1623660256618201090, '林小红', '18800001112', '2024-04-03', 1002, 'admin', '2024-04-03 12:00:00', 'admin', '2024-04-03 12:00:00', '家庭保洁服务'),
(20013, 1623660256618201090, '胡兵', '18700001113', '2024-04-05', 1003, 'admin', '2024-04-05 14:25:00', 'admin', '2024-04-05 14:25:00', '维修水管'),
(20014, 1623660256618201090, '马丽', '18600001114', '2024-04-07', 1004, 'admin', '2024-04-07 11:15:00', 'admin', '2024-04-07 11:15:00', '送花贺乔迁'),
(20015, 1623660256618201090, '张磊', '18500001115', '2024-04-08', 1005, 'admin', '2024-04-08 09:00:00', 'admin', '2024-04-08 09:00:00', '顺丰快递');

-- 文水家园 (community_id: 1623660565545652154)
INSERT INTO visitor (visitor_id, community_id, visitor_name, visitor_phone_number, visitor_date, create_by_id, create_by, create_time, update_by, update_time, remark) VALUES
(20016, 1623660565545652154, '李娟', '18400001116', '2024-04-01', 1001, 'admin', '2024-04-01 15:30:00', 'admin', '2024-04-01 15:30:00', '看房'),
(20017, 1623660565545652154, '王强', '18300001117', '2024-04-04', 1002, 'admin', '2024-04-04 10:00:00', 'admin', '2024-04-04 10:00:00', '送发票'),
(20018, 1623660565545652154, '黄燕', '18200001118', '2024-04-06', 1003, 'admin', '2024-04-06 16:30:00', 'admin', '2024-04-06 16:30:00', '聚餐'),
(20019, 1623660565545652154, '沈刚', '18100001119', '2024-04-08', 1004, 'admin', '2024-04-08 13:00:00', 'admin', '2024-04-08 13:00:00', '维修网络'),
(20020, 1623660565545652154, '宋佳', '18000001120', '2024-04-09', 1005, 'admin', '2024-04-09 09:45:00', 'admin', '2024-04-09 09:45:00', '送钥匙');
