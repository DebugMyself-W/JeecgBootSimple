<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item label="单号:">
              <a-input placeholder="单号" v-model="queryParam.planNo"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item label="门诊住院">
              <j-dict-select-tag v-model="queryParam.inOutPatient" placeholder="请选择门诊住院" dictCode="in_out_patient"/>
            </a-form-item>
          </a-col>
          <template v-if="pageControl!=='1'">
            <a-col :xl="4" :lg="7" :md="8" :sm="24">
              <a-form-item label="状态">
                <j-search-select-tag v-model="queryParam.oaStatus" placeholder="状态选择" dict="oa_status"/>
              </a-form-item>
            </a-col>
          </template>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item label="采购员">
              <a-input placeholder="采购员姓名" v-model="queryParam.buyer"></a-input>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :xl="4" :lg="7" :md="8" :sm="24">
              <a-form-item label="审核员">
                <a-input placeholder="审核员姓名" v-model="queryParam.reviewer"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xl="8" :lg="7" :md="8" :sm="24">
              <a-form-item label="审核时间:">
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
      <a-button v-if="pageControl!=='1'" @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('采购计划')">导出</a-button>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择
        <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table ref="table" size="middle" bordered rowKey="id" class="j-table-force-nowrap" :scroll="{x:true}"
               :columns="columns" :dataSource="dataSource" :pagination="ipagination" :loading="loading"
               :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange, type:'radio'}"
               :customRow="clickThenSelect" @change="handleTableChange">

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

          <template v-if="pageControl !=='1' && record.oaStatus==='0'">
              <a @click="handleEdit(record)">编辑</a>
              <a-divider type="vertical"/>
              <a-popconfirm title="确定提交吗?" @confirm="() => handleSubmit(record.id)">
                                  <a>OA审核</a>
              </a-popconfirm>
              <a-divider type="vertical"/>
              <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                                    <a>删除</a>
              </a-popconfirm>
          </template>
          <template>
            <a v-if="record.purchaseOut !== true && pageControl ==='1' " v-show="record.notDoneNumber>0"
               @click="showModal(record.id)">分拆订单</a>
          </template>
        </span>
      </a-table>
    </div>

    <a-tabs defaultActiveKey="1">
      <a-tab-pane tab="采购计划详情" key="1">
        <OrderPlanDetailList :mainId="selectedMainId" :planNo="selectedPlanNo" :pageControl="pageControl"
                             :status="selectedStatus.status" :selectedMainStartTime="selectedMainStartTime"/>
      </a-tab-pane>
    </a-tabs>

    <orderPlan-modal ref="modalForm" @ok="modalFormOk"></orderPlan-modal>

    <a-modal v-model="visible" title="销售清单拆单确认" :width="width" v-if="shuju"
             :maskClosable="!subLoading" :closable="false">
      <a-spin :spinning="spinningValue">
        <a-table v-for="(item,index) in data" :key="item.id" class="table1" :columns="previewColumns"
                 :data-source="item.orderDetail" bordered :scroll="{ x:1200,y: 250 }">
               <span slot="action" slot-scope="record, index">
                    {{ record.dj * record.shl }}
                </span>
          <template slot="title" slot-scope="currentPageData">
            <strong>物流销售单{{ index + 1 }}</strong>
          </template>
        </a-table>
      </a-spin>
      <template slot="footer">
        <a-button key="back" @click="visible=false" v-if="!subLoading">
          关闭
        </a-button>
        <a-button key="submit" type="primary" :loading="subLoading" @click="handleOk">
          下单
        </a-button>
      </template>
    </a-modal>
  </a-card>

</template>

<script>
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import OrderPlanModal from './modules/OrderPlanModal'
import { getAction, getActionWithTimeLimit, getActionWithParams } from '@/api/manage'
import OrderPlanDetailList from './OrderPlanDetailList'
import { initDictOptions, filterMultiDictText } from '@/components/dict/JDictSelectUtil'
import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
import '@/assets/less/TableExpand.less'
import { filterObj } from '@/utils/util'
import urlUtil from '../../utils/urlUtil'

export default {
  name: 'OrderPlanList',
  mixins: [JeecgListMixin],
  components: {
    OrderPlanDetailList,
    OrderPlanModal,
    JSearchSelectTag
  },
  data() {
    return {
      subLoading: false, //下单loading
      visible: false,
      width: 1200,
      id: '',
      description: '采购计划管理页面',
      shuju: true,
      // 表头
      columns: [
        {
          title: '单号',
          align: 'center',
          dataIndex: 'planNo'
        },
        {
          title: '采购组织',
          align: 'center',
          dataIndex: 'orgCode_dictText'
        },
        {
          title: '门诊住院',
          align: 'center',
          dataIndex: 'inOutPatient_dictText'
        },
        {
          title: '耗材属性',
          align: 'center',
          dataIndex: 'consProp_dictText'
        },
        {
          title: 'OA审批状态',
          align: 'center',
          width: 200,
          dataIndex: 'oaStatus_dictText'
        },
        {
          title: '是否外采',
          align: 'center',
          dataIndex: 'purchaseOut_dictText'
        },
        {
          title: '采购员',
          align: 'center',
          dataIndex: 'buyer_dictText'
        },
        {
          title: '审核员',
          align: 'center',
          dataIndex: 'reviewer_dictText'
        },
        {
          title: '总共品规数',
          align: 'center',
          dataIndex: 'totalNumber'
        },
        {
          title: '已下单完成品规数',
          align: 'center',
          dataIndex: 'doneNumber'
        },
        {
          title: '审核时间',
          align: 'center',
          dataIndex: 'reviewTime'
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
      previewColumns: [
        {
          title: '药品代码(医药)',
          align: 'center',
          width: 200,
          dataIndex: 'spbh'
        },
        {
          title: '药品名称',
          align: 'center',
          width: 200,
          dataIndex: 'name'
        }, {
          title: '规格',
          align: 'center',
          width: 200,
          dataIndex: 'spec'
        }, {
          title: '生产企业',
          align: 'center',
          width: 200,
          dataIndex: 'manufacturer',
          ellipsis: true
        },
        {
          title: '入库单位',
          align: 'center',
          width: 200,
          dataIndex: 'storageUnit'
        },
        {
          title: '下单数量',
          align: 'center',
          width: 200,
          dataIndex: 'shl'
        }, {
          title: '单价',
          align: 'center',
          width: 200,
          dataIndex: 'dj'
        }, {
          title: '建议零售价',
          align: 'center',
          width: 200,
          dataIndex: 'mrsp'
        },
        {
          title: '金额',
          align: 'center',
          width: 200,
          dataIndex: '',
          scopedSlots: { customRender: 'action' }
          // customRender(text) {
          //     if (text == '是') {
          //         return '正常'
          //     } else {
          //         return '禁用'
          //     }
          // }
        }
      ],
      data: [],
      url: {
        list: '/order/orderPlan/list',
        delete: '/order/orderPlan/delete',
        deleteBatch: '/order/orderPlan/deleteBatch',
        exportXlsUrl: '/order/orderPlan/exportXls',
        importExcelUrl: 'order/orderPlan/importExcel',
        submit: '/order/orderPlan/approval'
      },
      dictOptions: {
        orgCode: [],
        inOutPatient: [],
        consProp: [],
        dosageForm: []
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
      selectedPlanNo: '',
      selectedMainStartTime: '',
      spinningValue: false,

      pageControl: '', //权限控制[,采购计划(orderPlanList=0),分拆销售订单页面(send=1)]
      selectedStatus: {
        status: ''
      } //当前选中记录的审批状态
    }
  },
  //祖孙通信
  provide() {
    return {
      status: this.selectedStatus  //传入一个可监听的对象
    }
  },
  created() {
    /*
    * 根据不同路径显示不同内容[orderPlanList,send]
    * send:物流下单员
    * orderPlanList:医院采购员(审批通过列表,即oa_status=1)
    * */
    if (urlUtil.getLast(this.$route.path) === 'send') {
      this.pageControl = '1'
    }

  },
  mounted() {
  },
  computed: {
    importExcelUrl: function() {
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
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
      this.queryParam.sTime = dateString[0]
      this.queryParam.eTime = dateString[1]
      this.searchQuery()
    },
    initDictConfig() {
      initDictOptions('base_purchase_org,name,code').then(res => {
        if (res.success) {
          this.$set(this.dictOptions, 'orgCode', res.result)
        }
      })
      initDictOptions('in_out_patient').then(res => {
        if (res.success) {
          this.$set(this.dictOptions, 'inOutPatient', res.result)
        }
      })
      initDictOptions('cons_prop').then(res => {
        if (res.success) {
          this.$set(this.dictOptions, 'consProp', res.result)
        }
      })
      initDictOptions('dosage_form').then(res => {
        if (res.success) {
          this.$set(this.dictOptions, 'dosageForm', res.result)
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
      this.selectedPlanNo = ''
      this.selectedMainStartTime = ''
    },
    onSelectChange(selectedRowKeys, selectionRows) {
      this.selectedMainId = selectedRowKeys[0]
      this.selectedPlanNo = selectionRows[0].planNo
      this.selectedMainStartTime = selectionRows[0].startTime
      this.selectedRowKeys = selectedRowKeys
      this.selectionRows = selectionRows

      this.selectedStatus.status = selectionRows[0].oaStatus
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
      var params = this.getQueryParams() //查询条件

      //加载数据时权限控制
      if (urlUtil.getLast(this.$route.path) === 'send') {
        this.pageControl = '1'
        params['oaStatus'] = '1'  //send页只显示oaStatus=1的记录
      }
      this.loading = true
      getAction(this.url.list, params).then(res => {
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
    showModal(id, record) {
      console.log(record)
      this.visible = true
      this.id = id
      this.previewOrder(id)

    },
    handleOk(e) {
      this.subLoading = true
      console.log(e)
      if (this.spinningValue) {
        this.$message.info('请等待拆单完毕...')
        return
      }
      this.sendOrder(this.id)
    },
    previewOrder(planId) {
      this.spinningValue = true
      getActionWithTimeLimit('/order/orderPlan/preview?planId=' + planId, 200000).then(res => {
        if(!res.success) {
          this.shuju = false;
          this.data = [];
          this.$message.error(res.message,10);
        } else {
          this.data = res.result;
          this.spinningValue = false;
          this.shuju = true;
        }
      })
    },
    sendOrder(planId) {
      //点击下单按钮 进入加载状态
      this.$message.info('正在下单中...')
      getActionWithParams('/order/orderPlan/send', { planId: planId }, 200000).then(res => {
      }).finally(() => {
        this.visible = false
        this.$message.success('完成下单')
        this.subLoading = false
      })
    }
  }
}
</script>
<style scoped>
@import '~@assets/less/common.less';

.spin-content {
  border: 1px solid #91d5ff;
  background-color: #e6f7ff;
  padding: 30px;
}
</style>