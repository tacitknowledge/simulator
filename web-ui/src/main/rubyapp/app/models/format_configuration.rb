# == Schema Information
#
# Table name: format_configurations
#
#  id              :integer(10)     not null, primary key
#  format_id       :integer(10)
#  attribute_name  :string(255)     not null
#  attribute_value :string(1000)    not null
#  created_at      :datetime
#  updated_at      :datetime
#

class FormatConfiguration < ActiveRecord::Base
  belongs_to :format,
             :foreign_key => :format_id,
             :class_name => 'Format'

  validates_presence_of :attribute_name, :attribute_value
end