/**
 * @author trevis
 * 获取url中参数
 */

export default {
  //截取url参数
  getUrlKey: function(name) {
    return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.href) || [, ''])[1].replace(/\+/g, '%20')) || null
  },

  //截取url最后一个/后地址
  getLast: function(name) {
    let index = name.lastIndexOf('\/')
    return name.substring(index + 1, name.length)
  }
}