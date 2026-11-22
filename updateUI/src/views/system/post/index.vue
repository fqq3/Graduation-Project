<template>
  <div class="app-container">
    <el-form ref="queryForm" :model="queryParams" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="岗位编码" prop="postCode">
        <el-input v-model="queryParams.postCode" placeholder="请输入岗位编码" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="岗位名称" prop="postName">
        <el-input v-model="queryParams.postName" placeholder="请输入岗位名称" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="岗位状态" clearable size="small">
          <el-option v-for="dict in statusOptions" :key="dict.dictValue" :label="dict.dictLabel" :value="dict.dictValue" />
        </el-select>
      </el-form-item>
      <el-form-item>
          <el-button class="el-button--cyan" size="small" @click="handleQuery"><el-icon><Search /></el-icon><span>搜索</span></el-button>
        <el-button size="small" @click="resetQuery"><el-icon><Refresh /></el-icon><span>重置</span></el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" size="small" @click="handleAdd" v-hasPermi="['system:post:add']"><el-icon><Plus /></el-icon><span>新增</span></el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" size="small" :disabled="single" @click="handleUpdate" v-hasPermi="['system:post:edit']"><el-icon><Edit /></el-icon><span>修改</span></el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" size="small" :disabled="multiple" @click="handleDelete" v-hasPermi="['system:post:remove']"><el-icon><Delete /></el-icon><span>删除</span></el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" size="small" @click="handleExport" v-hasPermi="['system:post:export']"><el-icon><Download /></el-icon><span>导出</span></el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="postList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="岗位编号" align="center" prop="postId" />
      <el-table-column label="岗位编码" align="center" prop="postCode" />
      <el-table-column label="岗位名称" align="center" prop="postName" />
      <el-table-column label="岗位排序" align="center" prop="postSort" />
      <el-table-column label="状态" align="center" prop="status" :formatter="statusFormat" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button size="small" type="primary" link @click="handleUpdate(scope.row)" v-hasPermi="['system:post:edit']"><el-icon><Edit /></el-icon><span>修改</span></el-button>
          <el-button size="small" type="primary" link @click="handleDelete(scope.row)" v-hasPermi="['system:post:remove']"><el-icon><Delete /></el-icon><span>删除</span></el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="岗位名称" prop="postName">
          <el-input v-model="form.postName" placeholder="请输入岗位名称" />
        </el-form-item>
        <el-form-item label="岗位编码" prop="postCode">
          <el-input v-model="form.postCode" placeholder="请输入编码名称" />
        </el-form-item>
        <el-form-item label="岗位顺序" prop="postSort">
          <el-input-number v-model="form.postSort" controls-position="right" :min="0" />
        </el-form-item>
        <el-form-item label="岗位状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in statusOptions" :key="dict.dictValue" :label="dict.dictValue">{{ dict.dictLabel }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
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
  </div>
</template>

<script>
import { Delete, Download, Edit, Plus, Refresh, Search } from '@element-plus/icons-vue'
import { listPost, getPost, delPost, addPost, updatePost, exportPost } from '@/api/system/post'

export default {
  name: 'Post',
  components: { Delete, Download, Edit, Plus, Refresh, Search },
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      postList: [],
      title: '',
      open: false,
      statusOptions: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        postCode: undefined,
        postName: undefined,
        status: undefined
      },
      form: {},
      rules: {
        postName: [{ required: true, message: '岗位名称不能为空', trigger: 'blur' }],
        postCode: [{ required: true, message: '岗位编码不能为空', trigger: 'blur' }],
        postSort: [{ required: true, message: '岗位顺序不能为空', trigger: 'blur' }]
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
      listPost(this.queryParams).then((response) => {
        this.postList = response.rows
        this.total = response.total
        this.loading = false
      })
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
        postId: undefined,
        postCode: undefined,
        postName: undefined,
        postSort: 0,
        status: '0',
        remark: undefined
      }
      this.resetForm('form')
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.postId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加岗位'
    },
    handleUpdate(row) {
      this.reset()
      const postId = row.postId || this.ids[0]
      getPost(postId).then((response) => {
        this.form = response.data
        this.open = true
        this.title = '修改岗位'
      })
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          return
        }
        const action = this.form.postId !== undefined ? updatePost : addPost
        const successMessage = this.form.postId !== undefined ? '修改成功' : '新增成功'
        action(this.form).then(() => {
          this.msgSuccess(successMessage)
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const postIds = row.postId || this.ids
      this.$confirm('是否确认删除岗位编号为"' + postIds + '"的数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => delPost(postIds))
        .then(() => {
          this.getList()
          this.msgSuccess('删除成功')
        })
    },
    handleExport() {
      const queryParams = this.queryParams
      this.$confirm('是否确认导出所有岗位数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => exportPost(queryParams))
        .then((response) => {
          this.download(response.msg)
        })
    }
  }
}
</script>
