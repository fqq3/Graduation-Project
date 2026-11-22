<template>
  <div class="app-container">
    <el-form ref="queryForm" :model="queryParams" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="账单编号" prop="billNo">
        <el-input v-model="queryParams.billNo" placeholder="请输入账单编号" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="业主姓名" prop="ownerName">
        <el-input v-model="queryParams.ownerName" placeholder="请输入业主姓名" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="费用项目" prop="itemName">
        <el-input v-model="queryParams.itemName" placeholder="请输入费用项目" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="账单年份" prop="billYear">
        <el-input-number v-model="queryParams.billYear" :min="2000" :max="2100" controls-position="right" style="width: 140px" />
      </el-form-item>
      <el-form-item label="账单月份" prop="billMonth">
        <el-input-number v-model="queryParams.billMonth" :min="1" :max="12" controls-position="right" style="width: 140px" />
      </el-form-item>
      <el-form-item label="状态" prop="billStatus">
        <el-select v-model="queryParams.billStatus" placeholder="请选择状态" clearable size="small">
          <el-option v-for="item in billStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button class="el-button--cyan" size="small" @click="handleQuery"><el-icon><Search /></el-icon><span>搜索</span></el-button>
        <el-button size="small" @click="resetQuery"><el-icon><Refresh /></el-icon><span>重置</span></el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" size="small" @click="handleAdd" v-hasPermi="['community:feeBill:add']">
          <el-icon><Plus /></el-icon><span>新增</span>
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" size="small" :disabled="single" @click="handleUpdate" v-hasPermi="['community:feeBill:edit']">
          <el-icon><Edit /></el-icon><span>修改</span>
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" size="small" :disabled="multiple" @click="handleDelete" v-hasPermi="['community:feeBill:remove']">
          <el-icon><Delete /></el-icon><span>删除</span>
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="feeBillList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="50" align="center" />
      <el-table-column label="账单编号" align="center" prop="billNo" min-width="150" />
      <el-table-column label="业主姓名" align="center" min-width="120">
        <template #default="scope">{{ scope.row.ownerRealName || scope.row.ownerName }}</template>
      </el-table-column>
      <el-table-column label="房屋" align="center" min-width="180">
        <template #default="scope">{{ formatRoomName(scope.row) }}</template>
      </el-table-column>
      <el-table-column label="费用项目" align="center" prop="itemName" min-width="120" />
      <el-table-column label="账期" align="center" min-width="100">
        <template #default="scope">{{ `${scope.row.billYear || '-'}-${String(scope.row.billMonth || '').padStart(2, '0')}` }}</template>
      </el-table-column>
      <el-table-column label="应收金额" align="center" prop="receivableAmount" />
      <el-table-column label="实收金额" align="center" prop="receivedAmount" />
      <el-table-column label="未收金额" align="center" prop="unpaidAmount" />
      <el-table-column label="到期日期" align="center" prop="dueDate" width="120" />
      <el-table-column label="状态" align="center" prop="billStatus">
        <template #default="scope">{{ selectStaticLabel(billStatusOptions, scope.row.billStatus) }}</template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
        <template #default="scope">
          <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['community:feeBill:edit']">
            <el-icon><Edit /></el-icon><span>修改</span>
          </el-button>
          <el-button link type="primary" @click="handleDelete(scope.row)" v-hasPermi="['community:feeBill:remove']">
            <el-icon><Delete /></el-icon><span>删除</span>
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="760px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="96px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="所属业主" prop="ownerId">
              <el-select v-model="form.ownerId" filterable clearable placeholder="请选择业主" @change="handleOwnerChange">
                <el-option v-for="item in ownerOptions" :key="item.ownerId" :label="(item.ownerRealName || item.ownerNickname) + ' / ' + item.ownerPhoneNumber" :value="item.ownerId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属房屋" prop="roomId">
              <el-select v-model="form.roomId" filterable clearable placeholder="请选择房屋">
                <el-option v-for="item in roomOptions" :key="item.roomId" :label="formatRoomOption(item)" :value="item.roomId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="费用项目" prop="itemId">
              <el-select v-model="form.itemId" filterable clearable placeholder="请选择费用项目" @change="handleItemChange">
                <el-option v-for="item in feeItemOptions" :key="item.itemId" :label="item.itemName" :value="item.itemId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账单编号" prop="billNo">
              <el-input v-model="form.billNo" placeholder="留空则自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账单年份" prop="billYear">
              <el-input-number v-model="form.billYear" :min="2000" :max="2100" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账单月份" prop="billMonth">
              <el-input-number v-model="form.billMonth" :min="1" :max="12" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="应收金额" prop="receivableAmount">
              <el-input-number v-model="form.receivableAmount" :min="0" :precision="2" :step="0.01" controls-position="right" style="width: 100%" @change="syncAmounts" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="实收金额" prop="receivedAmount">
              <el-input-number v-model="form.receivedAmount" :min="0" :precision="2" :step="0.01" controls-position="right" style="width: 100%" @change="syncAmounts" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="未收金额" prop="unpaidAmount">
              <el-input-number v-model="form.unpaidAmount" :min="0" :precision="2" :step="0.01" controls-position="right" style="width: 100%" @change="syncStatusByAmounts" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="到期日期" prop="dueDate">
              <el-date-picker v-model="form.dueDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择到期日期" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账单状态" prop="billStatus">
              <el-select v-model="form.billStatus" placeholder="请选择账单状态">
                <el-option v-for="item in billStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
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
import { Delete, Edit, Plus, Refresh, Search } from '@element-plus/icons-vue'
import { listOwner } from '@/api/property/owner'
import { addFeeBill, delFeeBill, getFeeBill, listFeeBill, updateFeeBill } from '@/api/property/feeBill'
import { listFeeItem } from '@/api/property/feeItem'
import { listRoom } from '@/api/property/room'
import { getPropertyId } from '@/utils/auth'

export default {
  name: 'FeeBill',
  components: { Delete, Edit, Plus, Refresh, Search },
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      feeBillList: [],
      ownerOptions: [],
      roomOptions: [],
      feeItemOptions: [],
      title: '',
      open: false,
      billStatusOptions: [
        { label: '待缴费', value: '0' },
        { label: '部分缴费', value: '1' },
        { label: '已缴清', value: '2' }
      ],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        communityId: null,
        billNo: null,
        ownerName: null,
        itemName: null,
        billYear: null,
        billMonth: null,
        billStatus: null
      },
      form: {},
      rules: {
        ownerId: [{ required: true, message: '所属业主不能为空', trigger: 'change' }],
        roomId: [{ required: true, message: '所属房屋不能为空', trigger: 'change' }],
        itemId: [{ required: true, message: '费用项目不能为空', trigger: 'change' }],
        billYear: [{ required: true, message: '账单年份不能为空', trigger: 'blur' }],
        billMonth: [{ required: true, message: '账单月份不能为空', trigger: 'blur' }],
        receivableAmount: [{ required: true, message: '应收金额不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.syncCommunityId()
    this.loadOwners()
    this.loadRooms()
    this.loadFeeItems()
    this.getList()
  },
  watch: {
    '$route.query.communityId'(newValue, oldValue) {
      if (newValue === oldValue) {
        return
      }
      this.queryParams.pageNum = 1
      this.syncCommunityId()
      this.loadOwners()
      this.loadRooms()
      this.getList()
    }
  },
  methods: {
    getCurrentPropertyId() {
      const routeCommunityId = this.$route?.query?.communityId
      return routeCommunityId || getPropertyId() || null
    },
    syncCommunityId() {
      const propertyId = this.getCurrentPropertyId()
      this.queryParams.communityId = propertyId
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
      listRoom({ pageNum: 1, pageSize: 1000, communityId: this.getCurrentPropertyId() }).then((response) => {
        this.roomOptions = response.rows || []
      })
    },
    loadFeeItems() {
      listFeeItem({ pageNum: 1, pageSize: 1000, status: '0' }).then((response) => {
        this.feeItemOptions = response.rows || []
      })
    },
    getList() {
      this.loading = true
      this.queryParams.communityId = this.getCurrentPropertyId()
      listFeeBill(this.queryParams).then((response) => {
        this.feeBillList = response.rows || []
        this.total = response.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    selectStaticLabel(options, value) {
      const target = (options || []).find((item) => item.value === value)
      return target ? target.label : value
    },
    formatRoomName(row) {
      return [row.communityName, row.buildingName, row.unitName, row.roomName].filter(Boolean).join(' / ')
    },
    formatRoomOption(item) {
      return [item.communityName, item.buildingName, item.unitName, item.roomName].filter(Boolean).join(' / ')
    },
    handleOwnerChange(ownerId) {
      const owner = (this.ownerOptions || []).find((item) => String(item.ownerId) === String(ownerId))
      if (!owner) {
        return
      }
      this.form.ownerName = owner.ownerRealName || owner.ownerNickname
      if (owner.roomId) {
        this.form.roomId = owner.roomId
      }
    },
    handleItemChange(itemId) {
      const feeItem = (this.feeItemOptions || []).find((item) => String(item.itemId) === String(itemId))
      if (!feeItem) {
        return
      }
      this.form.itemName = feeItem.itemName
      if (this.form.receivableAmount == null || Number(this.form.receivableAmount) === 0) {
        this.form.receivableAmount = feeItem.chargeStandard
      }
      this.syncAmounts()
    },
    syncAmounts() {
      const receivable = Number(this.form.receivableAmount || 0)
      const received = Number(this.form.receivedAmount || 0)
      const unpaid = receivable - received
      this.form.unpaidAmount = unpaid > 0 ? Number(unpaid.toFixed(2)) : 0
      this.syncStatusByAmounts()
    },
    syncStatusByAmounts() {
      const unpaid = Number(this.form.unpaidAmount || 0)
      const received = Number(this.form.receivedAmount || 0)
      if (unpaid <= 0) {
        this.form.billStatus = '2'
      } else if (received > 0) {
        this.form.billStatus = '1'
      } else {
        this.form.billStatus = '0'
      }
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      const now = new Date()
      this.form = {
        billId: null,
        billNo: null,
        ownerId: null,
        ownerName: null,
        roomId: null,
        communityId: this.getCurrentPropertyId(),
        itemId: null,
        itemName: null,
        billYear: now.getFullYear(),
        billMonth: now.getMonth() + 1,
        receivableAmount: null,
        receivedAmount: 0,
        unpaidAmount: null,
        dueDate: null,
        billStatus: '0',
        remark: null
      }
      this.resetForm('form')
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
      this.ids = selection.map((item) => item.billId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.loadOwners()
      this.loadRooms()
      this.loadFeeItems()
      this.open = true
      this.title = '新增费用账单'
    },
    handleUpdate(row) {
      this.reset()
      const billId = row.billId || this.ids[0]
      getFeeBill(billId).then((response) => {
        this.form = response.data
        this.syncAmounts()
        this.loadOwners()
        this.loadRooms()
        this.loadFeeItems()
        this.open = true
        this.title = '修改费用账单'
      })
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          return
        }
        this.form.communityId = this.getCurrentPropertyId()
        this.syncAmounts()
        const action = this.form.billId ? updateFeeBill : addFeeBill
        const message = this.form.billId ? '修改成功' : '新增成功'
        action(this.form).then(() => {
          this.msgSuccess(message)
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const billIds = row.billId || this.ids
      this.$confirm(`是否确认删除费用账单编号为 "${billIds}" 的数据项？`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => delFeeBill(billIds)).then(() => {
        this.getList()
        this.msgSuccess('删除成功')
      })
    }
  }
}
</script>
