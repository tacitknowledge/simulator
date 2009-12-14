# == Schema Information
#
# Table name: transport_types
#
#  id         :integer(10)     not null, primary key
#  name       :string(255)     not null
#  class_name :string(255)     not null
#  created_at :datetime
#  updated_at :datetime
#

class TransportType < ActiveRecord::Base
#  has_many :transports
  
  validates_presence_of :name, :class_name
end
