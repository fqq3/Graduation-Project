-- TOPSIS 小区运行综合评价菜单
-- Date: 2026-05-01
-- Purpose: add community ranking analysis page without changing existing analysis menus.

SET @remark_tag = '2026-05-01 community ranking analysis';

SET @parent_id := (
    SELECT menu_id
    FROM sys_menu
    WHERE menu_type = 'M'
      AND (
          menu_name IN ('数据分析', '统计分析', '社区服务')
          OR path IN ('analysis', 'statAnalysis', 'serviceManage')
      )
    ORDER BY
      CASE
        WHEN menu_name IN ('数据分析', '统计分析') THEN 1
        WHEN path IN ('analysis', 'statAnalysis') THEN 2
        WHEN menu_name = '社区服务' OR path = 'serviceManage' THEN 3
        ELSE 9
      END,
      menu_id
    LIMIT 1
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
    '小区运行综合评价',
    7,
    'communityRanking',
    'property/analysis/communityRanking',
    '1',
    '0',
    'C',
    '0',
    '0',
    'community:analysis:ranking',
    'chart',
    'admin',
    NOW(),
    @remark_tag
WHERE @parent_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM sys_menu
      WHERE perms = 'community:analysis:ranking'
         OR component = 'property/analysis/communityRanking'
  );

SET @ranking_menu_id := (
    SELECT menu_id
    FROM sys_menu
    WHERE perms = 'community:analysis:ranking'
    LIMIT 1
);

SET @admin_role_id := (
    SELECT role_id
    FROM sys_role
    WHERE role_key = 'admin'
    LIMIT 1
);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT @admin_role_id, @ranking_menu_id
WHERE @admin_role_id IS NOT NULL
  AND @ranking_menu_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM sys_role_menu rm
      WHERE rm.role_id = @admin_role_id
        AND rm.menu_id = @ranking_menu_id
  );
