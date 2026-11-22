<template>
  <div class="app-container">
    <el-form ref="queryForm" :model="queryParams" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="设备名称" prop="deviceName">
        <el-input v-model="queryParams.deviceName" placeholder="请输入设备名称" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="设备编码" prop="deviceCode">
        <el-input v-model="queryParams.deviceCode" placeholder="请输入设备编码" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="设备类型" prop="deviceType">
        <el-input v-model="queryParams.deviceType" placeholder="请输入设备类型" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="设备状态" prop="deviceStatus">
        <el-select v-model="queryParams.deviceStatus" placeholder="请选择设备状态" clearable size="small">
          <el-option v-for="item in deviceStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button class="el-button--cyan" size="small" @click="handleQuery"><el-icon><Search /></el-icon><span>搜索</span></el-button>
        <el-button size="small" @click="resetQuery"><el-icon><Refresh /></el-icon><span>重置</span></el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" size="small" @click="handleAdd" v-hasPermi="['community:device:add']"><el-icon><Plus /></el-icon><span>新增</span></el-button></el-col>
      <el-col :span="1.5"><el-button type="success" size="small" :disabled="single" @click="handleUpdate" v-hasPermi="['community:device:edit']"><el-icon><Edit /></el-icon><span>修改</span></el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" size="small" :disabled="multiple" @click="handleDelete" v-hasPermi="['community:device:remove']"><el-icon><Delete /></el-icon><span>删除</span></el-button></el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="deviceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="50" align="center" />
      <el-table-column label="设备名称" align="center" prop="deviceName" />
      <el-table-column label="设备编码" align="center" prop="deviceCode" />
      <el-table-column label="设备类型" align="center" prop="deviceType" />
      <el-table-column label="所属楼栋" align="center" prop="buildingName" />
      <el-table-column label="所属单元" align="center" prop="unitName" />
      <el-table-column label="设备状态" align="center" prop="deviceStatus" :formatter="deviceStatusFormat" />
      <el-table-column label="在线状态" align="center" prop="onlineStatus" :formatter="onlineStatusFormat" />
      <el-table-column label="厂家" align="center" prop="manufacturer" />
      <el-table-column label="安装日期" align="center" prop="installDate" width="120" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button size="small" type="primary" link @click="handleUpdate(scope.row)" v-hasPermi="['community:device:edit']"><el-icon><Edit /></el-icon><span>修改</span></el-button>
          <el-button size="small" type="primary" link @click="handleDelete(scope.row)" v-hasPermi="['community:device:remove']"><el-icon><Delete /></el-icon><span>删除</span></el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="620px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="96px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="所属楼栋" prop="buildingId">
              <el-select v-model="form.buildingId" placeholder="请选择楼栋" clearable @change="handleBuildingChange">
                <el-option v-for="item in buildingOptions" :key="item.buildingId" :label="item.buildingName" :value="item.buildingId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属单元" prop="unitId">
              <el-select v-model="form.unitId" placeholder="请选择单元" clearable>
                <el-option v-for="item in unitOptions" :key="item.unitId" :label="item.unitName" :value="item.unitId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="设备名称" prop="deviceName"><el-input v-model="form.deviceName" placeholder="请输入设备名称" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="设备编码" prop="deviceCode"><el-input v-model="form.deviceCode" placeholder="请输入设备编码" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="设备类型" prop="deviceType"><el-input v-model="form.deviceType" placeholder="请输入设备类型" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="设备位置" prop="deviceLocation"><el-input v-model="form.deviceLocation" placeholder="请输入设备位置" /></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="设备状态" prop="deviceStatus">
              <el-select v-model="form.deviceStatus" placeholder="请选择设备状态">
                <el-option v-for="item in deviceStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="在线状态" prop="onlineStatus">
              <el-select v-model="form.onlineStatus" placeholder="请选择在线状态">
                <el-option v-for="item in onlineStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="生产厂家" prop="manufacturer"><el-input v-model="form.manufacturer" placeholder="请输入生产厂家" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="安装日期" prop="installDate"><el-date-picker v-model="form.installDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择安装日期" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="上次维护" prop="lastMaintainTime"><el-date-picker v-model="form.lastMaintainTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择时间" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="下次维护" prop="nextMaintainTime"><el-date-picker v-model="form.nextMaintainTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择时间" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="告警状态" prop="alarmStatus">
              <el-select v-model="form.alarmStatus" placeholder="请选择告警状态">
                <el-option v-for="item in alarmStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24"><el-form-item label="告警内容" prop="alarmContent"><el-input v-model="form.alarmContent" type="textarea" placeholder="请输入告警内容" /></el-form-item></el-col>
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
import { queryPullDownRoom } from '@/api/property/building'
import { addDevice, delDevice, getDevice, listDevice, updateDevice } from '@/api/property/device'
import { getPropertyId } from '@/utils/auth'

export default {
  name: 'Device',
  components: { Delete, Edit, Plus, Refresh, Search },
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      deviceList: [],
      buildingOptions: [],
      unitOptions: [],
      title: '',
      open: false,
      deviceStatusOptions: [{ label: '正常', value: '0' }, { label: '异常', value: '1' }],
      onlineStatusOptions: [{ label: '在线', value: '0' }, { label: '离线', value: '1' }],
      alarmStatusOptions: [{ label: '无告警', value: '0' }, { label: '有告警', value: '1' }],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        communityId: null,
        buildingId: null,
        unitId: null,
        deviceName: null,
        deviceCode: null,
        deviceType: null,
        deviceStatus: null,
        onlineStatus: null,
        alarmStatus: null
      },
      form: {},
      rules: {
        deviceName: [{ required: true, message: '设备名称不能为空', trigger: 'blur' }],
        deviceCode: [{ required: true, message: '设备编码不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.syncCommunityId()
    this.loadBuildingTree()
    this.getList()
  },
  watch: {
    '$route.query.communityId'(newValue, oldValue) {
      if (String(newValue || '') === String(oldValue || '')) {
        return
      }
      this.queryParams.buildingId = null
      this.queryParams.unitId = null
      this.syncCommunityId()
      this.loadBuildingTree()
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
    loadBuildingTree() {
      queryPullDownRoom({ communityId: this.getCurrentPropertyId() }).then((response) => {
        this.buildingOptions = response.data || []
        this.refreshUnitOptions()
      })
    },
    refreshUnitOptions() {
      const target = (this.buildingOptions || []).find((item) => String(item.buildingId) === String(this.form.buildingId))
      this.unitOptions = target && target.children ? target.children : []
    },
    handleBuildingChange() {
      this.form.unitId = null
      this.refreshUnitOptions()
    },
    getList() {
      this.loading = true
      this.queryParams.communityId = this.getCurrentPropertyId()
      listDevice(this.queryParams).then((response) => {
        this.deviceList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      })
    },
    deviceStatusFormat(row) {
      return this.selectStaticLabel(this.deviceStatusOptions, row.deviceStatus)
    },
    onlineStatusFormat(row) {
      return this.selectStaticLabel(this.onlineStatusOptions, row.onlineStatus)
    },
    selectStaticLabel(options, value) {
      const target = (options || []).find((item) => item.value === value)
      return target ? target.label : value
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        deviceId: null,
        communityId: this.getCurrentPropertyId(),
        buildingId: null,
        unitId: null,
        deviceName: null,
        deviceCode: null,
        deviceType: null,
        deviceLocation: null,
        deviceStatus: '0',
        onlineStatus: '0',
        manufacturer: null,
        installDate: null,
        lastMaintainTime: null,
        nextMaintainTime: null,
        alarmStatus: '0',
        alarmContent: null,
        remark: null
      }
      this.unitOptions = []
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
      this.ids = selection.map((item) => item.deviceId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.loadBuildingTree()
      this.open = true
      this.title = '新增设备'
    },
    handleUpdate(row) {
      this.reset()
      const deviceId = row.deviceId || this.ids[0]
      getDevice(deviceId).then((response) => {
        this.form = response.data
        this.loadBuildingTree()
        this.open = true
        this.title = '修改设备'
      })
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          return
        }
        this.form.communityId = this.getCurrentPropertyId()
        const action = this.form.deviceId ? updateDevice : addDevice
        const message = this.form.deviceId ? '修改成功' : '新增成功'
        action(this.form).then(() => {
          this.msgSuccess(message)
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const deviceIds = row.deviceId || this.ids
      this.$confirm('是否确认删除设备编号为"' + deviceIds + '"的数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => delDevice(deviceIds)).then(() => {
        this.getList()
        this.msgSuccess('删除成功')
      })
    }
  }
}
</script>
