<template>
  <div class="app-container">
    <el-form ref="queryForm" :model="queryParams" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="昵称" prop="ownerNickname">
        <el-input v-model="queryParams.ownerNickname" placeholder="请输入昵称" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="真实姓名" prop="ownerRealName">
        <el-input v-model="queryParams.ownerRealName" placeholder="请输入真实姓名" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="身份证号" prop="ownerIdCard">
        <el-input v-model="queryParams.ownerIdCard" placeholder="请输入身份证号" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="手机号" prop="ownerPhoneNumber">
        <el-input v-model="queryParams.ownerPhoneNumber" placeholder="请输入手机号" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button class="el-button--cyan" size="small" @click="handleQuery"><el-icon><Search /></el-icon><span>搜索</span></el-button>
        <el-button size="small" @click="resetQuery"><el-icon><Refresh /></el-icon><span>重置</span></el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" size="small" @click="handleAdd" v-hasPermi="['system:owner:add']">
          <el-icon><Plus /></el-icon><span>新增</span>
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" size="small" :disabled="single" @click="handleUpdate" v-hasPermi="['system:owner:edit']">
          <el-icon><Edit /></el-icon><span>修改</span>
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" size="small" :disabled="multiple" @click="handleDelete" v-hasPermi="['system:owner:remove']">
          <el-icon><Delete /></el-icon><span>删除</span>
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" size="small" @click="handleExport" v-hasPermi="['system:owner:export']">
          <el-icon><Download /></el-icon><span>导出</span>
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="ownerList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="50" align="center" />
      <el-table-column label="昵称" align="center" prop="ownerNickname" min-width="100" />
      <el-table-column label="真实姓名" align="center" prop="ownerRealName" min-width="120" />
      <el-table-column label="性别" align="center" prop="ownerGender">
        <template #default="scope">{{ selectStaticLabel(genderOptions, scope.row.ownerGender) }}</template>
      </el-table-column>
      <el-table-column label="年龄" align="center" prop="ownerAge" width="80" />
      <el-table-column label="身份证号" align="center" prop="ownerIdCard" min-width="180" />
      <el-table-column label="手机号" align="center" prop="ownerPhoneNumber" min-width="120" />
      <el-table-column label="小区" align="center" prop="communityName" min-width="120" />
      <el-table-column label="楼栋" align="center" prop="buildingName" min-width="100" />
      <el-table-column label="单元" align="center" prop="unitName" min-width="100" />
      <el-table-column label="房屋" align="center" prop="roomName" min-width="100" />
      <el-table-column label="生日" align="center" prop="ownerBirthday" width="120">
        <template #default="scope">{{ parseTime(scope.row.ownerBirthday, '{y}-{m}-{d}') }}</template>
      </el-table-column>
      <el-table-column label="业主类型" align="center" prop="ownerType">
        <template #default="scope">{{ selectDictLabel(ownerTypeOptions, scope.row.ownerType) }}</template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
        <template #default="scope">
          <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['system:owner:edit']">
            <el-icon><Edit /></el-icon><span>修改</span>
          </el-button>
          <el-button link type="primary" @click="handleDelete(scope.row)" v-hasPermi="['system:owner:remove']">
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
            <el-form-item label="昵称" prop="ownerNickname">
              <el-input v-model="form.ownerNickname" placeholder="请输入昵称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="ownerRealName">
              <el-input v-model="form.ownerRealName" placeholder="请输入真实姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="ownerGender">
              <el-select v-model="form.ownerGender" placeholder="请选择性别">
                <el-option v-for="item in genderOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄" prop="ownerAge">
              <el-input-number v-model="form.ownerAge" :min="0" :max="150" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="ownerIdCard">
              <el-input v-model="form.ownerIdCard" placeholder="请输入身份证号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="ownerPhoneNumber">
              <el-input v-model="form.ownerPhoneNumber" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出生日期" prop="ownerBirthday">
              <el-date-picker v-model="form.ownerBirthday" type="date" value-format="YYYY-MM-DD" placeholder="请选择出生日期" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="业主类型" prop="ownerType">
              <el-select v-model="form.ownerType" placeholder="请选择业主类型">
                <el-option v-for="item in ownerTypeOptions" :key="item.dictValue" :label="item.dictLabel" :value="item.dictValue" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="微信号" prop="ownerWechatId">
              <el-input v-model="form.ownerWechatId" placeholder="请输入微信号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="QQ号" prop="ownerQqNumber">
              <el-input v-model="form.ownerQqNumber" placeholder="请输入QQ号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="个性签名" prop="ownerSignature">
              <el-input v-model="form.ownerSignature" placeholder="请输入个性签名" />
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
import { addOwner, delOwner, exportOwner, getOwner, listOwner, updateOwner } from '@/api/property/owner'
import { getPropertyId } from '@/utils/auth'

export default {
  name: 'Owner',
  components: { Delete, Download, Edit, Plus, Refresh, Search },
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      ownerList: [],
      ownerTypeOptions: [],
      genderOptions: [
        { label: '男', value: 'Male' },
        { label: '女', value: 'Female' },
        { label: '未知', value: 'UnKnown' }
      ],
      title: '',
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        communityId: null,
        ownerNickname: null,
        ownerRealName: null,
        ownerIdCard: null,
        ownerPhoneNumber: null
      },
      form: {},
      rules: {
        ownerRealName: [{ required: true, message: '真实姓名不能为空', trigger: 'blur' }],
        ownerPhoneNumber: [{ required: true, message: '手机号不能为空', trigger: 'blur' }],
        ownerType: [{ required: true, message: '业主类型不能为空', trigger: 'change' }]
      }
    }
  },
  created() {
    this.syncCommunityId()
    this.getList()
    this.getDicts('owner_state').then((response) => {
      this.ownerTypeOptions = response.data || []
    })
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
    selectStaticLabel(options, value) {
      const target = (options || []).find((item) => item.value === value)
      return target ? target.label : value
    },
    getList() {
      this.loading = true
      this.queryParams.communityId = this.getCurrentPropertyId()
      listOwner(this.queryParams).then((response) => {
        this.ownerList = response.rows || []
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
        ownerId: null,
        ownerNickname: null,
        ownerRealName: null,
        ownerGender: 'UnKnown',
        ownerAge: null,
        ownerIdCard: null,
        ownerPhoneNumber: null,
        ownerOpenId: null,
        ownerWechatId: null,
        ownerQqNumber: null,
        ownerBirthday: null,
        ownerPortrait: null,
        ownerSignature: null,
        ownerStatus: '0',
        ownerLogonMode: null,
        ownerType: null,
        ownerPassword: null,
        communityId: this.getCurrentPropertyId(),
        remark: null
      }
      this.resetForm('form')
    },
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.ownerId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增业主信息'
    },
    handleUpdate(row) {
      this.reset()
      const ownerId = row.ownerId || this.ids[0]
      getOwner(ownerId).then((response) => {
        this.form = {
          ...this.form,
          ...response.data
        }
        this.open = true
        this.title = '修改业主信息'
      })
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          return
        }
        this.form.communityId = this.getCurrentPropertyId()
        const action = this.form.ownerId ? updateOwner : addOwner
        const message = this.form.ownerId ? '修改成功' : '新增成功'
        action(this.form).then(() => {
          this.msgSuccess(message)
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const ownerIds = row.ownerId || this.ids
      this.$confirm(`是否确认删除业主编号为 "${ownerIds}" 的数据项？`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => delOwner(ownerIds)).then(() => {
        this.getList()
        this.msgSuccess('删除成功')
      })
    },
    handleExport() {
      const queryParams = { ...this.queryParams }
      this.$confirm('是否确认导出所有业主信息数据项？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => exportOwner(queryParams)).then((response) => {
        this.download(response.msg)
      })
    }
  }
}
</script>
