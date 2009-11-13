TK.SystemsIndex = Ext.extend(Ext.grid.GridPanel, {

    constructor:function(config) {
        TK.SystemsIndex.superclass.constructor.call(this, Ext.apply({

            title: 'Systems',
            columns : [
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
            ],
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
        },config));

    }
    }
);



       
        
       
        

      

