var MYDATA = {
    searchInput: '',        //搜索栏的输入
    moduleSelect: '全部',    //所选模块
    changeable: false,      //是否允许添加、修改、删除解决方案
    pageCurrent: 1,         //当前页面索引
    pageSize: 100,          //每页显示数量
    pages: 1                //总页数
};

function test() {
    alert('test');
}

function requestParam() {
    var url = location.search;
    if (url.indexOf('?') == -1) {
        return null;
    }
    var paramsStr = url.substr(1).split("&");
    var paramObj = {};
    for (var i = 0; i < paramsStr.length; i++) {
        var kv = paramsStr[i].split('=');
        var key = kv[0], value = decodeURI(kv[1]);
        paramObj[key] = value
    }
    return paramObj;
}

function newURL() {
    var url = location.pathname + '?';
    url += 'searchInput=' + MYDATA.searchInput + '&';
    url += 'moduleSelect=' + MYDATA.moduleSelect + '&';
    url += 'pageCurrent=' + MYDATA.pageCurrent;
    return url;
}

function searchSolution(module) {
    if (module) {
        MYDATA.moduleSelect = module;
    }
    MYDATA.searchInput = $('#searchInput').val();
    $.ajax({
        url: "/solutionConfig/getSolutionNumber.do",
        data: {
            module: MYDATA.moduleSelect,
            searchInput: MYDATA.searchInput
        },
        success: function (result) {
            if (result.success) {
                var solutionNumber = result.data;
                MYDATA.pages = Math.ceil(solutionNumber / MYDATA.pageSize);
                setPagination();
                loadPage();
            } else {
                alert('获取解决方案数量失败！');
            }
        }
    });
    
}

function setPagination() {
    var paginationDiv = '';
    paginationDiv += '<ul class="pagination justify-content-center">';
    paginationDiv += '<li class="page-item page-pre disabled" style="pointer-events: none;"><a class="page-link" href="#" tabindex="-1">&laquo; 上一页</a></li>';
    if (MYDATA.pages < 2) {
        $('#pagination').html('');
        return;
    } else {
        for (let index = 1; index <= MYDATA.pages; index++) {
            if (index == MYDATA.pageCurrent) {
                paginationDiv += '<li class="page-item active"><a class="page-link" href="#">' + index + '</a></li>';
            } else {
                paginationDiv += '<li class="page-item"><a class="page-link" href="#">' + index + '</a></li>';
            }
        }
    }
    paginationDiv += '<li class="page-item page-next"><a class="page-link" href="#">下一页 &raquo;</a></li>'
    paginationDiv += '</ul>';
    $('#pagination').html(paginationDiv);
}

function loadPage() {
    var pageCurrent =  $('#pagination>ul>li.active');
    if (pageCurrent.length > 0) {
        MYDATA.pageCurrent = parseInt(pageCurrent.text());
    }
    $.ajax({
        url: "/solutionConfig/searchSolution.do",
        data: {
            module: MYDATA.moduleSelect,
            searchInput: MYDATA.searchInput,
            start: MYDATA.pageSize * (MYDATA.pageCurrent - 1),
            limit: MYDATA.pageSize
        },
        success: function (result) {
            if (result.success) {
                var solutions = result.data;
                var solutionDiv = '';
                for (let index = 0; index < solutions.length; index++) {
                    const solution = solutions[index];
                    var url = encodeURI("index_solution.html?name=" + solution.name + "&menu=" + solution.webConfig);
                    solutionDiv += '<div class="col-md-4 col-6" style="margin-bottom: 20px;">';
                    solutionDiv += '<div class="card mb-4 h-100" style="max-width: 22rem;">';
                    solutionDiv += '<div class="card-header">' + solution.module + "</div>";
                    solutionDiv += '<div class="card-body h-100">';
                    // solutionDiv += '<h5 class="card-title">' + solution.name + '</h5>';
                    solutionDiv += '<div class="row justify-content-between">';
                    solutionDiv += '<div class="col">';
                    solutionDiv += '<h5 class="card-title">' + solution.name + '</h5>';
                    solutionDiv += '</div>';
                    if (MYDATA.changeable) {
                        solutionDiv += '<div class="col-2" style="margin-right: 10px;">';
                        solutionDiv += '<button type="button" class="btn btn-default" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" style="box-shadow: none;">';
                        solutionDiv += '<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" class="bi bi-three-dots" viewBox="0 0 16 16">';
                        solutionDiv += '<g transform="translate(0, -6.5)">';
                        solutionDiv += '<path d="M3 9.5a1.5 1.5 0 1 1 0-3 1.5 1.5 0 0 1 0 3zm5 0a1.5 1.5 0 1 1 0-3 1.5 1.5 0 0 1 0 3zm5 0a1.5 1.5 0 1 1 0-3 1.5 1.5 0 0 1 0 3z"/>';
                        solutionDiv += '</g>';
                        solutionDiv += '</svg>';
                        solutionDiv += '</button>';
                        solutionDiv += '<div class="dropdown-menu text-center" style="min-width: 50%;" aria-labelledby="dropdownMenuButton">';
                        solutionDiv += '<a role="button" onclick="updateSolution(&apos;' + solution.name + '&apos;);" class="dropdown-item">更新</a>';
                        solutionDiv += '<a role="button" onclick="deleteSolution(&apos;' + solution.name + '&apos;);" class="dropdown-item">删除</a>';
                        solutionDiv += '</div>';
                        solutionDiv += '</div>';
                    }
                    solutionDiv += '</div>';
                    solutionDiv += '<p class="card-text">' + solution.intro + '</p>';
                    solutionDiv += '</div>';
                    solutionDiv += '<div class="card-body" style="display: flex; justify-content: space-between;">';
                    solutionDiv += '<a href="' + url + '" class="btn btn-primary">查看详情</a>';
                    // if (MYDATA.changeable) {
                    //   solutionDiv += '<button onclick="updateSolution(&apos;' + solution.name + '&apos;);" class="btn btn-warning">更新</button>';
                    //   solutionDiv += '<button onclick="deleteSolution(&apos;' + solution.name + '&apos;);" class="btn btn-danger">删除</button>';
                    // }
                    solutionDiv += '</div>';
                    solutionDiv += '<div class="card-footer">';
                    solutionDiv += '<p class="card-text">';
                    solutionDiv += '<small class="text-muted">创建人：' + solution.creator + '</small><br>';
                    solutionDiv += '<small class="text-muted">版本：' + solution.version + '</small>'
                    solutionDiv += '</p>';
                    solutionDiv += '</div>';
                    solutionDiv += '</div>';
                    solutionDiv += '</div>';
                }
                $("#solutionDiv").html(solutionDiv);
            } else {
                alert('获取解决方案失败！');
            }
        }
    });
    
}

function addSolution() {
    let form = document.getElementById('addSolutionForm');
    if (form.checkValidity() === false) {
        form.classList.add('was-validated');
        return false;
    }
    $.ajax({
        url: "/solutionConfig/addSolutionConfig.do",
        data: {
            name: $('#inputName').val(),
            intro: $('#inputIntro').val(),
            webConfig: $('#inputWebConfig').val(),
            creator: $('#inputCreator').val(),
            version: $('#inputVersion').val(),
            module: $('#inputModule').val()
        },
        success: function (result) {
            if (result.success) {
                form.reset();
                $('#addSolutionModal').modal('hide');
                location.reload();
            } else {
                alert("添加失败！" + result.errorMsg);
            }
        }
    })
}

function updateSolution(name) {
    $.ajax({
        url: "/solutionConfig/getSolutionConfigByName.do",
        data: {
            name: name
        },
        success: function (result) {
            if (result.success) {
                let solution = result.data;
                $('#updateId').val(solution.id);
                $('#updateName').val(solution.name);
                $('#updateIntro').val(solution.intro);
                $('#updateWebConfig').val(solution.webConfig);
                $('#updateCreator').val(solution.creator);
                $('#updateVersion').val(solution.version);
                $('#updateModule').val(solution.module);
                $('#updateSolutionModal').modal({ backdrop: 'static', keyboard: false })
                    .one('click', '#submitUpdateSolution', function () {
                        let form = document.getElementById('updateSolutionForm');
                        if (form.checkValidity() === false) {
                            form.classList.add('was-validated');
                            return false;
                        }
                        $.ajax({
                            url: "/solutionConfig/updateSolutionConfig.do",
                            data: {
                                id: $('#updateId').val(),
                                name: $('#updateName').val(),
                                intro: $('#updateIntro').val(),
                                webConfig: $('#updateWebConfig').val(),
                                creator: $('#updateCreator').val(),
                                version: $('#updateVersion').val(),
                                module: $('#updateModule').val()
                            },
                            success: function (result) {
                                if (result.success) {
                                    form.reset();
                                    $('#updateSolutionModal').modal('hide');
                                    location.replace(newURL());
                                } else {
                                    alert("更新失败！" + result.errorMsg);
                                }
                            }
                        })
                    });
            } else {
                alert("获取解决方案失败！" + result.errorMsg);
            }
        }
    })
}

function deleteSolution(name) {
    $.ajax({
        url: "/solutionConfig/getSolutionConfigByName.do",
        data: {
            name: name
        },
        success: function (result) {
            if (result.success) {
                let solution = result.data;
                $('#deleteSolutionConfirmModal').modal({ backdrop: 'static', keyboard: false })
                    .one('click', '#deleteSolutionBtn', function () {
                        $.ajax({
                            url: "/solutionConfig/deleteSolutionConfig.do",
                            data: {
                                id: solution.id
                            },
                            success: function (result) {
                                if (result.success) {
                                    location.replace(newURL());
                                } else {
                                    alert("删除失败！" + result.errorMsg);
                                }
                            }
                        })
                    });
            } else {
                alert("获取解决方案失败！" + result.errorMsg);
            }
        }
    });
}

window.onload = function () {
    var param = requestParam();
    if (param) {
        MYDATA.searchInput = param['searchInput'];
        MYDATA.moduleSelect = param['moduleSelect'];
        MYDATA.pageCurrent = parseInt(param['pageCurrent']);
    }
    $('#searchInput').val(MYDATA.searchInput);
    var modules = $('#modules>label');
    for (let index = 0; index < modules.length; index++) {
        const module = modules.get(index);
        if (module.textContent == MYDATA.moduleSelect) {
            module.classList.add('active');
            break;
        }
    }
    $.ajax({
        url: "/user/getUserAuthorities.do",
        data: null,
        success: function (result) {
            if (result.success) {
                var userRole = result.data;
                MYDATA.changeable = (userRole.indexOf('ROLE_ADMIN') != -1);
                if (MYDATA.changeable) {
                    $('#addSolutionBtn').css('display', 'block');
                } else {
                    $('#addSolutionBtn').css('display', 'none');
                    console.log("You are not super admin, so you can not change the solution config! Your role: " + userRole);
                }
                searchSolution();
            }
        }
    });
}

function generatePageItem(pageNum){
    return '<li class="page-item"><a class="page-link">'+pageNum+'</a></li>';
}
 
$(document).on("click", '.page-item', function () {
    var click = $(this);
    var textClick = click.text();
    var active = $("#pagination .active");
    var pagePre = $(".page-pre");
    var pageNext = $(".page-next");
    var pages = MYDATA.pages;
    if(textClick.indexOf("上一页") != -1){
        active.removeClass("active");
        active.prev().addClass("active");
    }else if(textClick.indexOf("下一页") != -1){
        active.removeClass("active");
        active.next().addClass("active");
    }else{
        active.removeClass("active");
        click.addClass("active");
    }
    var newActive = $("#pagination .active");
    if (newActive.text() == '1') {
        pagePre.addClass('disabled').css("pointer-events", "none");
        pagePre.children('a').attr('tabindex', '-1');
    } else {
        pagePre.removeClass('disabled').css("pointer-events", "auto");
        pagePre.children('a').removeAttr('tabindex');
    }
    if (newActive.text() == pages + '') {
        pageNext.addClass('disabled').css("pointer-events", "none");
        pageNext.children('a').attr('tabindex', '-1');
    } else {
        pageNext.removeClass('disabled').css("pointer-events", "auto");
        pageNext.children('a').removeAttr('tabindex');
    }
    loadPage();
});