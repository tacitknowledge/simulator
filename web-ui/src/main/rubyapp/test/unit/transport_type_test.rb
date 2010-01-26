require 'test_helper'

class TransportTypeTest < ActiveSupport::TestCase
  
  def test_get_transport_class_name_by_name
    result = TransportType.get_format_class_name_by_name('no_name')
    assert_nil result
    result = FormatType.get_format_class_name_by_name(transport_types(:rest).name)
    assert_not_nil result
    assert_equal transport_types(:rest).class_name, result
  end
end
