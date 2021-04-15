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
            <a-form-item label="采购组织" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['orgCode']" :trigger-change="true" dictCode="base_purchase_org,name,code,deleted = '0' and end_time is null" placeholder="请选择采购组织"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="门诊住院" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['inOutPatient']" :trigger-change="true" dictCode="in_out_patient" placeholder="请选择门诊住院"/>
            </a-form-item>
          </a-col>
          <!-- <a-col :span="24">
            <a-form-item label="耗材属性" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['consProp']" :trigger-change="true" dictCode="cons_prop" placeholder="请选择耗材属性"/>
            </a-form-item>
          </a-col> -->
          <a-col :span="24">
            <a-form-item label="是否外采" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['purchaseOut']" :trigger-change="true" dictCode="purchase_out"
                                 placeholder="请选择是否外采"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="是否紧急" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['urgent']" :trigger-change="true" dictCode="	true_false"
                                 placeholder="请选择是否紧急"/>
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
  import JDictSelectTag from "@/components/dict/JDictSelectTag"

  export default {
    name: "OrderPlanModal",
    components: {
      JDictSelectTag,
    },
    data () {
      return {
        form: this.$form.createForm(this),
        title:"操作",
        width:800,
        visible: false,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },

        confirmLoading: false,
        validatorRules: {
        },
        url: {
          add: "/order/orderPlan/add",
          edit: "/order/orderPlan/edit",
        }

      }
    },
    created () {
    },
    methods: {
      add () {
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'planNo','status','buyer','reviewer','reviewTime','orgCode','inOutPatient','consProp','dosageForm','purchaseOut','urgent'))
        })
      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
        const that = this;
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true;
            let httpurl = '';
            let method = '';
            if(!this.model.id){
              httpurl+=this.url.add;
              method = 'post';
            }else{
              httpurl+=this.url.edit;
               method = 'put';
            }
            let formData = Object.assign(this.model, values);
            console.log("表单提交数据",formData)
            httpAction(httpurl,formData,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
              }else{
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
              that.close();
            })
          }

        })
      },
      handleCancel () {
        this.close()
      },
      popupCallback(row){
        this.form.setFieldsValue(pick(row,'planNo','status','buyer','reviewer','reviewTime','orgCode','inOutPatient','consProp','dosageForm','purchaseOut','urgent'))
      },


    }
  }
</script>