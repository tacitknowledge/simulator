TK.AdminForm = Ext.extend(Ext.FormPanel, {
    /**
     * current conversation id.
     * null means new conversation
     */
    conversationId:'',

    initComponent: function()
    {
        TK.AdminForm.superclass.initComponent.apply(this, arguments);
    },

    constructor:function(config)
    {
        var initialConfig = {
            id:'admin-form',
            labelWidth: 120, // label settings here cascade unless overridden
            title: 'Admin',
            bodyStyle: 'padding:5px 5px 0;',
            defaults: {
                width: "98%"
            },
            defaultType: 'textfield',
            items: [
               new Ext.ux.form.FileUploadField({
//                   renderTo: 'fi-basic',
                   width: 400
               }),
                {
                    id: 'export_button',
                    text: 'Export',
                    name: 'export_button',
                    xtype: 'button',
                    handler:function()
                    {
                        TK.export();
                    }
                }
            ]
        };
        TK.AdminForm.superclass.constructor.call(this, Ext.apply(initialConfig, config));
    }
});
