class SystemsController < ApplicationController

  def index
    @systems = System.all
     if(params[:format]== 'json')
       render :json => { :data => @systems }
     end
  end

  def show
    @system = System.find(params[:id])
    if(params[:format]== 'json')
      render :json => { :success => true, :data => @system }
    end
#    redirect_to "systems/1/show"
  end

  def create
    @system = System.new(ActiveSupport::JSON.decode(params[:data]))

    if @system.save
      render :json => { :success => true, :message => "Created new System #{@system.id}", :data => @system }
    else
      render :json => { :message => "Failed to create System"}
    end
  end

  def update
    @system = System.find(params[:id])

    if @system.update_attributes(ActiveSupport::JSON.decode(params[:data]))
      render :json => { :success => true, :message => "Updated System #{@system.id}", :data => @system }
    else
      render :json => { :message => "Failed to update System"}
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

end
