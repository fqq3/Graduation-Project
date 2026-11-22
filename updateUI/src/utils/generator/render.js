import { h, resolveDynamicComponent } from 'vue'
import { makeMap } from '@/utils/index'
import {
  buildGeneratorLegacyIconVNode,
  generatorLegacyIconPresets
} from './icons'

const isAttr = makeMap(
  'accept,accept-charset,accesskey,action,align,alt,async,autocomplete,'
  + 'autofocus,autoplay,autosave,bgcolor,border,buffered,challenge,charset,'
  + 'checked,cite,class,code,codebase,color,cols,colspan,content,http-equiv,'
  + 'name,contenteditable,contextmenu,controls,coords,data,datetime,default,'
  + 'defer,dir,dirname,disabled,download,draggable,dropzone,enctype,method,for,'
  + 'form,formaction,headers,height,hidden,high,href,hreflang,http-equiv,'
  + 'icon,id,ismap,itemprop,keytype,kind,label,lang,language,list,loop,low,'
  + 'manifest,max,maxlength,media,method,GET,POST,min,multiple,email,file,'
  + 'muted,name,novalidate,open,optimum,pattern,ping,placeholder,poster,'
  + 'preload,radiogroup,readonly,rel,required,reversed,rows,rowspan,sandbox,'
  + 'scope,scoped,seamless,selected,shape,size,type,text,password,sizes,span,'
  + 'spellcheck,src,srcdoc,srclang,srcset,start,step,style,summary,tabindex,'
  + 'target,title,type,usemap,value,width,wrap'
)

function buildOptions(conf, tag, extraProps = {}) {
  return (conf.options || []).map((item) => h(tag, {
    label: item.label,
    value: item.value,
    disabled: item.disabled,
    ...extraProps
  }, () => item.label))
}

function normalizeSize(size) {
  if (size === 'mini') {
    return 'small'
  }
  if (size === 'medium') {
    return 'default'
  }
  return size
}

function parseSelectableRanges(options) {
  const selectableRange = options?.selectableRange
  const ranges = (Array.isArray(selectableRange) ? selectableRange : [selectableRange])
    .filter(Boolean)
    .map((range) => {
      const match = String(range).match(
        /^\s*(\d{1,2}):(\d{1,2})(?::(\d{1,2}))?\s*-\s*(\d{1,2}):(\d{1,2})(?::(\d{1,2}))?\s*$/
      )

      if (!match) {
        return null
      }

      return match.slice(1).map((value, index) => {
        if ((index === 2 || index === 5) && value === undefined) {
          return 0
        }
        return Number(value)
      })
    })
    .filter((range) => {
      if (!range || range.some((value) => Number.isNaN(value))) {
        return false
      }
      const [startHour, startMinute, startSecond, endHour, endMinute, endSecond] = range
      return (
        startHour >= 0 && startHour <= 23
        && endHour >= 0 && endHour <= 23
        && startMinute >= 0 && startMinute <= 59
        && endMinute >= 0 && endMinute <= 59
        && startSecond >= 0 && startSecond <= 59
        && endSecond >= 0 && endSecond <= 59
      )
    })

  return ranges.map(([startHour, startMinute, startSecond, endHour, endMinute, endSecond]) => [
    startHour * 3600 + startMinute * 60 + startSecond,
    endHour * 3600 + endMinute * 60 + endSecond
  ])
}

function isFullSelectableRange(ranges) {
  return ranges.some(([start, end]) => start <= 0 && end >= 86399)
}

function hasSelectableSecond(ranges, start, end) {
  return ranges.some(([rangeStart, rangeEnd]) => end >= rangeStart && start <= rangeEnd)
}

function buildTimePickerProps(conf) {
  const ranges = parseSelectableRanges(conf['picker-options'])

  if (!ranges.length || isFullSelectableRange(ranges)) {
    return {}
  }

  return {
    disabledHours: () => (
      Array.from({ length: 24 }, (_, hour) => hour)
        .filter((hour) => !hasSelectableSecond(ranges, hour * 3600, hour * 3600 + 3599))
    ),
    disabledMinutes: (hour) => (
      Array.from({ length: 60 }, (_, minute) => minute)
        .filter((minute) => {
          const start = hour * 3600 + minute * 60
          return !hasSelectableSecond(ranges, start, start + 59)
        })
    ),
    disabledSeconds: (hour, minute) => (
      Array.from({ length: 60 }, (_, second) => second)
        .filter((second) => {
          const current = hour * 3600 + minute * 60 + second
          return !hasSelectableSecond(ranges, current, current)
        })
    )
  }
}

function buildChildren(conf) {
  switch (conf.tag) {
    case 'el-button':
      return {
        default: () => {
          const children = []
          const iconNode = buildGeneratorLegacyIconVNode(conf.icon)
          if (iconNode) {
            children.push(iconNode)
          }
          if (conf.default) {
            children.push(conf.icon ? h('span', conf.default) : conf.default)
          }
          return children
        }
      }
    case 'el-input': {
      const slots = {}
      if (conf.prepend) slots.prepend = () => conf.prepend
      if (conf.append) slots.append = () => conf.append
      if (conf['prefix-icon']) slots.prefix = () => buildGeneratorLegacyIconVNode(conf['prefix-icon'])
      if (conf['suffix-icon']) slots.suffix = () => buildGeneratorLegacyIconVNode(conf['suffix-icon'])
      return slots
    }
    case 'el-select':
      return { default: () => buildOptions(conf, 'el-option') }
    case 'el-radio-group':
      return {
        default: () => (
          conf.optionType === 'button'
            ? buildOptions(conf, 'el-radio-button')
            : buildOptions(conf, 'el-radio', { border: conf.border })
        )
      }
    case 'el-checkbox-group':
      return {
        default: () => (
          conf.optionType === 'button'
            ? buildOptions(conf, 'el-checkbox-button')
            : buildOptions(conf, 'el-checkbox', { border: conf.border })
        )
      }
    case 'el-upload': {
      const slots = {
        default: () => {
          if (conf['list-type'] === 'picture-card') {
            return buildGeneratorLegacyIconVNode(generatorLegacyIconPresets.pictureCardAdd)
          }
          return h(resolveDynamicComponent('el-button'), {
            size: 'small',
            type: 'primary'
          }, {
            default: () => [
              buildGeneratorLegacyIconVNode(generatorLegacyIconPresets.uploadTrigger),
              h('span', conf.buttonText)
            ]
          })
        }
      }
      if (conf.showTip) {
        slots.tip = () => h(
          'div',
          { class: 'el-upload__tip' },
          `只能上传不超过 ${conf.fileSize}${conf.sizeUnit} 的${conf.accept}文件`
        )
      }
      return slots
    }
    default:
      return null
  }
}

export default {
  name: 'GeneratorRender',
  props: {
    conf: {
      type: Object,
      required: true
    }
  },
  emits: ['input'],
  render() {
    const confClone = JSON.parse(JSON.stringify(this.conf))
    const props = {}

    Object.keys(confClone).forEach((key) => {
      const val = confClone[key]
      if (key === 'vModel') {
        props.modelValue = confClone.defaultValue
        props['onUpdate:modelValue'] = (value) => this.$emit('input', value)
      } else if (key === 'size') {
        props.size = normalizeSize(val)
      } else if (key === 'style') {
        props.style = val
      } else if (key === 'class') {
        props.class = val
      } else if (confClone.tag === 'el-button' && key === 'icon') {
        return
      } else if (confClone.tag === 'el-input' && (key === 'prefix-icon' || key === 'suffix-icon')) {
        return
      } else if (confClone.tag === 'el-time-picker' && key === 'picker-options') {
        return
      } else if (!isAttr(key)) {
        props[key] = val
      } else {
        props[key] = val
      }
    })

    if (confClone.tag === 'el-time-picker') {
      Object.assign(props, buildTimePickerProps(confClone))
    }

    const component = resolveDynamicComponent(confClone.tag)
    const children = buildChildren(confClone)
    return h(component, props, children)
  }
}
