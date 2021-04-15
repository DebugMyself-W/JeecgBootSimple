<template>
  <a-card :bordered="false" class="dm-table">
    <!-- 查询区域 -->
    <div>
      <a-row class="row" type="flex">
        <a-col :span="2">
          订单装箱号：
        </a-col>
        <a-col :span="16">
          <template v-for="(item,index) in newData">
                <span :class="item.numberClass" style="cursor: pointer" :key="index" @click="toOrderCheck(item.name)">
                  {{item.name}}
                </span>
          </template>
        </a-col>
        <a-col :span="6">
          <a-spin :spinning="spinningValue" v-has="'user:watch'" >
            <a-button @click="click()"  v-if="data.length==0" :disabled="hisCheckinClick">验收入库</a-button>
            <a-button :disabled="data.length!=0" v-if="data.length!=0">存在待验收</a-button>
          </a-spin>
        </a-col>
      </a-row>
      <a-row class="row" type="flex">
        <a-col class="mid" :span="6" :order="1">
          物流发货单：{{distributionNo}}

        </a-col>
        <a-col class="mid" :span="6" :order="2">
          装箱数量：{{qty}}

        </a-col>
        <a-col :span="6" :order="3">
          配送员：{{postman}}
        </a-col>
        <a-col :span="6" :order="4">
          属性：{{inOutPatient}}
        </a-col>
      </a-row>
    </div>

    <a-table class="table1" :columns="columns" :data-source="data" bordered :scroll="{ x:2000,y: 250 }" size="small">

      <template slot="title" slot-scope="currentPageData">
        <div class="f-dfsb">
          <div>
            <strong>待验药品</strong>
          </div>
          <a-input-search style="width: 300px" placeholder="药品拼音首字缩写/代码" enter-button="搜索" @search="onSearch"
                          @change="onChange3" @pressEnter="onSearch"/>
        </div>
      </template>
      <template slot="footer">
        总计{{data.length}}条数据
      </template>
    </a-table>
    <a-table class="table1" :columns="columns2" :data-source="data2" bordered :scroll="{ x:2000,y: 250 }" size="small">
      <!-- <a slot="action" slot-scope="text">action</a> -->
      <template slot="title" slot-scope="currentPageData">
        <div class="f-dfsb">
          <div>
            <strong>已验药品</strong>
            <a-select default-value="0" @change="handleCheckedChange" style="width: 120px;margin-left:30px;">
              <a-select-option value="0">
                全部药品
              </a-select-option>
              <a-select-option value="1">
                漏发药品
              </a-select-option>
              <a-select-option value="2">
                退货药品
              </a-select-option>
            </a-select>
          </div>

          <a-input-search style="width: 300px" placeholder="药品拼音首字缩写/代码" enter-button="搜索" @search="onSearch"
                          @change="onSearch" @pressEnter="onSearch"/>
        </div>
      </template>
      <template slot="footer">
        总计{{data2.length}}条数据
      </template>
    </a-table>
  </a-card>

</template>

<script>
  import axios from 'axios'
  import '@/assets/less/TableExpand.less'
  import { getAction, postAction, deleteAction } from '@/api/manage'

  export default {
    name: 'OrderBoxingCheck',
    // mixins: [JeecgListMixin, mixinDevice],

    data() {
      return {
        // visible: false,
        // inf: '',
        orderBoxingNoList: [],
        distributionNo: this.$route.query.distributionNo,
        hisCheckinClick: '',
        inOutPatient: '',
        postman: '',
        qty: '',
        btnIsShow: false,

        packageNo: '',
        barCode: '',
        checkedName: '',
        unCheckedName: '',
        rowId: '',
        // 表头
        data: [],
        newData: [],
        dataMap: new Map([]),
        data2: [],
        columns: [
          {
            title: '医院药品代码',
            align: 'center',
            width: 200,
            dataIndex: 'hospitalDrugCode'
          },
          {
            title: '医院药品名称',
            align: 'center',
            width: 200,
            dataIndex: 'hospitalDrugName'
          },
          {
            title: '生产厂家',
            dataIndex: 'manufacturer',
            width: 200,
            align: 'center',
            ellipsis: true
          },
          {
            title: '单位',
            dataIndex: 'unit',
            width: 200,
            align: 'center'
          },
          {
            title: '发货数量',
            dataIndex: 'shipQty',
            width: 200,
            align: 'center'
          },
          {
            title: '批号',
            dataIndex: 'batchNo',
            width: 200,
            align: 'center'
          },
          {
            title: '失效日期',
            dataIndex: 'drugEndDate',
            width: 200,
            align: 'center'
          },
          {
            title: '生产日期',
            dataIndex: 'drugStartDate',
            width: 200,
            align: 'center'
          },
          {
            title: '订单装箱号',
            align: 'center',
            width: 200,
            dataIndex: 'packageNo'
          },
          {
            title: '退货数量',
            dataIndex: 'returnQty',
            width: 200,
            align: 'center'
          },
          {
            title: '漏货数量',
            dataIndex: 'lostQty',
            width: 200,
            align: 'center'
          },
          {
            title: '医药药品代码',
            align: 'center',
            width: 200,
            dataIndex: 'logisticsDrugCode'
          },
          {
            title: '医药药品名称',
            align: 'center',
            width: 200,
            dataIndex: 'logisticsDrugName'
          }
        ],
        columns2: [
          {
            title: '医院药品代码',
            align: 'center',
            width: 200,
            dataIndex: 'hospitalDrugCode'
          },
          {
            title: '医院药品名称',
            align: 'center',
            width: 200,
            dataIndex: 'hospitalDrugName'
          },
          {
            title: '生产厂家',
            dataIndex: 'manufacturer',
            width: 200,
            align: 'center',
            ellipsis: true
          },
          {
            title: '单位',
            dataIndex: 'unit',
            width: 200,
            align: 'center'
          },
          {
            title: '批号',
            dataIndex: 'batchNo',
            width: 200,
            align: 'center'
          },
          {
            title: '失效日期',
            dataIndex: 'drugEndDate',
            width: 200,
            align: 'center'
          },
          {
            title: '发货数量',
            dataIndex: 'shipQty',
            width: 200,
            align: 'center'
          },
          {
            title: '验收数量',
            dataIndex: 'checkInNum',
            width: 200,
            align: 'center'
          },
          {
            title: '退货数量',
            dataIndex: 'returnQty',
            width: 200,
            align: 'center'
          },
          {
            title: '漏发数量',
            dataIndex: 'lostQty',
            width: 200,
            align: 'center'
          },
          {
            title: '生产日期',
            dataIndex: 'drugStartDate',
            width: 200,
            align: 'center'
          },
          {
            title: '订单装箱号',
            align: 'center',
            width: 200,
            dataIndex: 'packageNo'
          },
          {
            title: '规格',
            align: 'center',
            width: 200,
            dataIndex: 'spec'
          },
          {
            title: '医药药品代码',
            align: 'center',
            width: 200,
            dataIndex: 'logisticsDrugCode'
          },
          {
            title: '医药药品名称',
            align: 'center',
            width: 200,
            dataIndex: 'logisticsDrugName'
          }
        ],
        drugStatus: '',
        spinningValue: false
      }
    },
    created() {
      this.onSearch()
      this.invoiceChecked()
    },
    mounted() {
      //可以在这里面直接进行滚动条的获取
    },
    computed: {
      // importExcelUrl: function() {
      //     return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
      // }
    },
    methods: {
      toOrderCheck(item) {
        console.log(item)
        this.$router.push({
          name: 'order-OrderBoxingCheck',
          query: {
            'packageNo': item
          }
        })
      },
      onSearch() {
        let params = {
          distributionNo: this.distributionNo,
          // checkDrugSearchText: this.checkedName,
          unCheckDrugSearchText: this.unCheckedName
        }
        getAction('/order/orderBoxingCheck/invoiceCheck', params).then(res => {
          this.data = res.uncheckedDrug;
          this.btnIsShow = res.exist;
          this.xuanran(res)
        })
      },

      click() {
        //this.axios.all([this.checkClick(), this.checkWarehousing()]);
        this.axios.all([this.checkClick()])
      },
      checkClick() {
        this.hisCheckinClick = true
        this.spinningValue = true
        let params = {
          distributionNo: this.distributionNo
        }
        let that = this
        getAction('/order/orderBoxingCheck/hisCheckClick', params).then(res => {
          that.$message.info(res.message)
          that.spinningValue = false
          that.onSearch()
          that.invoiceChecked()
        }).finally(() => {
          that.onSearch()
          that.invoiceChecked()
        })
      },
      checkWarehousing() {
        this.hisCheckinClick = true
        this.spinningValue = true
        let params = {
          distributionNo: this.distributionNo
        }
        getAction('/order/orderBoxingCheck/checkWarehousing', params, 240000).then(res => {
          this.$message.info(res.message)
          this.onSearch()
          this.invoiceChecked()
        })
      },

      invoiceChecked() {
        let params = {
          distributionNo: this.distributionNo,
          searchText: this.unCheckedName,
          drugStatus: this.drugStatus
        }
        getAction('/order/orderBoxingCheck/invoiceChecked', params).then(res => {
          this.data2 = res.checkedDrug
          this.xuanran(res)
        })
      },
      xuanran(res) {
        this.orderBoxingNoList = res.orderBoxingNoList
        this.orderBoxingNoList.forEach(object => {
          this.dataMap.set(object, {
            name: object,
            numberClass: 'number'
          })
          this.data.forEach(object2 => {
              if (object === object2.packageNo) {
                this.dataMap.set(object, {
                  name: object,
                  numberClass: 'numberRed'
                })
              }
            }
          )
        })
        this.distributionNo = res.distributionNo
        this.inOutPatient = res.inOutPatient
        this.postman = res.postman
        this.qty = res.qty
        this.hisCheckinClick = res.hisCheckinClick
        this.newData = this.dataMap.values()
      },
      handleCheckedChange(value) {
        this.drugStatus = value
        this.invoiceChecked()
      },
      onChange4(value) {
        this.checkedName = value.target.value
      },
      onChange3(value) {
        this.unCheckedName = value.target.value
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';

  .number {
    color: #1890ff;
    margin-right: 30px;
  }

  .numberRed {
    color: #f30434;
    margin-right: 30px;
  }

  .row {
    padding: 15px;
  }

  .mid {
    display: flex;
    align-items: center;
  }

  .f-dfsb {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .dm-table /deep/ .clickRowStyl {
    background-color: #1890ff;
    color: #fff;
  }

  .dm-table /deep/ .clickRowStyl:hover > td,
  .clickRowStyl:focus > td {
    background-color: #1890ff !important;
  }

  .f-wd {
    display: inline-block;
    width: 150px;
    text-align: right;
  }
</style>



