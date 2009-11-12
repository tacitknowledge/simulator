class ConversationsController < ApplicationController

  def index
    @conversations = Conversation.find_all_by_system_id(params[:system_id])
    if(params[:format]=='json')
      render :json => { :data => @conversations }
    else
      redirect_to :controller => "systems", :action => "show", :id => params[:system_id]
      return
    end
  end

  def show
   @conversation = Conversation.find(params[:id])
   if(params[:format]=='json')
     render :json => { :success => true, :data => @conversation }
   end
  end

  def create
    @system = Conversation.new(ActiveSupport::JSON.decode(params[:data]))

    if @system.save
      render :json => { :success => true, :message => "Created new Conversation #{@conversation.id}", :data => @conversation }
    else
      render :json => { :message => "Failed to create Conversation"}
    end
  end

  def update
    @conversation = Conversation.find(params[:id])

    if @conversation.update_attributes(ActiveSupport::JSON.decode(params[:data]))
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
