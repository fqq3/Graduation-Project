<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="4" :xs="24">
        <div class="head-container">
          <el-input
            v-model="deptName"
            placeholder="请输入部门名称"
            clearable
            size="small"
            style="margin-bottom: 20px"
          >
            <template #prefix>
              <el-icon class="el-input__icon"><Search /></el-icon>
            </template>
          </el-input>
        </div>
        <div class="head-container">
          <el-tree
            ref="tree"
            :data="deptOptions"
            :props="defaultProps"
            :expand-on-click-node="false"
            :filter-node-method="filterNode"
            default-expand-all
            @node-click="handleNodeClick"
          />
        </div>
      </el-col>

      <el-col :span="20" :xs="24">
        <el-form ref="queryForm" :model="queryParams" :inline="true" v-show="showSearch" label-width="68px">
          <el-form-item label="用户名称" prop="userName">
            <el-input
              v-model="queryParams.userName"
              placeholder="请输入用户名称"
              clearable
              size="small"
              style="width: 240px"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item label="手机号码" prop="phonenumber">
            <el-input
              v-model="queryParams.phonenumber"
              placeholder="请输入手机号码"
              clearable
              size="small"
              style="width: 240px"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select
              v-model="queryParams.status"
              placeholder="用户状态"
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
              v-hasPermi="['system:user:add']"
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
              v-hasPermi="['system:user:edit']"
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
              v-hasPermi="['system:user:remove']"
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
              v-hasPermi="['system:user:export']"
            >
              <el-icon><Download /></el-icon>
              <span>导出</span>
            </el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
        </el-row>

        <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="用户编号" align="center" prop="userId" />
          <el-table-column label="用户名称" align="center" prop="userName" :show-overflow-tooltip="true" />
          <el-table-column label="用户昵称" align="center" prop="nickName" :show-overflow-tooltip="true" />
          <el-table-column label="部门" align="center" prop="dept.deptName" :show-overflow-tooltip="true" />
          <el-table-column label="手机号码" align="center" prop="phonenumber" width="120" />
          <el-table-column label="状态" align="center">
            <template #default="scope">
              <el-switch
                v-model="scope.row.status"
                active-value="0"
                inactive-value="1"
                @change="handleStatusChange(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="创建时间" align="center" prop="createTime" width="160">
            <template #default="scope">
              <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="160" class-name="small-padding fixed-width">
            <template #default="scope">
              <el-button
                size="small"
                type="primary"
                link
                @click="handleUpdate(scope.row)"
                v-hasPermi="['system:user:edit']"
              >
                <el-icon><Edit /></el-icon>
                <span>修改</span>
              </el-button>
              <el-button
                v-if="scope.row.userId !== 1"
                size="small"
                type="primary"
                link
                @click="handleDelete(scope.row)"
                v-hasPermi="['system:user:remove']"
              >
                <el-icon><Delete /></el-icon>
                <span>删除</span>
              </el-button>
              <el-button
                size="small"
                type="primary"
                link
                @click="handleResetPwd(scope.row)"
                v-hasPermi="['system:user:resetPwd']"
              >
                <el-icon><Key /></el-icon>
                <span>重置</span>
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
      </el-col>
    </el-row>

    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户昵称" prop="nickName">
              <el-input v-model="form.nickName" placeholder="请输入用户昵称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="归属部门" prop="deptId">
              <LegacyTreeselect
                v-model="form.deptId"
                :options="deptOptions"
                placeholder="请选择归属部门"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="手机号码" prop="phonenumber">
              <el-input v-model="form.phonenumber" placeholder="请输入手机号码" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item v-if="form.userId === undefined" label="用户名称" prop="userName">
              <el-input v-model="form.userName" placeholder="请输入用户名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item v-if="form.userId === undefined" label="用户密码" prop="password">
              <el-input v-model="form.password" placeholder="请输入用户密码" type="password" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户性别">
              <el-select v-model="form.sex" placeholder="请选择">
                <el-option
                  v-for="dict in sexOptions"
                  :key="dict.dictValue"
                  :label="dict.dictLabel"
                  :value="dict.dictValue"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
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
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="岗位">
              <el-select v-model="form.postIds" multiple placeholder="请选择">
                <el-option
                  v-for="item in postOptions"
                  :key="item.postId"
                  :label="item.postName"
                  :value="item.postId"
                  :disabled="item.status == 1"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色">
              <el-select v-model="form.roleIds" multiple placeholder="请选择">
                <el-option
                  v-for="item in roleOptions"
                  :key="item.roleId"
                  :label="item.roleName"
                  :value="item.roleId"
                  :disabled="item.status == 1"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog :title="upload.title" v-model="upload.open" width="400px" append-to-body>
      <el-upload
        ref="upload"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <el-icon class="legacy-el-icon el-icon-upload"><Upload /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或
          <em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            <el-checkbox v-model="upload.updateSupport" true-label="1" false-label="0" />是否更新已经存在的用户数据
            <el-link type="info" style="font-size: 12px" @click="importTemplate">下载模板</el-link>
          </div>
          <div class="el-upload__tip" style="color: red">提示：仅允许导入“xls”或“xlsx”格式文件！</div>
        </template>
      </el-upload>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitFileForm">确 定</el-button>
          <el-button @click="upload.open = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { Delete, Download, Edit, Key, Plus, Refresh, Search, Upload } from '@element-plus/icons-vue'
import { listUser, getUser, delUser, addUser, updateUser, exportUser, resetUserPwd, changeUserStatus, importTemplate } from '@/api/system/user'
import { getToken } from '@/utils/auth'
import { treeselect } from '@/api/system/dept'
import LegacyTreeselect from '@/components/LegacyTreeselect/index.vue'

export default {
  name: 'User',
  components: { Delete, Download, Edit, Key, LegacyTreeselect, Plus, Refresh, Search, Upload },
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      userList: [],
      title: '',
      deptOptions: undefined,
      open: false,
      deptName: undefined,
      initPassword: undefined,
      dateRange: [],
      statusOptions: [],
      sexOptions: [],
      postOptions: [],
      roleOptions: [],
      form: {},
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      upload: {
        open: false,
        title: '',
        isUploading: false,
        updateSupport: 0,
        headers: { Authorization: `Bearer ${getToken()}` },
        url: `${import.meta.env.VITE_APP_BASE_API}/system/user/importData`
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: undefined,
        phonenumber: undefined,
        status: undefined,
        deptId: undefined
      },
      rules: {
        userName: [{ required: true, message: '用户名称不能为空', trigger: 'blur' }],
        nickName: [{ required: true, message: '用户昵称不能为空', trigger: 'blur' }],
        deptId: [{ required: true, message: '归属部门不能为空', trigger: 'change' }],
        password: [{ required: true, message: '用户密码不能为空', trigger: 'blur' }],
        email: [
          { required: true, message: '邮箱地址不能为空', trigger: 'blur' },
          {
            type: 'email',
            message: '请输入正确的邮箱地址',
            trigger: ['blur', 'change']
          }
        ],
        phonenumber: [
          { required: true, message: '手机号码不能为空', trigger: 'blur' },
          {
            pattern: /^1[3-9]\d{9}$/,
            message: '请输入正确的手机号码',
            trigger: 'blur'
          }
        ]
      }
    }
  },
  watch: {
    deptName(val) {
      this.$refs.tree.filter(val)
    }
  },
  created() {
    this.getList()
    this.getTreeselect()
    this.getDicts('sys_normal_disable').then((response) => {
      this.statusOptions = response.data
    })
    this.getDicts('sys_user_sex').then((response) => {
      this.sexOptions = response.data
    })
  },
  methods: {
    getList() {
      this.loading = true
      listUser(this.addDateRange(this.queryParams, this.dateRange)).then((response) => {
        this.userList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    getTreeselect() {
      treeselect().then((response) => {
        this.deptOptions = response.data
      })
    },
    filterNode(value, data) {
      if (!value) {
        return true
      }
      return data.label.indexOf(value) !== -1
    },
    handleNodeClick(data) {
      this.queryParams.deptId = data.id
      this.getList()
    },
    handleStatusChange(row) {
      const text = row.status === '0' ? '启用' : '停用'
      this.$confirm('确认要"' + text + '""' + row.userName + '"用户吗?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => changeUserStatus(row.userId, row.status))
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
    reset() {
      this.form = {
        userId: undefined,
        deptId: undefined,
        userName: undefined,
        nickName: undefined,
        password: undefined,
        phonenumber: undefined,
        email: undefined,
        sex: undefined,
        status: '0',
        remark: undefined,
        postIds: [],
        roleIds: []
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
      this.ids = selection.map((item) => item.userId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.getTreeselect()
      getUser().then((response) => {
        this.postOptions = response.posts
        this.roleOptions = response.roles
        this.open = true
        this.title = '添加用户'
        this.form.password = this.initPassword
      })
    },
    handleUpdate(row) {
      this.reset()
      this.getTreeselect()
      const userId = row.userId || this.ids
      getUser(userId).then((response) => {
        this.form = response.data
        this.postOptions = response.posts
        this.roleOptions = response.roles
        this.form.postIds = response.postIds
        this.form.roleIds = response.roleIds
        this.open = true
        this.title = '修改用户'
        this.form.password = ''
      })
    },
    handleResetPwd(row) {
      this.$prompt(`请输入"${row.userName}"的新密码`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      })
        .then(({ value }) => {
          resetUserPwd(row.userId, value).then(() => {
            this.msgSuccess(`修改成功，新密码是：${value}`)
          })
        })
        .catch(() => {})
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          return
        }
        if (this.form.userId !== undefined) {
          updateUser(this.form).then(() => {
            this.msgSuccess('修改成功')
            this.open = false
            this.getList()
          })
          return
        }
        addUser(this.form).then(() => {
          this.msgSuccess('新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const userIds = row.userId || this.ids
      this.$confirm('是否确认删除用户编号为"' + userIds + '"的数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => delUser(userIds))
        .then(() => {
          this.getList()
          this.msgSuccess('删除成功')
        })
    },
    handleExport() {
      const queryParams = this.queryParams
      this.$confirm('是否确认导出所有用户数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => exportUser(queryParams))
        .then((response) => {
          this.download(response.msg)
        })
    },
    handleImport() {
      this.upload.title = '用户导入'
      this.upload.open = true
    },
    importTemplate() {
      importTemplate().then((response) => {
        this.download(response.msg)
      })
    },
    handleFileUploadProgress() {
      this.upload.isUploading = true
    },
    handleFileSuccess(response) {
      this.upload.open = false
      this.upload.isUploading = false
      this.$refs.upload.clearFiles()
      this.$alert(response.msg, '导入结果', { dangerouslyUseHTMLString: true })
      this.getList()
    },
    submitFileForm() {
      this.$refs.upload.submit()
    }
  }
}
</script>
