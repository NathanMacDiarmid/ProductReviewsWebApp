const columnDefs = [
    {
        headerName: 'Products',
        children: [
            {
                headerName: 'ID',
                field: 'id',
                cellRenderer: (params) => {
                    let url = window.location.href + "/" + params.value
                    return '<a href=' + url +' rel="noopener">'+ params.value +'</a>'
                },
                width: 90,
                filter: 'agTextColumnFilter',
            },
            {
                headerName: 'URL',
                field: 'url',
                cellRenderer: (params) => {
                    let url = "https://" + params.value
                    return '<a href=' + url + ' target="_blank" rel="noopener">'+ params.value +'</a>'
                },
                width: 180,
                filter: 'agNumberColumnFilter',
            },
            {
                headerName: 'Name',
                field: 'name',
                width: 180,
                filter: 'agTextColumnFilter',
            },
            {
                headerName: 'Category',
                field: 'category',
                width: 180 },
        ],
    },
    {
        headerName: 'Reviews',
        children: [
            {
                columnGroupShow: 'closed',
                headerName: 'Reviews',
                valueGetter: (params) => {
                    return JSON.stringify(params.data.reviews)
                },
                width: 450,
                filter: 'agNumberColumnFilter',
            },
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