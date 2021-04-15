<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="发货单号:">
              <a-input placeholder="发货单号" v-model="queryParam.distributionNo"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="漏货单号：">
              <a-input placeholder="漏货单号" v-model="queryParam.lostNo"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="验收人员：">
              <a-input placeholder="验收人员" v-model="queryParam.createBy"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="生成日期:">
              <a-range-picker v-model="queryParam.zhanwei1"
                              format="YYYY-MM-DD"
                              :placeholder="['开始时间', '结束时间']"
                              @change="onChange"/>
            </a-form-item>
          </a-col>
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
      <a-button type="primary" icon="download" @click="handleExportXls('漏货单表')">导出</a-button>
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
          <a-popconfirm title="确定提交吗?" @confirm="isExistQtyAndReason(record.lostNo,record.id)">
            <a v-if="record.status=='待提交'">OA审核</a>
          </a-popconfirm>
          <a-divider v-if="record.status=='待提交'" type="vertical" />
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
            <a v-if="record.status=='待提交'">删除</a>
          </a-popconfirm>
        </span>

      </a-table>
    </div>

    <a-tabs defaultActiveKey="1" @change="pushList">
      <a-tab-pane tab="物流规格信息" key="1">
        <OrderLostDetailList ref="detail" :mainId="selectedMainId"/>
      </a-tab-pane>
      <a-tab-pane tab="医院规格信息" key="2">
        <OrderLostStockList ref="stock" :mainId="selectedMainId" @func="setRefresh" :status="status"/>
      </a-tab-pane>
    </a-tabs>

    <orderLost-modal ref="modalForm" @ok="modalFormOk"></orderLost-modal>
  </a-card>
</template>

<script>

import {JeecgListMixin} from '@/mixins/JeecgListMixin'
import OrderLostModal from './modules/OrderLostModal'
import {getAction} from '@/api/manage'
import OrderLostDetailList from './OrderLostDetailList'
import OrderLostStockList from "./OrderLostStockList";
import '@/assets/less/TableExpand.less'
import {postAction} from '../../api/manage'
import {filterObj} from '@/utils/util'

export default {
  name: 'OrderLostList',
  mixins: [JeecgListMixin],
  components: {
    OrderLostDetailList,
    OrderLostStockList,
    OrderLostModal
  },
  data() {
    return {
      description: '漏货单表管理页面',
      // 表头
      columns: [
        {
          title: '漏货单号',
          align: 'center',
          dataIndex: 'lostNo'
        },
        {
          title: '类型',
          align: 'center',
          dataIndex: 'type'
        },
        {
          title: '发货单号',
          align: 'center',
          dataIndex: 'distributionNo'
        },
        {
          title: '红冲发票号',
          align: 'center',
          dataIndex: 'setoff'
        },
        {
          title: '生成日期',
          align: 'center',
          dataIndex: 'createTime'
        },
        {
          title: '状态',
          align: 'center',
          dataIndex: 'status'
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
        list: '/order/orderLost/list',
        delete: '/order/orderLost/delete',
        submit: "/order/orderLost/approval",
        deleteBatch: '/order/orderLost/deleteBatch',
        exportXlsUrl: '/order/orderLost/exportXls',
        importExcelUrl: 'order/orderLost/importExcel',
        isExistQtyAndReason: "order/orderLost/isExistQtyAndResult"
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
      refresh: false,
      selectedStatus: { status: '待提交' },
      status:''
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
    importExcelUrl: function () {
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
    },
    changeSelectedStatus(){
      this.selectedStatus.status = this.status;
      console.log("退货状态改变")
      return this.status;
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
      this.queryParam.sTime = dateString[0] + ' 00:00:00'
      this.queryParam.eTime = dateString[1] + ' 23:59:59'
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
      this.selectedMainId = selectionRows[0].lostNo
      this.selectedRowKeys = selectedRowKeys
      this.selectionRows = selectionRows

      this.status = selectionRows[0].status
      this.$refs.detail.setStatus(selectionRows[0].status)
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
    },
    handleSend(id) {
      var data = new FormData()
      data.append('id', id)
      postAction('/order/orderLost/send2wl', data).then((res) => {
        this.$message.success('漏货单已发送至物流')
      })
    },
    pushList(key) {
      if (key == "1" || key == 1) {
        this.$refs.detail.updateList();
      }

      if (key == "2" || key == 2) {
        if (this.refresh == true) {
          this.$refs.stock.updateList();
        }
      }
    },
    setRefresh(data) {
      this.refresh = data;
    },
    /*
    确认是否存在没有填写的qty和reason数据
     */
    isExistQtyAndReason(lostNo, id) {
      let params = {
        lostNo: lostNo
      }
      getAction(this.url.isExistQtyAndReason, params).then((res) => {
        if (res.success) {
          this.handleSubmit(id);
        } else {
          this.$message.warning(res.message);
        }
      })
    }
  }
}
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>