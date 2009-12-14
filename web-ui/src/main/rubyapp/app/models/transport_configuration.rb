# == Schema Information
#
# Table name: transport_configurations
#
#  id              :integer(10)     not null, primary key
#  transport_id    :integer(10)
#  attribute_name  :string(255)     not null
#  attribute_value :string(1000)    not null
#  created_at      :datetime
#  updated_at      :datetime
#

class TransportConfiguration < ActiveRecord::Base
  belongs_to :transport,
             :foreign_key => :transport_id, 
             :class_name => 'Transport'

  validates_presence_of :attribute_name, :attribute_value
end