-- 创建community
/*Table structure for table `community` */

DROP TABLE IF EXISTS `community`;

CREATE TABLE `community` (
  `community_id` bigint(20) NOT NULL COMMENT '小区id',
  `community_name` varchar(128) DEFAULT NULL COMMENT '小区名称',
  `community_code` varchar(128) DEFAULT NULL COMMENT '小区编码',
  `community_province_code` varchar(32) DEFAULT NULL COMMENT '省区划码',
  `community_city_code` varchar(32) DEFAULT NULL COMMENT '市区划码',
  `community_town_code` varchar(32) DEFAULT NULL COMMENT '区县区划码',
  `community_detailed_address` varchar(128) DEFAULT NULL COMMENT '详细地址',
  `community_longitude` varchar(32) DEFAULT NULL COMMENT '经度',
  `community_latitude` varchar(32) DEFAULT NULL COMMENT '纬度',
  `dept_id` bigint(32) DEFAULT NULL COMMENT '物业id',
  `community_sort` int(11) DEFAULT NULL COMMENT '排序',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(1024) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`community_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='小区 ';

/*Data for the table `community` */
INSERT INTO `community` (
    `community_id`, `community_name`, `community_code`, `community_province_code`,
    `community_city_code`, `community_town_code`, `community_detailed_address`,
    `community_longitude`, `community_latitude`, `dept_id`, `community_sort`,
    `create_by`, `create_time`, `update_by`, `update_time`, `remark`
) VALUES
(1212512251212121212, '天鹅湖花园', 'COMMUNITY_1774811731293', '31', '3103', '310307', '江苏省无锡市滨湖区蠡湖大道', '120.25', '31.50',101,  NULL,'admin', '2024-03-30 03:15:31', 'admin', '2024-03-30 03:15:31', NULL),
(1256522511225511225, '南湖家园', 'COMMUNITY_16759457411000', '31', '3103', '310307', '江苏省无锡市滨湖区南湖大道', '120.29', '31.58', 102, NULL, 'admin', '2024-03-30 02:58:25', 'admin', '2024-03-30 04:00:45', NULL),
(1623660256618201090, '宏福苑小区', 'COMMUNITY_1675945745985', '1', '10', '1010', '北京市昌平区宏福苑', '115.78', '40.04', 103, NULL, 'admin', '2023-02-09 20:29:06', 'admin', '2023-02-09 20:29:06', NULL),
(1623660565545652154, '文水家园', 'COMMUNITY_1675945745211', '1', '10', '1013', '北京市房山区政通路', '116.50', '40.40', 124, NULL, 'admin', '2022-03-30 02:48:26', 'admin', '2022-03-30 02:48:41', NULL);
-- 
-- insert  into `community`(`community_id`,`community_name`,`community_code`,`community_province_code`,`community_city_code`,`community_town_code`,`community_detailed_address`,`community_longitude`,`community_latitude`,`dept_id`,`community_sort`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) values 
-- (1623660256618201090,'宏福苑小区','COMMUNITY_1675945745985','1','10','1010','北京市昌平区宏福苑',NULL,NULL,103,NULL,'admin','2023-02-09 20:29:06','admin','2023-02-09 20:29:06',NULL);
-- 
/*Table structure for table `community_interaction` *社区互动表/

DROP TABLE IF EXISTS `community_interaction`;

CREATE TABLE `community_interaction` (
  `interaction_id` bigint(20) NOT NULL COMMENT 'id',
  `community_id` bigint(20) DEFAULT NULL COMMENT '小区ID',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人ID',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新者ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `content` varchar(1024) DEFAULT NULL COMMENT '内容',
  `del_flag` int(2) DEFAULT '0' COMMENT '删除状态0默认1删除',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `user_id` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  PRIMARY KEY (`interaction_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='社区互动表';

/*Data for the table `community_interaction` */

