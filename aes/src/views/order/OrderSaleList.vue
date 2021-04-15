<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="销售订单号">
              <a-input placeholder="销售订单号" v-model="queryParam.orderNo"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="采购计划:">
              <a-input placeholder="采购计划" v-model="queryParam.planNo"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="发货单号">
              <a-input placeholder="发货单号" v-model="queryParam.distributionNo"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="HIS入库单号">
              <a-input placeholder="HIS入库单号" v-model="queryParam.hisCheckinNo"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="发票号:">
              <a-input placeholder="发票号" v-model="queryParam.invoiceNo"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item label="门诊住院">
              <j-dict-select-tag v-model="queryParam.inOutPatient" placeholder="请选择门诊住院" dictCode="in_out_patient"/>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="发货单生成日期:">
                <a-range-picker v-model="queryParam.zhanwei1"
                                format="YYYY-MM-DD"
                                :placeholder="['开始时间', '结束时间']"
                                @change="onDistributionChange"/>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="销售订单生成日期:">
                <a-range-picker v-model="queryParam.zhanwei2"
                                format="YYYY-MM-DD"
                                :placeholder="['开始时间', '结束时间']"
                                @change="onSaleChange"/>
              </a-form-item>
            </a-col>

            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="HIS入库日期:">
                <a-range-picker v-model="queryParam.zhanwei3"
                                format="YYYY-MM-DD"
                                :placeholder="['开始时间', '结束时间']"
                                @change="onHisChange"/>
              </a-form-item>
            </a-col>
          </template>
          <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-button type="primary" @click="search" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" style="margin-left: 8px">重置</a-button>
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
      <a-button type="primary" icon="download" @click="handleExportXls('销售订单')">导出</a-button>
      <a-radio-group :value="statusType" @change="statisticst" v-model="statusType">
        <a-radio-button value="searchResetVal" @click="searchReset">全部</a-radio-button>
        <a-radio-button value="waitShippedVal" @click="waitShipped">待发货</a-radio-button>
        <a-radio-button value="waitCheckVal" @click="waitCheck">待验收</a-radio-button>
        <a-radio-button value="hasCheckedVal" @click="hasChecked">已验收</a-radio-button>
      </a-radio-group>
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
        bordered
        rowKey="id"
        class="j-table-force-nowrap"
        :scroll="{x:true}"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange, type:'radio'}"
        :customRow="clickThenSelect"
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
          <a v-if="record.distributionNo!=null" v-show="record.packageUncheckNum==0" style="color:blue" @click="toHis(record)">跳转入库页面</a>
          <a v-if="record.distributionNo!=null" v-show="record.packageUncheckNum>0" style="color:red" @click="toHis(record)">跳转入库页面</a>
        </span>

      </a-table>
    </div>

    <a-tabs defaultActiveKey="1">
      <a-tab-pane tab="销售订单详情" key="1">
        <OrderSaleDetailList :mainId="selectedMainId"/>
      </a-tab-pane>
    </a-tabs>

    <orderSale-modal ref="modalForm" @ok="modalFormOk"></orderSale-modal>
  </a-card>
</template>

<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import OrderSaleModal from './modules/OrderSaleModal'
  import { getAction } from '@/api/manage'
  import OrderSaleDetailList from './OrderSaleDetailList'
  import '@/assets/less/TableExpand.less'
  import { filterObj } from '@/utils/util'

  export default {
    name: 'OrderSaleList',
    mixins: [JeecgListMixin],
    components: {
      OrderSaleDetailList,
      OrderSaleModal
    },
    data() {
      return {
        statusType: 'waitCheckVal',
        description: '销售订单管理页面',
        // 表头
        columns: [
          {
            title: '销售订单号',
            align: 'center',
            dataIndex: 'orderNo'
          },
          {
            title: '类型',
            align: 'center',
            dataIndex: 'inOutPatient'
          },
          {
            title: '销售订单生成日期',
            align: 'center',
            dataIndex: 'createTime'
          },
          {
            title: '采购计划',
            align: 'center',
            dataIndex: 'planNo'
          },
          {
            title: '发货单号',
            align: 'center',
            dataIndex: 'distributionNo'
          },
          {
            title: '发货单生成日期',
            align: 'center',
            dataIndex: 'distributionTime'
          },
          {
            title: 'HIS入库单号',
            align: 'center',
            dataIndex: 'hisCheckinNo'
          },
          {
            title: 'HIS入库日期',
            align: 'center',
            dataIndex: 'hisCheckinTime'
          },
          {
            title: '发票号',
            align: 'center',
            dataIndex: 'invoiceNo'
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
          list: '/order/orderSale/list',
          delete: '/order/orderSale/delete',
          deleteBatch: '/order/orderSale/deleteBatch',
          exportXlsUrl: '/order/orderSale/exportXls',
          importExcelUrl: 'order/orderSale/importExcel'
        },
        dictOptions: {},
        /* 分页参数 */
        ipagination: {
          current: 1,
          pageSize: 5,
          pageSizeOptions: ['5', '10', '50'],
          showTotal: (total, range) => {
            return range[0] + '-' + range[1] + ' 共' + total + '条'
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        selectedMainId: '',
      }
    },
    created() {

    },
    computed: {
      importExcelUrl: function() {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
      }
    },
    methods: {
      statisticst(){
        this.$message.info('切换')
        console.log("qiehuan")
      },
      searchReset(){
        //debugger
        Object.keys(this.queryParam).forEach(key=>{
          debugger
          this.queryParam[key]=""
        })
        this.statusType = "searchResetVal"
        this.queryParam.waitShipped = false
        this.queryParam.waitCheck = false
        this.queryParam.hasChecked = false
        this.dataSource = [];
        this.loadData();
      },
      search() {
        // this.queryParam.waitShipped = false
        // this.queryParam.waitCheck = false
        // this.queryParam.hasChecked = false
        this.searchQuery()
      },
      waitShipped() {
        this.statusType="waitShippedVal";
        this.queryParam.waitShipped = true
        this.queryParam.waitCheck = false
        this.queryParam.hasChecked = false
        this.searchQuery()
      },
      waitCheck() {
        this.statusType="waitCheckVal";
        this.queryParam.waitShipped = false
        this.queryParam.waitCheck = true
        this.queryParam.hasChecked = false
        this.searchQuery()
      },
      hasChecked() {
        this.statusType="hasCheckedVal";
        this.queryParam.waitShipped = false
        this.queryParam.waitCheck = false
        this.queryParam.hasChecked = true
        this.searchQuery()
      },
      searchQuery() {
        this.loadData(1)
      },
      getQueryParams() {
        //高级查询器
        let sqp = {}
        if (this.superQueryParams) {
          sqp['superQueryParams'] = encodeURI(this.superQueryParams)
          sqp['superQueryMatchType'] = this.superQueryMatchType
        }
        var param = Object.assign(sqp, this.queryParam, this.isorter, this.filters)

        param.field = this.getQueryField()
        param.pageNo = this.ipagination.current
        param.pageSize = this.ipagination.pageSize
        delete param.zhanwei1 //范围参数不传递后台
        delete param.zhanwei2
        delete param.zhanwei3
        return filterObj(param)
      },
      onDistributionChange(date, dateString) {
        this.queryParam.distributionStartTime = dateString[0]
        this.queryParam.distributionEndTime = dateString[1]
      },
      onSaleChange(date, dateString) {
        this.queryParam.saleStartTime = dateString[0]
        this.queryParam.saleEndTime = dateString[1]
      },
      onHisChange(date, dateString) {
        this.queryParam.hisStartTime = dateString[0]
        this.queryParam.hisEndTime = dateString[1]
      },
      toHis(record) {
        console.log(record)
        this.$router.push({
          name: 'order-OrderDistributionCheckList',
          query: {
            'distributionNo': record.distributionNo
          }
        })
      },
      initDictConfig() {
      },
      clickThenSelect(record) {
        return {
          on: {
            click: () => {
              this.onSelectChange(record.id.split(','), [record])
            }
          }
        }
      },
      onClearSelected() {
        this.selectedRowKeys = []
        this.selectionRows = []
        this.selectedMainId = ''
      },
      onSelectChange(selectedRowKeys, selectionRows) {
        // this.selectedMainId=selectedRowKeys[0]
        this.selectedMainId = selectionRows[0].orderNo
        this.selectedRowKeys = selectedRowKeys
        this.selectionRows = selectionRows
      },
      loadData(arg) {
        if (!this.url.list) {
          this.$message.error('请设置url.list属性!')
          return
        }
        //加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.ipagination.current = 1
        }
        this.onClearSelected()
        if (this.statusType=="waitCheckVal"){
          this.queryParam.waitShipped = false
          this.queryParam.waitCheck = true
          this.queryParam.hasChecked = false
        }

        var params = this.getQueryParams()//查询条件
        this.loading = true
        getAction(this.url.list, params).then((res) => {
          if (res.success) {
            this.dataSource = res.result.records
            this.ipagination.total = res.result.total
          }
          if (res.code === 510) {
            this.$message.warning(res.message)
          }
          this.loading = false
        })
      }

    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>