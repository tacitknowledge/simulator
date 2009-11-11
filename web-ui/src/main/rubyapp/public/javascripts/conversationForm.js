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
        ['1', 'Scenario1', 'This is a fake scenario', true],
        ['2', 'Scenario 2', 'I repeat, this is a fake scenario', false]
    ];

    store.loadData(myData);


    Ext.QuickTips.init();

    // turn on validation errors beside the field globally
    Ext.form.Field.prototype.msgTarget = 'side';

    var form = new Ext.FormPanel({
        labelWidth: 120, // label settings here cascade unless overridden
        url:'conversation',
        frame: true,
        title: 'Conversation',
        bodyStyle: 'padding:5px 5px 0;',
        width: "800",
        
        defaults: {
            width: "98%"
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
                        hiddenName: 'inbound_transport_type',
                        store: new Ext.data.ArrayStore({
                            fields: ['inbound_transport_type', 'name'],
                            data : [['JmsTransport', 'JMS'], ['FileTransport', 'File']]
                        }),
                        valueField: 'inbound_transport_type',
                        displayField:'name',
                        typeAhead: true,
                        mode: 'local',
                        triggerAction: 'all',
                        emptyText:'Select a transport...',
                        selectOnFocus: true,
                        width:190,
                        editable: false
                    }),
                    new Ext.form.ComboBox({
                        fieldLabel: 'Format',
                        hiddenName: 'inbound_format_type',
                        store: new Ext.data.ArrayStore({
                            fields: ['inbound_format_type', 'name'],
                            data : [['XmlAdapter', 'XML'], ['CsvAdapter', 'CSV']]
                        }),
                        valueField: 'inbound_format_type',
                        displayField:'name',
                        typeAhead: true,
                        mode: 'local',
                        triggerAction: 'all',
                        emptyText: 'Select a format...',
                        selectOnFocus: true,
                        width: 190,
                        editable: false
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
                        hiddenName: 'outbound_transport_type',
                        store: new Ext.data.ArrayStore({
                            fields: ['outbound_transport_type', 'name'],
                            data : [['JmsTransport', 'JMS'], ['FileTransport', 'File']]
                        }),
                        valueField: 'outbound_transport_type',
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
                        hiddenName: 'outbound_format_type',
                        store: new Ext.data.ArrayStore({
                            fields: ['outbound_format_type', 'name'],
                            data : [['XmlAdapter', 'XML'], ['CsvAdapter', 'CSV']]
                        }),
                        valueField: 'outbound_format_type',
                        displayField:'name',
                        typeAhead: true,
                        mode: 'local',
                        triggerAction: 'all',
                        emptyText:'Select a format...',
                        selectOnFocus: true,
                        width:190
                    }),
                    new Ext.form.Hidden({
                        hiddenName: 'system_id',
                        name: 'system_id',
                        value:"1"
                    })
                ]
            },
            new Ext.grid.GridPanel({
                store: store,
                columns: [
                    {header: 'Active', width: 50, sortable: true, dataIndex: 'is_active'},
                    {header: 'Title', width: 150, sortable: true,  dataIndex: 'name'},
                    {header: 'Description', width: 400, sortable: true, dataIndex: 'description'},
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
                            url: 'conversation/save',
                            waitMsg: 'Saving....',
                            success: function(fp, o) {
                                Ext.MessageBox.alert('Success', o.result.msg)
                            }
                        });
                    }
                }
            },
            {
                text: 'Cancel'
            }
        ]
    });

    form.render(document.body);
});