<template>
  <div class="app-container">
    <el-form ref="queryForm" :model="queryParams" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="小区名称" prop="communityName">
        <el-input v-model="queryParams.communityName" placeholder="请输入小区名称" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="小区编码" prop="communityCode">
        <el-input v-model="queryParams.communityCode" placeholder="请输入小区编码" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
          <el-button class="el-button--cyan" size="small" @click="handleQuery"><el-icon><Search /></el-icon><span>搜索</span></el-button>
        <el-button size="small" @click="resetQuery"><el-icon><Refresh /></el-icon><span>重置</span></el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" size="small" @click="handleAdd" v-hasPermi="['system:community:add']"><el-icon><Plus /></el-icon><span>新增</span></el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" size="small" :disabled="single" @click="handleUpdate" v-hasPermi="['system:community:edit']"><el-icon><Edit /></el-icon><span>修改</span></el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" size="small" :disabled="multiple" @click="handleDelete" v-hasPermi="['system:community:remove']"><el-icon><Delete /></el-icon><span>删除</span></el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" size="small" @click="handleExport"><el-icon><Download /></el-icon><span>导出</span></el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="communityList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="50" align="center" />
      <el-table-column v-if="show" label="ID" align="center" prop="communityId" />
      <el-table-column label="小区名称" align="center" prop="communityName" />
      <el-table-column label="小区编码" align="center" prop="communityCode" />
      <el-table-column label="省" align="center" prop="communityProvinceName" />
      <el-table-column label="市" align="center" prop="communityCityName" />
      <el-table-column label="区/县" align="center" prop="communityTownName" />
      <el-table-column label="创建时间" align="center" prop="createTime" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button size="small" type="primary" link @click="handleUpdate(scope.row)" v-hasPermi="['system:community:edit']"><el-icon><Edit /></el-icon><span>修改</span></el-button>
          <el-button size="small" type="primary" link @click="handleDelete(scope.row)" v-hasPermi="['system:community:remove']"><el-icon><Delete /></el-icon><span>删除</span></el-button>
          <el-button size="small" type="primary" link @click="replaceProperty(scope.row)" v-hasPermi="['system:community:remove']"><el-icon class="legacy-el-icon el-icon-setting"><Setting /></el-icon><span>更换物业</span></el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="property" width="700px" append-to-body>
      <el-table v-loading="loading" :data="deptList" row-key="deptId" default-expand-all :tree-props="{ children: 'children', hasChildren: 'hasChildren' }">
        <el-table-column prop="deptName" label="物业名称" width="260" />
        <el-table-column prop="status" label="状态" :formatter="statusFormat" width="100" />
        <el-table-column label="创建时间" align="center" prop="createTime" width="200">
          <template #default="scope"><span>{{ parseTime(scope.row.createTime) }}</span></template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button v-if="scope.row.parentId === 100 && scope.row.deptId !== selectedDeptId" size="small" type="primary" link @click="replacePropertyAction(scope.row)" v-hasPermi="['system:dept:add']"><el-icon class="legacy-el-icon el-icon-success"><SuccessFilled /></el-icon><span>选择</span></el-button>
            <el-button v-else-if="scope.row.deptId === selectedDeptId" size="small" type="primary" link><el-icon class="legacy-el-icon el-icon-success"><SuccessFilled /></el-icon><span>已选择</span></el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="小区名称" prop="communityName">
          <el-input v-model="form.communityName" placeholder="请输入小区名称" />
        </el-form-item>
        <el-form-item label="详细地址" prop="communityDetailedAddress">
          <el-input v-model="form.communityDetailedAddress" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="经度" prop="communityLongitude">
          <el-input v-model="form.communityLongitude" placeholder="请输入小区经度，如 120.25" />
        </el-form-item>
        <el-form-item label="纬度" prop="communityLatitude">
          <el-input v-model="form.communityLatitude" placeholder="请输入小区纬度，如 31.50" />
        </el-form-item>
        <el-form-item label="所属区划" prop="selected">
          <el-cascader v-model="selectedAreaInfo" :props="cascaderProps" :options="areaOptions" @change="selectedAreas" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { Delete, Download, Edit, Plus, Refresh, Search, Setting, SuccessFilled } from '@element-plus/icons-vue'
import {
  listCommunity,
  getCommunity,
  delCommunity,
  addCommunity,
  updateCommunity,
  exportCommunity,
  getAreaTree
} from '@/api/property/community'
import { listDept } from '@/api/system/dept'

export default {
  inject: ['reload'],
  name: 'Community',
  components: { Delete, Download, Edit, Plus, Refresh, Search, Setting, SuccessFilled },
  data() {
    return {
      show: false,
      loading: true,
      ids: [],
      deptList: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      communityList: [],
      selectedAreaInfo: [],
      areaOptions: [],
      cascaderProps: {
        value: 'code',
        label: 'name'
      },
      title: '',
      publicCommunityId: '',
      selectedDeptId: '',
      open: false,
      property: false,
      statusOptions: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        communityName: null,
        communityCode: null,
        communityProvinceCode: null,
        communityCityCode: null,
        communityTownCode: null,
        communityLongitude: null,
        communityLatitude: null,
        deptId: null,
        sort: null
      },
      form: {},
      rules: {
        communityName: [{ required: true, message: '小区名称不能为空', trigger: 'blur' }],
        communityDetailedAddress: [{ required: true, message: '详细地址不能为空', trigger: 'blur' }],
        selected: [{ required: true, message: '请选择区域', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
    this.getDicts('sys_normal_disable').then((response) => {
      this.statusOptions = response.data
    })
  },
  methods: {
    statusFormat(row) {
      return this.selectDictLabel(this.statusOptions, row.status)
    },
    selectedAreas(detail) {
      this.form.selected = detail
      this.selectedAreaInfo = detail
    },
    getList() {
      this.loading = true
      listCommunity(this.queryParams).then((response) => {
        this.communityList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        communityId: null,
        communityName: null,
        communityCode: null,
        communityProvinceCode: null,
        communityCityCode: null,
        communityTownCode: null,
        longitude: null,
        latitude: null,
        deptId: null,
        sort: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        selected: null,
        remark: null,
        communityDetailedAddress: null,
        communityLongitude: null,
        communityLatitude: null
      }
      this.resetForm('form')
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.communityId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.selectedAreaInfo = []
      getAreaTree().then((response) => {
        this.areaOptions = response.data
      })
      this.open = true
      this.title = '添加小区信息'
    },
    handleUpdate(row) {
      this.reset()
      const communityId = row.communityId || this.ids[0]
      getCommunity(communityId).then((response) => {
        this.form = response.data
        this.selectedAreaInfo = [
          parseInt(this.form.communityProvinceCode, 10),
          parseInt(this.form.communityCityCode, 10),
          parseInt(this.form.communityTownCode, 10)
        ]
        this.form.selected = this.selectedAreaInfo
        getAreaTree().then((treeResponse) => {
          this.areaOptions = treeResponse.data
        })
        this.open = true
        this.title = '修改小区信息'
      })
    },
    replaceProperty(row) {
      this.loading = true
      this.selectedDeptId = row.deptId
      this.publicCommunityId = row.communityId
      listDept().then((response) => {
        this.deptList = this.handleTreeAll(response.data, 'deptId')
        this.loading = false
      })
      this.property = true
      this.title = '更换物业'
    },
    replacePropertyAction(row) {
      this.form.communityId = this.publicCommunityId
      this.form.deptId = row.deptId
      this.$confirm('是否确认更换成该物业？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        updateCommunity(this.form).then(() => {
          this.msgSuccess('物业更换成功')
          this.property = false
          this.getList()
        })
      })
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          return
        }
        this.form.communityProvinceCode = this.selectedAreaInfo[0]
        this.form.communityCityCode = this.selectedAreaInfo[1]
        this.form.communityTownCode = this.selectedAreaInfo[2]
        if (this.form.communityId != null) {
          updateCommunity(this.form).then(() => {
            this.msgSuccess('修改成功')
            this.open = false
            this.getList()
            this.reload()
          })
          return
        }
        addCommunity(this.form).then(() => {
          this.msgSuccess('新增成功')
          this.open = false
          this.getList()
          this.reload()
        })
      })
    },
    handleDelete(row) {
      const communityIds = row.communityId || this.ids
      this.$confirm('是否确认删除小区信息编号为"' + communityIds + '"的数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => delCommunity(communityIds))
        .then(() => {
          this.getList()
          this.msgSuccess('删除成功')
        })
    },
    handleExport() {
      const queryParams = this.queryParams
      this.$confirm('是否确认导出所有小区信息数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return exportCommunity(queryParams)
      }).then((response) => {
        this.download(response.msg)
      })
    }
  }
}
</script>
