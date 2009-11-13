
TK.ScenarioForm = Ext.extend(Ext.FormPanel, {
    /**
         * current scenario id.
         * null means new scenario
         */
    scenarioId: '',
    initComponent: function() {
        TK.ScenarioForm.superclass.initComponent.apply(this, arguments);

        //if system_id is set then load the data into the form. otherwise we are trying to create a new form
        if (this.scenarioId != '' && this.scenarioId != undefined) {
            this.getForm().load({
                url: this.scenarioId + ".json",
                method: 'GET',
                failure: function(form, action) {
                    Ext.Msg.alert("Load failed", action.result.errorMessage);
                }
            })
        }

    },

    constructor:function(config) {
        TK.ScenarioForm.superclass.constructor.call(this, Ext.apply({
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
        }, config))
    }
});