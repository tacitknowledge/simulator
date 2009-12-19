require 'test_helper'

class ConfigurationControllerTest < ActionController::TestCase

  def test_export
    get :export
    assert_response(200)
  end
end
