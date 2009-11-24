class ConversationsController < ApplicationController
  include ConversationHelper

  def index
    @conversations = Conversation.find_all_by_system_id(params[:system_id])
    if (params[:format]=='json')
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
    @conversation = Conversation.find(params[:id])
    @conversation[:inbound_transport_type_id] = @conversation.in_transport.transport_type_id
    @conversation[:outbound_transport_type_id] = @conversation.out_transport.transport_type_id
    @conversation[:inbound_format_type_id] = @conversation.in_format.format_type_id
    @conversation[:outbound_format_type_id] = @conversation.out_format.format_type_id

    if (params[:format]=='json')
      render :json => { :success => true, :data => @conversation }
    end
  end

  def create
    #   @system = Conversation.new(ActiveSupport::JSON.decode(params[:data]))

    conversation = populate_conversation(params)

    if conversation.save
      render :json => { :success => true, :message => "Created Conversation with id #{conversation.id}", :data => conversation }
    else
      logger.debug("Unable to save Conversation. List of Errors follow up:")
      #logger.debug("     #{conversation.errors.full_messages  }")
      if (conversation.out_transport.errors)
        logger.debug("     #{conversation.out_transport.errors.full_messages}")
      end
      if (conversation.out_format.errors)
        logger.debug("     #{conversation.out_format.errors.full_messages}")
      end

      render :json => { :message => "Failed to create Conversation"}
    end
  end

  def update
    conversation = update_conversation(params)

    if conversation.save
      render :json => { :success => true, :message => "Updated Conversation #{conversation.id}", :data => conversation }
    else
      render :json => { :message => "Failed to update Conversation"}
    end
  end

  def destroy
    @conversation = Conversation.find(params[:id])

    if @conversation.destroy
      render :json => { :success => true, :message => "Destroyed Conversation #{@conversation.id}" }
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
      SimulatorConnector.instance.deactivate()
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
