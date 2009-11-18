require 'java'

class SystemsController < ApplicationController

  def index
    @systems = System.all
    if (params[:format]== 'json')
      render :json => { :success => true, :data => @systems }
    end
  end

  def show
    begin
      @system = System.find(params[:id])
    rescue
      # If the system with :id is not found, log the error and redirect to index
      logger.error("System with id #{params[:id]} not found")
      redirect_to :action => 'index'
      return
    end

    if (params[:format]== 'json')
      render :json => { :success => true, :data => @system }
    end
#    redirect_to "systems/1/show"
  end

  def create
    @system = System.new()

    @system.name=params[:name];
    @system.description=params[:description];
    @system.script_language=params[:script_language];

    if @system.save
      render :json => { :success => true, :message => "Created new System #{@system.id}", :data => @system }
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
      render :json => { :success => true, :message => "Updated System #{@system.id}", :data => @system }
    else
      render :json => { success => false, :message => "Failed to update System"}
    end
  end

  def destroy
    @system = System.find(params[:id])

    if @system.destroy
      render :json => { :success => true, :message => "Destroyed System #{@system.id}" }
    else
      render :json => { :message => "Failed to destroy System" }
    end
  end

  def new
    @system = System.new
    render :action => 'show'
  end

  def script_languages
    
  end
end
