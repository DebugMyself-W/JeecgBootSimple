<template>
  <div class="editable-cell">
    <div v-if="editable" class="editable-cell-input-wrapper">
      <a-input :value="value" type="number" @change="handleChange" @pressEnter="check"/>
      <a-icon
        type="check"
        class="editable-cell-icon-check"
        @click="check"
      />
    </div>
    <div v-else class="editable-cell-text-wrapper">
      {{ value || ' ' }}
      <a-icon type="edit" v-if="status.status === '待提交' || status.ignore===true" class="editable-cell-icon"
              @click="edit"/>
    </div>
  </div>
</template>

<script>
// import { httpAction } from '@/api/manage'
// import pick from 'lodash.pick'
// import { validateDuplicateValue } from '@/utils/util'

export default {
  name: 'EditableCell',
  components: {},
  props: {
    text: Number
  },
  inject: ['status'], //孙节点监听
  watch: {
    value() {
      console.log('监听到退货数量改变')
      let num = Number(this.value)
      if (isNaN(num)) {
        this.$message.error('请输入数字类型')
        this.value = this.text
        return false
      }
      if (num < 0 || num >= 100000) {
        this.$message.error('有效范围为1-100000')
        this.value = this.text
        return false
      }
    }
  },
  mounted() {
  },
  created() {

  },
  data() {
    return {
      value: this.text,
      editable: false
    }
  },
  methods: {
    handleChange(e) {
      const value = e.target.value
      this.value = Number(value)
    },
    check() {
      this.editable = false
      this.$emit('change', this.value)
    },
    edit() {
      this.editable = true
    }
  }
}
</script>
<style scoped>
.editable-cell {
  position: relative;
}

.editable-cell-input-wrapper,
.editable-cell-text-wrapper {
  padding-right: 24px;
}

.editable-cell-text-wrapper {
  padding: 5px 24px 5px 5px;
}

.editable-cell-icon,
.editable-cell-icon-check {
  position: absolute;
  right: 0;
  width: 20px;
  cursor: pointer;
}

.editable-cell-icon {
  line-height: 18px;
  display: none;
}

.editable-cell-icon-check {
  line-height: 28px;
}

.editable-cell:hover .editable-cell-icon {
  display: inline-block;
}

.editable-cell-icon:hover,
.editable-cell-icon-check:hover {
  color: #108ee9;
}

.editable-add-btn {
  margin-bottom: 8px;
}
</style>
