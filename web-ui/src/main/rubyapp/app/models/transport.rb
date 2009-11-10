class Transport < ActiveRecord::Base
  belongs_to :conversation
  has_one :transport_type
  has_many :configurations
end
