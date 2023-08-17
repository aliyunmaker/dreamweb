var serviceNames = [];
var appNames = [];

$(document).ready(function() {
    $("#costAnalysisPage").append(
    `<div class="position-absolute top-50 start-50 translate-middle">
        <div class="spinner-border" style="width: 5rem; height: 5rem;" role="status">
            <span class="visually-hidden">Loading...</span>
        </div>
    </div>`
    );

    var listServices = $.ajax({
        url: "../common/listServices.do",
        success: function(result){
            if (result.success) {
                serviceNames = result.data;
            } else {
                alert(result.errorMsg);
            }
        }
      });

    var getApplications = $.ajax({
    url: "../common/getApplication.do",
    success: function(result){
        if (result.success) {
            appNames = result.data;
        } else {
            alert(result.errorMsg);
        }
    }
    });

    $.when(listServices, getApplications).done(showCostAnalysisPage);
    
});


function showCostAnalysisPage() {
    var page = $("#costAnalysisPage");
    page.empty();

    var skeleton = `
    <div class="row g-4 py-3 row-cols-2 row-cols-lg-2" id="chartsContainer">
        <div id="appCostBarchartContainer" class="col align-self-start">
        <h4 class="pb-2 fs-4">应用成本</h4>
        <div class="container d-flex align-items-start">
            <select class="form-select" aria-label="Default select example" id="appCostBarchartCycleFilter" onchange="getApplicationCostBarchart()">
            </select>
            <select class="form-select mr-2" aria-label="Default select example" id="appCostBarchartServiceFilter" onchange="getApplicationCostBarchart()">
            </select>
        </div>
        <div id="appCostBarchart"></div>
        </div>
        <div id="depCostBarchartContainer" class="col align-self-start">
        <div class="row">
            <h4 class="pb-2 fs-4">部门成本</h4>
            <div class="container d-flex align-items-start">
            <select class="form-select mr-2" aria-label="Default select example" id="depCostBarchartCycleFilter" onchange="getDepartmentCostBarchart()">
            </select>
            <select class="form-select" aria-label="Default select example" id="depCostBarchartServiceFilter" onchange="getDepartmentCostBarchart()">
            </select>
            </div>
        </div>
        <div class="row" id="depCostBarchart"></div>
        </div>
        <div id="appCostLinechartContainer" class="col align-self-start">
        <div class="col">
            <h4 class="pb-2 fs-4">应用成本历史趋势</h4>
            <div class="container d-flex align-items-start">
            <select class="form-select mr-2" aria-label="Default select example" id="appCostLinechartCycleFilterStart" onchange="getApplicationCostLinechart()">
            </select>
            <select class="form-select mr-2" aria-label="Default select example" id="appCostLinechartCycleFilterEnd" onchange="getApplicationCostLinechart()">
            </select>
            <select class="form-select" aria-label="Default select example" id="appCostLinechartServiceFilter" onchange="getApplicationCostLinechart()">
            </select>
            </div>
        </div>
        <div class="row" id="appCostLinechart"></div>
        </div>
        <div id="depCostLinechartContainer" class="col align-self-start">
        <div class="col">
            <h4 class="pb-2 fs-4">部门成本历史趋势</h4>
            <div class="container d-flex align-items-start">
            <select class="form-select mr-2" aria-label="Default select example" id="depCostLinechartCycleFilterStart" onchange="getDepartmentCostLinechart()">
            </select>
            <select class="form-select mr-2" aria-label="Default select example" id="depCostLinechartCycleFilterEnd" onchange="getDepartmentCostLinechart()">
            </select>
            <select class="form-select" aria-label="Default select example" id="depCostLinechartServiceFilter" onchange="getDepartmentCostLinechart()">
            </select>
            </div>
        </div>
        <div class="row" id="depCostLinechart"></div>
        </div>
    </div>`;
    page.append(skeleton);

    // add options to cycle filter
    var date = new Date();
    
    for (var i = 1; i <= 12; i ++) { // 从1开始是因为最近只能查看上月账单
        var billingCycle;
        if (date.getMonth() + 1 - i > 0) { // +1是因为date.getMonth()的值从0开始
            billingCycle = String(date.getFullYear()) + "-" + String(date.getMonth() + 1 - i).padStart(2, '0');
        } else {
            billingCycle = String(date.getFullYear() - 1) + "-" + String(date.getMonth() + 1 - i + 12).padStart(2, '0');
        }
        $("#appCostBarchartCycleFilter").append(`<option value=${billingCycle}>${billingCycle}</option>`);
        $("#depCostBarchartCycleFilter").append(`<option value=${billingCycle}>${billingCycle}</option>`);
        $("#appCostLinechartCycleFilterEnd").append(`<option value=${billingCycle}>${billingCycle}</option>`);
        $("#depCostLinechartCycleFilterEnd").append(`<option value=${billingCycle}>${billingCycle}</option>`);
        if (i !== 1) { // 保证至少有两个月的值
            $("#appCostLinechartCycleFilterStart").append(`<option value=${billingCycle}>${billingCycle}</option>`);
            $("#depCostLinechartCycleFilterStart").append(`<option value=${billingCycle}>${billingCycle}</option>`);
        }
    }

    // add options to service filter
    $("#appCostBarchartServiceFilter").append(`<option value='all'}>所有资源类型</option>`);
    $("#depCostBarchartServiceFilter").append(`<option value='all'}>所有资源类型</option>`);
    $("#appCostLinechartServiceFilter").append(`<option value='all'}>所有资源类型</option>`);
    $("#depCostLinechartServiceFilter").append(`<option value='all'}>所有资源类型</option>`);

    for (var serviceName of serviceNames) {
        $("#appCostBarchartServiceFilter").append(`<option value=${serviceName.toLowerCase()}>${serviceName}</option>`);
        $("#depCostBarchartServiceFilter").append(`<option value=${serviceName.toLowerCase()}>${serviceName}</option>`);
        $("#appCostLinechartServiceFilter").append(`<option value=${serviceName.toLowerCase()}>${serviceName}</option>`);
        $("#depCostLinechartServiceFilter").append(`<option value=${serviceName.toLowerCase()}>${serviceName}</option>`);
    }

    getApplicationCostBarchart();
    getDepartmentCostBarchart();
    getApplicationCostLinechart();
    getDepartmentCostLinechart();
}

function getApplicationCostBarchart() { 
    $("#appCostBarchart").empty();
    $("#appCostBarchart").append(
    `<div class="d-flex justify-content-center align-items-center">
        <div class="spinner-border" style="width: 5rem; height: 5rem;" role="status">
            <span class="visually-hidden">Loading...</span>
        </div>
    </div>`
    );
    
    var billingCycle = $("#appCostBarchartCycleFilter").val();
    var serviceName = $("#appCostBarchartServiceFilter").val();
    
    var params = {
        billingCycle: billingCycle,
        productCode: serviceName,
        tagKey: "application"
    };
    
    $.ajax({
    url: "../" + "costAnalysis/describeBillBarchart.do",
    type: "POST",
    data: params,
    success: function (result) {
        if (result.success) {
            appsCosts = result.data;
            showApplicationCostBarchart(appsCosts);
        } else {
            console.log("data.message: " + result.errorMsg);
            alert(result.errorMsg);
        }
    }
    });
}

function showApplicationCostBarchart(appsCosts) {
    var chart = {
        plotBackgroundColor: null,
        plotBorderWidth: null,
        plotShadow: false
    };
    // var title = {
    //    text: $("#appCostBarchartCycleFilter option:selected").text()+ $("#appCostBarchartServiceFilter option:selected").text() + '的应用成本扇形图'   
    // };      
    var tooltip = {
       pointFormat: '{series.name}: <b>{point.y:.2f}元</b>'
    };
    var plotOptions = {
       pie: {
          allowPointSelect: true,
          cursor: 'pointer',
          dataLabels: {
             enabled: true,
             format: '<b>{point.name}</b>: {point.y:.2f}元',
             style: {
                color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
             }
          }
       }
    };
    var dataList = [];
    for (var appName in appsCosts) {
        dataList.push([appName, appsCosts[appName]]);
    }
    var series= [{
       type: 'pie',
       name: 'Application Cost',
       data: dataList,
    }];     
       
    var json = {};   
    json.chart = chart; 
    json.title = "";
    json.tooltip = tooltip;  
    json.series = series;
    json.plotOptions = plotOptions;
    $("#appCostBarchart").empty();
    $('#appCostBarchart').highcharts(json);  
}

function getDepartmentCostBarchart() {
    $("#depCostBarchart").empty();
    $("#depCostBarchart").append(
    `<div class="d-flex justify-content-center align-items-center">
        <div class="spinner-border" style="width: 5rem; height: 5rem;" role="status">
            <span class="visually-hidden">Loading...</span>
        </div>
    </div>`
    );

    var billingCycle = $("#depCostBarchartCycleFilter").val();
    var serviceName = $("#depCostBarchartServiceFilter").val();

    var params = {
        billingCycle: billingCycle,
        productCode: serviceName,
        tagKey: "department"
    };
    $.ajax({
    url: "../" + "costAnalysis/describeBillBarchart.do",
    type: "POST",
    data: params,
    success: function (result) {
        if (result.success) {
            var depsCosts = result.data;
            console.log(depsCosts);
            showDepartmentCostBarchart(depsCosts);
        } else {
            console.log("data.message: " + result.errorMsg);
            alert(result.errorMsg);
        }
    }
    });
}

function showDepartmentCostBarchart(depsCosts) {
    var chart = {
        plotBackgroundColor: null,
        plotBorderWidth: null,
        plotShadow: false
    };
    // var title = {
    //    text: $("#depCostBarchartCycleFilter option:selected").text()+ $("#depCostBarchartServiceFilter option:selected").text() + '的部门成本扇形图'   
    // };      
    var tooltip = {
       pointFormat: '{series.name}: <b>{point.y:.2f}元</b>'
    };
    var plotOptions = {
       pie: {
          allowPointSelect: true,
          cursor: 'pointer',
          dataLabels: {
             enabled: true,
             format: '<b>{point.name}</b>: {point.y:.2f}元',
             style: {
                color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
             }
          }
       }
    };
    var dataList = [];
    for (var depName in depsCosts) {
        dataList.push([depName, depsCosts[depName]]);
    }
    var series= [{
       type: 'pie',
       name: 'Department Cost',
       data: dataList,
    }];     
       
    var json = {};   
    json.chart = chart; 
    json.title = ""; 
    json.tooltip = tooltip;  
    json.series = series;
    json.plotOptions = plotOptions;
    $('#depCostBarchart').empty();
    $('#depCostBarchart').highcharts(json);  
}

function getApplicationCostLinechart() {
    $("#appCostLinechart").empty();
    $("#appCostLinechart").append(
    `<div class="d-flex justify-content-center align-items-center">
        <div class="spinner-border" style="width: 5rem; height: 5rem;" role="status">
            <span class="visually-hidden">Loading...</span>
        </div>
    </div>`
    );

    var billingCycleStart = $("#appCostLinechartCycleFilterStart").val();
    var billingCycleEnd = $("#appCostLinechartCycleFilterEnd").val();
    var serviceName = $("#appCostLinechartServiceFilter").val();

    var params = {
        billingCycleStart: billingCycleStart,
        billingCycleEnd: billingCycleEnd,
        productCode: serviceName,
        tagKey: "application"
    };
    $.ajax({
    url: "../" + "costAnalysis/describeBillLinechart.do",
    type: "POST",
    data: params,
    success: function (result) {
        if (result.success) {
            appsCosts = result.data;
            showApplicationCostLinechart(appsCosts);
        } else {
            console.log("data.message: " + result.errorMsg);
            alert(result.errorMsg);
        }
    }
    });
}

function showApplicationCostLinechart(appsCosts) {
    // var title = {
    //     text: $("#appCostLinechartCycleFilterStart option:selected").text() + '至' + 
    //     $("#appCostLinechartCycleFilterEnd option:selected").text() + $("#appCostLinechartServiceFilter option:selected").text() + 
    //     ' \n 应用成本的月度历史折线图'   
    //  };
     var categories = [];
     for (var billingCycle in appsCosts[Object.keys(appsCosts)[0]]) {
        categories.push(billingCycle);
     }
     categories.sort();
     var xAxis = {
        categories: categories
     };
     var yAxis = {
        title: {
           text: 'Cost (元)'
        },
        plotLines: [{
           value: 0,
           width: 1,
           color: '#808080'
        }]
     };   
  
     var tooltip = {
        valueSuffix: '元'
     }
  
     var legend = {
        layout: 'vertical',
        align: 'right',
        verticalAlign: 'middle',
        borderWidth: 0
     };
  
     var series = [];

     for (var appName in appsCosts) {
         var serie = {};
         serie.name = appName;
         serie.data = [];
         for (var cycle of categories) {
             serie.data.push(appsCosts[appName][cycle]);
         }
         series.push(serie);
     }
  
     var json = {};
  
     json.title = "";
     json.xAxis = xAxis;
     json.yAxis = yAxis;
     json.tooltip = tooltip;
     json.legend = legend;
     json.series = series;
  
     $("#appCostLinechart").empty();
     $('#appCostLinechart').highcharts(json);
}

function getDepartmentCostLinechart() {
    $("#depCostLinechart").empty();
    $("#depCostLinechart").append(
    `<div class="d-flex justify-content-center align-items-center">
        <div class="spinner-border" style="width: 5rem; height: 5rem;" role="status">
            <span class="visually-hidden">Loading...</span>
        </div>
    </div>`
    );
    
    var billingCycleStart = $("#depCostLinechartCycleFilterStart").val();
    var billingCycleEnd = $("#depCostLinechartCycleFilterEnd").val();
    var serviceName = $("#depCostLinechartServiceFilter").val();

    var params = {
        billingCycleStart: billingCycleStart,
        billingCycleEnd: billingCycleEnd,
        productCode: serviceName,
        tagKey: "department"
    };
    $.ajax({
    url: "../" + "costAnalysis/describeBillLinechart.do",
    type: "POST",
    data: params,
    success: function (result) {
        if (result.success) {
            console.log(result.data);
            var depsCosts = result.data;
            showDepartmentCostLinechart(depsCosts);
        } else {
            console.log("data.message: " + result.errorMsg);
            alert(result.errorMsg);
        }
    }
    });
}

function showDepartmentCostLinechart(depsCosts) {
    // var title = {
    //     text: $("#depCostLinechartCycleFilterStart option:selected").text() + '至' + 
    //     $("#depCostLinechartCycleFilterEnd option:selected").text() + $("#depCostLinechartServiceFilter option:selected").text() + 
    //     ' \n 部门成本的月度历史折线图'   
    //  };
     var categories = [];
     for (var billingCycle in depsCosts[Object.keys(depsCosts)[0]]) {
        categories.push(billingCycle);
     }
     categories.sort();
     var xAxis = {
        categories: categories
     };
     var yAxis = {
        title: {
           text: 'Cost (元)'
        },
        plotLines: [{
           value: 0,
           width: 1,
           color: '#808080'
        }]
     };   
  
     var tooltip = {
        valueSuffix: '元'
     }
  
     var legend = {
        layout: 'vertical',
        align: 'right',
        verticalAlign: 'middle',
        borderWidth: 0
     };
  
     var series = [];

     for (var depName in depsCosts) {
         var serie = {};
         serie.name = depName;
         serie.data = [];
         for (var cycle of categories) {
             serie.data.push(depsCosts[depName][cycle]);
         }
         series.push(serie);
     }
  
     var json = {};
  
     json.title = "";
     json.xAxis = xAxis;
     json.yAxis = yAxis;
     json.tooltip = tooltip;
     json.legend = legend;
     json.series = series;
  
     $('#depCostLinechart').empty();
     $('#depCostLinechart').highcharts(json);
}

function getDocumentByModule(){
    var params = {
      module: "costanalysis"
    }
    $.ajax({
      url: "../" + "common/getDocumentByModule.do",
      type: "POST",
      data: params,
      success: function (result) {
        if (result.success) {
          var documentContent = result.data;
           document.getElementById('documentContent').innerHTML = marked.parse(documentContent);
        } else {
          console.log("data.message: " + result.errorMsg);
          alert(result.errorMsg);
        }
      }
    })
  }