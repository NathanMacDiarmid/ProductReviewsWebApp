const columnDefs = [
    {
        headerName: 'Clients',
        children: [
            {
                headerName: 'ID',
                field: 'id',
                width: 90,
                filter: 'agNumberColumnFilter',
            },
            {
                headerName: 'Username',
                field: 'username',
                cellRenderer: (params) => {
                    let url = window.location.href + "/" + params.value
                    return '<a class="idLink" href=' + url +' rel="noopener">'+ params.value +'</a>'
                },
                width: 180,
                filter: 'agTextColumnFilter',
            },
            {
                headerName: 'Followers',
                field: 'followerCount',
                width: 180,
                filter: 'agNumberColumnFilter'
            },
            {
                // TODO: Make this dynamic!
                headerName: 'Jaccard Distance',
                width: 180,
                filter: 'agNumberColumnFilter'
            }
        ],
    }
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