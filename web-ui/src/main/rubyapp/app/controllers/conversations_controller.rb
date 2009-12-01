class ConversationsController < ApplicationController
  include ConversationHelper

  def index
    if (params[:format]=='json')
      @conversations = Conversation.find_all_by_system_id(params[:system_id])

      connector = SimulatorConnector.instance
      @conversations.each do|conversation|                                                                    
        conversation[:active]= connector.is_active(conversation)
      end
      render :json => { :success=>true, :data => @conversations }
    else
      redirect_to :controller => "systems", :action => "show", :id => params[:system_id]
      return
    end
  end

  def show
    # No need to start loading data from DB if it's not a JSON request
    if (params[:format]=='json')
      @conversation = Conversation.find(params[:id])
      @conversation[:inbound_transport_type_id] = @conversation.in_transport.transport_type_id
      @conversation[:outbound_transport_type_id] = @conversation.out_transport.transport_type_id
      @conversation[:inbound_format_type_id] = @conversation.in_format.format_type_id
      @conversation[:outbound_format_type_id] = @conversation.out_format.format_type_id

      in_tt_name = TransportType.find(@conversation.in_transport.transport_type_id).name
      out_tt_name = TransportType.find(@conversation.out_transport.transport_type_id).name

      in_frmt_name = FormatType.find(@conversation.in_format.format_type_id).name
      out_frmt_name = FormatType.find(@conversation.out_format.format_type_id).name

      # Include the configurations in the JSON response
      configurations = {}

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
      
      render :json => msg.to_json
    end
  end

  def create
    conversation = populate_conversation(params)

    if conversation.save
      flash[:notice] = "Successfully created new Conversation '#{conversation.name}' with id #{conversation.id}"
      render :json => { :success => true, :data => conversation }
    else
      logger.debug("Unable to save Conversation. List of Errors follow up:")
      logger.debug("     #{conversation.errors.full_messages  }")

      render :json => {:success => false, :message => "Failed to create Conversation"}
    end
  end

  def update
    conversation = update_conversation(params)

    if conversation.save
      msg = "Successfully updated conversation '#{conversation.name}' with id #{conversation.id}"
      render :json => { :success => true, :message => msg, :data => conversation }
    else
      render :json => { :message => "Failed to update Conversation"}
    end
  end

  def destroy
    @conversation = Conversation.find(params[:id])
    conv_name = @conversation.name
    SimulatorConnector.instance.delete_conversation(@conversation)
    if @conversation.destroy
      msg = "Successfully deleted Conversation '#{conv_name}' with id #{params[:id]}"
      render :json => { :success => true, :message => msg}
    else
      render :json => { :message => "Failed to destroy Conversation" }
    end
  end

  def new
    @conversation = Conversation.new
    render :action => 'show'
  end


  def transport_types
    @transport_types = TransportType.find(:all)

    render :json => { :success=>true, :data => @transport_types}
  end

  def format_types
    @format_types = FormatType.find(:all)

    render :json => { :success=>true, :data => @format_types}
  end

  def transport_parameters
    render :json => {:data => SimulatorConnector.instance.get_transport_parameters(params[:type])}
  end

  def format_parameters
    render :json => {:data => SimulatorConnector.instance.get_format_parameters(params[:format])}
  end

  def enable
    conversation = Conversation.find(params[:id])
    conversation.enabled=!conversation.enabled
#    if new value is disabled then deactivate
    if (!conversation.enabled)
      SimulatorConnector.instance.deactivate(conversation)
    end
    conversation.save
    render :json => { :success=>true, :data => conversation}
  end
  
  #if active => deactivates
  #is inactive => deactivates
  def activate
    conversation = Conversation.find(params[:id])
    if (conversation.enabled)
      is_active = SimulatorConnector.instance.is_active(conversation)
      if(is_active)
        SimulatorConnector.instance.deactivate(conversation)
      else
        SimulatorConnector.instance.activate(conversation)
      end
    end
    render :json => { :success=>true, :data => conversation}
  end
end
