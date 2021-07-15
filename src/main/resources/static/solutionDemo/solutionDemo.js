var MYDATA = {
    searchInput: '',        //搜索栏的输入
    moduleSelect: '全部',    //所选模块
    changeable: false,      //是否允许添加、修改、删除解决方案
    pageCurrent: 1,         //当前页面索引
    pageSize: 100,          //每页显示数量
    pages: 1                //总页数
};

var solutionMap = new Map();

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
    url += 'moduleSelect=' + MYDATA.moduleSelect;
    return url;
}

function searchSolution(module) {
    if (module) {
        MYDATA.moduleSelect = module;
    }
    MYDATA.searchInput = $('#searchInput').val();
    $.ajax({
        url: "/solutionConfig/searchSolution.do",
        data: {
            module: MYDATA.moduleSelect,
            searchInput: MYDATA.searchInput
        },
        success: function (result) {
            if (result.success) {
                let solutions = result.data;
                solutionMap.clear();
                for (let index = 0; index < solutions.length; index++) {
                    const solution = solutions[index];
                    solutionMap.set(solution.id, solution);
                }
                MYDATA.pages = Math.ceil(solutions.length / MYDATA.pageSize);
                MYDATA.pageCurrent = 1;
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
    var keys = solutionMap.keys();
    for (let i = 0; i < MYDATA.pageSize * (MYDATA.pageCurrent - 1); i++) keys.next();
    var solutionDiv = '';
    for (let index = 0; index < MYDATA.pageSize; index++) {
        let key = keys.next();
        if (key.done) {
            break;
        }
        const solution = solutionMap.get(key.value);
        var url = encodeURI("index_solution.html?name=" + solution.name + "&menu=" + solution.webConfig);
        solutionDiv += '<div class="col-md-auto" style="margin-bottom: 10px; margin-righ: 10px;">';
        solutionDiv += '<div class="card mb-4 h-100" style="min-width: 22rem; max-width: 22rem; min-height: 33rem; max-height: 33rem;">';
        solutionDiv += '<div class="card-header">' + solution.module + "</div>";
        solutionDiv += '<div class="card-body h-100">';
        solutionDiv += '<div class="row">';
        solutionDiv += '<div class="col">';
        solutionDiv += '<h5 class="card-title">' + solution.name + '</h5>';
        solutionDiv += '</div>';
        if (MYDATA.changeable) {
            solutionDiv += '<div class="col-2" style="margin-right: 5px;">';
            solutionDiv += '<button type="button" class="btn btn-default" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" style="box-shadow: none;">';
            solutionDiv += '<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" class="bi bi-three-dots" viewBox="0 0 16 16">';
            solutionDiv += '<g transform="translate(0, -6.5)">';
            solutionDiv += '<path d="M3 9.5a1.5 1.5 0 1 1 0-3 1.5 1.5 0 0 1 0 3zm5 0a1.5 1.5 0 1 1 0-3 1.5 1.5 0 0 1 0 3zm5 0a1.5 1.5 0 1 1 0-3 1.5 1.5 0 0 1 0 3z"/>';
            solutionDiv += '</g>';
            solutionDiv += '</svg>';
            solutionDiv += '</button>';
            solutionDiv += '<div class="dropdown-menu text-center" style="min-width: 50%;" aria-labelledby="dropdownMenuButton">';
            solutionDiv += '<a role="button" onclick="updateSolution(' + solution.id + ');" class="dropdown-item">更新</a>';
            solutionDiv += '<a role="button" onclick="deleteSolution(' + solution.id + ');" class="dropdown-item">删除</a>';
            solutionDiv += '</div>';
            solutionDiv += '</div>';
        }
        solutionDiv += '</div>';
        solutionDiv += '<p class="card-text" style="overflow: hidden; text-overflow: ellipsis; display:-webkit-box; -webkit-box-orient:vertical; -webkit-line-clamp:8;">' + solution.intro + '</p>';
        solutionDiv += '</div>';
        solutionDiv += '<div class="card-body" >';
        solutionDiv += '<div class="row" style="display: flex; justify-content: space-between;">';
        solutionDiv += '<div class="ml-3 mb-3">';
        for (let i = 0; i < solution.customerNum; i++) {
            solutionDiv += '<image src="/img/Star.png" class="img-responsive" style="width: auto; height: 2rem;"></image>';
        }
        solutionDiv += '</div>';
        if (solution.isMVP) solutionDiv += '<image src="/img/MVP.png" class="img-responsive mr-3" style="width: auto; height: 2rem;"></image>';
        solutionDiv += '</div>';
        solutionDiv += '<div class="row" style="display: flex; justify-content: space-between;">';
        solutionDiv += '<a href="' + url + '" class="btn btn-primary ml-3">查看详情</a>';
        if (solution.hasTerraform) solutionDiv += '<image src="/img/terraform.png" class="img-responsive" style="width: auto; height: 2rem; margin-right: 21px;"></image>';
        solutionDiv += '</div>';
        solutionDiv += '</div>';
        solutionDiv += '<div class="card-footer">';
        solutionDiv += '<p class="card-text">';
        solutionDiv += '<small class="text-muted">创建人：' + solution.creator + '</small><br>';
        solutionDiv += '</p>';
        solutionDiv += '</div>';
        solutionDiv += '</div>';
        solutionDiv += '</div>';
    }
    $("#solutionDiv").html(solutionDiv);
    
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
            module: $('#inputModule').val(),
            customerNum: $('#inputCustomerNum').val(),
            isMVP: $('#inputIsMVP').val(),
            hasTerraform: $('#inputHasTerraform').val()
        },
        success: function (result) {
            if (result.success) {
                form.submit();
                $('#addSolutionModal').modal('hide');
                location.reload();
            } else {
                alert("添加失败！" + result.errorMsg);
            }
        }
    })
}

function updateSolution(id) {
    var solution = solutionMap.get(id);
    $('#updateId').val(solution.id);
    $('#updateName').val(solution.name);
    $('#updateIntro').val(solution.intro);
    $('#updateWebConfig').val(solution.webConfig);
    $('#updateCreator').val(solution.creator);
    $('#updateModule').val(solution.module);
    $('#updateCustomerNum').val(solution.customerNum);
    $('#updateIsMVP').val(String(solution.isMVP));
    $('#updateHasTerraform').val(String(solution.hasTerraform));
    $('#updateSolutionModal').modal({ backdrop: 'static', keyboard: false });
}

$(document).on('click', '#submitUpdateSolution', function () {
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
            module: $('#updateModule').val(),
            customerNum: $('#updateCustomerNum').val(),
            isMVP: $('#updateIsMVP').val(),
            hasTerraform: $('#updateHasTerraform').val()
        },
        success: function (result) {
            if (result.success) {
                form.submit();
                $('#updateSolutionModal').modal('hide');
                location.replace(newURL());
            } else {
                alert("更新失败！" + result.errorMsg);
            }
        }
    })
});

function deleteSolution(id) {
    $('#deleteSolutionConfirmModal').modal({ backdrop: 'static', keyboard: false })
        .one('click', '#deleteSolutionBtn', function () {
            $.ajax({
                url: "/solutionConfig/deleteSolutionConfig.do",
                data: {
                    id: id
                },
                success: function (result) {
                    if (result.success) {
                        solutionMap.delete(id);
                        let newPages = Math.ceil(solutionMap.size / MYDATA.pageSize);
                        if (newPages != MYDATA.pages) {
                            MYDATA.pages = newPages;
                            MYDATA.pageCurrent = Math.min(MYDATA.pageCurrent, MYDATA.pages);
                            setPagination();
                        }
                        location.replace(newURL());
                    } else {
                        alert("删除失败！" + result.errorMsg);
                    }
                }
            })
        });
}

window.onload = function () {
    var param = requestParam();
    if (param) {
        MYDATA.searchInput = param['searchInput'];
        MYDATA.moduleSelect = param['moduleSelect'];
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
                    $('#demoTitle').append('<button class="btn btn-success" id="addSolutionBtn" data-toggle="modal" data-target="#addSolutionModal" style="margin-right: 20px;">添加解决方案</button>');
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
    MYDATA.pageCurrent = parseInt(newActive.text());
    loadPage();
});