-- final_sys_menu_property_modules.sql
-- Purpose: final calibration for property/community business menus.
-- Notes:
--   1. This script does not modify the original sys_menu creation script.
--   2. New community business modules use the community:* permission prefix.
--   3. Owner/ownerRoom menus are intentionally not renamed, according to current requirement.

-- 1. Calibrate community business menu permissions and routes.
UPDATE sys_menu
SET perms = 'community:device:list', update_by = 'admin', update_time = NOW()
WHERE perms = 'system:device:list' OR component = 'property/device/index';

UPDATE sys_menu
SET perms = 'community:vehicle:list', update_by = 'admin', update_time = NOW()
WHERE perms = 'system:vehicle:list' OR component = 'property/vehicle/index';

UPDATE sys_menu
SET perms = 'community:parkingSpace:list', update_by = 'admin', update_time = NOW()
WHERE perms = 'system:parkingSpace:list' OR component = 'property/parkingSpace/index';

UPDATE sys_menu
SET perms = 'community:parkingRecord:list', update_by = 'admin', update_time = NOW()
WHERE perms = 'system:parkingRecord:list' OR component = 'property/parkingRecord/index';

UPDATE sys_menu
SET perms = 'community:feeItem:list', update_by = 'admin', update_time = NOW()
WHERE perms = 'system:feeItem:list' OR component = 'property/feeItem/index';

UPDATE sys_menu
SET perms = 'community:feeBill:list', update_by = 'admin', update_time = NOW()
WHERE perms = 'system:feeBill:list' OR component = 'property/feeBill/index';

UPDATE sys_menu
SET perms = 'community:paymentRecord:list', update_by = 'admin', update_time = NOW()
WHERE perms = 'system:paymentRecord:list' OR component = 'property/paymentRecord/index';

UPDATE sys_menu
SET path = 'financeStat',
    component = 'property/financeStat/index',
    perms = 'community:financeStat:list',
    update_by = 'admin',
    update_time = NOW()
WHERE perms IN ('system:financeStatistics:list', 'community:financeStat:list')
   OR component IN ('property/financeStatistics/index', 'property/financeStat/index')
   OR path = 'financeStatistics';

UPDATE sys_menu
SET perms = 'community:repairOrder:list', update_by = 'admin', update_time = NOW()
WHERE perms IN ('system:repair:list', 'community:repairOrder:list')
   OR component = 'property/repair/index';

-- 2. Calibrate button permissions if old system:* permissions already exist.
UPDATE sys_menu SET perms = REPLACE(perms, 'system:device:', 'community:device:'), update_by = 'admin', update_time = NOW() WHERE perms LIKE 'system:device:%';
UPDATE sys_menu SET perms = REPLACE(perms, 'system:vehicle:', 'community:vehicle:'), update_by = 'admin', update_time = NOW() WHERE perms LIKE 'system:vehicle:%';
UPDATE sys_menu SET perms = REPLACE(perms, 'system:parkingSpace:', 'community:parkingSpace:'), update_by = 'admin', update_time = NOW() WHERE perms LIKE 'system:parkingSpace:%';
UPDATE sys_menu SET perms = REPLACE(perms, 'system:parkingRecord:', 'community:parkingRecord:'), update_by = 'admin', update_time = NOW() WHERE perms LIKE 'system:parkingRecord:%';
UPDATE sys_menu SET perms = REPLACE(perms, 'system:feeItem:', 'community:feeItem:'), update_by = 'admin', update_time = NOW() WHERE perms LIKE 'system:feeItem:%';
UPDATE sys_menu SET perms = REPLACE(perms, 'system:feeBill:', 'community:feeBill:'), update_by = 'admin', update_time = NOW() WHERE perms LIKE 'system:feeBill:%';
UPDATE sys_menu SET perms = REPLACE(perms, 'system:paymentRecord:', 'community:paymentRecord:'), update_by = 'admin', update_time = NOW() WHERE perms LIKE 'system:paymentRecord:%';
UPDATE sys_menu SET perms = REPLACE(perms, 'system:repair:', 'community:repairOrder:'), update_by = 'admin', update_time = NOW() WHERE perms LIKE 'system:repair:%';
UPDATE sys_menu SET perms = REPLACE(perms, 'system:financeStatistics:', 'community:financeStat:'), update_by = 'admin', update_time = NOW() WHERE perms LIKE 'system:financeStatistics:%';

-- 3. Ensure button permissions required by current frontend pages exist.
CREATE TEMPORARY TABLE IF NOT EXISTS tmp_final_property_perms (
    parent_perm varchar(100) NOT NULL,
    action_name varchar(20) NOT NULL,
    order_num int NOT NULL,
    action_perm varchar(100) NOT NULL
);
TRUNCATE TABLE tmp_final_property_perms;

INSERT INTO tmp_final_property_perms(parent_perm, action_name, order_num, action_perm) VALUES
('community:device:list', '查看', 1, 'community:device:query'),
('community:device:list', '新增', 2, 'community:device:add'),
('community:device:list', '修改', 3, 'community:device:edit'),
('community:device:list', '删除', 4, 'community:device:remove'),
('community:device:list', '导出', 5, 'community:device:export'),
('community:vehicle:list', '查看', 1, 'community:vehicle:query'),
('community:vehicle:list', '新增', 2, 'community:vehicle:add'),
('community:vehicle:list', '修改', 3, 'community:vehicle:edit'),
('community:vehicle:list', '删除', 4, 'community:vehicle:remove'),
('community:vehicle:list', '导出', 5, 'community:vehicle:export'),
('community:parkingSpace:list', '查看', 1, 'community:parkingSpace:query'),
('community:parkingSpace:list', '新增', 2, 'community:parkingSpace:add'),
('community:parkingSpace:list', '修改', 3, 'community:parkingSpace:edit'),
('community:parkingSpace:list', '删除', 4, 'community:parkingSpace:remove'),
('community:parkingSpace:list', '导出', 5, 'community:parkingSpace:export'),
('community:parkingRecord:list', '查看', 1, 'community:parkingRecord:query'),
('community:parkingRecord:list', '新增', 2, 'community:parkingRecord:add'),
('community:parkingRecord:list', '修改', 3, 'community:parkingRecord:edit'),
('community:parkingRecord:list', '删除', 4, 'community:parkingRecord:remove'),
('community:parkingRecord:list', '导出', 5, 'community:parkingRecord:export'),
('community:feeItem:list', '查看', 1, 'community:feeItem:query'),
('community:feeItem:list', '新增', 2, 'community:feeItem:add'),
('community:feeItem:list', '修改', 3, 'community:feeItem:edit'),
('community:feeItem:list', '删除', 4, 'community:feeItem:remove'),
('community:feeItem:list', '导出', 5, 'community:feeItem:export'),
('community:feeBill:list', '查看', 1, 'community:feeBill:query'),
('community:feeBill:list', '新增', 2, 'community:feeBill:add'),
('community:feeBill:list', '修改', 3, 'community:feeBill:edit'),
('community:feeBill:list', '删除', 4, 'community:feeBill:remove'),
('community:feeBill:list', '导出', 5, 'community:feeBill:export'),
('community:paymentRecord:list', '查看', 1, 'community:paymentRecord:query'),
('community:paymentRecord:list', '新增', 2, 'community:paymentRecord:add'),
('community:paymentRecord:list', '修改', 3, 'community:paymentRecord:edit'),
('community:paymentRecord:list', '删除', 4, 'community:paymentRecord:remove'),
('community:paymentRecord:list', '导出', 5, 'community:paymentRecord:export'),
('community:repairOrder:list', '查看', 1, 'community:repairOrder:query'),
('community:repairOrder:list', '新增', 2, 'community:repairOrder:add'),
('community:repairOrder:list', '修改', 3, 'community:repairOrder:edit'),
('community:repairOrder:list', '删除', 4, 'community:repairOrder:remove'),
('community:repairOrder:list', '导出', 5, 'community:repairOrder:export');

INSERT INTO sys_menu (
    menu_name, parent_id, order_num, path, component,
    is_frame, is_cache, menu_type, visible, status, perms,
    icon, create_by, create_time, remark
)
SELECT
    CONCAT(parent.menu_name, p.action_name),
    parent.menu_id,
    p.order_num,
    '',
    NULL,
    1,
    0,
    'F',
    '0',
    '0',
    p.action_perm,
    '#',
    'admin',
    NOW(),
    CONCAT(parent.menu_name, p.action_name, '按钮')
FROM tmp_final_property_perms p
JOIN sys_menu parent ON parent.perms = p.parent_perm AND parent.menu_type = 'C'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu exists_menu WHERE exists_menu.perms = p.action_perm
);

-- 4. Ensure data analysis menus and permissions are calibrated.
SET @analysis_parent_id := (
    SELECT menu_id
    FROM sys_menu
    WHERE menu_type = 'M'
      AND (menu_name IN ('数据分析', '统计分析', '社区服务') OR path IN ('analysis', 'statAnalysis', 'communityService', 'serviceManage'))
    ORDER BY CASE
        WHEN menu_name IN ('数据分析', '统计分析') THEN 1
        WHEN path IN ('analysis', 'statAnalysis') THEN 2
        WHEN menu_name = '社区服务' OR path IN ('communityService', 'serviceManage') THEN 3
        ELSE 9
    END, menu_id
    LIMIT 1
);

UPDATE sys_menu
SET perms = 'community:analysis:complaintWordCloud',
    menu_name = '投诉建议分析',
    path = 'complaintWordCloud',
    component = 'property/analysis/complaintWordCloud',
    menu_type = 'C',
    visible = '0',
    status = '0',
    update_by = 'admin',
    update_time = NOW()
WHERE perms IN ('community:analysis:complaintWordCloud', 'community:analysis:complaintWordcloud')
   OR component IN ('property/analysis/complaintWordCloud', 'property/analysis/complaintWordcloud');

INSERT INTO sys_menu (
    parent_id, menu_name, order_num, path, component,
    is_frame, is_cache, menu_type, visible, status, perms,
    icon, create_by, create_time, remark
)
SELECT
    @analysis_parent_id,
    '投诉建议分析',
    5,
    'complaintWordCloud',
    'property/analysis/complaintWordCloud',
    1,
    0,
    'C',
    '0',
    '0',
    'community:analysis:complaintWordCloud',
    'chart',
    'admin',
    NOW(),
    '投诉建议词云与TF-IDF分析菜单'
WHERE @analysis_parent_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'community:analysis:complaintWordCloud');

INSERT INTO sys_menu (
    parent_id, menu_name, order_num, path, component,
    is_frame, is_cache, menu_type, visible, status, perms,
    icon, create_by, create_time, remark
)
SELECT
    @analysis_parent_id,
    '停车高峰分析',
    6,
    'parkingPeak',
    'property/analysis/parkingPeak',
    1,
    0,
    'C',
    '0',
    '0',
    'community:analysis:parkingPeak',
    'chart',
    'admin',
    NOW(),
    '停车高峰分析菜单'
WHERE @analysis_parent_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'community:analysis:parkingPeak');

INSERT INTO sys_menu (
    parent_id, menu_name, order_num, path, component,
    is_frame, is_cache, menu_type, visible, status, perms,
    icon, create_by, create_time, remark
)
SELECT
    @analysis_parent_id,
    '小区运行综合评价',
    7,
    'communityRanking',
    'property/analysis/communityRanking',
    1,
    0,
    'C',
    '0',
    '0',
    'community:analysis:ranking',
    'chart',
    'admin',
    NOW(),
    'TOPSIS小区运行综合评价菜单'
WHERE @analysis_parent_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'community:analysis:ranking');

UPDATE sys_menu
SET path = 'communityRanking',
    component = 'property/analysis/communityRanking',
    visible = '0',
    status = '0',
    update_by = 'admin',
    update_time = NOW()
WHERE perms = 'community:analysis:ranking';

-- 5. Grant calibrated menus to admin role. Other roles can be configured from role management page.
SET @admin_role_id := (SELECT role_id FROM sys_role WHERE role_key = 'admin' LIMIT 1);

INSERT INTO sys_role_menu(role_id, menu_id)
SELECT @admin_role_id, m.menu_id
FROM sys_menu m
WHERE @admin_role_id IS NOT NULL
  AND (
      m.perms IN (
          'community:device:list', 'community:vehicle:list', 'community:parkingSpace:list', 'community:parkingRecord:list',
          'community:feeItem:list', 'community:feeBill:list', 'community:paymentRecord:list', 'community:financeStat:list',
          'community:repairOrder:list', 'community:analysis:complaintWordCloud', 'community:analysis:parkingPeak', 'community:analysis:ranking'
      )
      OR m.perms LIKE 'community:device:%'
      OR m.perms LIKE 'community:vehicle:%'
      OR m.perms LIKE 'community:parkingSpace:%'
      OR m.perms LIKE 'community:parkingRecord:%'
      OR m.perms LIKE 'community:feeItem:%'
      OR m.perms LIKE 'community:feeBill:%'
      OR m.perms LIKE 'community:paymentRecord:%'
      OR m.perms LIKE 'community:repairOrder:%'
  )
  AND NOT EXISTS (
      SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = @admin_role_id AND rm.menu_id = m.menu_id
  );

DROP TEMPORARY TABLE IF EXISTS tmp_final_property_perms;
