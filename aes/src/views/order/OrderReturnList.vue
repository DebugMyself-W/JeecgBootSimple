<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="退货单号:">
              <a-input placeholder="退货单号" v-model="queryParam.returnNo"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="红冲发票号：">
              <a-input placeholder="红冲发票号" v-model="queryParam.invoiceNo"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="类型">
              <j-dict-select-tag v-model="queryParam.type" placeholder="请选择门诊住院" dictCode="in_out_patient"/>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item label="状态：">
              <j-search-select-tag v-model="queryParam.status" placeholder="请选择状态"
                                   dict="order_return,distinct status,status,status is not null"/>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :xl="4" :lg="7" :md="8" :sm="24">
              <a-form-item label="退货方式：">
                <j-search-select-tag v-model="queryParam.returnMode" placeholder="请选择退货方式"
                                     dict="order_return,distinct return_mode,return_mode,return_mode is not null"/>
              </a-form-item>
            </a-col>
            <a-col :xl="8" :lg="7" :md="8" :sm="24">
              <a-form-item label="生成日期:">
                <a-range-picker v-model="queryParam.zhanwei1"
                                format="YYYY-MM-DD"
                                :placeholder="['开始时间', '结束时间']"
                                @change="onChange"/>
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
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('退货单表')">导出</a-button>
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
<!--          <a-popconfirm title="确定提交吗?" @confirm="() => handleSubmit(record.id)">-->
          <!--                  <a v-if="record.status == '待提交'">OA审核</a>-->
          <!--          </a-popconfirm>-->
          <a-popconfirm title="确定提交吗?" @confirm="isExistQtyAndReason(record.returnNo,record.id)">
                  <a>{{ record.btnText }}</a>
          </a-popconfirm>
          <a-divider v-if="record.btnText !=null && record.deletedText != null" type="vertical"/>
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>{{ record.deletedText }}</a>
          </a-popconfirm>
        </span>

      </a-table>
    </div>

    <a-tabs defaultActiveKey="1" @change="pushList">
      <a-tab-pane tab="物流规格信息" key="1">
        <OrderReturnDetailList ref="detail" :mainId="selectedMainId"/>
      </a-tab-pane>

      <a-tab-pane tab="医院规格信息" key="2">
        <OrderReturnStockList ref="stock" :mainId="selectedMainId" :status="status"/>
      </a-tab-pane>
    </a-tabs>


    <orderReturn-modal ref="modalForm" @ok="modalFormOk"></orderReturn-modal>
  </a-card>
</template>

<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import OrderReturnModal from './modules/OrderReturnModal'
  import { getAction } from '@/api/manage'
  import OrderReturnDetailList from './OrderReturnDetailList'
  import OrderReturnStockList from './OrderReturnStockList'
  import { initDictOptions, filterMultiDictText } from '@/components/dict/JDictSelectUtil'
  import '@/assets/less/TableExpand.less'
  import { filterObj } from '@/utils/util'
  import JSearchSelectTag from '@/components/dict/JSearchSelectTag'

  export default {
    name: 'OrderReturnList',
    mixins: [JeecgListMixin],
    components: {
      OrderReturnDetailList: OrderReturnDetailList,
      OrderReturnStockList: OrderReturnStockList,
      OrderReturnModal,
      JSearchSelectTag
    },
    data() {
      return {
        description: '退货单表管理页面',
        // 表头
        columns: [
          {
            title: '退货单号',
            align: 'center',
            dataIndex: 'returnNo'
          },
          {
            title: '类型',
            align: 'center',
            dataIndex: 'type_dictText'
          },
          {
            title: 'HIS出库单号',
            align: 'center',
            dataIndex: 'hisCheckoutNo'
          },
          {
            title: '红冲发票号',
            align: 'center',
            dataIndex: 'invoiceNo'
          },
          {
            title: '状态',
            align: 'center',
            dataIndex: 'status'
          },
          {
            title: '退货方式',
            align: 'center',
            dataIndex: 'returnMode'
          },
          {
            title: '生成日期',
            align: 'center',
            dataIndex: 'createTime'
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
          list: '/order/orderReturn/list',
          delete: '/order/orderReturn/delete',
          submit: '/order/orderReturn/approval',
          deleteBatch: '/order/orderReturn/deleteBatch',
          exportXlsUrl: '/order/orderReturn/exportXls',
          importExcelUrl: 'order/orderReturn/importExcel',
          isExistQtyAndReason: 'order/orderReturn/isExistQtyAndResult'
        },
        dictOptions: {
          type: [],
          drugType: []
        },
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
        status: '',
        selectedStatus: { status: '待提交' }
      }
    },
    created() {
    },
    //祖孙通信
    provide() {
      return {
        status: this.selectedStatus  //传入一个可监听的对象
      }
    },
    computed: {
      importExcelUrl: function() {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
      },

    },
    watch:{
      status(o,n){
        console.log('退货状态改变')
        this.selectedStatus.status = this.status
      }
    },
    methods: {
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
        return filterObj(param)
      },
      onChange(date, dateString) {
        this.queryParam.startSearchTime = dateString[0] + ' 00:00:00'
        this.queryParam.endSearchTime = dateString[1] + ' 23:59:59'
      },
      initDictConfig() {
        initDictOptions('in_out_patient').then((res) => {
          if (res.success) {
            this.$set(this.dictOptions, 'type', res.result)
          }
        })
        initDictOptions('dosage_form').then((res) => {
          if (res.success) {
            this.$set(this.dictOptions, 'drugType', res.result)
          }
        })
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
        this.selectedMainId = selectionRows[0].returnNo
        //console.log(this.selectedMainId);
        this.selectedRowKeys = selectedRowKeys
        this.selectionRows = selectionRows

        this.status = selectionRows[0].status
        this.$refs.detail.setStatus(selectionRows[0].status)
        //this.$refs.stock.setStatus(selectionRows[0].status);
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
        let params = this.getQueryParams()//查询条件
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
      },
      pushList(key) {
        if (key == '1' || key == 1) {
          this.$refs.detail.updateList()
        }

        if (key == '2' || key == 2) {
          this.$refs.stock.updateList()
        }
      },
      /*
      确认是否存在没有填写的qty和reason数据
       */
      isExistQtyAndReason(returnNo, id) {
        let params = {
          returnNo: returnNo
        }
        getAction(this.url.isExistQtyAndReason, params).then((res) => {
          if (res.success) {
            this.handleSubmit(id)
          } else {
            this.$message.warning(res.message)
          }
        })
      }

    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>