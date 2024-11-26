import request from '@/utils/request'

// 查询配信先リスト列表
export function listSend_to(query) {
  return request({
    url: '/business/send_to/list',
    method: 'get',
    params: query
  })
}

// 查询配信先リスト列表
export function listSend_to2(query) {
  return request({
    url: '/business/send_to/list2',
    method: 'get',
    params: query
  })
}

// 查询配信先リスト详细
export function getSend_to(sendToListId) {
  return request({
    url: '/business/send_to/' + sendToListId,
    method: 'get'
  })
}

// 新增配信先リスト
export function addSend_to(data) {
  return request({
    url: '/business/send_to',
    method: 'post',
    data: data
  })
}

// 修改配信先リスト
export function updateSend_to(data) {
  return request({
    url: '/business/send_to',
    method: 'put',
    data: data
  })
}

// 删除配信先リスト
export function delSend_to(sendToListId) {
  return request({
    url: '/business/send_to/' + sendToListId,
    method: 'delete'
  })
}
