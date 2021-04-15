<template>
  <j-modal
    :title="title"
    :width="1200"
    :visible="visible"
    :maskClosable="false"
    switchFullscreen
    @ok="handleOk"
    :okButtonProps="{ class:{'jee-hidden': disableSubmit} }"
    @cancel="handleCancel">
    <distribution-form ref="realForm" @ok="submitCallback" :disabled="disableSubmit"/>
  </j-modal>
</template>

<script>
  import DistributionForm from './DistributionForm'
  import { postAction, getAction } from '@/api/manage'
  export default {
    name: 'Distribution',
    components: {
      DistributionForm
    },
    data() {
      return {
        title:'选择退货药品',
        width:800,
        visible: false,
        type: '',
        returnNo: '',
        disableSubmit: false,
        url: {
          add: "/order/orderDistribution/save",
        }
      }
    },
    methods:{
      init (type,returnNo){
        this.visible=true;
        this.type = type;
        this.returnNo = returnNo;
        console.log(returnNo);
      },
      add () {
        this.visible=true
        this.$nextTick(()=>{
          this.$refs.realForm.add();
        })
      },
      edit (record) {
        this.visible=true
        this.$nextTick(()=>{
          this.$refs.realForm.edit(record);
        })
      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
        let httpurl = '';
        httpurl+=this.url.add;
        this.$refs.realForm.selectionRows.forEach(item => {
          item.type = this.type;
          item.returnNo = this.returnNo;
        });

       postAction(httpurl,this.$refs.realForm.selectionRows).then((res)=>{
          if(res.success){
            this.$message.success("退货药品保存成功！");
            this.close();
          }
        });

      },
      submitCallback(){
        this.$emit('ok');
        this.visible = false;
      },
      handleCancel () {
        this.close()
      }
    }
  }
</script>