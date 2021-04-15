<template>
  <a-card :bordered="false" class="dm-table">
    <!-- 查询区域 -->
    <div>
      <a-row class="row" type="flex">
        <a-col :span="6" :order="1">
          物流发货单：{{distributionNo}}
        </a-col>
        <a-col :span="6" :order="2">
          装箱数量：{{qty}}
        </a-col>
        <a-col :span="6" :order="3">
          配送员：{{postman}}
        </a-col>
        <a-col :span="6" :order="4">
          属性：{{inOutPatient}}
        </a-col>
      </a-row>
      <a-row class="row" type="flex">
        <a-col class="mid" :span="8" :order="1">
          装箱条形码：
          <a-input-search style="width: 300px" ref="getFocus" v-model="packageNo" placeholder="扫描装箱条码" @focus="selectValue($event)"
                          enter-button="确定[回车]" @search="onSearch" @change="onChange1" @pressEnter="onSearch"/>
        </a-col>
        <a-col class="mid" :span="8" :order="2">
          药品条形码：
          <a-input-search style="width: 300px" ref="getFocus2" v-model="barCode" placeholder="扫描药品69条码"
                          enter-button="确定[回车]" @search="onSearch" @change="onChange2" @pressEnter="onSearch"/>
        </a-col>
        <a-col :span="4" :order="3">

        </a-col>
        <a-col :span="4" :order="4">

        </a-col>
      </a-row>
    </div>

    <a-table class="table1" :columns="columns" :data-source="data" bordered :scroll="{ x:2000,y: 250 }" size="small"
             :customRow="rowClick" :rowClassName="setRowClassName" @keyup.up.native="up($event)"
             @keyup.down.native="down($event)" @keypress.enter.native="enter">
            <span slot="action" slot-scope="record, index">
                <span @click="rowDelete(record.mapId,index)">漏发</span>
            </span>
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
    <a-table class="table1" :columns="columns2" :data-source="data2" bordered :scroll="{ x:2000,y: 250 }"
             :customRow="rowClick2" :rowClassName="setRowClassName2" @keyup.up.native="up2($event)"
             @keyup.down.native="down2($event)" @keypress.enter.native="enter2" size="small">
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
                退货药品
              </a-select-option>
              <a-select-option value="2">
                漏发药品
              </a-select-option>
            </a-select>
          </div>
          <a-input-search style="width: 300px" placeholder="药品拼音首字缩写/代码" enter-button="搜索" @search="onSearch"
                          @change="onChange4" @pressEnter="onSearch"/>
        </div>
      </template>
      <template slot="footer">
        总计{{data2.length}}条数据
      </template>
    </a-table>
    <a-modal v-model="visible" title="药品信息" @ok="handleOk" :width="1000">
      <a-row class="row" type="flex">
        <a-col class="mid" :span="12" :order="1">
          <span class="f-wd">医院药品代码：</span>
          <a-input class="fontColor" v-model="inf.hospitalDrugCode" :disabled="true"/>
        </a-col>
        <a-col class="mid" :span="12" :order="2">
          <span class="f-wd">批号：</span>
          <a-input class="checkBox" style="width: 300px" v-model="inf.batchNo" :disabled="true"/>
        </a-col>
      </a-row>
      <a-row class="row" type="flex">
        <a-col class="mid" :span="12" :order="1">
          <span class="f-wd">医院药品名称：</span>
          <a-input class="checkBox" style="width: 300px" v-model="inf.hospitalDrugName" :disabled="true"/>
        </a-col>
        <a-col class="mid" :span="12" :order="2">
          <span class="f-wd">发货数量：</span>
          <a-input class="checkBox" style="width: 300px" v-model="inf.shipQty" :disabled="true"/>
        </a-col>
      </a-row>
      <a-row class="row" type="flex">
        <a-col class="mid" :span="12" :order="1">
          <span class="f-wd">医药药品代码：</span>
          <a-input class="fontColor" v-model="inf.logisticsDrugCode" :disabled="true"/>
        </a-col>
        <a-col class="mid" :span="12" :order="2">
          <span class="f-wd">退货数量：</span>
          <a-input style="width: 300px" v-model="inf.returnQty" ref="myInput" @pressEnter="confirmEnter"/>
        </a-col>
      </a-row>
      <a-row class="row" type="flex">
        <a-col class="mid" :span="12" :order="1">
          <span class="f-wd">医药药品名称：</span>
          <a-input class="fontColor" v-model="inf.logisticsDrugName" :disabled="true"/>
        </a-col>
        <a-col class="mid" :span="12" :order="2">
          <span class="f-wd">漏发数量：</span>
          <a-input style="width: 300px" v-model="inf.lostQty" @pressEnter="confirmEnter"/>
        </a-col>
      </a-row>
      <a-row class="row" type="flex">
        <a-col class="mid" :span="12" :order="1">
          <span class="f-wd">批准文号：</span>
          <a-input class="fontColor" v-model="inf.licenseNo" :disabled="true"/>
        </a-col>
        <a-col class="mid" :span="12" :order="2">
          <span class="f-wd">单位：</span>
          <a-input class="fontColor" v-model="inf.unit" :disabled="true"/>
        </a-col>
      </a-row>
      <a-row class="row" type="flex">
        <a-col class="mid" :span="12" :order="1">
          <span class="f-wd">生产厂家：</span>
          <a-input class="checkBox" style="width: 300px" v-model="inf.manufacturer" :disabled="true"/>
        </a-col>
        <a-col class="mid" :span="12" :order="2">
          <span class="f-wd">规格：</span>
          <a-input class="checkBox" style="width: 300px" v-model="inf.spec" :disabled="true"/>
        </a-col>
      </a-row>
      <div>批次信息</div>
      <a-row class="row" type="flex">
        <a-col class="mid" :span="8" :order="1">
          <span class="f-wd">生产日期：</span>
          <a-input class="fontColor" v-model="inf.drugStartDate" :disabled="true"/>
        </a-col>
        <a-col class="mid" :span="8" :order="2">
          <span class="f-wd">失效日期：</span>
          <a-input class="fontColor" v-model="inf.drugEndDate" :disabled="true"/>
        </a-col>
        <a-col class="mid" :span="8" :order="3">
          <span class="f-wd">近效期：</span>
          <a-input class="fontColor" v-model="inf.nearExpirationDate + '个月内'" :disabled="true"/>
        </a-col>
      </a-row>
      <a-row class="row" type="flex">
        <a-col class="mid" :span="8" :order="1">
          <span class="f-wd">供应商：</span>
          <a-input class="fontColor" v-model="inf.manufacturer" :disabled="true"/>
        </a-col>
        <a-col class="mid" :span="8" :order="2">
          <span class="f-wd">质量状态：</span>
          <a-input class="fontColor" v-model="inf.qualityStatus" :disabled="true"/>
        </a-col>
        <a-col class="mid" :span="8" :order="3">

        </a-col>
      </a-row>
    </a-modal>
  </a-card>

</template>

<script>
  import '@/assets/less/TableExpand.less'
  // import { mixinDevice } from '@/utils/mixin'
  // import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { getAction, postAction, deleteAction } from '@/api/manage'

  export default {
    name: 'OrderBoxingCheck',
    // mixins: [JeecgListMixin, mixinDevice],发货单生成日期

    data() {
      return {
        visible: false,
        inf: '',
        distributionNo: '',
        inOutPatient: '',
        postman: '',
        qty: '',
        hisCheckinClick:'',

        packageNo: this.$route.query.packageNo,
        barCode: '',
        checkedName: '',
        unCheckedName: '',
        rowId: '',
        rowId2: '',
        // 表头
        data: [],
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
            align: 'center'
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
            align: 'center'
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
        returnNum: 0,
        missingNum: 0
      }
    },
    created() {

    },
    activated() {
      this.packageNo = this.$route.query.packageNo;
      this.$refs.getFocus.focus();

      this.onSearch()
    },
    watch: {
      //每次变化，input获取焦点
      visible: function(a, b) {
        if (b == false) {
          this.$nextTick(() => {
            this.$refs.myInput.focus()
          })
        }
      }
    },
    computed: {
      // importExcelUrl: function() {
      //     return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
      // }
    },
    methods: {
        selectValue(e) {
            e.currentTarget.select();
        },
      handleCheckedChange(value) {
        this.drugStatus = value
        this.onSearch()
      },
      onSearch() {

        let params = {
          packageNo: this.packageNo,
          barCode: this.barCode,
          checkedName: this.checkedName,
          unCheckedName: this.unCheckedName,
          drugStatus: this.drugStatus
        }
        getAction('/order/orderBoxingCheck/list', params).then(res => {
          this.data = res.uncheckedDrug
          this.data2 = res.checkedDrug
          this.distributionNo = res.distributionNo
          this.inOutPatient = res.inOutPatient
          this.postman = res.postman
          this.hisCheckinClick = res.hisCheckinClick
          this.qty = res.qty
          if(res.message.length>0){
            this.$message.warn(res.message);
          }
          if (this.data.length == 1 && this.barCode != '') {
            this.visible = true
            this.inf = this.data[0]
          }else if (this.data.length == 0) {
             this.$refs.getFocus.focus();
             this.$refs.getFocus2.blur();
          } else if (this.data.length > 1){
              this.$refs.getFocus2.focus();
              this.$refs.getFocus.blur();
          }
        })
      },
      onChange1(value) {
        this.packageNo = value.target.value
      },
      onChange2(value) {
        this.barCode = value.target.value
        if (value.target.value.length == 13) {
          this.onSearch()
        }
      },
      onChange4(value) {
        this.checkedName = value.target.value
      },
      onChange3(value) {
        this.unCheckedName = value.target.value
      },
      rowClick(record, index) {
        return {
          on: {
            click: e => {
              this.rowId = index
            },
            dblclick: e => {
              this.visible = true
              this.inf = record
            }
          }
        }
      },
      rowClick2(record, index) {
        return {
          on: {
            click: e => {
              this.rowId2 = index
            },
            dblclick: e => {
              this.visible = true
              this.inf = record
            }
          }
        }
      },
      setRowClassName(record, index) {
        return index === this.rowId ? 'clickRowStyl' : ''
      },
      setRowClassName2(record, index) {
        return index === this.rowId2 ? 'clickRowStyl' : ''
      },
      up(event) {
        // var top = Math.floor(document.body.scrollTop || document.documentElement.scrollTop || window.pageXOffset)
        // window.event.preventDefault()

        if (this.rowId > 0) {
          this.rowId = this.rowId - 1
        }

        // window.scrollTo(0, 0);
      },
      up2(event) {
        // var top = Math.floor(document.body.scrollTop || document.documentElement.scrollTop || window.pageXOffset)
        // window.event.preventDefault()

        if (this.rowId2 > 0) {
          this.rowId2 = this.rowId2 - 1
        }

        // window.scrollTo(0, 0);
      },
      down(event) {
        // var top = Math.floor(document.body.scrollTop || document.documentElement.scrollTop || window.pageXOffset)
        // window.event.preventDefault()
        if (this.rowId < this.data2.length - 1) {
          this.rowId = this.rowId + 1
        }

        // window.scrollTo(0, 0);
      },
      down2(event) {
        // var top = Math.floor(document.body.scrollTop || document.documentElement.scrollTop || window.pageXOffset)
        // window.event.preventDefault()
        if (this.rowId2 < this.data2.length - 1) {
          this.rowId2 = this.rowId2 + 1
        }

        // window.scrollTo(0, 0);
      },
      enter() {
        this.visible = true
        let ind = this.rowId
        this.inf = this.data[ind]
      },
      enter2() {
        this.visible = true
        let ind = this.rowId2
        this.inf = this.data2[ind]
      },
      handleOk() {
        console.log("标记："+this.hisCheckinClick)
          if(this.hisCheckinClick){
            console.log("标记："+this.hisCheckinClick)
            this.$message.warn('该药品所属订单已入库，不可修改数据');
            callback("该药品所属订单已入库，不可修改数据");
          }
        let numR = Number(this.inf.returnQty)
        let numL = Number(this.inf.lostQty)
        if (isNaN(numR)) {
          this.$message.error('请输入数字')
          return false
        }
        if (isNaN(numL)) {
          this.$message.error('请输入数字')
          return false
        }
        if (parseInt(this.inf.returnQty) + parseInt(this.inf.lostQty) > parseInt(this.inf.shipQty)) {
          this.$message.warn('退货数量与漏发数量之和大于发货数量')
          return
        }
        let params = {
          batchNo: this.inf.batchNo,
          distributionNo: this.distributionNo,
          drugId: this.inf.drugId,
          missingNum: this.inf.lostQty,
          packageNo: this.packageNo,
          returnNum: this.inf.returnQty,
          distributionId: this.inf.distributionId
        }
        console.log(params)
        getAction('/order/orderBoxingCheck/checkOver', params).then(res => {
          this.missingNum = 0
          this.returnNum = 0
          this.inf = ''
          this.visible = false
          this.$refs.getFocus2.focus()
          this.rowId = ''
          this.rowId2 = ''
          this.onSearch()
        })
        this.barCode = ''
      },
      confirmEnter() {
        this.handleOk()
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
  .checkBox{
    color: #1890FF;
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
    color: #333333;
  }

  /*.ant-input[disabled] {*/
  /*  color: #333333;*/
  /*  background-color: #f5f5f5;*/
  /*  cursor: not-allowed;*/
  /*  opacity: 1;*/
  /*}*/

  .fontColor{
    width: 300px;
    color: #333333;
  }
</style>



