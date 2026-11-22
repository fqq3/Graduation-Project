<template>
  <div class="app-container">
    <el-form ref="queryForm" :model="queryParams" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="车牌号码" prop="plateNo">
        <el-input v-model="queryParams.plateNo" placeholder="请输入车牌号码" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="缴费状态" prop="payStatus">
        <el-select v-model="queryParams.payStatus" placeholder="请选择缴费状态" clearable size="small">
          <el-option v-for="item in payStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="记录类型" prop="recordType">
        <el-select v-model="queryParams.recordType" placeholder="请选择记录类型" clearable size="small">
          <el-option v-for="item in recordTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button class="el-button--cyan" size="small" @click="handleQuery"><el-icon><Search /></el-icon><span>搜索</span></el-button>
        <el-button size="small" @click="resetQuery"><el-icon><Refresh /></el-icon><span>重置</span></el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" size="small" @click="handleAdd" v-hasPermi="['community:parkingRecord:add']"><el-icon><Plus /></el-icon><span>新增</span></el-button></el-col>
      <el-col :span="1.5"><el-button type="success" size="small" :disabled="single" @click="handleUpdate" v-hasPermi="['community:parkingRecord:edit']"><el-icon><Edit /></el-icon><span>修改</span></el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" size="small" :disabled="multiple" @click="handleDelete" v-hasPermi="['community:parkingRecord:remove']"><el-icon><Delete /></el-icon><span>删除</span></el-button></el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="parkingRecordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="50" align="center" />
      <el-table-column label="车牌号码" align="center" prop="plateNo" />
      <el-table-column label="车位编号" align="center" prop="spaceNo" />
      <el-table-column label="所属业主" align="center" prop="ownerName" />
      <el-table-column label="入场时间" align="center" prop="inTime" width="160" />
      <el-table-column label="出场时间" align="center" prop="outTime" width="160" />
      <el-table-column label="停车时长(分钟)" align="center" prop="parkingDuration" width="120" />
      <el-table-column label="停车费用" align="center" prop="parkingFee" />
      <el-table-column label="缴费状态" align="center" prop="payStatus" :formatter="payStatusFormat" />
      <el-table-column label="记录类型" align="center" prop="recordType" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button size="small" type="primary" link @click="handleUpdate(scope.row)" v-hasPermi="['community:parkingRecord:edit']"><el-icon><Edit /></el-icon><span>修改</span></el-button>
          <el-button size="small" type="primary" link @click="handleDelete(scope.row)" v-hasPermi="['community:parkingRecord:remove']"><el-icon><Delete /></el-icon><span>删除</span></el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="680px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="108px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="车辆信息" prop="vehicleId">
              <el-select v-model="form.vehicleId" filterable clearable placeholder="请选择车辆" :disabled="form.recordType === '临停车'" @change="handleVehicleChange">
                <el-option v-for="item in vehicleOptions" :key="item.vehicleId" :label="item.plateNo + ' / ' + (item.ownerRealName || item.ownerName || '')" :value="item.vehicleId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="车位信息" prop="spaceId">
              <el-select v-model="form.spaceId" filterable clearable placeholder="请选择车位">
                <el-option v-for="item in parkingSpaceOptions" :key="item.spaceId" :label="item.spaceNo + ' / ' + (item.spaceArea || '')" :value="item.spaceId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属业主" prop="ownerId">
              <el-select v-model="form.ownerId" filterable clearable placeholder="请选择业主" :disabled="form.recordType === '临停车'">
                <el-option v-for="item in ownerOptions" :key="item.ownerId" :label="(item.ownerRealName || item.ownerNickname) + ' / ' + item.ownerPhoneNumber" :value="item.ownerId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="车牌号码" prop="plateNo"><el-input v-model="form.plateNo" placeholder="请输入车牌号码" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="入场时间" prop="inTime"><el-date-picker v-model="form.inTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择入场时间" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="出场时间" prop="outTime"><el-date-picker v-model="form.outTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择出场时间" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="停车时长" prop="parkingDuration"><el-input v-model="form.parkingDuration" placeholder="自动计算或手动填写" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="停车费用" prop="parkingFee"><el-input v-model="form.parkingFee" placeholder="请输入停车费用" /></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="缴费状态" prop="payStatus">
              <el-select v-model="form.payStatus" placeholder="请选择缴费状态">
                <el-option v-for="item in payStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="记录类型" prop="recordType">
              <el-select v-model="form.recordType" placeholder="请选择记录类型" @change="handleRecordTypeChange">
                <el-option v-for="item in recordTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24"><el-form-item label="备注" prop="remark"><el-input v-model="form.remark" type="textarea" placeholder="请输入备注" /></el-form-item></el-col>
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
import { listParkingSpace } from '@/api/property/parkingSpace'
import { addParkingRecord, delParkingRecord, getParkingRecord, listParkingRecord, updateParkingRecord } from '@/api/property/parkingRecord'
import { listVehicle } from '@/api/property/vehicle'
import { getPropertyId } from '@/utils/auth'

export default {
  name: 'ParkingRecord',
  components: { Delete, Edit, Plus, Refresh, Search },
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      parkingRecordList: [],
      ownerOptions: [],
      vehicleOptions: [],
      parkingSpaceOptions: [],
      title: '',
      open: false,
      payStatusOptions: [{ label: '未支付', value: '0' }, { label: '已支付', value: '1' }],
      recordTypeOptions: [{ label: '固定车', value: '固定车' }, { label: '临停车', value: '临停车' }],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        communityId: null,
        plateNo: null,
        payStatus: null,
        recordType: null
      },
      form: {},
      rules: {
        recordType: [{ required: true, message: '记录类型不能为空', trigger: 'change' }],
        vehicleId: [{
          validator: (rule, value, callback) => {
            if (this.form.recordType === '固定车' && !value) {
              callback(new Error('车辆信息不能为空'))
              return
            }
            callback()
          },
          trigger: 'change'
        }],
        plateNo: [{
          validator: (rule, value, callback) => {
            if (this.form.recordType === '临停车' && !value) {
              callback(new Error('车牌号码不能为空'))
              return
            }
            callback()
          },
          trigger: 'blur'
        }],
        inTime: [{ required: true, message: '入场时间不能为空', trigger: 'change' }]
      }
    }
  },
  created() {
    this.syncCommunityId()
    this.loadOwners()
    this.loadVehicles()
    this.loadParkingSpaces()
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
      listOwner({ pageNum: 1, pageSize: 1000 }).then((response) => {
        const propertyId = this.getCurrentPropertyId()
        const rows = response.rows || []
        this.ownerOptions = propertyId ? rows.filter((item) => String(item.communityId) === String(propertyId)) : rows
      })
    },
    loadVehicles() {
      listVehicle({ pageNum: 1, pageSize: 1000, communityId: this.getCurrentPropertyId() }).then((response) => {
        this.vehicleOptions = response.rows || []
      })
    },
    loadParkingSpaces() {
      listParkingSpace({ pageNum: 1, pageSize: 1000, communityId: this.getCurrentPropertyId() }).then((response) => {
        this.parkingSpaceOptions = response.rows || []
      })
    },
    getList() {
      this.loading = true
      this.queryParams.communityId = this.getCurrentPropertyId()
      listParkingRecord(this.queryParams).then((response) => {
        this.parkingRecordList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      })
    },
    payStatusFormat(row) {
      return this.selectStaticLabel(this.payStatusOptions, row.payStatus)
    },
    selectStaticLabel(options, value) {
      const target = (options || []).find((item) => item.value === value)
      return target ? target.label : value
    },
    handleVehicleChange(vehicleId) {
      const vehicle = (this.vehicleOptions || []).find((item) => String(item.vehicleId) === String(vehicleId))
      if (vehicle) {
        this.form.plateNo = vehicle.plateNo
        if (vehicle.ownerId) {
          this.form.ownerId = vehicle.ownerId
        }
        this.$refs.form?.clearValidate(['vehicleId', 'plateNo'])
        return
      }
      if (!vehicleId && this.form.recordType === '固定车') {
        this.form.ownerId = null
      }
    },
    handleRecordTypeChange(recordType) {
      if (recordType === '临停车') {
        this.form.vehicleId = null
        this.form.ownerId = null
      }
      this.$refs.form?.clearValidate(['recordType', 'vehicleId', 'plateNo', 'spaceId'])
    },
    syncParkingDuration() {
      if (!this.form.inTime || !this.form.outTime) {
        return
      }
      const start = new Date(String(this.form.inTime).replace(/-/g, '/'))
      const end = new Date(String(this.form.outTime).replace(/-/g, '/'))
      const diff = Math.floor((end.getTime() - start.getTime()) / 60000)
      if (!Number.isNaN(diff) && diff >= 0) {
        this.form.parkingDuration = diff
      }
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        recordId: null,
        vehicleId: null,
        spaceId: null,
        ownerId: null,
        communityId: this.getCurrentPropertyId(),
        plateNo: null,
        inTime: null,
        outTime: null,
        parkingDuration: null,
        parkingFee: null,
        payStatus: '0',
        recordType: '固定车',
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
      this.ids = selection.map((item) => item.recordId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.loadOwners()
      this.loadVehicles()
      this.loadParkingSpaces()
      this.open = true
      this.title = '新增停车记录'
    },
    handleUpdate(row) {
      this.reset()
      const recordId = row.recordId || this.ids[0]
      getParkingRecord(recordId).then((response) => {
        this.form = {
          ...this.form,
          ...response.data,
          communityId: response.data?.communityId || this.getCurrentPropertyId(),
          recordType: response.data?.recordType || '固定车'
        }
        this.loadOwners()
        this.loadVehicles()
        this.loadParkingSpaces()
        this.open = true
        this.title = '修改停车记录'
      })
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          return
        }
        this.form.communityId = this.getCurrentPropertyId()
        if (this.form.recordType === '临停车') {
          this.form.vehicleId = null
          this.form.ownerId = null
        }
        this.syncParkingDuration()
        const action = this.form.recordId ? updateParkingRecord : addParkingRecord
        const message = this.form.recordId ? '修改成功' : '新增成功'
        action(this.form).then(() => {
          this.msgSuccess(message)
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const recordIds = row.recordId || this.ids
      this.$confirm('是否确认删除停车记录编号为"' + recordIds + '"的数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => delParkingRecord(recordIds)).then(() => {
        this.getList()
        this.msgSuccess('删除成功')
      })
    }
  }
}
</script>
