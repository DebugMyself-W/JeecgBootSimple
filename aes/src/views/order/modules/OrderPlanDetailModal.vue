<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row>
          <a-col :span="24">
            <a-form-item label="药品" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-select
                show-search
                v-decorator="['drugId', validatorRules.drugId]"
                placeholder="输入药品名称或代码查询"
                :default-active-first-option="true"
                :show-arrow="false"
                :filter-option="false"
                :not-found-content="null"
                @search="handleSearch">
                <a-select-option v-for="d in hospitalDrugs" :key="d.bisId">
                  {{ d.name }} - {{ d.code }} - {{ d.shortName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="数量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['qty', validatorRules.qty]" placeholder="请输入数量"></a-input>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </j-modal>
</template>

<script>

import { httpAction } from '@/api/manage'
import pick from 'lodash.pick'
import { validateDuplicateValue } from '@/utils/util'
import { getAction } from '../../../api/manage'

export default {
  name: 'OrderPlanDetailModal',
  props: {
    mainId: {
      type: String,
      required: false,
      default: ''
    },
    planNo: {
      type: String,
      required: false,
      default: ''
    }
  },
  data() {
    return {
      form: this.$form.createForm(this),
      title: '操作',
      width: 800,
      visible: false,
      model: {},
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
      },

      confirmLoading: false,
      validatorRules: {
        drugId: {
          rules: [
            { required: true },
            {
              validator: this.validatorDrug
            }
          ]
        },
        qty: {
          rules: [
            { required: true },
            { pattern: /^([1-9]\d{0,4}|1000)$/, message: '有效值为1-99999' }
          ]
        }
      },
      url: {
        add: '/order/orderPlan/addOrderPlanDetail',
        edit: '/order/orderPlan/editOrderPlanDetail'
      },
      hospitalDrugs: []
    }
  },
  created() {
  },
  methods: {
    //校验器
    validatorDrug(rule, value, callback){
      getAction('/order/orderPlan/validator', { 'drugId': value, 'planNo': this.planNo }).then((res) => {
        if(res.code ===500){
          callback(res.message)
        }
        callback()
      })
    },


    add() {
      this.edit({})
      //不搜索赋值option，无法关闭弹窗 =。=！
      this.handleSearch('甲状腺')
    },
    edit(record) {
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue(pick(this.model, 'createBy', 'createTime', 'updateBy', 'updateTime', 'planNo', 'drugId', 'qty', 'startTime', 'endTime', 'deleted'))
      })
    },
    close() {
      this.$emit('close')
      this.visible = false
    },
    handleOk() {
      const that = this
      // 触发表单验证
      this.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let httpurl = ''
          let method = ''
          if (!this.model.id) {
            httpurl += this.url.add
            method = 'post'
          } else {
            httpurl += this.url.edit
            method = 'put'
          }
          let formData = Object.assign(this.model, values)
          formData['planNo'] = this.planNo
          console.log('表单提交数据', formData)
          httpAction(httpurl, formData, method).then((res) => {
            if (res.success) {
              that.$message.success(res.message)
              that.$emit('ok')
            } else {
              that.$message.warning(res.message)
            }
          }).finally(() => {
            that.confirmLoading = false
            that.close()
          })
        }

      })
    },
    handleCancel() {
      this.close()
    },
    popupCallback(row) {
      this.form.setFieldsValue(pick(row, 'createBy', 'createTime', 'updateBy', 'updateTime', 'planNo', 'drugId', 'qty', 'startTime', 'endTime', 'deleted'))
    },
    handleSearch(value) {
      if (value) {
        this.getHosiptalDrug(value)
      }
    },
    getHosiptalDrug(keyword) {
      if (keyword.length > 1) {
        getAction('/order/orderPlan/search', { 'keyword': keyword, 'planNo': this.planNo }).then((res) => {
          this.hospitalDrugs = res.result
          console.log('搜索到药品信息' + this.hospitalDrugs.length + '条')
        })
      }
    }
  }
}
</script>
