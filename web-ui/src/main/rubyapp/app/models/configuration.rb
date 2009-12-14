# == Schema Information
#
# Table name: configurations
#
#  id              :integer(10)     not null, primary key
#  transport_id    :integer(10)
#  format_id       :integer(10)
#  attribute_name  :string(255)     not null
#  attribute_value :string(1000)    not null
#  created_at      :datetime
#  updated_at      :datetime
#

class Configuration < ActiveRecord::Base
# Remove associations to prevent conflicts
#  belongs_to :transport
#  belongs_to :format

  validates_presence_of :attribute_name, :attribute_value
end