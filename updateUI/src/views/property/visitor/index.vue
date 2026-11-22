<template>
  <div class="app-container">
    <el-form ref="queryForm" :model="queryParams" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="访客姓名" prop="visitorName">
        <el-input v-model="queryParams.visitorName" placeholder="请输入访客姓名" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="访客手机号" prop="visitorPhoneNumber">
        <el-input v-model="queryParams.visitorPhoneNumber" placeholder="请输入访客手机号" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="到访日期" prop="visitorDate">
        <el-date-picker v-model="queryParams.visitorDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择到访日期" clearable size="small" style="width: 180px" />
      </el-form-item>
      <el-form-item>
        <el-button class="el-button--cyan" size="small" @click="handleQuery"><el-icon><Search /></el-icon><span>搜索</span></el-button>
        <el-button size="small" @click="resetQuery"><el-icon><Refresh /></el-icon><span>重置</span></el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" size="small" @click="handleAdd" v-hasPermi="['system:visitor:add']">
          <el-icon><Plus /></el-icon><span>新增</span>
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" size="small" :disabled="single" @click="handleUpdate" v-hasPermi="['system:visitor:edit']">
          <el-icon><Edit /></el-icon><span>修改</span>
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" size="small" :disabled="multiple" @click="handleDelete" v-hasPermi="['system:visitor:remove']">
          <el-icon><Delete /></el-icon><span>删除</span>
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" size="small" @click="handleExport" v-hasPermi="['system:visitor:export']">
          <el-icon><Download /></el-icon><span>导出</span>
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="visitorList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="50" align="center" />
      <el-table-column label="访客姓名" align="center" prop="visitorName" min-width="120" />
      <el-table-column label="访客手机号" align="center" prop="visitorPhoneNumber" min-width="120" />
      <el-table-column label="所属小区" align="center" prop="communityName" min-width="120" />
      <el-table-column label="邀请业主" align="center" prop="ownerRealName" min-width="120" />
      <el-table-column label="业主电话" align="center" prop="ownerPhoneNumber" min-width="120" />
      <el-table-column label="到访日期" align="center" prop="visitorDate" width="120">
        <template #default="scope">{{ parseTime(scope.row.visitorDate, '{y}-{m}-{d}') }}</template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="备注" align="center" prop="remark" min-width="160" show-overflow-tooltip />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
        <template #default="scope">
          <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['system:visitor:edit']">
            <el-icon><Edit /></el-icon><span>修改</span>
          </el-button>
          <el-button link type="primary" @click="handleDelete(scope.row)" v-hasPermi="['system:visitor:remove']">
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
            <el-form-item label="访客姓名" prop="visitorName">
              <el-input v-model="form.visitorName" placeholder="请输入访客姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="访客手机号" prop="visitorPhoneNumber">
              <el-input v-model="form.visitorPhoneNumber" placeholder="请输入访客手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="到访日期" prop="visitorDate">
              <el-date-picker v-model="form.visitorDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择到访日期" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邀请业主" prop="createById">
              <el-select v-model="form.createById" filterable clearable placeholder="请选择邀请业主" @change="handleOwnerChange">
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
import { Delete, Download, Edit, Plus, Refresh, Search } from '@element-plus/icons-vue'
import { listOwner } from '@/api/property/owner'
import { addVisitor, delVisitor, exportVisitor, getVisitor, listVisitor, updateVisitor } from '@/api/property/visitor'
import { getPropertyId } from '@/utils/auth'

export default {
  name: 'Visitor',
  components: { Delete, Download, Edit, Plus, Refresh, Search },
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      visitorList: [],
      ownerOptions: [],
      title: '',
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        communityId: null,
        visitorName: null,
        visitorPhoneNumber: null,
        visitorDate: null
      },
      form: {},
      rules: {
        visitorName: [{ required: true, message: '访客姓名不能为空', trigger: 'blur' }],
        visitorPhoneNumber: [{ required: true, message: '访客手机号不能为空', trigger: 'blur' }],
        visitorDate: [{ required: true, message: '到访日期不能为空', trigger: 'change' }]
      }
    }
  },
  created() {
    this.syncCommunityId()
    this.loadOwners()
    this.getList()
  },
  methods: {
    getCurrentPropertyId() {
      return getPropertyId() || null
    },
    syncCommunityId() {
      const propertyId = this.getCurrentPropertyId()
      this.queryParams.communityId = propertyId
      if (this.form && typeof this.form === 'object') {
        this.form.communityId = propertyId
      }
    },
    loadOwners() {
      listOwner({ pageNum: 1, pageSize: 1000, communityId: this.getCurrentPropertyId() }).then((response) => {
        this.ownerOptions = response.rows || []
      })
    },
    handleOwnerChange(ownerId) {
      const owner = (this.ownerOptions || []).find((item) => String(item.ownerId) === String(ownerId))
      if (owner && !this.form.createByOpenId) {
        this.form.createByOpenId = owner.ownerOpenId
      }
    },
    getList() {
      this.loading = true
      this.queryParams.communityId = this.getCurrentPropertyId()
      listVisitor(this.queryParams).then((response) => {
        this.visitorList = response.rows || []
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
        visitorId: null,
        communityId: this.getCurrentPropertyId(),
        visitorName: null,
        visitorPhoneNumber: null,
        visitorDate: null,
        createById: null,
        createByOpenId: null,
        remark: null
      }
      this.resetForm('form')
    },
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.visitorId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.loadOwners()
      this.open = true
      this.title = '新增访客邀请'
    },
    handleUpdate(row) {
      this.reset()
      const visitorId = row.visitorId || this.ids[0]
      getVisitor(visitorId).then((response) => {
        this.form = {
          ...this.form,
          ...response.data
        }
        this.loadOwners()
        this.open = true
        this.title = '修改访客邀请'
      })
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          return
        }
        this.form.communityId = this.getCurrentPropertyId()
        const action = this.form.visitorId ? updateVisitor : addVisitor
        const message = this.form.visitorId ? '修改成功' : '新增成功'
        action(this.form).then(() => {
          this.msgSuccess(message)
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const visitorIds = row.visitorId || this.ids
      this.$confirm(`是否确认删除访客邀请编号为 "${visitorIds}" 的数据项？`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => delVisitor(visitorIds)).then(() => {
        this.getList()
        this.msgSuccess('删除成功')
      })
    },
    handleExport() {
      const queryParams = { ...this.queryParams }
      this.$confirm('是否确认导出所有访客邀请数据项？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => exportVisitor(queryParams)).then((response) => {
        this.download(response.msg)
      })
    }
  }
}
</script>
