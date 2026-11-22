-- Analysis menus for complaint word cloud and parking peak
-- Date: 2026-04-29
SET @remark_tag = '2026-04-29 analysis menus';

INSERT INTO sys_menu (parent_id, menu_name, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT p.menu_id, '投诉建议分析', 5, 'complaintWordCloud', 'property/analysis/complaintWordCloud', '1', '0', 'C', '0', '0', 'community:analysis:complaintWordcloud', 'chart', 'admin', NOW(), @remark_tag
FROM sys_menu p
WHERE p.path = 'serviceManage' AND p.menu_type = 'M'
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'community:analysis:complaintWordcloud' AND menu_type = 'C');

INSERT INTO sys_menu (parent_id, menu_name, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT p.menu_id, '停车高峰分析', 4, 'parkingPeak', 'property/analysis/parkingPeak', '1', '0', 'C', '0', '0', 'community:analysis:parkingPeak', 'histogram', 'admin', NOW(), @remark_tag
FROM sys_menu p
WHERE p.path = 'parkingManage' AND p.menu_type = 'M'
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'community:analysis:parkingPeak' AND menu_type = 'C');

SET @admin_role_id = (SELECT role_id FROM sys_role WHERE role_key = 'admin' LIMIT 1);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT @admin_role_id, m.menu_id
FROM sys_menu m
WHERE @admin_role_id IS NOT NULL
  AND m.perms IN ('community:analysis:complaintWordcloud', 'community:analysis:parkingPeak')
  AND NOT EXISTS (
      SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = @admin_role_id AND rm.menu_id = m.menu_id
  );
