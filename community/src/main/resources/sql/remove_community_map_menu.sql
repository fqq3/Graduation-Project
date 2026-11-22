SET NAMES utf8mb4;

-- 删除旧“小区地图/地图浏览”动态菜单与角色绑定。
-- 首页地图已改为 /index 内置展示，不再依赖 system:community:map 菜单权限。
DELETE rm
FROM sys_role_menu rm
JOIN sys_menu m ON rm.menu_id = m.menu_id
WHERE m.perms = 'system:community:map'
   OR m.path IN ('/property/community/map/:communityId', 'communityMap/:communityId', 'community/map/:communityId', 'map/:communityId')
   OR m.component = 'property/community/map';

DELETE FROM sys_menu
WHERE perms = 'system:community:map'
   OR path IN ('/property/community/map/:communityId', 'communityMap/:communityId', 'community/map/:communityId', 'map/:communityId')
   OR component = 'property/community/map';
