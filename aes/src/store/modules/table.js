/**
 * @author Trevis
 * antd table翻页的时候不会保存前页记录对象,这里使用vuex保存.页面刷新后会清空.
 * todo  持久化存储请转存localstorage
 */
export const table = {
  namespaced: true,
  state: {
    selectedRowKeys: [],  //当前选中对象主键
    selectionRows: [],  //当前选中对象
    allRows: new Map  //去重后的全局对象
  },
  mutations: {
    //清空
    clearSelections(state) {
      console.log("vuex清空中...")
      state.selectedRowKeys = []
      state.selectionRows = []
      state.allRows = new Map
      console.debug("清理完成")
    },

    //新增selectedRowKeys
    updateRecords(state, vuexData) {
      state.selectedRowKeys = vuexData.selectedRowKeys

      //加入allRows去重 使用selectedRowKey作为key
      let tempRows = vuexData.selectionRows
      tempRows.forEach(val => {
        state.allRows.set(val.id, val)
      })


      //根据selectedRowKeys从allRows拿有效值保存到selectionRows
      state.selectionRows = []
      vuexData.selectedRowKeys.forEach(val => {
        state.selectionRows.push(state.allRows.get(val))
      })

    }

    //新增selectionRows
  }
}
