class ScenariosController < ApplicationController

  def index
    @scenarios = Scenario.find_all_by_conversation_id(params[:conversation_id])
    if(params[:format]=='json')
      render :json => { :data => @scenarios }
    else
      redirect_to :controller => "conversations", :action => "show", :id => params[:conversation_id]
      return
    end
  end

  def create
    scenario = Scenario.new
    scenario.conversation_id = params[:conversation_id]
    scenario.name = params[:name]
    scenario.criteria_script = params[:criteria_script]
    scenario.execution_script = params[:execution_script]
    scenario.enabled=true
    if scenario.save
      flash[:notice] = "Successfully created new Scenario '#{scenario.name}' with id #{scenario.id}"
      render :json => { :success => true, :data => scenario }
    else
      render :json => { :message => "Failed to create Scenario"}
    end
  end

  def update
    scenario = Scenario.find(params[:id])
    scenario.conversation_id = params[:conversation_id]
    scenario.name = params[:name]
    scenario.criteria_script = params[:criteria_script]
    scenario.execution_script = params[:execution_script]
    scenario.enabled=params[:enabled].nil? ? scenario.enabled : params[:enabled];

    SimulatorConnector.instance.create_or_update_conversation_scenario(scenario)

    if scenario.save
      msg = "Successfully updated Scenario '#{scenario.name}' with id #{scenario.id}"
      render :json => { :success => true, :message => msg, :data => scenario }
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

  def new
    @scenario = Scenario.new
    render :action => 'show'
  end

  def destroy
    @scenario = Scenario.find(params[:id])
    scenario_name = @scenario.name

    SimulatorConnector.instance.delete_scenario(@scenario)
    if @scenario.destroy
      msg = "Successfully deleted Scenario '#{scenario_name}' with id #{params[:id]}"
      render :json => { :success => true, :message => msg }
    else
      render :json => { :message => "Failed to destroy Scenario" }
    end
  end
  
  def enable
    scenario = Scenario.find(params[:id])
    scenario.enabled=!scenario.enabled
    scenario.save
    render :json => { :success=>true, :data => scenario}
  end
  
end
