TK.ConversationForm = Ext.extend(Ext.FormPanel, {
    /**
     * current conversation id.
     * null means new conversation
     */
    conversationId:'',

    onSaveHandler:function(id) {
        var doRedirect = false;
        //todo remove duplicates from here and the system form
        if (this.conversationId != '' && this.conversationId != undefined) {
            url = '../' + this.conversationId;
            submitMethod = 'PUT';
        } else {
            doRedirect = true;
            url = '../../conversations';
            submitMethod = 'POST';
        }
        if (Ext.getCmp('conversation-form').getForm().isValid()) {
            Ext.getCmp('conversation-form').getForm().submit({
                url: url,
                waitMsg: 'Saving....',
                method: submitMethod,
                success: function(fp, o) {
                    //Ext.MessageBox.alert('Success', o.result.message);
                    if (doRedirect) {
                        window.location = '../' + o.result.data.id + '/';
                    } else {
                        TK.showFlashMessage(o.result.message);
                    }
                },
                failure: function(fp, o) {
                    Ext.MessageBox.alert('Error', o.result.message);
                }
            });
        }
    },

    /*
        Generates a field from the parameter ParameterDefinition
     */
    getFieldFromParamData: function(in_out, field_prefix, param_data) {
        var field_name = field_prefix + param_data[0];
        var field_label = param_data[1];
        var field_type = param_data[2];
        var is_required = param_data[3] == 'required';
        var usage = param_data[4];
        var defaultValue = param_data[5];

        // --- Check usage restrictions (inOnly or outOInly)
        if (usage == 'inOnly' && in_out == 'out' ||
            usage == 'outOnly' && in_out == 'in') {
            // --- return null, so the field is not added
            return null;
        }

        var field;
        if (field_type == 'boolean') {
            field = new Ext.form.Checkbox({
                id: field_name,
                name: field_name,
                fieldLabel: field_label,
                inputValue: 'true',
                checked: defaultValue == 'true' ? true : false
            });
        } else {
            // --- Text field is default
            field = new Ext.form.TextField({
                    id: field_name,
                    fieldLabel: field_label,
                    name: field_name,
                    allowBlank: !is_required,
                    xtype: 'textfield',
                    value: (defaultValue != '' ? defaultValue : '')
                });
        }

        return field;
    },

    addParametersToFieldSet: function(in_out, fieldsetName, paramsArray, fieldPrefix) {
        var fieldset = Ext.getCmp(fieldsetName);

        // --- Remove all current parameters fields
        fieldset.removeAll(true);

        if (paramsArray != null && paramsArray.length > 0) {
            for (var i = 0; i < paramsArray.length; i++) {
                var new_param = paramsArray[i];

                // ---
                var newField =
                        Ext.getCmp('conversation-form').getFieldFromParamData(in_out, fieldPrefix, new_param);
                if (newField != null) {
                    fieldset.add(newField);
                }
            }
            fieldset.doLayout();
            fieldset.setVisible(true);
        }
    },

    handleSelectForParameters: function(in_out, url, params, fieldSetName, fieldPrefix) {
        Ext.Ajax.request({
            url : url,
            params : params,
            method: 'GET',
            success: function ( result, request ) {
                var jsonResponse = Ext.util.JSON.decode(result.responseText);

                Ext.getCmp('conversation-form').addParametersToFieldSet(in_out, fieldSetName, jsonResponse.data, fieldPrefix);
            },
            failure: function ( result, request) {
                Ext.MessageBox.alert('Failed', result.responseText);
            }
        });
    },

    populateConfigurationValues: function(prefix, configValues) {
        for (var i = 0; i < configValues.length; i++) {
            var config = configValues[i];
            var fieldName = prefix + config.attribute_name;

            var field = Ext.getCmp(fieldName);
            if (field) {
                field.setValue(config.attribute_value);
            }
        }
    },

    handleConfigurationsLoad: function (responseText) {
        var jsonResponse = Ext.util.JSON.decode(responseText);

        if (jsonResponse.configurations != undefined && jsonResponse.configurations != null) {
            var configurations = jsonResponse.configurations;

            for (var configuration in configurations) {
                var prefix = configuration + '_';
                var fieldSetName = prefix + "fieldset";
                var paramsArray = configurations[configuration].parameters;

                var in_out = "in";
                if(prefix.match(/out/)) {
                    in_out = "out";
                }

                Ext.getCmp('conversation-form').addParametersToFieldSet(in_out, fieldSetName, paramsArray, prefix);
                Ext.getCmp('conversation-form').populateConfigurationValues(prefix, configurations[configuration].config_values);
            }
        }
    },

    onSelectTransportHandler: function(in_out, type) {
        var url = '../../conversations/transport_parameters';
        var params = { 'type' : type };
        var fieldPrefix = 'transport_' + in_out + '_';
        var fieldSetName = fieldPrefix + 'fieldset';

        Ext.getCmp('conversation-form').handleSelectForParameters(in_out, url, params, fieldSetName, fieldPrefix);
    },

    onSelectFormatHandler: function(in_out, format) {
        var url = '../../conversations/format_parameters';
        var params = { 'format' : format };
        var fieldPrefix = 'format_' + in_out + '_';
        var fieldSetName = fieldPrefix + 'fieldset';

        Ext.getCmp('conversation-form').handleSelectForParameters(in_out, url, params, fieldSetName, fieldPrefix);
    },

    initComponent: function() {
        TK.ConversationForm.superclass.initComponent.apply(this, arguments);

        //if system_id is set then load the data into the form. otherwise we are trying to create a new form
        if (this.conversationId != '' && this.conversationId != undefined) {
            this.getForm().load({
                url: '../' + this.conversationId + '.json',
                method: 'GET',
                success: function(form, action) {
                    //Ext.Msg.alert("Attention", action.response.responseText);
                    Ext.getCmp('conversation-form').handleConfigurationsLoad(action.response.responseText); 
                },
                failure: function(form, action) {
                    Ext.Msg.alert("Load failed", action.result.errorMessage);
                }
            });
            var scenariosGrid = Ext.getCmp('scenarios_grid');
            scenariosGrid.show();
            scenariosGrid.getStore().load();
        }
    },

    constructor:function(config) {
        var scenariosStore = new Ext.data.JsonStore({
            root:'data',
            storeId:'scenarios_store',
            idProperty:'id',
            fields: ['enabled','id', 'name', 'criteria_script', 'execution_script'],
            url : 'scenarios.json'
        });
        var formatsStore = new Ext.data.JsonStore({
            root: 'data',
            idProperty: 'id',
            url: '../../../../conversations/format_types',
            fields: ['id', 'name']
        });

        formatsStore.load();

        var transportsStore = new Ext.data.JsonStore({
            root: 'data',
            idProperty: 'id',
            url: '../../../../conversations/transport_types',
            fields: ['id', 'name']
        });
        transportsStore.load();
        var enabledColumn = new Ext.grid.CheckColumn({
                            header: 'Enabled?',
                            dataIndex: 'enabled',
                            sortable: true,
                            width: 55

                        });

        enabledColumn.onChange= function(record) {
            TK.enableEntity('scenarios',record.data.id)
        };

        var initialConfig = {

            id:'conversation-form',
            labelWidth: 120, // label settings here cascade unless overridden
            //            frame: true,
            title: 'Conversation',
            bodyStyle: 'padding:5px 5px 0;',
            defaults: {
                width: "98%"
            },
            defaultType: 'textfield',

            items: [
                {
                    id:'name',
                    fieldLabel: 'Name',
                    name: 'name',
                    allowBlank:false,
                    xtype: 'textfield'
                },
                {
                    id:'description',
                    fieldLabel: 'Description',
                    name: 'description',
                    xtype: 'textarea'
                },
                {
                    xtype: 'fieldset',
                    id:    'inbound_fieldset',
                    title: 'Inbound',
                    collapsible: false,
                    autoHeight: true,

                    items :[

                        new Ext.form.ComboBox({
                            id:'inbound_transport',
                            scope: this,
                            fieldLabel: 'Transport',
                            store: transportsStore,
                            autoDestroy: true,
                            hiddenName: 'inbound_transport_type_id',
                            valueField: 'id',
                            displayField:'name',
                            typeAhead: true,
                            triggerAction: 'all',
                            emptyText:'Select an inbound transport...',
                            selectOnFocus: true,
                            editable: false,
                            listeners: {
                                select: function(combo, newValue, oldValue) {
                                    this.scope.onSelectTransportHandler('in', newValue.data.name);
                                }
                            }
                        }),

                        {
                            id: 'transport_in_fieldset',
                            xtype: 'fieldset',
                            labelWidth: 300,
                            collapsible: false,
                            autoHeight: true,
                            hidden: true,
                            defaults:
                            {
                                width: 250
                            }
                        },

                        new Ext.form.ComboBox({
                            id:'inbound_format',
                            scope: this,
                            fieldLabel: 'Format',
                            hiddenName: 'inbound_format_type_id',
                            store: formatsStore,
                            valueField: 'id',
                            displayField:'name',
                            typeAhead: true,
                            triggerAction: 'all',
                            emptyText: 'Select an inbound format...',
                            selectOnFocus: true,
//                            width: 190,
                            editable: false,
                            listeners: {
                                select: function(combo, newValue, oldValue) {
                                    this.scope.onSelectFormatHandler('in', newValue.data.name);
                                }
                            }
                        }),

                        {
                            id: 'format_in_fieldset',
                            xtype: 'fieldset',
                            labelWidth: 300,
                            collapsible: false,
                            autoHeight: true,
                            hidden: true
                        }
                    ]
                },
                {
                    xtype: 'fieldset',
                    title: 'Outbound',
                    collapsible: false,
                    autoHeight: true,

                    items :[
                        new Ext.form.ComboBox({
                            id:'outbound_transport',
                            scope: this,
                            fieldLabel: 'Transport',
                            hiddenName: 'outbound_transport_type_id',
                            store: transportsStore,
                            valueField: 'id',
                            displayField:'name',
                            typeAhead: true,
                            triggerAction: 'all',
                            emptyText:'Select an outbound transport...',
                            selectOnFocus: true,
                            width:190,
                            editable: false,
                            listeners: {
                                select: function(combo, newValue, oldValue) {
                                    this.scope.onSelectTransportHandler('out', newValue.data.name);
                                }
                            }
                        }),

                        {
                            id: 'transport_out_fieldset',
                            xtype: 'fieldset',
                            labelWidth: 300,
                            collapsible: false,
                            autoHeight: true,
                            hidden: true,
                            defaults:
                            {
                                width: 250
                            }
                        },

                        new Ext.form.ComboBox({
                            id:'outbound_format',
                            scope: this,
                            fieldLabel: 'Format',
                            hiddenName: 'outbound_format_type_id',
                            store:formatsStore,
                            valueField : 'id',
                            displayField : 'name',
                            typeAhead : true,
                            triggerAction : 'all',
                            emptyText : 'Select an outbound format...',
                            selectOnFocus : true,
                            width : 190,
                            editable: false,
                            listeners: {
                                select: function(combo, newValue, oldValue) {
                                    this.scope.onSelectFormatHandler('out', newValue.data.name);
                                }
                            }
                        }),

                        {
                            id: 'format_out_fieldset',
                            xtype: 'fieldset',
                            labelWidth: 300,
                            collapsible: false,
                            autoHeight: true,
                            hidden: true
                        },

                        new Ext.form.Hidden({
                            hiddenName: 'system_id',
                            name: 'system_id',
                            value: this.systemId
                        })
                    ]
                },
                {
                    fieldLabel:"Default response",
                    id:"default_response",
                    xtype:"textarea"
                },
                {
                    store: scenariosStore,
                    id: 'scenarios_grid',
                    xtype:'editorgrid',
                    selModel: new Ext.grid.RowSelectionModel(),
                    columns: [
                        enabledColumn
                        ,
                        {
                            header: 'Title',
                            width: 150,
                            sortable: true,
                            dataIndex: 'name'
                        },
                        {
                            header: 'Criteria Script',
                            width: 150,
                            sortable: true,
                            dataIndex : 'criteria_script'
                        },
                        {
                            header: 'Execution Script',
                            width: 150,
                            sortable: true,
                            dataIndex : 'execution_script'
                        }
                    ],
                    stripeRows: true,
                    height: 250,
                    title: 'Scenarios',
                    // config options for stateful behavior
                    plugins: [enabledColumn],
                    stateful: true,
                    stateId: 'grid',
                    hidden:true,

                    buttons:[
                        {
                            text:'Add',
                            id: 'scenario_add',
                            handler:function() {
                                window.location = 'scenarios/new/'
                            }
                        },
                         {
                            text:'Clone',
                            id: 'scenario_clone',
                            handler:function() {
                                var rec = Ext.getCmp('scenarios_grid').getSelectionModel().getSelected()
                                if (rec != undefined) {
                                    TK.cloneScenario(rec.data.id)
                                }
                            }
                        },
                        {
                            text:'Edit',
                            id: 'scenarion_edit',
                            handler:function() {
                                TK.editEntity('scenarios')
                            }
                        },
                        {
                            text:'Delete',
                            id: 'scenario_delete',
                            handler:function() {
                                TK.deleteEntity('scenarios')
                            }
                        }

                    ]
                }
            ],
            buttons: [
                {
                    id:'conversation_save',
                    scope:this,
                    text: 'Save',
                    handler: this.onSaveHandler
                }
            ]

        };
        TK.ConversationForm.superclass.constructor.call(this, Ext.apply(initialConfig, config));
    }
});
