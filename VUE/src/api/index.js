import request from '@/utils/request'

// 查询電子帳簿列表
export function listIndex(query) {
  return request({
    url: '/',
    method: 'get',
    params: query
  })
}
