<template>
  <div class="app-container">
    <el-form ref="queryForm" :model="queryParams" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="车位编号" prop="spaceNo">
        <el-input v-model="queryParams.spaceNo" placeholder="请输入车位编号" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="车位类型" prop="spaceType">
        <el-input v-model="queryParams.spaceType" placeholder="请输入车位类型" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="使用状态" prop="useStatus">
        <el-select v-model="queryParams.useStatus" placeholder="请选择使用状态" clearable size="small">
          <el-option v-for="item in useStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button class="el-button--cyan" size="small" @click="handleQuery"><el-icon><Search /></el-icon><span>搜索</span></el-button>
        <el-button size="small" @click="resetQuery"><el-icon><Refresh /></el-icon><span>重置</span></el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" size="small" @click="handleAdd" v-hasPermi="['community:parkingSpace:add']"><el-icon><Plus /></el-icon><span>新增</span></el-button></el-col>
      <el-col :span="1.5"><el-button type="success" size="small" :disabled="single" @click="handleUpdate" v-hasPermi="['community:parkingSpace:edit']"><el-icon><Edit /></el-icon><span>修改</span></el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" size="small" :disabled="multiple" @click="handleDelete" v-hasPermi="['community:parkingSpace:remove']"><el-icon><Delete /></el-icon><span>删除</span></el-button></el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="parkingSpaceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="50" align="center" />
      <el-table-column label="车位编号" align="center" prop="spaceNo" />
      <el-table-column label="区域" align="center" prop="spaceArea" />
      <el-table-column label="车位类型" align="center" prop="spaceType" />
      <el-table-column label="使用状态" align="center" prop="useStatus" :formatter="useStatusFormat" />
      <el-table-column label="所属业主" align="center" prop="ownerName" />
      <el-table-column label="绑定车辆" align="center" prop="plateNo" />
      <el-table-column label="月租费用" align="center" prop="monthlyFee" />
      <el-table-column label="租期开始" align="center" prop="startDate" width="120" />
      <el-table-column label="租期结束" align="center" prop="endDate" width="120" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button size="small" type="primary" link @click="handleUpdate(scope.row)" v-hasPermi="['community:parkingSpace:edit']"><el-icon><Edit /></el-icon><span>修改</span></el-button>
          <el-button size="small" type="primary" link @click="handleDelete(scope.row)" v-hasPermi="['community:parkingSpace:remove']"><el-icon><Delete /></el-icon><span>删除</span></el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="620px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="96px">
        <el-row>
          <el-col :span="12"><el-form-item label="车位编号" prop="spaceNo"><el-input v-model="form.spaceNo" placeholder="请输入车位编号" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="所在区域" prop="spaceArea"><el-input v-model="form.spaceArea" placeholder="请输入所在区域" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="车位类型" prop="spaceType"><el-input v-model="form.spaceType" placeholder="请输入车位类型" /></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="使用状态" prop="useStatus">
              <el-select v-model="form.useStatus" placeholder="请选择使用状态">
                <el-option v-for="item in useStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属业主" prop="ownerId">
              <el-select v-model="form.ownerId" filterable clearable placeholder="请选择业主">
                <el-option v-for="item in ownerOptions" :key="item.ownerId" :label="(item.ownerRealName || item.ownerNickname) + ' / ' + item.ownerPhoneNumber" :value="item.ownerId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="绑定车辆" prop="vehicleId">
              <el-select v-model="form.vehicleId" filterable clearable placeholder="请选择车辆" @change="handleVehicleChange">
                <el-option v-for="item in vehicleOptions" :key="item.vehicleId" :label="item.plateNo + ' / ' + (item.ownerRealName || item.ownerName || '')" :value="item.vehicleId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="开始日期" prop="startDate"><el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择开始日期" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="结束日期" prop="endDate"><el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择结束日期" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="月租费用" prop="monthlyFee"><el-input v-model="form.monthlyFee" placeholder="请输入月租费用" /></el-form-item></el-col>
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
import { addParkingSpace, delParkingSpace, getParkingSpace, listParkingSpace, updateParkingSpace } from '@/api/property/parkingSpace'
import { listVehicle } from '@/api/property/vehicle'
import { getPropertyId } from '@/utils/auth'

export default {
  name: 'ParkingSpace',
  components: { Delete, Edit, Plus, Refresh, Search },
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      parkingSpaceList: [],
      ownerOptions: [],
      vehicleOptions: [],
      title: '',
      open: false,
      useStatusOptions: [{ label: '空闲', value: '0' }, { label: '占用', value: '1' }],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        communityId: null,
        spaceNo: null,
        spaceType: null,
        useStatus: null
      },
      form: {},
      rules: {
        spaceNo: [{ required: true, message: '车位编号不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.syncCommunityId()
    this.loadOwners()
    this.loadVehicles()
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
    getList() {
      this.loading = true
      this.queryParams.communityId = this.getCurrentPropertyId()
      listParkingSpace(this.queryParams).then((response) => {
        this.parkingSpaceList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      })
    },
    useStatusFormat(row) {
      return this.selectStaticLabel(this.useStatusOptions, row.useStatus)
    },
    selectStaticLabel(options, value) {
      const target = (options || []).find((item) => item.value === value)
      return target ? target.label : value
    },
    handleVehicleChange(vehicleId) {
      const vehicle = (this.vehicleOptions || []).find((item) => String(item.vehicleId) === String(vehicleId))
      if (vehicle && vehicle.ownerId && !this.form.ownerId) {
        this.form.ownerId = vehicle.ownerId
      }
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        spaceId: null,
        communityId: this.getCurrentPropertyId(),
        spaceNo: null,
        spaceArea: null,
        spaceType: null,
        useStatus: '0',
        ownerId: null,
        vehicleId: null,
        startDate: null,
        endDate: null,
        monthlyFee: null,
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
      this.ids = selection.map((item) => item.spaceId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.loadOwners()
      this.loadVehicles()
      this.open = true
      this.title = '新增停车位'
    },
    handleUpdate(row) {
      this.reset()
      const spaceId = row.spaceId || this.ids[0]
      getParkingSpace(spaceId).then((response) => {
        this.form = response.data
        this.loadOwners()
        this.loadVehicles()
        this.open = true
        this.title = '修改停车位'
      })
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          return
        }
        this.form.communityId = this.getCurrentPropertyId()
        const action = this.form.spaceId ? updateParkingSpace : addParkingSpace
        const message = this.form.spaceId ? '修改成功' : '新增成功'
        action(this.form).then(() => {
          this.msgSuccess(message)
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const spaceIds = row.spaceId || this.ids
      this.$confirm('是否确认删除车位编号为"' + spaceIds + '"的数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => delParkingSpace(spaceIds)).then(() => {
        this.getList()
        this.msgSuccess('删除成功')
      })
    }
  }
}
</script>
