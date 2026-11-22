<template>
  <div class="app-container">
    <el-form ref="queryForm" :model="queryParams" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="车牌号码" prop="plateNo">
        <el-input v-model="queryParams.plateNo" placeholder="请输入车牌号码" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="业主姓名" prop="ownerName">
        <el-input v-model="queryParams.ownerName" placeholder="请输入业主姓名" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="车辆类型" prop="vehicleType">
        <el-input v-model="queryParams.vehicleType" placeholder="请输入车辆类型" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable size="small">
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button class="el-button--cyan" size="small" @click="handleQuery"><el-icon><Search /></el-icon><span>搜索</span></el-button>
        <el-button size="small" @click="resetQuery"><el-icon><Refresh /></el-icon><span>重置</span></el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" size="small" @click="handleAdd" v-hasPermi="['community:vehicle:add']"><el-icon><Plus /></el-icon><span>新增</span></el-button></el-col>
      <el-col :span="1.5"><el-button type="success" size="small" :disabled="single" @click="handleUpdate" v-hasPermi="['community:vehicle:edit']"><el-icon><Edit /></el-icon><span>修改</span></el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" size="small" :disabled="multiple" @click="handleDelete" v-hasPermi="['community:vehicle:remove']"><el-icon><Delete /></el-icon><span>删除</span></el-button></el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="vehicleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="50" align="center" />
      <el-table-column label="车牌号码" align="center" prop="plateNo" />
      <el-table-column label="业主姓名" align="center">
        <template #default="scope">{{ scope.row.ownerRealName || scope.row.ownerName }}</template>
      </el-table-column>
      <el-table-column label="联系方式" align="center">
        <template #default="scope">{{ scope.row.ownerPhoneNumber || scope.row.phone }}</template>
      </el-table-column>
      <el-table-column label="车辆类型" align="center" prop="vehicleType" />
      <el-table-column label="品牌" align="center" prop="vehicleBrand" />
      <el-table-column label="颜色" align="center" prop="vehicleColor" />
      <el-table-column label="房屋" align="center" prop="roomName" />
      <el-table-column label="绑定状态" align="center" prop="bindStatus" :formatter="bindStatusFormat" />
      <el-table-column label="状态" align="center" prop="status" :formatter="statusFormat" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button size="small" type="primary" link @click="handleUpdate(scope.row)" v-hasPermi="['community:vehicle:edit']"><el-icon><Edit /></el-icon><span>修改</span></el-button>
          <el-button size="small" type="primary" link @click="handleDelete(scope.row)" v-hasPermi="['community:vehicle:remove']"><el-icon><Delete /></el-icon><span>删除</span></el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="620px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="96px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="所属业主" prop="ownerId">
              <el-select v-model="form.ownerId" filterable placeholder="请选择业主" @change="handleOwnerChange">
                <el-option v-for="item in ownerOptions" :key="item.ownerId" :label="(item.ownerRealName || item.ownerNickname) + ' / ' + item.ownerPhoneNumber" :value="item.ownerId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属房屋" prop="roomId">
              <el-select v-model="form.roomId" filterable placeholder="请选择房屋">
                <el-option v-for="item in roomOptions" :key="item.roomId" :label="item.buildingName + '-' + item.unitName + '-' + item.roomName" :value="item.roomId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="车牌号码" prop="plateNo"><el-input v-model="form.plateNo" placeholder="请输入车牌号码" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="车辆类型" prop="vehicleType"><el-input v-model="form.vehicleType" placeholder="请输入车辆类型" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="车辆品牌" prop="vehicleBrand"><el-input v-model="form.vehicleBrand" placeholder="请输入车辆品牌" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="车辆颜色" prop="vehicleColor"><el-input v-model="form.vehicleColor" placeholder="请输入车辆颜色" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="联系电话" prop="phone"><el-input v-model="form.phone" placeholder="请输入联系电话" /></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="绑定状态" prop="bindStatus">
              <el-select v-model="form.bindStatus" placeholder="请选择绑定状态">
                <el-option v-for="item in bindStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态">
                <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
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
import { listRoom } from '@/api/property/room'
import { addVehicle, delVehicle, getVehicle, listVehicle, updateVehicle } from '@/api/property/vehicle'
import { getPropertyId } from '@/utils/auth'

export default {
  name: 'Vehicle',
  components: { Delete, Edit, Plus, Refresh, Search },
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      vehicleList: [],
      ownerOptions: [],
      roomOptions: [],
      title: '',
      open: false,
      bindStatusOptions: [{ label: '已绑定', value: '0' }, { label: '未绑定', value: '1' }],
      statusOptions: [{ label: '正常', value: '0' }, { label: '停用', value: '1' }],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        communityId: null,
        ownerName: null,
        plateNo: null,
        vehicleType: null,
        status: null
      },
      form: {},
      rules: {
        ownerId: [{ required: true, message: '所属业主不能为空', trigger: 'change' }],
        roomId: [{ required: true, message: '所属房屋不能为空', trigger: 'change' }],
        plateNo: [{ required: true, message: '车牌号码不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.syncCommunityId()
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
    loadRooms() {
      listRoom({ pageNum: 1, pageSize: 1000, communityId: this.getCurrentPropertyId() }).then((response) => {
        this.roomOptions = response.rows || []
      })
    },
    getList() {
      this.loading = true
      this.queryParams.communityId = this.getCurrentPropertyId()
      listVehicle(this.queryParams).then((response) => {
        this.vehicleList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      })
    },
    bindStatusFormat(row) {
      return this.selectStaticLabel(this.bindStatusOptions, row.bindStatus)
    },
    statusFormat(row) {
      return this.selectStaticLabel(this.statusOptions, row.status)
    },
    selectStaticLabel(options, value) {
      const target = (options || []).find((item) => item.value === value)
      return target ? target.label : value
    },
    handleOwnerChange(ownerId) {
      const owner = (this.ownerOptions || []).find((item) => String(item.ownerId) === String(ownerId))
      if (owner) {
        this.form.ownerName = owner.ownerRealName || owner.ownerNickname
        this.form.phone = owner.ownerPhoneNumber
      }
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        vehicleId: null,
        ownerId: null,
        ownerName: null,
        roomId: null,
        communityId: this.getCurrentPropertyId(),
        plateNo: null,
        vehicleType: null,
        vehicleBrand: null,
        vehicleColor: null,
        phone: null,
        bindStatus: '0',
        status: '0',
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
      this.ids = selection.map((item) => item.vehicleId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.loadOwners()
      this.loadRooms()
      this.open = true
      this.title = '新增车辆'
    },
    handleUpdate(row) {
      this.reset()
      const vehicleId = row.vehicleId || this.ids[0]
      getVehicle(vehicleId).then((response) => {
        this.form = response.data
        this.loadOwners()
        this.loadRooms()
        this.open = true
        this.title = '修改车辆'
      })
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          return
        }
        this.form.communityId = this.getCurrentPropertyId()
        const action = this.form.vehicleId ? updateVehicle : addVehicle
        const message = this.form.vehicleId ? '修改成功' : '新增成功'
        action(this.form).then(() => {
          this.msgSuccess(message)
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const vehicleIds = row.vehicleId || this.ids
      this.$confirm('是否确认删除车辆编号为"' + vehicleIds + '"的数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => delVehicle(vehicleIds)).then(() => {
        this.getList()
        this.msgSuccess('删除成功')
      })
    }
  }
}
</script>
