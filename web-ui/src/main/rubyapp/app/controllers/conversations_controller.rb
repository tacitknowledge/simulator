class ConversationsController < ApplicationController

  def index
    @conversations = Conversation.find_all_by_system_id(params[:system_id])
    if (params[:format]=='json')
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

    name = params[:name]
    description = params[:description]
    in_tt = params[:inbound_transport_type_id]
    out_tt = params[:outbound_transport_type_id]
    in_ft = params[:inbound_format_type_id]
    out_ft = params[:outbound_format_type_id]
    sys_id = params[:system_id]

    conversation = Conversation.new
    inbound_transport = Transport.new
    inbound_transport.transport_type = TransportType.find in_tt

    outbound_transport = Transport.new
    outbound_transport.transport_type = TransportType.find out_tt

    inbound_format = Format.new
    inbound_format.format_type = FormatType.find in_ft

    outbound_format = Format.new
    outbound_format.format_type = FormatType.find out_ft

    conversation.name=name;
    conversation.description=description;
    conversation.system_id=sys_id;

    conversation.in_transport = inbound_transport;
    conversation.in_format = inbound_format;

    conversation.out_transport = outbound_transport;
    conversation.out_format = outbound_format;

    if conversation.save
      render :json => { :success => true, :message => "Updated Conversation with id #{conversation.id}", :data => conversation }
    else
      render :json => { :message => "Failed to update Conversation"}
    end
  end

  def update
    name = params[:name]
    description = params[:description]
    in_tt = params[:inbound_transport_type_id]
    out_tt = params[:outbound_transport_type_id]
    in_ft = params[:inbound_format_type_id]
    out_ft = params[:outbound_format_type_id]
    sys_id = params[:system_id]

    conversation = Conversation.find params[:id]

    conversation.name=name;
    conversation.description=description;
    conversation.system_id=sys_id;

    conversation.in_transport.transport_type_id = in_tt
    conversation.out_transport.transport_type_id = out_tt
    conversation.in_format.format_type_id = in_ft
    conversation.out_format.format_type_id =out_ft

    if conversation.save
      render :json => { :success => true, :message => "Created new Conversation #{conversation.id}", :data => conversation }
    else
      render :json => { :message => "Failed to create Conversation"}
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

  def enable
    conversation = Conversation.find(params[:id])
    conversation.enabled=!conversation.enabled
    conversation.save
    render :json => { :success=>true, :data => conversation}
  end
end
