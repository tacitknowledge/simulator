require 'test_helper'

class SystemsControllerTest < ActionController::TestCase
  def test_save_system_create_new
    system = System.find_by_name 'nnaammee1'
    assert_nil system
    get :save, :project_name => 'nnaammee1', :project_description =>'something here', :project_script_language=>'JavaScript'

    assert_response 200
    system = System.find_by_name 'nnaammee1'
    assert_not_nil system
  end

  def test_save_system_create_existing
    get :save, :project_name => 'Project 1', :project_description =>'something here', :project_script_language=>'JavaScript'
    assert_response 200
  end

  def test_list
    response = get :list
    assert_response 200
    systems = response.body
    assert_not_nil systems
    assert(systems.length > 0)
  end
end
