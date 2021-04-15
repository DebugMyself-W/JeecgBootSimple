<template>
  <div class="editable-cell">
    <div v-if="editable" class="editable-cell-input-wrapper">
      <a-input :value="value" @blur="handleChange" style="width: 65px"/>
    </div>
    <div v-else class="editable-cell-text-wrapper">
      {{ value || ' ' }}
      <a-icon type="edit" class="editable-cell-icon" @click="edit"/>
    </div>
  </div>
</template>

<script>
export default {
  name: 'EditSlot',
  props: {
    text: {
      type: String | Number,
      default: ''
    },
  },
  data() {
    return {
      value: this.text,
      editable: false
    }
  },

  methods: {
    handleChange(e) {
      this.value = e.target.value
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

</style>
