<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="8">
            <a-form-item label="验收时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择验收时间" v-decorator="['checkinTime']" :trigger-change="true" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="验收人" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['checkinBy']" placeholder="请输入验收人"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="装箱号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['packageNo']" placeholder="请输入装箱号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="单位" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['unit']" placeholder="请输入单位"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="药品失效日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择药品失效日期" v-decorator="['drugEndDate']" :trigger-change="true" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="药品生产日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择药品生产日期" v-decorator="['drugStartDate']" :trigger-change="true" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="供应商" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['supplier']" placeholder="请输入供应商"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="质量状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['qualityStatus']" placeholder="请输入质量状态"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="批号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['batchNo']" placeholder="请输入批号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="实际开票价" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['price']" placeholder="请输入实际开票价" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="发货数量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['qty']" placeholder="请输入发货数量" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="药品" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['drugId']" placeholder="请输入药品"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="发货单号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['distributionNo']" placeholder="请输入发货单号"></a-input>
            </a-form-item>
          </a-col>
          <a-col v-if="showFlowSubmitButton" :span="24" style="text-align: center">
            <a-button @click="submitForm">提 交</a-button>
          </a-col>
        </a-row>
      </a-form>
    </j-form-container>
  </a-spin>
</template>

<script>

  import { httpAction, getAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import { validateDuplicateValue } from '@/utils/util'
  import JFormContainer from '@/components/jeecg/JFormContainer'
  import JDate from '@/components/jeecg/JDate'  

  export default {
    name: 'OrderDistributionForm',
    components: {
      JFormContainer,
      JDate,
    },
    props: {
      //流程表单data
      formData: {
        type: Object,
        default: ()=>{},
        required: false
      },
      //表单模式：true流程表单 false普通表单
      formBpm: {
        type: Boolean,
        default: false,
        required: false
      },
      //表单禁用
      disabled: {
        type: Boolean,
        default: false,
        required: false
      }
    },
    data () {
      return {
        form: this.$form.createForm(this),
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
          add: "/order/orderDistribution/add",
          edit: "/order/orderDistribution/edit",
          queryById: "/order/orderDistribution/queryById"
        }
      }
    },
    computed: {
      formDisabled(){
        if(this.formBpm===true){
          if(this.formData.disabled===false){
            return false
          }
          return true
        }
        return this.disabled
      },
      showFlowSubmitButton(){
        if(this.formBpm===true){
          if(this.formData.disabled===false){
            return true
          }
        }
        return false
      }
    },
    created () {
      //如果是流程中表单，则需要加载流程表单data
      this.showFlowData();
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
          this.form.setFieldsValue(pick(this.model,'checkinTime','checkinBy','packageNo','unit','drugEndDate','drugStartDate','supplier','qualityStatus','batchNo','price','qty','drugId','distributionNo'))
        })
      },
      //渲染流程表单数据
      showFlowData(){
        if(this.formBpm === true){
          let params = {id:this.formData.dataId};
          getAction(this.url.queryById,params).then((res)=>{
            if(res.success){
              this.edit (res.result);
            }
          });
        }
      },
      submitForm () {
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
            })
          }
         
        })
      },
      popupCallback(row){
        this.form.setFieldsValue(pick(row,'checkinTime','checkinBy','packageNo','unit','drugEndDate','drugStartDate','supplier','qualityStatus','batchNo','price','qty','drugId','distributionNo'))
      },
    }
  }
</script>