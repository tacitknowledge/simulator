Ext.onReady(function() {
    Ext.QuickTips.init();

    var store = new Ext.data.JsonStore({
        //current url is <systemroot>/systems/
        url: '../systems.json',
        root:'data',
        storeId:'systems store',
        idProperty:'name',
        fields: ['id', 'name', 'description']
    });

    // Let's pretend we rendered our grid-columns with meta-data from our ORM framework.
    var userColumns = [
        {
            header: "Name",
            sortable: true,
            dataIndex: 'name',
            width: 100
        },
        {
            header: "Description",
            dataIndex: 'description',
            width: 200

        }
    ];

    // load the store immeditately
    // Create a typical GridPanel with RowEditor plugin
    var systemGrid = new Ext.grid.GridPanel({
        title: 'Systems',
        store: store,
        columns : userColumns,
        height: 500,
        width: '100%',
        buttons:[
            {
                text:'Add',
                handler:function() {
                    window.location = 'new'
                }
            },
            {
                text:'Edit',
                handler:function() {
                    var rec = systemGrid.getSelectionModel().getSelected();
                    if (rec != undefined) {
                        window.location = rec.data.id
                    }
                }
            },
            {
                text:'Remove',
                handler:function() {
                    var rec = systemGrid.getSelectionModel().getSelected();
                }
            }
        ]
    });
    store.load();
    systemGrid.render(document.body)

});


