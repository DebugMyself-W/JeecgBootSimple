<template>
  <a-card :bordered="false" :class="'cust-erp-sub-tab'">

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button type="primary" icon="download" @click="handleExportXls(mainId+'缺货单详情')">导出</a-button>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a
        style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
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

        <template slot="qtySlot" slot-scope="text,record">
          <span>{{ text | round }}</span>
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

        <template slot="noteSlot" slot-scope="text, record,index">
          <edit-slot :text="text" @change="onCellChange(record,index, 'note', $event)"/>
        </template>

        <template slot="arrivalSlot" slot-scope="text, record,index">
          <arrival-date :obj="text" @change="onCellChange(record,index, 'arrivalDate', $event)"/>
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

    <orderShortageDetail-modal ref="modalForm" @ok="modalFormOk" :mainId="mainId"></orderShortageDetail-modal>
  </a-card>
</template>

<script>

import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import OrderShortageDetailModal from './modules/OrderShortageDetailModal'
import EditSlot from '@views/order/EditSlot'
import ArrivalDate from '@views/order/ArrivalDate'
import { postAction,putAction } from '@/api/manage'


export default {
  name: 'OrderShortageDetailList',
  mixins: [JeecgListMixin],
  components: { OrderShortageDetailModal, EditSlot, ArrivalDate },
  props: {
    mainId: {
      type: String,
      default: '',
      required: false
    }
  },
  filters: {
    round(value) {
      return Math.round(value)
    }
  },
  watch: {
    mainId: {
      immediate: true,
      handler(val) {
        if (!this.mainId) {
          this.clearList()
        } else {
          this.queryParam['shortageNo'] = val
          this.loadData(1)
        }
      }
    }
  },
  data() {
    return {
      description: '缺货单管理页面',
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
        //   title:'缺货单号',
        //   align:"center",
        //   dataIndex: 'shortageNo'
        // },
        {
          title: '门诊/住院',
          align: 'center',
          dataIndex: 'inOutPatient'
        },
        {
          title: '医院药品代码',
          align: 'center',
          dataIndex: 'code'
        },
        {
          title: '医院药品名称',
          align: 'center',
          dataIndex: 'drugId_dictText'
        },
        {
          title: '医院入库单位',
          align: 'center',
          dataIndex: 'storageUnit'
        },
        {
          title: '医院厂家',
          align: 'center',
          dataIndex: 'company'
        },
        {
          title: '缺货数量',
          align: 'center',
          dataIndex: 'qty',
          scopedSlots: { customRender: 'qtySlot' }
        },
        {
          title: '缺货原因',
          align: 'center',
          dataIndex: 'note',
          scopedSlots: { customRender: 'noteSlot' }
        },
        {
          title: '预计物流到货日期',
          align: 'center',
          dataIndex: 'arrivalDate',
          scopedSlots: { customRender: 'arrivalSlot' }
        }
        // ,
        // {
        //   title: '操作',
        //   dataIndex: 'action',
        //   align:"center",
        //   fixed:"right",
        //   width:147,
        //   scopedSlots: { customRender: 'action' },
        // }
      ],
      url: {
        list: '/order/orderShortage/listOrderShortageDetailByMainId',
        delete: '/order/orderShortage/deleteOrderShortageDetail',
        deleteBatch: '/order/orderShortage/deleteBatchOrderShortageDetail',
        exportXlsUrl: '/order/orderShortage/exportOrderShortageDetail',
        importUrl: '/order/orderShortage/importOrderShortageDetail',
        updateUrl:'/order/orderShortage/update',
      },
      dictOptions: {}

    }
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
    onCellChange(record,key, dataIndex, value) {
      this.$set(this.dataSource[key], dataIndex, value)

      postAction(this.url.updateUrl,record).then(res => {
        if(res.code===200){
          this.$message.success('保存成功')
        }else {
          this.$message.error('保存失败')
        }

      })

    }

  }
}
</script>
<style scoped>
@import '~@assets/less/common.less'
</style>
