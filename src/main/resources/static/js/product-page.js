$(document).on("submit", "#removeReviewForm", function (event) {
    $.ajax({
        data: $("#removeReviewForm").serializeArray(),
        type: 'delete',
        url: '/api/deleteReview'
    }).done(function (response) {
        console.log(response);
    });
    event.preventDefault();
});