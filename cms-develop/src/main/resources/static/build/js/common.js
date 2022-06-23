/**
 * 显示一般信息
 * @param info
 */
function dlg_info(info) {
    layer.alert(info, {
        title: '温馨提示',
        skin: 'layui-layer-molv' //样式类名
        ,closeBtn: 0
    });
}

/**
 * 显示错误信息
 * @param info
 */
function dlg_error(info) {
    layer.alert(info, {
        title:'温馨提示',
        icon: 2,
        skin: 'layui-layer-molv'
    });
}

/**
 * 删除确认对话框
 * @param url
 * @param data
 * @param callback
 */
function dlg_delete(url, data, callback) {
    layer.confirm('您确定要删除该记录吗？', {
        btn: ['确定','取消'],
        title: '温馨提示',
        skin: 'layui-layer-molv',
        icon: 3,
    }, function(){
        layer.closeAll();
        ajax_post(url, data, callback, 'delete');
    });
}

/**
 * 弹出一个页面
 * @param url
 * @param title
 */
function dlg_open(url, title) {
    layer.open({
        type    : 2,
        title   : typeof(title) === 'undefined' ? false : title,
        //area  : ['630px', '360px'],
        area    : ['80%', '80%'],
        shade   : 0.8,
        closeBtn: 1,
        content : url,
        skin    : 'layui-layer-rim', //加上边框
        shadeClose  : false,
    });
}

/**
 * 显示提示框，点击确认后跳转到新页面或者执行回调函数
 * @param info
 * @param url2callback string|function
 */
function dlg_url2callback(info, url2callback) {
    layer.open({
        content: info
        ,skin: 'layui-layer-molv' //样式类名
        ,icon: 1
        ,btn: ['确定']
        ,title: '温馨提示'
        ,yes: function (index) {
            layer.close(index);
            if (typeof url2callback === "function") {
                url2callback();
            }
            else {
                document.location.href = url2callback;
            }
        }
    });
}

/**
 * 向远处服务器提交POST数据，并获取服务器返回的JSON数据
 * @param url 请求地址
 * @param data
 * @param callback
 * @param method
 */
function ajax_post(url, data, callback, method) {
    $.ajax({
        type: typeof(method) === "string" ? method : 'POST',
        url: url,
        data: data,
        contentType: "application/json",
        beforeSend:function () {
            show_loading();
        },
        error: function (request) {
            close_loading();
            //console.log(request.responseText);
        },
        success: function (data) {
            close_loading();
            if (typeof callback === "function") {
                callback(data);
            }
        },
        dataType: 'json'//规定预期的服务器响应的数据类型:json|html
    });
}

function show_loading() {
    layer.load(1, {
        shade: [0.1,'#fff'] //0.1透明度的白色背景
    });
}
function close_loading() {
    layer.closeAll();
}
