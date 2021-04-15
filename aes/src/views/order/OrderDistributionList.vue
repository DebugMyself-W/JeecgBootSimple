<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="物流药品名称:">
              <a-input placeholder="物流药品名称" v-model="queryParam.drugName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="装箱号:">
              <a-input placeholder="装箱号" v-model="queryParam.packageNo"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="批号:">
              <a-input placeholder="批号" v-model="queryParam.batchNo"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="发货单号:">
              <a-input placeholder="发货单号" v-model="queryParam.distributionNo"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="采购计划单号:">
              <a-input placeholder="采购计划单号" v-model="queryParam.planNo"></a-input>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :xl="8" :lg="7" :md="8" :sm="24">
              <a-form-item  label="生成日期:">
                <a-range-picker v-model="zhanwei1"
                                format="YYYY-MM-DD"
                                :placeholder="['开始时间', '结束时间']"
                                @change="onExistChange" />
              </a-form-item>
            </a-col>
            <a-col :xl="8" :lg="7" :md="8" :sm="24">
              <a-form-item  label="验收日期:">
                <a-range-picker v-model="zhanwei2"
                                format="YYYY-MM-DD"
                                :placeholder="['开始时间', '结束时间']"
                                @change="onCheckChange" />
              </a-form-item>
            </a-col>
          </template>
          <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a>
            </a-col>
          </span>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button type="primary" icon="download" @click="handleExportXls('发货单详情')">导出</a-button>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{
        selectedRowKeys.length }}</a>项
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
        class="j-table-force-nowrap"
        @change="handleTableChange">

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
         <a @click="handleDetail(record)">详情</a>
        </span>

      </a-table>
    </div>

    <order-distribution-modal ref="modalForm" @ok="modalFormOk"></order-distribution-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import OrderDistributionModal from './modules/OrderDistributionModal'
  import { filterObj } from '@/utils/util';

  export default {
    name: 'OrderDistributionList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      OrderDistributionModal
    },
    data() {
      return {
        zhanwei1:[],
        zhanwei2:[],
        description: '发货单详情管理页面',
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
          {
            title: '发货单号',
            align: 'center',
            dataIndex: 'distributionNo'
          },
          {
            title: '批号',
            align: 'center',
            dataIndex: 'batchNo'
          },
          {
            title: '药品',
            align: 'center',
            dataIndex: 'drugId_dictText'
          },
          {
            title: '装箱号',
            align: 'center',
            dataIndex: 'packageNo'
          },
          {
            title: '采购计划单号',
            align: 'center',
            dataIndex: 'planNo'
          },
          {
            title: '验收时间',
            align: 'center',
            dataIndex: 'checkinTime',
            customRender: function(text) {
              return !text ? '' : (text.length > 10 ? text.substr(0, 10) : text)
            }
          },
          {
            title: '验收人',
            align: 'center',
            dataIndex: 'checkinBy'
          },
          {
            title: '发货数量',
            align: 'center',
            dataIndex: 'qty'
          },
          {
            title: '单位',
            align: 'center',
            dataIndex: 'unit'
          },
          {
            title: '药品生产日期',
            align: 'center',
            dataIndex: 'drugStartDate',
            customRender: function(text) {
              return !text ? '' : (text.length > 10 ? text.substr(0, 10) : text)
            }
          },
          {
            title: '药品失效日期',
            align: 'center',
            dataIndex: 'drugEndDate',
            customRender: function(text) {
              return !text ? '' : (text.length > 10 ? text.substr(0, 10) : text)
            }
          },
          {
            title: '生成时间',
            align: 'center',
            dataIndex: 'createTime',
            customRender: function(text) {
              return !text ? '' : (text.length > 10 ? text.substr(0, 10) : text)
            }
          },
          {
            title: '供应商',
            align: 'center',
            dataIndex: 'supplier'
          },
          {
            title: '质量状态',
            align: 'center',
            dataIndex: 'qualityStatus'
          },

          {
            title: '实际开票价',
            align: 'center',
            dataIndex: 'price'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align: 'center',
            fixed: 'right',
            width: 147,
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: '/order/orderDistribution/page',
          delete: '/order/orderDistribution/delete',
          deleteBatch: '/order/orderDistribution/deleteBatch',
          exportXlsUrl: '/order/orderDistribution/exportXls',
          importExcelUrl: 'order/orderDistribution/importExcel'

        },
        dictOptions: {}
      }
    },
    created() {
    },
    computed: {
      importExcelUrl: function() {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
      },
    },
    methods: {
      getQueryParams(){
        //高级查询器
        let sqp = {}
        if(this.superQueryParams){
          sqp['superQueryParams']=encodeURI(this.superQueryParams)
          sqp['superQueryMatchType'] = this.superQueryMatchType
        }
        var param = Object.assign(sqp, this.queryParam, this.isorter ,this.filters);

        param.field = this.getQueryField();
        param.pageNo = this.ipagination.current;
        param.pageSize = this.ipagination.pageSize;
        return filterObj(param);
      },
      onExistChange(date, dateString) {
        this.queryParam.existStart=dateString[0]
        this.queryParam.existEnd=dateString[1]
      },
      onCheckChange(date, dateString) {
        this.queryParam.start=dateString[0]
        this.queryParam.end=dateString[1]
      },
      initDictConfig() {
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>