<template>
  <div class="avatar-cropper">
    <div class="avatar-cropper__editor">
      <img ref="image" :src="src" alt="avatar-cropper" class="avatar-cropper__image" />
    </div>
    <div class="avatar-cropper__preview-wrapper">
      <div ref="preview" class="avatar-upload-preview avatar-cropper__preview" />
    </div>
  </div>
</template>

<script>
import Cropper from 'cropperjs'
import 'cropperjs/dist/cropper.css'

export default {
  name: 'AvatarCropper',
  props: {
    src: {
      type: String,
      default: ''
    },
    autoCropWidth: {
      type: Number,
      default: 200
    },
    autoCropHeight: {
      type: Number,
      default: 200
    }
  },
  emits: ['ready', 'realTime'],
  mounted() {
    this.initCropper()
  },
  beforeUnmount() {
    this.destroyCropper()
  },
  watch: {
    src() {
      this.$nextTick(() => {
        this.initCropper()
      })
    }
  },
  methods: {
    initCropper() {
      const image = this.$refs.image
      if (!image || !this.src) {
        this.destroyCropper()
        return
      }

      this.destroyCropper()

      this.cropper = new Cropper(image, {
        aspectRatio: this.autoCropWidth / this.autoCropHeight,
        viewMode: 1,
        dragMode: 'move',
        autoCropArea: 1,
        background: false,
        guides: true,
        center: true,
        highlight: false,
        responsive: true,
        preview: this.$refs.preview,
        cropBoxMovable: false,
        cropBoxResizable: false,
        toggleDragModeOnDblclick: false,
        ready: () => {
          this.emitPreview()
          this.$emit('ready')
        },
        crop: () => {
          this.emitPreview()
        }
      })
    },
    destroyCropper() {
      if (this.cropper) {
        this.cropper.destroy()
        this.cropper = null
      }
      if (this.$refs.preview) {
        this.$refs.preview.innerHTML = ''
      }
    },
    emitPreview() {
      const canvas = this.getCroppedCanvas()
      if (!canvas) {
        return
      }
      this.$emit('realTime', {
        url: canvas.toDataURL('image/png'),
        img: {
          width: `${this.autoCropWidth}px`,
          height: `${this.autoCropHeight}px`
        }
      })
    },
    getCroppedCanvas() {
      if (!this.cropper) {
        return null
      }
      return this.cropper.getCroppedCanvas({
        width: this.autoCropWidth,
        height: this.autoCropHeight,
        imageSmoothingEnabled: true,
        imageSmoothingQuality: 'high'
      })
    },
    getCropBlob(callback) {
      const canvas = this.getCroppedCanvas()
      if (!canvas) {
        callback?.(null)
        return
      }
      canvas.toBlob((blob) => callback?.(blob), 'image/png')
    },
    changeScale(step) {
      this.cropper?.zoom(step * 0.1)
      this.emitPreview()
    },
    rotateLeft() {
      this.cropper?.rotate(-90)
      this.emitPreview()
    },
    rotateRight() {
      this.cropper?.rotate(90)
      this.emitPreview()
    }
  }
}
</script>

<style scoped lang="scss">
.avatar-cropper {
  display: flex;
}

.avatar-cropper__editor,
.avatar-cropper__preview-wrapper {
  width: 50%;
  height: 350px;
}

.avatar-cropper__editor {
  overflow: hidden;
}

.avatar-cropper__image {
  display: block;
  max-width: 100%;
}

.avatar-cropper__preview-wrapper {
  position: relative;
}

.avatar-cropper__preview :deep(img) {
  display: block;
  max-width: none;
}
</style>
