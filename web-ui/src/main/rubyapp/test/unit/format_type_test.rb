require 'test_helper'

class FormatTypeTest < ActiveSupport::TestCase
  
  def test_get_format_class_name_by_name
    result = FormatType.get_format_class_name_by_name('no_name')
    assert_nil result
    result = FormatType.get_format_class_name_by_name(format_types(:rest).name)
    assert_not_nil result
    assert_equal format_types(:rest).class_name, result
  end
end
