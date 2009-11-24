class FormatConfiguration < ActiveRecord::Base
  belongs_to :format,
             :foreign_key => :format_id,
             :class_name => 'Format'

  validates_presence_of :attribute_name, :attribute_value
end
