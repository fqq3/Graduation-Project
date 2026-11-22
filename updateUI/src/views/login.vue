<template>
  <div class="login-page">
    <div class="background-layers">
      <div id="tsparticles"></div>
      <div class="fluid-smoke"></div>
      <div class="bottom-glow"></div>
    </div>

    <div class="content-layer">
      <h1 class="system-title">面向未来社区的数字孪生物业管理系统</h1>

      <div class="login-card">
        <el-form
          ref="loginForm"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          auto-complete="on"
          label-position="left"
        >
          <el-form-item prop="username" class="input-group">
            <el-input
              v-model="loginForm.username"
              type="text"
              auto-complete="off"
              placeholder="账号 / Account"
              spellcheck="false"
              @keyup.enter="handleLogin"
            >
              <template #prefix>
                <el-icon class="login-field-icon"><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password" class="input-group">
            <el-input
              ref="password"
              v-model="loginForm.password"
              :type="passwordType"
              auto-complete="off"
              placeholder="密码 / Password"
              @keyup.enter="handleLogin"
            >
              <template #prefix>
                <el-icon class="login-field-icon"><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <div class="code-row">
            <el-form-item prop="code" class="input-group code-input-item">
              <el-input
                v-model="loginForm.code"
                auto-complete="off"
                placeholder="验证码 / Code"
                @keyup.enter="handleLogin"
              >
                <template #prefix>
                  <el-icon class="login-field-icon"><Key /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <div class="login-code" title="点击刷新验证码">
              <img :src="codeUrl" class="login-code-img" alt="验证码" @click="getCode" />
            </div>
          </div>

          <el-checkbox v-model="loginForm.rememberMe" class="remember-checkbox">记住密码</el-checkbox>

          <button class="login-btn" :disabled="loading" @click.prevent="handleLogin">
            <span v-if="!loading">进入系统</span>
            <span v-else>登录中...</span>
          </button>
        </el-form>
      </div>
    </div>

    <div class="el-login-footer">
      <span>
        Copyright 2014-2023 Jiangnan University.. 苏ICP备09101752号
        <a href="https://www.jiangnan.edu.cn/">江南大学</a>
      </span>
    </div>
  </div>
</template>

<script>
import { getCodeImg } from '@/api/login'
import { Key, Lock, User } from '@element-plus/icons-vue'
import Cookies from 'js-cookie'
import { encrypt, decrypt } from '@/utils/jsencrypt'

const TSPARTICLES_CDN = 'https://cdn.jsdelivr.net/npm/@tsparticles/all@3.1.0/tsparticles.all.bundle.min.js'

const particleOptions = {
  fullScreen: {
    enable: false
  },
  fpsLimit: 60,
  detectRetina: true,
  particles: {
    number: {
      value: 34,
      density: {
        enable: false
      }
    },
    color: {
      value: ['#EC797B', '#FBE4DE']
    },
    shape: {
      type: 'circle'
    },
    opacity: {
      value: {
        min: 0.18,
        max: 0.5
      },
      animation: {
        enable: true,
        speed: 0.8,
        minimumValue: 0.12,
        sync: false
      }
    },
    size: {
      value: {
        min: 2,
        max: 5
      }
    },
    move: {
      enable: true,
      speed: 0.55,
      direction: 'none',
      random: true,
      straight: false,
      outModes: {
        default: 'bounce'
      }
    },
    links: {
      enable: true,
      distance: 190,
      color: '#EC797B',
      opacity: 0.22,
      width: 1
    }
  },
  interactivity: {
    events: {
      onHover: {
        enable: false
      },
      onClick: {
        enable: false
      },
      resize: true
    }
  },
  background: {
    color: 'transparent'
  }
}

export default {
  name: 'Login',
  components: {
    User,
    Lock,
    Key
  },
  data() {
    return {
      codeUrl: '',
      loginForm: {
        username: 'admin',
        password: 'admin123',
        rememberMe: false,
        code: '',
        uuid: ''
      },
      loginRules: {
        username: [{ required: true, trigger: 'blur', message: '用户名不能为空' }],
        password: [{ required: true, trigger: 'blur', message: '密码不能为空' }],
        code: [{ required: true, trigger: 'change', message: '验证码不能为空' }]
      },
      loading: false,
      passwordType: 'password',
      redirect: undefined,
      particlesContainer: null
    }
  },
  watch: {
    $route: {
      handler(route) {
        this.redirect = route.query?.redirect
      },
      immediate: true
    }
  },
  created() {
    this.getCode()
    this.getCookie()
  },
  mounted() {
    this.initParticles()
  },
  beforeUnmount() {
    this.destroyVisualEffects()
  },
  methods: {
    loadTsParticlesScript() {
      if (window.tsParticles) {
        return Promise.resolve()
      }

      const existedScript = document.getElementById('tsparticles-cdn')
      if (existedScript) {
        return new Promise((resolve, reject) => {
          existedScript.addEventListener('load', resolve, { once: true })
          existedScript.addEventListener('error', reject, { once: true })
        })
      }

      return new Promise((resolve, reject) => {
        const script = document.createElement('script')
        script.id = 'tsparticles-cdn'
        script.src = TSPARTICLES_CDN
        script.async = true
        script.onload = resolve
        script.onerror = reject
        document.head.appendChild(script)
      })
    },
    async initParticles() {
      try {
        await this.loadTsParticlesScript()

        if (!window.tsParticles) {
          return
        }

        try {
          this.particlesContainer = await window.tsParticles.load({
            id: 'tsparticles',
            options: particleOptions
          })
        } catch (error) {
          this.particlesContainer = await window.tsParticles.load('tsparticles', particleOptions)
        }
      } catch (error) {
        console.warn('tsParticles 初始化失败，请检查 CDN 是否可访问。', error)
      }
    },
    destroyVisualEffects() {
      this.particlesContainer?.destroy?.()
      this.particlesContainer = null
    },
    showPwd() {
      if (this.passwordType === 'password') {
        this.passwordType = ''
      } else {
        this.passwordType = 'password'
      }
      this.$nextTick(() => {
        this.$refs.password?.focus?.()
      })
    },
    getCode() {
      getCodeImg().then((res) => {
        this.codeUrl = res.img
        this.loginForm.uuid = res.uuid
      })
    },
    getCookie() {
      const username = Cookies.get('username')
      const password = Cookies.get('password')
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        ...this.loginForm,
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : rememberMe === 'true'
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate((valid) => {
        if (!valid) {
          return
        }

        this.loading = true
        if (this.loginForm.rememberMe) {
          Cookies.set('username', this.loginForm.username, { expires: 30 })
          Cookies.set('password', encrypt(this.loginForm.password), { expires: 30 })
          Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 })
        } else {
          Cookies.remove('username')
          Cookies.remove('password')
          Cookies.remove('rememberMe')
        }

        this.$store
          .dispatch('Login', this.loginForm)
          .then(() => this.$router.push({ path: this.redirect || '/' }))
          .catch(() => {
            this.getCode()
          })
          .finally(() => {
            this.loading = false
          })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  --bg-deep: #312520;
  --glow-pink: #fbe4de;
  --accent-red: #ec797b;

  position: relative;
  min-height: 100vh;
  width: 100%;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #ffffff;
  background-color: var(--bg-deep);
  font-family: 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
}

.background-layers {
  position: fixed;
  inset: 0;
  z-index: 1;
  background:
    radial-gradient(circle at 50% 120%, rgba(251, 228, 222, 0.16), transparent 48%),
    linear-gradient(180deg, #312520 0%, #2b211d 100%);
}

#tsparticles {
  position: absolute;
  inset: 0;
  z-index: 4;
  pointer-events: auto;
  opacity: 1;
  mask-image: linear-gradient(to bottom, black 0%, black 55%, transparent 100%);
  -webkit-mask-image: linear-gradient(to bottom, black 0%, black 55%, transparent 100%);
}

.fluid-smoke {
  position: absolute;
  inset: 0;
  z-index: 3;
  pointer-events: none;
  overflow: hidden;
}

.fluid-smoke::before,
.fluid-smoke::after {
  content: '';
  position: absolute;
  width: 70vw;
  height: 70vw;
  border-radius: 50%;
  filter: blur(90px);
  opacity: 0.45;
  mix-blend-mode: screen;
}

.fluid-smoke::before {
  left: -18vw;
  bottom: -26vw;
  background: radial-gradient(
    circle,
    rgba(251, 228, 222, 0.55) 0%,
    rgba(251, 228, 222, 0.22) 35%,
    transparent 68%
  );
}

.fluid-smoke::after {
  right: -22vw;
  top: -18vw;
  background: radial-gradient(
    circle,
    rgba(236, 121, 123, 0.42) 0%,
    rgba(236, 121, 123, 0.18) 38%,
    transparent 70%
  );
}

.bottom-glow {
  position: fixed;
  bottom: -18vh;
  left: 0;
  width: 100%;
  height: 46vh;
  background: radial-gradient(
    ellipse at center,
    rgba(251, 228, 222, 0.65) 0%,
    rgba(251, 228, 222, 0.22) 36%,
    rgba(251, 228, 222, 0) 76%
  );
  filter: blur(36px);
  opacity: 0.42;
  z-index: 2;
  pointer-events: none;
}

.content-layer {
  position: relative;
  z-index: 10;
  text-align: center;
  width: 100%;
  max-width: 500px;
  padding: 0 20px;
  perspective: 1000px;
}

.system-title {
  color: rgba(255, 255, 255, 0.9);
  font-size: 28px;
  font-weight: 300;
  letter-spacing: 6px;
  line-height: 1.5;
  margin: 0 0 70px;
  text-shadow: 0 4px 15px rgba(0, 0, 0, 0.4);
  opacity: 0;
  transform: translate3d(0, 42px, 0);
  background-image: linear-gradient(
    135deg,
    rgba(255, 255, 255, 0.68) 0%,
    #ffffff 48%,
    rgba(251, 228, 222, 0.9) 58%,
    rgba(255, 255, 255, 0.68) 100%
  );
  background-size: 220% 100%;
  background-position: 200% 0;
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: titleEnter 1.25s cubic-bezier(0.22, 1, 0.36, 1) 0.15s forwards;
  will-change: opacity, transform;
}

@keyframes titleEnter {
  0% {
    opacity: 0;
    transform: translate3d(0, 42px, 0);
  }
  100% {
    opacity: 1;
    transform: translate3d(0, 0, 0);
  }
}

.login-card {
  position: relative;
  width: 100%;
  background: rgba(255, 255, 255, 0.045);
  backdrop-filter: blur(14px) saturate(115%);
  -webkit-backdrop-filter: blur(14px) saturate(115%);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 28px;
  padding: 55px 45px;
  box-shadow: 0 35px 70px rgba(0, 0, 0, 0.4);
  overflow: hidden;
  opacity: 0;
  transform: translate3d(0, 50px, 0) rotateX(18deg);
  animation: cardEnter 1.35s cubic-bezier(0.22, 1, 0.36, 1) 0.45s forwards;
  will-change: transform, opacity;
}

@keyframes cardEnter {
  0% {
    opacity: 0;
    transform: translate3d(0, 50px, 0) rotateX(18deg);
  }
  100% {
    opacity: 1;
    transform: translate3d(0, 0, 0) rotateX(0deg);
  }
}

.login-card::before {
  content: '';
  position: absolute;
  inset: 0;
  padding: 1px;
  border-radius: inherit;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(251, 228, 222, 0.18) 28%,
    rgba(236, 121, 123, 0.62) 50%,
    rgba(251, 228, 222, 0.18) 72%,
    transparent 100%
  );
  background-size: 220% 100%;
  opacity: 0;
  pointer-events: none;
  -webkit-mask:
    linear-gradient(#000 0 0) content-box,
    linear-gradient(#000 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  transition: opacity 0.35s ease;
  z-index: 1;
}

.login-card:hover::before {
  opacity: 1;
  animation: borderSweep 5s linear infinite;
}

@keyframes borderSweep {
  0% {
    background-position: 220% 0;
  }
  100% {
    background-position: -220% 0;
  }
}

.login-form {
  position: relative;
  z-index: 2;
}

.input-group {
  margin-bottom: 32px;
}

.input-group:last-child {
  margin-bottom: 0;
}

.code-row {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 30px;
}

.code-input-item {
  flex: 1;
  margin-bottom: 0;
}

.login-code {
  box-sizing: border-box;
  flex: 0 0 132px;
  width: 132px;
  height: 54px;
  padding: 4px;
  background: rgba(255, 255, 255, 0.055);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.login-code:hover {
  border-color: rgba(236, 121, 123, 0.95);
  box-shadow: 0 0 18px rgba(236, 121, 123, 0.24);
}

.login-code-img {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 10px;
}

.remember-checkbox {
  display: flex;
  margin: -4px 0 25px;
}

.login-btn {
  width: 100%;
  padding: 18px;
  background: var(--accent-red);
  border: none;
  border-radius: 14px;
  color: #ffffff;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.4s;
  margin-top: 10px;
  box-shadow: 0 8px 20px rgba(236, 121, 123, 0.24);
}

.login-btn:hover:not(:disabled) {
  opacity: 0.92;
  transform: translateY(-2px);
  box-shadow: 0 12px 25px rgba(236, 121, 123, 0.35);
}

.login-btn:disabled {
  cursor: not-allowed;
  opacity: 0.72;
}

.el-login-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  z-index: 10;
  width: 100%;
  height: 40px;
  line-height: 40px;
  text-align: center;
  color: rgba(255, 255, 255, 0.62);
  font-family: Arial, sans-serif;
  font-size: 10px;
  letter-spacing: 1px;
}

.el-login-footer a {
  color: rgba(251, 228, 222, 0.82);
  text-decoration: none;
}

:deep(.el-form-item) {
  margin-bottom: 0;
}

:deep(.el-form-item.input-group) {
  margin-bottom: 18px;
}

:deep(.el-form-item.code-input-item) {
  margin-bottom: 0;
}

:deep(.el-form-item__error) {
  padding-top: 5px;
  color: #ffb4b5;
}

:deep(.el-input),
:deep(.el-input__wrapper) {
  width: 100%;
}

:deep(.el-input__wrapper) {
  height: 54px;
  padding: 0 20px;
  background: rgba(255, 255, 255, 0.055);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 14px;
  box-shadow: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.el-input__wrapper.is-focus) {
  background: rgba(255, 255, 255, 0.085);
  border-color: rgba(236, 121, 123, 0.95);
  box-shadow: 0 0 18px rgba(236, 121, 123, 0.24);
}

:deep(.el-input__inner) {
  height: 54px;
  color: #ffffff;
  font-size: 14px;
  caret-color: var(--accent-red);
}

:deep(.el-input__prefix) {
  margin-right: 10px;
}

.login-field-icon {
  color: rgba(251, 228, 222, 0.7);
  font-size: 17px;
  transition: color 0.3s ease;
}

:deep(.el-input__wrapper.is-focus) .login-field-icon {
  color: var(--accent-red);
}

:deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.36);
}

:deep(.el-input__inner:-webkit-autofill),
:deep(.el-input__inner:-webkit-autofill:hover),
:deep(.el-input__inner:-webkit-autofill:focus) {
  box-shadow: 0 0 0 1000px rgba(49, 37, 32, 0.96) inset !important;
  -webkit-text-fill-color: #ffffff !important;
  caret-color: var(--accent-red);
}

:deep(.el-checkbox__label) {
  color: rgba(255, 255, 255, 0.72);
  font-size: 14px;
}

:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: var(--accent-red);
  border-color: var(--accent-red);
}

:deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
  color: rgba(255, 255, 255, 0.86);
}

:deep(.el-checkbox__inner:hover),
:deep(.el-checkbox__input.is-focus .el-checkbox__inner) {
  border-color: var(--accent-red);
}

@media (max-width: 640px) {
  .content-layer {
    max-width: 100%;
    padding: 0 18px;
  }

  .system-title {
    font-size: 22px;
    letter-spacing: 3px;
    margin-bottom: 42px;
  }

  .login-card {
    padding: 38px 24px;
    border-radius: 22px;
  }

  .code-row {
    gap: 10px;
  }

  .login-code {
    width: 116px;
  }
}
</style>