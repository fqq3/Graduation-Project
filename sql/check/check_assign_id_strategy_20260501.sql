-- Check property business tables for MyBatis-Plus IdType.ASSIGN_ID strategy.
-- Scope: business tables only. sys_* system tables are intentionally excluded.

-- 1. Business tables should not have AUTO_INCREMENT columns.
SELECT
  table_name,
  column_name,
  extra
FROM information_schema.columns
WHERE table_schema = DATABASE()
  AND extra LIKE '%auto_increment%'
  AND table_name IN (
    'community',
    'building',
    'unit',
    'room',
    'owner',
    'owner_room',
    'owner_room_record',
    'complaint_suggest',
    'visitor',
    'community_device',
    'community_vehicle',
    'community_parking_space',
    'community_parking_record',
    'community_fee_item',
    'community_fee_bill',
    'community_payment_record',
    'community_repair_order'
  )
ORDER BY table_name, column_name;

-- 2. Business tables should have PRIMARY KEY constraints.
SELECT
  table_name,
  column_name,
  constraint_name
FROM information_schema.key_column_usage
WHERE table_schema = DATABASE()
  AND constraint_name = 'PRIMARY'
  AND table_name IN (
    'community',
    'building',
    'unit',
    'room',
    'owner',
    'owner_room',
    'owner_room_record',
    'complaint_suggest',
    'visitor',
    'community_device',
    'community_vehicle',
    'community_parking_space',
    'community_parking_record',
    'community_fee_item',
    'community_fee_bill',
    'community_payment_record',
    'community_repair_order'

  )
ORDER BY table_name;
