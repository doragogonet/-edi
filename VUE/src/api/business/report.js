import request from '@/utils/request'

// sxt 20240301 add start
// 获取doragogoUrl
export function getDoragogoUrl() {
  return request({
    url: '/business/report/getDoragogoUrl',
    method: 'get'
  })
}
// sxt 20240301 add end

// 查询電子帳簿列表
export function listReport(query) {
  return request({
    url: '/business/report/list',
    method: 'get',
    params: query
  })
}

// 查询電子帳簿详细
export function getReport(data) {
  alert(1);
  return request({
    url: '/business/report/getInfo',
    method: 'post',
    data: data
  })
}

// 新增電子帳簿
export function addReport(data) {
  return request({
    url: '/business/report',
    method: 'post',
    data: data
  })
}

// 修改電子帳簿
export function updateReport(data) {
  return request({
    url: '/business/report/edit',
    method: 'put',
    data: data
  })
}

// 删除電子帳簿
export function delReport(cid1) {
  return request({
    url: '/business/report/' + cid1,
    method: 'delete'
  })
}
