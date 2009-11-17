
TK.ScenarioForm = Ext.extend(Ext.FormPanel, {
    /**
         * current scenario id.
         * null means new scenario
         */
    scenarioId: '',

    onSaveHandler:function(id) {
           var doRedirect = false;
           //todo remove duplicates from here and the system form
           if (this.scenarioId != '' && this.scenarioId != undefined) {
               url ='../'+this.scenarioId
               submitMethod = 'PUT'
           } else {
               doRedirect = true
               url = '../../scenarios'
               submitMethod = 'POST'
           }
        var form = Ext.getCmp('scenario_form').getForm();
        if (form.isValid()) {
               form.submit({
                   url: url,
                   waitMsg: 'Saving....',
                   method: submitMethod,
                   success: function(fp, o) {
                       Ext.MessageBox.alert('Success', o.result.message)
                       if (doRedirect) {
                           window.location = '../' + o.result.data.id + '/'
                       }
                   },
                   failure: function(fp, o) {
                       Ext.MessageBox.alert('Error', o.result.message)
                   }
               });
           }
       },



    initComponent: function() {
        TK.ScenarioForm.superclass.initComponent.apply(this, arguments);

        //if system_id is set then load the data into the form. otherwise we are trying to create a new form
        if (this.scenarioId != '' && this.scenarioId != undefined) {
            this.getForm().load({
                url: '../'+this.scenarioId + ".json",
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
            id:'scenario_form',
            url:'/scenarios',
            frame: true,
            title: 'Scenario',
            bodyStyle: 'padding:5px 5px 0;',

            defaults: {
                width: "98%"
            },

            defaultType: 'textfield',

            items: [
                     new Ext.form.Hidden({
                            hiddenName: 'conversation_id',
                            name: 'conversation_id',
                            value: this.conversationId
                        }),
            {
                fieldLabel: 'Name',
                name: 'name',
                id: 'name',
                allowBlank: false
            },
            {
                fieldLabel: 'Label',
                name: 'label',
                id: 'label'
            },
            {
                xtype: 'fieldset',
                title: 'When the request looks like this:',
                collapsible: false,
                autoHeight: true,
                labelWidth: 1,

                items: [
                {
                    name: 'criteria_script',
                    xtype: 'textarea',
                    id: 'criteria_script',
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
                labelWidth: 1,

                items: [
                {
                    name: 'execution_script',
                    xtype: 'textarea',
                    id: 'execution_script',
                    allowBlank: false,
                    height: 200,
                    width: "100%"
                }
                ]
            }
            ],
            buttons: [
            {
                scope:this,
                text: 'Save',
                id: 'scenario_save',
                handler: this.onSaveHandler
            }
            ]
        }, config))
    }
});