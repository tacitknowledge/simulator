require 'test_helper'

class SystemsControllerTest < ActionController::TestCase
  fixtures :systems

  def test_get_index
    get :index

    assert_response :success
    systems_index = @response.body

    assert systems_index.include? '/javascripts/tk-base.js'
  end

  def test_index_json
    get :index, :format => 'json'
    assert_response :success
    systems_json = @response.body

    assert_not_nil systems_json
    assert(systems_json.include? 'data')
  end

  def test_new_screen
    get :new
    assert_response :success
    assert_template 'show'
  end

  def test_should_not_create_system
    # Attempt to create without required attributes
    assert_difference('System.count', 0, 'Should not create system without attributes') do
      post :create
      assert_response :success
      json = JSON.parse(@response.body)
        assert json['success'].eql? false
    end
  end

  def test_create_system
    system = System.find_by_name 'nnaammee1'
    assert_nil system
    post :create, :name => 'nnaammee1', :description =>'something here', :script_language=>'JavaScript'

    assert_response :success
    system = System.find_by_name 'nnaammee1'
    assert_not_nil system
  end

  def test_show_screen
    get :show
    assert_response :success
    assert_template 'show'
  end

  def test_get_script_languages
    get :script_languages
    assert_response :success
    json = JSON.parse(@response.body)

    #puts json.to_s
    assert_not_nil json['data']
    assert(json['data'].length > 0)
  end

  def test_update_system
    post :create, :name => 'Project 1', :description =>'something here', :script_language=>'JavaScript'
    assert_response :success

    system = System.find_by_name('Project 1')
    assert_not_nil system, 'Expecting newly created system'
    system_id = system.id

    post :update, :id => system_id, :name => 'Project 1 Updated', :description => 'something else', :script_language => 'JRuby'
    assert_response :success

    system = System.find(system_id)
    assert(system.name.eql? 'Project 1 Updated')
    assert(system.description.eql? 'something else')
    assert(system.script_language.eql? 'JRuby')
  end

  def test_destroy
    # Create new deletable system
    post :create, :name => 'Project 1', :description =>'something here', :script_language=>'JavaScript'
    assert_response :success

    system = System.find_by_name('Project 1')
    assert_not_nil system, 'Expecting newly created system'
    system_id = system.id

    assert_difference('System.count', -1) do
      delete :destroy, :id => system_id
    end
  end
end
