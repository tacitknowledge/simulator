class SystemController < ApplicationController

  def save_system
    name = params[:project_name]
    description = params[:project_description]
    language = params[:project_script_language]
    project = System.find_by_name name
    if (project.nil?)
      new_system = System.new
      new_system.name=name;
      new_system.description=description;
      new_system.script_language=language;
      new_system.save

      render :json =>{:success => true, :mgs => 'Saved with id ' + new_system.id.to_s + ''}, :status => 200
    else
      render :json =>{:success => false, :msg => 'System with name "'+name+'" already exists'}, :status => 200
    end
  end
end
