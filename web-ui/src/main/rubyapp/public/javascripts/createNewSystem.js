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
            ['1','JavaScript'],
            ['qwerqwer',"asdf"]
        ]
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
})
        ;