class ScenariosController < ApplicationController

  def index
    @scenarios = Scenario.find_all_by_conversation_id(params[:conversation_id])
    if(params[:format]=='json')
      render :json => { :data => @scenarios }
    else
      redirect_to :controller => "conversation", :action => "show", :id => params[:conversation_id]
      return
    end
  end

  def create
    @scenario = Scenario.new(ActiveSupport::JSON.decode(params[:data]))

    if @scenario.save
      render :json => { :success => true, :message => "Created new Scenario #{@scenario.id}", :data => @scenario }
    else
      render :json => { :message => "Failed to create Scenario"}
    end
  end

  def update
    @scenario = Scenario.find(params[:id])

    if @scenario.update_attributes(ActiveSupport::JSON.decode(params[:data]))
      render :json => { :success => true, :message => "Updated Scenario #{@scenario.id}", :data => @scenario }
    else
      render :json => { :message => "Failed to update Scenario"}
    end
  end

  def show
   @scenario = Scenario.find(params[:id])
   if(params[:format]=='json')
     render :json => { :success => true, :data => @scenario }
   end
  end

  def destroy
    @scenario = Scenario.find(params[:id])

    if @scenario.destroy
      render :json => { :success => true, :message => "Destroyed Scenario #{@scenario.id}" }
    else
      render :json => { :message => "Failed to destroy Scenario" }
    end
  end
  
end
