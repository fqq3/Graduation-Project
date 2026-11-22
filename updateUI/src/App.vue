<template>
  <router-view v-if="isRouterAlive" />
</template>
<!--isRouterAlive 为 true ，<router-view> 会被创建并插入 DOM，页面正常显示。
                false ，<router-view> 会被完全销毁（连同它所有子组件） -->


<script>
// 下面开始写这个组件的 JavaScript 逻辑
export default {//定义一个默认导出的对象导出(名为 App 的 Vue 组件)
  name: 'App',  // 组件的名字叫 App
  provide() {  // 向下面的子组件提供一些东西
    return {
      // 向子组件提供一个叫 reload 的函数，
      // 实际指向当前组件里的 reload 方法
      reload: this.reload
    }
  },
  // 定义这个组件的数据
  data() {
    return {
      // 这个组件有一个数据叫 isRouterAlive，
      // 初始值是 true（表示当前页面显示）
      isRouterAlive: true
    }
  },
  
  methods: {// 定义这个组件的方法（函数）
    reload() {    // 用来刷新当前页面的方法
      this.isRouterAlive = false   // 先把页面显示开关关掉（页面会被移除）
      // 等页面先完成刚才那次更新后，再执行下面代码
      this.$nextTick(() => {
        // 再把开关打开（页面重新创建，相当于刷新）
        this.isRouterAlive = true
      })
    }
  }
}
</script>