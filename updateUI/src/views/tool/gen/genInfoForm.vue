<template>
  <el-form ref="genInfoForm" :model="info" :rules="rules" label-width="150px">
    <el-row>
      <el-col :span="12">
        <el-form-item prop="tplCategory">
          <template #label>生成模板</template>
          <el-select v-model="info.tplCategory">
            <el-option label="单表（增删改查）" value="crud" />
            <el-option label="树表（增删改查）" value="tree" />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item prop="packageName">
          <template #label>
            <span>
              生成包路径
              <el-tooltip content="生成在哪个java包下，例如 com.jeethink.system" placement="top">
                <el-icon><QuestionFilled /></el-icon>
              </el-tooltip>
            </span>
          </template>
          <el-input v-model="info.packageName" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item prop="moduleName">
          <template #label>
            <span>
              生成模块名
              <el-tooltip content="可理解为子系统名，例如 system" placement="top">
                <el-icon><QuestionFilled /></el-icon>
              </el-tooltip>
            </span>
          </template>
          <el-input v-model="info.moduleName" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item prop="businessName">
          <template #label>
            <span>
              生成业务名
              <el-tooltip content="可理解为功能英文名，例如 user" placement="top">
                <el-icon><QuestionFilled /></el-icon>
              </el-tooltip>
            </span>
          </template>
          <el-input v-model="info.businessName" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item prop="functionName">
          <template #label>
            <span>
              生成功能名
              <el-tooltip content="用作类描述，例如 用户" placement="top">
                <el-icon><QuestionFilled /></el-icon>
              </el-tooltip>
            </span>
          </template>
          <el-input v-model="info.functionName" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item>
          <template #label>
            <span>
              上级菜单
              <el-tooltip content="分配到指定菜单下，例如 系统管理" placement="top">
                <el-icon><QuestionFilled /></el-icon>
              </el-tooltip>
            </span>
          </template>
          <LegacyTreeselect
            :append-to-body="true"
            v-model="info.parentMenuId"
            :options="menus"
            :normalizer="normalizer"
            :show-count="true"
            placeholder="请选择系统菜单"
          />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item prop="genType">
          <template #label>
            <span>
              生成代码方式
              <el-tooltip content="默认为zip压缩包下载，也可以自定义生成路径" placement="top">
                <el-icon><QuestionFilled /></el-icon>
              </el-tooltip>
            </span>
          </template>
          <el-radio v-model="info.genType" label="0">zip压缩包</el-radio>
          <el-radio v-model="info.genType" label="1">自定义路径</el-radio>
        </el-form-item>
      </el-col>
      <el-col v-if="info.genType === '1'" :span="24">
        <el-form-item prop="genPath">
          <template #label>
            <span>
              自定义路径
              <el-tooltip content="填写磁盘绝对路径，若不填写，则生成到当前Web项目下" placement="top">
                <el-icon><QuestionFilled /></el-icon>
              </el-tooltip>
            </span>
          </template>
          <el-input v-model="info.genPath">
            <template #append>
              <el-dropdown>
                <el-button type="primary">
                  最近路径快速选择
                  <el-icon class="el-icon--right"><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="info.genPath = '/'">恢复默认的生成基础路径</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </el-input>
        </el-form-item>
      </el-col>
    </el-row>

    <el-row v-show="info.tplCategory === 'tree'">
      <h4 class="form-header">其他信息</h4>
      <el-col :span="12">
        <el-form-item>
          <template #label>
            <span>
              树编码字段
              <el-tooltip content="树显示的编码字段名， 如：dept_id" placement="top">
                <el-icon><QuestionFilled /></el-icon>
              </el-tooltip>
            </span>
          </template>
          <el-select v-model="info.treeCode" placeholder="请选择">
            <el-option
              v-for="column in info.columns || []"
              :key="column.columnName"
              :label="`${column.columnName}：${column.columnComment || ''}`"
              :value="column.columnName"
            />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item>
          <template #label>
            <span>
              树父编码字段
              <el-tooltip content="树显示的父编码字段名， 如：parent_Id" placement="top">
                <el-icon><QuestionFilled /></el-icon>
              </el-tooltip>
            </span>
          </template>
          <el-select v-model="info.treeParentCode" placeholder="请选择">
            <el-option
              v-for="column in info.columns || []"
              :key="column.columnName + '-parent'"
              :label="`${column.columnName}：${column.columnComment || ''}`"
              :value="column.columnName"
            />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item>
          <template #label>
            <span>
              树名称字段
              <el-tooltip content="树节点的显示名称字段名， 如：dept_name" placement="top">
                <el-icon><QuestionFilled /></el-icon>
              </el-tooltip>
            </span>
          </template>
          <el-select v-model="info.treeName" placeholder="请选择">
            <el-option
              v-for="column in info.columns || []"
              :key="column.columnName + '-name'"
              :label="`${column.columnName}：${column.columnComment || ''}`"
              :value="column.columnName"
            />
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>

<script>
import LegacyTreeselect from '@/components/LegacyTreeselect/index.vue'
import { ArrowDown, QuestionFilled } from '@element-plus/icons-vue'

export default {
  name: 'GenInfoForm',
  components: { ArrowDown, LegacyTreeselect, QuestionFilled },
  props: {
    info: {
      type: Object,
      default: () => ({})
    },
    menus: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      rules: {
        tplCategory: [{ required: true, message: '请选择生成模板', trigger: 'change' }],
        packageName: [{ required: true, message: '请输入生成包路径', trigger: 'blur' }],
        moduleName: [{ required: true, message: '请输入生成模块名', trigger: 'blur' }],
        businessName: [{ required: true, message: '请输入生成业务名', trigger: 'blur' }],
        functionName: [{ required: true, message: '请输入生成功能名', trigger: 'blur' }]
      }
    }
  },
  methods: {
    normalizer(node) {
      if (node.children && !node.children.length) {
        delete node.children
      }
      return {
        id: node.menuId,
        label: node.menuName,
        children: node.children
      }
    }
  }
}
</script>
