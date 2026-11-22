SET NAMES utf8mb4;

DROP TABLE IF EXISTS `community_fee_item`;
CREATE TABLE `community_fee_item` (
  `item_id` bigint NOT NULL COMMENT '费用项目ID',
  `item_name` varchar(100) DEFAULT NULL COMMENT '费用项目名称',
  `item_type` varchar(32) DEFAULT NULL COMMENT '费用类型（物业费 停车费 维修费 水费 电费 其他）',
  `charge_standard` decimal(10,2) DEFAULT NULL COMMENT '收费标准',
  `charge_unit` varchar(32) DEFAULT NULL COMMENT '收费单位',
  `charge_cycle` varchar(32) DEFAULT NULL COMMENT '收费周期（月 季度 年 一次性）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0启用 1停用）',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(1024) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`item_id`),
  KEY `idx_fee_item_type` (`item_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='费用项目表';

INSERT INTO `community_fee_item` (`item_id`, `item_name`, `item_type`, `charge_standard`, `charge_unit`, `charge_cycle`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(1, '物业管理费', '物业费', 2.50, '元/㎡', '月', '0', 'admin', '2025-12-30 09:48:41', 'admin', '2025-12-30 09:48:41', '按房屋面积每月计费'),
(2, '固定车位月租费', '停车费', 300.00, '元/月', '月', '0', 'admin', '2025-12-30 09:48:41', 'admin', '2025-12-30 09:48:41', '固定停车位月租费用'),
(3, '公共维修服务费', '维修费', 50.00, '元/次', '一次性', '0', 'admin', '2025-12-30 09:48:41', 'admin', '2025-12-30 09:48:41', '公共区域或入户维修服务费用'),
(4, '生活水费', '水费', 4.20, '元/吨', '月', '0', 'admin', '2025-12-30 09:48:41', 'admin', '2025-12-30 09:48:41', '居民生活用水费用');
