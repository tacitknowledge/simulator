require 'java'

class SystemsController < ApplicationController

  def index
    if (params[:format]== 'json')
      @systems = System.all
      render :json => {:success => true, :data => @systems }
    end
  end

  def show
    if (params[:format]== 'json')
      begin
        @system = System.find(params[:id])
      rescue
        # If the system with :id is not found, log the error and redirect to index
        logger.error("System with id #{params[:id]} not found")
        redirect_to :action => 'index'
        return
      end

      render :json => {:success => true, :data => @system }
    end
#    redirect_to "systems/1/show"
  end

  def create
    @system = System.new()

    @system.name=params[:name];
    @system.description=params[:description];
    @system.script_language=params[:script_language];

    if @system.save
      flash[:notice] = "Successfully created new System '#{@system.name}' with id #{@system.id}"
      render :json => {:success => true, :data => @system }
    else
      render :json => {:success => false, :message => "Failed to create System"}
    end
  end

  def update
    @system = System.find(params[:id])
    @system.name=params[:name];
    @system.description=params[:description];
    @system.script_language=params[:script_language];
    if @system.save
      #if @system.update_attributes(ActiveSupport::JSON.decode(params[:data]))
      notice = "Successfully updated system '#{@system.name}' with id #{@system.id}"
      render :json => {:success => true, :message => notice, :data => @system }
    else
      render :json => { :success => false, :message => "Failed to update System"}
    end
  end

  def destroy
    @system = System.find(params[:id])
    system_name = @system.name

    begin
      @system.conversations.each do |conversation|
        SimulatorConnector.instance.delete_conversation(conversation)
      end
    rescue java.lang.Exception => ex
      logger.info(ex)
      render :status => 500, :json => { :message => "We had a problem deleting system"} and return
    end

    if @system.destroy
      notice = "Successfully removed system '#{system_name}' with id #{params[:id]}"
      render :json => {:success => true, :message => notice }
    else
      render :json => {:message => "Failed to destroy System" }
    end
  end

  def new
    @system = System.new
    render :action => 'show'
  end

  def script_languages
    languages = SimulatorConnector.instance.available_languages()
    render :json =>{:success=>true, :data=>languages}
  end
end
