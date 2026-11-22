-- 业务查询与统计索引补充脚本
-- 说明：仅新增索引，不修改表结构和历史数据；如目标索引已存在，请跳过对应语句。

CREATE INDEX idx_building_community_id
ON building(community_id);

CREATE INDEX idx_unit_community_building
ON unit(community_id, building_id);

CREATE INDEX idx_room_community_building_unit_status
ON room(community_id, building_id, unit_id, room_status);

CREATE INDEX idx_owner_room_community_room_owner_status
ON owner_room(community_id, room_id, owner_id, room_status);

CREATE INDEX idx_complaint_community_time_type
ON complaint_suggest(community_id, create_time, complaint_suggest_type);

CREATE INDEX idx_visitor_community_date
ON visitor(community_id, visitor_date);

CREATE INDEX idx_repair_community_status_time
ON community_repair_order(community_id, status, create_time);

CREATE INDEX idx_fee_bill_room_month_status
ON community_fee_bill(room_id, bill_year, bill_month, bill_status);

CREATE INDEX idx_payment_room_time_status
ON community_payment_record(room_id, payment_time, payment_status);
