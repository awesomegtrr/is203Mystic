/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//handle bid input change bid button to enable 
$('.bidinput').change(function () {
    var input = $(this);
    var row = input.attr("id");
    row = row.substring(row.indexOf("t") + 1);
    if ($.trim(input.val()) === "") {
        input.val("");
        return;
    }
    if (isNaN(input.val()) || parseInt(input.val()) < 10 || input.val() === "") {
        console.log(input.val());

        if (input.val() !== "") {
            alert("please enter a valid number (min. 10)");
        }
    }
});

//handle bidbutton when click
$('.bidbutton').click(function () {
    $that = $(this);
    //setting bidparameters into temp
    var temp = $that.attr("bidparameters").split(",");

    //retrieve bidparameters values
    var course = temp[0];
    var section = temp[1];
    var amount = $("#amount" + $that.attr("id")).val();

    //calling ajax method
    $.ajax({
        type: "POST",
        url: "placebid.do",
        data: {course: course, section: section, amount: amount}
    }).done(function (msg) {
        //temp track log delete 
        var output = eval(msg);

        if (output.status === "success") {
            $("#placebid .modal-body p").each(function () {
                $(this).remove();
            });
            $("#placebid .modal-title").html("");
            $("#placebid .modal-title").append("Bid success!");
            $("#placebid .modal-body").append("<p>Balance: e$" + output.message + "</p>");
            $('#placebid').modal('show')
        } else {
            $("#placebid .modal-body p").each(function () {
                $(this).remove();
            });
            $("#placebid .modal-title").html("");
            $("#placebid .modal-title").append("Bid error!");
            for (var i = 0; i < msg.message.length; i++) {
                $("#placebid .modal-body").append("<p>" + output.message[i] + "</p>");
            }
            $('#placebid').modal('show')
        }
    });
});

$('#placebid').on('hidden.bs.modal', function (e) {
    if ($("#placebid .modal-title").html() === "Bid success!") {
        window.location.reload(true);
    }
});

$('.updatebid').click(function () {
    $that = $(this);
    //setting bidparameters into temp
    var temp = $that.attr("bidparameters").split(",");
    //retrieve bidparameters values
    var course = temp[0];
    var section = temp[1];
    var amount = $("#amount" + $that.attr("id")).val();

    //calling ajax method
    $.ajax({
        type: "POST",
        url: "editbid.do",
        data: {course: course, section: section, amount: amount}
    }).done(function (msg) {
        //temp track log delete 
        var output = eval(msg);
        if (output.status === "success") {
            $("#updatebid .modal-body p").each(function () {
                $(this).remove();
            });
            $("#updatebid .modal-title").html("");
            $("#updatebid .modal-title").append("Update bid success!");
            $("#updatebid .modal-body").append("<p>Balance: e$" + output.message + "</p>");
            $('#updatebid').modal('show')
        } else {
            $("#updatebid .modal-body p").each(function () {
                $(this).remove();
            });
            $("#updatebid .modal-title").html("");
            $("#updatebid .modal-title").append("Update bid error!");
            for (var i = 0; i < msg.message.length; i++) {
                $("#updatebid .modal-body").append("<p>" + output.message[i] + "</p>");
            }
            $('#updatebid').modal('show')
        }
    });
});

$('.deletebid').click(function () {
    $that = $(this);
    //setting bidparameters into temp
    var temp = $that.attr("bidparameters").split(",");

    //retrieve bidparameters values
    var course = temp[0];
    var section = temp[1];

    //calling ajax method
    $.ajax({
        type: "POST",
        url: "deletebid.do",
        data: {course: course, section: section}
    }).done(function (msg) {
        //temp track log delete 
        var output = eval(msg);
        if (output.status === "success") {
            $("#deletebid .modal-body p").each(function () {
                $(this).remove();
            });
            $("#deletebid .modal-title").html("");
            $("#deletebid .modal-title").append("Delete bid success!");
            $("#deletebid .modal-body").append("<p>Balance: e$" + output.message + "</p>");
            $('#deletebid').modal('show')
        } else {
            $("#deletebid .modal-body p").each(function () {
                $(this).remove();
            });
            $("#deletebid .modal-title").html("");
            $("#deletebid .modal-title").append("Delete bid error!");
            for (var i = 0; i < msg.message.length; i++) {
                $("#deletebid .modal-body").append("<p>" + output.message[i] + "</p>");
            }
            $('#deletebid').modal('show')
        }

    });
});

$('.dropsection').click(function () {
    if (confirm('Are you sure you want to drop this enrolled section?')) {
        $that = $(this);
        //setting bidparameters into temp
        var temp = $that.attr("sectionparam").split(",");

        //retrieve bidparameters values
        var course = temp[0];
        var section = temp[1];
        $.ajax({
            type: "POST",
            url: "dropsection.do",
            data: {course: course, section: section}
        }).done(function (msg) {
            //temp track log delete 
            var output = eval(msg);
            if (output.status === "success") {
                $("#dropsect .modal-body p").each(function () {
                    $(this).remove();
                });
                $("#dropsect .modal-title").html("");
                $("#dropsect .modal-title").append("Drop section success!");
                $("#dropsect .modal-body").append("<p>Balance: e$" + output.message + "</p>");
                $('#dropsect').modal('show')
            } else {
                $("#dropsect .modal-body p").each(function () {
                    $(this).remove();
                });
                $("#dropsect .modal-title").html("");
                $("#dropsect .modal-title").append("Drop section error!");
                for (var i = 0; i < msg.message.length; i++) {
                    $("#dropsect .modal-body").append("<p>" + output.message[i] + "</p>");
                }
                $('#dropsect').modal('show')
            }
        });
    }
});
$('#deletebid').on('hidden.bs.modal', function (e) {
    window.location.reload(true);
})

$('#dropsect').on('hidden.bs.modal', function (e) {
    window.location.reload(true);
})

$('#updatebid').on('hidden.bs.modal', function (e) {
    if ($("#updatebid .modal-title").html() === "Update bid success!") {
        window.location.reload(true);
    }
})

$('.coursedetail').click(function () {
    var course = $(this).html();
    $.ajax({
        type: "POST",
        url: "classdetail.do",
        data: {course: course}
    }).done(function (msg) {

        var output = eval(msg);
        if (output.status === "success") {
            $("#classdetail .modal-body p").each(function () {
                $(this).remove();
            });
            $("#classdetail .modal-title").html("");
            $("#classdetail .modal-title").append("Course Detail");
            $("#classdetail .modal-body").append("<p>Code: " + output.message[0] + "</p>");
            $("#classdetail .modal-body").append("<p>Title: " + output.message[1] + "</p>");
            $("#classdetail .modal-body").append("<p>School: " + output.message[6] + "</p>");
            $("#classdetail .modal-body").append("<p><br></p>");
            $("#classdetail .modal-body").append("<p>Description: " + output.message[2] + "</p>");
            $("#classdetail .modal-body").append("<p><br></p>");
            $("#classdetail .modal-body").append("<p>Exam date: " + output.message[3] + "</p>");
            $("#classdetail .modal-body").append("<p>Exam start: " + output.message[4] + "</p>");
            $("#classdetail .modal-body").append("<p>Exam end: " + output.message[5] + "</p>");
            $("#classdetail .modal-body").append("<p><br></p>");
            if (msg.message[7].length > 0) {
                $("#classdetail .modal-body").append("<p>Prequisites:</p>");
                for (var i = 0; i < msg.message[7].length; i++) {
                    $("#classdetail .modal-body").append("<p>" + output.message[7][i] + "</p>");
                }
            }
            $('#classdetail').modal('show')
        }

    });
});
