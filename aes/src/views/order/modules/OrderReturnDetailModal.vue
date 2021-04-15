<template>
  <div>
    <j-modal :title="title" :width="width" :visible="visible" :confirmLoading="confirmLoading" switchFullscreen
             @ok="handleOk" @cancel="handleCancel" cancelText="关闭">
      <a-spin :spinning="confirmLoading">
        <a-form :form="form">
          <a-row :gutter="24">
            <a-col :span="10" :lg="7" :md="8" :sm="24">
              <a-form-item label="" :labelCol="labelCol" :wrapperCol="wrapperCol">
                <a-input @change="onChange" placeholder="药品名称/药品代码/生产厂商"></a-input>
              </a-form-item>
            </a-col>
            <a-col :span="1" >
              <a-button class="searchBtn" icon="search" @click="list">查询</a-button>
            </a-col>
          </a-row>
          <!-- <a-row>
          <a-col :span="8">
              <a-form-item label="验收时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-range-picker @change="onChange" />
              </a-form-item>
          </a-col>
          <a-col :span="8">

          </a-col>
          <a-col :span="8">
              <a-button class="searchBtn" icon="search">查询</a-button>
          </a-col>
      </a-row> -->
        </a-form>
        <a-table
          ref="table"
          size="middle"
          bordered
          rowKey="id"
          :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectClicked, type:'radio'}"
          :dataSource="dataSource"
          :pagination="ipagination"
          :columns="columns"
          :loading="loading"
          :data-source="data" bordered
          :scroll="{ x:1600,y: 250 }"
          @change="handleTableChangePositionAndValue">
          <template slot="title">
            <strong>库存药品清单</strong>
          </template>
        </a-table>
      </a-spin>
    </j-modal>
    <a-modal v-model="visible2" title="选择药品批次" @ok="handleOk2" width="1200px">
      <a-table :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange, type:'radio'}"
               :columns="columns2" :data-source="data2" bordered :scroll="{ x:2600,y: 250 }">
        <template slot="title">
          <strong>药品批次信息</strong>
        </template>
      </a-table>
    </a-modal>
  </div>
</template>

<script>
import {JeecgListMixin} from '@/mixins/JeecgListMixin'
import {getAction, httpAction,postAction} from '@/api/manage'
import pick from 'lodash.pick'
import {validateDuplicateValue} from '@/utils/util'

export default {
  name: 'OrderReturnDetailModal',
  mixins: [JeecgListMixin],
  components: {},
  props: {
    mainId: {
      type: String,
      required: false,
      default: '',
    },
    refresh: {
      type: Function,
      default: null
    }

  },
  data() {
    return {
      form: this.$form.createForm(this),
      title: '操作',
      width: 1200,
      visible: false,
      visible2: false,
      model: {},
      labelCol: {
        xs: {span: 24},
        sm: {span: 5}
      },
      wrapperCol: {
        xs: {span: 24},
        sm: {span: 16}
      },


      confirmLoading: false,
      validatorRules: {},
      url: {
        add: '/order/orderReturn/addOrderReturnDetail',
        edit: '/order/orderReturn/editOrderReturnDetail',
        list: '/order/orderReturnStock/drugList',
        addDrugInfo:'/order/orderReturnStock/add'
      },
      columns: [
        {
          title: '医院药品代码',
          align: 'center',
          width: 100,
          dataIndex: 'code'
        },
        {
          title: '医院药品名称',
          align: 'center',
          width: 200,
          dataIndex: 'name'
        },
        {
          title: '规格',
          align: 'center',
          width: 100,
          dataIndex: 'spec'
        },
        {
          title: '产地',
          align: 'center',
          width: 150,
          dataIndex: 'company'
        },
        {
          title: '入库单位',
          align: 'center',
          width: 150,
          dataIndex: 'minUnit'
        },
        {
          title: '售价(入库单位)',
          align: 'center',
          width: 150,
          dataIndex: 'retailPrice'
        },
        {
          title: '药品仓库库存数量',
          align: 'center',
          width: 150,
          dataIndex: 'drugNumber'
        }
        // ,
        // {
        //     title: '剂型',
        //     align: 'center',
        //     width: 150,
        //     dataIndex: 'drugType',
        // }
      ],
      /* 分页参数 */
      ipagination: {
        current: 1,
        pageSize: 10,
        pageSizeOptions: ['5', '10', '50'],
        showTotal: (total, range) => {
          return range[0] + "-" + range[1] + " 共" + total + "条"
        },
        showQuickJumper: true,
        showSizeChanger: true,
        total: 0
      },
      data: [],
      searchText: '',
      selectedMainId: '',
      selectedRowKeys: [],
      selectionRows: [],
      columns2: [
        {
          title: '入库日期',
          align: 'center',
          width: 150,
          dataIndex: 'checkinDate'
        },
        {
          title: '产地',
          align: 'center',
          width: 150,
          dataIndex: 'mnf'
        },
        {
          title: '批号',
          align: 'center',
          width: 150,
          dataIndex: 'batchNo'
        },
        {
          title: '有效期至',
          align: 'center',
          width: 150,
          dataIndex: 'expDate'
        },
        {
          title: '入库数',
          align: 'center',
          width: 150,
          dataIndex: 'recQty'
        },
        {
          title: '入库单位',
          align: 'center',
          width: 150,
          dataIndex: 'unitDesc'
        },
        // {
        //   title: '库存数',
        //   align: 'center',
        //   width: 150,
        //   dataIndex: ''
        // },
        {
          title: '批价',
          align: 'center',
          width: 150,
          dataIndex: 'wholesalePrice'
        },
        {
          title: '进价',
          align: 'center',
          width: 150,
          dataIndex: 'rp'
        },
        {
          title: '售价',
          align: 'center',
          width: 150,
          dataIndex: 'sp'
        },
        {
          title: '剂型',
          align: 'center',
          width: 150,
          dataIndex: 'drugFrom'
        },
        {
          title: '发票号',
          align: 'center',
          width: 150,
          dataIndex: 'invNo'
        },
        {
          title: '发票金额',
          align: 'center',
          width: 150,
          dataIndex: 'invAmt'
        },
        {
          title: '发票日期',
          align: 'center',
          width: 150,
          dataIndex: 'invDate'
        }
      ],
      data2: []
    }
  },
  computed: {
    // rowSelection() {
    //     return {
    //         onChange: (selectedRowKeys, selectedRows) => {
    //             this.selectedRows = selectedRows
    //              console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows)
    //         }
    //     }
    // }
  },
  created() {
    this.list()
  },
  methods: {
    onSelectChange(selectedRowKeys, selectionRows) {
      console.log(selectedRowKeys)
      console.log(selectionRows)
      this.selectedMainId = selectionRows[0].id
      this.selectedRowKeys = selectedRowKeys
      this.selectionRows = selectionRows
    },
    onSelectClicked(selectedRowKeys, selectionRows) {
      console.log(selectedRowKeys)
      console.log(selectionRows)
      this.selectedMainId = selectionRows[0].id
      this.selectedRowKeys = selectedRowKeys
      this.selectionRows = selectionRows
      this.handleOk();
    },
    handleTableChangePositionAndValue(pagination, filters, sorter) {
      //分页、排序、筛选变化时触发
      if (Object.keys(sorter).length > 0) {
        this.isorter.column = sorter.field;
        this.isorter.order = "ascend" == sorter.order ? "asc" : "desc"
      }
      this.ipagination = pagination;
      this.list();
    },

    list(arg) {
      let params = {
        searchText: this.searchText,
        returnNo: this.mainId,
        pageNo: this.ipagination.current,
        pageSize: this.ipagination.pageSize
      }

      if (arg === 1) {
        this.ipagination.current = 1;
      }
      this.onClearSelected();
      this.loading = true;
      // getAction('/base/baseHospitalDrug/list', params).then(res => {
      //     this.data = res.result.records
      // })
      getAction(this.url.list, params).then(res => {
        //this.data = res.result.records;
        this.dataSource = res.result.records;
        this.ipagination.total = res.result.total;
      });
      this.loading = false;
    },
    onChange(value) {
      this.searchText = value.target.value
    },
    add() {
      this.edit({})
    },
    edit(record) {
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue(
          pick(
            this.model,
            'createBy',
            'createTime',
            'updateBy',
            'updateTime',
            'returnNo',
            'drugId',
            'qty',
            'unit',
            'batchNo',
            'price',
            'deleted',
            'startTime',
            'endTime',
            'distributionNo',
            'reasonCode',
            'oaStatus',
            'bisId'
          )
        )
      })
    },
    close() {
      this.$emit('close')
      this.visible = false
    },
    handleOk() {
      // this.visible = false;
      let params = {
        id: this.selectedMainId
      }
      getAction('/order/orderReturn/hospital/drug', params).then(res => {
        this.data2 = res.result;
        this.visible2 = true
      })

      // const that = this
      // // 触发表单验证
      // this.form.validateFields((err, values) => {
      //     if (!err) {
      //         that.confirmLoading = true
      //         let httpurl = ''
      //         let method = ''
      //         if (!this.model.id) {
      //             httpurl += this.url.add
      //             method = 'post'
      //         } else {
      //             httpurl += this.url.edit
      //             method = 'put'
      //         }
      //         let formData = Object.assign(this.model, values)
      //         formData['returnNo'] = this.mainId
      //         console.log('表单提交数据', formData)
      //         httpAction(httpurl, formData, method)
      //             .then(res => {
      //                 if (res.success) {
      //                     that.$message.success(res.message)
      //                     that.$emit('ok')
      //                 } else {
      //                     that.$message.warning(res.message)
      //                 }
      //             })
      //             .finally(() => {
      //                 that.confirmLoading = false
      //                 that.close()
      //             })
      //     }
      // })
    },
    handleOk2() {
      //2020-11-12 新增添加药品到退货单详情功能
      // this.visible = false;
      //定义一个参数对象
      let params = {};
      //将入库Id存入ingri字段中，并去除id字段的数值
      let inGri = {
        ingri : this.selectionRows[0].id,
        returnNo : this.mainId
      };
      //合并对象
      Object.assign(params,this.selectionRows[0]);
      Object.assign(params,inGri);
      params.id = "";
      //发送请求
      postAction(this.url.addDrugInfo, params).then(res => {
        if (res.success){
          this.data2 = res.result;
          this.visible2 = false;
          this.refresh();
        }else {
          this.$message.warning(res.message);
        }

      })
    },
    handleCancel() {
      this.close()
    },
    popupCallback(row) {
      this.form.setFieldsValue(
        pick(
          row,
          'createBy',
          'createTime',
          'updateBy',
          'updateTime',
          'returnNo',
          'drugId',
          'qty',
          'unit',
          'batchNo',
          'price',
          'deleted',
          'startTime',
          'endTime',
          'distributionNo',
          'reasonCode',
          'oaStatus',
          'bisId'
        )
      )
    },
    onClearSelected() {
      this.selectedRowKeys = [];
      this.selectionRows = [];
      this.selectedMainId = ''
    },
  }
}
</script>
<style scoped>
.searchBtn {
  float: right;
  margin-right: 48px;
}
</style>
