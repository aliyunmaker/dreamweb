var resourcesCounts;
$(document).ready(function() {
    $("#resourceCenterPage").append(
        `<div class="position-absolute top-50 start-50 translate-middle">
        <div class="spinner-border" style="width: 5rem; height: 5rem;" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>`
    );
    $.ajax({
        url: "../resources/listResourcesByRegion.do",
        success: function(result){
            if (result.success) {
                resourcesCounts = result.data;
                console.log(resourcesCounts);
                // showAppCenterPage();
            } else {
                alert(result.errorMsg);
            }
        }
    });
});