<template>
  <a-card :bordered="false" :class="'cust-erp-sub-tab'">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item label="药品名称:">
              <a-input placeholder="药品名称" v-model="queryParam.name"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item label="药品代码:">
              <a-input placeholder="药品代码" v-model="queryParam.code"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item label="价格策略">
              <j-search-select-tag v-model="queryParam.priceStrategy" placeholder="价格策略"
                                   dict="base_price_strategy,name,bis_id,deleted='0' and END_TIME is null"/>
            </a-form-item>
          </a-col>

          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item label="是否下单完成">
              <a-select v-model="queryParam.done" @change="searchQuery" placeholder="是否下单完成">
                <a-select-option value="1">是</a-select-option>
                <a-select-option value="0">否</a-select-option>
                <a-select-option value="">全部</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item label="预警">
              <a-select v-model="queryParam.inventoryWarningMark" @change="searchQuery" placeholder="预警">
                <a-select-option value="1">是</a-select-option>
                <a-select-option value="">全部</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchPlanDetailReset" icon="reload"
                        style="margin-left: 8px">重置</a-button>
            </a-col>
          </span>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->
    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <template v-if="mainId && pageControl!=='1'">
        <template v-if="status==='0'">
          <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
        </template>
        <a-button type="primary" icon="download" @click="handleExportXls('采购计划详情')">导出</a-button>
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
      </template>
      <template v-if="mainId && pageControl==='1'">
        <a-button type="primary" icon="download" @click="exportFinishedStatus('采购计划详情'+new Date().toLocaleString())">
          导出
          <template v-if="queryParam.done==='1'">完成</template>
          <template v-if="queryParam.done==='0'">未完成</template>
          药品
        </a-button>
      </template>
    </div>


    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{
          selectedRowKeys.length
        }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        size="small"
        bordered
        rowKey="id"
        :scroll="{x:true}"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        :rowClassName="tableRowClass"
        @change="handleTableChange">

        <template slot="qtySlot" slot-scope="text, record">
          <editable-cell :text="text | fmtQty" @change="onCellChange(record, 'qty', $event)" :flag="'qty'"/>
        </template>

        <template slot="outpatientUsedSlot" slot-scope="text,record">
          <span :style="{color:(record.outPatientFixed?'blue':'black')}">{{ text }}</span>
        </template>

        <template slot="inpatientUsedSlot" slot-scope="text,record">
          <span :style="{color:(record.inpatientFixed?'blue':'black')}">{{ text }}</span>
        </template>

        <template slot="fixedDouble" slot-scope="text">
          <span>{{ text.toFixed(2) }}</span>
        </template>

        <template slot="predictQtySlot" slot-scope="text,record">
          <span>{{ predict(record) }}</span>
        </template>

        <template slot="inventoryWarningMarkSlot" slot-scope="text">
          <span>{{ text ? '是' : '否' }}</span>
        </template>

        <template slot="inventoryProbationDaysSlot" slot-scope="text">
          <span>{{ Number(text).toFixed(1) }}</span>
        </template>

        <template slot="remarkSlot" slot-scope="text, record">
          <editable-cell :text="text" @change="onCellChange(record, 'remark', $event)"/>
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

        <span v-if="pageControl !=='1'" slot="action" slot-scope="text, record">
          <template v-if="status==='0'">
             <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
            <a>删除</a>
          </a-popconfirm>
          </template>
          <template v-else>
            <a style="color: grey">删除</a>
          </template>
        </span>

      </a-table>
    </div>

    <orderPlanDetail-modal ref="modalForm" @ok="modalFormOk" :mainId="mainId" :planNo="planNo"></orderPlanDetail-modal>
  </a-card>
</template>

<script>

import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import OrderPlanDetailModal from './modules/OrderPlanDetailModal'
import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
import { downFile, putAction } from '@/api/manage'

//1.进入编辑状态  2.数据展示状态   3.数据展示状态且不可编辑
const EditableCell = {
  template: `
    <div class="editable-cell">
    <div v-if="editable" class="editable-cell-input-wrapper">
      <a-input :value="value" v-focus @change="handleChange" @pressEnter="check" style="width: 65px"/>
      <a-icon
        type="check"
        class="editable-cell-icon-check"
        @click="check"
      />
    </div>
    <div v-else class="editable-cell-text-wrapper">
      {{ value || ' ' }}
      <a-icon v-if="status.status === '0'" type="edit" class="editable-cell-icon" @click="edit"/>
    </div>
    </div>

  `,
  props: {
    text: String,
    flag: String
  },
  inject: ['status'], //孙节点监听
  data() {
    return {
      value: this.text,
      editable: false
    }
  },
  //自定义指令获取焦点
  directives: {
    focus: {
      // 指令的定义
      inserted: function(el) {
        el.focus()
        el.select()
      }
    }
  },
  watch: {
    value() {
      if (this.flag === 'qty') {
        console.log('监听到采购计划数量改变')
        let num = Number(this.value)
        if (isNaN(num)) {
          this.$message.error('请输入数字类型')
          this.value = this.text
          return false
        }
        if (num < 0 || num >= 100000) {
          this.$message.error('有效范围为1-100000')
          this.value = this.text
          return false
        }
      }
    }
  },
  methods: {
    handleChange(e) {
      const value = e.target.value
      this.value = value
    },
    check() {
      this.editable = false
      this.$emit('change', this.value)
    },
    edit() {
      this.editable = true
    }
  }
}

export default {
  name: 'OrderPlanDetailList',
  mixins: [JeecgListMixin],
  components: { OrderPlanDetailModal, JSearchSelectTag, EditableCell },
  created() {
    //分拆销售订单页不可操作删除按钮
    if (this.pageControl !== '1') {
      this.columns.push(
        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',
          fixed: 'right',
          width: 147,
          scopedSlots: { customRender: 'action' }
        }
      )
    }

  },
  mounted() {
  },
  props: {
    mainId: {
      type: String,
      default: '',
      required: false
    },
    planNo: {
      type: String,
      default: '',
      required: false
    },
    pageControl: {
      type: String,
      default: '',
      required: false
    },
    //审批状态
    status: {
      default: '0'
    },
    //主表记录start_time 暂时废弃
    selectedMainStartTime: {
      required: true
    }
  },
  watch: {
    planNo: {
      immediate: true,
      handler(val) {
        if (!this.planNo) {
          this.clearList()
        } else {
          this.queryParam['planNo'] = val
          this.loadData(1)
        }
      }
    }
  },
  filters: {
    fmtQty(value) {
      return parseFloat(value).toFixed(2)
    }
  },
  data() {
    return {
      description: '采购计划管理页面',
      disableMixinCreated: true,
      // 表头
      columns: [
        {
          title: '序号',
          dataIndex: '',
          fixed: 'left',
          key: 'rowIndex',
          width: 60,
          align: 'center',
          customRender: function(t, r, index) {
            return parseInt(index) + 1
          }
        },
        {
          title: '药品代码',
          align: 'center',
          dataIndex: 'code'
        },
        {
          title: '药品名称',
          align: 'center',
          dataIndex: 'name'
        },
        {
          title: '库存单位',
          align: 'center',
          dataIndex: 'storageUnit'
        },
        {
          title: '医院入库单位售价',
          align: 'center',
          dataIndex: 'retailPrice'
        },
        {
          title: '采购数量',
          align: 'center',
          dataIndex: 'qty',
          scopedSlots: { customRender: 'qtySlot' }
        },
        {
          title: '整件数量',
          align: 'center',
          dataIndex: 'bigPackQty'
        },
        {
          title: '物流凑整数',
          align: 'center',
          dataIndex: 'predictQty',
          scopedSlots: { customRender: 'predictQtySlot' }
        },
        {
          title: '物流单位',
          align: 'center',
          dataIndex: 'unit',
          scopedSlots: { customRender: 'unit' }
        },
        {
          title: '门诊平均用量',
          align: 'center',
          dataIndex: 'outpatientUsed',
          scopedSlots: { customRender: 'outpatientUsedSlot' }
        },
        {
          title: '门诊库存',
          align: 'center',
          dataIndex: 'outpatientStock',
          scopedSlots: {customRender: 'fixedDouble'}
        },
        {
          title: '住院平均用量',
          align: 'center',
          dataIndex: 'inpatientUsed',
          scopedSlots: { customRender: 'inpatientUsedSlot' }
        },
        {
          title: '住院库存',
          align: 'center',
          dataIndex: 'inpatientStock',
          scopedSlots: {customRender: 'fixedDouble'}
        },
        {
          title: '产地',
          align: 'center',
          dataIndex: 'manufacturer'
        },
        {
          title: '已下单',
          align: 'center',
          dataIndex: 'sentQty'
        },
        {
          title: '备注',
          align: 'center',
          dataIndex: 'remark',
          scopedSlots: { customRender: 'remarkSlot' }
        },
        {
          title: '规格',
          align: 'center',
          dataIndex: 'spec'
        },
        {
          title: '剂型',
          align: 'center',
          dataIndex: 'drugType'
        },
        {
          title: '价格策略',
          align: 'center',
          dataIndex: 'priceStrategy_dictText'
        },
        {
          title: '本季计划控量',
          align: 'center',
          dataIndex: 'yearQty',
          scopedSlots: { customRender: 'fixedDouble' }
        },
        {
          title: '本季可用控量',
          align: 'center',
          dataIndex: 'quarterCtrlSum',
          scopedSlots: { customRender: 'fixedDouble' }
        },
        {
          title: '下单完成',
          align: 'center',
          dataIndex: 'done_dictText'
        },
        {
          title: '单位转化比',
          align: 'center',
          dataIndex: 'firstRatio'
        },
        {
          title: '预警',
          align: 'center',
          dataIndex: 'inventoryWarningMark',
          scopedSlots: { customRender: 'inventoryWarningMarkSlot' }
        },
        {
          title: '库存可使用天数',
          align: 'center',
          dataIndex: 'inventoryProbationDays',
          scopedSlots: { customRender: 'inventoryProbationDaysSlot' }
        }
      ],
      url: {
        list: '/order/orderPlan/listOrderPlanDetailByMainId',
        delete: '/order/orderPlan/deleteOrderPlanDetail',
        deleteBatch: '/order/orderPlan/deleteBatchOrderPlanDetail',
        exportXlsUrl: '/order/orderPlan/exportOrderPlanDetail',
        importUrl: '/order/orderPlan/importOrderPlanDetail',
        exportUnfinishedUrl: '/order/orderPlan/exportFinishedStatus'
      },
      dictOptions: {
        orgCode: [],
        inOutPatient: [],
        consProp: [],
        dosageForm: []
      }

    }
  },
  computed: {
    importExcelUrl() {
      return `${window._CONFIG['domianURL']}/${this.url.importUrl}/${this.mainId}`
    },

    predict() {
      return (record) => {
        let tempRatio = record.firstRatio == null ? 1 : record.firstRatio
        return Math.round(record.qty / tempRatio)
      }
    }


  },
  //过滤器
  filter: {},
  methods: {
    tableRowClass(record, index) {
      var isModify = record.startTime !== record.createTime;
      var isOutCtrl = (record.priceStrategy_dictText == '战略' || record.priceStrategy_dictText == '控量') 
              && (record.qty * record.firstRatio > record.quarterCtrlSum);
      if(isModify && isOutCtrl) {
        return "color-orange";
      } else if (isModify) {
        return "color-blue";
      } else if(isOutCtrl) {
        return "color-red";
      }
    },
    searchPlanDetailReset() {
      Object.keys(this.queryParam).forEach(key => {
        if (key != 'planNo') {
          this.queryParam[key] = ''
        }
      })
      this.loadData(1)
    },
    clearList() {
      this.dataSource = []
      this.selectedRowKeys = []
      this.ipagination.current = 1
    },
    //编辑数量,备注
    onCellChange(record, dataIndex, value) {
      if (dataIndex === 'qty') {
        putAction('/order/orderPlan/updateOrderPlanDetailQty', { 'id': record.id, 'qty': value }).then(res => {
          if (res.success) {
            this.$message.success('更新成功')

            this.dataSource.forEach(item => {
              if (item.id === record.id) {
                item.qty = value
              }
            })
          } else {
            this.$message.error('修改数据失败，请稍后重试或联系管理员')
          }
        })
      }
      if (dataIndex === 'remark') {
        putAction('/order/orderPlan/updateOrderPlanDetailRemark', { 'id': record.id, 'remark': value }).then(res => {
          if (res.success) {
            this.$message.success('更新成功')

            this.dataSource.forEach(item => {
              if (item.id === record.id) {
                item.remark = value
              }
            })
          } else {
            this.$message.error('修改数据失败，请稍后重试或联系管理员')
          }
        })
      }

    },
    //导出未完成清单
    exportFinishedStatus(fileName) {
      if (!fileName || typeof fileName != 'string') {
        fileName = '导出文件'
      }
      let param = { planNo: this.planNo, done: this.queryParam.done }

      downFile(this.url.exportUnfinishedUrl, param).then((data) => {
        if (!data) {
          this.$message.warning('文件下载失败')
          return
        }
        if (typeof window.navigator.msSaveBlob !== 'undefined') {
          window.navigator.msSaveBlob(new Blob([data], { type: 'application/vnd.ms-excel' }), fileName + '.xls')
        } else {
          let url = window.URL.createObjectURL(new Blob([data], { type: 'application/vnd.ms-excel' }))
          let link = document.createElement('a')
          link.style.display = 'none'
          link.href = url
          link.setAttribute('download', fileName + '.xls')
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link) //下载完成移除元素
          window.URL.revokeObjectURL(url) //释放掉blob对象
        }
      })
    }
  }
}
</script>
<style>
@import '~@assets/less/common.less';

.color-blue {color: #1890ff;}
.color-record {color: red;}
.color-orange {color: orange;}
</style>
