class ScenariosController < ApplicationController

  def index
    if (params[:format]=='json')
      @scenarios = Scenario.find_all_by_conversation_id(params[:conversation_id])
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
    scenario.label = params[:label]
    scenario.criteria_script = params[:criteria_script]
    scenario.execution_script = params[:execution_script]
    scenario.enabled = true
    if scenario.save

      begin
        SimulatorConnector.instance.create_or_update_conversation_scenario(scenario)
      rescue java.lang.Exception => ex
        logger.info(ex)
        render :status => 500, :json => { :message => "We had a problem creating the scenario"} and return
      end

      flash[:notice] = "Successfully created new Scenario '#{scenario.name}' with id #{scenario.id}"
      render :json => { :success => true, :data => scenario }
    else
      render :json => { :success => false, :message => "Failed to create Scenario"}
    end
  end

  def update
    scenario = Scenario.find(params[:id])
    scenario.conversation_id = params[:conversation_id]
    scenario.name = params[:name]
    scenario.label = params[:label]
    scenario.criteria_script = params[:criteria_script]
    scenario.execution_script = params[:execution_script]
    scenario.enabled=params[:enabled].nil? ? scenario.enabled : params[:enabled];

    begin
      SimulatorConnector.instance.create_or_update_conversation_scenario(scenario)
    rescue java.lang.Exception => ex
      logger.info(ex)
      render :status => 500, :json => { :message => "We had a problem updating the scenario"} and return
    end

    if scenario.save
      msg = "Successfully updated Scenario '#{scenario.name}' with id #{scenario.id}"
      render :json => { :success => true, :message => msg, :data => scenario }
    else
      render :json => { :success => false, :message => "Failed to update Scenario"}
    end
  end

  def show
   if(params[:format] == 'json')
     @scenario = Scenario.find(params[:id])
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

    begin
      SimulatorConnector.instance.delete_scenario(@scenario)
    rescue java.lang.Exception => ex
      logger.info(ex)
      render :status => 500, :json => { :message => "We had a problem deleting the scenario"} and return
    end

    if @scenario.destroy
      msg = "Successfully deleted Scenario '#{scenario_name}' with id #{params[:id]}"
      render :json => { :success => true, :message => msg }
    else
      render :json => { :message => "Failed to destroy Scenario" }
    end
  end
  
  def enable
    scenario = Scenario.find(params[:id])
    scenario.enabled =!scenario.enabled
    scenario.save
    render :json => { :success=>true, :data => scenario}
  end

  def clone
     scenario = Scenario.find(params[:id])
     new_scenario = Scenario.new
     new_scenario.name = scenario.name + " copy"
     new_scenario.label = scenario.label
     new_scenario.enabled = true;
     new_scenario.conversation = scenario.conversation;
     new_scenario.criteria_script = scenario.criteria_script;
     new_scenario.execution_script = scenario.execution_script
   if(new_scenario.save)
     render :json => { :success => true, :data => new_scenario}
   else
     render :json => { :message => "Failed to clone Scenario" }
   end
  end

end
