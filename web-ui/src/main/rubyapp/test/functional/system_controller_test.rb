require 'test_helper'

class SystemControllerTest < ActionController::TestCase
  def test_save_system_create_new
    get :save_system, :project_name => 'nnaammee1', :project_description =>'something here', :project_script_language=>'JavaScript'

    assert_response 200
    system = System.find_by_name 'nnaammee1'
    assert_not_nil system
  end

  def test_save_system_create_existing
    get :save_system, :project_name => 'Project 1', :project_description =>'something here', :project_script_language=>'JavaScript'
    assert_response 200
  end
end
