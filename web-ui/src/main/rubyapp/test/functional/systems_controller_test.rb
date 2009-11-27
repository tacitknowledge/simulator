require 'test_helper'

class SystemsControllerTest < ActionController::TestCase
  def test_get_index
    get :index

    assert_response :success
    systems_index = @response.body

    assert systems_index.include? '/javascripts/tk-base.js'
  end

  def test_create_system
    system = System.find_by_name 'nnaammee1'
    assert_nil system
    post :create, :name => 'nnaammee1', :description =>'something here', :script_language=>'JavaScript'

    assert_response 200
    system = System.find_by_name 'nnaammee1'
    assert_not_nil system
  end

  def test_save_system_create_existing
    get :create, :project_name => 'Project 1', :project_description =>'something here', :project_script_language=>'JavaScript'
    assert_response 200
  end

  def test_index_json
    get :index, :format => 'json'
    assert_response 200
    systems_json = @response.body

    assert_not_nil systems_json
    assert(systems_json.include? 'data')
  end
end
