require 'test_helper'
require 'json'

class ScenariosControllerTest < ActionController::TestCase
  fixtures :all

  def test_get_index
    get :index, :conversation_id => conversations(:one).id

    assert_redirected_to :controller => 'conversations', :action => 'show'
  end

  def test_get_json_index
    get :index, :format => 'json', :conversation_id => conversations(:one).id

    assert_response :success
    json = JSON.parse(@response.body)

    assert_not_nil json['data']
    assert json['data'].length > 0
  end

  def test_new_screen
    get :new

    assert_response :success
    assert_template 'show'
  end

  def test_should_not_create
    assert_difference('Scenario.count', 0, 'Should not create scenario without attributes') do
      post :create, :conversation_id => conversations(:one).id

      assert_response :success
      json = JSON.parse(@response.body)
      assert(!json['success'])
    end
  end

  def test_should_create
    assert_difference('Scenario.count', 1, 'Should create scenario') do
      post :create,
           :conversation_id => conversations(:one).id,
           :name => 'New scenario',
           :criteria_script => '2 + 2 = 4',
           :execution_script => '4',
           :label => 'label'

      assert_response :success
      json = JSON.parse(@response.body)
      assert(json['success'] == true)
      assert_not_nil(json['data'])
    end
  end

  def test_show_screen
    get :show

    assert_response :success
    assert_template 'show'
  end

  def test_json_show
    get :show, :format => 'json', :id => scenarios(:one).id

    assert_response :success
    json = JSON.parse(@response.body)
    assert_not_nil(json['data'])

    assert(scenarios(:one).name.eql? json['data']['name'])
  end

  def test_update
    orig_name = scenarios(:one).name
    orig_criteria = scenarios(:one).criteria_script
    orig_exec = scenarios(:one).criteria_script
    orig_label = scenarios(:one).label

    post :update,
         :id => scenarios(:one).id,
         :conversation_id => scenarios(:one).conversation_id,
         :name => 'Updated name',
         :criteria_script => 'a==4',
         :execution_script => 'a = 3 x 3',
         :label => 'label 2'

    assert_response :success
    json = JSON.parse(@response.body)
    assert json['success']
    assert_not_nil json['data']

    json_conv = json['data']
    assert !(orig_name.eql? json_conv['name'])
    assert !(orig_criteria.eql? json_conv['criteria_script'])
    assert !(orig_exec.eql? json_conv['execution_script'])
    assert !(orig_label.eql? json_conv['label'])
  end

  def test_delete
    post :create,
         :conversation_id => conversations(:one).id,
         :name => 'New scenario',
         :criteria_script => '2 + 2 = 4',
         :execution_script => '4'

    assert_response :success
    json = JSON.parse(@response.body)
    assert_not_nil json['data']

    delete :destroy, :id => json['data']['id']

    assert_response :success
    json = JSON.parse(@response.body)
    assert json['success']
  end

  def test_enable
    is_enabled = scenarios(:one).enabled

    post :enable, :id => scenarios(:one).id

    assert_response :success
    json = JSON.parse(@response.body)
    assert json['success']

    assert is_enabled != json['data']['enabled']
  end

  def test_clone
    source_scenario = scenarios(:one)
    get :clone, :id=> source_scenario.id
    assert_response(:success)
    json = JSON.parse @response.body

    new_scenario = Scenario.find json['data']['id']
    assert_not_nil(new_scenario)
    assert(new_scenario.id != source_scenario.id)
    assert(new_scenario.enabled?)

    assert_equal(new_scenario.conversation, source_scenario.conversation);
    assert_equal(new_scenario.criteria_script, source_scenario.criteria_script);
    assert_equal(new_scenario.execution_script, source_scenario.execution_script);

    assert_equal(source_scenario.name+' copy', new_scenario.name)

  end

end
