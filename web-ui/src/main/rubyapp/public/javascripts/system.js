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
    Ext.form.Field.prototype.msgTarget = 'side';

    var scriptList = new Ext.data.ArrayStore({
        fields: ['id', 'value'],
        data : [
            ['JavaScript','Java Script'],
            ['somethingelse',"something else"]
        ]
    });

    var systemDataFor = new Ext.FormPanel({
        labelWidth: 120, // label settings here cascade unless overridden
        url:'/system/save',
        frame:true,
        title: 'New Project',
        bodyStyle:'padding:5px 5px 0',
        width: "100%",
        defaults: {
            width: "99%"
        },
        defaultType: 'textfield',

        items: [
            {
                fieldLabel: 'Project Name',
                name: 'project_name',
                allowBlank:false,
                xtype: 'textfield'
            },
            {
                fieldLabel: 'Description',
                name: 'project_description',
                xtype: 'textarea'
            },
            {
                fieldLabel: 'Script language',
                name: 'project_script_language',
                xtype: 'combo',
                store: scriptList,
                displayField:'value',
                mode: 'local'
            }
        ],
        buttons: [
            {
                text: 'Update',
                handler: function() {
                    if (systemDataFor.getForm().isValid()) {
                        systemDataFor.getForm().submit({
                            url: '/system/save',
                            waitMsg: 'Saving....',
                            success: function(fp, o) {
                                Ext.MessageBox.alert('Success', o.result.msg)
                            },
                            failure: function(fp, o) {
                                Ext.MessageBox.alert('Error', o.result.msg)
                            }
                        });
                    }
                }
            }
        ]
    })


    systemDataFor.render(document.getElementById('system'));

    var store = new Ext.data.ArrayStore({
        fields: [
            {
                name: 'name'
            },
            {
                name: 'description'
            }
        ]
    });

    var conversationsGrid = new Ext.grid.GridPanel({
        store: store,
        columns: [
            {
                id:'name',
                header: 'Name',
                width: 160,
                sortable: true,
                dataIndex: 'company'
            },
            {
                header: 'Description',
                width: 75,
                dataIndex: 'description'
            }
        ],
        stripeRows: true,
        autoExpandColumn: 'company',
        height: 350,
        width: 600,
        title: 'Array Grid',
        // config options for stateful behavior
        stateful: true,
        stateId: 'grid'
    });


    var conversationsForm = new Ext.FormPanel({
        labelWidth: 120, // label settings here cascade unless overridden
        url:'/system/save_system',
        frame:true,
        title: 'New Project',
        bodyStyle:'padding:5px 5px 0',
        width: "100%",
        defaults: {
            width: "99%"
        },
        defaultType: 'textfield',

        items: [
            conversationsGrid
        ],
        buttons: [
            {
                text: 'Add',
                handler: function() {
                    Ext.MessageBox.alert('ddd', 'Add')
                }
            },
            {
                text: 'Edit' ,
                handler: function() {
                    Ext.MessageBox.alert('ddd', 'Edit')
                }
            },
            {
                text: 'Remove',
                handler: function() {
                    Ext.MessageBox.alert('ddd', 'Remove')
                }
            }
        ]
    })

    conversationsForm.render(document.getElementById('conversations'))
})