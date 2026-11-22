<template>
  <div class="app-container">
    <el-form ref="queryForm" :model="queryParams" v-show="showSearch" :inline="true">
      <el-form-item label="角色名称" prop="roleName">
        <el-input
          v-model="queryParams.roleName"
          placeholder="请输入角色名称"
          clearable
          size="small"
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="权限字符" prop="roleKey">
        <el-input
          v-model="queryParams.roleKey"
          placeholder="请输入权限字符"
          clearable
          size="small"
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="角色状态"
          clearable
          size="small"
          style="width: 240px"
        >
          <el-option
            v-for="dict in statusOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间">
        <el-date-picker
          v-model="dateRange"
          size="small"
          style="width: 240px"
          value-format="YYYY-MM-DD"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        />
      </el-form-item>
      <el-form-item>
          <el-button class="el-button--cyan" size="small" @click="handleQuery"><el-icon><Search /></el-icon><span>搜索</span></el-button>
        <el-button size="small" @click="resetQuery"><el-icon><Refresh /></el-icon><span>重置</span></el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          size="small"
          @click="handleAdd"
          v-hasPermi="['system:role:add']"
        >
          <el-icon><Plus /></el-icon>
          <span>新增</span>
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          size="small"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:role:edit']"
        >
          <el-icon><Edit /></el-icon>
          <span>修改</span>
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          size="small"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:role:remove']"
        >
          <el-icon><Delete /></el-icon>
          <span>删除</span>
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          size="small"
          @click="handleExport"
          v-hasPermi="['system:role:export']"
        >
          <el-icon><Download /></el-icon>
          <span>导出</span>
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="roleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="角色编号" prop="roleId" width="150" />
      <el-table-column label="角色名称" prop="roleName" :show-overflow-tooltip="true" width="150" />
      <el-table-column label="权限字符" prop="roleKey" :show-overflow-tooltip="true" width="150" />
      <el-table-column label="显示顺序" prop="roleSort" width="150" />
      <el-table-column label="状态" align="center" width="150">
        <template #default="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="280">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button
            size="small"
            type="primary"
            link
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:role:edit']"
          >
            <el-icon><Edit /></el-icon>
            <span>修改</span>
          </el-button>
          <el-button
            size="small"
            type="primary"
            link
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:role:remove']"
          >
            <el-icon><Delete /></el-icon>
            <span>删除</span>
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="权限字符" prop="roleKey">
          <el-input v-model="form.roleKey" placeholder="请输入权限字符" />
        </el-form-item>
        <el-form-item label="角色顺序" prop="roleSort">
          <el-input-number v-model="form.roleSort" controls-position="right" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in statusOptions"
              :key="dict.dictValue"
              :label="dict.dictValue"
            >
              {{ dict.dictLabel }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单权限">
          <el-checkbox v-model="menuExpand" @change="handleCheckedTreeExpand($event, 'menu')">展开/折叠</el-checkbox>
          <el-checkbox v-model="menuNodeAll" @change="handleCheckedTreeNodeAll($event, 'menu')">全选/全不选</el-checkbox>
          <el-checkbox v-model="form.menuCheckStrictly" @change="handleCheckedTreeConnect($event, 'menu')">父子联动</el-checkbox>
          <el-tree
            ref="menu"
            class="tree-border"
            :data="menuOptions"
            show-checkbox
            node-key="id"
            :check-strictly="!form.menuCheckStrictly"
            empty-text="加载中，请稍候"
            :props="defaultProps"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog :title="title" v-model="openDataScope" width="500px" append-to-body>
      <el-form :model="form" label-width="80px">
        <el-form-item label="角色名称">
          <el-input v-model="form.roleName" :disabled="true" />
        </el-form-item>
        <el-form-item label="权限字符">
          <el-input v-model="form.roleKey" :disabled="true" />
        </el-form-item>
        <el-form-item label="权限范围">
          <el-select v-model="form.dataScope">
            <el-option
              v-for="item in dataScopeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="数据权限" v-show="form.dataScope == 2">
          <el-checkbox v-model="deptExpand" @change="handleCheckedTreeExpand($event, 'dept')">展开/折叠</el-checkbox>
          <el-checkbox v-model="deptNodeAll" @change="handleCheckedTreeNodeAll($event, 'dept')">全选/全不选</el-checkbox>
          <el-checkbox v-model="form.deptCheckStrictly" @change="handleCheckedTreeConnect($event, 'dept')">父子联动</el-checkbox>
          <el-tree
            ref="dept"
            class="tree-border"
            :data="deptOptions"
            show-checkbox
            default-expand-all
            node-key="id"
            :check-strictly="!form.deptCheckStrictly"
            empty-text="加载中，请稍候"
            :props="defaultProps"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitDataScope">确 定</el-button>
          <el-button @click="cancelDataScope">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { Delete, Download, Edit, Plus, Refresh, Search } from '@element-plus/icons-vue'
import { listRole, getRole, delRole, addRole, updateRole, exportRole, dataScope, changeRoleStatus } from '@/api/system/role'
import { treeselect as menuTreeselect, roleMenuTreeselect } from '@/api/system/menu'
import { treeselect as deptTreeselect } from '@/api/system/dept'

export default {
  name: 'Role',
  components: { Delete, Download, Edit, Plus, Refresh, Search },
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      roleList: [],
      title: '',
      open: false,
      openDataScope: false,
      menuExpand: false,
      menuNodeAll: false,
      deptExpand: true,
      deptNodeAll: false,
      dateRange: [],
      statusOptions: [],
      dataScopeOptions: [
        { value: '1', label: '全部数据权限' },
        { value: '2', label: '自定数据权限' },
        { value: '3', label: '本部门数据权限' },
        { value: '4', label: '本部门及以下数据权限' },
        { value: '5', label: '仅本人数据权限' }
      ],
      menuOptions: [],
      deptOptions: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        roleName: undefined,
        roleKey: undefined,
        status: undefined
      },
      form: {},
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      rules: {
        roleName: [{ required: true, message: '角色名称不能为空', trigger: 'blur' }],
        roleKey: [{ required: true, message: '权限字符不能为空', trigger: 'blur' }],
        roleSort: [{ required: true, message: '角色顺序不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
    this.getDicts('sys_normal_disable').then((response) => {
      this.statusOptions = response.data
    })
  },
  methods: {
    getList() {
      this.loading = true
      listRole(this.addDateRange(this.queryParams, this.dateRange)).then((response) => {
        this.roleList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    getMenuTreeselect() {
      menuTreeselect().then((response) => {
        this.menuOptions = response.data
      })
    },
    getDeptTreeselect() {
      deptTreeselect().then((response) => {
        this.deptOptions = response.data
      })
    },
    getMenuAllCheckedKeys() {
      const checkedKeys = this.$refs.menu.getCheckedKeys()
      const halfCheckedKeys = this.$refs.menu.getHalfCheckedKeys()
      checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys)
      return checkedKeys
    },
    getDeptAllCheckedKeys() {
      const checkedKeys = this.$refs.dept.getCheckedKeys()
      const halfCheckedKeys = this.$refs.dept.getHalfCheckedKeys()
      checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys)
      return checkedKeys
    },
    getRoleMenuTreeselect(roleId) {
      return roleMenuTreeselect(roleId).then((response) => {
        this.menuOptions = response.menus
        return response
      })
    },
    handleStatusChange(row) {
      const text = row.status === '0' ? '启用' : '停用'
      this.$confirm('确认要"' + text + '""' + row.roleName + '"角色吗?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => changeRoleStatus(row.roleId, row.status))
        .then(() => {
          this.msgSuccess(`${text}成功`)
        })
        .catch(() => {
          row.status = row.status === '0' ? '1' : '0'
        })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    cancelDataScope() {
      this.openDataScope = false
      this.reset()
    },
    reset() {
      if (this.$refs.menu !== undefined) {
        this.$refs.menu.setCheckedKeys([])
      }
      this.menuExpand = false
      this.menuNodeAll = false
      this.deptExpand = true
      this.deptNodeAll = false
      this.form = {
        roleId: undefined,
        roleName: undefined,
        roleKey: undefined,
        roleSort: 0,
        status: '0',
        menuIds: [],
        deptIds: [],
        menuCheckStrictly: true,
        deptCheckStrictly: true,
        remark: undefined
      }
      this.resetForm('form')
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.dateRange = []
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.roleId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleCheckedTreeExpand(value, type) {
      if (type === 'menu') {
        const treeList = this.menuOptions
        for (let i = 0; i < treeList.length; i += 1) {
          this.$refs.menu.store.nodesMap[treeList[i].id].expanded = value
        }
      } else if (type === 'dept') {
        const treeList = this.deptOptions
        for (let i = 0; i < treeList.length; i += 1) {
          this.$refs.dept.store.nodesMap[treeList[i].id].expanded = value
        }
      }
    },
    handleCheckedTreeNodeAll(value, type) {
      if (type === 'menu') {
        this.$refs.menu.setCheckedNodes(value ? this.menuOptions : [])
      } else if (type === 'dept') {
        this.$refs.dept.setCheckedNodes(value ? this.deptOptions : [])
      }
    },
    handleCheckedTreeConnect(value, type) {
      if (type === 'menu') {
        this.form.menuCheckStrictly = !!value
      } else if (type === 'dept') {
        this.form.deptCheckStrictly = !!value
      }
    },
    handleAdd() {
      this.reset()
      this.getMenuTreeselect()
      this.open = true
      this.title = '添加角色'
    },
    handleUpdate(row) {
      this.reset()
      const roleId = row.roleId || this.ids
      const roleMenu = this.getRoleMenuTreeselect(roleId)
      getRole(roleId).then((response) => {
        this.form = response.data
        this.open = true
        this.$nextTick(() => {
          roleMenu.then((res) => {
            this.$refs.menu.setCheckedKeys(res.checkedKeys)
          })
        })
        this.title = '修改角色'
      })
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          return
        }
        this.form.menuIds = this.getMenuAllCheckedKeys()
        if (this.form.roleId !== undefined) {
          updateRole(this.form).then(() => {
            this.msgSuccess('修改成功')
            this.open = false
            this.getList()
          })
          return
        }
        addRole(this.form).then(() => {
          this.msgSuccess('新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    submitDataScope() {
      if (this.form.roleId === undefined) {
        return
      }
      this.form.deptIds = this.getDeptAllCheckedKeys()
      dataScope(this.form).then(() => {
        this.msgSuccess('修改成功')
        this.openDataScope = false
        this.getList()
      })
    },
    handleDelete(row) {
      const roleIds = row.roleId || this.ids
      this.$confirm('是否确认删除角色编号为"' + roleIds + '"的数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => delRole(roleIds))
        .then(() => {
          this.getList()
          this.msgSuccess('删除成功')
        })
    },
    handleExport() {
      const queryParams = this.queryParams
      this.$confirm('是否确认导出所有角色数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => exportRole(queryParams))
        .then((response) => {
          this.download(response.msg)
        })
    }
  }
}
</script>
