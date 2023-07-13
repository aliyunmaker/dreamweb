var table;
$(document).ready(function () {
    $("#loading").removeClass("d-none");
    getDataAndInitTable();

    $('#userTable tbody').on('click', 'tr', function () {
        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');
        }
        else {
            table.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
        }
    });

    $("#cancelAdd").on('click', function () {
        console.log('cancelAdd');
        $("#addBookModal").find('input').val('')
    })

    $("#addInfo").on('click', function () {
        console.log('addInfo');
        var typeName = $("input[name='typeName']:checked").val();
        var userName = $("#userName").val();
        var externalId = $("#externalId").val();
        console.log("typeName: " + typeName);
        if (typeName == "user") {
            var familyName = $("#familyName").val();
            var givenName = $("#givenName").val();
            var displayName = $("#displayName").val();
            var email = $("#email").val();
            table.row.add(["id", userName, familyName, givenName, displayName, email, externalId, typeName]).draw();

            // var user = {
            //     "userName": userName,
            //     "familyName": familyName,
            //     "givenName": givenName,
            //     "displayName": displayName,
            //     "email": email,
            //     "externalId": externalId,
            // };
            // var params = {
            //     formString: JSON.stringify(user)
            // }
            // $.ajax({
            //     url: "../" + "employeeList/addUser.do",
            //     type: "POST",
            //     data: params,
            //     success: function (result) {
            //         if (result.success) {
            //             var userID = result.data;
            //             table.row.add([userID, userName, familyName, givenName, displayName, email, externalId, typeName]).draw();
            //         } else {
            //             console.log("result.errorMsg: " + result.errorMsg);
            //             alert(result.errorMsg);
            //         }
            //     },
            // })
        } else if (typeName == "userGroup") {
            table.row.add(["id", userName, '', '', '', '', externalId, typeName]).draw();

            // var userGroup = {
            //     "displayName": userName,
            //     "externalId": externalId,
            // };
            // var params = {
            //     formString: JSON.stringify(userGroup)
            // }
            // $.ajax({
            //     url: "../" + "employeeList/addGroup.do",
            //     type: "POST",
            //     data: params,
            //     success: function (result) {
            //         if (result.success) {
            //             var userGroupID = result.data;
            //             table.row.add([userGroupID, userName, '', '', '', '', externalId, typeName]).draw();
            //         } else {
            //             console.log("result.errorMsg: " + result.errorMsg);
            //             alert(result.errorMsg);
            //         }
            //     },
            // })
        }
        $("#inputText").find('input').val('');
    })


    $("#btn_add").click(function () {
        console.log('add');
        $("#add").modal("show");
    });

    $('#btn_edit').click(function () {
        console.log('edit');
        if (table.rows('.selected').data().length) {
            $("#editInfo").modal("show");
//            console.log("length: " + table.rows('.selected').data().length);
            var rowData = table.rows('.selected').data()[0];
//            console.log("rowData:" + rowData);

            var inputs = $("#editInputText").find('input');
            for (var i = 0; i < inputs.length; i++) {
                $(inputs[i]).val(rowData[i + 1])
            }
            $("input[name='editTypeName'][value='" + rowData[7] + "']").prop("checked", true);
        } else {
            alert('请选择项目');
        }
    });

    $("#saveEdit").click(function () {
        console.log('saveEdit');
        var rowData = table.rows('.selected').data()[0];
        var id = rowData[0];
        var typeName = $("input[name='editTypeName']:checked").val();
        var userName = $("#editUserName").val();
        var externalId = $("#editExternalId").val();
        // console.log("typeName: " + typeName);
        if (typeName == "user") {
            var familyName = $("#editFamilyName").val();
            var givenName = $("#editGivenName").val();
            var displayName = $("#editDisplayName").val();
            var email = $("#editEmail").val();
            var newRowData = [].concat(id, userName, familyName, givenName, displayName, email, externalId, typeName);
            table.row('.selected').data(newRowData).draw();

            // var user = {
            //     "id": id,
            //     "userName": userName,
            //     "familyName": familyName,
            //     "givenName": givenName,
            //     "displayName": displayName,
            //     "email": email,
            //     "externalId": externalId,
            // };
            // var params = {
            //     formString: JSON.stringify(user)
            // }
            // $.ajax({
            //     url: "../" + "employeeList/updateUser.do",
            //     type: "POST",
            //     data: params,
            //     success: function (result) {
            //         if (result.success) {
            //             var newRowData = [].concat(id, userName, familyName, givenName, displayName, email, externalId, typeName);
            //             table.row('.selected').data(newRowData).draw();
            //         } else {
            //             console.log("result.errorMsg: " + result.errorMsg);
            //             alert(result.errorMsg);
            //         }
            //     },
            // })
        } else if (typeName == "userGroup") {
            var newRowData = [].concat(id, userName, "", "", "", "", externalId, typeName);
            table.row('.selected').data(newRowData).draw();

            // var userGroup = {
            //     "id": id,
            //     "displayName": userName,
            //     "externalId": externalId,
            // };
            // var params = {
            //     formString: JSON.stringify(userGroup)
            // }
            // $.ajax({
            //     url: "../" + "employeeList/updateGroup.do",
            //     type: "POST",
            //     data: params,
            //     success: function (result) {
            //         if (result.success) {
            //             var newRowData = [].concat(id, userName, "", "", "", "", externalId, typeName);
            //             table.row('.selected').data(newRowData).draw();
            //             //                    var tds = Array.prototype.slice.call($('.selected td'))
            //             //                    for (var i = 0; i < newRowData.length; i++) {
            //             //                      if (newRowData[i] !== '') {
            //             //                        tds[i + 1].innerHTML = newRowData[i];
            //             //                      }
            //             //                    }

            //         } else {
            //             console.log("result.errorMsg: " + result.errorMsg);
            //             alert(result.errorMsg);
            //         }
            //     },
            // })
        }
        $("#editInputText").find('input').val('');
    })

    $("#cancelEdit").click(function () {
        console.log('cancelAdd');
        $("#editBookModal").find('input').val('')
    })

    $('#btn_delete').click(function () {
        if (table.rows('.selected').data().length) {
            $("#deleteModal").modal("show")
        } else {
            alert('请选择项目');
        }
    });

    $('#delete').click(function () {
        var rowData = table.rows('.selected').data()[0];
        var id = rowData[0];
        var typeName = rowData[7];
        var params = {
            idArray: JSON.stringify([id])
        }
        table.row('.selected').remove().draw(false);
        // if (typeName == "user") {
        //     $.ajax({
        //         url: "../" + "employeeList/deleteUser.do",
        //         type: "POST",
        //         data: params,
        //         success: function (result) {
        //             if (result.success) {
        //                 table.row('.selected').remove().draw(false);
        //             } else {
        //                 console.log("result.errorMsg: " + result.errorMsg);
        //                 alert(result.errorMsg);
        //             }
        //         },
        //     })
        // } else if (typeName == "userGroup") {
        //     $.ajax({
        //         url: "../" + "employeeList/deleteGroup.do",
        //         type: "POST",
        //         data: params,
        //         success: function (result) {
        //             if (result.success) {
        //                 table.row('.selected').remove().draw(false);
        //             } else {
        //                 console.log("result.errorMsg: " + result.errorMsg);
        //                 alert(result.errorMsg);
        //             }
        //         },
        //     })
        // }
    });

    $('#btn_syc').click(function () {
        $("input[name='syncType'][value='false']").prop("checked", true);
        $("#syncList").modal("show");
    });

    $('#sync').click(function () {
        sync();
    });

    // 同步后重新拉取数据
    $('#btn_syncResult').click(function () {
        //       // 新的数据数组
        //       var newData = getData();
        //       // 清空表格数据
        //       table.clear();
        //       // 添加新的数据
        //       table.rows.add(newData);
        //       // 重新绘制表格
        //       table.draw();
    });

});



function getDataAndInitTable() {
    var data = [['1', '用户名', '姓', '名', '显示名称', 'Email', 'externalId', 'type']];
    var tableData = [];
    var request1 = $.ajax({
        url: "../" + "employeeList/getAllUser.do",
        type: "POST",
        //        async: false,
        data: null,
        success: function (result) {
            if (result.success) {
                data = result.data;
                for (var i = 0; i < data.length; i++) {
                    tableData.push([data[i].id, data[i].userName, data[i].familyName, data[i].givenName, data[i].displayName, data[i].email, data[i].externalId, "user"]);
                }
            } else {
                console.log("result.errorMsg: " + result.errorMsg);
                alert(result.errorMsg);
            }
        },
    })

    var request2 = $.ajax({
        url: "../" + "employeeList/getAllGroup.do",
        type: "POST",
        //        async: false,
        data: null,
        success: function (result) {
            if (result.success) {
                data = result.data;
                for (var i = 0; i < data.length; i++) {
                    tableData.push([data[i].id, data[i].displayName, "", "", "", "", data[i].externalId, "userGroup"]);
                }
            } else {
                console.log("result.errorMsg: " + result.errorMsg);
                alert(result.errorMsg);
            }
        },
    })

    // 使用 Promise.all() 等待两个请求都完成
    Promise.all([request1, request2])
        .then(function (responses) {
            // 两个请求都完成了
            table = $('#userTable').DataTable({
                data: tableData,
                width: "100%",
                autoWidth: false,
                deferRender: true, // 延迟渲染
                "pagingType": "full_numbers",
                "bSort": true,
                "language": {
                    "sProcessing": "处理中...",
                    "sLengthMenu": "显示 _MENU_ 项结果",
                    "sZeroRecords": "没有匹配结果",
                    "sInfo": "显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项",
                    "sInfoEmpty": "显示第 0 至 0 项结果，共 0 项",
                    "sInfoFiltered": "(由 _MAX_ 项结果过滤)",
                    "sInfoPostFix": "",
                    "sSearch": "搜索:",
                    "sUrl": "",
                    "sEmptyTable": "表中数据为空",
                    "sLoadingRecords": "载入中...",
                    "sInfoThousands": ",",
                    "oPaginate": {
                        "sFirst": "首页",
                        "sPrevious": "上页",
                        "sNext": "下页",
                        "sLast": "末页"
                    },
                    "oAria": {
                        "sSortAscending": ": 以升序排列此列",
                        "sSortDescending": ": 以降序排列此列"
                    }
                },
                "columnDefs": [{
                    "searchable": false,
                    "orderable": true,
                    "targets": 0
                }],
                "order": [[7, 'asc']],
                // true代表后台处理分页，false代表前台处理分页
                "serverSide": false,
                "lengthMenu": [5, 10, 20, 50],
                initComplete: function () {
                    $("#loading").addClass("d-none");
                    $("#content").show();
                }
            });
        })
        .catch(function (error) {
            // 出了点问题
            console.log('出了点问题');
            console.log(error);
        })

    //    return tableData;
}

function sync() {
    var data = table.rows().data().toArray();
    var dataList = [];
    for (var i = 0; i < data.length; i++) {
        dataList.push({
            "id": data[i][0],
            "userName": data[i][1],
            "familyName": data[i][2],
            "givenName": data[i][3],
            "displayName": data[i][4],
            "email": data[i][5],
            "externalId": data[i][6],
            "typeName": data[i][7]
        });
    }
    syncType = $("input[name='syncType']:checked").val();
    console.log(dataList);
    params = {
        formString: JSON.stringify(dataList),
        removeUnselected: syncType
    }
    $.ajax({
        url: "../" + "employeeList/sync.do",
        type: "POST",
        async: false,
        data: params,
        success: function (result) {
            if (result.success) {
//                console.log(result.data);
                document.getElementById("syncResult").innerText = result.data;
            } else {
                document.getElementById("syncResult").innerText = result.errorMsg;
            }
            $("#syncList").modal('hide');
            $("#syncResultModal").modal("show");
        }
    })
}
