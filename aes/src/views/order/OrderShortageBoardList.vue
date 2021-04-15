<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->

    <!-- table区域-begin -->
    <div>
      <div class="table-operator">
        <a-button type="primary" icon="download" :loading="btn_loading" @click="handleExportXls('缺货看板')">导出</a-button>
      </div>
      <a-table
        ref="table"
        size="small"
        :scroll="{x:true}"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        class="j-table-force-nowrap"
        @change="handleTableChange">

        <span slot="action" slot-scope="text, record">
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
            <a>删除</a>
          </a-popconfirm>
        </span>

      </a-table>
    </div>

    <order-shortage-board-modal ref="modalForm" @ok="modalFormOk"></order-shortage-board-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import OrderShortageBoardModal from './modules/OrderShortageBoardModal'
  import moment from 'moment'

  export default {
    name: 'OrderShortageBoardList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      OrderShortageBoardModal
    },
    data () {
      return {
        description: '缺货看板管理页面',
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
            title:'药品代码',
            align:"center",
            dataIndex: 'code'
          },
          {
            title:'药品',
            align:"center",
            dataIndex: 'drugId_dictText'
          },
          {
            title:'缺货数量',
            align:"center",
            dataIndex: 'qty'
          },
          {
            title:'单位',
            align: "center",
            dataIndex: 'unit'
          },
          {
            title: '开始缺货日期',
            align: 'center',
            dataIndex: 'startTime'
          },
          {
            title: '缺货天数',
            align: 'center',
            customRender: (text, row, index) => {
              return moment(row.startTime, "YYYY-MM-DD").fromNow(true);
            }
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            fixed:"right",
            width:147,
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: "/order/orderShortageBoard/list",
          exportXlsUrl: "/order/orderShortageBoard/exportXls",
          delete: "/order/orderShortageBoard/delete"
        },
        dictOptions:{},
        ipagination: {
          pageSize: 30,
        },
        btn_loading: false
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
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>