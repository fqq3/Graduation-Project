import mitt from 'mitt'

const emitter = mitt()

const global = {
  $on: emitter.on,
  $off: emitter.off,
  $emit: emitter.emit,
  on: emitter.on,
  off: emitter.off,
  emit: emitter.emit
}

export default global
