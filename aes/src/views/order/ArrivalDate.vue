<template>
  <div class="editable-cell">
    <div v-if="editable" class="editable-cell-input-wrapper">
      <a-date-picker :value="value"  @change="handleChange" @pressEnter="check"
                     :disabledDate="disabledDate"/>
    </div>
    <div v-else class="editable-cell-text-wrapper">
      {{ value || ' ' }}
      <a-icon  type="edit" class="editable-cell-icon" @click="edit"/>
    </div>
  </div>
</template>

<script>
/*生效时间不得早于当前时间*/
import moment from "moment"
export default {
  name: 'startTime',
  components: {},
  props: {
    obj: String
  },
  watch: {
    obj: {
      handler: function(val) {
        this.value = this.obj
      }
    }
  },

  data() {
    return {
      value: this.obj,
      editable: false
    }
  },
  methods: {
    disabledDate(current) {
      return current && current <moment().subtract(1, "days");
    },
    handleChange(date, dateString) {
      this.value = dateString
      this.editable = false
      /*发送给父组件*/
      this.$emit('change', this.value)
    },
    /*弃用*/
    check() {
      this.editable = false

    },
    edit() {
      this.editable = true
    }
  }
}
</script>

