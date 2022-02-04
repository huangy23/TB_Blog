$(function () {
    $("#jqGrid").jqGrid({
        url: 'users/list_o',
        datatype: "json",
        colModel: [
            {label: 'id', name: 'id', index: 'id', width: 50, hidden: true, key: true},
            {label: 'title', name: 'title', index: 'title', sortable: false, width: 80},
            {label: 'pubtime', name: 'pubtime', index: 'pubtime', sortable: false, width: 40}
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

function deleteCagegory() {

    var ids = getSelectedRows();
    if (ids == null) {
        return;
    }
    swal({
        title: "确认弹框",
        text: "确认要删除数据吗?",
        icon: "warning",
        buttons: true,
        dangerMode: true,
    }).then((flag) => {
            if (flag) {
                $.ajax({
                    type: "POST",
                    url: "/admin/article/delete",
                    contentType: "application/json",
                    data: JSON.stringify(ids),
                    success: function (r) {
                        if (r.resultCode == 200) {
                            swal("删除成功", {
                                icon: "success",
                            });
                            $("#jqGrid").trigger("reloadGrid");
                        } else {
                            swal(r.message, {
                                icon: "error",
                            });
                        }
                    }
                });
            }
        }
    )
    ;
}

function titleEdit() {
    var ids = getSelectedRow();
    if (ids == null) {
        return;
    }

    swal("新的标题:", {
        content: "input",
    }).then((value) => {
        var data = {
            "id" : ids,
            "title" : value,
        };

        $.ajax({
            type: "POST",
            url: "/admin/article/edit",
            contentType: "application/json",
            data: JSON.stringify(data),
            success: function (r) {
                if (r.resultCode == 200) {
                    swal("修改成功", {
                        icon: "success",
                    });
                    $("#jqGrid").trigger("reloadGrid");
                } else {
                    swal(r.message, {
                        icon: "error",
                    });
                }
            }
        });
    })
}