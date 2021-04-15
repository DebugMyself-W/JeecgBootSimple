<template>
  <a-card :bordered="false" :class="'cust-erp-sub-tab'">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item label="药品名称:">
              <a-input placeholder="药品名称/代码" v-model="queryParam.searchText"></a-input>
            </a-form-item>
          </a-col>
          <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReturnDetailReset" icon="reload"
                        style="margin-left: 8px">重置</a-button>
            </a-col>
          </span>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->
    <!-- 操作按钮区域 -->
    <div class="table-operator" v-if="mainId">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('物流规格信息')">导出</a-button>
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
        <template slot="qty" slot-scope="text, record">
          <editable-cell :text="text" @change="onCellChange(record.id, 'qty', $event)"/>
        </template>

        <template slot="reason" slot-scope="text, record">
          <j-dict-select-tag style="width: 120px" @input="changeRtReason(record)" v-model="record.reason"  :disabled="status!=='待提交'"
                             placeholder="请选择类型" dictCode="return_reason"/>
        </template>

        <template slot="htmlSlot" slot-scope="text">
          <div v-html="text"></div>
        </template>
        <template slot="imgSlot" slot-scope="text">
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
<!--                    <a @click="UpdateOrderReturn(record)">保存</a>-->
          <!--                    <a-divider type="vertical"/>-->
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
            <a v-if="status === '待提交'">删除</a>
          </a-popconfirm>
         </span>
      </a-table>
    </div>

    <orderReturnDetail-modal ref="modalForm" @ok="modalFormOk" :mainId="mainId" :refresh="updateList"></orderReturnDetail-modal>
  </a-card>
</template>

<script>
import {JeecgListMixin} from '@/mixins/JeecgListMixin'
import OrderReturnDetailModal from './modules/OrderReturnDetailModal'
import EditableCell from './modules/EditableCell'
import {initDictOptions} from "@comp/dict/JDictSelectUtil";
import {putAction} from "@api/manage";

export default {
  name: 'OrderReturnDetailList',
  mixins: [JeecgListMixin],
  components: {EditableCell, OrderReturnDetailModal},
  props: {
    mainId: {
      type: String,
      default: '',
      required: false
    }
  },
  watch: {
    mainId: {
      immediate: true,
      handler: function (val) {
        if (!this.mainId) {
          this.clearList()
        } else {
          this.clearList();
          this.queryParam['returnNo'] = val;
          this.loadData(1)
        }
      }
    }
  },
  data() {
    return {
      description: '退货单表管理页面',
      disableMixinCreated: true,
      pageNo: 1,   //当前页
      // 表头
      columns: [
        {
          title: '#',
          dataIndex: '',
          key: 'rowIndex',
          width: 60,
          align: 'center',
          customRender: function (t, r, index) {
            return parseInt(index) + 1
          }
        },
        // {
        //   title:'退货单',
        //   align:"center",
        //   dataIndex: 'returnNo'
        // },
        {
          title: '药品名称',
          align: 'center',
          dataIndex: 'drugId_dictText'
        },
        {
          title: '药品代码(物流)',
          align: 'center',
          dataIndex: 'drugCode'
        },
        {
          title: '单位',
          align: 'center',
          dataIndex: 'unit'
        },
        {
          title: '批号',
          align: 'center',
          dataIndex: 'batchNo'
        },
        {
          title: '实际开票价',
          align: 'center',
          dataIndex: 'price'
        },
        {
          title: '发货单号',
          align: 'center',
          dataIndex: 'distributionNo'
        },
        {
          title: '退货数量',
          align: 'center',
          dataIndex: 'qty',
          scopedSlots: {customRender: 'qty'},
        },
        {
          title: '退货原因',
          align: 'center',
          dataIndex: 'reason',
          scopedSlots: {customRender: 'reason'},
        },
        {
          title: 'OA审批状态',
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
        list: '/order/orderReturn/listOrderReturnDetailByMainId',
        delete: '/order/orderReturn/deleteOrderReturnDetail',
        deleteBatch: '/order/orderReturn/deleteBatchOrderReturnDetail',
        exportXlsUrl: '/order/orderReturn/exportOrderReturnDetail',
        importUrl: '/order/orderReturn/importOrderReturnDetail',
        edit: '/order/orderReturn/editOrderReturnDetail'
      },
      dictOptions: {
        type: [],
        drugType: []
      },
      sexDictOptions: [],
      status: ""
    }
  },
  computed: {
    importExcelUrl() {
      return `${window._CONFIG['domianURL']}/${this.url.importUrl}/${this.mainId}`
    }
  },
  methods: {
    searchReturnDetailReset() {
      Object.keys(this.queryParam).forEach(key => {
        if (key != 'returnNo') {
          this.queryParam[key] = ""
        }
      })
      this.loadData(1);
    },
    clearList() {
      this.dataSource = []
      this.selectedRowKeys = []
      this.ipagination.current = 1
    },
    onCellChange(key, dataIndex, value) {
      //debugger
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
        this.UpdateOrderReturn(target);
        this.$message.success("修改成功");
      }
    },
    changeRtReason(record) {
      if (this.status != "待提交"){
        this.$message.warning("该订单已提交,请勿修改信息！");
        return;
      }
      initDictOptions();  //获取数据字典数组值
      //获取id所对应的数据
      //let rtReason = this.sexDictOptions.find(item => item.value == record.reasonCode).text;
      let reason = this.sexDictOptions.find(item => item.value == record.reason).text;
      record.reasonCode = record.reason;
      record.reason = reason;
      this.UpdateOrderReturn(record);
      this.$message.success("修改成功");
    },
    //sexDictOptions 自行定义
    initDictConfig() {
      //初始化字典 - 性别
      initDictOptions('return_reason').then((res) => {
        if (res.success) {
          this.sexDictOptions = res.result;
        }
      });
    },
    UpdateOrderReturn(record) {
      let params = {};
      Object.assign(params, record);
      putAction(this.url.edit, params).then((res) => {
        if (res.success) {
          this.loadData(this.pageNo);
        }
        console.log(res);
      })
    },
    updateList() {
      console.log("detail...");
      this.clearList();
      this.loadData(this.pageNo);
    },
    setStatus(status) {
     // debugger
      this.status = status;
    }
  },
  created() {
    this.initDictConfig();
  }
}
</script>
<style scoped>
@import '~@assets/less/common.less';

</style>
