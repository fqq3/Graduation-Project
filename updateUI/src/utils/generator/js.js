import { exportDefault, titleCase } from '@/utils/index'
import { trigger } from './config'

const units = {
  KB: '1024',
  MB: '1024 / 1024',
  GB: '1024 / 1024 / 1024'
}
let confGlobal
const inheritAttrs = {
  file: '',
  dialog: 'inheritAttrs: false,'
}

function parseSelectableRanges(options) {
  const selectableRange = options?.selectableRange
  return (Array.isArray(selectableRange) ? selectableRange : [selectableRange])
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
    .map(([startHour, startMinute, startSecond, endHour, endMinute, endSecond]) => [
      startHour * 3600 + startMinute * 60 + startSecond,
      endHour * 3600 + endMinute * 60 + endSecond
    ])
}

export function makeUpJs(conf, type) {
  confGlobal = conf = JSON.parse(JSON.stringify(conf))
  const dataList = []
  const ruleList = []
  const optionsList = []
  const propsList = []
  const methodList = mixinMethod(type)
  const uploadVarList = []

  conf.fields.forEach(el => {
    buildAttributes(el, dataList, ruleList, optionsList, methodList, propsList, uploadVarList)
  })

  const script = buildexport(
    conf,
    type,
    hasLegacyIconDependency(conf.fields),
    dataList.join('\n'),
    ruleList.join('\n'),
    optionsList.join('\n'),
    uploadVarList.join('\n'),
    propsList.join('\n'),
    methodList.join('\n')
  )
  confGlobal = null
  return script
}

function hasLegacyIconDependency(fields = []) {
  return fields.some((field) => {
    const usesLegacyIcon = Boolean(
      field.icon
      || field['prefix-icon']
      || field['suffix-icon']
      || field.tag === 'el-upload'
    )

    return usesLegacyIcon || hasLegacyIconDependency(field.children || [])
  })
}

function buildAttributes(el, dataList, ruleList, optionsList, methodList, propsList, uploadVarList) {
  buildData(el, dataList)
  buildRules(el, ruleList)
  buildTimePickerDisabledMethods(el, methodList)

  if (el.options && el.options.length) {
    buildOptions(el, optionsList)
    if (el.dataType === 'dynamic') {
      const model = `${el.vModel}Options`
      const options = titleCase(model)
      buildOptionMethod(`get${options}`, model, methodList)
    }
  }

  if (el.props && el.props.props) {
    buildProps(el, propsList)
  }

  if (el.action && el.tag === 'el-upload') {
    uploadVarList.push(
      `${el.vModel}Action: '${el.action}',
      ${el.vModel}fileList: [],`
    )
    methodList.push(buildBeforeUpload(el))
    if (!el['auto-upload']) {
      methodList.push(buildSubmitUpload(el))
    }
  }

  if (el.children) {
    el.children.forEach(el2 => {
      buildAttributes(el2, dataList, ruleList, optionsList, methodList, propsList, uploadVarList)
    })
  }
}

function mixinMethod(type) {
  const list = []; const
    minxins = {
      file: confGlobal.formBtns ? {
        submitForm: `submitForm() {
        this.$refs['${confGlobal.formRef}'].validate(valid => {
          if(!valid) return
          // TODO 提交表单
        })
      },`,
        resetForm: `resetForm() {
        this.$refs['${confGlobal.formRef}'].resetFields()
      },`
      } : null,
      dialog: {
        onOpen: 'onOpen() {},',
        onClose: `onClose() {
        this.$refs['${confGlobal.formRef}'].resetFields()
      },`,
        close: `close() {
        this.$emit('update:modelValue', false)
        this.$emit('update:visible', false)
      },`,
        handelConfirm: `handelConfirm() {
        this.$refs['${confGlobal.formRef}'].validate(valid => {
          if(!valid) return
          this.close()
        })
      },`
      }
    }

  const methods = minxins[type]
  if (methods) {
    Object.keys(methods).forEach(key => {
      list.push(methods[key])
    })
  }

  return list
}

function buildData(conf, dataList) {
  if (conf.vModel === undefined) return
  let defaultValue
  if (typeof (conf.defaultValue) === 'string' && !conf.multiple) {
    defaultValue = `'${conf.defaultValue}'`
  } else {
    defaultValue = `${JSON.stringify(conf.defaultValue)}`
  }
  dataList.push(`${conf.vModel}: ${defaultValue},`)
}

function buildRules(conf, ruleList) {
  if (conf.vModel === undefined) return
  const rules = []
  if (trigger[conf.tag]) {
    if (conf.required) {
      const type = Array.isArray(conf.defaultValue) ? 'type: \'array\',' : ''
      let message = Array.isArray(conf.defaultValue) ? `请至少选择一个${conf.vModel}` : conf.placeholder
      if (message === undefined) message = `${conf.label}不能为空`
      rules.push(`{ required: true, ${type} message: '${message}', trigger: '${trigger[conf.tag]}' }`)
    }
    if (conf.regList && Array.isArray(conf.regList)) {
      conf.regList.forEach(item => {
        const pattern = stringifyPattern(item.pattern)
        if (pattern) {
          rules.push(`{ pattern: ${pattern}, message: '${item.message}', trigger: '${trigger[conf.tag]}' }`)
        }
      })
    }
    ruleList.push(`${conf.vModel}: [${rules.join(',')}],`)
  }
}

function buildTimePickerDisabledMethods(conf, methodList) {
  if (conf.tag !== 'el-time-picker' || !conf['picker-options']) {
    return
  }

  const ranges = parseSelectableRanges(conf['picker-options'])
  const hasCustomRange = ranges.length && !ranges.some(([start, end]) => start <= 0 && end >= 86399)

  if (!hasCustomRange) {
    return
  }

  const rangeCode = JSON.stringify(ranges)

  methodList.push(`${conf.vModel}DisabledHours() {
      const ranges = ${rangeCode}
      return Array.from({ length: 24 }, (_, hour) => hour).filter(hour => {
        const start = hour * 3600
        const end = start + 3599
        return !ranges.some(([rangeStart, rangeEnd]) => end >= rangeStart && start <= rangeEnd)
      })
    },`)
  methodList.push(`${conf.vModel}DisabledMinutes(hour) {
      const ranges = ${rangeCode}
      return Array.from({ length: 60 }, (_, minute) => minute).filter(minute => {
        const start = hour * 3600 + minute * 60
        const end = start + 59
        return !ranges.some(([rangeStart, rangeEnd]) => end >= rangeStart && start <= rangeEnd)
      })
    },`)
  methodList.push(`${conf.vModel}DisabledSeconds(hour, minute) {
      const ranges = ${rangeCode}
      return Array.from({ length: 60 }, (_, second) => second).filter(second => {
        const current = hour * 3600 + minute * 60 + second
        return !ranges.some(([rangeStart, rangeEnd]) => current >= rangeStart && current <= rangeEnd)
      })
    },`)
}

function stringifyPattern(pattern) {
  if (pattern instanceof RegExp) {
    return pattern.toString()
  }

  if (typeof pattern !== 'string') {
    return ''
  }

  const normalizedPattern = pattern.trim()
  if (!normalizedPattern) {
    return ''
  }

  const literalMatch = normalizedPattern.match(/^\/([\s\S]*)\/([dgimsuvy]*)$/)
  if (literalMatch) {
    const [, source, flags] = literalMatch

    try {
      // Validate the legacy regex literal before emitting it into generated code.
      new RegExp(source, flags)
      return normalizedPattern
    } catch (error) {
      return ''
    }
  }

  return `new RegExp(${JSON.stringify(normalizedPattern)})`
}

function buildOptions(conf, optionsList) {
  if (conf.vModel === undefined) return
  if (conf.dataType === 'dynamic') { conf.options = [] }
  const str = `${conf.vModel}Options: ${JSON.stringify(conf.options)},`
  optionsList.push(str)
}

function buildProps(conf, propsList) {
  if (conf.dataType === 'dynamic') {
    conf.valueKey !== 'value' && (conf.props.props.value = conf.valueKey)
    conf.labelKey !== 'label' && (conf.props.props.label = conf.labelKey)
    conf.childrenKey !== 'children' && (conf.props.props.children = conf.childrenKey)
  }
  const str = `${conf.vModel}Props: ${JSON.stringify(conf.props.props)},`
  propsList.push(str)
}

function buildBeforeUpload(conf) {
  const unitNum = units[conf.sizeUnit]; let rightSizeCode = ''; let acceptCode = ''; const
    returnList = []
  if (conf.fileSize) {
    rightSizeCode = `let isRightSize = file.size / ${unitNum} < ${conf.fileSize}
    if(!isRightSize){
      this.$message.error('文件大小超过 ${conf.fileSize}${conf.sizeUnit}')
    }`
    returnList.push('isRightSize')
  }
  if (conf.accept) {
    acceptCode = `let isAccept = new RegExp('${conf.accept}').test(file.type)
    if(!isAccept){
      this.$message.error('应该选择${conf.accept}类型的文件')
    }`
    returnList.push('isAccept')
  }
  const str = `${conf.vModel}BeforeUpload(file) {
    ${rightSizeCode}
    ${acceptCode}
    return ${returnList.join('&&')}
  },`
  return returnList.length ? str : ''
}

function buildSubmitUpload(conf) {
  const str = `submitUpload() {
    this.$refs['${conf.vModel}'].submit()
  },`
  return str
}

function buildOptionMethod(methodName, model, methodList) {
  const str = `${methodName}() {
    // TODO 发起请求获取数据
    this.${model}
  },`
  methodList.push(str)
}

function buildexport(conf, type, needsLegacyIcon, data, rules, selectOptions, uploadVar, props, methods) {
  const iconImport = needsLegacyIcon
    ? "import LegacyElIcon from '@/components/LegacyElIcon/index.vue'\n\n"
    : ''
  const inheritBlock = inheritAttrs[type]
    ? `  ${inheritAttrs[type]}\n`
    : ''
  const componentsBlock = needsLegacyIcon
    ? '  components: { LegacyElIcon },\n'
    : ''
  const str = `${iconImport}${exportDefault}{
${inheritBlock}${componentsBlock}  props: [],
  emits: ${type === 'dialog' ? "['update:modelValue', 'update:visible']" : '[]'},
  data () {
    return {
      ${conf.formModel}: {
        ${data}
      },
      ${conf.formRules}: {
        ${rules}
      },
      ${uploadVar}
      ${selectOptions}
      ${props}
    }
  },
  computed: {},
  watch: {},
  created () {},
  mounted () {},
  methods: {
    ${methods}
  }
}`
  return str
}
