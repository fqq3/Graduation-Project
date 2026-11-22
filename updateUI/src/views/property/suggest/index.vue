<template>
  <div class="app-container">
    <el-form ref="queryForm" :model="queryParams" :inline="true" label-width="88px">
      <el-form-item label="类型" prop="complaintSuggestType">
        <el-select v-model="queryParams.complaintSuggestType" placeholder="请选择类型" clearable size="small">
          <el-option v-for="item in suggestOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="内容" prop="complaintSuggestContent">
        <el-input v-model="queryParams.complaintSuggestContent" placeholder="请输入投诉建议内容" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button class="el-button--cyan" size="small" @click="handleQuery"><el-icon><Search /></el-icon><span>搜索</span></el-button>
        <el-button size="small" @click="resetQuery"><el-icon><Refresh /></el-icon><span>重置</span></el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" size="small" @click="handleAdd" v-hasPermi="['system:suggest:add']">
          <el-icon><Plus /></el-icon><span>新增</span>
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" size="small" :disabled="single" @click="handleUpdate" v-hasPermi="['system:suggest:edit']">
          <el-icon><Edit /></el-icon><span>修改</span>
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" size="small" :disabled="multiple" @click="handleDelete" v-hasPermi="['system:suggest:remove']">
          <el-icon><Delete /></el-icon><span>删除</span>
        </el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="suggestList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="50" align="center" />
      <el-table-column label="类型" align="center" prop="complaintSuggestType" width="100">
        <template #default="scope">{{ selectStaticLabel(suggestOptions, scope.row.complaintSuggestType) }}</template>
      </el-table-column>
      <el-table-column label="所属小区" align="center" prop="communityName" min-width="120" />
      <el-table-column label="创建人" align="center" prop="ownerRealName" min-width="120" />
      <el-table-column label="联系电话" align="center" prop="ownerPhoneNumber" min-width="120" />
      <el-table-column label="内容" align="center" prop="complaintSuggestContent" min-width="260" :show-overflow-tooltip="{ popperClass: 'suggest-content-tooltip' }" />
      <el-table-column label="备注" align="center" prop="remark" min-width="160" show-overflow-tooltip />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
        <template #default="scope">
          <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['system:suggest:edit']">
            <el-icon><Edit /></el-icon><span>修改</span>
          </el-button>
          <el-button link type="primary" @click="handleDelete(scope.row)" v-hasPermi="['system:suggest:remove']">
            <el-icon><Delete /></el-icon><span>删除</span>
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="720px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="96px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="类型" prop="complaintSuggestType">
              <el-select v-model="form.complaintSuggestType" placeholder="请选择类型">
                <el-option v-for="item in suggestOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="创建人" prop="userId">
              <el-select v-model="form.userId" filterable clearable placeholder="请选择创建人">
                <el-option
                  v-for="item in ownerOptions"
                  :key="item.ownerId"
                  :label="(item.ownerRealName || item.ownerNickname) + ' / ' + item.ownerPhoneNumber"
                  :value="item.ownerId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="内容" prop="complaintSuggestContent">
              <el-input v-model="form.complaintSuggestContent" type="textarea" :rows="6" placeholder="请输入投诉建议内容" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确定</el-button>
          <el-button @click="cancel">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { Delete, Edit, Plus, Refresh, Search } from '@element-plus/icons-vue'
import { listOwner } from '@/api/property/owner'
import { addSuggest, delSuggest, getSuggest, listSuggest, updateSuggest } from '@/api/property/suggest'
import { getPropertyId } from '@/utils/auth'

export default {
  name: 'Suggest',
  components: { Delete, Edit, Plus, Refresh, Search },
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      total: 0,
      suggestList: [],
      ownerOptions: [],
      title: '',
      open: false,
      suggestOptions: [
        { value: 'Complaint', label: '投诉' },
        { value: 'Suggest', label: '建议' }
      ],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        communityId: null,
        complaintSuggestType: null,
        complaintSuggestContent: null
      },
      form: {},
      rules: {
        complaintSuggestType: [{ required: true, message: '类型不能为空', trigger: 'change' }],
        userId: [{ required: true, message: '创建人不能为空', trigger: 'change' }],
        complaintSuggestContent: [{ required: true, message: '内容不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.syncCommunityId()
    this.syncKeyword()
    this.loadOwners()
    this.getList()
  },
  watch: {
    '$route.query.communityId'(newValue, oldValue) {
      if (String(newValue || '') === String(oldValue || '')) {
        return
      }
      this.syncCommunityId()
      this.loadOwners()
      this.getList()
    },
    '$route.query.keyword'(newValue, oldValue) {
      if (String(newValue || '') === String(oldValue || '')) {
        return
      }
      this.syncKeyword()
      this.getList()
    },
    '$route.query.complaintSuggestContent'(newValue, oldValue) {
      if (String(newValue || '') === String(oldValue || '')) {
        return
      }
      this.syncKeyword()
      this.getList()
    },
    '$route.query.complaintSuggestType'(newValue, oldValue) {
      if (String(newValue || '') === String(oldValue || '')) {
        return
      }
      this.syncKeyword()
      this.getList()
    }
  },
  methods: {
    getCurrentPropertyId() {
      return this.$route.query.communityId || getPropertyId() || null
    },
    syncCommunityId() {
      const propertyId = this.getCurrentPropertyId()
      this.queryParams.communityId = propertyId
      if (this.form && typeof this.form === 'object') {
        this.form.communityId = propertyId
      }
    },
    syncKeyword() {
      const keyword = this.$route.query.keyword || this.$route.query.complaintSuggestContent || null
      this.queryParams.complaintSuggestContent = keyword
      this.queryParams.complaintSuggestType = this.$route.query.complaintSuggestType || null
    },
    loadOwners() {
      listOwner({ pageNum: 1, pageSize: 1000 }).then((response) => {
        const rows = response.rows || []
        const propertyId = this.getCurrentPropertyId()
        this.ownerOptions = propertyId ? rows.filter((item) => String(item.communityId) === String(propertyId)) : rows
      })
    },
    selectStaticLabel(options, value) {
      const target = (options || []).find((item) => item.value === value)
      return target ? target.label : value
    },
    getList() {
      this.loading = true
      this.queryParams.communityId = this.getCurrentPropertyId()
      listSuggest(this.queryParams).then((response) => {
        this.suggestList = response.rows || []
        this.total = response.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.syncCommunityId()
      this.handleQuery()
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        complaintSuggestId: null,
        communityId: this.getCurrentPropertyId(),
        complaintSuggestType: null,
        complaintSuggestContent: null,
        userId: null,
        remark: null
      }
      this.resetForm('form')
    },
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.complaintSuggestId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.loadOwners()
      this.open = true
      this.title = '新增投诉建议'
    },
    handleUpdate(row) {
      this.reset()
      const complaintSuggestId = row.complaintSuggestId || this.ids[0]
      getSuggest(complaintSuggestId).then((response) => {
        this.form = {
          ...this.form,
          ...response.data
        }
        this.loadOwners()
        this.open = true
        this.title = '修改投诉建议'
      })
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          return
        }
        this.form.communityId = this.getCurrentPropertyId()
        const action = this.form.complaintSuggestId ? updateSuggest : addSuggest
        const message = this.form.complaintSuggestId ? '修改成功' : '新增成功'
        action(this.form).then(() => {
          this.msgSuccess(message)
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const complaintSuggestIds = row.complaintSuggestId || this.ids
      this.$confirm(`是否确认删除投诉建议编号为 "${complaintSuggestIds}" 的数据项？`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => delSuggest(complaintSuggestIds)).then(() => {
        this.getList()
        this.msgSuccess('删除成功')
      })
    }
  }
}
</script>

<style>
.suggest-content-tooltip { color: #ffffff !important; background: #ec797b !important; border-color: #ec797b !important; }
.suggest-content-tooltip .el-popper__arrow::before { background: #ec797b !important; border-color: #ec797b !important; }
</style>
