<template>
  <div class="app-container">
    <el-form ref="queryForm" :model="queryParams" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="项目名称" prop="itemName">
        <el-input v-model="queryParams.itemName" placeholder="请输入项目名称" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="项目类型" prop="itemType">
        <el-select v-model="queryParams.itemType" placeholder="请选择项目类型" clearable size="small">
          <el-option v-for="item in itemTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="收费周期" prop="chargeCycle">
        <el-select v-model="queryParams.chargeCycle" placeholder="请选择收费周期" clearable size="small">
          <el-option v-for="item in chargeCycleOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
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
      <el-col :span="1.5">
        <el-button type="primary" size="small" @click="handleAdd" v-hasPermi="['community:feeItem:add']">
          <el-icon><Plus /></el-icon><span>新增</span>
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" size="small" :disabled="single" @click="handleUpdate" v-hasPermi="['community:feeItem:edit']">
          <el-icon><Edit /></el-icon><span>修改</span>
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" size="small" :disabled="multiple" @click="handleDelete" v-hasPermi="['community:feeItem:remove']">
          <el-icon><Delete /></el-icon><span>删除</span>
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="feeItemList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="50" align="center" />
      <el-table-column label="项目名称" align="center" prop="itemName" min-width="140" />
      <el-table-column label="项目类型" align="center" prop="itemType">
        <template #default="scope">{{ selectStaticLabel(itemTypeOptions, scope.row.itemType) }}</template>
      </el-table-column>
      <el-table-column label="收费标准" align="center" prop="chargeStandard" />
      <el-table-column label="收费单位" align="center" prop="chargeUnit" />
      <el-table-column label="收费周期" align="center" prop="chargeCycle">
        <template #default="scope">{{ selectStaticLabel(chargeCycleOptions, scope.row.chargeCycle) }}</template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">{{ selectStaticLabel(statusOptions, scope.row.status) }}</template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="备注" align="center" prop="remark" min-width="160" show-overflow-tooltip />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
        <template #default="scope">
          <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['community:feeItem:edit']">
            <el-icon><Edit /></el-icon><span>修改</span>
          </el-button>
          <el-button link type="primary" @click="handleDelete(scope.row)" v-hasPermi="['community:feeItem:remove']">
            <el-icon><Delete /></el-icon><span>删除</span>
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="640px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="96px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="项目名称" prop="itemName">
              <el-input v-model="form.itemName" placeholder="请输入项目名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目类型" prop="itemType">
              <el-select v-model="form.itemType" placeholder="请选择项目类型">
                <el-option v-for="item in itemTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收费标准" prop="chargeStandard">
              <el-input-number v-model="form.chargeStandard" :min="0" :precision="2" :step="0.01" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收费单位" prop="chargeUnit">
              <el-input v-model="form.chargeUnit" placeholder="请输入收费单位" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收费周期" prop="chargeCycle">
              <el-select v-model="form.chargeCycle" placeholder="请选择收费周期">
                <el-option v-for="item in chargeCycleOptions" :key="item.value" :label="item.label" :value="item.value" />
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
import { addFeeItem, delFeeItem, getFeeItem, listFeeItem, updateFeeItem } from '@/api/property/feeItem'

export default {
  name: 'FeeItem',
  components: { Delete, Edit, Plus, Refresh, Search },
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      feeItemList: [],
      title: '',
      open: false,
      itemTypeOptions: [
        { label: '物业费', value: 'property' },
        { label: '停车费', value: 'parking' },
        { label: '水费', value: 'water' },
        { label: '电费', value: 'power' },
        { label: '维修费', value: 'repair' },
        { label: '其他', value: 'other' }
      ],
      chargeCycleOptions: [
        { label: '按月', value: 'month' },
        { label: '按季度', value: 'quarter' },
        { label: '按年', value: 'year' },
        { label: '一次性', value: 'once' }
      ],
      statusOptions: [
        { label: '正常', value: '0' },
        { label: '停用', value: '1' }
      ],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        itemName: null,
        itemType: null,
        chargeCycle: null,
        status: null
      },
      form: {},
      rules: {
        itemName: [{ required: true, message: '项目名称不能为空', trigger: 'blur' }],
        itemType: [{ required: true, message: '项目类型不能为空', trigger: 'change' }],
        chargeStandard: [{ required: true, message: '收费标准不能为空', trigger: 'blur' }],
        chargeUnit: [{ required: true, message: '收费单位不能为空', trigger: 'blur' }],
        chargeCycle: [{ required: true, message: '收费周期不能为空', trigger: 'change' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listFeeItem(this.queryParams).then((response) => {
        this.feeItemList = response.rows || []
        this.total = response.total || 0
      }).finally(() => {
        this.loading = false
      })
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
        itemId: null,
        itemName: null,
        itemType: null,
        chargeStandard: null,
        chargeUnit: null,
        chargeCycle: null,
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
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.itemId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增费用项目'
    },
    handleUpdate(row) {
      this.reset()
      const itemId = row.itemId || this.ids[0]
      getFeeItem(itemId).then((response) => {
        this.form = response.data
        this.open = true
        this.title = '修改费用项目'
      })
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          return
        }
        const action = this.form.itemId ? updateFeeItem : addFeeItem
        const message = this.form.itemId ? '修改成功' : '新增成功'
        action(this.form).then(() => {
          this.msgSuccess(message)
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const itemIds = row.itemId || this.ids
      this.$confirm(`是否确认删除费用项目编号为 "${itemIds}" 的数据项？`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => delFeeItem(itemIds)).then(() => {
        this.getList()
        this.msgSuccess('删除成功')
      })
    }
  }
}
</script>
