-- Business table primary key migration for MyBatis-Plus IdType.ASSIGN_ID.
-- Scope: property/community business tables only. Do not apply to sys_* tables.

ALTER TABLE community MODIFY COLUMN community_id BIGINT(20) NOT NULL COMMENT '小区id';
ALTER TABLE building MODIFY COLUMN building_id BIGINT(20) NOT NULL COMMENT '楼栋id';
ALTER TABLE unit MODIFY COLUMN unit_id BIGINT(20) NOT NULL COMMENT '单元id';
ALTER TABLE room MODIFY COLUMN room_id BIGINT(20) NOT NULL COMMENT '房间id';
ALTER TABLE owner MODIFY COLUMN owner_id BIGINT(20) NOT NULL COMMENT '业主id';
ALTER TABLE owner_room MODIFY COLUMN owner_room_id BIGINT(20) NOT NULL COMMENT '房屋绑定id';
ALTER TABLE owner_room_record MODIFY COLUMN record_id BIGINT(20) NOT NULL COMMENT '审核记录id';
ALTER TABLE complaint_suggest MODIFY COLUMN complaint_suggest_id BIGINT(20) NOT NULL COMMENT '投诉建议id';
ALTER TABLE visitor MODIFY COLUMN visitor_id BIGINT(20) NOT NULL COMMENT '访客id';
ALTER TABLE community_device MODIFY COLUMN device_id BIGINT(20) NOT NULL COMMENT '设备ID';
ALTER TABLE community_vehicle MODIFY COLUMN vehicle_id BIGINT(20) NOT NULL COMMENT '车辆ID';
ALTER TABLE community_parking_space MODIFY COLUMN space_id BIGINT(20) NOT NULL COMMENT '车位ID';
ALTER TABLE community_parking_record MODIFY COLUMN record_id BIGINT(20) NOT NULL COMMENT '停车记录ID';
ALTER TABLE community_fee_item MODIFY COLUMN item_id BIGINT(20) NOT NULL COMMENT '费用项目ID';
ALTER TABLE community_fee_bill MODIFY COLUMN bill_id BIGINT(20) NOT NULL COMMENT '账单ID';
ALTER TABLE community_payment_record MODIFY COLUMN payment_id BIGINT(20) NOT NULL COMMENT '缴费记录ID';
ALTER TABLE community_repair_order MODIFY COLUMN repair_id BIGINT(20) NOT NULL COMMENT '报修ID';

-- Existing scripts showed these three business tables without a primary key.
-- Run these only if the target database also lacks the primary key constraint.
-- 仅当目标库缺少主键时执行。
-- 如果目标库已有主键，应跳过对应 ADD PRIMARY KEY 语句，避免 Multiple primary key defined。
# ALTER TABLE building ADD PRIMARY KEY (building_id);
# ALTER TABLE unit ADD PRIMARY KEY (unit_id);
# ALTER TABLE room ADD PRIMARY KEY (room_id);
