<template>
  <span>{{ displayValue }}</span>
</template>

<script>
const requestFrame =
  typeof window !== 'undefined' && window.requestAnimationFrame
    ? window.requestAnimationFrame.bind(window)
    : (callback) => window.setTimeout(() => callback(Date.now()), 16)

const cancelFrame =
  typeof window !== 'undefined' && window.cancelAnimationFrame
    ? window.cancelAnimationFrame.bind(window)
    : (id) => window.clearTimeout(id)

export default {
  name: 'CountTo',
  props: {
    startVal: {
      type: Number,
      default: 0
    },
    endVal: {
      type: Number,
      default: 2017
    },
    duration: {
      type: Number,
      default: 3000
    },
    autoplay: {
      type: Boolean,
      default: true
    },
    decimals: {
      type: Number,
      default: 0,
      validator(value) {
        return value >= 0
      }
    },
    decimal: {
      type: String,
      default: '.'
    },
    separator: {
      type: String,
      default: ','
    },
    prefix: {
      type: String,
      default: ''
    },
    suffix: {
      type: String,
      default: ''
    },
    useEasing: {
      type: Boolean,
      default: true
    },
    easingFn: {
      type: Function,
      default(t, b, c, d) {
        return c * (-Math.pow(2, (-10 * t) / d) + 1) * 1024 / 1023 + b
      }
    }
  },
  data() {
    return {
      localStartVal: this.startVal,
      displayValue: this.formatNumber(this.startVal),
      printVal: null,
      paused: false,
      localDuration: this.duration,
      startTime: null,
      timestamp: null,
      remaining: null,
      rAF: null
    }
  },
  computed: {
    countDown() {
      return this.startVal > this.endVal
    }
  },
  watch: {
    startVal() {
      if (this.autoplay) {
        this.start()
      }
    },
    endVal() {
      if (this.autoplay) {
        this.start()
      }
    }
  },
  mounted() {
    if (this.autoplay) {
      this.start()
    }
    this.$emit('mountedCallback')
  },
  beforeUnmount() {
    cancelFrame(this.rAF)
  },
  methods: {
    start() {
      this.localStartVal = this.startVal
      this.startTime = null
      this.localDuration = this.duration
      this.paused = false
      this.rAF = requestFrame(this.count)
    },
    pauseResume() {
      if (this.paused) {
        this.resume()
        this.paused = false
      } else {
        this.pause()
        this.paused = true
      }
    },
    pause() {
      cancelFrame(this.rAF)
    },
    resume() {
      this.startTime = null
      this.localDuration = +this.remaining
      this.localStartVal = +this.printVal
      this.rAF = requestFrame(this.count)
    },
    reset() {
      this.startTime = null
      cancelFrame(this.rAF)
      this.displayValue = this.formatNumber(this.startVal)
    },
    count(timestamp) {
      if (!this.startTime) {
        this.startTime = timestamp
      }
      this.timestamp = timestamp
      const progress = timestamp - this.startTime
      this.remaining = this.localDuration - progress

      if (this.useEasing) {
        if (this.countDown) {
          this.printVal =
            this.localStartVal -
            this.easingFn(progress, 0, this.localStartVal - this.endVal, this.localDuration)
        } else {
          this.printVal = this.easingFn(
            progress,
            this.localStartVal,
            this.endVal - this.localStartVal,
            this.localDuration
          )
        }
      } else if (this.countDown) {
        this.printVal =
          this.localStartVal -
          ((this.localStartVal - this.endVal) * progress) / this.localDuration
      } else {
        this.printVal =
          this.localStartVal + ((this.endVal - this.localStartVal) * progress) / this.localDuration
      }

      if (this.countDown) {
        this.printVal = this.printVal < this.endVal ? this.endVal : this.printVal
      } else {
        this.printVal = this.printVal > this.endVal ? this.endVal : this.printVal
      }

      this.displayValue = this.formatNumber(this.printVal)

      if (progress < this.localDuration) {
        this.rAF = requestFrame(this.count)
      } else {
        this.$emit('callback')
      }
    },
    isNumber(value) {
      return !isNaN(parseFloat(value))
    },
    formatNumber(num) {
      num = Number(num || 0).toFixed(this.decimals)
      const values = String(num).split('.')
      let integer = values[0]
      const decimal = values.length > 1 ? this.decimal + values[1] : ''
      const rgx = /(\d+)(\d{3})/

      if (this.separator && !this.isNumber(this.separator)) {
        while (rgx.test(integer)) {
          integer = integer.replace(rgx, '$1' + this.separator + '$2')
        }
      }

      return this.prefix + integer + decimal + this.suffix
    }
  }
}
</script>
