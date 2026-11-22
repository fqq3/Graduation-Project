/*Table structure for table `complaint_suggest`投诉表 */

DROP TABLE IF EXISTS `complaint_suggest`;

CREATE TABLE `complaint_suggest` (
  `complaint_suggest_id` bigint(20) NOT NULL COMMENT 'id',
  `community_id` bigint(20) DEFAULT NULL COMMENT '小区id',
  `complaint_suggest_type` varchar(32) DEFAULT NULL COMMENT '类型(投诉、建议)',
  `complaint_suggest_content` varchar(3072) DEFAULT NULL COMMENT '内容',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(1024) DEFAULT NULL COMMENT '备注',
  `user_id` bigint(20) DEFAULT NULL COMMENT '投诉人ID',
  PRIMARY KEY (`complaint_suggest_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='投诉建议 ';

/*Data for the table `complaint_suggest` */

-- insert  into `complaint_suggest`(`complaint_suggest_id`,`community_id`,`complaint_suggest_type`,`complaint_suggest_content`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`,`user_id`) values 
-- (1339922660207263746,1338423709557272577,'Suggest','我是一个投诉',NULL,'2020-12-18 21:17:11',NULL,NULL,NULL,1338485732744232962),
-- (1339922858014834689,1338423709557272577,'Complaint','隔壁楼老是有噪音',NULL,'2020-12-18 21:17:59',NULL,NULL,NULL,1338485732744232962),
-- (1339926422765015042,1338423709557272577,'Suggest','我是一个投诉',NULL,'2020-12-18 21:32:08',NULL,NULL,NULL,1338485732744232962),
-- (1339926698737635330,1338423709557272577,'Suggest','管理不合格',NULL,'2020-12-18 21:33:14',NULL,NULL,NULL,1338673355416272897),
-- (1341246458851069954,1338423709557272577,'Suggest','我是一个投诉',NULL,'2020-12-22 12:57:30',NULL,NULL,NULL,1338485732744232962),
-- (1341317588676874241,1338423709557272577,'Complaint','垃圾房周围太丑了',NULL,'2020-12-22 17:40:08',NULL,NULL,NULL,1339439232424800258);
-- ============================================
-- 小区：天鹅湖花园 (community_id: 1212512251212121212)
-- ============================================
-- 建议1-5
INSERT INTO complaint_suggest (complaint_suggest_id, community_id, complaint_suggest_type, complaint_suggest_content, create_by, create_time, update_by, update_time, remark, user_id) VALUES
(10001, 1212512251212121212, '建议', '希望能在小区东门增设非机动车充电桩，方便电动自行车充电。', 'admin', '2024-03-30 12:00:00', 'admin', '2024-03-30 12:00:00', NULL, 1001),
(10002, 1212512251212121212, '建议', '建议将中心花园的草坪改为耐踩踏品种，或铺设石板路，避免雨天泥泞。', 'admin', '2024-03-30 12:01:00', 'admin', '2024-03-30 12:01:00', NULL, 1002),
(10003, 1212512251212121212, '建议', '小区电梯已运行多年，建议将电梯维保频率从每月1次增加到每月2次，并公示维保记录。', 'admin', '2024-03-30 12:02:00', 'admin', '2024-03-30 12:02:00', NULL, 1003),
(10004, 1212512251212121212, '建议', '地库出入口缺少反光镜，早晚高峰经常发生剐蹭，建议加装广角镜。', 'admin', '2024-03-30 12:03:00', 'admin', '2024-03-30 12:03:00', NULL, 1004),
(10005, 1212512251212121212, '建议', '建议在每栋楼架空层增加旧衣物回收箱，响应垃圾分类同时方便业主。', 'admin', '2024-03-30 12:04:00', 'admin', '2024-03-30 12:04:00', NULL, 1005);

-- 投诉1-3
INSERT INTO complaint_suggest (complaint_suggest_id, community_id, complaint_suggest_type, complaint_suggest_content, create_by, create_time, update_by, update_time, remark, user_id) VALUES
(10006, 1212512251212121212, '投诉', '3号楼2单元楼下建筑垃圾堆放点长期未清理，已经堆积到人行道，异味严重。', 'admin', '2024-03-30 12:05:00', 'admin', '2024-03-30 12:05:00', NULL, 1006),
(10007, 1212512251212121212, '投诉', '隔壁5栋1802室周末早晨7点就开始装修，电钻声巨大，严重影响休息，已向物业反映多次无果。', 'admin', '2024-03-30 12:06:00', 'admin', '2024-03-30 12:06:00', NULL, 1007),
(10008, 1212512251212121212, '投诉', '小区游泳池自去年夏天后一直没有开放，也未公示原因，池水已发绿，存在安全隐患且浪费公共资源。', 'admin', '2024-03-30 12:07:00', 'admin', '2024-03-30 12:07:00', NULL, 1008);

-- ============================================
-- 小区：南湖家园 (community_id: 1256522511225511225)
-- ============================================
-- 建议1-5
INSERT INTO complaint_suggest (complaint_suggest_id, community_id, complaint_suggest_type, complaint_suggest_content, create_by, create_time, update_by, update_time, remark, user_id) VALUES
(10009, 1256522511225511225, '建议', '建议在南门入口加装人脸识别门禁，提高通行效率，减轻保安核验压力。', 'admin', '2024-03-30 12:10:00', 'admin', '2024-03-30 12:10:00', NULL, 1001),
(10010, 1256522511225511225, '建议', '儿童游乐场的塑胶地面有多处破损，建议尽快修补，防止孩子摔伤。', 'admin', '2024-03-30 12:11:00', 'admin', '2024-03-30 12:11:00', NULL, 1002),
(10011, 1256522511225511225, '建议', '希望物业服务号能增加夜间值班电话的自动回复功能，方便业主紧急报修。', 'admin', '2024-03-30 12:12:00', 'admin', '2024-03-30 12:12:00', NULL, 1003),
(10012, 1256522511225511225, '建议', '小区树木多年未修剪，部分枝条已伸入住户阳台，建议统一安排修剪。', 'admin', '2024-03-30 12:13:00', 'admin', '2024-03-30 12:13:00', NULL, 1004),
(10013, 1256522511225511225, '建议', '建议地下车库增加新能源车充电桩数量，目前仅有的2个经常需要排队。', 'admin', '2024-03-30 12:14:00', 'admin', '2024-03-30 12:14:00', NULL, 1005);

-- 投诉1-3
INSERT INTO complaint_suggest (complaint_suggest_id, community_id, complaint_suggest_type, complaint_suggest_content, create_by, create_time, update_by, update_time, remark, user_id) VALUES
(10014, 1256522511225511225, '投诉', '7号楼电梯故障频繁，本周已经困人两次，维保后依然出现异响，强烈要求彻底检修。', 'admin', '2024-03-30 12:15:00', 'admin', '2024-03-30 12:15:00', NULL, 1006),
(10015, 1256522511225511225, '投诉', '小区门口夜间灯光太暗，保安亭也常无人值守，陌生人可随意进出，安全系数低。', 'admin', '2024-03-30 12:16:00', 'admin', '2024-03-30 12:16:00', NULL, 1007),
(10016, 1256522511225511225, '投诉', '物业管家服务态度极差，报修了半个月的墙面渗水至今未处理，每次打电话都推诿。', 'admin', '2024-03-30 12:17:00', 'admin', '2024-03-30 12:17:00', NULL, 1008);

-- ============================================
-- 小区：宏福苑小区 (community_id: 1623660256618201090)
-- ============================================
-- 建议1-5
INSERT INTO complaint_suggest (complaint_suggest_id, community_id, complaint_suggest_type, complaint_suggest_content, create_by, create_time, update_by, update_time, remark, user_id) VALUES
(10017, 1623660256618201090, '建议', '建议在小区中央广场增设遮阳棚和长椅，方便老人休息。', 'admin', '2024-03-30 12:20:00', 'admin', '2024-03-30 12:20:00', NULL, 1001),
(10018, 1623660256618201090, '建议', '快递经常被随意堆放在超市门口，建议引入快递柜或设立规范的快递驿站。', 'admin', '2024-03-30 12:21:00', 'admin', '2024-03-30 12:21:00', NULL, 1002),
(10019, 1623660256618201090, '建议', '建议物业定期举办消防演练，很多住户不会使用楼道内的灭火器。', 'admin', '2024-03-30 12:22:00', 'admin', '2024-03-30 12:22:00', NULL, 1003),
(10020, 1623660256618201090, '建议', '小区内道路多处井盖下陷，行车颠簸且有噪音，建议统一调整至与路面齐平。', 'admin', '2024-03-30 12:23:00', 'admin', '2024-03-30 12:23:00', NULL, 1004),
(10021, 1623660256618201090, '建议', '希望能开通线上缴纳物业费及停车费的功能，减少跑腿。', 'admin', '2024-03-30 12:24:00', 'admin', '2024-03-30 12:24:00', NULL, 1005);

-- 投诉1-3
INSERT INTO complaint_suggest (complaint_suggest_id, community_id, complaint_suggest_type, complaint_suggest_content, create_by, create_time, update_by, update_time, remark, user_id) VALUES
(10022, 1623660256618201090, '投诉', '1号楼1单元外墙瓷砖脱落，下面就是人行通道，非常危险，却只拉了警戒线无人维修。', 'admin', '2024-03-30 12:25:00', 'admin', '2024-03-30 12:25:00', NULL, 1006),
(10023, 1623660256618201090, '投诉', '小区内私家车乱停，占用消防通道，多次反映后物业只是象征性贴条，未根本解决。', 'admin', '2024-03-30 12:26:00', 'admin', '2024-03-30 12:26:00', '已转交秩序部跟进', 1007),
(10024, 1623660256618201090, '投诉', '垃圾清运车每天凌晨4点作业，噪音扰民，建议将清运时间调整到早晨7点以后。', 'admin', '2024-03-30 12:27:00', 'admin', '2024-03-30 12:27:00', NULL, 1008);

-- ============================================
-- 小区：文水家园 (community_id: 1623660565545652154)
-- ============================================
-- 建议1-5
INSERT INTO complaint_suggest (complaint_suggest_id, community_id, complaint_suggest_type, complaint_suggest_content, create_by, create_time, update_by, update_time, remark, user_id) VALUES
(10025, 1623660565545652154, '建议', '建议将小区北侧废弃喷泉改造成花坛或健身区，既美观又实用。', 'admin', '2024-03-30 12:30:00', 'admin', '2024-03-30 12:30:00', NULL, 1001),
(10026, 1623660565545652154, '建议', '很多业主反映小区标识牌不清晰，亲友来访经常迷路，建议更换成带夜光功能的导示牌。', 'admin', '2024-03-30 12:31:00', 'admin', '2024-03-30 12:31:00', NULL, 1002),
(10027, 1623660565545652154, '建议', '希望定期组织物业开放日，让业主了解水泵房、配电房等设备运行情况。', 'admin', '2024-03-30 12:32:00', 'admin', '2024-03-30 12:32:00', NULL, 1003),
(10028, 1623660565545652154, '建议', '建议在每层楼道内安装烟感报警器并与中控室联动，提升消防预警能力。', 'admin', '2024-03-30 12:33:00', 'admin', '2024-03-30 12:33:00', NULL, 1004),
(10029, 1623660565545652154, '建议', '小区内养犬居民多，建议设置专门的宠物便纸箱并加强文明养犬宣传。', 'admin', '2024-03-30 12:34:00', 'admin', '2024-03-30 12:34:00', NULL, 1005);

-- 投诉1-3
INSERT INTO complaint_suggest (complaint_suggest_id, community_id, complaint_suggest_type, complaint_suggest_content, create_by, create_time, update_by, update_time, remark, user_id) VALUES
(10030, 1623660565545652154, '投诉', '地下室水泵噪音巨大，影响1单元低层住户夜间休息，多次沟通未解决。', 'admin', '2024-03-30 12:35:00', 'admin', '2024-03-30 12:35:00', NULL, 1006),
(10031, 1623660565545652154, '投诉', '物业未按合同公示公共收益明细，广告位及公共停车费收入去向不明。', 'admin', '2024-03-30 12:36:00', 'admin', '2024-03-30 12:36:00', '需转财务部配合', 1007),
(10032, 1623660565545652154, '投诉', '顶层住户屋顶漏水，报修两年仍未彻底解决，雨季导致墙皮发霉脱落，严重影响生活和健康。', 'admin', '2024-03-30 12:37:00', 'admin', '2024-03-30 12:37:00', NULL, 1008);