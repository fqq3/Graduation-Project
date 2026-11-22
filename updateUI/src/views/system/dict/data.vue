<template>
  <div class="app-container">
    <el-form ref="queryForm" :model="queryParams" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="字典名称" prop="dictType">
        <el-select v-model="queryParams.dictType" size="small">
          <el-option v-for="item in typeOptions" :key="item.dictId" :label="item.dictName" :value="item.dictType" />
        </el-select>
      </el-form-item>
      <el-form-item label="字典标签" prop="dictLabel">
        <el-input
          v-model="queryParams.dictLabel"
          placeholder="请输入字典标签"
          clearable
          size="small"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="数据状态" clearable size="small">
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
        <el-button type="primary" size="small" @click="handleAdd" v-hasPermi="['system:dict:add']"><el-icon><Plus /></el-icon><span>新增</span></el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" size="small" :disabled="single" @click="handleUpdate" v-hasPermi="['system:dict:edit']"><el-icon><Edit /></el-icon><span>修改</span></el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" size="small" :disabled="multiple" @click="handleDelete" v-hasPermi="['system:dict:remove']"><el-icon><Delete /></el-icon><span>删除</span></el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" size="small" @click="handleExport" v-hasPermi="['system:dict:export']"><el-icon><Download /></el-icon><span>导出</span></el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="字典编码" align="center" prop="dictCode" />
      <el-table-column label="字典标签" align="center" prop="dictLabel" />
      <el-table-column label="字典键值" align="center" prop="dictValue" />
      <el-table-column label="字典排序" align="center" prop="dictSort" />
      <el-table-column label="状态" align="center" prop="status" :formatter="statusFormat" />
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button size="small" type="primary" link @click="handleUpdate(scope.row)" v-hasPermi="['system:dict:edit']"><el-icon><Edit /></el-icon><span>修改</span></el-button>
          <el-button size="small" type="primary" link @click="handleDelete(scope.row)" v-hasPermi="['system:dict:remove']"><el-icon><Delete /></el-icon><span>删除</span></el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="open" :title="title" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="字典类型">
          <el-input v-model="form.dictType" disabled />
        </el-form-item>
        <el-form-item label="数据标签" prop="dictLabel">
          <el-input v-model="form.dictLabel" placeholder="请输入数据标签" />
        </el-form-item>
        <el-form-item label="数据键值" prop="dictValue">
          <el-input v-model="form.dictValue" placeholder="请输入数据键值" />
        </el-form-item>
        <el-form-item label="显示排序" prop="dictSort">
          <el-input-number v-model="form.dictSort" controls-position="right" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
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
import { listData, getData, delData, addData, updateData, exportData } from '@/api/system/dict/data'
import { listType, getType } from '@/api/system/dict/type'

export default {
  name: 'Data',
  components: { Delete, Download, Edit, Plus, Refresh, Search },
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      dataList: [],
      defaultDictType: '',
      title: '',
      open: false,
      statusOptions: [],
      typeOptions: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        dictLabel: undefined,
        dictType: undefined,
        status: undefined
      },
      form: {},
      rules: {
        dictLabel: [{ required: true, message: '数据标签不能为空', trigger: 'blur' }],
        dictValue: [{ required: true, message: '数据键值不能为空', trigger: 'blur' }],
        dictSort: [{ required: true, message: '数据显示顺序不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    const dictId = this.$route.params?.dictId
    this.getTypeDetail(dictId)
    this.getTypeList()
    this.getDicts('sys_normal_disable').then((response) => {
      this.statusOptions = response.data || []
    })
  },
  methods: {
    getTypeDetail(dictId) {
      getType(dictId).then((response) => {
        this.queryParams.dictType = response.data.dictType
        this.defaultDictType = response.data.dictType
        this.getList()
      })
    },
    getTypeList() {
      listType().then((response) => {
        this.typeOptions = response.rows || []
      })
    },
    getList() {
      this.loading = true
      listData(this.queryParams).then((response) => {
        this.dataList = response.rows || []
        this.total = response.total || 0
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
        dictCode: undefined,
        dictType: this.queryParams.dictType,
        dictLabel: undefined,
        dictValue: undefined,
        dictSort: 0,
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
      this.queryParams.dictType = this.defaultDictType
      this.handleQuery()
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加字典数据'
    },
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.dictCode)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleUpdate(row) {
      this.reset()
      const dictCode = row?.dictCode || this.ids[0]
      getData(dictCode).then((response) => {
        this.form = response.data || {}
        this.open = true
        this.title = '修改字典数据'
      })
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          return
        }
        const action = this.form.dictCode !== undefined ? updateData : addData
        const successMessage = this.form.dictCode !== undefined ? '修改成功' : '新增成功'
        action(this.form).then(() => {
          this.msgSuccess(successMessage)
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const dictCodes = row?.dictCode || this.ids
      this.$confirm('是否确认删除字典编码为"' + dictCodes + '"的数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => delData(dictCodes))
        .then(() => {
          this.getList()
          this.msgSuccess('删除成功')
        })
    },
    handleExport() {
      const queryParams = this.queryParams
      this.$confirm('是否确认导出所有数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => exportData(queryParams))
        .then((response) => {
          this.download(response.msg)
        })
    }
  }
}
</script>
