-- 物业管理菜单校准
-- 日期：2026-04-28
-- 目的：
-- 1. 重建目标顶层菜单树
-- 2. 将现有页面与目标菜单结构对齐
-- 3. 为已修复的模块添加缺失的页面菜单和按钮权限
-- 4. 将校准后的菜单授予管理员角色

SET @remark_tag = '2026-04-28 menu calibration';

-- =========================================================
-- 1. 顶层目录
-- =========================================================

INSERT INTO sys_menu (parent_id, menu_name, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 0, '系统管理', 1, 'systemManage', 'Layout', '1', '0', 'M', '0', '0', '', 'system', 'admin', NOW(), @remark_tag
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'systemManage' AND menu_type = 'M');

INSERT INTO sys_menu (parent_id, menu_name, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 0, '社区资产', 2, 'communityAsset', 'Layout', '1', '0', 'M', '0', '0', '', 'office-building', 'admin', NOW(), @remark_tag
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'communityAsset' AND menu_type = 'M');

INSERT INTO sys_menu (parent_id, menu_name, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 0, '业主管理', 3, 'ownerManage', 'Layout', '1', '0', 'M', '0', '0', '', 'user', 'admin', NOW(), @remark_tag
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'ownerManage' AND menu_type = 'M');

INSERT INTO sys_menu (parent_id, menu_name, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 0, '车辆与停车', 4, 'parkingManage', 'Layout', '1', '0', 'M', '0', '0', '', 'van', 'admin', NOW(), @remark_tag
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'parkingManage' AND menu_type = 'M');

INSERT INTO sys_menu (parent_id, menu_name, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 0, '财务管理', 5, 'financeManage', 'Layout', '1', '0', 'M', '0', '0', '', 'money', 'admin', NOW(), @remark_tag
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'financeManage' AND menu_type = 'M');

INSERT INTO sys_menu (parent_id, menu_name, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 0, '社区服务', 6, 'serviceManage', 'Layout', '1', '0', 'M', '0', '0', '', 'chat-line-square', 'admin', NOW(), @remark_tag
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'serviceManage' AND menu_type = 'M');

UPDATE sys_menu SET parent_id = 0, menu_name = '系统管理', order_num = 1, component = 'Layout', icon = 'system', remark = @remark_tag
WHERE path = 'systemManage' AND menu_type = 'M';

UPDATE sys_menu SET parent_id = 0, menu_name = '社区资产', order_num = 2, component = 'Layout', icon = 'office-building', remark = @remark_tag
WHERE path = 'communityAsset' AND menu_type = 'M';

UPDATE sys_menu SET parent_id = 0, menu_name = '业主管理', order_num = 3, component = 'Layout', icon = 'user', remark = @remark_tag
WHERE path = 'ownerManage' AND menu_type = 'M';

UPDATE sys_menu SET parent_id = 0, menu_name = '车辆与停车', order_num = 4, component = 'Layout', icon = 'van', remark = @remark_tag
WHERE path = 'parkingManage' AND menu_type = 'M';

UPDATE sys_menu SET parent_id = 0, menu_name = '财务管理', order_num = 5, component = 'Layout', icon = 'money', remark = @remark_tag
WHERE path = 'financeManage' AND menu_type = 'M';

UPDATE sys_menu SET parent_id = 0, menu_name = '社区服务', order_num = 6, component = 'Layout', icon = 'chat-line-square', remark = @remark_tag
WHERE path = 'serviceManage' AND menu_type = 'M';

-- =========================================================
-- 2. 页面菜单
-- =========================================================

UPDATE sys_menu m
JOIN (
    SELECT 'systemManage' AS parent_path, '用户管理' AS menu_name, 1 AS order_num, 'user' AS path, 'system/user/index' AS component, 'system:user:list' AS perms, 'user' AS icon
    UNION ALL SELECT 'systemManage', '角色管理', 2, 'role', 'system/role/index', 'system:role:list', 'peoples'
    UNION ALL SELECT 'systemManage', '菜单管理', 3, 'menu', 'system/menu/index', 'system:menu:list', 'tree-table'
    UNION ALL SELECT 'systemManage', '部门管理', 4, 'dept', 'system/dept/index', 'system:dept:list', 'tree'
    UNION ALL SELECT 'systemManage', '岗位管理', 5, 'post', 'system/post/index', 'system:post:list', 'post'
    UNION ALL SELECT 'systemManage', '字典管理', 6, 'dict', 'system/dict/index', 'system:dict:list', 'dict'
    UNION ALL SELECT 'systemManage', '操作日志', 7, 'operlog', 'monitor/operlog/index', 'monitor:operlog:list', 'form'
    UNION ALL SELECT 'systemManage', '登录日志', 8, 'logininfor', 'monitor/logininfor/index', 'monitor:logininfor:list', 'logininfor'
    UNION ALL SELECT 'communityAsset', '小区信息', 1, 'community', 'property/community/index', 'system:community:list', 'office-building'
    UNION ALL SELECT 'communityAsset', '楼栋信息', 2, 'building', 'property/building/index', 'system:building:list', 'building'
    UNION ALL SELECT 'communityAsset', '单元信息', 3, 'unit', 'property/unit/index', 'system:unit:list', 'grid'
    UNION ALL SELECT 'communityAsset', '房屋信息', 4, 'room', 'property/room/index', 'system:room:list', 'house'
    UNION ALL SELECT 'communityAsset', '设备信息', 5, 'device', 'property/device/index', 'community:device:list', 'monitor'
    UNION ALL SELECT 'ownerManage', '业主信息', 1, 'owner', 'property/owner/index', 'system:owner:list', 'user-filled'
    UNION ALL SELECT 'ownerManage', '业主审核', 2, 'ownerRoom', 'property/ownerRoom/index', 'system:ownerRoom:list', 'finished'
    UNION ALL SELECT 'parkingManage', '车辆信息', 1, 'vehicle', 'property/vehicle/index', 'community:vehicle:list', 'van'
    UNION ALL SELECT 'parkingManage', '停车位信息', 2, 'parkingSpace', 'property/parkingSpace/index', 'community:parkingSpace:list', 'guide'
    UNION ALL SELECT 'parkingManage', '停车记录', 3, 'parkingRecord', 'property/parkingRecord/index', 'community:parkingRecord:list', 'document'
    UNION ALL SELECT 'financeManage', '费用项目', 1, 'feeItem', 'property/feeItem/index', 'community:feeItem:list', 'money'
    UNION ALL SELECT 'financeManage', '费用账单', 2, 'feeBill', 'property/feeBill/index', 'community:feeBill:list', 'tickets'
    UNION ALL SELECT 'financeManage', '缴费记录', 3, 'paymentRecord', 'property/paymentRecord/index', 'community:paymentRecord:list', 'wallet'
    UNION ALL SELECT 'financeManage', '财务统计', 4, 'financeStat', 'property/financeStat/index', 'community:financeStat:list', 'histogram'
    UNION ALL SELECT 'serviceManage', '通知公告', 1, 'notice', 'system/notice/index', 'system:notice:list', 'message'
    UNION ALL SELECT 'serviceManage', '访客邀请', 2, 'visitor', 'property/visitor/index', 'system:visitor:list', 'calendar'
    UNION ALL SELECT 'serviceManage', '报修管理', 3, 'repair', 'property/repair/index', 'community:repairOrder:list', 'tools'
    UNION ALL SELECT 'serviceManage', '投诉建议', 4, 'suggest', 'property/suggest/index', 'system:suggest:list', 'chat-dot-round'
) t ON m.perms = t.perms AND m.menu_type = 'C'
JOIN sys_menu p ON p.path = t.parent_path AND p.menu_type = 'M'
SET m.parent_id = p.menu_id,
    m.menu_name = t.menu_name,
    m.order_num = t.order_num,
    m.path = t.path,
    m.component = t.component,
    m.icon = t.icon,
    m.is_frame = '1',
    m.is_cache = '0',
    m.visible = '0',
    m.status = '0',
    m.remark = @remark_tag;

INSERT INTO sys_menu (parent_id, menu_name, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT p.menu_id, t.menu_name, t.order_num, t.path, t.component, '1', '0', 'C', '0', '0', t.perms, t.icon, 'admin', NOW(), @remark_tag
FROM (
    SELECT 'systemManage' AS parent_path, '用户管理' AS menu_name, 1 AS order_num, 'user' AS path, 'system/user/index' AS component, 'system:user:list' AS perms, 'user' AS icon
    UNION ALL SELECT 'systemManage', '角色管理', 2, 'role', 'system/role/index', 'system:role:list', 'peoples'
    UNION ALL SELECT 'systemManage', '菜单管理', 3, 'menu', 'system/menu/index', 'system:menu:list', 'tree-table'
    UNION ALL SELECT 'systemManage', '部门管理', 4, 'dept', 'system/dept/index', 'system:dept:list', 'tree'
    UNION ALL SELECT 'systemManage', '岗位管理', 5, 'post', 'system/post/index', 'system:post:list', 'post'
    UNION ALL SELECT 'systemManage', '字典管理', 6, 'dict', 'system/dict/index', 'system:dict:list', 'dict'
    UNION ALL SELECT 'systemManage', '操作日志', 7, 'operlog', 'monitor/operlog/index', 'monitor:operlog:list', 'form'
    UNION ALL SELECT 'systemManage', '登录日志', 8, 'logininfor', 'monitor/logininfor/index', 'monitor:logininfor:list', 'logininfor'
    UNION ALL SELECT 'communityAsset', '小区信息', 1, 'community', 'property/community/index', 'system:community:list', 'office-building'
    UNION ALL SELECT 'communityAsset', '楼栋信息', 2, 'building', 'property/building/index', 'system:building:list', 'building'
    UNION ALL SELECT 'communityAsset', '单元信息', 3, 'unit', 'property/unit/index', 'system:unit:list', 'grid'
    UNION ALL SELECT 'communityAsset', '房屋信息', 4, 'room', 'property/room/index', 'system:room:list', 'house'
    UNION ALL SELECT 'communityAsset', '设备信息', 5, 'device', 'property/device/index', 'community:device:list', 'monitor'
    UNION ALL SELECT 'ownerManage', '业主信息', 1, 'owner', 'property/owner/index', 'system:owner:list', 'user-filled'
    UNION ALL SELECT 'ownerManage', '业主审核', 2, 'ownerRoom', 'property/ownerRoom/index', 'system:ownerRoom:list', 'finished'
    UNION ALL SELECT 'parkingManage', '车辆信息', 1, 'vehicle', 'property/vehicle/index', 'community:vehicle:list', 'van'
    UNION ALL SELECT 'parkingManage', '停车位信息', 2, 'parkingSpace', 'property/parkingSpace/index', 'community:parkingSpace:list', 'guide'
    UNION ALL SELECT 'parkingManage', '停车记录', 3, 'parkingRecord', 'property/parkingRecord/index', 'community:parkingRecord:list', 'document'
    UNION ALL SELECT 'financeManage', '费用项目', 1, 'feeItem', 'property/feeItem/index', 'community:feeItem:list', 'money'
    UNION ALL SELECT 'financeManage', '费用账单', 2, 'feeBill', 'property/feeBill/index', 'community:feeBill:list', 'tickets'
    UNION ALL SELECT 'financeManage', '缴费记录', 3, 'paymentRecord', 'property/paymentRecord/index', 'community:paymentRecord:list', 'wallet'
    UNION ALL SELECT 'financeManage', '财务统计', 4, 'financeStat', 'property/financeStat/index', 'community:financeStat:list', 'histogram'
    UNION ALL SELECT 'serviceManage', '通知公告', 1, 'notice', 'system/notice/index', 'system:notice:list', 'message'
    UNION ALL SELECT 'serviceManage', '访客邀请', 2, 'visitor', 'property/visitor/index', 'system:visitor:list', 'calendar'
    UNION ALL SELECT 'serviceManage', '报修管理', 3, 'repair', 'property/repair/index', 'community:repairOrder:list', 'tools'
    UNION ALL SELECT 'serviceManage', '投诉建议', 4, 'suggest', 'property/suggest/index', 'system:suggest:list', 'chat-dot-round'
) t
JOIN sys_menu p ON p.path = t.parent_path AND p.menu_type = 'M'
WHERE NOT EXISTS (
    SELECT 1
    FROM sys_menu m
    WHERE m.perms = t.perms
      AND m.menu_type = 'C'
);

-- =========================================================
-- 3. 按钮权限
-- =========================================================

INSERT INTO sys_menu (parent_id, menu_name, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT parent.menu_id, b.menu_name, b.order_num, '#', '', '1', '0', 'F', '0', '0', b.perms, '#', 'admin', NOW(), @remark_tag
FROM (
    SELECT 'system:community:list' AS parent_perm, '新增' AS menu_name, 1 AS order_num, 'system:community:add' AS perms
    UNION ALL SELECT 'system:community:list', '修改', 2, 'system:community:edit'
    UNION ALL SELECT 'system:community:list', '删除', 3, 'system:community:remove'
    UNION ALL SELECT 'system:community:list', '导出', 4, 'system:community:export'
    UNION ALL SELECT 'system:building:list', '新增', 1, 'system:building:add'
    UNION ALL SELECT 'system:building:list', '修改', 2, 'system:building:edit'
    UNION ALL SELECT 'system:building:list', '删除', 3, 'system:building:remove'
    UNION ALL SELECT 'system:building:list', '导出', 4, 'system:building:export'
    UNION ALL SELECT 'system:unit:list', '新增', 1, 'system:unit:add'
    UNION ALL SELECT 'system:unit:list', '修改', 2, 'system:unit:edit'
    UNION ALL SELECT 'system:unit:list', '删除', 3, 'system:unit:remove'
    UNION ALL SELECT 'system:unit:list', '导出', 4, 'system:unit:export'
    UNION ALL SELECT 'system:room:list', '新增', 1, 'system:room:add'
    UNION ALL SELECT 'system:room:list', '修改', 2, 'system:room:edit'
    UNION ALL SELECT 'system:room:list', '删除', 3, 'system:room:remove'
    UNION ALL SELECT 'system:room:list', '导出', 4, 'system:room:export'
    UNION ALL SELECT 'system:owner:list', '新增', 1, 'system:owner:add'
    UNION ALL SELECT 'system:owner:list', '修改', 2, 'system:owner:edit'
    UNION ALL SELECT 'system:owner:list', '删除', 3, 'system:owner:remove'
    UNION ALL SELECT 'system:owner:list', '导出', 4, 'system:owner:export'
    UNION ALL SELECT 'system:ownerRoom:list', '新增', 1, 'system:ownerRoom:add'
    UNION ALL SELECT 'system:ownerRoom:list', '修改', 2, 'system:ownerRoom:edit'
    UNION ALL SELECT 'system:ownerRoom:list', '删除', 3, 'system:ownerRoom:remove'
    UNION ALL SELECT 'system:ownerRoom:list', '导出', 4, 'system:ownerRoom:export'
    UNION ALL SELECT 'system:notice:list', '查看', 1, 'system:notice:query'
    UNION ALL SELECT 'system:notice:list', '新增', 2, 'system:notice:add'
    UNION ALL SELECT 'system:notice:list', '修改', 3, 'system:notice:edit'
    UNION ALL SELECT 'system:notice:list', '删除', 4, 'system:notice:remove'
    UNION ALL SELECT 'system:visitor:list', '新增', 1, 'system:visitor:add'
    UNION ALL SELECT 'system:visitor:list', '修改', 2, 'system:visitor:edit'
    UNION ALL SELECT 'system:visitor:list', '删除', 3, 'system:visitor:remove'
    UNION ALL SELECT 'system:visitor:list', '导出', 4, 'system:visitor:export'
    UNION ALL SELECT 'system:suggest:list', '新增', 1, 'system:suggest:add'
    UNION ALL SELECT 'system:suggest:list', '修改', 2, 'system:suggest:edit'
    UNION ALL SELECT 'system:suggest:list', '删除', 3, 'system:suggest:remove'
    UNION ALL SELECT 'system:suggest:list', '导出', 4, 'system:suggest:export'
    UNION ALL SELECT 'community:device:list', '查看', 1, 'community:device:query'
    UNION ALL SELECT 'community:device:list', '新增', 2, 'community:device:add'
    UNION ALL SELECT 'community:device:list', '修改', 3, 'community:device:edit'
    UNION ALL SELECT 'community:device:list', '删除', 4, 'community:device:remove'
    UNION ALL SELECT 'community:device:list', '导出', 5, 'community:device:export'
    UNION ALL SELECT 'community:vehicle:list', '查看', 1, 'community:vehicle:query'
    UNION ALL SELECT 'community:vehicle:list', '新增', 2, 'community:vehicle:add'
    UNION ALL SELECT 'community:vehicle:list', '修改', 3, 'community:vehicle:edit'
    UNION ALL SELECT 'community:vehicle:list', '删除', 4, 'community:vehicle:remove'
    UNION ALL SELECT 'community:vehicle:list', '导出', 5, 'community:vehicle:export'
    UNION ALL SELECT 'community:parkingSpace:list', '查看', 1, 'community:parkingSpace:query'
    UNION ALL SELECT 'community:parkingSpace:list', '新增', 2, 'community:parkingSpace:add'
    UNION ALL SELECT 'community:parkingSpace:list', '修改', 3, 'community:parkingSpace:edit'
    UNION ALL SELECT 'community:parkingSpace:list', '删除', 4, 'community:parkingSpace:remove'
    UNION ALL SELECT 'community:parkingSpace:list', '导出', 5, 'community:parkingSpace:export'
    UNION ALL SELECT 'community:parkingRecord:list', '查看', 1, 'community:parkingRecord:query'
    UNION ALL SELECT 'community:parkingRecord:list', '新增', 2, 'community:parkingRecord:add'
    UNION ALL SELECT 'community:parkingRecord:list', '修改', 3, 'community:parkingRecord:edit'
    UNION ALL SELECT 'community:parkingRecord:list', '删除', 4, 'community:parkingRecord:remove'
    UNION ALL SELECT 'community:parkingRecord:list', '导出', 5, 'community:parkingRecord:export'
    UNION ALL SELECT 'community:feeItem:list', '查看', 1, 'community:feeItem:query'
    UNION ALL SELECT 'community:feeItem:list', '新增', 2, 'community:feeItem:add'
    UNION ALL SELECT 'community:feeItem:list', '修改', 3, 'community:feeItem:edit'
    UNION ALL SELECT 'community:feeItem:list', '删除', 4, 'community:feeItem:remove'
    UNION ALL SELECT 'community:feeItem:list', '导出', 5, 'community:feeItem:export'
    UNION ALL SELECT 'community:feeBill:list', '查看', 1, 'community:feeBill:query'
    UNION ALL SELECT 'community:feeBill:list', '新增', 2, 'community:feeBill:add'
    UNION ALL SELECT 'community:feeBill:list', '修改', 3, 'community:feeBill:edit'
    UNION ALL SELECT 'community:feeBill:list', '删除', 4, 'community:feeBill:remove'
    UNION ALL SELECT 'community:feeBill:list', '导出', 5, 'community:feeBill:export'
    UNION ALL SELECT 'community:paymentRecord:list', '查看', 1, 'community:paymentRecord:query'
    UNION ALL SELECT 'community:paymentRecord:list', '新增', 2, 'community:paymentRecord:add'
    UNION ALL SELECT 'community:paymentRecord:list', '修改', 3, 'community:paymentRecord:edit'
    UNION ALL SELECT 'community:paymentRecord:list', '删除', 4, 'community:paymentRecord:remove'
    UNION ALL SELECT 'community:paymentRecord:list', '导出', 5, 'community:paymentRecord:export'
    UNION ALL SELECT 'community:repairOrder:list', '查看', 1, 'community:repairOrder:query'
    UNION ALL SELECT 'community:repairOrder:list', '新增', 2, 'community:repairOrder:add'
    UNION ALL SELECT 'community:repairOrder:list', '修改', 3, 'community:repairOrder:edit'
    UNION ALL SELECT 'community:repairOrder:list', '删除', 4, 'community:repairOrder:remove'
    UNION ALL SELECT 'community:repairOrder:list', '导出', 5, 'community:repairOrder:export'
) b
JOIN sys_menu parent ON parent.perms = b.parent_perm AND parent.menu_type = 'C'
WHERE NOT EXISTS (
    SELECT 1
    FROM sys_menu m
    WHERE m.parent_id = parent.menu_id
      AND m.perms = b.perms
);

-- 财务统计保持只读，不生成伪造的增删改按钮。

-- =========================================================
-- 4. Grant to admin role
-- =========================================================

SET @admin_role_id = (SELECT role_id FROM sys_role WHERE role_key = 'admin' LIMIT 1);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT @admin_role_id, m.menu_id
FROM sys_menu m
WHERE @admin_role_id IS NOT NULL
  AND m.remark = @remark_tag
  AND NOT EXISTS (
      SELECT 1
      FROM sys_role_menu rm
      WHERE rm.role_id = @admin_role_id
        AND rm.menu_id = m.menu_id
  );
