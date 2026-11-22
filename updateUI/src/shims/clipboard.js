export default class ClipboardJS {
  constructor(selector, options = {}) {
    this.selector = selector
    this.options = options
    this.handlers = {}
    this.listener = (event) => {
      const target = event.target.closest(this.selector)
      if (!target) {
        return
      }
      try {
        const text = typeof this.options.text === 'function' ? this.options.text(target) : ''
        if (navigator.clipboard?.writeText) {
          navigator.clipboard.writeText(text)
        }
        this.handlers.success?.forEach((handler) => handler({ trigger: target }))
      } catch (error) {
        this.handlers.error?.forEach((handler) => handler(error))
      }
    }
    document.addEventListener('click', this.listener)
  }

  on(eventName, handler) {
    if (!this.handlers[eventName]) {
      this.handlers[eventName] = []
    }
    this.handlers[eventName].push(handler)
  }

  destroy() {
    document.removeEventListener('click', this.listener)
  }
}
