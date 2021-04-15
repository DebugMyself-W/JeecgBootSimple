<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="48">
          <a-col :md="8" :sm="24" >
            <a-form-item label="药品名称">
              <a-input v-model="queryParam.drugName" placeholder="请输入药品名称"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24" >
            <a-form-item label="药品批号">
              <a-input v-model="queryParam.batchNo" placeholder="请输入批号数字"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24" >
            <a-form-item label="发货单号" >
              <a-input v-model="queryParam.distributionNo" placeholder="请输入发货单号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24" >
            <a-form-item label="验收时间">
              <a-date-picker v-model="queryParam.startTime" style="width: 100%" placeholder="请选择开始时间"/>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24" >
            <a-form-item label="~">
              <a-date-picker v-model="queryParam.endTime" style="width: 100%" placeholder="请选择结束时间"/>
            </a-form-item>
          </a-col>
          <a-col :md="7" :sm="8">
              <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        size="middle"
        :scroll="{x:true}"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        :customRow="clickThenSelect"
        class="j-table-force-nowrap"
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

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="handleDetail(record)">详情</a>
              </a-menu-item>
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: 'DistributionForm',
    mixins:[JeecgListMixin, mixinDevice],
    components: {

    },
    data () {
      return {
        description: '发货单详情管理页面',
        // 查询参数
        queryParam: {},
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
            title:'药品批号',
            align:"center",
            dataIndex: 'distributionDetail.batchNo'
          },
          {
            title:'发货单号',
            align:"center",
            dataIndex: 'distributionDetail.distributionNo'
          },
          {
            title:'药品名称',
            align:"center",
            dataIndex: 'drugName'
          },
          {
            title:'医院药品编号',
            align:"center",
            dataIndex: 'yiBaoCode'
          },
          {
            title:'生产厂家',
            align:"center",
            dataIndex: 'manufacturer'
          },
          {
            title:'单位',
            align:"center",
            dataIndex: 'distributionDetail.unit'
          },
          {
            title:'实际开票价',
            align:"center",
            dataIndex: 'distributionDetail.price'
          },
          {
            title:'验收数量',
            align:"center",
            dataIndex: 'distributionDetail.checkInNum'
          },
          {
            title:'采购总金额',
            align:"center",
            dataIndex: 'distributionDetail.totalPrice'
          },
          {
            title:'验收日期',
            align:"center",
            dataIndex: 'distributionDetail.checkinTime',
            customRender:function (text) {
              return !text?"":(text.length>10?text.substr(0,10):text)
            }
          },
          {
            title:'生产日期',
            align:"center",
            dataIndex: 'distributionDetail.drugStartTime',
            customRender:function (text) {
              return !text?"":(text.length>10?text.substr(0,10):text)
            }
          },
          {
            title:'有效期至',
            align:"center",
            dataIndex: 'distributionDetail.drugEndDate',
            customRender:function (text) {
              return !text?"":(text.length>10?text.substr(0,10):text)
            }
          }
        ],
        url: {
          list: "/order/orderDistribution/list",
          delete: "/order/orderDistribution/delete",
          deleteBatch: "/order/orderDistribution/deleteBatch",
          exportXlsUrl: "/order/orderDistribution/exportXls",
          importExcelUrl: "order/orderDistribution/importExcel",

        },
        dictOptions:{},
      }
    },
    created() {
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      },
    },
    methods: {
      initDictConfig(){
      },
      clickThenSelect(record) {
        return {
          on: {
            click: () => {
              this.onSelectChange(record.id.split(","), [record]);
            }
          }
        }
      },
      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedMainId = selectionRows[0].id
        this.selectedRowKeys = selectedRowKeys;
        this.selectionRows = selectionRows;
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>