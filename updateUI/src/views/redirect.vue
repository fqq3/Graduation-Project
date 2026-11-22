<script>
export default {
  created() {
    const { params, query } = this.$route
    const pathMatch = Array.isArray(params.pathMatch)
      ? params.pathMatch.join('/')
      : params.pathMatch || ''

    this.$router.replace({ path: `/${pathMatch}`, query })
  },
  render() {
    return null
  }
}
</script>
