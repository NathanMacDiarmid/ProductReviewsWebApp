const columnDefs = [
    {
        headerName: 'Review',
        children: [
            {
                headerName: 'ID',
                field: 'id',
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
                headerName: 'Description',
                field: 'description',
                width: 180,
                filter: 'agTextColumnFilter',
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