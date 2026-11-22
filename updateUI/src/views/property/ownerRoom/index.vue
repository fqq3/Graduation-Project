<template>
  <div class="app-container">
    <el-form ref="queryForm" :model="queryParams" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="绑定状态" prop="roomStatus">
        <el-select v-model="queryParams.roomStatus" placeholder="请选择绑定状态" clearable size="small">
          <el-option v-for="item in bindingOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="业主类型" prop="ownerType">
        <el-select v-model="queryParams.ownerType" placeholder="请选择业主类型" clearable size="small">
          <el-option v-for="item in ownerTypeOptions" :key="item.dictValue" :label="item.dictLabel" :value="item.dictValue" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button class="el-button--cyan" size="small" @click="handleQuery"><el-icon><Search /></el-icon><span>搜索</span></el-button>
        <el-button size="small" @click="resetQuery"><el-icon><Refresh /></el-icon><span>重置</span></el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" size="small" @click="handleAdd" v-hasPermi="['system:ownerRoom:add']">
          <el-icon><Plus /></el-icon><span>新增</span>
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" size="small" :disabled="single" @click="handleUpdate" v-hasPermi="['system:ownerRoom:edit']">
          <el-icon><Edit /></el-icon><span>修改</span>
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" size="small" :disabled="multiple" @click="handleDelete" v-hasPermi="['system:ownerRoom:remove']">
          <el-icon><Delete /></el-icon><span>删除</span>
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="ownerRoomList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="50" align="center" />
      <el-table-column label="小区名称" align="center" prop="communityName" min-width="120" />
      <el-table-column label="楼栋" align="center" prop="buildingName" min-width="100" />
      <el-table-column label="单元" align="center" prop="unitName" min-width="100" />
      <el-table-column label="房屋" align="center" prop="roomName" min-width="120" />
      <el-table-column label="业主姓名" align="center" prop="ownerRealName" min-width="120" />
      <el-table-column label="联系电话" align="center" prop="ownerPhoneNumber" min-width="120" />
      <el-table-column label="业主类型" align="center" prop="ownerType">
        <template #default="scope">{{ selectDictLabel(ownerTypeOptions, scope.row.ownerType) }}</template>
      </el-table-column>
      <el-table-column label="绑定状态" align="center" prop="roomStatus">
        <template #default="scope">{{ selectStaticLabel(bindingOptions, scope.row.roomStatus) }}</template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" min-width="160" show-overflow-tooltip />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="260">
        <template #default="scope">
          <el-button
            v-if="scope.row.roomStatus === 'Auditing'"
            link
            type="primary"
            @click="handleAudit(scope.row)"
            v-hasPermi="['system:ownerRoom:edit']"
          >
            <el-icon><Pointer /></el-icon><span>审核</span>
          </el-button>
          <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['system:ownerRoom:edit']">
            <el-icon><Edit /></el-icon><span>修改</span>
          </el-button>
          <el-button link type="primary" @click="handleRecord(scope.row)" v-hasPermi="['system:ownerRoom:list']">
            <el-icon><Fold /></el-icon><span>记录</span>
          </el-button>
          <el-button link type="primary" @click="handleDelete(scope.row)" v-hasPermi="['system:ownerRoom:remove']">
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
            <el-form-item label="所属业主" prop="ownerId">
              <el-select v-model="form.ownerId" filterable clearable placeholder="请选择业主">
                <el-option
                  v-for="item in ownerOptions"
                  :key="item.ownerId"
                  :label="(item.ownerRealName || item.ownerNickname) + ' / ' + item.ownerPhoneNumber"
                  :value="item.ownerId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属房屋" prop="roomId">
              <el-select v-model="form.roomId" filterable clearable placeholder="请选择房屋" @change="handleRoomChange">
                <el-option v-for="item in roomOptions" :key="item.roomId" :label="formatRoomOption(item)" :value="item.roomId" />
              </el-select>
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
            <el-form-item label="绑定状态" prop="roomStatus">
              <el-select v-model="form.roomStatus" placeholder="请选择绑定状态">
                <el-option v-for="item in bindingOptions" :key="item.value" :label="item.label" :value="item.value" />
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

    <el-dialog title="审核业主绑定" v-model="auditOpen" width="520px" append-to-body>
      <el-form ref="auditForm" :model="auditForm" :rules="auditRules" label-width="96px">
        <el-form-item label="审核意见" prop="recordAuditOpinion">
          <el-input v-model="auditForm.recordAuditOpinion" type="textarea" placeholder="请输入审核意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitAudit('Binding')">同意</el-button>
          <el-button @click="submitAudit('Reject')">拒绝</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog title="审核记录" v-model="recordOpen" width="920px" append-to-body>
      <el-table v-loading="recordLoading" :data="recordList">
        <el-table-column label="序号" type="index" width="50" align="center" />
        <el-table-column label="业主姓名" align="center" prop="ownerRealName" min-width="120" />
        <el-table-column label="业主类型" align="center" prop="ownerType">
          <template #default="scope">{{ selectDictLabel(ownerTypeOptions, scope.row.ownerType) }}</template>
        </el-table-column>
        <el-table-column label="绑定状态" align="center" prop="roomStatus">
          <template #default="scope">{{ selectStaticLabel(bindingOptions, scope.row.roomStatus) }}</template>
        </el-table-column>
        <el-table-column label="审核意见" align="center" prop="recordAuditOpinion" min-width="180" show-overflow-tooltip />
        <el-table-column label="审核类型" align="center" prop="recordAuditType">
          <template #default="scope">{{ selectStaticLabel(reviewerOptions, scope.row.recordAuditType) }}</template>
        </el-table-column>
        <el-table-column label="审核人" align="center" prop="createByName" min-width="100" />
        <el-table-column label="审核时间" align="center" prop="createTime" width="160" />
      </el-table>
      <pagination
        v-show="recordTotal > 0"
        :total="recordTotal"
        v-model:page="recordQueryParams.pageNum"
        v-model:limit="recordQueryParams.pageSize"
        @pagination="getRecordList"
      />
    </el-dialog>
  </div>
</template>

<script>
import { Delete, Edit, Fold, Plus, Pointer, Refresh, Search } from '@element-plus/icons-vue'
import { listOwner } from '@/api/property/owner'
import {
  addOwnerRoom,
  delOwnerRoom,
  getOwnerRoom,
  listOwnerRoom,
  listOwnerRoomRecord,
  updateOwnerRoom
} from '@/api/property/ownerRoom'
import { listRoom as listPhysicalRoom } from '@/api/property/room'
import { getPropertyId } from '@/utils/auth'

export default {
  name: 'OwnerRoom',
  components: { Delete, Edit, Fold, Plus, Pointer, Refresh, Search },
  data() {
    return {
      loading: true,
      recordLoading: false,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      ownerRoomList: [],
      recordList: [],
      ownerOptions: [],
      roomOptions: [],
      title: '',
      open: false,
      auditOpen: false,
      recordOpen: false,
      ownerTypeOptions: [],
      bindingOptions: [
        { value: 'Auditing', label: '审核中' },
        { value: 'Binding', label: '已绑定' },
        { value: 'Reject', label: '已拒绝' },
        { value: 'Unbind', label: '已解绑' }
      ],
      reviewerOptions: [
        { value: 'Web', label: '物业后台' },
        { value: 'App', label: '业主端' }
      ],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        communityId: null,
        ownerType: null,
        roomStatus: null
      },
      recordQueryParams: {
        pageNum: 1,
        pageSize: 10,
        ownerRoomId: null,
        communityId: null
      },
      recordTotal: 0,
      form: {},
      auditForm: {
        ownerRoomId: null,
        recordAuditOpinion: ''
      },
      rules: {
        ownerId: [{ required: true, message: '所属业主不能为空', trigger: 'change' }],
        roomId: [{ required: true, message: '所属房屋不能为空', trigger: 'change' }],
        ownerType: [{ required: true, message: '业主类型不能为空', trigger: 'change' }],
        roomStatus: [{ required: true, message: '绑定状态不能为空', trigger: 'change' }]
      },
      auditRules: {
        recordAuditOpinion: [{ required: true, message: '审核意见不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.syncCommunityId()
    this.getDicts('owner_state').then((response) => {
      this.ownerTypeOptions = response.data || []
    })
    this.loadOwners()
    this.loadRooms()
    this.getList()
  },
  methods: {
    getCurrentPropertyId() {
      return getPropertyId() || null
    },
    syncCommunityId() {
      const propertyId = this.getCurrentPropertyId()
      this.queryParams.communityId = propertyId
      this.recordQueryParams.communityId = propertyId
      if (this.form && typeof this.form === 'object') {
        this.form.communityId = propertyId
      }
    },
    loadOwners() {
      listOwner({ pageNum: 1, pageSize: 1000 }).then((response) => {
        const rows = response.rows || []
        const propertyId = this.getCurrentPropertyId()
        this.ownerOptions = propertyId ? rows.filter((item) => String(item.communityId) === String(propertyId)) : rows
      })
    },
    loadRooms() {
      listPhysicalRoom({ pageNum: 1, pageSize: 1000, communityId: this.getCurrentPropertyId() }).then((response) => {
        this.roomOptions = response.rows || []
      })
    },
    formatRoomOption(item) {
      return [item.communityName, item.buildingName, item.unitName, item.roomName].filter(Boolean).join(' / ')
    },
    selectStaticLabel(options, value) {
      const target = (options || []).find((item) => item.value === value)
      return target ? target.label : value
    },
    getList() {
      this.loading = true
      this.queryParams.communityId = this.getCurrentPropertyId()
      listOwnerRoom(this.queryParams).then((response) => {
        this.ownerRoomList = response.rows || []
        this.total = response.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    getRecordList() {
      this.recordLoading = true
      this.recordQueryParams.communityId = this.getCurrentPropertyId()
      listOwnerRoomRecord(this.recordQueryParams).then((response) => {
        this.recordList = response.rows || []
        this.recordTotal = response.total || 0
      }).finally(() => {
        this.recordLoading = false
      })
    },
    handleRoomChange(roomId) {
      const room = (this.roomOptions || []).find((item) => String(item.roomId) === String(roomId))
      if (!room) {
        return
      }
      this.form.communityId = room.communityId || this.getCurrentPropertyId()
      this.form.buildingId = room.buildingId
      this.form.unitId = room.unitId
      this.form.roomId = room.roomId
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        ownerRoomId: null,
        communityId: this.getCurrentPropertyId(),
        buildingId: null,
        unitId: null,
        roomId: null,
        ownerId: null,
        ownerType: null,
        roomStatus: 'Auditing',
        remark: null
      }
      this.resetForm('form')
    },
    resetAuditForm() {
      this.auditForm = {
        ownerRoomId: null,
        recordAuditOpinion: ''
      }
      this.resetForm('auditForm')
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
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.ownerRoomId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.loadOwners()
      this.loadRooms()
      this.open = true
      this.title = '新增业主审核'
    },
    handleUpdate(row) {
      this.reset()
      const ownerRoomId = row.ownerRoomId || this.ids[0]
      getOwnerRoom(ownerRoomId).then((response) => {
        this.form = {
          ...this.form,
          ...response.data
        }
        this.loadOwners()
        this.loadRooms()
        this.open = true
        this.title = '修改业主审核'
      })
    },
    handleAudit(row) {
      this.resetAuditForm()
      this.auditForm.ownerRoomId = row.ownerRoomId
      this.auditOpen = true
    },
    submitAudit(status) {
      this.$refs.auditForm.validate((valid) => {
        if (!valid) {
          return
        }
        updateOwnerRoom({
          ownerRoomId: this.auditForm.ownerRoomId,
          roomStatus: status,
          recordAuditOpinion: this.auditForm.recordAuditOpinion,
          recordAuditType: 'Web'
        }).then(() => {
          this.msgSuccess('审核成功')
          this.auditOpen = false
          this.getList()
        })
      })
    },
    handleRecord(row) {
      this.recordQueryParams.pageNum = 1
      this.recordQueryParams.ownerRoomId = String(row.ownerRoomId)
      this.recordOpen = true
      this.getRecordList()
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          return
        }
        if (!this.form.communityId) {
          this.form.communityId = this.getCurrentPropertyId()
        }
        const action = this.form.ownerRoomId ? updateOwnerRoom : addOwnerRoom
        const message = this.form.ownerRoomId ? '修改成功' : '新增成功'
        action(this.form).then(() => {
          this.msgSuccess(message)
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const ownerRoomIds = row.ownerRoomId || this.ids
      this.$confirm(`是否确认删除业主审核编号为 "${ownerRoomIds}" 的数据项？`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => delOwnerRoom(ownerRoomIds)).then(() => {
        this.getList()
        this.msgSuccess('删除成功')
      })
    }
  }
}
</script>
