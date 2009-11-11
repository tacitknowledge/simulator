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
        height: 600,
        width: '100%',
        title: 'Systems',
        // config options for stateful behavior
        stateful: true,
        stateId: 'grid'
    });

    store.load();
    conversationsGrid.render(document.body)
})