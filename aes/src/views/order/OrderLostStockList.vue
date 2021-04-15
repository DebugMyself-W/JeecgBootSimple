<template>
  <a-card :bordered="false" :class="'cust-erp-sub-tab'">
    <!-- 操作按钮区域 -->
    <div class="table-operator" v-if="mainId">
<!--      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>-->
      <a-button type="primary" icon="download" @click="handleExportXls('医院规格信息')">导出</a-button>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel">
            <a-icon type="delete"/>
            删除
          </a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作
          <a-icon type="down"/>
        </a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择
        <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table ref="table" size="middle" bordered rowKey="id" :scroll="{x:true}" :columns="columns"
               :dataSource="dataSource" :pagination="ipagination" :loading="loading"
               :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}" @change="handleTableChange">
        <template slot="ltQty" slot-scope="text, record,index" >
          <editable-cell  :text="text" @change="onCellChange(record.id, 'ltQty', $event)"/>
        </template>
        <!--2020-11-13 医院规格信息table的退货原因下拉框-->
        <template slot="ltReason" slot-scope="text, record">
          <j-dict-select-tag style="width: 120px" @input="changeRtReason(record)" v-model="record.ltReason" placeholder="请选择类型" dictCode="lost_reason" :disabled="status!=='待提交'"/>
        </template>
        <template slot="drug_photo" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无图片</span>
          <img v-else :src="getImgView(text)" height="25px" alt=""
               style="max-width:80px;font-size: 12px;font-style: italic;"/>
        </template>
        <template slot="fileSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>
          <a-button v-else :ghost="true" type="primary" icon="download" size="small" @click="downloadFile(text)">
            下载
          </a-button>
        </template>

        <span slot="action" slot-scope="text, record">
<!--                    <a @click="UpdateOrderReturnStock(record)">保存</a>-->
<!--                    <a-divider type="vertical"/>-->
                    <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                        <a v-if="status==='待提交'">删除</a>
                    </a-popconfirm>
                </span>
      </a-table>
    </div>
  </a-card>
</template>

<script>
import {JeecgListMixin} from '@/mixins/JeecgListMixin'
import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
import {initDictOptions, filterDictText} from '@/components/dict/JDictSelectUtil'
import EditableCell from './modules/EditableCell'
import {getAction,putAction} from "@api/manage";
import Vue from 'vue';

let vm = new Vue();

export default {
  name: 'OrderLostStockList',
  mixins: [JeecgListMixin],
  components: {EditableCell,JDictSelectTag},
  props: {
    mainId: {
      type: String,
      default: '',
      required: false
    },
    status:{
      type:String,
      required: false,
      default: ''
    }
  },
  watch: {
    mainId: {
      immediate: true,
      handler(val) {
        if (!this.mainId) {
          this.clearList()
        } else {
          this.queryParam['lostNo'] = val
          this.loadData(1)
        }
      }
    }
  },
  data() {
    return {
      description: '医院规格信息管理页面',
      disableMixinCreated: true,

      // 表头
      columns: [
        {
            title: '#',
            dataIndex: '',
            key: 'rowIndex',
            width: 60,
            align: 'center',
            customRender: function(t, r, index) {
                return parseInt(index) + 1
            }
        },
        // {
        //   title:'退货单',
        //   align:"center",
        //   dataIndex: 'returnNo'
        // },
        {
          title: '药品名称(医院)',
          align: 'center',
          dataIndex: 'drugDesc'
        },
        {
          title: '药品代码(医院)',
          align: 'center',
          dataIndex: 'drugCode',
          scopedSlots: {customRender: 'drugCode'},
        },
        {
          title: '入库单位(医院)',
          align: 'center',
          dataIndex: 'uomDesc'
        },
        {
          title: '批号(医院)',
          align: 'center',
          dataIndex: 'batchNo'
        },
        {
          title: '有效期至',
          align: 'center',
          dataIndex: 'startTime',
          scopedSlots: {customRender: 'startTime'},
          customRender:function (te){
            return vm.formatConversion(te);
          }
        },
        {
          title: '生产厂商',
          align: 'center',
          dataIndex: 'manufacturer'
        },
        {
          title: '批价',
          align: 'center',
          dataIndex: 'pp'
        },
        {
          title: '进价',
          align: 'center',
          dataIndex: 'rp'
        },
        {
          title: '售价',
          align: 'center',
          dataIndex: 'sp',
          scopedSlots: {customRender: 'sp'},
        },
        {
          title: '剂型',
          align: 'center',
          dataIndex: 'drugForm'
        },
        // {
        //   title: '发票号',
        //   align: 'center',
        //   dataIndex: 'invNo'
        // },
        // {
        //   title: '发票金额',
        //   align: 'center',
        //   dataIndex: 'invAmt'
        // },
        // {
        //   title: '发票日期',
        //   align: 'center',
        //   dataIndex: 'invDate',
        //   scopedSlots: {customRender: 'invDate'},
        // },
        {
          title: '漏货数量',
          align: 'center',
          dataIndex: 'ltQty',
          scopedSlots: {customRender: 'ltQty'}
        },
        {
          title: '漏货原因',
          align: 'center',
          dataIndex: 'ltReason',
          scopedSlots: {customRender: 'ltReason'},
        },
        // {
        //   title: '药品照片',
        //   align: 'center',
        //   dataIndex: 'drugPhoto'
        // },
        {
          title: '审批状态',
          align: 'center',
          dataIndex: 'oaStatus'
        },
        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',
          fixed: 'right',
          width: 147,
          scopedSlots: {customRender: 'action'}
        }
      ],
      url: {
        list: '/order/orderLostStock/list',
        edit: '/order/orderLostStock/edit',
        delete: '/order/orderLostStock/delete',
      },
      dictOptions: {
        type: [],
        drugType: []
      },
      hospitalDrugs: [],
      sexDictOptions:[],
      pageNo:0,   //当前页
      status:""
    }
  },
  created() {
    this.initDictConfig();
    this.$emit('func',true);
  },
  computed: {
    importExcelUrl() {
      return `${window._CONFIG['domianURL']}/${this.url.importUrl}/${this.mainId}`
    }
  },
  methods: {
    clearList() {
      this.dataSource = []
      this.selectedRowKeys = []
      this.ipagination.current = 1
    },
    onCellChange(key, dataIndex, value) {
      if (this.status != "待提交"){
        this.$message.warning("该订单已提交,请勿修改信息！");
        return;
      }
      let re = /^[1-9]+[0-9]*]*$/;
      const dataSource = [...this.dataSource]
      const target = dataSource.find(item => item.id === key)
      if (!re.test(value)) {
        this.$message.warning("请输入有效数字！");
      } else {
        if (target) {
          target[dataIndex] = value
          this.dataSource = dataSource
        }
        this.UpdateOrderReturnStock(target);
      }
    },
    handleChange(value) {
      this.value = value;
      if (value) {
        this.getHosiptalDrug(value);
      }

    },
    getHosiptalDrug(keyword) {
      getAction("/base/baseHospitalDrug/search", {'keyword': keyword}).then((res) => {
        this.hospitalDrugs = res.result;
      })
    },
    UpdateOrderReturnStock(record) {
      let params = {};
      Object.assign(params, record);
      putAction(this.url.edit, params).then((res) => {
        if (res.success){
          this.loadData(this.pageNo);
        }
      })
    },
    changeNum(value, record) {
      record.sp = value.data;
    },
    //2020-11-13 获取数据字典中的数组值，并查找下拉框中所选择的数据，赋值到该行的对象中
    changeRtReason(record){
      debugger;
      initDictOptions();  //获取数据字典数组值
      //获取id所对应的数据
      let ltReason = this.sexDictOptions.find(item => item.value == record.ltReason).text;
      record.ltReasonId = record.ltReason;
      record.ltReason = ltReason;
      this.UpdateOrderReturnStock(record);
    },
    //sexDictOptions 自行定义
    initDictConfig() {
      //初始化字典 - 性别
      initDictOptions('lost_reason').then((res) => {
        if (res.success) {
          this.sexDictOptions = res.result;
        }
      });
    },
    updateList(){
      this.clearList();
      this.loadData(this.pageNo);
    },
    setStatus(status){
      this.status = status;
    }
  }
}
</script>
<style scoped>
@import '~@assets/less/common.less';

</style>
