require 'test_helper'

class ConfigurationControllerTest < ActionController::TestCase

  def test_export
    get :export
    assert_response(200)
    assert_equal 'application/xml', @response.content_type
  end
  
  def test_import 
    get :import, :file=> 'test/functional/export.xml'
    assert_response(200)
    assert_equal "messageSuccessfully imported", @response.body
  end
end
