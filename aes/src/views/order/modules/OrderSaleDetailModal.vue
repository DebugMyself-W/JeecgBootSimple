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
            <a-form-item label="销售订单号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['orderNo']" placeholder="请输入销售订单号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="药品" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['drugId']" :trigger-change="true" dictCode="base_logistics_drug,name,bis_id,deleted=0 and end_time is null and active = '1' " placeholder="请选择药品"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="实际开票价" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['price']" placeholder="请输入实际开票价"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="核算成本价" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['costPrice']" placeholder="请输入核算成本价"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="价格策略" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['priceStrategy']" placeholder="请输入价格策略"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="数量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['qty']" placeholder="请输入数量"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="单位" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['unit']" placeholder="请输入单位"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="单价系数" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['priceRatio']" placeholder="请输入单价系数"></a-input>
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
    name: "OrderSaleDetailModal",
    components: {
      JDictSelectTag,
    },
    props:{
      mainId:{
        type:String,
        required:false,
        default:''
      }
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
          add: "/order/orderSale/addOrderSaleDetail",
          edit: "/order/orderSale/editOrderSaleDetail",
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
          this.form.setFieldsValue(pick(this.model,'createBy','createTime','updateBy','updateTime','orderNo','drugId','price','costPrice','priceStrategy','qty','unit','priceRatio','deleted'))
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
            formData['orderNo'] = this.mainId
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
        this.form.setFieldsValue(pick(row,'createBy','createTime','updateBy','updateTime','orderNo','drugId','price','costPrice','priceStrategy','qty','unit','priceRatio','deleted'))
      },


    }
  }
</script>
