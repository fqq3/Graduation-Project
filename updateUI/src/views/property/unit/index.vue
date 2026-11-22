<template>
  <div class="app-container">
    <el-form ref="queryForm" :model="queryParams" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="单元名称" prop="unitName">
        <el-input
          v-model="queryParams.unitName"
          placeholder="请输入单元名称"
          clearable
          size="small"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="单元编号" prop="unitCode">
        <el-input
          v-model="queryParams.unitCode"
          placeholder="请输入单元编号"
          clearable
          size="small"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否有电梯" prop="unitHaveElevator" label-width="120px">
        <el-select v-model="queryParams.unitHaveElevator" placeholder="请选择是否有电梯" clearable size="small">
          <el-option v-for="dict in statusOptions" :key="dict.dictValue" :label="dict.dictLabel" :value="dict.dictValue" />
        </el-select>
      </el-form-item>
      <el-form-item label="所属楼栋" prop="buildingId">
        <el-select v-model="queryParams.buildingId" placeholder="所属楼栋" clearable size="small">
          <el-option v-for="item in options" :key="item.buildingId" :label="item.buildingName" :value="item.buildingId" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button class="el-button--cyan" size="small" @click="handleQuery">
          <el-icon><Search /></el-icon>
          <span>搜索</span>
        </el-button>
        <el-button size="small" @click="resetQuery">
          <el-icon><Refresh /></el-icon>
          <span>重置</span>
        </el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" size="small" @click="handleAdd" v-hasPermi="['system:unit:add']">
          <el-icon><Plus /></el-icon>
          <span>新增</span>
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" size="small" :disabled="single" @click="handleUpdate" v-hasPermi="['system:unit:edit']">
          <el-icon><Edit /></el-icon>
          <span>修改</span>
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" size="small" :disabled="multiple" @click="handleDelete" v-hasPermi="['system:unit:remove']">
          <el-icon><Delete /></el-icon>
          <span>删除</span>
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" size="small" @click="handleExport" v-hasPermi="['system:unit:export']">
          <el-icon><Download /></el-icon>
          <span>导出</span>
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="unitList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="50" align="center" />
      <el-table-column label="单元ID" v-if="show" align="center" prop="unitId" />
      <el-table-column label="小区名称" align="center" prop="communityName" />
      <el-table-column label="楼栋" align="center" prop="buildingName" />
      <el-table-column label="单元名称" align="center" prop="unitName" />
      <el-table-column label="层数" align="center" prop="unitLevel" />
      <el-table-column label="建筑面积" align="center" prop="unitAcreage" />
      <el-table-column label="是否有电梯" align="center" prop="unitHaveElevator" :formatter="statusFormat" />
      <el-table-column label="创建时间" align="center" prop="createTime" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button size="small" type="primary" link @click="handleUpdate(scope.row)" v-hasPermi="['system:unit:edit']">
            <el-icon><Edit /></el-icon>
            <span>修改</span>
          </el-button>
          <el-button size="small" type="primary" link @click="handleDelete(scope.row)" v-hasPermi="['system:unit:remove']">
            <el-icon><Delete /></el-icon>
            <span>删除</span>
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="楼栋" prop="buildingId">
          <el-select v-model="form.buildingId" filterable placeholder="请选择楼栋" class="avatar-container right-menu-item hover-effect">
            <el-option v-for="item in options" :key="item.buildingId" :label="item.buildingName" :value="item.buildingId" />
          </el-select>
        </el-form-item>
        <el-form-item label="单元名称" prop="unitName">
          <el-input v-model="form.unitName" placeholder="请输入单元名称" />
        </el-form-item>
        <el-form-item label="单元编号" prop="unitCode">
          <el-input v-model="form.unitCode" placeholder="请输入单元编号" />
        </el-form-item>
        <el-form-item label="层数" prop="unitLevel">
          <el-input v-model="form.unitLevel" placeholder="请输入层数" />
        </el-form-item>
        <el-form-item label="建筑面积" prop="unitAcreage">
          <el-input v-model="form.unitAcreage" placeholder="请输入建筑面积" />
        </el-form-item>
        <el-form-item label="是否有电梯" label-width="82px">
          <el-radio-group v-model="form.unitHaveElevator">
            <el-radio v-for="dict in statusOptions" :key="dict.dictValue" :label="dict.dictValue">
              {{ dict.dictLabel }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
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
import { listUnit, getUnit, delUnit, addUnit, updateUnit, exportUnit } from '@/api/property/unit'
import { queryPullDown } from '@/api/property/building'
import { getPropertyId } from '@/utils/auth'

export default {
  name: 'Unit',
  components: { Delete, Download, Edit, Plus, Refresh, Search },
  data() {
    return {
      show: false,
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      unitList: [],
      title: '',
      open: false,
      statusOptions: [],
      options: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        unitId: null,
        communityId: null,
        buildingId: null,
        unitName: null,
        unitCode: null,
        unitLevel: null,
        unitAcreage: null,
        unitHaveElevator: null
      },
      form: {},
      rules: {
        buildingId: [{ required: true, message: '请选择楼栋', trigger: 'change' }],
        unitLevel: [
          { required: true, message: '层数不能为空', trigger: 'blur' },
          { pattern: /^[1-9]\d*$/, message: '请输入正确的整数格式', trigger: 'change' }
        ],
        unitHaveElevator: [{ required: true, message: '是否有电梯不能为空', trigger: 'change' }]
      }
    }
  },
  created() {
    this.syncCommunityId()
    this.getList()
    this.getDicts('sys_yes_no').then((response) => {
      this.statusOptions = response.data || []
    })
    this.loadBuildingOptions()
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
    loadBuildingOptions() {
      queryPullDown({ communityId: this.getCurrentPropertyId() }).then((response) => {
        this.options = response.data || []
      })
    },
    resolveCommunityIdByBuilding(buildingId) {
      const current = this.options.find((item) => String(item.buildingId) === String(buildingId))
      return current?.communityId || null
    },
    statusFormat(row) {
      return this.selectDictLabel(this.statusOptions, row.unitHaveElevator)
    },
    getList() {
      this.loading = true
      this.queryParams.communityId = this.getCurrentPropertyId()
      listUnit(this.queryParams).then((response) => {
        this.unitList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        unitId: null,
        communityId: this.getCurrentPropertyId(),
        buildingId: null,
        unitName: null,
        unitCode: null,
        unitLevel: null,
        unitAcreage: null,
        unitHaveElevator: '0',
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
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
      this.queryParams.buildingId = null
      this.queryParams.pageNum = 1
      this.getList()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.unitId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.loadBuildingOptions()
      this.open = true
      this.title = '添加单元'
    },
    handleUpdate(row) {
      this.reset()
      const unitId = row.unitId || this.ids[0]
      getUnit(unitId).then((response) => {
        this.form = response.data || {}
        this.open = true
        this.title = '修改单元'
      })
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          return
        }
        this.form.communityId = this.getCurrentPropertyId() || this.resolveCommunityIdByBuilding(this.form.buildingId) || this.form.communityId
        const action = this.form.unitId != null ? updateUnit : addUnit
        const successMessage = this.form.unitId != null ? '修改成功' : '新增成功'
        action(this.form).then(() => {
          this.msgSuccess(successMessage)
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const unitIds = row.unitId || this.ids
      this.$confirm('是否确认删除单元编号为"' + unitIds + '"的数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => delUnit(unitIds))
        .then(() => {
          this.getList()
          this.msgSuccess('删除成功')
        })
    },
    handleExport() {
      const queryParams = { ...this.queryParams }
      this.$confirm('是否确认导出所有单元数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => exportUnit(queryParams))
        .then((response) => {
          this.download(response.msg)
        })
    }
  }
}
</script>
