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

    var form = new Ext.FormPanel({
        labelWidth: 150, // label settings here cascade unless overridden
        url:'/scenarios',
        frame: true,
        title: 'Scenario',
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
                allowBlank: false
            },
            {
                fieldLabel: 'Label',
                name: 'label'
            },
            {
                xtype: 'fieldset',
                title: 'When the request looks like this:',
                collapsible: false,
                autoHeight: true,
                width: "750",
                labelWidth: 1,

                items: [
                    {
                        name: 'criteria_script',
                        xtype: 'textarea',
                        allowBlank: false,
                        height: 200,
                        width: "100%"
                    }
                ]
            },
            {
                xtype: 'fieldset',
                title: 'Send a modified response like this:',
                collapsible: false,
                autoHeight: true,
                width: "750",
                labelWidth: 1,

                items: [
                    {
                        name: 'execution_script',
                        xtype: 'textarea',
                        allowBlank: false,
                        height: 200,
                        width: "100%"
                    }
                ]
            }
        ],
        buttons: [
            {
                text: 'Save',
                handler: function() {
                    if (form.getForm().isValid()) {
                        form.getForm().submit({
                            url: '/scenarios',
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