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

    //    var bd = Ext.getBody();
    //
    //    /*
    //     * ================  Simple form  =======================
    //     */
    //    bd.createChild({tag: 'h2', html: 'Form 1 - Very Simple'});
    var scriptList = new Ext.data.ArrayStore({
        fields: ['id', 'value'],
        data : [
            ['JavaScript','Java Script'],
            ['somethingelse',"something else"]
        ]
    });

    var store = new Ext.data.ArrayStore({
        fields: [
            {
                name: 'company'
            },
            {
                name: 'price',
                type: 'float'
            },
            {
                name: 'change',
                type: 'float'
            },
            {
                name: 'pctChange',
                type: 'float'
            },
            {
                name: 'lastChange',
                type: 'date',
                dateFormat: 'n/j h:ia'
            }
        ]
    });

    var conversationsGrid = new Ext.grid.GridPanel({
        store: store,
        columns: [
            {
                id:'company',
                header: 'Company',
                width: 160,
                sortable: true,
                dataIndex: 'company'
            },
            {
                header: 'Price',
                width: 75,
                sortable: true,
                renderer: 'usMoney',
                dataIndex: 'price'
            },
            {
                header: 'Change',
                width: 75,
                sortable: true,
//                renderer: change,
                dataIndex: 'change'
            },
            {
                header: '% Change',
                width: 75,
                sortable: true,
//                renderer: pctChange,
                dataIndex: 'pctChange'
            },
            {
                header: 'Last Updated',
                width: 85,
                sortable: true,
                renderer: Ext.util.Format.dateRenderer('m/d/Y'),
                dataIndex: 'lastChange'
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

    var form = new Ext.FormPanel({
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
          /*  ,
            conversationsGrid*/
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
                                Ext.MessageBox.alert('Success', o.result.msg)
                            },
                            failure: function(fp, o) {
                                Ext.MessageBox.alert('Error', o.result.msg)
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

    form.render(document.body);
})