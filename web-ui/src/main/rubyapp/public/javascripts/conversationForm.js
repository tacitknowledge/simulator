/*!
 * Ext JS Library 3.0.0
 * Copyright(c) 2006-2009 Ext JS, LLC
 * licensing@extjs.com
 * http://www.extjs.com/license
 */
Ext.onReady(function()
{
   // create the data store
    var store = new Ext.data.ArrayStore({
        fields: [
           {name: 'id'},
           {name: 'name'},
           {name: 'description'},
           {name: 'is_active', type: 'boolean'},
        ]
    });


    var myData = [
        ['1', 'Scenario1', 'One scenario', true],
        ['2', 'Scenario 2', 'Second scenario', false]
    ];

    store.loadData(myData);


    Ext.QuickTips.init();

    // turn on validation errors beside the field globally
    Ext.form.Field.prototype.msgTarget = 'side';

    var form = new Ext.FormPanel({
        labelWidth: 120, // label settings here cascade unless overridden
        url:'/conversation',
        frame: true,
        title: 'Conversation',
        bodyStyle: 'padding:5px 5px 0;',
        width: "800",
        
        defaults: {
            width: "100%"
        },
        
        defaultType: 'textfield',

        items: [
            {
                fieldLabel: 'Name',
                name: 'name',
                allowBlank:false,
                xtype: 'textfield'
            },
            {
                fieldLabel: 'Description',
                name: 'description',
                xtype: 'textarea'
            },
            {
                xtype: 'fieldset',
                title: 'Inbound',
                collapsible: false,
                autoHeight: true,
                width: "750",

                items :[
                    new Ext.form.ComboBox({
                        fieldLabel: 'Transport',
                        hiddenName: 'name',
                        store: new Ext.data.ArrayStore({
                            fields: ['transport_type', 'name'],
                            data : [['JMS', 'JSM'], ['File', 'File']]
                        }),
                        valueField: 'transport_type',
                        displayField:'name',
                        typeAhead: true,
                        mode: 'local',
                        triggerAction: 'all',
                        emptyText:'Select a transport...',
                        selectOnFocus: true,
                        width:190
                    }),
                    new Ext.form.ComboBox({
                        fieldLabel: 'Format',
                        hiddenName: 'name',
                        store: new Ext.data.ArrayStore({
                            fields: ['format_type', 'name'],
                            data : [['XML', 'XML'], ['CSV', 'CSV']]
                        }),
                        valueField: 'format_type',
                        displayField:'name',
                        typeAhead: true,
                        mode: 'local',
                        triggerAction: 'all',
                        emptyText:'Select a format...',
                        selectOnFocus: true,
                        width:190
                    })
                ]
            },
            {
                xtype: 'fieldset',
                title: 'Outbound',
                collapsible: false,
                autoHeight: true,
                width: "750",

                items :[
                    new Ext.form.ComboBox({
                        fieldLabel: 'Transport',
                        hiddenName: 'name',
                        store: new Ext.data.ArrayStore({
                            fields: ['transport_type', 'name'],
                            data : [['JMS', 'JSM'], ['File', 'File']]
                        }),
                        valueField: 'transport_type',
                        displayField:'name',
                        typeAhead: true,
                        mode: 'local',
                        triggerAction: 'all',
                        emptyText:'Select a transport...',
                        selectOnFocus: true,
                        width:190
                    }),
                    new Ext.form.ComboBox({
                        fieldLabel: 'Format',
                        hiddenName: 'name',
                        store: new Ext.data.ArrayStore({
                            fields: ['format_type', 'name'],
                            data : [['XML', 'XML'], ['CSV', 'CSV']]
                        }),
                        valueField: 'format_type',
                        displayField:'name',
                        typeAhead: true,
                        mode: 'local',
                        triggerAction: 'all',
                        emptyText:'Select a format...',
                        selectOnFocus: true,
                        width:190
                    })
                ]
            },
            new Ext.grid.GridPanel({
                store: store,
                columns: [
                    {header: 'Active', width: 50, sortable: true, dataIndex: 'is_active'},
                    {header: 'Title', width: 150, sortable: true,  dataIndex: 'name'},
                    {header: 'Description', width: 350, sortable: true, dataIndex: 'description'},
                    {header: 'Label', width: 150, sortable: true}
                ],
                stripeRows: true,
                height: 250,
                width: 600,
                title: 'Scenarios',
                // config options for stateful behavior
                stateful: true,
                stateId: 'grid'
            })
        ],
        buttons: [
            {
                text: 'Save',
                handler: function() {
                    if (form.getForm().isValid()) {
                        form.getForm().submit({
                            url: '/system/save_system',
                            waitMsg: 'Saving....',
                            success: function(fp, o) {
                                msg('Success', 'Saved with id "' + o.result.id + '" on the server');
                            }
                        });
                    }
                }
            },
            {
                text: 'Cancel'
            }
        ]
    })
            ;

    form.render(document.body);
});