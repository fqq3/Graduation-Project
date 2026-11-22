<template>
  <div class="app-container">
    <el-form ref="queryForm" :model="queryParams" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="缴费编号" prop="paymentNo">
        <el-input v-model="queryParams.paymentNo" placeholder="请输入缴费编号" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="账单编号" prop="billNo">
        <el-input v-model="queryParams.billNo" placeholder="请输入账单编号" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="业主姓名" prop="ownerName">
        <el-input v-model="queryParams.ownerName" placeholder="请输入业主姓名" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="支付方式" prop="paymentMethod">
        <el-select v-model="queryParams.paymentMethod" placeholder="请选择支付方式" clearable size="small">
          <el-option v-for="item in paymentMethodOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="支付状态" prop="paymentStatus">
        <el-select v-model="queryParams.paymentStatus" placeholder="请选择支付状态" clearable size="small">
          <el-option v-for="item in paymentStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button class="el-button--cyan" size="small" @click="handleQuery"><el-icon><Search /></el-icon><span>搜索</span></el-button>
        <el-button size="small" @click="resetQuery"><el-icon><Refresh /></el-icon><span>重置</span></el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" size="small" @click="handleAdd" v-hasPermi="['community:paymentRecord:add']">
          <el-icon><Plus /></el-icon><span>新增</span>
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" size="small" :disabled="single" @click="handleUpdate" v-hasPermi="['community:paymentRecord:edit']">
          <el-icon><Edit /></el-icon><span>修改</span>
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" size="small" :disabled="multiple" @click="handleDelete" v-hasPermi="['community:paymentRecord:remove']">
          <el-icon><Delete /></el-icon><span>删除</span>
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="paymentRecordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="50" align="center" />
      <el-table-column label="缴费编号" align="center" prop="paymentNo" min-width="150" />
      <el-table-column label="账单编号" align="center" prop="billNo" min-width="150" />
      <el-table-column label="业主姓名" align="center" min-width="120">
        <template #default="scope">{{ scope.row.ownerRealName || scope.row.ownerName }}</template>
      </el-table-column>
      <el-table-column label="费用项目" align="center" prop="itemName" min-width="120" />
      <el-table-column label="支付金额" align="center" prop="paymentAmount" />
      <el-table-column label="支付方式" align="center" prop="paymentMethod">
        <template #default="scope">{{ selectStaticLabel(paymentMethodOptions, scope.row.paymentMethod) }}</template>
      </el-table-column>
      <el-table-column label="支付状态" align="center" prop="paymentStatus">
        <template #default="scope">{{ selectStaticLabel(paymentStatusOptions, scope.row.paymentStatus) }}</template>
      </el-table-column>
      <el-table-column label="支付时间" align="center" prop="paymentTime" width="160" />
      <el-table-column label="收据编号" align="center" prop="receiptNo" min-width="140" />
      <el-table-column label="操作人" align="center" prop="operatorName" min-width="120" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
        <template #default="scope">
          <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['community:paymentRecord:edit']">
            <el-icon><Edit /></el-icon><span>修改</span>
          </el-button>
          <el-button link type="primary" @click="handleDelete(scope.row)" v-hasPermi="['community:paymentRecord:remove']">
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
            <el-form-item label="所属账单" prop="billId">
              <el-select v-model="form.billId" filterable clearable placeholder="请选择账单" @change="handleBillChange">
                <el-option v-for="item in billOptions" :key="item.billId" :label="formatBillOption(item)" :value="item.billId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="缴费编号" prop="paymentNo">
              <el-input v-model="form.paymentNo" placeholder="留空则自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="业主姓名">
              <el-input :model-value="form.ownerName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="对应房屋">
              <el-input :model-value="selectedRoomLabel" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="支付金额" prop="paymentAmount">
              <el-input-number v-model="form.paymentAmount" :min="0" :precision="2" :step="0.01" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="支付方式" prop="paymentMethod">
              <el-select v-model="form.paymentMethod" placeholder="请选择支付方式">
                <el-option v-for="item in paymentMethodOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="支付状态" prop="paymentStatus">
              <el-select v-model="form.paymentStatus" placeholder="请选择支付状态">
                <el-option v-for="item in paymentStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="支付时间" prop="paymentTime">
              <el-date-picker v-model="form.paymentTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择支付时间" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收据编号" prop="receiptNo">
              <el-input v-model="form.receiptNo" placeholder="请输入收据编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作人" prop="operatorName">
              <el-input v-model="form.operatorName" placeholder="请输入操作人" />
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
import { addPaymentRecord, delPaymentRecord, getPaymentRecord, listPaymentRecord, updatePaymentRecord } from '@/api/property/paymentRecord'
import { listFeeBill } from '@/api/property/feeBill'
import { listRoom } from '@/api/property/room'
import { getPropertyId } from '@/utils/auth'

export default {
  name: 'PaymentRecord',
  components: { Delete, Edit, Plus, Refresh, Search },
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      paymentRecordList: [],
      billOptions: [],
      roomOptions: [],
      title: '',
      open: false,
      paymentMethodOptions: [
        { label: '现金', value: 'cash' },
        { label: '微信', value: 'wechat' },
        { label: '支付宝', value: 'alipay' },
        { label: '银行卡', value: 'bank' },
        { label: '转账', value: 'transfer' },
        { label: '其他', value: 'other' }
      ],
      paymentStatusOptions: [
        { label: '待确认', value: '0' },
        { label: '已支付', value: '1' },
        { label: '已作废', value: '2' }
      ],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        communityId: null,
        paymentNo: null,
        billNo: null,
        ownerName: null,
        paymentMethod: null,
        paymentStatus: null
      },
      form: {},
      rules: {
        billId: [{ required: true, message: '所属账单不能为空', trigger: 'change' }],
        paymentAmount: [{ required: true, message: '支付金额不能为空', trigger: 'blur' }],
        paymentMethod: [{ required: true, message: '支付方式不能为空', trigger: 'change' }],
        paymentStatus: [{ required: true, message: '支付状态不能为空', trigger: 'change' }],
        paymentTime: [{ required: true, message: '支付时间不能为空', trigger: 'change' }]
      }
    }
  },
  computed: {
    selectedRoomLabel() {
      const room = (this.roomOptions || []).find((item) => String(item.roomId) === String(this.form.roomId))
      return room ? [room.communityName, room.buildingName, room.unitName, room.roomName].filter(Boolean).join(' / ') : ''
    }
  },
  created() {
    this.syncCommunityId()
    this.loadBills()
    this.loadRooms()
    this.getList()
  },
  watch: {
    '$route.query.communityId'(newValue, oldValue) {
      if (newValue === oldValue) {
        return
      }
      this.queryParams.pageNum = 1
      this.syncCommunityId()
      this.loadBills()
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
    loadBills() {
      listFeeBill({ pageNum: 1, pageSize: 1000, communityId: this.getCurrentPropertyId() }).then((response) => {
        this.billOptions = response.rows || []
      })
    },
    loadRooms() {
      listRoom({ pageNum: 1, pageSize: 1000, communityId: this.getCurrentPropertyId() }).then((response) => {
        this.roomOptions = response.rows || []
      })
    },
    getList() {
      this.loading = true
      this.queryParams.communityId = this.getCurrentPropertyId()
      listPaymentRecord(this.queryParams).then((response) => {
        this.paymentRecordList = response.rows || []
        this.total = response.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    selectStaticLabel(options, value) {
      const target = (options || []).find((item) => item.value === value)
      return target ? target.label : value
    },
    formatBillOption(item) {
      const ownerName = item.ownerRealName || item.ownerName || '未知业主'
      return `${item.billNo} / ${ownerName} / 未收 ${item.unpaidAmount || 0}`
    },
    handleBillChange(billId) {
      const bill = (this.billOptions || []).find((item) => String(item.billId) === String(billId))
      if (!bill) {
        return
      }
      this.form.ownerId = bill.ownerId
      this.form.ownerName = bill.ownerRealName || bill.ownerName
      this.form.roomId = bill.roomId
      if (this.form.paymentAmount == null || Number(this.form.paymentAmount) === 0) {
        this.form.paymentAmount = bill.unpaidAmount || bill.receivableAmount || 0
      }
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        paymentId: null,
        paymentNo: null,
        billId: null,
        billNo: null,
        ownerId: null,
        ownerName: null,
        roomId: null,
        communityId: this.getCurrentPropertyId(),
        paymentAmount: null,
        paymentMethod: 'wechat',
        paymentStatus: '1',
        paymentTime: this.parseTime(new Date(), '{y}-{m}-{d} {h}:{i}:{s}'),
        operatorName: null,
        receiptNo: null,
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
      this.ids = selection.map((item) => item.paymentId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.loadBills()
      this.loadRooms()
      this.open = true
      this.title = '新增缴费记录'
    },
    handleUpdate(row) {
      this.reset()
      const paymentId = row.paymentId || this.ids[0]
      getPaymentRecord(paymentId).then((response) => {
        this.form = response.data
        this.loadBills()
        this.loadRooms()
        this.open = true
        this.title = '修改缴费记录'
      })
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          return
        }
        this.form.communityId = this.getCurrentPropertyId()
        const bill = (this.billOptions || []).find((item) => String(item.billId) === String(this.form.billId))
        if (bill) {
          this.form.ownerId = bill.ownerId
          this.form.ownerName = bill.ownerRealName || bill.ownerName
          this.form.roomId = bill.roomId
        }
        const action = this.form.paymentId ? updatePaymentRecord : addPaymentRecord
        const message = this.form.paymentId ? '修改成功' : '新增成功'
        action(this.form).then(() => {
          this.msgSuccess(message)
          this.open = false
          this.getList()
          this.loadBills()
        })
      })
    },
    handleDelete(row) {
      const paymentIds = row.paymentId || this.ids
      this.$confirm(`是否确认删除缴费记录编号为 "${paymentIds}" 的数据项？`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => delPaymentRecord(paymentIds)).then(() => {
        this.getList()
        this.loadBills()
        this.msgSuccess('删除成功')
      })
    }
  }
}
</script>
