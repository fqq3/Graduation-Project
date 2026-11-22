SET NAMES utf8mb4;

DROP TABLE IF EXISTS `community_repair_order`;
CREATE TABLE `community_repair_order` (
  `repair_id` bigint NOT NULL COMMENT '报修ID',
  `repair_no` varchar(64) DEFAULT NULL COMMENT '报修单号',
  `owner_id` bigint DEFAULT NULL COMMENT '业主ID',
  `owner_name` varchar(50) DEFAULT NULL COMMENT '业主姓名',
  `room_id` bigint DEFAULT NULL COMMENT '房屋ID',
  `community_id` bigint DEFAULT NULL COMMENT '小区ID',
  `repair_title` varchar(100) DEFAULT NULL COMMENT '报修标题',
  `repair_type` varchar(32) DEFAULT NULL COMMENT '报修类型（水电 门禁 电梯 公共设施 其他）',
  `repair_location` varchar(255) DEFAULT NULL COMMENT '报修位置',
  `repair_content` varchar(1024) DEFAULT NULL COMMENT '报修内容',
  `contact_name` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `repair_images` varchar(1024) DEFAULT NULL COMMENT '报修图片',
  `audit_status` char(1) DEFAULT '0' COMMENT '审核状态（0待审核 1通过 2驳回）',
  `audit_opinion` varchar(500) DEFAULT NULL COMMENT '审核意见',
  `audit_by` varchar(32) DEFAULT NULL COMMENT '审核人',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `worker_name` varchar(50) DEFAULT NULL COMMENT '维修人员',
  `repair_start_time` datetime DEFAULT NULL COMMENT '维修开始时间',
  `repair_finish_time` datetime DEFAULT NULL COMMENT '维修完成时间',
  `repair_result` varchar(500) DEFAULT NULL COMMENT '维修结果',
  `repair_cost` decimal(10,2) DEFAULT NULL COMMENT '维修费用',
  `satisfaction` int DEFAULT NULL COMMENT '满意度评分（1-5）',
  `status` char(1) DEFAULT '0' COMMENT '工单状态（0待审核 1待维修 2维修中 3已完成 4已驳回）',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(1024) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`repair_id`),
  KEY `idx_repair_order_owner_id` (`owner_id`),
  KEY `idx_repair_order_room_id` (`room_id`),
  KEY `idx_repair_order_community_id` (`community_id`),
  KEY `idx_repair_order_no` (`repair_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='报修表';

DROP TEMPORARY TABLE IF EXISTS `tmp_repair_ref`;
CREATE TEMPORARY TABLE `tmp_repair_ref` AS
SELECT (@rownum := @rownum + 1) AS rn, t.*
FROM (
  SELECT DISTINCT od.community_id, od.room_id, o.owner_id, o.owner_real_name, o.owner_phone_number, r.room_name
  FROM `owner_room` od
  INNER JOIN `owner` o ON o.owner_id = od.owner_id
  INNER JOIN `room` r ON r.room_id = od.room_id
  ORDER BY od.owner_room_id
  LIMIT 4
) t, (SELECT @rownum := 0) x;

-- INSERT INTO `community_repair_order` (`repair_no`, `owner_id`, `owner_name`, `room_id`, `community_id`, `repair_title`, `repair_type`, `repair_location`, `repair_content`, `contact_name`, `contact_phone`, `repair_images`, `audit_status`, `audit_opinion`, `audit_by`, `audit_time`, `worker_name`, `repair_start_time`, `repair_finish_time`, `repair_result`, `repair_cost`, `satisfaction`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
-- SELECT CONCAT('BX202512', LPAD(rn, 4, '0')),
--        owner_id, owner_real_name, room_id, community_id,
--        CASE rn WHEN 1 THEN '厨房水管漏水' WHEN 2 THEN '门禁无法识别' WHEN 3 THEN '电梯异响' ELSE '楼道照明损坏' END,
--        CASE rn WHEN 1 THEN '水电' WHEN 2 THEN '门禁' WHEN 3 THEN '电梯' ELSE '公共设施' END,
--        CONCAT(IFNULL(room_name, '房屋'), CASE rn WHEN 4 THEN '楼道' ELSE '室内' END),
--        CASE rn WHEN 1 THEN '厨房下水管接口渗水，需要安排维修人员处理。' WHEN 2 THEN '业主刷卡后门禁无响应，需检查门禁设备。' WHEN 3 THEN '电梯运行过程中存在异响，需安排维保检查。' ELSE '楼道照明灯损坏，夜间通行不便。' END,
--        owner_real_name, owner_phone_number, NULL,
--        CASE rn WHEN 1 THEN '1' WHEN 2 THEN '1' WHEN 3 THEN '0' ELSE '1' END,
--        CASE rn WHEN 3 THEN NULL ELSE '审核通过，安排维修。' END,
--        CASE rn WHEN 3 THEN NULL ELSE 'admin' END,
--        CASE rn WHEN 3 THEN NULL ELSE '2025-12-30 10:00:00' END,
--        CASE rn WHEN 1 THEN '维修工A' WHEN 2 THEN '维修工B' WHEN 4 THEN '维修工C' ELSE NULL END,
--        CASE rn WHEN 3 THEN NULL ELSE '2025-12-30 10:30:00' END,
--        CASE rn WHEN 1 THEN '2025-12-30 11:30:00' WHEN 2 THEN NULL WHEN 4 THEN '2025-12-30 12:00:00' ELSE NULL END,
--        CASE rn WHEN 1 THEN '已更换水管接口' WHEN 2 THEN '维修处理中' WHEN 4 THEN '已更换照明灯' ELSE NULL END,
--        CASE rn WHEN 1 THEN 80.00 WHEN 4 THEN 30.00 ELSE NULL END,
--        CASE rn WHEN 1 THEN 5 WHEN 4 THEN 4 ELSE NULL END,
--        CASE rn WHEN 1 THEN '3' WHEN 2 THEN '2' WHEN 3 THEN '0' ELSE '3' END,
--        'admin', '2025-12-30 09:48:41', 'admin', '2025-12-30 09:48:41',
--        '根据现有业主、房屋和小区关系生成的报修工单'
-- FROM `tmp_repair_ref`;

INSERT INTO `community_repair_order` (`repair_id`, `repair_no`, `owner_id`, `owner_name`, `room_id`, `community_id`, `repair_title`, `repair_type`, `repair_location`, `repair_content`, `contact_name`, `contact_phone`, `repair_images`, `audit_status`, `audit_opinion`, `audit_by`, `audit_time`, `worker_name`, `repair_start_time`, `repair_finish_time`, `repair_result`, `repair_cost`, `satisfaction`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(2200000000000001001, 'BX2026041001', 1800000000000000001, '邓南森', 1700100010010000002, 1212512251212121212, '厨房水管漏水', '水电', '小区公共区域', '厨房水管漏水，请物业安排处理。', '邓南森', '13110000037', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2026-04-28 09:00:00', 'admin', '2026-04-28 10:00:00', '根据新版 owner 数据重新生成的报修数据'),
(2200000000000001002, 'BX2026041002', 1800000000000000002, '吕梅华', 1700100010010000003, 1212512251212121212, '门禁无法识别', '门禁', '小区公共区域', '门禁无法识别，请物业安排处理。', '吕梅华', '13220000074', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2026-04-28 09:00:00', 'admin', '2026-04-28 10:00:00', '根据新版 owner 数据重新生成的报修数据'),
(2200000000000001003, 'BX2026041003', 1800000000000000003, '萧飞南', 1700100010010000004, 1212512251212121212, '电梯异响', '电梯', '小区公共区域', '电梯异响，请物业安排处理。', '萧飞南', '13330000111', NULL, '1', '已通过', 'admin', '2026-04-28 09:30:00', '维修员A', '2026-04-28 10:00:00', '2026-04-28 11:30:00', '已完成维修', '110.00', 5, '3', 'admin', '2026-04-28 09:00:00', 'admin', '2026-04-28 10:00:00', '根据新版 owner 数据重新生成的报修数据'),
(2200000000000001004, 'BX2026041004', 1800000000000000004, '徐香娟', 1700100010010000006, 1212512251212121212, '公共照明损坏', '公共设施', '小区公共区域', '公共照明损坏，请物业安排处理。', '徐香娟', '13440000148', NULL, '1', '已通过', 'admin', '2026-04-28 09:30:00', '维修员A', '2026-04-28 10:00:00', '2026-04-28 11:30:00', '已完成维修', '130.00', 5, '3', 'admin', '2026-04-28 09:00:00', 'admin', '2026-04-28 10:00:00', '根据新版 owner 数据重新生成的报修数据'),
(2200000000000002001, 'BX2026042001', 1800000000000000164, '朱娜芳', 1700200010010000001, 1256522511225511225, '厨房水管漏水', '水电', '小区公共区域', '厨房水管漏水，请物业安排处理。', '朱娜芳', '13440006068', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2026-04-28 09:00:00', 'admin', '2026-04-28 10:00:00', '根据新版 owner 数据重新生成的报修数据'),
(2200000000000002002, 'BX2026042002', 1800000000000000165, '何海低', 1700200010010000002, 1256522511225511225, '门禁无法识别', '门禁', '小区公共区域', '门禁无法识别，请物业安排处理。', '何海低', '13550006105', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2026-04-28 09:00:00', 'admin', '2026-04-28 10:00:00', '根据新版 owner 数据重新生成的报修数据'),
(2200000000000002003, 'BX2026042003', 1800000000000000166, '傅涵甜', 1700200010010000003, 1256522511225511225, '电梯异响', '电梯', '小区公共区域', '电梯异响，请物业安排处理。', '傅涵甜', '13660006142', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2026-04-28 09:00:00', 'admin', '2026-04-28 10:00:00', '根据新版 owner 数据重新生成的报修数据'),
(2200000000000002004, 'BX2026042004', 1800000000000000167, '丁俊翔', 1700200010010000005, 1256522511225511225, '公共照明损坏', '公共设施', '小区公共区域', '公共照明损坏，请物业安排处理。', '丁俊翔', '13770006179', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2026-04-28 09:00:00', 'admin', '2026-04-28 10:00:00', '根据新版 owner 数据重新生成的报修数据'),
(2200000000000002005, 'BX2026042005', 1800000000000000168, '邓翠', 1700200010010000006, 1256522511225511225, '楼道墙面破损', '公共设施', '小区公共区域', '楼道墙面破损，请物业安排处理。', '邓翠', '13880006216', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2026-04-28 09:00:00', 'admin', '2026-04-28 10:00:00', '根据新版 owner 数据重新生成的报修数据'),
(2200000000000002006, 'BX2026042006', 1800000000000000169, '周川', 1700200010020000001, 1256522511225511225, '下水道堵塞', '水电', '小区公共区域', '下水道堵塞，请物业安排处理。', '周川', '13990006253', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2026-04-28 09:00:00', 'admin', '2026-04-28 10:00:00', '根据新版 owner 数据重新生成的报修数据'),
(2200000000000002007, 'BX2026042007', 1800000000000000170, '曹婷月', 1700200010020000002, 1256522511225511225, '消防门闭合异常', '公共设施', '小区公共区域', '消防门闭合异常，请物业安排处理。', '曹婷月', '13000006290', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2026-04-28 09:00:00', 'admin', '2026-04-28 10:00:00', '根据新版 owner 数据重新生成的报修数据'),
(2200000000000002008, 'BX2026042008', 1800000000000000171, '罗林', 1700200010020000004, 1256522511225511225, '单元门锁故障', '门禁', '小区公共区域', '单元门锁故障，请物业安排处理。', '罗林', '13110006327', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2026-04-28 09:00:00', 'admin', '2026-04-28 10:00:00', '根据新版 owner 数据重新生成的报修数据'),
(2200000000000002009, 'BX2026042009', 1800000000000000172, '薛静巧', 1700200010020000005, 1256522511225511225, '地下车库照明异常', '公共设施', '小区公共区域', '地下车库照明异常，请物业安排处理。', '薛静巧', '13220006364', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2026-04-28 09:00:00', 'admin', '2026-04-28 10:00:00', '根据新版 owner 数据重新生成的报修数据'),
(2200000000000002010, 'BX2026042010', 1800000000000000173, '高远', 1700200010020000006, 1256522511225511225, '电表读数异常', '水电', '小区公共区域', '电表读数异常，请物业安排处理。', '高远', '13330006401', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2026-04-28 09:00:00', 'admin', '2026-04-28 10:00:00', '根据新版 owner 数据重新生成的报修数据'),
(2200000000000002011, 'BX2026042011', 1800000000000000174, '黄华梅', 1700200010030000001, 1256522511225511225, '楼道感应灯失灵', '公共设施', '小区公共区域', '楼道感应灯失灵，请物业安排处理。', '黄华梅', '13440006438', NULL, '1', '已通过', 'admin', '2026-04-28 09:30:00', '维修员A', '2026-04-28 10:00:00', '2026-04-28 11:30:00', '已完成维修', '70.00', 5, '3', 'admin', '2026-04-28 09:00:00', 'admin', '2026-04-28 10:00:00', '根据新版 owner 数据重新生成的报修数据'),
(2200000000000002012, 'BX2026042012', 1800000000000000175, '苏云西', 1700200010030000003, 1256522511225511225, '弱电井异响', '其他', '小区公共区域', '弱电井异响，请物业安排处理。', '苏云西', '13550006475', NULL, '1', '已通过', 'admin', '2026-04-28 09:30:00', '维修员A', '2026-04-28 10:00:00', '2026-04-28 11:30:00', '已完成维修', '90.00', 5, '3', 'admin', '2026-04-28 09:00:00', 'admin', '2026-04-28 10:00:00', '根据新版 owner 数据重新生成的报修数据'),
(2200000000000003001, 'BX2026043001', 1800000000000000327, '赵高中', 1700300010010000001, 1623660256618201090, '厨房水管漏水', '水电', '小区公共区域', '厨房水管漏水，请物业安排处理。', '赵高中', '13770012099', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2026-04-28 09:00:00', 'admin', '2026-04-28 10:00:00', '根据新版 owner 数据重新生成的报修数据'),
(2200000000000003002, 'BX2026043002', 1800000000000000328, '孙美慧', 1700300010010000002, 1623660256618201090, '门禁无法识别', '门禁', '小区公共区域', '门禁无法识别，请物业安排处理。', '孙美慧', '13880012136', NULL, '1', '已通过', 'admin', '2026-04-28 09:30:00', '维修员A', '2026-04-28 10:00:00', '2026-04-28 11:30:00', '已完成维修', '90.00', 5, '3', 'admin', '2026-04-28 09:00:00', 'admin', '2026-04-28 10:00:00', '根据新版 owner 数据重新生成的报修数据'),
(2200000000000003003, 'BX2026043003', 1800000000000000329, '张东辉', 1700300010010000004, 1623660256618201090, '电梯异响', '电梯', '小区公共区域', '电梯异响，请物业安排处理。', '张东辉', '13990012173', NULL, '1', '已通过', 'admin', '2026-04-28 09:30:00', '维修员A', '2026-04-28 10:00:00', '2026-04-28 11:30:00', '已完成维修', '110.00', 5, '3', 'admin', '2026-04-28 09:00:00', 'admin', '2026-04-28 10:00:00', '根据新版 owner 数据重新生成的报修数据'),
(2200000000000004001, 'BX2026044001', 1800000000000000488, '张静芳', 1700400010010000001, 1623660565545652154, '厨房水管漏水', '水电', '小区公共区域', '厨房水管漏水，请物业安排处理。', '张静芳', '13880018056', NULL, '1', '已通过', 'admin', '2026-04-28 09:30:00', '维修员A', '2026-04-28 10:00:00', '2026-04-28 11:30:00', '已完成维修', '70.00', 5, '3', 'admin', '2026-04-28 09:00:00', 'admin', '2026-04-28 10:00:00', '根据新版 owner 数据重新生成的报修数据'),
(2200000000000004002, 'BX2026044002', 1800000000000000489, '赵川伟', 1700400010010000003, 1623660565545652154, '门禁无法识别', '门禁', '小区公共区域', '门禁无法识别，请物业安排处理。', '赵川伟', '13990018093', NULL, '1', '已通过', 'admin', '2026-04-28 09:30:00', '维修员A', '2026-04-28 10:00:00', '2026-04-28 11:30:00', '已完成维修', '90.00', 5, '3', 'admin', '2026-04-28 09:00:00', 'admin', '2026-04-28 10:00:00', '根据新版 owner 数据重新生成的报修数据');

