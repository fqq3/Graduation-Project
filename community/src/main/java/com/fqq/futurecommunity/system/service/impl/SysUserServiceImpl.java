package com.fqq.futurecommunity.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fqq.futurecommunity.common.constant.UserConstants;
import com.fqq.futurecommunity.common.core.exception.CustomException;
import com.fqq.futurecommunity.system.domain.SysPost;
import com.fqq.futurecommunity.system.domain.SysRole;
import com.fqq.futurecommunity.system.domain.SysUser;
import com.fqq.futurecommunity.system.domain.SysUserPost;
import com.fqq.futurecommunity.system.domain.SysUserRole;
import com.fqq.futurecommunity.system.mapper.SysPostMapper;
import com.fqq.futurecommunity.system.mapper.SysRoleMapper;
import com.fqq.futurecommunity.system.mapper.SysUserMapper;
import com.fqq.futurecommunity.system.mapper.SysUserPostMapper;
import com.fqq.futurecommunity.system.mapper.SysUserRoleMapper;
import com.fqq.futurecommunity.system.service.SysUserService;
import jakarta.annotation.Resource;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Resource
    private SysUserMapper userMapper;

    @Resource
    private SysUserPostMapper userPostMapper;

    @Resource
    private SysUserRoleMapper userRoleMapper;

    @Resource
    private SysRoleMapper roleMapper;

    @Resource
    private SysPostMapper postMapper;

    @Override
    public Page<SysUser> selectUserPage(Page<SysUser> page, SysUser user) {
        return userMapper.selectUserPage(page, user);
    }

    @Override
    public List<SysUser> selectUserList(SysUser user) {
        return userMapper.selectUserList(user);
    }

    @Override
    public SysUser selectUserByUserName(String userName) {
        return userMapper.selectUserByUserName(userName);
    }

    @Override
    public SysUser selectUserById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public String checkUserNameUnique(String userName) {
        Long count = userMapper.selectCount(
                Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUserName, userName)
        );
        return count > 0 ? UserConstants.NOT_UNIQUE : UserConstants.UNIQUE;
    }

    @Override
    public String checkPhoneUnique(SysUser user) {
        Long userId = Objects.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.selectOne(
                Wrappers.<SysUser>lambdaQuery()
                        .eq(SysUser::getPhonenumber, user.getPhonenumber())
                        .last("limit 1")
        );
        if (!Objects.isNull(info) && !Objects.equals(info.getUserId(), userId)) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public String checkEmailUnique(SysUser user) {
        Long userId = Objects.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.selectOne(
                Wrappers.<SysUser>lambdaQuery()
                        .eq(SysUser::getEmail, user.getEmail())
                        .last("limit 1")
        );
        if (!Objects.isNull(info) && !Objects.equals(info.getUserId(), userId)) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public void checkUserAllowed(SysUser user) {
        if (!Objects.isNull(user.getUserId()) && user.isAdmin()) {
            throw new CustomException(500, "不允许操作超级管理员用户");
        }
    }

    @Override
    public int updateUserStatus(SysUser user) {
        return userMapper.updateById(user);
    }

    @Override
    public int updateUserProfile(SysUser user) {
        return userMapper.updateById(user);
    }

    @Override
    public boolean updateUserAvatar(String userName, String avatar) {
        LambdaUpdateWrapper<SysUser> updateWrapper = Wrappers.<SysUser>lambdaUpdate()
                .eq(SysUser::getUserName, userName)
                .set(SysUser::getAvatar, avatar);
        return userMapper.update(null, updateWrapper) > 0;
    }

    @Override
    public int resetPwd(SysUser user) {
        return userMapper.updateById(user);
    }

    @Override
    public int resetUserPwd(String userName, String password) {
        LambdaUpdateWrapper<SysUser> updateWrapper = Wrappers.<SysUser>lambdaUpdate()
                .eq(SysUser::getUserName, userName)
                .set(SysUser::getPassword, password);
        return userMapper.update(null, updateWrapper);
    }

    @Override
    public String selectUserRoleGroup(String userName) {
        List<SysRole> roles = roleMapper.selectRolesByUserName(userName);
        if (roles == null || roles.isEmpty()) {
            return "";
        }
        return roles.stream()
                .map(SysRole::getRoleName)
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.joining(","));
    }

    @Override
    public String selectUserPostGroup(String userName) {
        List<SysPost> posts = postMapper.selectPostsByUserName(userName);
        if (posts == null || posts.isEmpty()) {
            return "";
        }
        return posts.stream()
                .map(SysPost::getPostName)
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.joining(","));
    }

    @Override
    public int insertUser(SysUser user) {
        int row = userMapper.insert(user);
        insertUserPost(user);
        insertUserRole(user);
        return row;
    }

    public void insertUserRole(SysUser user) {
        Long[] roleIds = user.getRoleIds();
        if (roleIds == null) {
            return;
        }
        for (Long roleId : roleIds) {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setRoleId(roleId);
            sysUserRole.setUserId(user.getUserId());
            userRoleMapper.insert(sysUserRole);
        }
    }

    public void insertUserPost(SysUser user) {
        Long[] postIds = user.getPostIds();
        if (postIds == null) {
            return;
        }
        for (Long postId : postIds) {
            SysUserPost sysUserPost = new SysUserPost();
            sysUserPost.setUserId(user.getUserId());
            sysUserPost.setPostId(postId);
            userPostMapper.insert(sysUserPost);
        }
    }

    @Override
    @Transactional
    public int updateUser(SysUser user) {
        Long userId = user.getUserId();
        deleteUserRoleByUserId(userId);
        insertUserRole(user);
        deleteUserPostByUserId(userId);
        insertUserPost(user);
        return userMapper.updateById(user);
    }

    @Override
    public int deleteUserById(Long userId) {
        deleteUserRoleByUserId(userId);
        deleteUserPostByUserId(userId);
        return userMapper.deleteById(userId);
    }

    @Override
    public int deleteUserByIds(Long[] userIds) {
        for (Long userId : userIds) {
            deleteUserRoleByUserId(userId);
            deleteUserPostByUserId(userId);
        }
        return userMapper.deleteBatchIds(Arrays.asList(userIds));
    }

    private void deleteUserRoleByUserId(Long userId) {
        userRoleMapper.delete(
                Wrappers.<SysUserRole>lambdaQuery().eq(SysUserRole::getUserId, userId)
        );
    }

    private void deleteUserPostByUserId(Long userId) {
        userPostMapper.delete(
                Wrappers.<SysUserPost>lambdaQuery().eq(SysUserPost::getUserId, userId)
        );
    }
}
