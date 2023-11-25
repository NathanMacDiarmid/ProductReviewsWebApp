const columnDefs = [
    {
        headerName: 'Clients',
        children: [
            {
                headerName: 'ID',
                field: 'id',
                cellRenderer: (params) => {
                    let url = window.location.href + "/" + params.value
                    return '<a class="idLink" href=' + url +' rel="noopener">'+ params.value +'</a>'
                },
                width: 90,
                filter: 'agNumberColumnFilter',
            },
            {
                headerName: 'Username',
                field: 'username',
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
                valueGetter: async (params) => {
                    fetchJaccardDistance(params);
                },
                width: 180,
                filter: 'agNumberColumnFilter'
            }
        ],
    }
];

function fetchJaccardDistance(params) {
    const map = window.location.href.split("/").pop();
    const apiUrl = 'api/' + map + "/" + params.data.id + "/jaccardDistance"

    let jaccardDistance = 0;

    fetch(apiUrl,
        {
            method: 'GET',
            headers: {'Content-Type': 'application/json'}
        })
        .then((response) => response.json())
        .then(data => {
            console.log("Jaccard Distance is: " + JSON.stringify(data))
            jaccardDistance = JSON.stringify(data);
        });

    return jaccardDistance;
}

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