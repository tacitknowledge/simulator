TK.SystemsIndex = Ext.extend(Ext.grid.GridPanel, {

    constructor:function(config) {
        TK.SystemsIndex.superclass.constructor.call(this, Ext.apply({

            title: 'Systems',
            id: 'systems_grid',
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
                id: 'system_add',
                handler:function() {
                    window.location = 'new/'

                }
            },
            {
                text:'Edit',
                id: 'system_edit',
                handler:function() {
                  TK.editEntity('systems','../systems')
                }
            },
            {
                text:'Remove',
                id: 'system_remove',
                handler:function() {
                    TK.deleteEntity('systems','../systems')
                }
            }
            ]
        },config));

    }
    }
);



       
        
       
        

      

