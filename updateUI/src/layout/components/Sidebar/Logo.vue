<template>
  <div
    class="sidebar-logo-container"
    :class="{ collapse }"
    :style="{ backgroundColor: sideTheme === 'theme-dark' ? variables.menuBg : variables.menuLightBg }"
  >
    <transition name="sidebarLogoFade">
      <router-link v-if="collapse" key="collapse" class="sidebar-logo-link" to="/">
        <img v-if="logo" :src="logo" class="sidebar-logo" />
        <h1
          v-else
          class="sidebar-title"
          :style="{ color: sideTheme === 'theme-dark' ? variables.sidebarTitle : variables.sidebarLightTitle }"
        >
          {{ title }}
        </h1>
      </router-link>
      <router-link v-else key="expand" class="sidebar-logo-link" to="/">
        <img v-if="logo" :src="logo" class="sidebar-logo" />
        <h1
          class="sidebar-title"
          :style="{ color: sideTheme === 'theme-dark' ? variables.sidebarTitle : variables.sidebarLightTitle }"
        >
          {{ title }}
        </h1>
      </router-link>
    </transition>
  </div>
</template>

<script>
import logoImg from '@/assets/logo/logo.png'

const styleVariables = {
  menuBg: '#312520',
  menuLightBg: '#312520',
  sidebarTitle: '#FBE4DE',
  sidebarLightTitle: '#FBE4DE'
}

export default {
  name: 'SidebarLogo',
  props: {
    collapse: {
      type: Boolean,
      required: true
    }
  },
  data() {
    return {
      title: '社区物业管理系统',
      logo: logoImg
    }
  },
  computed: {
    variables() {
      return styleVariables
    },
    sideTheme() {
      return this.$store.state.settings.sideTheme || 'theme-dark'
    }
  }
}
</script>

<style lang="scss" scoped>
.sidebarLogoFade-enter-active {
  transition: opacity 1.5s;
}

.sidebarLogoFade-enter,
.sidebarLogoFade-leave-to {
  opacity: 0;
}

.sidebar-logo-container {
  position: relative;
  width: 100%;
  height: 50px;
  line-height: 50px;
  background:
    radial-gradient(circle at 18% 0%, rgba(251, 228, 222, 0.10), transparent 42%),
    rgba(49, 37, 32, 0.96) !important;
  text-align: center;
  overflow: hidden;
  border-bottom: 1px solid rgba(251, 228, 222, 0.12);

  &::after {
    content: '';
    position: absolute;
    left: 14px;
    right: 14px;
    bottom: 0;
    height: 1px;
    background: linear-gradient(90deg, transparent, rgba(236, 121, 123, 0.55), transparent);
    opacity: 0.9;
  }

  & .sidebar-logo-link {
    height: 100%;
    width: 100%;

    & .sidebar-logo {
      width: 32px;
      height: 32px;
      vertical-align: middle;
      margin-right: 12px;
      border-radius: 10px;
      filter: drop-shadow(0 6px 12px rgba(0, 0, 0, 0.24));
    }

    & .sidebar-title {
      display: inline-block;
      margin: 0;
      color: #fbe4de;
      font-weight: 600;
      line-height: 50px;
      font-size: 14px;
      letter-spacing: 0.5px;
      font-family: Avenir, Helvetica Neue, Arial, Helvetica, sans-serif;
      vertical-align: middle;
      text-shadow: 0 0 18px rgba(236, 121, 123, 0.22);
    }
  }

  &.collapse {
    .sidebar-logo {
      margin-right: 0;
    }
  }
}
</style>
