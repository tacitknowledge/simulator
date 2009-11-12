class TransportType < ActiveRecord::Base
#  has_many :transports
  
  validates_presence_of :name, :class_name
end
