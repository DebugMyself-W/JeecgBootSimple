<template>
  <a-card :bordered="false">
    <a-col :span="18">
      <a-spin tip="Loading..." :spinning="spinning">
        <div>
          <a-row>
            <a-col :span="18">
              <p>
                <a-divider orientation="left">{{ returnNo }}</a-divider>
              </p>
            </a-col>
            <a-col :span="6"></a-col>
            <!-- 预览区域 -->
            <a-col :span="12" v-if="dataSource.length > 0">
              <template>
                <div v-for="(fileDetail,index) in dataSource[0].fileDetails" :key="index">
                  <div style="float: left;width:104px;height:104px;margin-right: 10px;margin: 0 8px 8px 0;">
                    <div
                      style="width: 100%;height: 100%;position: relative;padding: 8px;border: 1px solid #d9d9d9;border-radius: 4px;">
                      <img style="width: 100%;" :src="fileDetail.imgUrl" :preview="dataSource[0].key">
                    </div>
                  </div>
                </div>
              </template>
            </a-col>
          </a-row>
        </div>
      </a-spin>
      <p></p>
    </a-col>
  </a-card>
</template>

<script>

import ARow from 'ant-design-vue/es/grid/Row'
import {getAction} from '@/api/manage'

export default {
  name: 'OrderReturnImg',
  components: {
    ARow
  },
  data() {
    return {
      description: '图片预览页面',
      spinning: false,
      //数据集
      //dataSource: [],
      dataSource: [{
        key: 0,
        fileDetails: [
          {
            imgUrl: ""
          },
          {
            imgUrl: ""
          },
          {
            imgUrl: ""
          },
          {
            imgUrl: ""
          },
          {
            imgUrl: ""
          },
          {
            imgUrl: ""
          }
        ]
      }
      ],
      returnNo: "退货单号",
      bisId: ""
    }
  },
  created() {
    this.bisId = this.$route.query.bisId;
    this.getImg();
  },
  methods: {
    getImg() {
      if (this.bisId != null && this.bisId != '') {
        let param = {
          bisId : this.bisId
        }
        getAction('/file/view/getImgByBisId', param).then((res) => {
          if (res.success) {
            if (res.result == null || res.result.length == 0){
              this.returnNo = "暂无图片";
              this.dataSource.length = 0;
            }else {
              this.returnNo = res.result[0].returnNo;
              let fileDetails = [];
              for (let i = 0; i < res.result.length; i++) {
                let imgUrl = {
                  imgUrl : res.result[i].url
                }
                fileDetails.push(imgUrl);
              }

              let data = {
                key : 0,
                fileDetails : fileDetails
              }
              this.dataSource = [];
              this.dataSource.push(data);
            }
          }
        })
      }

    }
  }
}
</script>
<style scoped>
.table-operator {
  margin-bottom: 10px
}

.clName .ant-tree li span.ant-tree-switcher, .ant-tree li span.ant-tree-iconEle {
  width: 10px !important;
}

.clName .ant-tree li .ant-tree-node-content-wrapper.ant-tree-node-selected {
  background-color: #1890FF !important;
}
</style>