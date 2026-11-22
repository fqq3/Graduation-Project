<template>
  <div class="app-container">
    <el-form ref="queryForm" :model="queryParams" :inline="true" v-show="showSearch">
      <el-form-item label="部门名称" prop="deptName">
        <el-input
          v-model="queryParams.deptName"
          placeholder="请输入部门名称"
          clearable
          size="small"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="部门状态" clearable size="small">
          <el-option
            v-for="dict in statusOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
        </el-select>
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
          v-hasPermi="['system:dept:add']"
        >
          <el-icon><Plus /></el-icon>
          <span>新增</span>
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table
      v-loading="loading"
      :data="deptList"
      row-key="deptId"
      default-expand-all
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column prop="deptName" label="部门名称" width="260" />
      <el-table-column prop="orderNum" label="排序" width="200" />
      <el-table-column prop="status" label="状态" :formatter="statusFormat" width="100" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="200">
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
            v-hasPermi="['system:dept:edit']"
          >
            <el-icon><Edit /></el-icon>
            <span>修改</span>
          </el-button>
          <el-button
            size="small"
            type="primary"
            link
            @click="handleAdd(scope.row)"
            v-hasPermi="['system:dept:add']"
          >
            <el-icon><Plus /></el-icon>
            <span>新增</span>
          </el-button>
          <el-button
            v-if="scope.row.parentId !== 0"
            size="small"
            type="primary"
            link
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:dept:remove']"
          >
            <el-icon><Delete /></el-icon>
            <span>删除</span>
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="24" v-if="form.parentId !== 0">
            <el-form-item label="上级部门" prop="parentId">
              <LegacyTreeselect
                v-model="form.parentId"
                :options="deptOptions"
                :normalizer="normalizer"
                placeholder="选择上级部门"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门名称" prop="deptName">
              <el-input v-model="form.deptName" placeholder="请输入部门名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示排序" prop="orderNum">
              <el-input-number v-model="form.orderNum" controls-position="right" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人" prop="leader">
              <el-input v-model="form.leader" placeholder="请输入负责人" maxlength="20" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门状态">
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
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { Delete, Edit, Plus, Refresh, Search } from '@element-plus/icons-vue'
import { listDept, getDept, delDept, addDept, updateDept, listDeptExcludeChild } from '@/api/system/dept'
import LegacyTreeselect from '@/components/LegacyTreeselect/index.vue'

export default {
  name: 'Dept',
  components: { Delete, Edit, LegacyTreeselect, Plus, Refresh, Search },
  data() {
    return {
      loading: true,
      showSearch: true,
      deptList: [],
      deptOptions: [],
      title: '',
      open: false,
      statusOptions: [],
      queryParams: {
        deptName: undefined,
        status: undefined
      },
      form: {},
      rules: {
        parentId: [{ required: true, message: '上级部门不能为空', trigger: 'blur' }],
        deptName: [{ required: true, message: '部门名称不能为空', trigger: 'blur' }],
        orderNum: [{ required: true, message: '显示排序不能为空', trigger: 'blur' }],
        email: [
          {
            type: 'email',
            message: '请输入正确的邮箱地址',
            trigger: ['blur', 'change']
          }
        ],
        phone: [
          {
            pattern: /^1[3-9]\d{9}$/,
            message: '请输入正确的手机号',
            trigger: 'blur'
          }
        ]
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
      listDept(this.queryParams).then((response) => {
        this.deptList = this.handleTree(response.data, 'deptId')
        this.loading = false
      })
    },
    normalizer(node) {
      if (node.children && !node.children.length) {
        delete node.children
      }
      return {
        id: node.deptId,
        label: node.deptName,
        children: node.children
      }
    },
    statusFormat(row) {
      return this.selectDictLabel(this.statusOptions, row.status)
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        deptId: undefined,
        parentId: undefined,
        deptName: undefined,
        orderNum: undefined,
        leader: undefined,
        phone: undefined,
        email: undefined,
        status: '0'
      }
      this.resetForm('form')
    },
    handleQuery() {
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleAdd(row) {
      this.reset()
      if (row !== undefined) {
        this.form.parentId = row.deptId
      }
      this.open = true
      this.title = '添加部门'
      listDept().then((response) => {
        this.deptOptions = this.handleTree(response.data, 'deptId')
      })
    },
    handleUpdate(row) {
      this.reset()
      getDept(row.deptId).then((response) => {
        this.form = response.data
        this.open = true
        this.title = '修改部门'
      })
      listDeptExcludeChild(row.deptId).then((response) => {
        this.deptOptions = this.handleTree(response.data, 'deptId')
      })
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          return
        }
        if (this.form.deptId !== undefined) {
          updateDept(this.form).then(() => {
            this.msgSuccess('修改成功')
            this.open = false
            this.getList()
          })
          return
        }
        addDept(this.form).then(() => {
          this.msgSuccess('新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      this.$confirm('是否确认删除名称为"' + row.deptName + '"的数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => delDept(row.deptId))
        .then(() => {
          this.getList()
          this.msgSuccess('删除成功')
        })
    }
  }
}
</script>
