import request from '@/router/axios';

export const getList = (current, size, params) => {
    return request({
        url: '/api/%s/%s',
        method: 'get',
        params: {
            ...params,
            current,
            size,
        }
    })
}

export const getDetail = (id) => {
    return request({
        url: '/api/%s/%s/' + id,
        method: 'get',
        params: {
        }
    })
}

export const remove = (ids) => {
    return request({
        url: '/api/%s/%s',
        method: 'delete',
        params: {
            ids,
        }
    })
}

export const add = (row) => {
    return request({
        url: '/api/%s/%s',
        method: 'post',
        data: row
    })
}

export const update = (row) => {
    return request({
        url: '/api/%s/%s',
        method: 'put',
        data: row
    })
}
