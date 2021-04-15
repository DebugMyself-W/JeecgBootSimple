<template>
  <a-card :bordered="false" :class="'cust-erp-sub-tab'">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item label="药品名称:">
              <a-input placeholder="药品名称" v-model="queryParam.drugName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item label="药品代码:">
              <a-input placeholder="药品代码" v-model="queryParam.drugCode"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item label="价格策略">
              <j-search-select-tag v-model="queryParam.priceStrategy" placeholder="价格策略" dict="base_price_strategy,name,name,deleted='0' and END_TIME is null"/>
            </a-form-item>
          </a-col>
          <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchSaleDetailReset" icon="reload" style="margin-left: 8px">重置</a-button>
            </a-col>
          </span>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->
    <!-- 操作按钮区域 -->
    <div class="table-operator" v-if="mainId">
      <a-button type="primary" icon="download" @click="handleExportXls('销售订单详情')">导出</a-button>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :scroll="{x:true}"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">

        <template slot="htmlSlot" slot-scope="text">
          <div v-html="text"></div>
        </template>
        <template slot="imgSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无图片</span>
          <img v-else :src="getImgView(text)" height="25px" alt="" style="max-width:80px;font-size: 12px;font-style: italic;"/>
        </template>
        <template slot="fileSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>
          <a-button
            v-else
            :ghost="true"
            type="primary"
            icon="download"
            size="small"
            @click="downloadFile(text)">
            下载
          </a-button>
        </template>

        <!-- <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical" />
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
            <a>删除</a>
          </a-popconfirm>
        </span> -->

      </a-table>
    </div>

    <orderSaleDetail-modal ref="modalForm" @ok="modalFormOk" :mainId="mainId"></orderSaleDetail-modal>
  </a-card>
</template>

<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import OrderSaleDetailModal from './modules/OrderSaleDetailModal'
  import JSearchSelectTag from '@/components/dict/JSearchSelectTag'


  export default {
    name: "OrderSaleDetailList",
    mixins:[JeecgListMixin],
    components: { OrderSaleDetailModal,JSearchSelectTag },
    props:{
      mainId:{
        type:String,
        default:'',
        required:false
      }
    },
    watch:{
      mainId:{
        immediate: true,
        handler(val) {
          if(!this.mainId){
            this.clearList()
          }else{
            this.queryParam['orderNo'] = val
            this.loadData(1);
          }
        }
      }
    },
    data () {
      return {
        description: '销售订单管理页面',
        disableMixinCreated:true,
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            width:60,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
          },
          {
            title:'销售订单号',
            align:"center",
            dataIndex: 'orderNo'
          },
          {
            title:'药品名称(物流)',
            align:"center",
            dataIndex: 'drugName',
          },
          ,
          {
            title:'药品代码(物流)',
            align:"center",
            dataIndex: 'drugCode',
          },
          {
            title:'实际开票价',
            align:"center",
            dataIndex: 'price'
          },
          {
            title:'核算成本价',
            align:"center",
            dataIndex: 'costPrice'
          },
          {
            title:'价格策略',
            align:"center",
            dataIndex: 'priceStrategy'
          },
          {
            title:'数量',
            align:"center",
            dataIndex: 'qty'
          },
          {
            title:'出入库单位(物流)',
            align:"center",
            dataIndex: 'storageUnit'
          }
        ],
        url: {
          list: "/order/orderSale/listOrderSaleDetailByMainId",
          delete: "/order/orderSale/deleteOrderSaleDetail",
          deleteBatch: "/order/orderSale/deleteBatchOrderSaleDetail",
          exportXlsUrl: "/order/orderSale/exportOrderSaleDetail",
          importUrl: "/order/orderSale/importOrderSaleDetail",
        },
        dictOptions:{
        },

      }
    },
    computed: {
      importExcelUrl(){
        return `${window._CONFIG['domianURL']}/${this.url.importUrl}/${this.mainId}`;
      }
    },
    methods: {
      searchSaleDetailReset(){
        Object.keys(this.queryParam).forEach(key=>{
          if(key!='orderNo'){
            this.queryParam[key]=""
          }
        })
        this.loadData(1);
      },
      clearList(){
        this.dataSource=[]
        this.selectedRowKeys=[]
        this.ipagination.current = 1
      },

    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>
