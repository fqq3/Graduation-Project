<template>
  <div class="app-container">
    <el-form ref="queryForm" :model="queryParams" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="楼栋名称" prop="buildingName">
        <el-input
          v-model="queryParams.buildingName"
          placeholder="请输入楼栋名称"
          clearable
          size="small"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="楼栋编码" prop="buildingCode">
        <el-input
          v-model="queryParams.buildingCode"
          placeholder="请输入楼栋编码"
          clearable
          size="small"
          @keyup.enter="handleQuery"
        />
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
        <el-button type="primary" size="small" @click="handleAdd" v-hasPermi="['system:building:add']">
          <el-icon><Plus /></el-icon>
          <span>新增</span>
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" size="small" :disabled="single" @click="handleUpdate" v-hasPermi="['system:building:edit']">
          <el-icon><Edit /></el-icon>
          <span>修改</span>
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" size="small" :disabled="multiple" @click="handleDelete" v-hasPermi="['system:building:remove']">
          <el-icon><Delete /></el-icon>
          <span>删除</span>
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" size="small" @click="handleExport" v-hasPermi="['system:building:export']">
          <el-icon><Download /></el-icon>
          <span>导出</span>
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="buildingList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="50" align="center" />
      <el-table-column v-if="show" label="楼栋ID" align="center" prop="buildingId" />
      <el-table-column label="楼栋名称" align="center" prop="buildingName" />
      <el-table-column label="楼栋编码" align="center" prop="buildingCode" />
      <el-table-column label="楼栋面积" align="center" prop="buildingAcreage" />
      <el-table-column label="所属小区" align="center" prop="communityName" />
      <el-table-column label="创建时间" align="center" prop="createTime" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button size="small" type="primary" link @click="handleUpdate(scope.row)" v-hasPermi="['system:building:edit']">
            <el-icon><Edit /></el-icon>
            <span>修改</span>
          </el-button>
          <el-button size="small" type="primary" link @click="handleDelete(scope.row)" v-hasPermi="['system:building:remove']">
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
        <el-form-item label="楼栋名称" prop="buildingName">
          <el-input v-model="form.buildingName" placeholder="请输入楼栋名称" />
        </el-form-item>
        <el-form-item label="楼栋面积" prop="buildingAcreage">
          <el-input v-model="form.buildingAcreage" placeholder="请输入楼栋面积" />
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
import { getPropertyId } from '@/utils/auth'
import { addBuilding, delBuilding, exportBuilding, getBuilding, listBuilding, updateBuilding } from '@/api/property/building'

export default {
  name: 'Building',
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
      buildingList: [],
      title: '',
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        buildingId: null,
        buildingName: null,
        buildingCode: null,
        buildingAcreage: null,
        communityId: null
      },
      form: {},
      rules: {
        buildingName: [{ required: true, message: '楼栋名称不能为空', trigger: 'blur' }],
        buildingAcreage: [
          {
            pattern: /(^[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)/,
            message: '请输入正确格式，最多保留两位小数',
            trigger: 'change'
          }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getCurrentPropertyId() {
      return getPropertyId()
    },
    // 楼栋页的小区来源统一使用右上角全局小区切换。
    syncCommunityId() {
      this.queryParams.communityId = this.getCurrentPropertyId()
    },
    getList() {
      this.loading = true
      this.syncCommunityId()
      listBuilding(this.queryParams).then((response) => {
        this.buildingList = response.rows || []
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
        buildingId: null,
        buildingName: null,
        buildingCode: null,
        buildingAcreage: null,
        communityId: null,
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
      this.queryParams.pageNum = 1
      this.getList()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.buildingId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      const propertyId = this.getCurrentPropertyId()
      if (!propertyId) {
        this.$message.warning('请先在右上角选择所属小区后再新增')
        return
      }
      this.reset()
      // 新增时直接沿用右上角当前小区，避免页面内重复选择。
      this.form.communityId = propertyId
      this.open = true
      this.title = '添加楼栋'
    },
    handleUpdate(row) {
      this.reset()
      const buildingId = row.buildingId || this.ids[0]
      getBuilding(buildingId).then((response) => {
        this.form = response.data || {}
        this.open = true
        this.title = '修改楼栋'
      })
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          return
        }
        const payload = { ...this.form }
        // 新增时强制绑定到右上角当前小区，编辑时保留原小区。
        if (payload.buildingId == null) {
          payload.communityId = this.getCurrentPropertyId()
        }
        const action = payload.buildingId != null ? updateBuilding : addBuilding
        const successMessage = payload.buildingId != null ? '修改成功' : '新增成功'
        action(payload).then(() => {
          this.msgSuccess(successMessage)
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const buildingIds = row.buildingId || this.ids
      this.$confirm(`是否确认删除楼栋编号为"${buildingIds}"的数据项?`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => delBuilding(buildingIds))
        .then(() => {
          this.getList()
          this.msgSuccess('删除成功')
        })
    },
    handleExport() {
      const queryParams = { ...this.queryParams }
      // 导出时沿用右上角当前小区，避免导出跨小区数据。
      queryParams.communityId = this.getCurrentPropertyId()
      this.$confirm('是否确认导出所有楼栋数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => exportBuilding(queryParams))
        .then((response) => {
          this.download(response.msg)
        })
    }
  }
}
</script>
