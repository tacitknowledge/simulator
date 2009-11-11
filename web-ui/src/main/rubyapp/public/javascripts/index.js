/*!
 * Ext JS Library 3.0.0
 * Copyright(c) 2006-2009 Ext JS, LLC
 * licensing@extjs.com
 * http://www.extjs.com/license
 */
Ext.onReady(function()
{
    Ext.QuickTips.init();

    // turn on validation errors beside the field globally
    //    Ext.form.Field.prototype.msgTarget = 'side';

    var store = new Ext.data.JsonStore({
        //        autoDestroy: true,
        url: 'system/list',
        root:'systems',
        storeId:'storrre',
        idProperty:'name',
        fields: ['name', 'description']
    });

    var conversationsGrid = new Ext.grid.GridPanel({
        store: store,
        columns: [
            {
                id:'name',
                header: 'Name',
                width: 200,
                sortable: true,
                dataIndex: 'name'
            },
            {
                header: 'Description',
                width: '80%',
                dataIndex: 'description'
            }
        ],
        stripeRows: true,
        height: 420,
        width: '100%',
        title: 'Systems',
        // config options for stateful behavior
        stateful: true,
        stateId: 'grid'
    });
    var indexForm = new Ext.form.FormPanel({
         labelWidth: 120, // label settings here cascade unless overridden
        header:false,
        url:'system/save',
        frame:true,
        title: 'New Project',
        bodyStyle:'padding:5px 5px 0',
        height: 500,
        width: "100%",
        defaults: {
            width: "99%"
        },

        items: [
            conversationsGrid
        ],
        buttons: [
            {
                text: 'Add System',
                handler: function() {


                }
            }
        ]
    });
    store.load();
    indexForm.render(document.body)
})