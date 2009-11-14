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
    if (params[:format]=='json')
      render :json => { :success => true, :data => @conversation }
    end
  end

  def create
    #   @system = Conversation.new(ActiveSupport::JSON.decode(params[:data]))

    conversation = Conversation.new
    name = params[:name]
    description = params[:description]
    in_tt = params[:inbound_transport_type]
    out_tt = :outbound_transport_type
    in_ft = :inbound_format_type
    out_ft = :outbound_format_type
    sys_id = params[:system_id]

    inbound_transport = Transport.new
    inbound_transport.transport_type_id = (TransportType.find_by_class_name in_tt).object_id

    outbound_transport = Transport.new
    outbound_transport.transport_type_id = (TransportType.find_by_class_name params[ out_tt]).object_id

    inbound_format = Format.new
    inbound_format.format_type_id = (FormatType.find_by_class_name params[ in_ft]).object_id

    outbound_format = Format.new
    outbound_format.format_type_id = (FormatType.find_by_class_name params[ out_ft]).object_id

    conversation.name=name;
    conversation.description=description;
    conversation.system_id=sys_id;

    conversation.in_transport = inbound_transport;
    conversation.in_format = inbound_format;

    conversation.out_transport = outbound_transport;
    conversation.out_format = outbound_format;

    if conversation.save
      render :json => { :success => true, :message => "Created new Conversation #{conversation.id}", :data => conversation }
    else
      render :json => { :message => "Failed to create Conversation"}
    end
  end

  def update

    @conversation = ConversationHelper.build_conversation(params)

    #if @conversation.update_attributes(ActiveSupport::JSON.decode(params[:data]))

    if @conversation.save
      render :json => { :success => true, :message => "Updated Conversation #{@conversation.id}", :data => @conversation }
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

    render :json => { :data => @transport_types}
  end

  def format_types
    @format_types = FormatType.find(:all)

    render :json => { :data => @format_types}
  end
end
