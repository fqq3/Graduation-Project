SELECT
    menu_id,
    parent_id,
    menu_name,
    menu_type,
    path,
    component,
    visible,
    status,
    perms
FROM sys_menu
WHERE perms IN (
    'community:analysis:complaintWordCloud',
    'community:analysis:complaintWordcloud'
)
   OR path = 'complaintWordCloud';

SELECT
    r.role_id,
    r.role_name,
    r.role_key,
    m.menu_id,
    m.menu_name,
    m.perms
FROM sys_role r
JOIN sys_role_menu rm ON rm.role_id = r.role_id
JOIN sys_menu m ON m.menu_id = rm.menu_id
WHERE m.perms = 'system:suggest:list'
ORDER BY r.role_id;

SET @correct_perm = 'community:analysis:complaintWordCloud';
SET @wrong_perm = 'community:analysis:complaintWordcloud';

SET @suggest_menu_id = (
    SELECT menu_id
    FROM sys_menu
    WHERE perms = 'system:suggest:list'
      AND menu_type IN ('C', 'F')
    LIMIT 1
);

SET @service_parent_id = (
    SELECT menu_id
    FROM sys_menu
    WHERE path = 'serviceManage'
      AND menu_type = 'M'
    LIMIT 1
);

SET @parent_id = COALESCE(@suggest_menu_id, @service_parent_id);

UPDATE sys_menu
SET
    perms = @correct_perm,
    menu_name = '投诉建议分析',
    menu_type = 'F',
    parent_id = @parent_id,
    path = '#',
    component = '',
    visible = '1',
    status = '0',
    icon = '#',
    update_by = 'admin',
    update_time = NOW(),
    remark = '投诉建议词云分析权限'
WHERE perms = @wrong_perm
  AND @parent_id IS NOT NULL;

UPDATE sys_menu
SET
    menu_name = '投诉建议分析',
    menu_type = 'F',
    parent_id = @parent_id,
    path = '#',
    component = '',
    visible = '1',
    status = '0',
    icon = '#',
    update_by = 'admin',
    update_time = NOW(),
    remark = '投诉建议词云分析权限'
WHERE perms = @correct_perm
  AND @parent_id IS NOT NULL;

SET @analysis_perm_menu_id = (
    SELECT MIN(menu_id)
    FROM sys_menu
    WHERE perms = @correct_perm
);

INSERT INTO sys_menu (
    parent_id,
    menu_name,
    order_num,
    path,
    component,
    is_frame,
    is_cache,
    menu_type,
    visible,
    status,
    perms,
    icon,
    create_by,
    create_time,
    remark
)
SELECT
    @parent_id,
    '投诉建议分析',
    99,
    '#',
    '',
    '1',
    '0',
    'F',
    '1',
    '0',
    @correct_perm,
    '#',
    'admin',
    NOW(),
    '投诉建议词云分析权限'
WHERE @parent_id IS NOT NULL
  AND @analysis_perm_menu_id IS NULL;

SET @analysis_perm_menu_id = (
    SELECT MIN(menu_id)
    FROM sys_menu
    WHERE perms = @correct_perm
);

UPDATE sys_menu
SET
    status = CASE
        WHEN menu_id = @analysis_perm_menu_id THEN '0'
        ELSE '1'
    END,
    update_by = 'admin',
    update_time = NOW()
WHERE perms = @correct_perm;

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, @analysis_perm_menu_id
FROM sys_role r
WHERE r.role_key = 'admin'
  AND @analysis_perm_menu_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM sys_role_menu rm
      WHERE rm.role_id = r.role_id
        AND rm.menu_id = @analysis_perm_menu_id
  );

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, @analysis_perm_menu_id
FROM sys_role_menu rm
JOIN sys_menu m ON m.menu_id = rm.menu_id
WHERE m.perms = 'system:suggest:list'
  AND @analysis_perm_menu_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM sys_role_menu exists_rm
      WHERE exists_rm.role_id = rm.role_id
        AND exists_rm.menu_id = @analysis_perm_menu_id
  );

SELECT
    menu_id,
    parent_id,
    menu_name,
    menu_type,
    path,
    component,
    visible,
    status,
    perms
FROM sys_menu
WHERE perms IN (
    'community:analysis:complaintWordCloud',
    'community:analysis:complaintWordcloud'
);

SELECT
    r.role_id,
    r.role_name,
    r.role_key,
    m.menu_id,
    m.menu_name,
    m.perms
FROM sys_role r
JOIN sys_role_menu rm ON rm.role_id = r.role_id
JOIN sys_menu m ON m.menu_id = rm.menu_id
WHERE m.perms = 'community:analysis:complaintWordCloud'
ORDER BY r.role_id;
