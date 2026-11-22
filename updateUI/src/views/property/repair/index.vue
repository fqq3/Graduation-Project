<template>
  <div class="app-container">
    <el-form ref="queryForm" :model="queryParams" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="报修单号" prop="repairNo">
        <el-input v-model="queryParams.repairNo" placeholder="请输入报修单号" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="业主姓名" prop="ownerName">
        <el-input v-model="queryParams.ownerName" placeholder="请输入业主姓名" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="报修标题" prop="repairTitle">
        <el-input v-model="queryParams.repairTitle" placeholder="请输入报修标题" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="审核状态" prop="auditStatus">
        <el-select v-model="queryParams.auditStatus" placeholder="请选择审核状态" clearable size="small">
          <el-option v-for="item in auditStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button class="el-button--cyan" size="small" @click="handleQuery"><el-icon><Search /></el-icon><span>搜索</span></el-button>
        <el-button size="small" @click="resetQuery"><el-icon><Refresh /></el-icon><span>重置</span></el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" size="small" @click="handleAdd" v-hasPermi="['community:repairOrder:add']"><el-icon><Plus /></el-icon><span>新增</span></el-button></el-col>
      <el-col :span="1.5"><el-button type="success" size="small" :disabled="single" @click="handleUpdate" v-hasPermi="['community:repairOrder:edit']"><el-icon><Edit /></el-icon><span>修改</span></el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" size="small" :disabled="multiple" @click="handleDelete" v-hasPermi="['community:repairOrder:remove']"><el-icon><Delete /></el-icon><span>删除</span></el-button></el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="repairList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="50" align="center" />
      <el-table-column label="报修单号" align="center" prop="repairNo" />
      <el-table-column label="业主姓名" align="center">
        <template #default="scope">{{ scope.row.ownerRealName || scope.row.ownerName }}</template>
      </el-table-column>
      <el-table-column label="房屋" align="center" prop="roomName" />
      <el-table-column label="报修标题" align="center" prop="repairTitle" />
      <el-table-column label="报修类型" align="center" prop="repairType" />
      <el-table-column label="联系电话" align="center" prop="contactPhone" />
      <el-table-column label="审核状态" align="center" prop="auditStatus" :formatter="auditStatusFormat" />
      <el-table-column label="处理状态" align="center" prop="status" :formatter="statusFormat" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button size="small" type="primary" link @click="handleUpdate(scope.row)" v-hasPermi="['community:repairOrder:edit']"><el-icon><Edit /></el-icon><span>修改</span></el-button>
          <el-button size="small" type="primary" link @click="handleDelete(scope.row)" v-hasPermi="['community:repairOrder:remove']"><el-icon><Delete /></el-icon><span>删除</span></el-button>
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
                <el-option v-for="item in roomOptions" :key="item.roomId" :label="item.buildingName + '-' + item.unitName + '-' + item.roomName" :value="item.roomId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="报修单号" prop="repairNo"><el-input v-model="form.repairNo" placeholder="留空则自动生成" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="报修标题" prop="repairTitle"><el-input v-model="form.repairTitle" placeholder="请输入报修标题" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="报修类型" prop="repairType"><el-input v-model="form.repairType" placeholder="请输入报修类型" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="报修位置" prop="repairLocation"><el-input v-model="form.repairLocation" placeholder="请输入报修位置" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="联系人" prop="contactName"><el-input v-model="form.contactName" placeholder="请输入联系人" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="联系电话" prop="contactPhone"><el-input v-model="form.contactPhone" placeholder="请输入联系电话" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="报修内容" prop="repairContent"><el-input v-model="form.repairContent" type="textarea" placeholder="请输入报修内容" /></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="审核状态" prop="auditStatus">
              <el-select v-model="form.auditStatus" placeholder="请选择审核状态">
                <el-option v-for="item in auditStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="处理状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择处理状态">
                <el-option v-for="item in repairStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="审核意见" prop="auditOpinion"><el-input v-model="form.auditOpinion" placeholder="请输入审核意见" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="维修人员" prop="workerName"><el-input v-model="form.workerName" placeholder="请输入维修人员" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="开始维修" prop="repairStartTime"><el-date-picker v-model="form.repairStartTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择时间" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="完成维修" prop="repairFinishTime"><el-date-picker v-model="form.repairFinishTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择时间" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="维修结果" prop="repairResult"><el-input v-model="form.repairResult" placeholder="请输入维修结果" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="维修费用" prop="repairCost"><el-input v-model="form.repairCost" placeholder="请输入维修费用" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="满意度" prop="satisfaction"><el-input v-model="form.satisfaction" placeholder="请输入满意度" /></el-form-item></el-col>
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
import { addRepair, delRepair, getRepair, listRepair, updateRepair } from '@/api/property/repair'
import { listRoom } from '@/api/property/room'
import { getPropertyId } from '@/utils/auth'

export default {
  name: 'Repair',
  components: { Delete, Edit, Plus, Refresh, Search },
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      repairList: [],
      ownerOptions: [],
      roomOptions: [],
      title: '',
      open: false,
      auditStatusOptions: [{ label: '待审核', value: '0' }, { label: '审核通过', value: '1' }, { label: '审核拒绝', value: '2' }],
      repairStatusOptions: [
        { label: '待审核', value: '0' },
        { label: '待处理', value: '1' },
        { label: '处理中', value: '2' },
        { label: '已完成', value: '3' },
        { label: '已驳回', value: '4' }
      ],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        communityId: null,
        repairNo: null,
        ownerName: null,
        repairTitle: null,
        auditStatus: null
      },
      form: {},
      rules: {
        ownerId: [{ required: true, message: '所属业主不能为空', trigger: 'change' }],
        roomId: [{ required: true, message: '所属房屋不能为空', trigger: 'change' }],
        repairTitle: [{ required: true, message: '报修标题不能为空', trigger: 'blur' }],
        repairContent: [{ required: true, message: '报修内容不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.syncCommunityId()
    this.loadOwners()
    this.loadRooms()
    this.getList()
  },
  watch: {
    '$route.query.communityId'(newValue, oldValue) {
      if (String(newValue || '') === String(oldValue || '')) {
        return
      }
      this.syncCommunityId()
      this.loadOwners()
      this.loadRooms()
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
      listRepair(this.queryParams).then((response) => {
        this.repairList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      })
    },
    auditStatusFormat(row) {
      return this.selectStaticLabel(this.auditStatusOptions, row.auditStatus)
    },
    statusFormat(row) {
      return this.selectStaticLabel(this.repairStatusOptions, row.status)
    },
    selectStaticLabel(options, value) {
      const target = (options || []).find((item) => item.value === value)
      return target ? target.label : value
    },
    handleOwnerChange(ownerId) {
      const owner = (this.ownerOptions || []).find((item) => String(item.ownerId) === String(ownerId))
      if (owner) {
        this.form.ownerName = owner.ownerRealName || owner.ownerNickname
        if (!this.form.contactName) {
          this.form.contactName = owner.ownerRealName || owner.ownerNickname
        }
        if (!this.form.contactPhone) {
          this.form.contactPhone = owner.ownerPhoneNumber
        }
      }
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        repairId: null,
        repairNo: null,
        ownerId: null,
        ownerName: null,
        roomId: null,
        communityId: this.getCurrentPropertyId(),
        repairTitle: null,
        repairType: null,
        repairLocation: null,
        repairContent: null,
        contactName: null,
        contactPhone: null,
        repairImages: null,
        auditStatus: '0',
        auditOpinion: null,
        auditBy: null,
        auditTime: null,
        workerName: null,
        repairStartTime: null,
        repairFinishTime: null,
        repairResult: null,
        repairCost: null,
        satisfaction: null,
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
      this.ids = selection.map((item) => item.repairId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.loadOwners()
      this.loadRooms()
      this.open = true
      this.title = '新增报修'
    },
    handleUpdate(row) {
      this.reset()
      const repairId = row.repairId || this.ids[0]
      getRepair(repairId).then((response) => {
        this.form = response.data
        this.loadOwners()
        this.loadRooms()
        this.open = true
        this.title = '修改报修'
      })
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          return
        }
        this.form.communityId = this.getCurrentPropertyId()
        const action = this.form.repairId ? updateRepair : addRepair
        const message = this.form.repairId ? '修改成功' : '新增成功'
        action(this.form).then(() => {
          this.msgSuccess(message)
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const repairIds = row.repairId || this.ids
      this.$confirm('是否确认删除报修编号为"' + repairIds + '"的数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => delRepair(repairIds)).then(() => {
        this.getList()
        this.msgSuccess('删除成功')
      })
    }
  }
}
</script>
