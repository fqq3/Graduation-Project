<template>
  <div class="component-upload-image">
    <el-upload
      :action="uploadImgUrl"
      list-type="picture-card"
      :on-success="handleUploadSuccess"
      :before-upload="handleBeforeUpload"
      :on-error="handleUploadError"
      name="file"
      :show-file-list="false"
      :headers="headers"
      style="display: inline-block; vertical-align: top"
    >
      <img v-if="imageUrl" :src="imageUrl" class="avatar" />
      <el-icon v-else class="avatar-uploader-icon">
        <Plus />
      </el-icon>
    </el-upload>
  </div>
</template>

<script>
import { Plus } from '@element-plus/icons-vue'
import { getToken } from '@/utils/auth'

export default {
  name: 'UploadImage',
  components: { Plus },
  emits: ['input', 'update:modelValue'],
  props: {
    modelValue: {
      type: String,
      default: undefined
    },
    value: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      uploadImgUrl: import.meta.env.VITE_APP_BASE_API + '/common/upload',
      headers: {
        Authorization: 'Bearer ' + getToken()
      },
      loading: null
    }
  },
  computed: {
    imageUrl() {
      return this.modelValue !== undefined ? this.modelValue : this.value
    }
  },
  beforeUnmount() {
    this.loading?.close()
    this.loading = null
  },
  methods: {
    handleUploadSuccess(res) {
      this.$emit('update:modelValue', res.url)
      this.$emit('input', res.url)
      this.loading?.close()
    },
    handleBeforeUpload() {
      this.loading?.close()
      this.loading = this.$loading({
        lock: true,
        text: '上传中',
        background: 'rgba(0, 0, 0, 0.7)'
      })
    },
    handleUploadError() {
      this.$message({
        type: 'error',
        message: '上传失败'
      })
      this.loading?.close()
    }
  }
}
</script>

<style scoped lang="scss">
.avatar {
  width: 100%;
  height: 100%;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: var(--el-text-color-secondary, #909399);
}
</style>
