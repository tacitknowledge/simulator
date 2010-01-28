class ConversationsController < ApplicationController
  include ConversationHelper

  def index
    if (params[:format]=='json')
      @conversations = Conversation.find_all_by_system_id(params[:system_id])

      begin
        connector = SimulatorConnector.instance
        @conversations.each do|conversation|
          conversation[:active]= connector.is_active(conversation)
        end
      rescue java.lang.Exception => ex
        logger.info(ex)
        render :status => 500, :json => { :message => "We had a problem retrieving the conversations"} and return
      end
      render :json => { :success=>true, :data => @conversations }
    else
      redirect_to :controller => "systems", :action => "show", :id => params[:system_id]
      return
    end
  end

  def show
    # No need to start loading data from DB if it's not a JSON request
    if(params[:format] == 'json')
      @conversation = Conversation.find(params[:id])
      @conversation[:inbound_transport_type_id] = @conversation.in_transport.transport_type_id
      @conversation[:outbound_transport_type_id] = @conversation.out_transport.transport_type_id
      @conversation[:inbound_format_type_id] = @conversation.in_format.format_type_id
      @conversation[:outbound_format_type_id] = @conversation.out_format.format_type_id

      in_tt_name = TransportType.find(@conversation.in_transport.transport_type_id).class_name
      out_tt_name = TransportType.find(@conversation.out_transport.transport_type_id).class_name

      in_frmt_name = FormatType.find(@conversation.in_format.format_type_id).class_name
      out_frmt_name = FormatType.find(@conversation.out_format.format_type_id).class_name

      # Include the configurations in the JSON response
      configurations = {}

      begin
        # Transports
        transport_in = {}
        transport_in[:parameters] = SimulatorConnector.instance.get_transport_parameters(in_tt_name)
        transport_in[:config_values] =
                TransportConfiguration.find_all_by_transport_id(@conversation.inbound_transport_id)
        configurations[:transport_in] = transport_in

        transport_out = {}
        transport_out[:parameters] = SimulatorConnector.instance.get_transport_parameters(out_tt_name)
        transport_out[:config_values] =
                TransportConfiguration.find_all_by_transport_id(@conversation.outbound_transport_id)
        configurations[:transport_out] = transport_out

        # Formats
        format_in = {}
        format_in[:parameters] = SimulatorConnector.instance.get_format_parameters(in_frmt_name)
        format_in[:config_values] =
                FormatConfiguration.find_all_by_format_id(@conversation.inbound_format_id)
        configurations[:format_in] = format_in

        # Formats
        format_out = {}
        format_out[:parameters] = SimulatorConnector.instance.get_format_parameters(out_frmt_name)
        format_out[:config_values] =
                FormatConfiguration.find_all_by_format_id(@conversation.outbound_format_id)
        configurations[:format_out] = format_out


        msg = {}
        msg[:success] = true
        msg[:data] = @conversation
        msg[:configurations] = configurations

        logger.debug(msg.to_json)
      rescue java.lang.Exception => ex
        logger.info(ex)
        render :status => 500, :json => { :message => "We had a problem retrieving the conversation"} and return
      end

      render :json => msg.to_json
    end
  end

  def create
    conversation = populate_conversation(params)

    if conversation.save
      begin
        SimulatorConnector.instance.create_or_update_conversation(conversation)
        flash[:notice] = "Successfully created new Conversation '#{conversation.name}' with id #{conversation.id}"
        render :json => { :success => true, :data => conversation }
      rescue java.lang.Exception => ex
        logger.info(ex)
        render :status => 500, :json => { :message => "We had a problem creating the conversation"} and return
      end
    else
      logger.debug("Unable to save Conversation. List of Errors follow up:")
      logger.debug("     #{conversation.errors.full_messages  }")

      render :json => {:success => false, :message => "Failed to create Conversation"}
    end
  end

  def update
#   remove conversation from the system if it's currently running
    begin
      conversation = Conversation.find params[:id]
      was_active=false
      if(SimulatorConnector.instance.is_active(conversation))
        was_active=true
        SimulatorConnector.instance.deactivate(conversation)
      end
      conversation = update_conversation(params, conversation)
  #   restart conversation using new configuration
      if conversation.save
        jconvers = SimulatorConnector.instance.create_or_update_conversation(conversation)
        conversation.scenarios.each do |scenario|
              system = System.find(scenario.conversation.system_id)
              script_language = system.script_language
              jconvers.addOrUpdateScenario( scenario.id, script_language, scenario.criteria_script, scenario.execution_script)
          end
        if(was_active)
          SimulatorConnector.instance.activate(conversation)
        end
        msg = "Successfully updated conversation '#{conversation.name}' with id #{conversation.id}"
        render :json => { :success => true, :message => msg, :data => conversation }
      else
        render :json => { :message => "Failed to update Conversation"}
      end
    rescue java.lang.Exception => ex
      logger.info(ex)
      render :status => 500, :json => { :message => "We had a problem updating conversation #{conversation.name}"}
    end
  end

  def destroy
    begin
      @conversation = Conversation.find(params[:id])
      conv_name = @conversation.name
      SimulatorConnector.instance.delete_conversation(@conversation)
      if @conversation.destroy
        msg = "Successfully deleted Conversation '#{conv_name}' with id #{params[:id]}"
        render :json => { :success => true, :message => msg}
      else
        render :json => { :message => "Failed to destroy Conversation" }
      end
    rescue java.lang.Exception => ex
      logger.info(ex)
      render :status => 500, :json => { :message => "We had a problem destroying conversation #{@conversation.name}"}
    end
  end

  def new
    @conversation = Conversation.new
    render :action => 'show'
  end


  def transport_types
    type = params[:type]
    if type
      case type
        when 'REST'
          @transport_types = TransportType.find(:all, :conditions => "name = 'REST'")
        when 'SOAP'
          @transport_types = TransportType.find(:all, :conditions => "name = 'SOAP'")
        else
          @transport_types = TransportType.find(:all, :conditions => "name <> 'REST' AND name <> 'SOAP'")
      end
    else
      @transport_types = TransportType.find(:all)
    end

    render :json => { :success=>true, :data => @transport_types}
  end

  def get_inbound_transport_type_by_conversation_id
    @transport_type = '';
    begin
    conversationId = params[:conversationId]
    if conversationId
      conversation = Conversation.find(conversationId)
      if conversation
        transport_type = TransportType.find(conversation.in_transport.transport_type_id, :select =>'name')
        if transport_type
          @transport_type = transport_type.name
        end
      end
    end
    rescue    
    end
    render :json => { :success=>true, :data => @transport_type}
  end

  def format_types
    type = params[:type]
    if type
      case type
        when 'REST'
          @format_types = FormatType.find(:all, :conditions => "name = 'REST'")
        when 'SOAP'
          @format_types = FormatType.find(:all, :conditions => "name = 'SOAP'")
        else
          @format_types = FormatType.find(:all, :conditions => "name <> 'REST' AND name <> 'SOAP'")
      end
    else
      @format_types = FormatType.find(:all)  
    end
    render :json => { :success=>true, :data => @format_types}
  end

  def transport_parameters
    data = [] 
    if !params[:type].nil? && !params[:type].empty?
      begin
        transport_class_name = TransportType.get_transport_class_name_by_name(params[:type])
        data = SimulatorConnector.instance.get_transport_parameters(transport_class_name)
      rescue java.lang.Exception => ex
        logger.info(ex)
        render :status => 500, :json => {:message => "An error ocurred retrieving transport parameters."} and return
      end
    end
    render :json => {:data => data}
  end

  def format_parameters
    data = []
    if !params[:format].nil? && !params[:format].empty?
      begin
        format_class_name = FormatType.get_format_class_name_by_name(params[:format])
        data = SimulatorConnector.instance.get_format_parameters(format_class_name)
      rescue java.lang.Exception => ex
        logger.info(ex)
        render :status => 500, :json => {:message => "An error ocurred retrieving format parameters."} and return
      end
    end
    render :json => {:data => data}
  end

  def enable
    conversation = Conversation.find(params[:id])
    conversation.enabled=!conversation.enabled
#    if new value is disabled then deactivate
    if (!conversation.enabled)
      begin
        SimulatorConnector.instance.deactivate(conversation)
      rescue java.lang.Exception => ex
        logger.info(ex)
        render :status => 500, :json => {:message => "An error ocurred enabling conversation #{conversation.name}."} and return
      end
    end
    conversation.save
    render :json => { :success=>true, :data => conversation}
  end
  
  #if active => deactivates
  #is inactive => deactivates
  def activate
    conversation = Conversation.find(params[:id])
    begin
      if (conversation.enabled)
        is_active = SimulatorConnector.instance.is_active(conversation)
        if(is_active)
          SimulatorConnector.instance.deactivate(conversation)
        else
          SimulatorConnector.instance.activate(conversation)
        end
      end
      is_active = SimulatorConnector.instance.is_active(conversation)
    rescue java.lang.Exception => ex
        logger.info(ex)
        render :status => 500, :json => {:message => "An error ocurred activating conversation #{conversation.name}."} and return
    end
    render :json => { :success=>true, :data => conversation, :is_active => is_active}
  end
end
