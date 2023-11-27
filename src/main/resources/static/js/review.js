const columnDefs = [
    {
        headerName: 'Review',
        children: [
            {
                headerName: 'ID',
                field: 'id',
                cellRenderer: (params) => {
                    let url = window.location.href + "/" + params.value
                    return '<a class="idLink" href=' + url +' rel="noopener">'+ params.value +'</a>'
                },
                width: 90,
                filter: 'agTextColumnFilter',
            },
            {
                headerName: 'Rating',
                field: 'rating',
                width: 180,
                filter: 'agNumberColumnFilter',
            },
            {
                headerName: 'Comment',
                field: 'comment',
                width: 540,
                filter: 'agTextColumnFilter',
            },
            {
                headerName: 'Product',
                cellRenderer: (params) => {
                    let url = 'product/' + params.data.product.id
                    return '<a class="idLink" href=' + url +' rel="noopener">'+ params.data.product.name +'</a>'
                },
                width: 180,
                filter: 'agTextColumnFilter',
            },
            {
                headerName: 'Author',
                field: 'authorId',
                cellRenderer: (params) => {
                    let url = 'client/' + params.value;
                    return '<a class="idLink" href=' + url +' rel="noopener">'+ params.value +'</a>'
                },
                width: 180,
                filter: 'agTextColumnFilter',
            }
        ],
    },
];

const gridOptions = {
    defaultColDef: {
        sortable: true,
        resizable: true,
        filter: true,
    },
    // debug: true,
    columnDefs: columnDefs,
    rowData: null,
};

// set up the grid after the page has finished loading
document.addEventListener('DOMContentLoaded', function () {
    const gridDiv = document.querySelector('#grid');
    const map = window.location.href.split("/").pop();
    new agGrid.Grid(gridDiv, gridOptions);

    fetch('api/' + map,
        {
            method: 'GET',
            headers: {'Content-Type': 'application/json'}
        })
        .then((response) => response.json())
        .then(function (data) {
            gridOptions.api.setRowData(data)
        });
});