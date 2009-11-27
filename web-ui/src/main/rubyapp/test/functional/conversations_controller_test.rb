require 'test_helper'
require 'json'

class ConversationsControllerTest < ActionController::TestCase
  def test_get_index
    get :index, :system_id => '1'

    # We expect to be redirected to the show template
    assert_response 302
  end

  def test_get_index_json
    get :index, :format => 'json', :system_id => '1'

    assert_response :success

    puts @response.body
    
    json = JSON.parse(@response.body)

    puts json.to_s
    assert_not_nil json['data']
  end

  def test_get_show
    get :show

    assert_response :success
    assert(@response.body.include? '/javascripts/conversationForm.js')
  end

  def test_get_show_json
    get :show, :format => 'json', :id => '1'
  end
end
