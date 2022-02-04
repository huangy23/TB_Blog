$(function () {
    $("#jqGrid").jqGrid({
        url: 'users/list_syslog',
        datatype: "json",
        colModel: [
            {label: 'id', name: 'id', index: 'id', width: 20, hidden: true, key: true},
            {label: '用户', name: 'username', index: 'username', sortable: false, width: 18},
            {label: '操作', name: 'operation', index: 'operation', sortable: false, width: 23},
            {label: '时间', name: 'time', index: 'time', sortable: false, width: 10},
            {label: '方法', name: 'method', index: 'method', sortable: false, width: 20},
            {label: 'params', name: 'params', index: 'params', sortable: false, width: 20},
            {label: 'IP', name: 'ip', index: 'ip', sortable: false, width: 18},
            {label: '创建时间', name: 'createTime', index: 'createTime', sortable: false, width: 40}
        ],
        height: 485,
        rowNum: 10,
        rowList: [10, 30, 50],
        styleUI: 'Bootstrap',
        loadtext: '信息读取中...',
        rownumbers: true,
        rownumWidth: 35,
        autowidth: true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader: {
            root: "data.list",
            page: "data.currPage",
            total: "data.totalPage",
            records: "data.totalCount"
        },
        prmNames: {
            page: "page",
            rows: "limit",
            order: "order"
        },
        gridComplete: function () {
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
        }
    });

    $(window).resize(function () {
        $("#jqGrid").setGridWidth($(".card-body").width());
    });
});