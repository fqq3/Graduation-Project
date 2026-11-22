<template>
  <div>
    <div class="user-info-head" @click="editCropper()">
      <img :src="avatar" title="点击上传头像" class="img-circle img-lg" />
    </div>
    <el-dialog
      :title="title"
      v-model="open"
      width="800px"
      append-to-body
      @opened="modalOpened"
      @closed="handleClosed"
    >
      <AvatarCropper
        v-if="visible"
        ref="cropper"
        :src="options.img"
        :auto-crop-width="options.autoCropWidth"
        :auto-crop-height="options.autoCropHeight"
        @realTime="realTime"
      />
      <br />
      <el-row>
        <el-col :lg="2" :md="2">
          <el-upload
            action="#"
            :http-request="requestUpload"
            :show-file-list="false"
            :before-upload="beforeUpload"
          >
            <el-button size="small">
              上传
              <el-icon class="legacy-el-icon el-icon-upload el-icon--right"><Upload /></el-icon>
            </el-button>
          </el-upload>
        </el-col>
        <el-col :lg="{ span: 1, offset: 2 }" :md="2">
          <el-button size="small" @click="changeScale(1)"><el-icon class="legacy-el-icon el-icon-plus"><Plus /></el-icon></el-button>
        </el-col>
        <el-col :lg="{ span: 1, offset: 1 }" :md="2">
          <el-button size="small" @click="changeScale(-1)"><el-icon class="legacy-el-icon el-icon-minus"><Minus /></el-icon></el-button>
        </el-col>
        <el-col :lg="{ span: 1, offset: 1 }" :md="2">
          <el-button size="small" @click="rotateLeft()"><el-icon class="legacy-el-icon el-icon-refresh-left"><RefreshLeft /></el-icon></el-button>
        </el-col>
        <el-col :lg="{ span: 1, offset: 1 }" :md="2">
          <el-button size="small" @click="rotateRight()"><el-icon class="legacy-el-icon el-icon-refresh-right"><RefreshRight /></el-icon></el-button>
        </el-col>
        <el-col :lg="{ span: 2, offset: 6 }" :md="2">
          <el-button type="primary" size="small" @click="uploadImg()">提 交</el-button>
        </el-col>
      </el-row>
    </el-dialog>
  </div>
</template>

<script>
import { Minus, Plus, RefreshLeft, RefreshRight, Upload } from '@element-plus/icons-vue'
import AvatarCropper from '@/components/AvatarCropper/index.vue'
import { uploadAvatar } from '@/api/system/user'
import { useUserStore } from '@/stores/user'

export default {
  name: 'UserAvatar',
  components: { AvatarCropper, Minus, Plus, RefreshLeft, RefreshRight, Upload },
  props: {
    user: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      open: false,
      visible: false,
      title: '修改头像',
      uploadedAvatar: '',
      options: {
        img: '',
        autoCropWidth: 200,
        autoCropHeight: 200,
        fixedBox: true
      },
      previews: {}
    }
  },
  computed: {
    avatar() {
      return this.uploadedAvatar || this.user.avatar || this.$store.getters.avatar
    }
  },
  methods: {
    editCropper() {
      this.options.img = this.avatar
      this.open = true
    },
    modalOpened() {
      this.visible = true
    },
    handleClosed() {
      this.visible = false
    },
    requestUpload() {},
    rotateLeft() {
      this.$refs.cropper?.rotateLeft()
    },
    rotateRight() {
      this.$refs.cropper?.rotateRight()
    },
    changeScale(num) {
      this.$refs.cropper?.changeScale(num)
    },
    beforeUpload(file) {
      if (!file.type.startsWith('image/')) {
        this.msgError('文件格式错误，请上传图片类型,如：JPG，PNG后缀的文件。')
        return false
      }

      const reader = new FileReader()
      reader.readAsDataURL(file)
      reader.onload = () => {
        this.options.img = reader.result
      }
      return false
    },
    uploadImg() {
      this.$refs.cropper?.getCropBlob((data) => {
        if (!data) {
          return
        }

        const formData = new FormData()
        formData.append('avatarfile', data)
        uploadAvatar(formData).then((response) => {
          this.open = false
          this.visible = false
          this.options.img = response.imgUrl
          this.uploadedAvatar = response.imgUrl
          useUserStore().avatar = response.imgUrl
          this.msgSuccess('修改成功')
        })
      })
    },
    realTime(data) {
      this.previews = data
    }
  }
}
</script>

<style scoped lang="scss">
.user-info-head {
  position: relative;
  display: inline-block;
  height: 120px;
}

.user-info-head:hover:after {
  content: '+';
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  color: #eee;
  background: rgba(0, 0, 0, 0.5);
  font-size: 24px;
  font-style: normal;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  cursor: pointer;
  line-height: 110px;
  border-radius: 50%;
}
</style>
