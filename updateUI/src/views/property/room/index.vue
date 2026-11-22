<template>
  <div class="app-container">
    <el-form ref="queryForm" :model="queryParams" :inline="true" v-show="showSearch" label-width="92px">
      <el-form-item label="单元/楼栋" prop="selected">
        <el-cascader
          v-model="selectedSearchInfo"
          :props="cascaderProps"
          :options="searchOptions"
          clearable
          @change="selectedUnitBuilding"
        />
      </el-form-item>
      <el-form-item label="房屋状态" prop="roomStatus">
        <el-select v-model="queryParams.roomStatus" placeholder="请选择房屋状态" clearable size="small">
          <el-option
            v-for="dict in roomStatusOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="small" @click="handleQuery">搜索</el-button>
        <el-button size="small" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" size="small" @click="handleAdd" v-hasPermi="['system:room:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" size="small" :disabled="single" @click="handleUpdate" v-hasPermi="['system:room:edit']">
          修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" size="small" :disabled="multiple" @click="handleDelete" v-hasPermi="['system:room:remove']">
          删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" size="small" @click="handleExport" v-hasPermi="['system:room:export']">
          导出
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="roomList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="60" align="center" />
      <el-table-column v-if="show" label="房间ID" align="center" prop="roomId" />
      <el-table-column label="小区名称" align="center" prop="communityName" />
      <el-table-column label="楼栋名称" align="center" prop="buildingName" />
      <el-table-column label="单元名称" align="center" prop="unitName" />
      <el-table-column label="楼层" align="center" prop="roomLevel" />
      <el-table-column label="房间名称" align="center" prop="roomName" />
      <el-table-column label="房间编号" align="center" prop="roomCode" />
      <el-table-column label="房屋建筑面积" align="center" prop="roomAcreage" />
      <el-table-column label="算费系数" align="center" prop="roomCost" />
      <el-table-column label="房屋状态" align="center" prop="roomStatus" :formatter="roomStatusFormat" />
      <el-table-column label="是否商铺" align="center" prop="roomIsShop" :formatter="roomIsShopFormat" />
      <el-table-column label="是否商品房" align="center" prop="roomSCommercialHouse" :formatter="roomSCommercialHouseFormat" />
      <el-table-column label="房屋户型" align="center" prop="roomHouseType" :formatter="roomHouseTypeFormat" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="{ row }">
          <span>{{ parseTime(row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="120">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleUpdate(row)" v-hasPermi="['system:room:edit']">修改</el-button>
          <el-button link type="danger" @click="handleDelete(row)" v-hasPermi="['system:room:remove']">删除</el-button>
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

    <el-dialog :title="title" v-model="open" width="560px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="单元/楼栋" prop="selected">
          <el-cascader
            v-model="selectedAreaInfo"
            :props="cascaderProps"
            :options="unitBuildingOptions"
            clearable
            @change="handleAreaChange"
          />
        </el-form-item>
        <el-form-item label="楼层" prop="roomLevel">
          <el-input v-model="form.roomLevel" placeholder="请输入楼层" />
        </el-form-item>
        <el-form-item label="房间名称" prop="roomName">
          <el-input v-model="form.roomName" placeholder="请输入房间名称" />
        </el-form-item>
        <el-form-item label="建筑面积" prop="roomAcreage">
          <el-input v-model="form.roomAcreage" placeholder="请输入房屋建筑面积" />
        </el-form-item>
        <el-form-item label="算费系数" prop="roomCost">
          <el-input v-model="form.roomCost" placeholder="请输入算费系数" />
        </el-form-item>
        <el-form-item label="房屋状态" prop="roomStatus">
          <el-select v-model="form.roomStatus" placeholder="请选择房屋状态">
            <el-option
              v-for="dict in roomStatusOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="是否商铺" prop="roomIsShop">
          <el-radio-group v-model="form.roomIsShop">
            <el-radio v-for="dict in roomIsShopOptions" :key="dict.dictValue" :label="dict.dictValue">
              {{ dict.dictLabel }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否商品房" prop="roomSCommercialHouse">
          <el-radio-group v-model="form.roomSCommercialHouse">
            <el-radio
              v-for="dict in roomSCommercialHouseOptions"
              :key="dict.dictValue"
              :label="dict.dictValue"
            >
              {{ dict.dictLabel }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="房屋户型" prop="roomHouseType">
          <el-select v-model="form.roomHouseType" placeholder="请选择房屋户型">
            <el-option
              v-for="dict in roomHouseTypeOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"
            />
          </el-select>
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
import { listRoom, getRoom, delRoom, addRoom, updateRoom, exportRoom } from '@/api/property/room'
import { queryPullDownRoom } from '@/api/property/building'
import { getPropertyId } from '@/utils/auth'

export default {
  name: 'Room',
  data() {
    return {
      show: false,
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      roomList: [],
      selectedAreaInfo: [],
      selectedSearchInfo: [],
      unitBuildingOptions: [],
      searchOptions: [],
      title: '',
      open: false,
      roomStatusOptions: [],
      roomIsShopOptions: [],
      roomSCommercialHouseOptions: [],
      roomHouseTypeOptions: [],
      cascaderProps: {
        value: 'code',
        label: 'name'
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        communityId: null,
        roomStatus: null,
        buildingId: null,
        unitId: null
      },
      form: {},
      rules: {
        selected: [{ required: true, message: '楼栋/单元不能为空', trigger: 'change' }],
        roomName: [{ required: true, message: '房间名称不能为空', trigger: 'blur' }],
        roomLevel: [
          { required: true, message: '楼层不能为空', trigger: 'blur' },
          { pattern: /^[1-9]\d*$/, message: '请输入正确的整数格式', trigger: 'change' }
        ],
        roomHouseType: [{ required: true, message: '房屋户型不能为空', trigger: 'change' }],
        roomIsShop: [{ required: true, message: '是否商铺不能为空', trigger: 'change' }],
        roomSCommercialHouse: [{ required: true, message: '是否商品房不能为空', trigger: 'change' }],
        roomStatus: [{ required: true, message: '房屋状态不能为空', trigger: 'change' }],
        roomAcreage: [
          { required: true, message: '面积不能为空', trigger: 'blur' },
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
    this.syncCommunityId()
    this.getList()
    this.getDicts('room_state').then((response) => {
      this.roomStatusOptions = response.data || []
    })
    this.getDicts('sys_yes_no').then((response) => {
      this.roomIsShopOptions = response.data || []
      this.roomSCommercialHouseOptions = response.data || []
    })
    this.getDicts('house_state').then((response) => {
      this.roomHouseTypeOptions = response.data || []
    })
    this.loadUnitBuildingOptions()
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
    loadUnitBuildingOptions() {
      queryPullDownRoom({ communityId: this.getCurrentPropertyId() }).then((response) => {
        this.unitBuildingOptions = response.data || []
        this.searchOptions = response.data || []
      })
    },
    resolveCommunityIdByBuilding(buildingId) {
      const current = this.unitBuildingOptions.find((item) => String(item.buildingId) === String(buildingId))
      return current?.communityId || null
    },
    selectedUnitBuilding(detail) {
      this.selectedSearchInfo = detail || []
    },
    handleAreaChange(detail) {
      this.selectedAreaInfo = detail || []
      this.form.selected = this.selectedAreaInfo
    },
    getList() {
      this.loading = true
      this.queryParams.communityId = this.getCurrentPropertyId()
      listRoom(this.queryParams)
        .then((response) => {
          this.roomList = response.rows || []
          this.total = response.total || 0
        })
        .finally(() => {
          this.loading = false
        })
    },
    roomStatusFormat(row) {
      return this.selectDictLabel(this.roomStatusOptions, row.roomStatus)
    },
    roomIsShopFormat(row) {
      return this.selectDictLabel(this.roomIsShopOptions, row.roomIsShop)
    },
    roomSCommercialHouseFormat(row) {
      return this.selectDictLabel(this.roomSCommercialHouseOptions, row.roomSCommercialHouse)
    },
    roomHouseTypeFormat(row) {
      return this.selectDictLabel(this.roomHouseTypeOptions, row.roomHouseType)
    },
    reset() {
      this.form = {
        roomId: null,
        communityId: this.getCurrentPropertyId(),
        buildingId: null,
        unitId: null,
        roomLevel: null,
        roomCode: null,
        roomName: null,
        roomAcreage: null,
        roomCost: null,
        roomStatus: null,
        roomIsShop: 'Y',
        roomSCommercialHouse: 'Y',
        roomHouseType: null,
        remark: null,
        selected: null
      }
      this.selectedAreaInfo = []
      this.resetForm('form')
    },
    cancel() {
      this.open = false
      this.reset()
    },
    handleQuery() {
      this.queryParams.buildingId = this.selectedSearchInfo?.[0] || null
      this.queryParams.unitId = this.selectedSearchInfo?.[1] || null
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.selectedSearchInfo = []
      this.syncCommunityId()
      this.queryParams.buildingId = null
      this.queryParams.unitId = null
      this.queryParams.pageNum = 1
      this.getList()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.roomId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.loadUnitBuildingOptions()
      this.open = true
      this.title = '添加房间'
    },
    handleUpdate(row) {
      this.reset()
      const roomId = row.roomId || this.ids[0]
      getRoom(roomId).then((response) => {
        this.form = response.data || {}
        this.selectedAreaInfo = [this.form.buildingId, this.form.unitId].filter(Boolean)
        this.form.selected = this.selectedAreaInfo
        this.loadUnitBuildingOptions()
        this.open = true
        this.title = '修改房间'
      })
    },
    submitForm() {
      this.form.selected = this.selectedAreaInfo
      this.$refs.form.validate((valid) => {
        if (!valid) {
          return
        }
        this.form.buildingId = this.selectedAreaInfo?.[0] || null
        this.form.unitId = this.selectedAreaInfo?.[1] || null
        this.form.communityId = this.getCurrentPropertyId() || this.resolveCommunityIdByBuilding(this.form.buildingId) || this.form.communityId
        const request = this.form.roomId ? updateRoom(this.form) : addRoom(this.form)
        request.then(() => {
          this.msgSuccess(this.form.roomId ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const roomIds = row.roomId || this.ids
      this.$confirm('是否确认删除房间编号为"' + roomIds + '"的数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => delRoom(roomIds))
        .then(() => {
          this.msgSuccess('删除成功')
          this.getList()
        })
    },
    handleExport() {
      const queryParams = { ...this.queryParams }
      this.$confirm('是否确认导出所有房间数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => exportRoom(queryParams))
        .then((response) => {
          this.download(response.msg)
        })
    }
  }
}
</script>
