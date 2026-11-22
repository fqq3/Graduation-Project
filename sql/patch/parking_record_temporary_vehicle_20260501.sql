-- 支持外来车辆临时停车记录：停车记录直接保存所属小区
ALTER TABLE `community_parking_record`
  ADD COLUMN `community_id` BIGINT(20) NULL COMMENT '小区ID' AFTER `owner_id`;

CREATE INDEX `idx_parking_record_community_time`
  ON `community_parking_record` (`community_id`, `in_time`);

UPDATE `community_parking_record` pr
LEFT JOIN `community_parking_space` ps ON ps.space_id = pr.space_id
LEFT JOIN `community_vehicle` v ON v.vehicle_id = pr.vehicle_id
LEFT JOIN `room` vr ON vr.room_id = v.room_id
LEFT JOIN `owner_room` orr
  ON orr.owner_id = COALESCE(v.owner_id, pr.owner_id)
  AND orr.owner_room_id = (
    SELECT MAX(orr2.owner_room_id)
    FROM `owner_room` orr2
    WHERE orr2.owner_id = COALESCE(v.owner_id, pr.owner_id)
  )
LEFT JOIN `room` lr ON lr.room_id = orr.room_id
SET pr.community_id = COALESCE(ps.community_id, vr.community_id, lr.community_id, orr.community_id)
WHERE pr.community_id IS NULL;
