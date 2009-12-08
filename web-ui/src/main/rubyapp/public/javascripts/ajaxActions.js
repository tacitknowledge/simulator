TK.deleteEntity = function(entityName, relativeUrl) {
    relativeUrl = relativeUrl == undefined ? entityName : relativeUrl
    var grid = Ext.getCmp(entityName + '_grid');

    var rec = grid.getSelectionModel().getSelected();
    if (rec != undefined) {
        Ext.Ajax.request({
            url: relativeUrl + '/' + rec.data.id ,
            method: 'DELETE',
            success: function(response) {
                grid.store.reload()

                // --- Show the flash message
                var obj = Ext.decode(response.responseText);
                TK.showFlashMessage(obj.message);
            },
            failure: function() {
                //todo handler
            }
        })
    }
}
TK.editEntity = function(entityName, relativeUrl) {
    relativeUrl = relativeUrl == undefined ? entityName : relativeUrl
    var rec = Ext.getCmp(entityName + '_grid').getSelectionModel().getSelected();
    if (rec != undefined) {
        window.location = relativeUrl + '/' + rec.data.id + '/'
    }
}
/**
 *
 * toggles enabled/disabled stat
 * @param entity  entity name.. like scenarios or conversations
 * @param entityId entityId
 *
 */
TK.enableEntity = function(entity, entityId) {

    Ext.Ajax.request({
        url: entity + '/' + entityId + '/enable'  ,
        method: 'GET',
        success: function() {
            //do nothing
        },
        failure: function() {
            //todo handler
        }
    })

}

/**
 *
 * toggles activated/diactivated status
 * @param entity  entity name.. like scenarios or conversations
 * @param entityId entityId
 *
 */
TK.activateEntity = function(entity, entityId) {

    Ext.Ajax.request({
        url: entity + '/' + entityId + '/activate'  ,
        method: 'GET',
        success: function() {
            //do nothing
        },
        failure: function() {
            //todo handler
        }
    })

}

TK.cloneScenario = function (scenarioId) {
    Ext.Ajax.request({
        url: 'scenarios/' + scenarioId + '/clone'  ,
        method: 'GET',
        success: function(result, request) {
            var jsonResponse = Ext.util.JSON.decode(result.responseText)
            window.location = "scenarios/" + jsonResponse.data.id + "/"
        },
        failure: function() {
            //todo handler
        }
    })
};